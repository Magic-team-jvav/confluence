package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.List;

public class CrystalChargeProjectile extends AbstractManaProjectile {
    private ParticleEmitter emitter;

    public CrystalChargeProjectile(EntityType<? extends CrystalChargeProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("crystal_serpent_projectile"));
    }

    public CrystalChargeProjectile(LivingEntity living) {
        this(ModEntities.CRYSTAL_CHARGE_1.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (getType() == ModEntities.CRYSTAL_CHARGE_1.get()) {
            doSimpleMove();
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.96));
            doBouncyMove(true, this::doNothing, vec3 -> vec3.scale(0.98));
            doAgeCheck(20);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (getType() == ModEntities.CRYSTAL_CHARGE_1.get()) {
            doSplit();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (doHurtAndKnockback(result.getEntity(), 0.44, 0.2)) {
            if (getType() == ModEntities.CRYSTAL_CHARGE_1.get()) {
                doSplit();
            }
        }
    }

    @Override
    protected boolean doHurtAndKnockback(Entity target, double knockbackStrength, double knockbackMotionY) {
        if (getType() == ModEntities.CRYSTAL_CHARGE_2.get()) {
            knockbackStrength *= 0.8;
            knockbackMotionY *= 0.8;
        }
        return super.doHurtAndKnockback(target, knockbackStrength, knockbackMotionY);
    }

    private void doSplit() {
        if (level().isClientSide) return;
        if (!(getOwner() instanceof ServerPlayer player)) {
            discard();
            return;
        }
        ProjectileCombatSnapshot parentSnapshot = getProjectileCombatSnapshot();
        if (parentSnapshot == null) {
            discard();
            return;
        }

        int amount = random.nextIntBetweenInclusive(3, 5);
        float velocity = getDefaultVelocity();
        Vec3 parentMotion = getDeltaMovement();
        ProjectileCombatSnapshot childSnapshot = parentSnapshot.derive(
                parentSnapshot.baseDamage() * 0.8F,
                velocity,
                0.0F);
        List<ProjectileLaunch> launches = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            CrystalChargeProjectile projectile = new CrystalChargeProjectile(ModEntities.CRYSTAL_CHARGE_2.get(), level());
            projectile.setDefaultVelocity(velocity);
            // 先让原版 shoot 只负责抽取一次旧有的 10 度散布，再把得到的方向交给事务；
            // 服务不会再次随机，因此子弹数量、散布和速度均保持原效果。
            projectile.shoot(parentMotion.x, parentMotion.y, parentMotion.z, velocity, 10.0F);
            launches.add(new ProjectileLaunch(
                    projectile,
                    position(),
                    projectile.getDeltaMovement()));
        }
        ServerProjectileFireService.spawnDerived(player, childSnapshot, launches);
        discard();
    }

    @Override
    public double getDefaultGravity() {
        return 0.04;
    }
}
