package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.entity.ICollisionAttackEntity;
import org.confluence.mod.api.entity.IStateChangeableMob;
import org.confluence.mod.api.entity.ai.IFSMGeoMob;
import org.confluence.mod.client.gui.CustomizeBossHealthBar;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.entity.ai.fsm.CircleMobSkills;
import org.confluence.mod.util.entity.ai.goal.LookForwardWanderFlyGoal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * BOSS基类
 *
 * @param <T> Boss类型
 */
@SuppressWarnings("all")
public abstract class AbstractTerraBossBase extends Monster implements GeoEntity, IFSMGeoMob, ICollisionAttackEntity, IStateChangeableMob {
    /* 属性 */
    public float ironGlomResistance = 0.4f;
    public float explosionResistance = 0.5f;

    protected boolean dirty = true;
    protected ServerBossEvent bossEvent;

    //    public int stage = 1; //阶段
    private boolean consumeStageChange = false;

    public AbstractTerraBossBase(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = this.createMoveControl();
        setNoGravity(true);
//        this.baseHealth = health;
//        this.baseArmor = armor;
        if (level().isClientSide) {
            CustomizeBossHealthBar.registerBossHealthBar(getDisplayName().getString(), this.getType());
        }

        this.addSkills();


        bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(), getBossBarColor(), BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true).setPlayBossMusic(true);
    }

    protected MoveControl createMoveControl() {
        return new FlyingMoveControl(this, 10, false);
    }

    /**
     * 再次进入游戏需要同步BOSS阶段
     *
     * @param stage
     */
    protected void initStage(int stage) {}

    public float getAttributeMultiplier(Holder<Attribute> attribute) {
        return getMultiple(level(), blockPosition(), attribute);
    }

    /**
     * 因为finalizeSpawn中生成时，id可能会错乱，所以必须推迟在onAddedToLevel中调用
     */
    public void firstSpawn() {}

    public void aganinSpawn() {}

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {}

    @Override
    public void onAddedToWorld() {
        if (!level().isClientSide) {
            if (dirty) {
                firstSpawn();
            } else {
                aganinSpawn();
            }
            if (bossEvent != null) {
                bossEvent.getPlayers().forEach(p -> syncBossHealthBar(p));

            }

        }
        super.onAddedToWorld();
        if (skills.count() > 0) {
            skills.forceStartIndex(0);
        }
        if (!this.level().isClientSide) {
            this.initStage(this.getStage());
        }

    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @javax.annotation.Nullable SpawnGroupData spawnGroupData) {
        spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
//        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.baseHealth);
//        this.getAttribute(Attributes.ARMOR).setBaseValue(baseArmor);
        this.setHealth(this.getMaxHealth());
        return spawnGroupData;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 1)
                .add(LibAttributes.getAttackDamage(), 1)
                .add(Attributes.ATTACK_KNOCKBACK, 2.2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 300.0)
                .add(Attributes.FLYING_SPEED);
    }

    // 尽量不要使用这个方法，应该使用modifier且使用最好使用乘法，以适配其他模组的属性
    protected void setAttactDamage(float damage) {
        this.getAttribute(LibAttributes.getAttackDamage()).setBaseValue(damage);
    }

    /* 攻击目标 */
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = entity -> entity instanceof Player;

    protected void registerGoals() {
        //this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 100F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));

        this.registerRandomStrollGoal();
    }

    protected void registerRandomStrollGoal() {
        if (ServerConfig.BOSS_KEEP_WANDERING.get()) {
            this.goalSelector.addGoal(10, new LookForwardWanderFlyGoal(this, 0.3f, 0));
        } else {
            this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 10, 1f));
        }
    }

    /* FSM */
    public CircleMobSkills skills = new CircleMobSkills(this, DATA_SKILL_INDEX);
    public static final EntityDataAccessor<Integer> DATA_STATUS_STATUS = SynchedEntityData.defineId(AbstractTerraBossBase.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILL_INDEX = SynchedEntityData.defineId(AbstractTerraBossBase.class, EntityDataSerializers.INT);
    protected ClientBoundAnimationMessage skillMessage = new ClientBoundAnimationMessage();
    protected int lastSkillTick;

    @Override
    public CircleMobSkills getSkills() {
        return skills;
    }

    @Override
    public ClientBoundAnimationMessage getAnimationMessage() {
        return skillMessage;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SKILL_INDEX, 0);
        entityData.define(DATA_STATUS_STATUS, 1);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        syncSkills(DATA_SKILL_INDEX);
    }

    public int getSkillIndex() {
        return this.entityData.get(DATA_SKILL_INDEX);
    }

    public EntityDataAccessor<Integer> getDataStateStatus() {
        return DATA_STATUS_STATUS;
    }

    /* Collision */
    protected CollisionProperties collisionProperties = new CollisionProperties(5, 20, 0);

    @Override
    public CollisionProperties getCollisionProperties() {
        return collisionProperties;
    }

    @Override
    public boolean shouldDoCollision() {
        return getTarget() != null && this.isAlive();
    }

    /* discard */
    LivingEntity target;
    protected static final int DISCARD_TICK = 100;
    protected int discardTick = 0;
    protected boolean isCreativePlayer; // 如果附近有创造模式玩家，则不清除

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            target = getTarget();
            if (this.isAlive()) {
                skills.tick();
            }
            //没有目标禁止行为


            if (target == null || !target.isAlive() || !target.canBeSeenAsEnemy()) {
                var entity = findTarget();
                setTarget(entity);
                if (entity != null) {
                    return;
                }

                if (!isCreativePlayer) {
                    discardTick++;
                    if (!level().isClientSide && discardTick > DISCARD_TICK && ServerConfig.BOSS_CLEAR_WHEN_NO_TARGET.get() && shouldEscape()) {
                        this.bossEvent.getPlayers().forEach(p -> p.sendSystemMessage(this.getDisplayName().copy().append(Component.translatable("message.confluence.boss_discard", getDisplayName()))));
                        this.discard();
                    }
                    return;
                }
            }
            this.refreshDimensions();
            discardTick = 0;

            doCollisionAttack(
                    e -> e instanceof LivingEntity living && canAttack(living) && e != this && living.canBeSeenAsEnemy(),
                    this::doHurtTarget
            );
            if (shouldOverPlayer() && target != null && position().y < target.getY()) {
                addDeltaMovement(new Vec3(0, 0.02f, 0));
            }
        } else {
            this.skills.tick += 1;
        }

        if (!shouldDiscardFriction()) {
            this.setDeltaMovement(getDeltaMovement().scale(0.95));//空气阻力
        }
    }

    /**
     * 如果为true，则y坐标低于玩家时向上加速
     *
     * @return
     */
    protected boolean shouldOverPlayer() {
        return false;
    }

    /**
     * 找索敌范围内仇恨最大的，如果多个一样的从中随机选一个
     */
    protected LivingEntity findTarget() {
        double range = getAttributeValue(Attributes.FOLLOW_RANGE);
        List<Player> players = getNearbyPlayers(range);
        List<Player> maxAggroPlayers = players.stream()
                .collect(Collectors.groupingBy(player -> player.getAttribute(ConfluenceMagicLib.AGGRO.get()).getValue(), Collectors.toList()))
                .entrySet().stream().max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(List.of());
        if (!maxAggroPlayers.isEmpty()) {
            return maxAggroPlayers.get(level().random.nextInt(maxAggroPlayers.size()));
        }
        return null;
    }

    protected List<Player> getNearbyPlayers(double range) {
        List<Player> players = new ArrayList<>();
        isCreativePlayer = false;
        for (Player player : level().players()) {
            if (player.canBeSeenAsEnemy() && this.distanceToSqr(player) < range * range) {
                players.add(player);
            }
            if (!isCreativePlayer && !player.canBeSeenAsEnemy()) {
                isCreativePlayer = true;
            }
            this.noActionTime = 0; // 防止某些站桩boss被刷新
        }
        return players;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        return super.doHurtTarget(entity);
    }

    // 可以给巨鹿用
    public boolean shouldEscape() {
        return true;
    }

    /* func */
    public void lookAtPos(Vec3 target, float pMaxYRotIncrease, float pMaxXRotIncrease) {
        double d0 = target.x - this.getX();
        double d2 = target.z - this.getZ();
        double d1 = target.y - this.getEyeY();

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = (float) (Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
        float f1 = (float) (-(Mth.atan2(d1, d3) * 57.2957763671875));
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

    public void lookAt(float maxAngleY) {
        var pEntity = getTarget();
        if (pEntity != null) {
            lookAt(getTarget(), maxAngleY, 85);
            this.lookControl.setLookAt(getTarget());
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() instanceof IronGolem) {
            pAmount *= ironGlomResistance;
        }
        if (pSource.is(DamageTypes.EXPLOSION)) {
            pAmount *= explosionResistance;
        }

        boolean flag = super.hurt(pSource, pAmount);
        this.changeState();
        return flag;

    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && target.isPickable() && (
                target instanceof Player ||
                        target != this
//                                        &&!(target instanceof AbstractTerraBossBase)
                                && target.canBeSeenAsEnemy()
        );
    }

    public float getHealthPercentage() {
        return this.getHealth() / this.getMaxHealth();
    }

    public float getMoveSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    /* boss条 */
    public boolean shouldShowBossBar() {
        return true;
    }

    @Override // boss条显示
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (shouldShowBossBar()) {
            this.bossEvent.addPlayer(player);
            if (tickCount != 0)
                syncBossHealthBar(player);
        }
    }

    public void syncBossHealthBar(ServerPlayer player) {
        float[] datas = getBossEventProgress();
        ((IBossEvent) this.bossEvent).terra_enity$setBossHealth(datas[0]);
        ((IBossEvent) this.bossEvent).terra_enity$setBossMaxHealth(datas[1]);
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new SyncBossEventHealthPacket(bossEvent.getId(), datas[0], datas[1]));
    }

    @Override // boss条消失
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (shouldShowBossBar())
            this.bossEvent.removePlayer(player);
    }

    /**
     * 获取boss血量和最大血量
     *
     * @return [血量, 最大血量]
     */
    public float[] getBossEventProgress() {
        return new float[]{this.getHealth(), this.getMaxHealth()};
    }

    @Override // boss条更新
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (shouldShowBossBar()) {
            float[] datas = getBossEventProgress();
            ((IBossEvent) this.bossEvent).terra_enity$setBossHealth(datas[0]);
            ((IBossEvent) this.bossEvent).terra_enity$setBossMaxHealth(datas[1]);
            float res = datas[1] == 0 ? 1 : datas[0] / datas[1];
            this.bossEvent.setProgress(res);
        }
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
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("dirty", false);
        if (getStage() > 0) {
            compound.putInt("Stage", getStage());
        }
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
        if (tag.contains("Stage")) {
            this.setStage(tag.getInt("Stage"));
        }
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        bossEvent.setName(getDisplayName());
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return distanceToSqr(entity) < 100 * 100;
    }

    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    public void lavaHurt() {
        if (!this.fireImmune()) {
            float v = LibUtils.switchByDifficulty(level(), blockPosition(), 0.25F, 0.15F, 0.05F);
            this.igniteForSeconds(15.0F * v);
            if (this.hurt(this.damageSources().lava(), 4.0F * v)) {
                this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }
        }
    }

    @Override
    public void changeState() {}

    protected boolean isExpert() {
        return LibUtils.isAtLeastExpert(level(), blockPosition());
    }

    protected boolean isMaster() {
        return LibUtils.isMaster(level(), blockPosition());
    }

    protected boolean isFtw() {
        return ModSecretSeeds.FOR_THE_WORTHY.match();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypes.LAVA)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    protected int calShareFlag(int data, int index, boolean value) {
        return value ? (data | (1 << index)) : (data & ~(1 << index));
    }

    @Override
    public int getMaxHeadXRot() {
        return 85;
    }
}
