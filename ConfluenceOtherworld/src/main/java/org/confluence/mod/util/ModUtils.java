package org.confluence.mod.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.component.NbtComponent;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class ModUtils {
    public static final Direction[] HORIZONTAL = new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};
    public static final Direction[] DIRECTIONS = Direction.values();
    public static final String NO_DROPS_TAG = "confluence:no_drops";
    public static final Set<String> CONFLUENCE_NAMESPACES = Set.of(Confluence.MODID, TerraCurio.MODID, TerraEntity.MODID, TerraGuns.MODID);
    public static final int MAX_STACK_SIZE = 9999;
    public static final Codec<BlockPos> BLOCK_POS_CODEC = Codec.STRING.xmap(str -> {
        String[] split = str.split(", ");
        int[] pos = new int[3];
        for (int i = 0; i < 3; i++) {
            if (i < split.length) {
                pos[i] = Integer.parseInt(split[i]);
            }
        }
        return new BlockPos(pos[0], pos[1], pos[2]);
    }, pos -> pos.getX() + ", " + pos.getY() + ", " + pos.getZ());

    public static void createItemEntity(ItemStack itemStack, double x, double y, double z, Level level, int pickUpDelay) {
        if (itemStack.isEmpty()) return;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        itemEntity.setPickUpDelay(pickUpDelay);
        level.addFreshEntity(itemEntity);
    }

    public static void createItemEntity(ItemStack itemStack, Vec3 pos, Level level, int pickUpDelay) {
        createItemEntity(itemStack, pos.x, pos.y, pos.z, level, pickUpDelay);
    }

    public static void createItemEntity(Item item, int count, double x, double y, double z, Level level, int pickUpDelay) {
        if (count <= 0 || item == Items.AIR) return;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, new ItemStack(item, count));
        itemEntity.setPickUpDelay(pickUpDelay);
        level.addFreshEntity(itemEntity);
    }

    public static void createItemEntity(Item item, int count, Vec3 pos, Level level, int pickUpDelay) {
        createItemEntity(item, count, pos.x, pos.y, pos.z, level, pickUpDelay);
    }

    public static void dropMoney(int amount, double x, double y, double z, Level level) {
        int copper_count = amount % UPGRADES_COUNT;
        int i = ((amount - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        createItemEntity(ModItems.COPPER_COIN.get(), copper_count, x, y, z, level, 0);
        createItemEntity(ModItems.SILVER_COIN.get(), silver_count, x, y, z, level, 0);
        createItemEntity(ModItems.GOLDEN_COIN.get(), golden_count, x, y, z, level, 0);
        createItemEntity(ModItems.PLATINUM_COIN.get(), k, x, y, z, level, 0);
    }

    public static void dropMoney(long amount, double x, double y, double z, Level level) {
        while (amount > 0x3F3F3F3F) {
            dropMoney(0x3F3F3F3F, x, y, z, level);
            amount -= 0x3F3F3F3F;
        }
        dropMoney((int) amount, x, y, z, level);
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(BlockEntityType<A> a, BlockEntityType<E> b, BlockEntityTicker<? super E> ticker) {
        return a == b ? (BlockEntityTicker<A>) ticker : null;
    }

    /**
     * 为专家?在处理if...else if时应先使用:
     *
     * @see ModUtils#isMaster(Level, BlockPos)
     */
    public static boolean isAtLeastExpert(Level level, BlockPos pos) {
        return level.getCurrentDifficultyAt(pos).getEffectiveDifficulty() >= 1;
    }

    /**
     * 为大师?在处理if...else if时应先使用此方法
     */
    public static boolean isMaster(Level level, BlockPos pos) {
        return level.getCurrentDifficultyAt(pos).getEffectiveDifficulty() >= 2;
    }

    /**
     * 根据游戏难度选择值
     *
     * @param classic 经典难度的值
     * @param expert  专家难度的值
     * @param master  大师难度的值
     * @return 选择到的值
     */
    public static <T> T switchByDifficulty(Level level, BlockPos blockPos, T classic, T expert, T master) {
        float difficulty = level.getCurrentDifficultyAt(blockPos).getEffectiveDifficulty();
        if (difficulty >= 2) return master;
        if (difficulty >= 1) return expert;
        return classic;
    }

    /**
     * 根据游戏难度选择值
     *
     * @param classic   经典难度的值
     * @param expert    专家难度的值
     * @param master    大师难度的值
     * @param legendary 传奇难度的值
     * @return 选择到的值
     */
    public static <T> T switchByDifficulty(Level level, BlockPos blockPos, T classic, T expert, T master, T legendary) {
        float difficulty = level.getCurrentDifficultyAt(blockPos).getEffectiveDifficulty();
        if (difficulty >= 3) return legendary;
        if (difficulty >= 2) return master;
        if (difficulty >= 1) return expert;
        return classic;
    }

    /**
     * 仅获取
     *
     * @see TCUtils#getItemStackNbt(ItemStack) 获取或创建
     */
    public static @Nullable CompoundTag getItemStackNbt(ItemStack itemStack) {
        NbtComponent component = itemStack.get(TCDataComponentTypes.NBT);
        if (component == null) return null;
        return component.nbt();
    }

    @Nullable
    public static Immunity getImmunityCause(DamageSource damageSource) {
        Entity directEntity = damageSource.getDirectEntity();
        ItemStack weaponItemStack = damageSource.getWeaponItem();
        if (weaponItemStack != null) {
            Item weaponItem = weaponItemStack.getItem();
            boolean fromConfluence = isFromConfluence(BuiltInRegistries.ITEM, weaponItem);
            if (fromConfluence && (weaponItem instanceof SwordItem) && directEntity instanceof Projectile projectile) { // 汇流剑气
                return (Immunity) projectile;
            } else if (weaponItem instanceof Immunity im) { // 非汇流但是实现了Immunity
                return switch (im.confluence$getImmunityType()) {
                    case STATIC -> im;
                    case LOCAL -> (Immunity) (Object) weaponItemStack;
                };
            } else if (fromConfluence) { // 其他所有汇流武器
                return (Immunity) (Object) weaponItemStack;
            }
        }
        if (directEntity instanceof Projectile proj && directEntity instanceof Immunity im && isFromConfluence(BuiltInRegistries.ENTITY_TYPE, directEntity.getType())) { // 汇流射弹
            return switch (im.confluence$getImmunityType()) {
                case STATIC -> (Immunity) proj.getType();
                case LOCAL -> im;
            };
        }
        // TODO: 打表
        return null;
    }

    public static <T> boolean isFromConfluence(Registry<T> registry, T obj) {
        ResourceLocation key = registry.getKey(obj);
        return key != null && CONFLUENCE_NAMESPACES.contains(key.getNamespace());
    }

    public static int getSlotIndex(@Nullable EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            case null, default -> -1;
        };
    }

    public static int getMaxStackSize(int original) {
        return Math.max(original, MAX_STACK_SIZE);
    }

    public static boolean anyHandHasItem(LivingEntity living, Predicate<ItemStack> predicate) {
        return predicate.test(living.getMainHandItem()) || predicate.test(living.getOffhandItem());
    }

    public static boolean isWaterBottle(ItemStack itemStack) {
        return itemStack.is(PotionItems.BOTTLED_WATER) || itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
    }

    public static void setItemAndDropChance(Mob mob, DifficultyInstance difficulty, EquipmentSlot slot, Item item, float chance) {
        ItemStack itemStack = item.getDefaultInstance();
        float enchantChance = (slot.getType() == EquipmentSlot.Type.HAND ? 0.25F : 0.5F) * difficulty.getSpecialMultiplier();
        if (mob.getRandom().nextFloat() < enchantChance) {
            EnchantmentHelper.enchantItemFromProvider(itemStack, mob.registryAccess(), VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT, difficulty, mob.getRandom());
        }
        mob.setItemSlot(slot, itemStack);
        mob.setDropChance(slot, chance);
    }
}
