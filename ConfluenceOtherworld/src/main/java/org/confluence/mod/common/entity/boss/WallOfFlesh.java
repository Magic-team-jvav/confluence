package org.confluence.mod.common.entity.boss;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.effect.harmful.HorrifiedEffect;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.BossEntities;

/**
 * 肉墙——沿水平方向推进的地狱 Boss。移动速度随血量降低而加快。
 * 周期性从眼部射击激光，从顶部生成饿鬼。
 */
public class WallOfFlesh extends BaseBoss {
    private static final double BASE_SPEED = 0.1;
    private static final int SHOOT_COOLDOWN = 30;
    private static final int HUNGRY_COOLDOWN = 200;
    private static final int HUNGRY_MAX = 8;
    private int shootTimer = 0;
    private int hungryTimer = HUNGRY_COOLDOWN;

    public WallOfFlesh(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        this.noPhysics = false;
        this.xpReward = 3000;
    }

    // === Attributes ===

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 25.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(100); // passive — movement is in tick()
            }
        };
    }

    // === Movement ===

    @Override
    public void tick() {
        super.tick();

        double hpRatio = getHealth() / getMaxHealth();
        double speed = BASE_SPEED * (1 + (1 - hpRatio) * 2.5); // up to 3.5x
        setDeltaMovement(-speed, 0, 0); // sweep west

        if (!level().isClientSide) {
            // Find target for shooting
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 48);
                if (nearest != null) setTarget(nearest);
            }

            // Apply Horrified effect to nearby players
            if (tickCount % 20 == 0) {
                HorrifiedEffect horrified = ModEffects.HORRIFIED.get();
                horrified.setWallOfFlesh(this);
                for (Player player : level().players()) {
                    if (!player.isCreative() && !player.isSpectator()
                            && player.getBoundingBox().intersects(getBoundingBox().inflate(48))
                            && player.level().dimension() == level().dimension()) {
                        if (!player.hasEffect(ModEffects.HORRIFIED.get())) {
                            player.addEffect(new MobEffectInstance(ModEffects.HORRIFIED.get(), 100));
                        }
                    }
                }
            }

            // Shoot lasers from eyes
            if (getTarget() != null) {
                shootTimer--;
                if (shootTimer <= 0) {
                    shootTimer = (int) (SHOOT_COOLDOWN / (1 + (1 - hpRatio)));
                    shootEyeLasers();
                }
            }

            // Spawn Hungry
            int hungryCount = level().getEntities(this, getBoundingBox().inflate(16),
                    e -> e instanceof Hungry && e.isAlive()).size();
            if (hungryCount < HUNGRY_MAX) {
                hungryTimer--;
                if (hungryTimer <= 0) {
                    hungryTimer = HUNGRY_COOLDOWN + random.nextInt(60);
                    spawnHungry();
                }
            }
        }
    }

    private void shootEyeLasers() {
        if (getTarget() == null || !(level() instanceof ServerLevel)) return;
        // Left eye + right eye positions relative to body
        Vec3 leftEye = position().add(0, getBbHeight() * 0.7, -getBbWidth() * 0.3);
        Vec3 rightEye = position().add(0, getBbHeight() * 0.7, getBbWidth() * 0.3);
        shootLaserFrom(leftEye);
        shootLaserFrom(rightEye);
    }

    private void shootLaserFrom(Vec3 origin) {
        if (getTarget() == null) return;
        Vec3 dir = getTarget().getEyePosition().subtract(origin).normalize();
        // Use ShootAction logic via temporary approach: direct damage hitscan
        Vec3 end = origin.add(dir.scale(48));
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().expandTowards(dir.scale(48)).inflate(1.0))) {
            if (e == this || !canAttack(e)) continue;
            if (e.getBoundingBox().clip(origin, end).isPresent()) {
                e.hurt(damageSources().mobAttack(this), 12.0f);
                break;
            }
        }
    }

    private void spawnHungry() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        Hungry hungry = BossEntities.HUNGRY.get().create(level());
        if (hungry != null) {
            // Position above the wall
            double rx = random.nextFloat() * getBbWidth() - getBbWidth() / 2;
            double ry = getBbHeight() + 1;
            double rz = (random.nextFloat() - 0.5) * getBbWidth();
            Vec3 relPos = new Vec3(rx, ry, rz);
            hungry.setPos(position().add(relPos));
            hungry.setMaster(this, relPos);
            if (getTarget() != null) hungry.setTarget(getTarget());
            serverLevel.addFreshEntity(hungry);
        }
    }

    // === Y lock ===

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
    }

    @Override
    public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected boolean shouldDiscardWhenNoTarget() { return false; }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        // TODO: trigger Hardmode activation
    }

    public void setForward(Direction direction) {
        // todo
    }
}
