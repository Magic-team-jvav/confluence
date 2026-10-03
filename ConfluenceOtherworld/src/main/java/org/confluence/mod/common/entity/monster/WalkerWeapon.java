package org.confluence.mod.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.PartHitTarget;
import java.util.UUID;

/** 持久化的受击体；外观对应本体模型中的同名骨骼。 */
public final class WalkerWeapon extends Monster implements PartHitTarget {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(WalkerWeapon.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SLOT = SynchedEntityData.defineId(WalkerWeapon.class, EntityDataSerializers.INT);
    private UUID ownerUUID;
    private MartianWalker owner;
    private int slot;
    private int missingTicks;
    public WalkerWeapon(EntityType<? extends WalkerWeapon> type, Level level) {
        super(type, level);
        setNoAi(true); setNoGravity(true); noPhysics = true;
        xpReward = 0;
    }
    public int slot() { return level().isClientSide ? entityData.get(SLOT) : slot; }
    @Override public Component getName() {
        return hasCustomName() ? super.getName() : Component.translatable(slot() == 0
                ? "entity.confluence.martian_walker_weapon.left" : "entity.confluence.martian_walker_weapon.right");
    }
    @Override protected void defineSynchedData() {
        super.defineSynchedData(); entityData.define(OWNER_ID, -1); entityData.define(SLOT, 0);
    }
    public void bind(MartianWalker walker, int slot) {
        if (walker == null || slot < 0 || slot > 1) return;
        this.owner = walker; ownerUUID = walker.getUUID(); this.slot = slot;
        entityData.set(OWNER_ID, walker.getId()); entityData.set(SLOT, slot);
    }
    public boolean belongsTo(MartianWalker walker, int slot) {
        return walker != null && slot >= 0 && slot <= 1 && this.slot() == slot
                && (walker.getUUID().equals(ownerUUID) || level().isClientSide && owner == walker);
    }
    public void follow(AABB bounds) { setPos(bounds.getCenter().x, bounds.minY, bounds.getCenter().z); setBoundingBox(bounds); }
    @Override public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (level().getEntity(entityData.get(OWNER_ID)) instanceof MartianWalker walker) {
                owner = walker;
                slot = entityData.get(SLOT);
                AABB bounds = walker.weaponBounds(entityData.get(SLOT));
                if (bounds != null) follow(bounds);
            }
            return;
        }
        if (!(level() instanceof ServerLevel server)) return;
        if (owner == null || owner.isRemoved()) {
            owner = ownerUUID != null && server.getEntity(ownerUUID) instanceof MartianWalker walker ? walker : null;
        }
        if (owner == null) { if (++missingTicks > 200) discard(); return; }
        missingTicks = 0;
        entityData.set(OWNER_ID, owner.getId()); entityData.set(SLOT, slot);
        if (!owner.acceptWeapon(this, slot)) discard();
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (owner == null || !owner.isAlive() || !owner.acceptWeapon(this, slot)) return false;
        boolean result = super.hurt(source, amount);
        if (result) owner.delayLaserAttack();
        return result;
    }
    @Override public void die(DamageSource source) {
        if (level() instanceof ServerLevel server) {
            if (owner != null) owner.destroyWeapon(slot);
            server.sendParticles(ParticleTypes.EXPLOSION, getX(), getBoundingBox().getCenter().y, getZ(), 8, 0.3, 0.3, 0.3, 0);
        }
        // 子实体的死亡被刻意排除在掉落物与入侵得分之外。
        discard();
    }
    @Override public boolean isPushable() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public Entity damageRecipient() { return this; }
    @Override public Entity encounterOwner() { return owner == null ? this : owner; }
    @Override public Entity dedupeIdentity() { return this; }
    @Override public boolean acceptsDirectHit() { return owner != null && owner.isAlive(); }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) tag.putUUID("WalkerOwner", ownerUUID);
        tag.putInt("WeaponSlot", slot);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerUUID = tag.hasUUID("WalkerOwner") ? tag.getUUID("WalkerOwner") : null;
        int saved = tag.contains("WeaponSlot") ? tag.getInt("WeaponSlot") : 0;
        slot = saved >= 0 && saved <= 1 ? saved : 0;
        owner = null; missingTicks = 0;
        entityData.set(OWNER_ID, -1); entityData.set(SLOT, slot);
        setNoAi(true); setNoGravity(true); noPhysics = true;
    }
}
