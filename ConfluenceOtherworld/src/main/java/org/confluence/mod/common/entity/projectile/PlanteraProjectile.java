package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.boss.Plantera;
import org.joml.Vector3f;

public class PlanteraProjectile extends StraightMonsterProjectile {
    // 每 tick 扣除的竖直速度（格/tick），决定孢子喷泉的顶点高度。
    private static final double SPORE_GRAVITY = 0.028;
    // 孢子下落速度上限（格/tick），限制越过顶点后的沉降速度。
    private static final double SPORE_TERMINAL_SPEED = 0.12;
    // 刺球每 tick 增加的向下速度（格/tick），同时影响落地和再次弹起的弧线。
    private static final double THORN_GRAVITY = 0.065;
    // 刺球每 tick 朝目标施加的水平加速度（格/tick²），竖直运动仍由重力决定。
    private static final double THORN_TRACKING = 0.025;
    // 专家及以上种子每 tick 混入目标方向的比例，转向后保持原速。
    private static final double SEED_TRACKING = 0.045;
    // 叶片去程每 tick 混入目标方向的比例，与侧向偏转共同形成弧线。
    private static final double LEAF_TRACKING = 0.025;
    // 大种子飞行多少 tick 仍未命中时展开碎片弹幕。
    private static final int SPLIT_AFTER_TICKS = 16;
    // 叶片飞行多少 tick 后转向发射者。
    private static final int LEAF_RETURN_TICKS = 30;

    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(PlanteraProjectile.class, EntityDataSerializers.INT);
    private final Variant initialVariant;

    public PlanteraProjectile(EntityType<? extends PlanteraProjectile> type, Level level, Variant variant) {
        super(type, level);
        initialVariant = variant;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(MODE, -1);
    }

    private Variant variant() {
        int mode = entityData.get(MODE);
        return mode < 0 ? initialVariant : Variant.VALUES[mode];
    }

    public void launch(Plantera owner, Variant variant, Vec3 origin, Vec3 velocity, float damage) {
        entityData.set(MODE, variant.ordinal());
        configure(owner, origin, velocity, damage, variant.lifetime);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        Variant mode = variant();
        LivingEntity target = getOwner() instanceof Mob owner ? owner.getTarget() : null;
        Vec3 aim = target != null && target.isAlive() ? target.getBoundingBox().getCenter().subtract(position()) : Vec3.ZERO;
        return switch (mode) {
            case SEED, POISON_SEED, SPLITTING_SEED ->
                    LibUtils.isAtLeastExpert(level()) ? steer(velocity, aim, SEED_TRACKING) : velocity;
            case SEED_FRAGMENT -> velocity;
            case THORN_BALL ->
                    velocity.add(aim.multiply(1, 0, 1).normalize().scale(THORN_TRACKING)).add(0, -THORN_GRAVITY, 0);
            case SPORE ->
                    new Vec3(velocity.x * 0.98, Math.max(-SPORE_TERMINAL_SPEED, velocity.y - SPORE_GRAVITY), velocity.z * 0.98);
            case PLANTING_SEED -> velocity.add(0, -0.065, 0);
            case LEAF -> tickCount < LEAF_RETURN_TICKS
                    ? steer(velocity.yRot(-0.035F), aim, LEAF_TRACKING)
                    : getOwner() == null ? velocity : steer(velocity, getOwner().getBoundingBox().getCenter().subtract(position()), 0.13);
        };
    }

    private static Vec3 steer(Vec3 velocity, Vec3 aim, double amount) {
        return aim.lengthSqr() < 1.0E-8 ? velocity : velocity.normalize().lerp(aim.normalize(), amount).normalize().scale(velocity.length());
    }

    @Override
    protected HitResult findImpact() {
        if (variant() == Variant.THORN_BALL || variant() == Variant.PLANTING_SEED)
            return super.findImpact();
        Vec3 start = position();
        Vec3 end = start.add(getDeltaMovement());
        var hit = ProjectileUtil.getEntityHitResult(level(), this, start, end, getBoundingBox().expandTowards(getDeltaMovement()).inflate(0.25), this::canHitEntity);
        return hit == null ? BlockHitResult.miss(end, Direction.UP, blockPosition()) : hit;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (variant() == Variant.PLANTING_SEED) {
            if (!level().isClientSide && getOwner() instanceof Plantera boss)
                boss.plantTentacle(result.getLocation());
            discard();
            return;
        }
        Direction direction = result.getDirection();
        Vec3 velocity = getDeltaMovement();
        Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
        Vec3 rebound = velocity.subtract(normal.scale(2 * velocity.dot(normal))).scale(0.86);
        if (direction == Direction.UP)
            rebound = new Vec3(rebound.x, 0.5 + random.nextDouble() * 0.4, rebound.z);
        setDeltaMovement(rebound);
        setPos(result.getLocation().add(normal.scale(0.08)));
    }

    @Override
    protected boolean ownsBlockImpactMovement(BlockHitResult result) {
        return variant() == Variant.THORN_BALL;
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (owner instanceof Plantera boss) {
            if (variant() == Variant.SPORE) boss.reportSporeHit(target);
            else boss.reportAttackHit(target);
        }
        if (variant() != Variant.POISON_SEED) return;
        boolean expert = LibUtils.isAtLeastExpert(level());
        if (!expert && random.nextBoolean()) return;
        int duration = expert ? 80 + random.nextInt(101) : 60 + random.nextInt(81);
        target.addEffect(new MobEffectInstance(MobEffects.POISON, duration));
    }

    @Override
    public boolean isPickable() {
        return variant() == Variant.SPORE;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (variant() != Variant.SPORE || damage <= 0 || source.getEntity() == getOwner())
            return false;
        if (!level().isClientSide) discard();
        return true;
    }

    @Override
    public void tick() {
        if (!level().isClientSide && getOwner() instanceof Plantera boss && !boss.isAlive()) {
            discard();
            return;
        }
        super.tick();
        if (isRemoved()) return;
        Variant mode = variant();
        if (level().isClientSide) {
            level().addParticle(mode.particle, getX(), getY(), getZ(), 0, 0, 0);
        } else if (mode == Variant.SPLITTING_SEED && tickCount >= SPLIT_AFTER_TICKS && getOwner() instanceof Plantera boss && boss.isAlive()) {
            Vec3 forward = getDeltaMovement().normalize();
            for (int index = -3; index <= 3; index++) {
                PlanteraProjectile fragment = getType().create(level()) instanceof PlanteraProjectile projectile ? projectile : null;
                if (fragment == null) break;
                Vec3 velocity = forward.yRot(index * 0.12F).xRot(index % 2 * 0.13F).scale(0.95);
                fragment.launch(boss, Variant.SEED_FRAGMENT, position(), velocity, getDamage() * 0.45F);
                level().addFreshEntity(fragment);
            }
            discard();
        }
    }

    public enum Variant {
        SEED(100, 20, dust(0.98F, 0.42F, 0.66F, 1)),
        THORN_BALL(200, 29, dust(0.35F, 0.64F, 0.08F, 1.8F)),
        SPORE(125, 37, ParticleTypes.SPORE_BLOSSOM_AIR),
        POISON_SEED(100, 25, dust(0.25F, 0.85F, 0.12F, 1)),
        SPLITTING_SEED(85, 30, dust(0.94F, 0.45F, 0.72F, 2)),
        PLANTING_SEED(160, 20, dust(0.45F, 0.76F, 0.22F, 1.3F)),
        LEAF(75, 29, dust(0.54F, 0.78F, 0.21F, 2.6F)),
        SEED_FRAGMENT(55, 14, dust(0.94F, 0.45F, 0.72F, 0.8F));

        private static final Variant[] VALUES = values();
        private final int lifetime;
        private final float baseDamage;
        private final ParticleOptions particle;

        Variant(int lifetime, float baseDamage, ParticleOptions particle) {
            this.lifetime = lifetime;
            this.baseDamage = baseDamage;
            this.particle = particle;
        }

        public float baseDamage() {
            return baseDamage;
        }

        private static DustParticleOptions dust(float red, float green, float blue, float size) {
            return new DustParticleOptions(new Vector3f(red, green, blue), size);
        }
    }
}
