package org.confluence.terraentity.entity.proj;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.EventHooks;
import org.confluence.terraentity.api.entity.IAttackableProjectile;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.config.ClientConfig;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.data.component.SingleBooleanComponent;
import org.confluence.terraentity.entity.util.trail.BoomerangTrail;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.item.Boomerang;
import org.confluence.terraentity.item.Boomerang.BoomerangModifier;
import org.confluence.terraentity.utils.TEUtils;

import java.util.LinkedList;
import java.util.Queue;

public class BoomerangProjectile extends Projectile {

    public ItemStack weapon = ItemStack.EMPTY;
    private BoomerangModifier modifier;
    public int randomRotation;//随机初始旋转角度
    private float backSpeed;//碰撞返回的速度
    public boolean isBacking;//正在返回
    private int backTime;//返回时间
    private int penetrationCount;
    public BoomerangTrail trail;

    public Queue<Vec3> trailQueue;
    public Queue<Vec3> trailQueue2;

    public BoomerangProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.modifier = new BoomerangModifier();
        this.randomRotation = this.random.nextInt(114514);
        //BoomerangItems.DEVELOPER_BOOMERANG.get().boomerangModifier
        trailQueue = new LinkedList<>();
        trailQueue2 = new LinkedList<>();

    }

    public BoomerangProjectile(LivingEntity owner, BoomerangModifier modifier, ItemStack weapon) {
        this(TEProjectileEntities.BOOMERANG_PROJECTILE.get(), owner.level());
        this.setOwner(owner);
        this.modifier = modifier;
        this.weapon = weapon;
        this.entityData.set(DATA_WEAPON, weapon.copy());
        this.penetrationCount = modifier.maxPenetration;

    }

    //同步客户端数据
    public static final EntityDataAccessor<ItemStack> DATA_WEAPON = SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> DATA_BACKING = SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DATA_BACKING_TIME = SynchedEntityData.defineId(BoomerangProjectile.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_WEAPON, ItemStack.EMPTY);
        builder.define(DATA_BACKING, false);
        builder.define(DATA_BACKING_TIME, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
        if (var1 == DATA_BACKING_TIME) {
            this.backTime = this.entityData.get(DATA_BACKING_TIME);
        } else if (var1 == DATA_BACKING) {
            this.isBacking = this.entityData.get(DATA_BACKING);
        } else if (var1 == DATA_WEAPON) {
            weapon = this.entityData.get(DATA_WEAPON);
            modifier = ((Boomerang) weapon.getItem()).boomerangModifier;
            if (this.modifier.trail != null) {
                trail = this.modifier.trail.get();
            }
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) return;
        Entity hurter = result.getEntity();
        Entity actualHurter = hurter;
        if (hurter instanceof PartEntity<?> part) {
            hurter = part.getParent();
        }
        if (this.getOwner() instanceof LivingEntity owner && this.getOwner() != actualHurter) {
            DamageSource source = this.damageSources().mobAttack(owner); // 回旋镖是近战伤害
            if (hurter instanceof LivingEntity living && actualHurter.isAlive() && TEUtils.projectileCanHurtEntityTest.test(this, living)) {
                penetrationCount--;
                float damage = (float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE) + modifier.damage - 1;
                if (actualHurter.hurt(source, damage)) {
                    EffectStrategyComponent data = weapon.get(TEDataComponentTypes.EFFECT_STRATEGY);
                    if (data != null) {
                        data.applyAll((LivingEntity) this.getOwner(), living);
                    }
                    owner.setLastHurtMob(actualHurter);
                    //击退
                    doKnockback(living);
                }
            }

            IAttackableProjectile.tryHit(hurter, source);

            if (!modifier.canPenetrate && penetrationCount <= 0 && modifier.forwardTick - tickCount > 10) {
                if (!isBacking) {
                    backTime = this.tickCount;
                    this.entityData.set(DATA_BACKING_TIME, backTime);
                    this.entityData.set(DATA_BACKING, true);
                    backSpeed = (float) this.getDeltaMovement().length();
                }
                isBacking = true;
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return TEUtils.projectileCanHitEntityTest.test(this, target);
    }

    protected void doKnockback(LivingEntity entity) {

        double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(modifier.knockback * d1);
        if (vec3.lengthSqr() > 0.0) {
            entity.push(vec3.x, 0.1, vec3.z);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!isBacking) {
            this.playSound(SoundEvents.WOOD_PLACE, 0.5f, 1.5f);
        }
        isBacking = true;

        this.noPhysics = true;
        entityData.set(DATA_BACKING, true);

        super.onHitBlock(result);
        if (level().isClientSide) {
            BlockPos blockpos = result.getBlockPos();
            BlockState blockstate = this.level().getBlockState(blockpos);
            Vec3 dir = this.getDeltaMovement().normalize().scale(2);
            Vec3 mid = new Vec3(blockpos.getX() + 0.5f, blockpos.getY() + 0.5f, blockpos.getZ() + 0.5f).add(Vec3.atLowerCornerOf(result.getDirection().getNormal()));
            this.level().addParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(blockpos), mid.x, mid.y, mid.z, -dir.x, -dir.y, -dir.z);
            this.level().addParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockstate)).setPos(blockpos), mid.x, mid.y, mid.z, -dir.x, -dir.y, -dir.z);
        }
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            super.tick();

            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, ClipContext.Block.COLLIDER);
            if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
                this.hitTargetOrDeflectSelf(hitresult);
            }

            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float f;
            if (!this.isInWater()) {
                f = 0.95F;
            } else {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;
                    this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25, d1 - vec3.y * 0.25, d2 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
                }
                f = 0.8F;
            }
            this.setDeltaMovement(vec3.add(vec3.normalize().scale(0.1)).scale(f));
            this.setPos(d0, d1, d2);
        } else {
            this.discard();
        }


        if (level().isClientSide) {
            if (trail != null) {
                trail.generateTrail(this, tickCount);
            }
        }

        if (this.getOwner() != null && this.getOwner() instanceof LivingEntity living) {
            if (!isBacking) {
                int delta = 10;
                double actualSpeed = Math.min(
                        Mth.lerp((float) (modifier.forwardTick - tickCount) / delta, 0.01F, modifier.flySpeed),
                        modifier.flySpeed
                );
//                this.setDeltaMovement(getDeltaMovement().normalize().scale(actualSpeed));
                Vec3 dir = getDeltaMovement().normalize();
                Vec3 motion = dir.scale(actualSpeed);
                this.setDeltaMovement(motion);

                if (this.modifier.forwardTick <= this.tickCount) {
                    isBacking = true;
                    backTime = this.tickCount;
                    this.noPhysics = true;
                    entityData.set(DATA_BACKING, true);
                }
            } else {
                Vec3 distinct = living.position().add(0, 1F, 0);
                Vec3 dir = distinct.subtract(this.position()).normalize();
                int delta = 10;
                double actualSpeed = Math.min(Mth.lerp((float) (tickCount - backTime) / delta, backSpeed + 0.01F, modifier.backSpeed), modifier.backSpeed);
                Vec3 motion = dir.scale(actualSpeed);
                this.setDeltaMovement(motion);
//                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.distanceToSqr(distinct) < modifier.backSpeed * 0.8F) {
                    discard();
                }
            }
        }
        if (level().isClientSide) {
            if (ClientConfig.GENERATE_PROJECTILE_PARTICLE.get() && modifier.particle != null) {
                ParticleOptions particle = modifier.particle.apply(this);
                for (int i = 0; i < modifier.particleCount; i++) {
                    level().addParticle(particle, this.getX() + random.nextFloat() - 0.5f, this.getY() + random.nextFloat() - 0.5f, this.getZ() + random.nextFloat() - 0.5f, 0, 0, 0);
                }
            }
        }

    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        float f1 = -Mth.sin((x + z) * 0.017453292F);
        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        this.shoot(f, f1, f2, velocity, inaccuracy);
        this.setDeltaMovement(this.getDeltaMovement());
    }


    @Override
    public boolean isOnFire() {
        return modifier.fire && (this.level().isClientSide && this.getSharedFlag(0));
    }

    @Override
    public void onRemovedFromLevel() {
        if (!level().isClientSide && !weapon.isEmpty() && getOwner() != null) {
            Boomerang.setBacked(weapon, SingleBooleanComponent.TRUE);

            WeaponStorage.of(getOwner()).tryReduce(weapon.getItem());
            //  提前部署
            if (getOwner() instanceof Player player
//                    && (modifier.shouldWaitForBack && !modifier.shouldApplyCd || modifier.maxCount - 1 == count)
            ) {

                player.getCooldowns().removeCooldown(weapon.getItem());

                if (WeaponStorage.of(player).leftClicking && weapon.is(player.getWeaponItem().getItem())) {
                    Boomerang boomerang = (Boomerang) weapon.getItem();
                    boomerang.onLeftClick(player, weapon);
                }
            }

        }
        super.onRemovedFromLevel();
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    public BoomerangModifier getModifier() {
        return modifier;
    }
}
