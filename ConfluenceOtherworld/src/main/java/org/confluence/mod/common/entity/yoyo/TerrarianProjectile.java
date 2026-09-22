package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.yoyo.YoyoSession;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 泰拉悠悠球的独立射弹：发射时选敌，直线飞行，前十 tick 可命中一次，随后减速消失。
public final class TerrarianProjectile extends Projectile implements Immunity, GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private float damage;
    private float criticalChance;
    private float knockback;
    private @Nullable YoyoEntity sourceYoyo;
    private boolean spent;

    public TerrarianProjectile(EntityType<? extends TerrarianProjectile> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public static void shootAtNearest(YoyoEntity source) {
        Entity owner = source.getOwner();
        if (owner == null || source.level().isClientSide) return;
        double distance = 25 * 25;
        LivingEntity nearest = null;
        for (LivingEntity candidate : source.level().getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(25))) {
            if (!EnemyDamageRules.isEnemy(candidate) || !ProjectileHitRules.canHit(owner, candidate))
                continue;
            double next = source.distanceToSqr(candidate);
            if (next < distance) {
                nearest = candidate;
                distance = next;
            }
        }
        Vec3 direction = nearest != null ? nearest.getBoundingBox().getCenter().subtract(source.position())
                : new Vec3(source.getRandom1211().nextGaussian(), source.getRandom1211().nextGaussian(), source.getRandom1211().nextGaussian());
        TerrarianProjectile shot = new TerrarianProjectile(ModEntities.TERRARIAN_PROJECTILE.get(), source.level());
        shot.setOwner(owner);
        shot.sourceYoyo = source;
        shot.damage = source.getDamage();
        shot.criticalChance = source.getCriticalChance();
        shot.knockback = source.getKnockback();
        shot.setPos(source.position().add(0, source.getBbHeight() * 0.5, 0));
        shot.setDeltaMovement(direction.normalize().scale(0.8));
        source.level().addFreshEntity(shot);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = getDeltaMovement();
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.GLOW, getX(), getY(), getZ(), 0, 0, 0);
        } else {
            if (!(getOwner() instanceof ServerPlayer owner) || !owner.isAlive() || owner.isSpectator() || tickCount > 20) {
                discard();
                return;
            }
            if (tickCount > 10) spent = true;
            if (!spent) {
                for (Entity candidate : level().getEntities(this, getBoundingBox().expandTowards(motion).inflate(0.3))) {
                    if (!EnemyDamageRules.isEnemy(candidate) || !ProjectileHitRules.canHit(owner, candidate))
                        continue;
                    LivingEntity logical = ProjectileHitRules.logicalLivingTarget(candidate);
                    if (logical == null) continue;
                    if (!candidate.getBoundingBox().inflate(0.3).contains(position())
                            && candidate.getBoundingBox().inflate(0.3).clip(position(), position().add(motion)).isEmpty())
                        continue;
                    Entity recipient = ProjectileHitRules.damageRecipient(candidate);
                    DamageSource source = LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, owner);
                    /// 实体类型作为共享无敌帧身份，多发泰拉射弹仍共用四 tick 冷却。
                    if (!Immunity.hurt((Immunity) getType(), recipient, logical, source, damage))
                        continue;
                    logical.knockback(knockback, -motion.x, -motion.z);
                    if (sourceYoyo != null) YoyoSession.of(owner).onHit(owner, sourceYoyo);
                    spent = true;
                    break;
                }
            }
        }
        setPos(position().add(motion));
        if (tickCount > 10) setDeltaMovement(motion.scale(0.85));
    }

    public float getCriticalChance() {return criticalChance;}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return animationCache;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    protected void defineSynchedData() {}

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    public boolean canChangeDimensions() {return false;}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return NetworkHooks.getEntitySpawningPacket(this);}

    @Override
    public Type confluence$getImmunityType() {return Type.STATIC;}

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {return 4;}
}
