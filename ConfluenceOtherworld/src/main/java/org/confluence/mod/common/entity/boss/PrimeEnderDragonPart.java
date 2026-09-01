package org.confluence.mod.common.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/// 本源末影龙的服务端碰撞部件。
///
/// 模型仍由主体一次性绘制，七个部件只负责头、躯干、尾部和双翼的精确受击区域。
/// 部件不独立保存生命或战利品，伤害始终转发给主体；非头部使用独立减伤公式，
/// 区块卸载后由主体按固定槽位重建。
public final class PrimeEnderDragonPart extends BaseBossPart<PrimeEnderDragon> {
    private static final EntityDataAccessor<Integer> SLOT = SynchedEntityData.defineId(PrimeEnderDragonPart.class, EntityDataSerializers.INT);

    public PrimeEnderDragonPart(EntityType<?> type, Level level) {
        super(type, level);
    }

    public void setMaster(PrimeEnderDragon master, PrimeEnderDragon.PartSlot slot) {
        entityData.set(SLOT, slot.ordinal());
        refreshDimensions();
        bindTo(master);
        master.bindPart(this);
    }

    public PrimeEnderDragon.PartSlot getSlot() {
        return PrimeEnderDragon.PartSlot.fromOrdinal(entityData.get(SLOT));
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(SLOT, 0);
    }

    @Override
    protected Class<PrimeEnderDragon> getOwnerType() {
        return PrimeEnderDragon.class;
    }

    @Override
    protected void tickPart(PrimeEnderDragon owner) {
        owner.updatePartPosition(this);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        PrimeEnderDragon owner = getOwner();
        if (owner == null || !owner.isAlive()) {
            return false;
        }
        float forwarded = getSlot() == PrimeEnderDragon.PartSlot.HEAD
                ? amount
                : amount * 0.25F + Math.min(amount, 1.0F);
        if (forwarded < 0.01F) return false;
        if (source.getEntity() instanceof Player || source.is(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS))
            owner.hurt(source, forwarded);
        return true;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        PrimeEnderDragon.PartSlot slot = getSlot();
        PrimeEnderDragon owner = getOwner();
        return EntityDimensions.scalable(slot.width(), slot.height())
                .scale(owner == null ? 1.0F : owner.getScale());
    }

    @Override
    public void remove(RemovalReason reason) {
        PrimeEnderDragon owner = getOwner();
        if (owner != null) {
            owner.onPartRemoved(this);
        }
        super.remove(reason);
    }
}
