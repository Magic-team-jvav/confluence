package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.init.ModEffects;

/** Imprecise Tesla shot; native projectile owner persistence prevents orphaned damage. */
public final class MartianElectricBolt extends StraightMonsterProjectile {
    public static final float SHOT_DAMAGE = 5.0F;
    private int missingOwnerTicks;

    public MartianElectricBolt(EntityType<? extends MartianElectricBolt> type, Level level) {
        super(type, level);
    }

    public void configureShot(Mob owner, LivingEntity target) {
        if (owner == null || target == null) return;
        Vec3 origin = new Vec3(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
        Vec3 aim = target.getEyePosition().subtract(origin);
        double horizontalSquared = aim.x * aim.x + aim.z * aim.z;
        double diagonal = horizontalSquared < 1.0E-6D ? 0.0D
                : 2.0D * Math.abs(aim.x * aim.z) / horizontalSquared;
        double distance = aim.length();
        double yawSpread = distance < 5.0D ? 110.0D + 15.0D * diagonal
                : 5.0D + 18.0D * diagonal + Math.min(12.0D, 60.0D / Math.max(distance, 1.0D));
        double pitchSpread = distance < 5.0D ? 40.0D : 4.0D + 12.0D * diagonal;
        float yaw = (float) Math.toRadians((random.nextDouble() * 2.0D - 1.0D) * yawSpread);
        float pitch = (float) Math.toRadians((random.nextDouble() * 2.0D - 1.0D) * pitchSpread);
        float speed = 1.5F + random.nextFloat() * 0.5F;
        configureAimed(owner, origin, aim.yRot(yaw).xRot(pitch), SHOT_DAMAGE, speed, 0.0F, 100);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isRemoved()) return;
        if (getOwner() == null) {
            if (++missingOwnerTicks > 40) discard();
        } else {
            missingOwnerTicks = 0;
        }
    }

    /// 敌怪阵营不互伤：电弧穿过所有同类敌怪，只命中玩家、玩家召唤物等敌对目标。
    @Override
    public boolean canHitEntity(Entity target) {
        return !EnemyDamageRules.isEnemy(target) && super.canHitEntity(target);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (random.nextInt(3) != 0) target.addEffect(new MobEffectInstance(ModEffects.ELECTRIFIED.get(), 100));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MissingOwnerTicks", missingOwnerTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        missingOwnerTicks = tag.contains("MissingOwnerTicks") ? Math.max(0, tag.getInt("MissingOwnerTicks")) : 0;
    }
}
