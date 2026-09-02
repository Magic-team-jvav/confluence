package org.confluence.mod.common.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.jetbrains.annotations.Nullable;

/// 血肉墙眼睛和嘴部的公共战斗部件。
///
/// 部件只承担命中判定、接触伤害和目标同步，生命值仍完全属于血肉墙本体。
/// 部件不单独存档；血肉墙重载后会根据已经保存的墙面种子重新建立相同布局，
/// 从而避免临时部件与本体分别存档造成重复实体。
public abstract class WallOfFleshPart extends BaseBossPart<WallOfFlesh> {
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(WallOfFleshPart.class, EntityDataSerializers.INT);
    private static final int CONTACT_INTERVAL = 10;

    private int contactCooldown;

    protected WallOfFleshPart(EntityType<?> type, Level level) {
        super(type, level);
    }

    public final void setMaster(WallOfFlesh master) {
        bindTo(master);
    }

    public final @Nullable LivingEntity getPartTarget() {
        Entity entity = level().getEntity(entityData.get(TARGET_ID));
        return entity instanceof LivingEntity living && living.isAlive()
                ? living : null;
    }

    final void setPartTarget(@Nullable LivingEntity target) {
        entityData.set(TARGET_ID, target == null ? -1 : target.getId());
    }

    @Override
    protected final void tickPart(WallOfFlesh master) {
        setYRot(master.getYRot());
        setXRot(master.getXRot());
        if (level().isClientSide) {
            return;
        }

        LivingEntity assignedTarget = master.getAssignedTarget(this);
        if (getPartTarget() != assignedTarget) {
            setPartTarget(assignedTarget);
        }
        tickAttack(master, assignedTarget);
        tickContactDamage(master);
    }

    protected abstract void tickAttack(WallOfFlesh master, @Nullable LivingEntity target);

    private void tickContactDamage(WallOfFlesh master) {
        if (master.getTarget() == null) return;
        if (contactCooldown > 0) {
            contactCooldown--;
            return;
        }
        for (Entity entity : SweptContactAttack.findTargets(this, 0.0D,
                SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && living != master && master.canAttack(living))) {
            LivingEntity living = (LivingEntity) entity;
            if (master.doHurtTarget(living)) {
                contactCooldown = CONTACT_INTERVAL;
                return;
            }
        }
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(TARGET_ID, -1);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return hurtOwnerAndPart(source, amount, 1.0F);
    }

    @Override
    protected Class<WallOfFlesh> getOwnerType() {
        return WallOfFlesh.class;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
}
