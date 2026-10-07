package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.confluence.mod.common.data.map.CreatureDefinition.ProjectileOverrides;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.monster.Gigazapper;
import org.confluence.mod.common.init.ModEffects;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 跟随持有者的近战刺击；可见武器由持有者模型中的电矛骨骼渲染。
public final class ElectricSpear extends Projectile {
    private static final float MODEL_SCALE = 1.3F;
    private static final int STRIKE_TICKS = 10;
    /// 专家矛头伤害 100 × 26%。
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

    /// 从模型矛杆坐标 [-6,8,-9]、尺寸 [1,1,12] 和攻击臂偏移 [1,4,0] 还原世界坐标。
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
        /// 根据右手矛杆的横向偏移校正瞄准角度。
        double offset = Math.asin(Math.min(1.0, 4.5 * UNIT * owner.getScale() / Math.max(distance, 0.001)));
        owner.faceCombatDirection(direction.yRot((float) offset), 30, 0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

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
        /// 与 attack.spear 动画同步：第二刻伸出，第五刻后收回。
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
                target.addEffect(new MobEffectInstance(ModEffects.ELECTRIFIED, 80 + random.nextInt(81)));
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        /// 短暂刺击在区块重载后结束，避免重新命中同一目标。
        strikeAge = STRIKE_TICKS;
    }
}
