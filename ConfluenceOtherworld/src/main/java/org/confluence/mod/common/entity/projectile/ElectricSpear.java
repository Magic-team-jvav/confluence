package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.monster.Gigazapper;
import org.confluence.mod.common.data.map.CreatureDefinition.ProjectileOverrides;
import org.confluence.mod.common.init.ModEffects;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Attached melee strike; the visible weapon is the owner's electric_spear bone. */
public final class ElectricSpear extends Projectile {
    private static final float MODEL_SCALE = 1.3F;
    private static final int STRIKE_TICKS = 10;
    // 专家矛头伤害 100 × 26%。
    private static final float ATTACK_DAMAGE = 26.0F;
    private static final double UNIT = MODEL_SCALE / 16.0;
    private final Set<UUID> hitTargets = new HashSet<>();
    private int strikeAge;
    private float damage = ATTACK_DAMAGE;

    public ElectricSpear(EntityType<? extends ElectricSpear> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public void configure(Gigazapper owner) {
        setOwner(owner);
        damage = ProjectileOverrides.get(owner, getType()).damageOr(ATTACK_DAMAGE);
        setPos(owner.position());
    }

    /** Model shaft [-6,8,-9] + [1,1,12], attack arm offset [1,4,0]. */
    public static Vec3 modelPoint(Gigazapper owner, double z) {
        double yaw = Math.toRadians(owner.yBodyRot);
        double unit = UNIT * owner.getScale();
        double x = -4.5 * unit;
        return owner.position().add(x * Math.cos(yaw) + z * unit * Math.sin(yaw),
                12.5 * unit, x * Math.sin(yaw) - z * unit * Math.cos(yaw));
    }

    public static boolean withinReach(Gigazapper owner, LivingEntity target) {
        return intersectsShaft(target.getBoundingBox(), modelPoint(owner, -1), modelPoint(owner, -13), owner.getScale());
    }

    private static boolean intersectsShaft(AABB bounds, Vec3 start, Vec3 end, float scale) {
        AABB expanded = bounds.inflate(0.55 * UNIT * scale);
        return expanded.contains(start) || expanded.contains(end) || expanded.clip(start, end).isPresent();
    }

    public static void faceTarget(Gigazapper owner, LivingEntity target) {
        Vec3 direction = target.position().subtract(owner.position());
        double distance = direction.horizontalDistance();
        // Aim the right-hand shaft at the victim instead of aiming the body's centre line.
        double offset = Math.asin(Math.min(1.0, 4.5 * UNIT * owner.getScale() / Math.max(distance, 0.001)));
        owner.faceCombatDirection(direction.yRot((float) offset), 30, 0);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isRemoved()) {
            return;
        }
        strikeAge++;
        if (!(getOwner() instanceof Gigazapper owner) || !owner.isAlive()
                || strikeAge > STRIKE_TICKS) {
            discard();
            return;
        }
        setPos(owner.position());
        // Matches attack.spear keyframes: extend at tick 2, retract after tick 5.
        if (strikeAge < 2 || strikeAge > 5) {
            return;
        }
        Vec3 start = modelPoint(owner, -1);
        Vec3 end = modelPoint(owner, -13);
        BlockHitResult wall = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, this));
        if (wall.getType() != HitResult.Type.MISS) {
            end = wall.getLocation();
        }
        AABB bounds = new AABB(start, end).inflate(0.55 * UNIT * owner.getScale());
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, bounds)) {
            if (!target.isAlive() || target == owner || EnemyDamageRules.isEnemy(target) || !owner.canAttack(target)
                    || hitTargets.contains(target.getUUID())
                    || !intersectsShaft(target.getBoundingBox(), start, end, owner.getScale())) {
                continue;
            }
            hitTargets.add(target.getUUID());
            if (target.hurt(damageSources().mobAttack(owner), damage)) {
                target.addEffect(new MobEffectInstance(ModEffects.ELECTRIFIED.get(), 80 + random.nextInt(81)));
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Short melee strikes never resume against the same victim after chunk reload.
        strikeAge = STRIKE_TICKS;
    }
}
