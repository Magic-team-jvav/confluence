package org.confluence.mod.common.item.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.storage.StorageCompanionEntity;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.Objects;
import java.util.function.Supplier;

/// 随身储物入口实体的召唤物品。
///
/// 注册时直接传入对应实体类型。普通使用负责召唤或重新放置入口，潜行使用只取消同类入口；
/// 两种操作不会因为准星是否碰到实体而改变含义。重复召唤只替换世界中的入口实体，
/// 玩家库存始终保存在自己的存钱罐数据中。
public final class StorageCompanionItem<T extends StorageCompanionEntity> extends Item {
    private final Supplier<EntityType<T>> entityType;

    public StorageCompanionItem(Properties properties, Supplier<EntityType<T>> entityType) {
        super(properties.stacksTo(1));
        this.entityType = Objects.requireNonNull(entityType, "Storage companion entity type must not be null");
    }

    public EntityType<T> entityType() {
        return entityType.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        EntityType<T> companionType = Objects.requireNonNull(entityType(), "Storage companion entity type must not be null");
        StorageCompanionEntity existing = null;
        for (Entity entity : serverPlayer.serverLevel().getAllEntities()) {
            // 实体管理器切换追踪状态的同一刻，遍历结果可能短暂暴露空槽。
            // 召唤逻辑只关心仍然存在的随身储物实体，跳过空槽即可。
            if (entity != null && entity.getType() == companionType && entity instanceof StorageCompanionEntity companion && companion.belongsTo(player)) {
                existing = companion;
                break;
            }
        }
        // 取消必须是显式操作。普通使用只负责召唤或重新放置入口，即使准星碰到随身储物实体也不会误删。
        // 钱币槽和眼骨在潜行使用时也只会取消各自对应的实体。
        if (player.isShiftKeyDown()) {
            if (existing != null) {
                existing.discard();
            }
            completeUse(player, hand);
            return InteractionResultHolder.success(stack);
        }

        T companion = companionType.create(level);
        if (companion == null) {
            throw new IllegalStateException("Storage companion entity type returned null");
        }
        companion.initializeOwner(serverPlayer);
        Vec3 spawn = findSpawnPosition(level, player, companion);
        companion.moveTo(spawn.x, spawn.y, spawn.z, player.getYRot(), 0.0F);
        if (!level.addFreshEntity(companion)) {
            return InteractionResultHolder.fail(stack);
        }
        if (existing != null) {
            // 新入口确认加入世界后再移除旧入口，避免生成失败时丢失玩家当前使用的储物入口。
            existing.discard();
        }
        player.playSound(ModSoundEvents.SUMMON_MONEY_TROUGH.get(), 1.0F, 1.0F);
        completeUse(player, hand);
        return InteractionResultHolder.success(stack);
    }

    private void completeUse(Player player, InteractionHand hand) {
        player.awardStat(Stats.ITEM_USED.get(this));
        player.swing(hand, true);
    }

    private static Vec3 findSpawnPosition(Level level, Player player, StorageCompanionEntity companion) {
        HitResult hit = player.pick(player.blockInteractionRange(), 1.0F, false);
        if (hit instanceof BlockHitResult blockHit && blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos clickedSide = blockHit.getBlockPos().relative(blockHit.getDirection());
            Vec3 sideCandidate = Vec3.atBottomCenterOf(clickedSide);
            if (canPlaceAt(level, companion, player, sideCandidate)) {
                return sideCandidate;
            }
            Vec3 topCandidate = Vec3.atBottomCenterOf(blockHit.getBlockPos().above());
            if (canPlaceAt(level, companion, player, topCandidate)) {
                return topCandidate;
            }
        }

        Vec3 look = player.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0.0, look.z);
        if (horizontalLook.lengthSqr() < 1.0E-5) {
            horizontalLook = Vec3.directionFromRotation(0.0F, player.getYRot());
        }
        Vec3 nearPlayer = player.position().add(horizontalLook.normalize().scale(1.6));
        Vec3 nearCandidate = new Vec3(nearPlayer.x, player.getY(), nearPlayer.z);
        if (canPlaceAt(level, companion, player, nearCandidate)) {
            return nearCandidate;
        }
        return player.position().add(0.0, 0.2, 0.0);
    }

    private static boolean canPlaceAt(Level level, StorageCompanionEntity companion, Player player, Vec3 position) {
        companion.moveTo(position.x, position.y, position.z, player.getYRot(), 0.0F);
        return level.noCollision(companion);
    }
}
