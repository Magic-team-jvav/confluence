package org.confluence.mod.util;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
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

import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class ModUtils {
    public static final Set<String> CONFLUENCE_NAMESPACES = Set.of(Confluence.MODID, TerraCurio.MODID, TerraEntity.MODID, TerraGuns.MODID);

    public static void dropMoney(int amount, double x, double y, double z, Level level) {
        int copper_count = amount % UPGRADES_COUNT;
        int i = ((amount - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        LibUtils.createItemEntity(ModItems.COPPER_COIN.get(), copper_count, x, y, z, level, 0);
        LibUtils.createItemEntity(ModItems.SILVER_COIN.get(), silver_count, x, y, z, level, 0);
        LibUtils.createItemEntity(ModItems.GOLDEN_COIN.get(), golden_count, x, y, z, level, 0);
        LibUtils.createItemEntity(ModItems.PLATINUM_COIN.get(), k, x, y, z, level, 0);
    }

    public static void dropMoney(long amount, double x, double y, double z, Level level) {
        while (amount > 0x3F3F3F3F) {
            dropMoney(0x3F3F3F3F, x, y, z, level);
            amount -= 0x3F3F3F3F;
        }
        dropMoney((int) amount, x, y, z, level);
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

    public static void summonBoss(Level level, Vec3 center, Mob boss) {
        boss.setPos(center.x + level.random.nextInt(-50, 51), center.y, center.z + level.random.nextInt(-50, 51));
        level.addFreshEntity(boss);
//        Player nearestPlayer = level.getNearestPlayer(boss, 200);
//        if (nearestPlayer != null) boss.setTarget(nearestPlayer);
    }

    public static @Nullable BlockState getLeadAnvilDamage(BlockState state, DirectionProperty FACING) {
        Block block = state.getBlock();
        if (block == FunctionalBlocks.LEAD_ANVIL.get()) {
            return FunctionalBlocks.CHIPPED_LEAD_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING));
        } else if (block == FunctionalBlocks.CHIPPED_LEAD_ANVIL.get()) {
            return FunctionalBlocks.DAMAGED_LEAD_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING));
        } else if (block == FunctionalBlocks.DAMAGED_LEAD_ANVIL.get()) {
            return null;
        }
        return state;
    }
}
