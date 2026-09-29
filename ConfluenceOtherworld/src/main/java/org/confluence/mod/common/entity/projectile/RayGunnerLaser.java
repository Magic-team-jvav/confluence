package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.EnemyDamageRules;

public final class RayGunnerLaser extends StraightMonsterProjectile {
    public static final float SHOT_DAMAGE = 12.0F;
    public static final float SHOT_SPEED = 5.25F;
    public static final int MAXIMUM_LIFETIME = 20;
    private static final int OWNER_GRACE_TICKS = 40;

    private int missingOwnerTicks;

    public RayGunnerLaser(EntityType<? extends RayGunnerLaser> type, Level level) {
        super(type, level);
    }

    public void configureShot(Mob owner, LivingEntity target) {
        if (owner == null || target == null) return;
        Vec3 origin = new Vec3(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
        configureAimed(owner, origin, target.getEyePosition().subtract(origin),
                SHOT_DAMAGE, SHOT_SPEED, 0.0F, MAXIMUM_LIFETIME);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isRemoved()) return;
        if (getOwner() == null) {
            if (++missingOwnerTicks > OWNER_GRACE_TICKS) discard();
        } else {
            missingOwnerTicks = 0;
        }
    }

    /// 敌怪阵营不互伤：射线穿过所有同类敌怪，只命中玩家、玩家召唤物等敌对目标。
    @Override
    public boolean canHitEntity(Entity target) {
        return target != null && !EnemyDamageRules.isEnemy(target) && super.canHitEntity(target);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MissingOwnerTicks", missingOwnerTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        missingOwnerTicks = tag.contains("MissingOwnerTicks")
                ? Mth.clamp(tag.getInt("MissingOwnerTicks"), 0, OWNER_GRACE_TICKS) : 0;
    }
}
