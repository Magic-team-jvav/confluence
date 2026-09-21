package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.yoyo.YoyoSession;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 悠悠球的短寿命衍生攻击，保留发射伤害，不读取玩家后来切换的武器。
public class YoyoEffectProjectile extends Projectile implements ItemSupplier, Immunity {
    private static final EntityDataAccessor<Integer> KIND = SynchedEntityData.defineId(YoyoEffectProjectile.class, EntityDataSerializers.INT);
    private final Set<UUID> hitTargets = new HashSet<>();
    private float damage;
    private float criticalChance;
    private float knockback;
    private @Nullable UUID excludedTarget;
    private @Nullable YoyoEntity sourceYoyo;
    private boolean spent;

    public enum Kind implements Immunity {
        CRYSTAL, WAVE, EYE, TERRARIAN, CASCADE;

        @Override
        public Type confluence$getImmunityType() {return Type.STATIC;}

        @Override
        public int confluence$getImmunityDuration(DamageSource source) {return this == CRYSTAL ? 7 : 4;}
    }

    public YoyoEffectProjectile(EntityType<? extends YoyoEffectProjectile> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public static void shoot(YoyoEntity source, Kind kind, Vec3 direction, @Nullable LivingEntity excluded) {
        YoyoEffectProjectile shot = new YoyoEffectProjectile(ModEntities.YOYO_EFFECT.get(), source.level());
        shot.setOwner(source.getOwner());
        shot.entityData.set(KIND, kind.ordinal());
        shot.sourceYoyo = source;
        shot.criticalChance = source.getCriticalChance();
        shot.knockback = source.getKnockback();
        shot.excludedTarget = excluded == null || kind == Kind.WAVE ? null : excluded.getUUID();
        shot.damage = source.getDamage() * (kind == Kind.WAVE ? 0.5F : kind == Kind.EYE || kind == Kind.CASCADE ? 2 : 1);
        shot.setPos(source.position().add(0, source.getBbHeight() * 0.5, 0));
        shot.setDeltaMovement(direction.normalize().scale(kind == Kind.EYE ? 1.2 : 0.8));
        source.level().addFreshEntity(shot);
    }

    /// 只在发射时选择目标，泰拉射弹发射后沿直线运动，不持续追踪。
    public static void shootAtNearest(YoyoEntity source, Kind kind, @Nullable LivingEntity excluded) {
        Entity owner = source.getOwner();
        if (owner == null) return;
        double range = kind == Kind.EYE ? 5 : kind == Kind.CASCADE ? 13 : 25;
        double distance = range * range;
        LivingEntity nearest = null;
        for (LivingEntity candidate : source.level().getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(range))) {
            if (candidate == excluded || !EnemyDamageRules.isEnemy(candidate) || !ProjectileHitRules.canHit(owner, candidate))
                continue;
            if (kind == Kind.CASCADE && !candidate.noPhysics && source.level().clip(new ClipContext(source.position(), candidate.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source)).getType() != HitResult.Type.MISS)
                continue;
            double next = source.distanceToSqr(candidate);
            if (next < distance) {
                nearest = candidate;
                distance = next;
            }
        }
        Vec3 direction;
        if (nearest != null)
            direction = nearest.getBoundingBox().getCenter().subtract(source.position());
        else if (kind == Kind.EYE && excluded != null)
            direction = excluded.getBoundingBox().getCenter().subtract(source.position());
        else
            direction = new Vec3(source.getRandom1211().nextGaussian(), source.getRandom1211().nextGaussian(), source.getRandom1211().nextGaussian());
        shoot(source, kind, direction, excluded);
    }

    @Override
    protected void defineSynchedData() {entityData.define(KIND, 0);}

    @Override
    public void tick() {
        super.tick();
        Kind kind = kind();
        Vec3 motion = getDeltaMovement();
        if (level().isClientSide) {
            level().addParticle(switch (kind) {
                case WAVE -> ParticleTypes.SPLASH;
                case CASCADE -> ParticleTypes.FLAME;
                case CRYSTAL -> ParticleTypes.END_ROD;
                default -> ParticleTypes.GLOW;
            }, getX(), getY(), getZ(), 0, 0, 0);
        } else {
            if (!(getOwner() instanceof ServerPlayer owner) || !owner.isAlive() || owner.isSpectator() || tickCount > 20) {
                discard();
                return;
            }
            if (kind == Kind.TERRARIAN && tickCount > 10) spent = true;
            if (!spent) {
                for (Entity candidate : level().getEntities(this, getBoundingBox().expandTowards(motion).inflate(0.3))) {
                    /// 自动衍生弹幕的命中与索敌采用同一敌怪规则，不误伤沿途城镇 NPC。
                    if (!EnemyDamageRules.isEnemy(candidate) || !ProjectileHitRules.canHit(owner, candidate))
                        continue;
                    Entity identity = ProjectileHitRules.dedupeIdentity(candidate);
                    UUID id = identity.getUUID();
                    LivingEntity logical = ProjectileHitRules.logicalLivingTarget(candidate);
                    if (logical == null) continue;
                    if (logical.getUUID().equals(excludedTarget) && (kind != Kind.CRYSTAL || tickCount <= 7))
                        continue;
                    if (hitTargets.contains(id)) continue;
                    if (!candidate.getBoundingBox().inflate(0.3).contains(position()) && candidate.getBoundingBox().inflate(0.3).clip(position(), position().add(motion)).isEmpty())
                        continue;
                    Entity recipient = ProjectileHitRules.damageRecipient(candidate);
                    DamageSource damageSource = LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, owner);
                    Immunity immunity = kind == Kind.EYE || kind == Kind.CASCADE ? this : kind;
                    if (!Immunity.hurt(immunity, recipient, logical, damageSource, damage))
                        continue;
                    hitTargets.add(id);
                    logical.knockback(knockback, -motion.x, -motion.z);
                    if (kind == Kind.CASCADE && random.nextInt(3) == 0)
                        recipient.setSecondsOnFire(1 + random.nextInt(4));
                    if (kind == Kind.TERRARIAN && sourceYoyo != null)
                        YoyoSession.of(owner).onHit(owner, sourceYoyo);
                    if (kind != Kind.EYE && kind != Kind.WAVE) {
                        spent = true;
                        break;
                    }
                }
            }
        }
        setPos(position().add(motion));
        if (tickCount > 10) setDeltaMovement(motion.scale(0.85));
    }

    public Kind kind() {return Kind.values()[entityData.get(KIND)];}

    public float getCriticalChance() {return kind() == Kind.EYE ? 0 : criticalChance;}

    @Override
    public ItemStack getItem() {
        /// 暂用现有物品标识衍生弹幕，独立美术到位后不影响伤害和运动逻辑。
        return new ItemStack(switch (kind()) {
            case CRYSTAL -> Items.AMETHYST_SHARD;
            case WAVE -> Items.PRISMARINE_CRYSTALS;
            case EYE -> Items.SPIDER_EYE;
            case TERRARIAN -> Items.ENDER_PEARL;
            case CASCADE -> Items.FIRE_CHARGE;
        });
    }

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    public boolean canChangeDimensions() {return false;}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return NetworkHooks.getEntitySpawningPacket(this);}

    @Override
    public Type confluence$getImmunityType() {return Type.LOCAL;}

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {return 4;}
}
