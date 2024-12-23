package org.confluence.mod.common.item.potion;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractPotionItem extends Item {
    private static final Object2IntMap<Item> MATERIAL_ID_MAP = new Object2IntOpenHashMap<>();
    private static final Object2ObjectMap<int[], ItemStack> MATERIAL_TO_RESULT = new Object2ObjectOpenHashMap<>();

    public AbstractPotionItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity entity) {
        return 20;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (canUse(level, player, hand)) return ItemUtils.startUsingInstantly(level, player, hand);
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity living) {
        apply(itemStack, level, living);
        if (living instanceof Player player && !player.getAbilities().instabuild) {
            itemStack.shrink(1); // 创造模式不消耗
        }
        if (itemStack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (living instanceof Player player && !player.getAbilities().instabuild) {
                ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(itemstack)) {
                    player.drop(itemstack, false);
                }
            }
            return itemStack;
        }
    }

    protected boolean canUse(Level level, Player player, InteractionHand hand) {
        return true;
    }

    protected abstract void apply(ItemStack itemStack, Level level, LivingEntity living);

    public static void initialize() {
        registerMaterial(MaterialItems.WATERLEAF.get());
        registerMaterial(MaterialItems.FLAMEFLOWERS.get());
        registerMaterial(MaterialItems.MOONSHINE_GRASS.get());
        registerMaterial(MaterialItems.SHINE_ROOT.get());
        registerMaterial(MaterialItems.SHIVERINGTHORNS.get());
        registerMaterial(MaterialItems.SUNFLOWERS.get());
        registerMaterial(MaterialItems.DEATHWEED.get());
        registerMaterial(MaterialItems.LENS.get());

        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.LENS.get()
        }, PotionItems.ARCHERY_POTION.toStack());
    }

    private static void registerMaterial(Item material) {
        MATERIAL_ID_MAP.put(material, MATERIAL_ID_MAP.size());
    }

    private static void registerMix(Item[] materials, ItemStack result) {
        int[] ids = Arrays.stream(materials).mapToInt(material -> MATERIAL_ID_MAP.getOrDefault(material, -1)).filter(id -> id != -1).toArray();
        Arrays.sort(ids);
        MATERIAL_TO_RESULT.put(ids, result);
    }

    public static void registerRecipes(Consumer<IBrewingRecipe> consumer) {
        consumer.accept(new IBrewingRecipe() {
            @Override
            public boolean isInput(@NotNull ItemStack input) {
                return input.is(PotionItems.CHAOS_POTION) || (input.is(Items.POTION) && input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER));
            }

            @Override
            public boolean isIngredient(@NotNull ItemStack ingredient) {
                return MATERIAL_ID_MAP.containsKey(ingredient.getItem());
            }

            @Override
            public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
                if (!isIngredient(ingredient) || !isInput(input)) return ItemStack.EMPTY;
                int addition = MATERIAL_ID_MAP.getOrDefault(ingredient.getItem(), -1);
                if (addition == -1) return ItemStack.EMPTY;
                int[] materials = append(getMaterials(input), addition);
                int length = materials.length;
                boolean isorted = true;
                outer:
                for (Map.Entry<int[], ItemStack> entry : MATERIAL_TO_RESULT.entrySet()) {
                    int[] ids = entry.getKey();
                    if (ids.length != length) continue;
                    if (isorted) {
                        Arrays.sort(materials);
                        isorted = false;
                    }
                    for (int i = 0; i < ids.length; i++) {
                        if (ids[i] != materials[i]) continue outer;
                    }
                    return entry.getValue().copy();
                }
                ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                setMaterials(stack, materials);
                return stack;
            }
        });
    }

    private static int[] append(int[] a, int addition) {
        int length = a.length;
        int[] materials = IntArrays.forceCapacity(a, length + 1, length);
        materials[length] = addition;
        return materials;
    }

    public static void addMaterials(ItemStack potion, Item... materials) {
        TCUtils.updateItemStackNbt(potion, tag -> {
            IntArrayList list = new IntArrayList(tag.getIntArray("confluence:potion_materials"));
            for (Item material : materials) {
                int id = MATERIAL_ID_MAP.getOrDefault(material, -1);
                if (id != -1) list.add(id);
            }
            int[] ids = list.elements();
            Arrays.sort(ids);
            tag.putIntArray("confluence:potion_materials", ids);
        });
    }

    public static void setMaterials(ItemStack potion, Item[] materials) {
        TCUtils.updateItemStackNbt(potion, tag -> {
            int[] ids = Arrays.stream(materials).mapToInt(material -> MATERIAL_ID_MAP.getOrDefault(material, -1)).filter(id -> id != -1).toArray();
            Arrays.sort(ids);
            tag.putIntArray("confluence:potion_materials", ids);
        });
    }

    public static void setMaterials(ItemStack potion, int[] materials) {
        Arrays.sort(materials);
        TCUtils.updateItemStackNbt(potion, tag -> tag.putIntArray("confluence:potion_materials", materials));
    }

    public static int[] getMaterials(ItemStack potion) {
        CompoundTag tag = ModUtils.getItemStackNbt(potion);
        if (tag == null) return new int[0];
        return tag.getIntArray("confluence:potion_materials");
    }
}
