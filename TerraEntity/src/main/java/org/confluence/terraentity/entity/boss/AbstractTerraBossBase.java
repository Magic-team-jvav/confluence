package org.confluence.terraentity.entity.boss;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.Config;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.gui.CustomizeBossHealthBar;
import org.confluence.terraentity.entity.ai.BossSkill;
import org.confluence.terraentity.entity.ai.CircleBossSkills;
import org.confluence.terraentity.entity.ai.goal.LookForwardWanderFlyGoal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Predicate;

import static org.confluence.terraentity.utils.TEUtils.getMultiple;


@SuppressWarnings("all")
public abstract class AbstractTerraBossBase extends Monster implements GeoEntity {

    public float ironGlomResistance = 0.4f;
    public float explosionResistance = 0.5f;
    public int attackInternal = 20;
    private int _attackInternal = 20;
    protected boolean dirty = true;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
    private final float baseHealth;
    public AbstractTerraBossBase(EntityType<? extends Monster> type, Level level, float health) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.baseHealth = health;
        var a = bossEvent.getOverlay();
        if(level().isClientSide){
            CustomizeBossHealthBar.registerBossHealthBar(getDisplayName().getString(),this.getType());
        }
    }

    public abstract void addSkills();

    public float getAttributeMultiplier(Holder<Attribute> attribute){
        return getMultiple(level(), attribute);
    }

    public void firstSpawn(){};
    @Override
    public void onAddedToLevel(){
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.baseHealth);
        float multiplier = getAttributeMultiplier(Attributes.MAX_HEALTH);
        int size = level().players().size();
        if(!level().isClientSide){

            if(dirty){
                this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(TerraEntity.space("difficulty_modifier_max_health"), multiplier*size - 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(TerraEntity.space("server_modifier_max_health"), Config.boss_attributes_multiplier_health-1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                this.setHealth(this.getMaxHealth());
                firstSpawn();

            }
            this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(TerraEntity.space("difficulty_modifier_attack_damage"), multiplier - 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            this.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier(TerraEntity.space("server_modifier_max_health"), Config.boss_attributes_multiplier_damage-1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

        }

        super.onAddedToLevel();
        this.addSkills();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.ATTACK_KNOCKBACK, 2.2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 100.0);

    }

    // 攻击目标
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = entity -> entity instanceof Player;

    protected void registerGoals() {
        //this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 100F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));

        if(!Config.bossClearWhenNoTarget && !(this instanceof EaterOfWorldSegment))
            this.goalSelector.addGoal(10, new LookForwardWanderFlyGoal(this,0.3f));

    }

    // 技能动画
    public CircleBossSkills skills = new CircleBossSkills(this);

    // 动画数据同步
    private int lastAnimIndex = -1;
    public static final EntityDataAccessor<Integer> DATA_SKILL_INDEX = SynchedEntityData.defineId(AbstractTerraBossBase.class, EntityDataSerializers.INT);
//    public static final EntityDataAccessor<Integer> DATA_SKILL_TICK = SynchedEntityData.defineId(AbstractTerraBossBase.class, EntityDataSerializers.INT);

    protected void setAttactDamage(float damage){
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SKILL_INDEX, 0);
//        builder.define(DATA_SKILL_TICK, 0);
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_SKILL_INDEX) {
            skills.index = this.entityData.get(DATA_SKILL_INDEX);
        }
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> {
            AbstractTerraBossBase entity = (AbstractTerraBossBase) state.getData(DataTickets.ENTITY);
            if (!entity.isAlive()) return PlayState.STOP;
            if (skills.count() == 0) return PlayState.STOP;

            RawAnimation skill = skills.getCurAnim();
            if(skill == null) return PlayState.STOP;
            String name = skill.toString();
            if (skill != null) {
                state.setAnimation(skill);
                if (lastAnimIndex != skills.index) {
                    lastAnimIndex = skills.index;
                    state.resetCurrentAnimation();

                    return PlayState.STOP;
                }
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
    }


    // 技能逻辑

    public void addSkill(BossSkill bossSkill) {
        this.skills.pushSkill(bossSkill);
    }

    public void addSkillNoAnim(BossSkill bossSkill) {
        this.skills.pushSkill(bossSkill);
        //if(anim==null)return;
        //skillMap.put(bossSkill.skill,anim);
    }

    LivingEntity target;
    protected static final int DISCARD_TICK = 100;
    protected int discardTick = 0;
    @Override
    public void tick() {
        super.tick();
        attackInternal--;
        if (!level().isClientSide){
            //没有目标禁止行为
            target = getTarget();
            skills.tick();
            if(target==null){
                discardTick++;
                if(!level().isClientSide && discardTick>DISCARD_TICK && Config.bossClearWhenNoTarget){
                    this.bossEvent.getPlayers().forEach(p->p.sendSystemMessage(this.getDisplayName().copy().append(Component.translatable("message.terraentity.boss_discard"))));
                    this.discard();
                }
                return;
            }
            discardTick = 0;
            collisionHurt();
        }

        this.setDeltaMovement(getDeltaMovement().scale(0.95));//空气阻力
    }


    public void lookAtPos(Vec3 target, float pMaxYRotIncrease, float pMaxXRotIncrease) {
        double d0 = target.x - this.getX();
        double d2 = target.z - this.getZ();
        double d1 = target.y - this.getEyeY();

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
        float f1 = (float)(-(Mth.atan2(d1, d3) * 57.2957763671875));
        this.setXRot(this.rotlerp(this.getXRot(), f1, pMaxXRotIncrease));
        this.setYRot(this.rotlerp(this.getYRot(), f, pMaxYRotIncrease));
    }

    private float rotlerp(float pAngle, float pTargetAngle, float pMaxIncrease) {
        float f = Mth.wrapDegrees(pTargetAngle - pAngle);
        if (f > pMaxIncrease) {
            f = pMaxIncrease;
        }

        if (f < -pMaxIncrease) {
            f = -pMaxIncrease;
        }

        return pAngle + f;
    }

    // 开启碰撞伤害
    public boolean canCollisionHurt() {
        return true;
    }

    public void collisionHurt() {
        if (canCollisionHurt() && !level().isClientSide) {
            // 包围盒检测造成伤害
            var entities = level().getEntities(this, this.getBoundingBox(), e->e instanceof LivingEntity living&& e!= this );
            if (!entities.isEmpty()) {
                for (var e : entities) {
                    if ( e instanceof LivingEntity living&& canAttack(living)){
                        attackInternal = _attackInternal;
                        //测试末影龙
                        e.hurt(this.damageSources().generic(),(float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        //e.hurt(this.damageSources().explosion(new Explosion(level(), this, 0.0f, 0,0,3, List.of(this.blockPosition()))), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                    }
                }
            }
       }
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.getEntity() instanceof IronGolem){
            pAmount *= ironGlomResistance;
        }
        if(pSource.is(DamageTypes.EXPLOSION)){
            pAmount *= explosionResistance;
        }

        return super.hurt(pSource,pAmount);
    }

    public boolean canAttack(LivingEntity entity) {
        return super.canAttack(entity)&&
                (
                        entity instanceof Player ||
                                        entity != this
                                        &&!(entity instanceof AbstractTerraBossBase)
                                        && entity instanceof LivingEntity living && living.canBeSeenAsEnemy()
                );
    }

    // boss条
    public boolean shouldShowBossBar() {
        return true;
    }

    @Override // boss条显示
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (shouldShowBossBar()){
            this.bossEvent.addPlayer(player);
        }
    }

    @Override // boss条消失
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (shouldShowBossBar())
            this.bossEvent.removePlayer(player);
    }

    @Override // boss条更新
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (shouldShowBossBar())
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override // 从客户端移除时
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
    }

    @Override // 取消墙体窒息伤害
    public boolean isInWall() {
        return false;
    }

    @Override // 是否免疫摔伤
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    @Override // 是否在实体上渲染着火效果
    public boolean displayFireAnimation() {
        return false;
    }

    @Override // 受伤音效
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("dirty", false);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (hasCustomName()) {
            bossEvent.setName(getDisplayName());
        }
        if (tag.contains("dirty")) {
            dirty = false;
        }
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        bossEvent.setName(getDisplayName());
    }
}