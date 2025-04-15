package org.confluence.mod.util;

import com.xiaohunao.heaven_destiny_moment.common.moment.MomentManager;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.entity.TEBossEntities;
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
     * @see LibUtils#getItemStackNbt(ItemStack) 获取或创建
     */
    public static @Nullable CompoundTag getItemStackNbt(ItemStack itemStack) {
        NbtComponent component = itemStack.get(ConfluenceMagicLib.NBT);
        if (component == null) return null;
        return component.nbt();
    }

    public static <T> boolean isFromConfluence(Registry<T> registry, T obj) {
        ResourceLocation key = registry.getKey(obj);
        return key != null && CONFLUENCE_NAMESPACES.contains(key.getNamespace());
    }

    public static boolean isWaterBottle(ItemStack itemStack) {
        return itemStack.is(PotionItems.BOTTLED_WATER) || itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
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

    public static void bossDeath(ServerLevel level, LivingEntity living) {
        EntityType<?> type = living.getType();
        ConfluenceData data = ConfluenceData.get(level);
        data.getKillBoard().defeat(type, data);
        boolean isEaterOfWorlds = type == TEBossEntities.EATER_OF_WORLDS.get();
        if (isEaterOfWorlds || type == TEBossEntities.BRAIN_OF_CTHULHU.get()) {
            if (DateUtils.isWithinDayTime(0, 0, 4, 30, level.getDayTime())) { // 00:00 -> 04:30
                MeteoriteTracker.INSTANCE.spawnAtNextNight = true;
            } else if (!MeteoriteTracker.INSTANCE.spawnAtNextNight) {
                MeteoriteTracker.INSTANCE.spawnAtNextNight = level.random.nextBoolean();
            }
        }
        boolean stickySituation = type == TEBossEntities.KING_SLIME.get() && MomentManager.of(level).hasMoment(TMMoments.SLIME_RAIN);
        ResourceKey<Level> dimension = living.level().dimension();
        level.players().stream().filter(player -> player.level().dimension() == dimension).forEach(player -> {
            TreasureBagItem.createItemEntity(living, player);
            if (isEaterOfWorlds) {
                PlayerUtils.awardAchievement(player, "worm_fodder");
            }
            if (stickySituation) {
                PlayerUtils.awardAchievement(player, "sticky_situation");
            }
        });
    }

    public static void enemyDropMoney(LivingEntity living, ServerLevel level) {
        AttributeInstance attack = living.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        AttributeInstance knockbackResistance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        double healthFactor = living.getMaxHealth() * 0.15;
        double attackFactor = attack == null ? 0.0 : attack.getValue() * 0.25;
        double armorFactor = armor == null ? 0.0 : armor.getValue() * 0.1;
        double knockbackResistanceFactor = knockbackResistance == null ? 10.0 : (1.0 + knockbackResistance.getValue()) * 10.0;
        double difficultyFactor = level.getCurrentDifficultyAt(living.blockPosition()).getEffectiveDifficulty() * 0.5;
        double amount = Math.min(Math.round((healthFactor + attackFactor + armorFactor + knockbackResistanceFactor) * difficultyFactor) * 7.0, 100000);

        if (living.hasEffect(ModEffects.MIDAS)) {
            amount *= Mth.nextDouble(living.getRandom(), 1.1, 1.49);
        }

        dropMoney((int) amount, living.getX(), living.getEyeY() - 0.3, living.getZ(), level);
    }
}
