package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class HurtnadoProjectile extends AbstractManaProjectile implements Immunity {
    private static final WeaponTargeting TARGETING = new WeaponTargeting(TargetPolicy.ENEMY_ONLY);

    private Entity target;
    /// 是否把伤害来源按近战算。
    ///
    /// 天候棒自己使用时保持魔法伤害；风锚链球投出时召唤的飓风改为近战伤害，
    /// 与链球本体一致。该开关写入 NBT，保证存档往返后仍是近战。
    private boolean meleeDamage = false;
    public float rotateO = 0.0F;
    public float rotate = 0.0F;

    public HurtnadoProjectile(EntityType<HurtnadoProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HurtnadoProjectile(LivingEntity living) {
        this(ModEntities.HURTNADO.get(), living.level());
    }

    @Override
    public void baseTick() {
        if (tickCount > 190) {
            if (!level().isClientSide) {
                discardInTicks(1);
            }
            return;
        }
        super.baseTick();
        doBouncyMove(true, this::doNothing, UnaryOperator.identity());

        if (level().isClientSide) {
            if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
            this.rotateO = rotate;
            this.rotate += Mth.PI * 0.15F;
        }

        if (target == null || target.isRemoved()) {
            this.target = getNearestEnemy();
        }
        if (TARGETING.canKeep(getOwner(), target)) {
            setDeltaMovement(getDeltaMovement().scale(0.96).add(LibMathUtils.getVectorA2B(this, target).scale(0.05)));
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void doHitCheck() {
        AABB boundingBox = getBoundingBox().inflate(1.0);
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level(), this, boundingBox.getMinPosition(), boundingBox.getMaxPosition(), boundingBox, this::canHitEntity, 0.5F);
        if (hitResult == null) return;
        Entity entity = hitResult.getEntity();
        /// 飓风是持续存在的伤害区域，不能写入 penetrateSet：父类的穿透集合会让同一敌人
        /// 被记录后再也无法命中，表现就是「一只怪整场只吃一次伤害」。
        /// 重复命中的间隔交给 `Immunity` 的静态无敌帧（8 tick）控制。
        doHurtAndKnockback(entity, 0.5, 0.2);
    }

    private @Nullable Entity getNearestEnemy() {
        return LibEntityUtils.nearest(level().getEntities(this, new AABB(blockPosition()).inflate(15.5)),
                candidate -> TARGETING.canAcquire(getOwner(), candidate),
                java.util.Comparator.comparingDouble(candidate -> candidate.distanceToSqr(getX(), getY(), getZ())));
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 8;
    }

    /// 切换伤害来源为近战（风锚链球召唤的飓风）。
    public void setMeleeDamage(boolean meleeDamage) {
        this.meleeDamage = meleeDamage;
    }

    public boolean isMeleeDamage() {
        return meleeDamage;
    }

    @Override
    public DamageSource getDamageSource() {
        return meleeDamage
                ? LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, getOwner())
                : super.getDamageSource();
    }

    /// 近战模式下伤害已由召唤方按链球本体伤害算好。
    ///
    /// 链球伤害取的是玩家攻击力属性，而该属性已经包含手持链球自身的词条加成；
    /// 若再走一遍父类的词条结算就会把词条算两次，所以近战模式直接返回设定值。
    @Override
    public float getCalculatedDamage() {
        return meleeDamage ? damage : super.getCalculatedDamage();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("MeleeDamage", meleeDamage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.meleeDamage = compound.getBoolean("MeleeDamage");
    }
}
