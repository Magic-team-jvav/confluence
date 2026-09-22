package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.confluence.lib.api.entity.Boss;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.jetbrains.annotations.Nullable;

/// 眼嘴由肉墙本体创建、定位和驱动，不独立追踪、存档或发送位置包。
public abstract class WallOfFleshPart extends PartEntity<WallOfFlesh> implements Boss.BossPart, PartHitTarget {
    private final float width;
    private final float height;
    private final int index;
    private final float visualRoll;
    private Vec3 sweepStart = Vec3.ZERO;

    protected WallOfFleshPart(WallOfFlesh parent, int index, float width, float height) {
        super(parent);
        this.index = index;
        /// 固定布局种子与部件序号决定倾斜，重载、多人追踪时一致，不逐帧随机。
        long hash = parent.getLayoutSeed() + index * 0x9E3779B97F4A7C15L;
        hash = (hash ^ (hash >>> 30)) * 0xBF58476D1CE4E5B9L;
        hash = (hash ^ (hash >>> 27)) * 0x94D049BB133111EBL;
        visualRoll = (((hash ^ (hash >>> 31)) >>> 40) / 16777215.0F - 0.5F) * 30.0F;
        this.width = width;
        this.height = height;
        noPhysics = true;
        refreshDimensions();
    }

    public final int getPartIndex() {return index;}

    public final float getVisualRoll() {return visualRoll;}

    public final @Nullable LivingEntity getPartTarget() {return getParent().getPartTarget(index);}

    /// 坐标由同一个本体位置计算，避免眼嘴各自插值导致墙面撕裂。
    final void updatePosition(Vec3 position) {
        sweepStart = position();
        xo = xOld = getX();
        yo = yOld = getY();
        zo = zOld = getZ();
        refreshDimensions();
        setPos(position);
        setYRot(getParent().getYRot());
    }

    final void tickPart() {
        WallOfFlesh master = getParent();
        if (level().isClientSide || !master.isAlive()) return;
        tickAttack(master, master.getAssignedTarget(this));
        if (master.getTarget() == null) return;
        for (Entity entity : SweptContactAttack.findTargets(this, sweepStart, 0.0D,
                SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && living != master && master.canAttack(living))) {
            master.hurtOnContact((LivingEntity) entity);
        }
    }

    protected abstract void tickAttack(WallOfFlesh master, @Nullable LivingEntity target);

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(width, height).scale(getParent().getScale());
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(this instanceof WallOfFleshEye ? "entity.confluence.wall_of_flesh_eye" : "entity.confluence.wall_of_flesh_mouth");
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || !isAttackable() || isInvulnerableTo(source) || EnemyDamageRules.blocks(this, source))
            return false;
        return getParent().hurtFromPart(this, source, amount);
    }

    @Override
    public boolean isAttackable() {return !isRemoved() && getParent().isAlive();}

    @Override
    public boolean isPickable() {return isAttackable();}

    @Override
    public boolean canBeHitByProjectile() {return isAttackable();}

    @Override
    public boolean is(Entity entity) {return this == entity || entity == getParent();}

    @Override
    public boolean fireImmune() {return true;}

    @Override
    public Entity damageRecipient() {return this;}

    @Override
    public Entity encounterOwner() {return getParent();}

    @Override
    public Entity dedupeIdentity() {return getParent();}

    @Override
    public boolean acceptsDirectHit() {return isAttackable();}

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
