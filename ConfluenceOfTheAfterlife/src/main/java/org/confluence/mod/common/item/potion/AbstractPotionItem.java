package org.confluence.mod.common.item.potion;

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
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.BaitItems;
import org.confluence.mod.common.init.item.FoodItems;
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

        // 箭术
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.LENS.get()
        }, PotionItems.ARCHERY_POTION.toStack());
        // 建造者
        registerMix(new Item[]{
                MaterialItems.SHINE_ROOT.get(),
                MaterialItems.SHIVERINGTHORNS.get(),
                MaterialItems.MOONSHINE_GRASS.get()
        }, PotionItems.BUILDER_POTION.toStack());
        // 危险感
        registerMix(new Item[]{
                MaterialItems.SHIVERINGTHORNS.get(),
                Items.COBWEB
        }, PotionItems.DANGERSENSE_POTION.toStack());
        // 耐力
        registerMix(new Item[]{
                FoodItems.ARMORED_CAVE_FISH.get(),
                MaterialItems.SHINE_ROOT.get()
        }, PotionItems.ENDURANCE_POTION.toStack());
        // 羽落
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.SHINE_ROOT.get(),
                Items.FEATHER
        }, PotionItems.FEATHERFALL_POTION.toStack());
        // 钓鱼
        registerMix(new Item[]{
                ModBlocks.CRISPY_HONEY_BLOCK.get().asItem(),
                MaterialItems.WATERLEAF.get(),
        }, PotionItems.FISHING_POTION.toStack());
        // 脚蹼
        registerMix(new Item[]{
                MaterialItems.SHIVERINGTHORNS.get(),
                MaterialItems.WATERLEAF.get(),
        }, PotionItems.FLIPPER_POTION.toStack());
        // 鱼腮
        registerMix(new Item[]{
                MaterialItems.WATERLEAF.get(),
                Items.FIRE_CORAL
        }, PotionItems.GILLS_POTION.toStack());
        // 重力
        registerMix(new Item[]{
                MaterialItems.FLAMEFLOWERS.get(),
                MaterialItems.DEATHWEED.get(),
                MaterialItems.SHINE_ROOT.get(),
                Items.FEATHER
        }, PotionItems.GRAVITATION_POTION.toStack());
        // 强效幸运
        registerMix(new Item[]{
                MaterialItems.WATERLEAF.get(),
                BaitItems.LADYBUG.get(),
                MaterialItems.PINK_PEARL.get()
        }, PotionItems.GREATER_LUCK_POTION.toStack());
        // 幸运
        registerMix(new Item[]{
                MaterialItems.WATERLEAF.get(),
                BaitItems.LADYBUG.get(),
                MaterialItems.BLACK_PEARL.get()
        }, PotionItems.LUCK_POTION.toStack());
        // 弱幸运
        registerMix(new Item[]{
                MaterialItems.WATERLEAF.get(),
                BaitItems.LADYBUG.get(),
                MaterialItems.PEARL.get()
        }, PotionItems.GREATER_LUCK_POTION.toStack());
        // 拾心
        registerMix(new Item[]{
                FoodItems.SCARLET_TIGER_FISH.get(),
                MaterialItems.SUNFLOWERS.get()
        }, PotionItems.HEART_REACH_POTION.toStack());
        // 狩猎
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.SHINE_ROOT.get(),
                MaterialItems.SHARK_FIN.get()
        }, PotionItems.HUNTER_POTION.toStack());
        // 狱火
        registerMix(new Item[]{
                FoodItems.FLASHFIN_KOI.get(),
                FoodItems.OBSIDIAN_FISH.get(),
                FoodItems.OBSIDIAN_FISH.get(),
                MaterialItems.FLAMEFLOWERS.get(),
        }, PotionItems.INFERNO_POTION.toStack());
        // 铁皮
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.RAW_LEAD.get()
        }, PotionItems.IRON_SKIN_POTION.toStack());
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                Items.RAW_IRON
        }, PotionItems.IRON_SKIN_POTION.toStack());
        // 生命力
        registerMix(new Item[]{
                FoodItems.COLORFUL_MINERAL_FISH.get(),
                MaterialItems.MOONSHINE_GRASS.get(),
                MaterialItems.SHIVERINGTHORNS.get(),
                MaterialItems.WATERLEAF.get()
        }, PotionItems.LIFEFORCE_POTION.toStack());
        // 爱情
        registerMix(new Item[]{
                FoodItems.PRINCESS_FISH.get(),
                MaterialItems.SHIVERINGTHORNS.get(),
        }, PotionItems.LOVE_POTION.toStack());
        // 魔能
        registerMix(new Item[]{
                MaterialItems.MOONSHINE_GRASS.get(),
                MaterialItems.DEATHWEED.get(),
                MaterialItems.FALLING_STAR.get()
        }, PotionItems.MAGIC_POWER_POTION.toStack());
        // 魔力回复
        registerMix(new Item[]{
                MaterialItems.MOONSHINE_GRASS.get(),
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.FALLING_STAR.get()
        }, PotionItems.MANA_REGENERATION_POTION.toStack());
        // 挖矿
        registerMix(new Item[]{
                MaterialItems.ANTLION_MANDIBLE.get(),
                MaterialItems.SHINE_ROOT.get()
        }, PotionItems.MINING_POTION.toStack());
        // 夜猫子
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.SHINE_ROOT.get()
        }, PotionItems.NIGHT_OWL_POTION.toStack());
        // 黑曜石皮
        registerMix(new Item[]{
                MaterialItems.FLAMEFLOWERS.get(),
                MaterialItems.WATERLEAF.get(),
                Items.OBSIDIAN
        }, PotionItems.OBSIDIAN_SKIN_POTION.toStack());
        // 暴怒
        registerMix(new Item[]{
                FoodItems.BLOODY_PIRANHAS.get(),
                MaterialItems.DEATHWEED.get()
        }, PotionItems.RAGE_POTION.toStack());
        // 再生
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                MaterialItems.LIFE_MUSHROOM.get()
        }, PotionItems.REGENERATION_POTION.toStack());
        // 光环
        registerMix(new Item[]{
                MaterialItems.SUNFLOWERS.get(),
                NatureBlocks.GLOWING_MUSHROOM.get().asItem()
        }, PotionItems.SHINE_POTION.toStack());
        // 洞探
        registerMix(new Item[]{
                MaterialItems.SHINE_ROOT.get(),
                MaterialItems.MOONSHINE_GRASS.get(),
                MaterialItems.RAW_PLATINUM.get()
        }, PotionItems.SPELUNKER_POTION.toStack());
        registerMix(new Item[]{
                MaterialItems.SHINE_ROOT.get(),
                MaterialItems.MOONSHINE_GRASS.get(),
                Items.RAW_GOLD
        }, PotionItems.SPELUNKER_POTION.toStack());
        // 敏捷
        registerMix(new Item[]{
                MaterialItems.SHINE_ROOT.get(),
                Items.CACTUS
        }, PotionItems.SWIFTNESS_POTION.toStack());
        // 荆棘
        registerMix(new Item[]{
                MaterialItems.DEATHWEED.get(),
                Items.CACTUS
        }, PotionItems.THORNS_POTION.toStack());
        // 泰坦
        registerMix(new Item[]{
                Items.BONE,
                MaterialItems.DEATHWEED.get(),
                MaterialItems.SHIVERINGTHORNS.get()
        }, PotionItems.TITAN_POTION.toStack());
        // 水上漂
        registerMix(new Item[]{
                MaterialItems.WATERLEAF.get(),
                MaterialItems.SHARK_FIN.get()
        }, PotionItems.WATER_WALKING_POTION.toStack());
        // 怒气
        registerMix(new Item[]{
                FoodItems.EBONY_KOI.get(),
                MaterialItems.DEATHWEED.get()
        }, PotionItems.WRATH_POTION.toStack());
        // 回忆
        registerMix(new Item[]{
                FoodItems.MIRROR_FISH.get(),
                MaterialItems.SUNFLOWERS.get()
        }, PotionItems.RECALL_POTION.toStack());
        // 虫洞
        registerMix(new Item[]{
                FoodItems.MIRROR_FISH.get(),
                MaterialItems.SHINE_ROOT.get()
        }, PotionItems.INVISIBILITY_POTION.toStack());
        // 传送
        registerMix(new Item[]{
                FoodItems.CHAOS_FISH.get(),
                MaterialItems.FLAMEFLOWERS.get()
        }, PotionItems.RANDOM_TELEPORT_POTION.toStack());
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
            int[] ids = tag.getIntArray("confluence:potion_materials");
            for (Item material : materials) {
                int id = MATERIAL_ID_MAP.getOrDefault(material, -1);
                if (id != -1) ids = append(ids, id);
            }
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
