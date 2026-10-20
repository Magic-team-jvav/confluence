package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;

import org.confluence.lib.common.entity.PartHitTarget;

/// 炮台只提供受击位置，血量、破坏状态和存档均由步行者本体管理。
public final class WalkerWeapon extends PartEntity<MartianWalker> implements PartHitTarget {
    private final int slot;
    private int followedTick = Integer.MIN_VALUE;

    public WalkerWeapon(MartianWalker parent, int slot) {
        super(parent);
        this.slot = slot;
    }

    public int slot() {return slot;}

    public float getHealth() {return getParent().weaponHealth(slot);}

    @Override
    public Component getName() {
        return Component.translatable(slot == 0 ? "entity.confluence.martian_walker_weapon.left"
                : "entity.confluence.martian_walker_weapon.right");
    }

    /// 与模型炮座保持相同位置；已破坏的炮座收起受击范围。
    public void follow(AABB bounds) {
        if (bounds == null) {
            setPos(getParent().position());
            setBoundingBox(new AABB(position(), position()));
            return;
        }
        boolean first = followedTick == Integer.MIN_VALUE;
        if (followedTick != getParent().tickCount) {
            setOldPosAndRot();
            followedTick = getParent().tickCount;
        }
        setPos(bounds.getCenter().x, bounds.minY, bounds.getCenter().z);
        setBoundingBox(bounds);
        if (first) setOldPosAndRot();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {return acceptsDirectHit() && getParent().hurtWeapon(slot, source, amount);}

    @Override
    public boolean isAlive() {return getParent().isAlive() && !getParent().weaponDestroyed(slot);}

    @Override
    public boolean isPickable() {return acceptsDirectHit();}

    @Override
    public boolean canBeCollidedWith() {return false;}

    @Override
    public boolean is(Entity entity) {return entity == this;}

    @Override
    public Entity encounterOwner() {return getParent();}

    @Override
    public Entity dedupeIdentity() {return this;}

    @Override
    public boolean acceptsDirectHit() {return isAlive() && getBoundingBox().getSize() > 0;}

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    public boolean shouldBeSaved() {return false;}
}
