package org.confluence.terraentity.entity.proj;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.*;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshPart;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;


public abstract class BaseProj<T extends BaseProj<T>> extends Projectile implements ICollisionAttackEntity, IAttackableProjectile {
    public float damage = 1;
    private final Set<UUID> hitList = new HashSet<>();
    public int penetration =1;
    protected List<MobEffectInstance> effects;
    protected IEffectStrategy effectStrategy;
    public ResourceLocation texture = TerraEntity.space("textures/entity/projectile/default.png");
    protected DeferredHolder<SoundEvent,SoundEvent> hitSound;
    public Consumer<BaseProj> clientTickCallback;
    public ITrackType trackType;
    public IGeneration generation;
    CollisionProperties collisionProperties = new CollisionProperties(1,1,0.5f);
    protected double accelerationPower = 0.1;
    protected float power = 0.4f;
    protected boolean canBeAttacked = false;



    protected boolean canPenetrateBlock = false;

    public CollisionProperties getCollisionProperties(){
        return collisionProperties;
    }


    public boolean shouldDoCollision(){
        return true;
    }

    protected Vec3 initSpeed = Vec3.ZERO;

    protected static final EntityDataAccessor<Vector3f> DATA_INIT_SPEED = SynchedEntityData.defineId(BaseProj.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(BaseProj.class, EntityDataSerializers.FLOAT);

    public BaseProj(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        this(pEntityType, pLevel, Lists.newArrayList());
    }

    public BaseProj(EntityType<? extends Projectile> pEntityType, Level pLevel,@Nullable MobEffectInstance pEffect) {
        this(pEntityType, pLevel, pEffect == null ? Lists.newArrayList() : Lists.newArrayList(pEffect));
    }

    public BaseProj(EntityType<? extends Projectile> pEntityType, Level pLevel, List<MobEffectInstance> pEffects) {
        super(pEntityType, pLevel);
        this.effects = pEffects;
    }

    public T setHitSound(DeferredHolder<SoundEvent,SoundEvent> hitSound){
        this.hitSound = hitSound;
        return (T) this;
    }
    public T setEffect(List<MobEffectInstance> effects) {
        this.effects = effects;
        return (T) this;
    }
    public T addEffect(MobEffectInstance effect) {
        if (effect != null) {
            this.effects.add(effect);
        }
        return (T) this;
    }
    public float getDamage() {return damage;}
    public void addDamage(float damage) {this.damage += damage;}
    public T setDamage(float damage) {
        this.damage = damage;
        return (T) this;
    }
    public T setPenetrate(int penetration){
        this.penetration = penetration;
        return (T) this;
    }
    public T setClientTickCallback(Consumer<BaseProj> clientTickCallback){
        this.clientTickCallback = clientTickCallback;
        return (T) this;
    }
    public T setTexture(ResourceLocation texture){
        this.texture = texture;
        return (T) this;
    }
    public T setEffectStrategy(IEffectStrategy effectStrategy){
        this.effectStrategy = effectStrategy;
        return (T) this;
    }
    public T setCanBeHurt(){
        this.canBeAttacked = true;
        return (T) this;
    }
    public T setCanPenetrateBlock(boolean canPenetrateBlock) {
        this.canPenetrateBlock = canPenetrateBlock;
        return (T) this;
    }

    /**
     * 简单弹幕的贴图
     */
    public ResourceLocation getTexture(){return texture;}

    /**
     * 生存时间
     */
    public abstract int getLifetime();

    /**
     * 无限生存时间
     */
    public boolean isInfinite(){
        return false;
    }
    public boolean shouldBeSaved(){
        return false;
    }

    protected void doKnockBack(LivingEntity entity) {
        double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 vec3;
        entity.setDeltaMovement(getDeltaMovement().scale(0.3f));
        if(getOwner() != null) {
            vec3 = entity.position().subtract(getOwner().position()).multiply(1.0, 0.0, 1.0).normalize().scale((((LivingEntity) getOwner()).getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) + 0.1f)  * power * d1);
        }else{
            vec3 = getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(power * d1);
        }
        if (vec3.lengthSqr() > 0.0) {
            entity.push(vec3.x, 0.3, vec3.z);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_INIT_SPEED, new Vector3f(0, 0, 0));
        builder.define(DATA_SCALE, 1.0f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data){
        super.onSyncedDataUpdated(data);
        if(level().isClientSide) {
            if (data == DATA_INIT_SPEED) {
                this.initSpeed = new Vec3(this.entityData.get(DATA_INIT_SPEED));
                this.setDeltaMovement(initSpeed);
            }else if(data == DATA_SCALE){
                this.refreshDimensions();
            }
        }
    }

    public void setScale(float scale){
        this.entityData.set(DATA_SCALE, scale);
    }

    public float getScale(){
        return this.entityData.get(DATA_SCALE);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return super.getDimensions(pose).scale(this.entityData.get(DATA_SCALE));
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        this.entityData.set(DATA_INIT_SPEED, getDeltaMovement().toVector3f());
        this.initSpeed = getDeltaMovement();
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            super.tick();

            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, ClipContext.Block.COLLIDER);
            if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockhitresult = (BlockHitResult) hitresult;
                    this.onHitBlock(blockhitresult);
                    BlockPos blockpos = blockhitresult.getBlockPos();
                    this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level().getBlockState(blockpos)));
                }
            }

            this.checkInsideBlocks();

            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            this.setDeltaMovement(vec3.add(vec3.normalize().scale(this.accelerationPower)));
            this.setPos(d0, d1, d2);
        } else {
            this.discard();
        }


        if(!level().isClientSide){
            this.doCollisionAttack(this::canHitEntity, this::doHurt);

            if (!this.isInfinite() && tickCount > getLifetime()) {
                discard();
                return;
            }
//            if(isInWall()){
//                discard();
//            }
        }else if(clientTickCallback!= null){
            clientTickCallback.accept(this);
        }

    }



    //弹幕设置
    @Override//取消射击惯性
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * (float) (Math.PI / 180.0)) * Mth.cos(pX * (float) (Math.PI / 180.0));
        float f1 = -Mth.sin((pX + pZ) * (float) (Math.PI / 180.0));
        float f2 = Mth.cos(pY * (float) (Math.PI / 180.0)) * Mth.cos(pX * (float) (Math.PI / 180.0));
        this.shoot(f, f1, f2, pVelocity, pInaccuracy);
    }
    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        if(!level().isClientSide()){
//            if(getOwner()==null){
////                discard();
//                return;
//            }
            this.damage += defaultDamage();
        }
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity hurter = pResult.getEntity();
        if(hurter instanceof LivingEntity living && canHitEntity(living)) {
            doHurt(living);
        }else if(hurter instanceof PartEntity<?> part && part.getParent() instanceof LivingEntity living && canHitEntity(living)){
            doHurt(living);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(this.canBeAttacked() && !this.level().isClientSide()){
            this.kill();
        }
        return super.hurt(source, amount);
    }

    public float defaultDamage(){
//        if(getOwner() != null)
//            return (int) ((LivingEntity)getOwner()).getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        return 0;
    }

    protected void doHurt(Entity hurter) {
        if (hurter instanceof LivingEntity living) {
            hitList.add(living.getUUID());
            for (MobEffectInstance effect : effects) {
                living.addEffect(new MobEffectInstance(effect)); // 需要复制，不然duration会减为0
            }
            if (effectStrategy != null) {
                if (this.getOwner() != null && this.getOwner() instanceof LivingEntity living1) {
                    this.effectStrategy.getEffect().accept(living1, living);
                }
            }
        }
        if (hitSound != null)
            level().playSound(this, this.blockPosition(), hitSound.get(), SoundSource.AMBIENT, 1.0f, 1.0f);
        Entity actualHurter = hurter instanceof PartEntity<?> part ? part.getParent() : hurter;
        if (actualHurter instanceof LivingEntity living && hurter.hurt(getDamageSource(living), damage)) {
            if (this.getOwner() instanceof LivingEntity owner) {
                owner.setLastHurtMob(hurter);
            }
            if(!(hurter instanceof PartEntity<?>))doKnockBack(living);
        }

        if (this.level() instanceof ServerLevel serverlevel) {
            penetration--;
            if (penetration <= 0) {
                discard();
            }
        }
    }

    public DamageSource getDamageSource(LivingEntity hurter){
        if(getOwner() instanceof ISummonMob mob) {
            return TETags.DamageTypes.of(level(), TETags.DamageTypes.SUMMONER, mob.summon_getOwner());
        }
        if(getOwner() != null && getOwner() instanceof LivingEntity living){
            return damageSources().mobProjectile(this, living);
        }
        return this.damageSources().generic();
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        // 如果目标是 PartEntity，使用父实体进行检查
        Entity actualTarget = target instanceof PartEntity<?> part ? part.getParent() : target;
        if(actualTarget == null) return false;

        // 不能攻击自己和不能被弹幕攻击的实体
        if(actualTarget == getOwner() || !actualTarget.isAttackable()){
            return false;
        }
        // 不能攻击已经被弹幕攻击过的实体（使用父实体的 UUID）
        if(hitList.contains(actualTarget.getUUID()))
            return false;
        // 召唤物不能攻击主人的仆从
        if(!TEUtils.attackTamableTest.test(getOwner(), target)){
            return false;
        }
        // 有主人的弹幕只能攻击主人可以攻击的目标
        if(getOwner()!=null && getOwner() instanceof LivingEntity living && actualTarget instanceof LivingEntity tar) {
            return living.canAttack(tar);
        }
        return false;
    }



    @Override
    public boolean isPickable() {
        return false;
    }


    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(!this.level().isClientSide() && !this.canPenetrateBlock) {
            this.discard();
        }
    }

    public boolean canBeAttacked(){
        return canBeAttacked;
    }

}
