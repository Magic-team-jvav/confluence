package org.confluence.mod.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.BaseLivingPart;

/** 持久化的炮台受击体；外观对应本体模型中的同名骨骼。 */
public final class WalkerWeapon extends BaseLivingPart<MartianWalker> {
    static final int RESOLUTION_GRACE_TICKS = 200;

    private static final EntityDataAccessor<Integer> SLOT = SynchedEntityData.defineId(WalkerWeapon.class, EntityDataSerializers.INT);

    public WalkerWeapon(EntityType<? extends WalkerWeapon> type, Level level) {
        super(type, level);
        xpReward = 0;
    }

    public int slot() {
        return entityData.get(SLOT);
    }

    @Override
    public Component getName() {
        return hasCustomName() ? super.getName() : Component.translatable(slot() == 0
                ? "entity.confluence.martian_walker_weapon.left" : "entity.confluence.martian_walker_weapon.right");
    }

    public boolean bind(MartianWalker walker, int slot) {
        if (walker == null || slot < 0 || slot > 1) {
            return false;
        }
        MartianWalker current = getOwner();
        if (current != null && (current != walker || slot() != slot)) {
            return false;
        }
        int previousSlot = slot();
        entityData.set(SLOT, slot);
        if (bindTo(walker)) {
            return true;
        }
        entityData.set(SLOT, previousSlot);
        return false;
    }

    public boolean belongsTo(MartianWalker walker, int slot) {
        return slot >= 0 && slot <= 1 && slot() == slot && isOwnedBy(walker);
    }

    public void follow(AABB bounds) {
        Vec3 center = bounds.getCenter();
        setPos(center.x, bounds.minY, center.z);
        setBoundingBox(bounds);
    }

    @Override
    protected Class<MartianWalker> getOwnerType() {
        return MartianWalker.class;
    }

    @Override
    protected boolean attachToOwner(MartianWalker owner) {
        return level().isClientSide || owner.acceptWeapon(this, slot());
    }

    @Override
    protected void detachFromOwner(MartianWalker owner, RemovalReason reason) {
        if (!level().isClientSide) {
            owner.releaseWeapon(this, reason);
        }
    }

    @Override
    protected void tickPart(MartianWalker owner) {
        // 跟随在自身原版 tick 之后执行，避免默认尺寸覆盖模型碰撞箱。
        AABB bounds = owner.weaponBounds(slot());
        if (bounds != null) {
            follow(bounds);
        }
    }

    @Override
    protected void onPartHurt(MartianWalker owner, DamageSource source) {
        owner.delayLaserAttack();
    }

    @Override
    protected void onPartDestroyed(MartianWalker owner) {
        owner.destroyWeapon(slot());
        if (level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION, getX(), getBoundingBox().getCenter().y, getZ(),
                    8, 0.3, 0.3, 0.3, 0);
        }
    }

    @Override
    protected boolean discardImmediatelyOnDeath() {
        // 炮台死亡不单独结算掉落物和入侵得分。
        return true;
    }

    @Override
    protected int ownerResolutionGraceTicks() {
        return RESOLUTION_GRACE_TICKS;
    }

    @Override
    protected String ownerSaveTag() {
        return "WalkerOwner";
    }

    @Override
    protected void addPartSaveData(CompoundTag tag) {
        tag.putInt("WeaponSlot", slot());
    }

    @Override
    protected void readPartSaveData(CompoundTag tag) {
        entityData.set(SLOT, Mth.clamp(tag.getInt("WeaponSlot"), 0, 1));
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(SLOT, 0);
    }
}
