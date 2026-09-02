package org.confluence.mod.common.entity.monster.slime;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.SpawnPlacementChecks;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.entity.boss.BossOwnerTracker;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.MonsterEntities;

public class BaseSlime extends BaseMonster implements BossOwnedEntity {
    protected static final String SIZE_KEY = "SlimeSize";
    private static final int HONEY_SOAK_CHECKS_REQUIRED = 120;
    private static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(BaseSlime.class, EntityDataSerializers.INT);
    protected final int slimeColor;
    protected final boolean passiveByDay;
    private float oldSquish;
    private float squish;
    private float targetSquish;
    private boolean wasOnGround;
    private int honeySoakTime;
    private final BossOwnerTracker<BaseBoss> bossOwnerTracker = new BossOwnerTracker<>(BaseBoss.class);

    public BaseSlime(EntityType<? extends BaseSlime> type, Level level) {
        this(type, level, 0x48E920, false, 2);
    }

    protected BaseSlime(EntityType<? extends BaseSlime> type, Level level, int slimeColor) {
        this(type, level, slimeColor, false, 2);
    }

    protected BaseSlime(EntityType<? extends BaseSlime> type, Level level,
                        int slimeColor, boolean passiveByDay) {
        this(type, level, slimeColor, passiveByDay, 2);
    }

    public BaseSlime(EntityType<? extends BaseSlime> type, Level level, int slimeColor, boolean passiveByDay, int size) {
        super(type, level);
        this.slimeColor = slimeColor;
        this.passiveByDay = passiveByDay;
        this.moveControl = new SlimeMoveControl(this);
        setSlimeSize(size);
    }

    /// 保留 1.21 侧原版史莱姆的索敌规则。
    ///
    /// 白天被动的颜色变体不会主动寻找玩家，但受击反击仍由
    /// {@link HurtByTargetGoal} 独立处理；玩家与史莱姆的高度差也必须不超过四格。
    /// 铁傀儡使用较低优先级，避免同时存在玩家时改变原有目标选择。
    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.removeAllGoals(goal -> true);
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::canProactivelyTargetPlayer));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    /// 判断玩家是否满足史莱姆的主动索敌条件；受击反击不经过此方法。
    protected boolean canProactivelyTargetPlayer(LivingEntity player) {
        return Math.abs(player.getY() - getY()) <= 4.0
                && (!passiveByDay || level().isNight());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SIZE, 2);
    }

    /// 返回服务端权威并同步到客户端的史莱姆大小。
    ///
    /// 该值同时驱动碰撞箱和模型尺寸；特殊变体只需修改这一处，不能再分别维护渲染缩放和逻辑体积。
    public int getSlimeSize() {
        return entityData.get(DATA_SIZE);
    }

    public void setBossOwner(BaseBoss owner) {
        bossOwnerTracker.bind(this, owner);
        setPersistenceRequired();
        BossMinionCoordinator.faceTargetImmediately(this, getTarget());
    }

    @Override
    public BaseBoss getBossOwner() {
        return bossOwnerTracker.resolve(this);
    }

    public java.util.UUID getBossOwnerUUID() {
        return bossOwnerTracker.getOwnerUUID();
    }

    protected void setSlimeSize(int size) {
        int clampedSize = Mth.clamp(size, 1, 127);
        entityData.set(DATA_SIZE, clampedSize);
        refreshDimensions();
        var movementSpeed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(0.2F + 0.1F * clampedSize);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_SIZE.equals(key)) {
            refreshDimensions();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float logicalSize = 0.52F * getSlimeSize();
        return EntityDimensions.scalable(logicalSize, logicalSize).scale(getVisualScale() * getScale());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(SIZE_KEY, getSlimeSize());
        bossOwnerTracker.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(SIZE_KEY, net.minecraft.nbt.Tag.TAG_INT)) {
            setSlimeSize(tag.getInt(SIZE_KEY));
        }
        bossOwnerTracker.load(tag);
    }

    /// 按史莱姆类型执行 1.21 侧现有的自然生成分层规则。
    ///
    /// 生物群系数据只决定某种史莱姆能否进入候选列表；亮度、高度、昼夜和露天条件仍在
    /// 此处统一判定。未列入任何分支的类型保持不可自然生成，包括虽然注册了放置规则、但
    /// 1.21 当前没有为其提供有效环境分支的青团史莱姆。
    public static boolean checkSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof Level world) || !SpawnPlacementChecks.checkMonsterSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }

        int y = pos.getY();
        if (type == MonsterEntities.YELLOW_SLIME.get() || type == MonsterEntities.RED_SLIME.get() || type == MonsterEntities.DESERT_SLIME.get()) {
            return level.getBrightness(LightLayer.SKY, pos) == 0 && y >= 0 && y < 40;
        }
        if (type == MonsterEntities.BLACK_SLIME.get() || type == MonsterEntities.DUNGEON_SLIME.get()) {
            return level.getBrightness(LightLayer.SKY, pos) == 0 && y <= 40;
        }
        if (type == MonsterEntities.LAVA_SLIME.get()) {
            return y >= 30 && y < 100;
        }
        if (type == MonsterEntities.BLUE_SLIME.get()
                || type == MonsterEntities.GREEN_SLIME.get()
                || type == MonsterEntities.PURPLE_SLIME.get()
                || type == MonsterEntities.ICE_SLIME.get()
                || type == MonsterEntities.JUNGLE_SLIME.get()
                || type == MonsterEntities.PINK_SLIME.get()
                || type == MonsterEntities.SWAMP_SLIME.get()
                || type == MonsterEntities.TROPIC_SLIME.get()) {
            return y >= 40 && y < 260 && world.isDay() && level.canSeeSky(pos);
        }
        return false;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new SlimeLocomotionAction(BaseSlime.this);
            }
        };
    }

    /// 返回下一次落地起跳前的等待时间。
    ///
    /// 普通史莱姆沿用原版的十至二十九刻随机间隔；进入攻击状态后，移动控制器会把
    /// 该间隔缩短为三分之一。仅金史莱姆等在 1.21 侧明确覆盖此值的变体需要重写。
    protected int getJumpDelay() {
        return random.nextInt(20) + 10;
    }

    @Override
    public void tick() {
        LivingEntity inheritedTarget = null;
        boolean bossOwned = !level().isClientSide && getBossOwnerUUID() != null;
        if (bossOwned) {
            bossOwnerTracker.tickDependent(this, true, 100);
            inheritedTarget = getTarget();
            if (isRemoved()) return;
        }
        if (!level().isClientSide) {
            updateHoneySoaking();
            if (isRemoved()) {
                return;
            }
        }
        oldSquish = squish;
        boolean groundedBeforeTick = onGround();
        super.tick();
        if (bossOwned && getTarget() != inheritedTarget) {
            setTarget(inheritedTarget);
        }
        if (onGround() && !groundedBeforeTick) {
            targetSquish = -0.5F;
        } else if (!onGround() && wasOnGround) {
            targetSquish = 1.0F;
        }
        squish += (targetSquish - squish) * 0.5F;
        targetSquish *= 0.6F;
        wasOnGround = onGround();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        BaseBoss owner = getBossOwner();
        return target != owner && (owner == null || owner.canAttack(target)) && super.canAttack(target);
    }

    @Override
    public void remove(RemovalReason reason) {
        bossOwnerTracker.unbind(this);
        super.remove(reason);
    }

    /// 连续驱动史莱姆移动的行为节点。
    ///
    /// 它对应 1.21 侧原版史莱姆同时运行的漂浮、攻击、随机转向和持续跳跃四个目标，
    /// 但仍作为新架构中的单一行为树节点执行。节点不会把一次跳跃拆成“蓄力—起跳—落地—
    /// 长时间等待”的离散任务，因此转向、追击速度和落地后的下一跳节奏与原实现一致。
    private static final class SlimeLocomotionAction extends BTNode {
        private final BaseSlime slime;
        private float idleDirection;
        private int directionChangeDelay;

        private SlimeLocomotionAction(BaseSlime slime) {
            this.slime = slime;
            this.idleDirection = slime.getYRot();
        }

        @Override
        public BTStatus execute() {
            if (!(slime.getMoveControl() instanceof SlimeMoveControl control)) {
                return BTStatus.FAILURE;
            }

            if (slime.isInWater() || slime.isInLava()) {
                if (slime.getRandom().nextFloat() < 0.8F) {
                    slime.getJumpControl().jump();
                }
                control.setWantedMovement(1.2);
                return BTStatus.RUNNING;
            }

            LivingEntity target = slime.getTarget();
            if (target != null && target.isAlive() && slime.canAttack(target)) {
                Vec3 pursuit = slime.getPursuitPosition(target);
                Vec3 horizontal = new Vec3(
                        pursuit.x - slime.getX(), 0.0D, pursuit.z - slime.getZ());
                if (horizontal.lengthSqr() > 1.0E-7D) {
                    float wantedYaw = (float) (Mth.atan2(-horizontal.x, horizontal.z) * Mth.RAD_TO_DEG);
                    control.setDirection(wantedYaw, slime.isEffectiveAi());
                } else {
                    control.setDirection(slime.getYRot(), slime.isEffectiveAi());
                }
                control.setWantedMovement(1.0);
                return BTStatus.RUNNING;
            }

            if (!slime.isPassenger() && (slime.onGround() || slime.hasEffect(net.minecraft.world.effect.MobEffects.LEVITATION))) {
                if (--directionChangeDelay <= 0) {
                    directionChangeDelay = 40 + slime.getRandom().nextInt(60);
                    idleDirection = slime.getRandom().nextInt(360);
                }
                control.setDirection(idleDirection, false);
                control.setWantedMovement(1.0);
            }
            return BTStatus.RUNNING;
        }
    }

    protected Vec3 getPursuitPosition(LivingEntity target) {
        return getBossOwnerUUID() == null
                ? target.position()
                : BossMinionCoordinator.predict(target, 3.0D, 2.5D);
    }

    /// 复刻原版史莱姆的移动控制器。行为树只提供朝向、速度和是否处于攻击状态；真正的
    /// 落地等待、起跳和空中续速都在这里连续推进，避免每一刻直接改写水平速度造成弹跳。
    private static final class SlimeMoveControl extends MoveControl {
        private final BaseSlime slime;
        private float wantedYRot;
        private int jumpDelay;
        private boolean aggressive;

        private SlimeMoveControl(BaseSlime slime) {
            super(slime);
            this.slime = slime;
            this.wantedYRot = slime.getYRot();
        }

        private void setDirection(float yRot, boolean aggressive) {
            this.wantedYRot = yRot;
            this.aggressive = aggressive;
        }

        private void setWantedMovement(double speed) {
            speedModifier = speed;
            operation = Operation.MOVE_TO;
        }

        @Override
        public void tick() {
            mob.setYRot(rotlerp(mob.getYRot(), wantedYRot, 90.0F));
            mob.setYHeadRot(mob.getYRot());
            mob.setYBodyRot(mob.getYRot());
            if (operation != Operation.MOVE_TO) {
                mob.setZza(0.0F);
                return;
            }

            operation = Operation.WAIT;
            float speed = (float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            if (!mob.onGround()) {
                mob.setSpeed(speed);
                return;
            }

            mob.setSpeed(speed);
            if (jumpDelay-- <= 0) {
                jumpDelay = slime.getJumpDelay();
                if (aggressive) {
                    jumpDelay /= 3;
                }
                slime.getJumpControl().jump();
                slime.playSound(SoundEvents.SLIME_JUMP, slime.getSoundVolume(), ((slime.getRandom().nextFloat() - slime.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                return;
            }

            slime.setXxa(0.0F);
            slime.setZza(0.0F);
            mob.setSpeed(0.0F);
        }
    }

    // === 子类可重写的行为钩子 ===

    /// 攻击目标后触发，用于附加效果（如冰霜减速）
    protected void onAttackTarget(LivingEntity target) {}

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            onAttackTarget(living);
        }
        return result;
    }

    /// 对目标造成接触伤害。
    protected void dealContactDamage(LivingEntity target) {
        if (!level().isClientSide && isAlive() && isEffectiveAi() && isWithinMeleeAttackRange(target) && hasLineOfSight(target) && doHurtTarget(target)) {
            playSound(SoundEvents.SLIME_ATTACK, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    /// 每 tick 推进蜂蜜浸泡状态。只有 1.21 侧明确支持的绿、蓝、紫三种史莱姆参与转化，
    /// 离开蜂蜜后进度立即清零；完成时由服务端原位替换为二号蜂蜜史莱姆。
    private void updateHoneySoaking() {
        if (!canConvertFromHoney() || !level().getBlockState(blockPosition()).is(ModTags.Blocks.HONEY)) {
            honeySoakTime = 0;
            return;
        }
        if (++honeySoakTime < HONEY_SOAK_CHECKS_REQUIRED) {
            return;
        }

        HoneySlime honeySlime = MonsterEntities.HONEY_SLIME.get().create(level());
        if (honeySlime == null) {
            return;
        }
        honeySlime.setSlimeSize(2);
        honeySlime.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
        level().addFreshEntity(honeySlime);
        discard();
    }

    private boolean canConvertFromHoney() {
        EntityType<?> type = getType();
        return type == MonsterEntities.GREEN_SLIME.get()
                || type == MonsterEntities.BLUE_SLIME.get()
                || type == MonsterEntities.PURPLE_SLIME.get();
    }

    @Override
    public void playerTouch(Player player) {
        dealContactDamage(player);
    }

    @Override
    public void push(Entity entity) {
        super.push(entity);
        if (entity instanceof IronGolem ironGolem) {
            dealContactDamage(ironGolem);
        }
    }

    /// 是否免疫火焰伤害
    protected boolean isFireImmune() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return isFireImmune();
    }

    /// 在水中是否受伤
    protected boolean hurtByWater() {
        return false;
    }

    /// 是否免疫溺水
    protected boolean ignoreDrowning() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (onGround()) {
            resetFallDistance();
        }
        if (hurtByWater() && isInWater()) {
            hurt(damageSources().freeze(), 0.8F);
        }
    }

    @Override
    public boolean isInWater() {
        if (ignoreDrowning()) return false;
        return super.isInWater();
    }

    /// 客户端模型缩放值，供使用同一实体类型表达不同年龄的史莱姆家族使用。
    public float getVisualScale() {
        return 1.0F;
    }

    /// 普通史莱姆在 1.21 侧使用原版尺寸 2；年龄或特殊变体可继续通过
    /// {@link #getVisualScale()} 调整最终大小。
    public float getVisualSize() {
        return getSlimeSize() * getVisualScale();
    }

    /// 上一游戏刻的挤压值，供客户端在两刻之间插值。
    public float getOldSquish() {
        return oldSquish;
    }

    /// 当前游戏刻的挤压值。
    public float getSquish() {
        return squish;
    }

    // === 属性工厂方法 ===

    protected static AttributeSupplier.Builder createSlimeAttributes(float attackDamage, int armor, float maxHealth) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, maxHealth)
                .add(Attributes.ATTACK_DAMAGE, attackDamage)
                .add(Attributes.ARMOR, (double) armor)
                .add(PortAttributesExtension.waterMovementEfficiency().get(), 0.2)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    public static AttributeSupplier.Builder createGreenAttributes() {
        return createSlimeAttributes(3.0f, 0, 9.0f);
    }

    public static AttributeSupplier.Builder createBlueAttributes() {
        return createSlimeAttributes(4.0f, 2, 16.0f);
    }

    public static AttributeSupplier.Builder createRedAttributes() {
        return createSlimeAttributes(5.0f, 4, 25.0f);
    }

    public static AttributeSupplier.Builder createPurpleAttributes() {
        return createSlimeAttributes(5.0f, 6, 25.0f);
    }

    public static AttributeSupplier.Builder createYellowAttributes() {
        return createSlimeAttributes(6.0f, 7, 25.0f);
    }

    public static AttributeSupplier.Builder createDungeonAttributes() {
        return createSlimeAttributes(15.6f, 2, 78.0f);
    }

    public static AttributeSupplier.Builder createDesertAttributes() {
        return createSlimeAttributes(6.0f, 5, 21.0f);
    }

    public static AttributeSupplier.Builder createJungleAttributes() {
        return createSlimeAttributes(12.0f, 6, 46.0f);
    }

    public static AttributeSupplier.Builder createEvilAttributes() {
        return createSlimeAttributes(29.0f, 2, 58.0f);
    }

    public static AttributeSupplier.Builder createGreenDumplingAttributes() {
        return createSlimeAttributes(5.0f, 0, 25.0f);
    }

    public static AttributeSupplier.Builder createSwampAttributes() {
        return createSlimeAttributes(5.0f, 1, 25.0f);
    }
}
