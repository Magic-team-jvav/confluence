package org.confluence.mod.common.entity.boss;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.color.FloatRGB;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.terraentity.api.entity.ai.IBossFSM;
import org.confluence.terraentity.client.gui.CustomizeBossHealthBar;
import org.confluence.terraentity.data.codec.TECodecs;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.model.CrownOfKingSlimeModelEntity;
import org.confluence.terraentity.entity.monster.slime.BaseSlime;
import org.confluence.terraentity.entity.monster.slime.SpikedSlime;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.confluence.terraentity.entity.util.DifficultSelector;
import org.confluence.terraentity.init.TEParticles;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.mixed.IBossEvent;
import org.confluence.terraentity.mixin.accessor.SlimeAccessor;
import org.confluence.terraentity.network.s2c.SyncBossEventHealthPacket;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.AdapterUtils;
import org.confluence.terraentity.utils.Easing;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * 史王
 */
@SuppressWarnings("all")
public class KingSlime extends Slime implements DeathAnimOptions, IBossFSM, Boss {

    private final int COLOR_INT;
    // 缩小/膨胀时长，单位：刻
    private final int SHRINK_ENLARGE_DURATION;
    // 大师 专家 普通
    private final int TOTAL_SPLITS;
    private final float JUMP_SPEED_HORIZONTAL;
    private final float JUMP_SPEED_VERTICAL;
    private final float JUMP_SPEED_VERTICAL_THIRD;
    private final float SWIM_SPEED_HORIZONTAL;
    private final float FLOATING_ACCELERATION;
    private final FloatRGB COLOR;
    private final float[] BLOOD_COLOR;

    public record SkillParams(int xpReward, int color, int shrinkDuration,
                              List<Integer> totalSplits,
                              List<Float> jumpSpeedHorizontal,
                              List<Float> jumpSpeedVertical,
                              List<Float> jumpSpeedVerticalThird,
                              List<Float> swimSpeedHorizontal,
                              float floatingAcceleration
    ) {

        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("xp_reward").forGetter(SkillParams::xpReward),
                Codec.INT.fieldOf("color").forGetter(SkillParams::color),
                Codec.INT.fieldOf("shrink_duration").forGetter(SkillParams::shrinkDuration),
                TECodecs.INT_LIST_CODEC.fieldOf("total_splits").forGetter(SkillParams::totalSplits),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("jump_speed_horizontal").forGetter(SkillParams::jumpSpeedHorizontal),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("jump_speed_vertical").forGetter(SkillParams::jumpSpeedVertical),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("jump_speed_vertical_third").forGetter(SkillParams::jumpSpeedVerticalThird),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("swim_speed_horizontal").forGetter(SkillParams::swimSpeedHorizontal),
                Codec.FLOAT.fieldOf("floating_acceleration").forGetter(SkillParams::floatingAcceleration)
        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams() {
            return new SkillParams(800, 0x73bcf4, 20,
                    List.of(30, 50, 75, 100),
                    List.of(1.1f, 1.35f, 1.55f, 1.80f),
                    List.of(1.5f, 1.75f, 2f, 2.25f),
                    List.of(2f, 2.25f, 2.5f, 2.75f),
                    List.of(0.1f, 0.15f, 0.2f, 0.25f),
                    0.05f);
        }
    }

    private static final State<KingSlime> STATE_NORMAL = new State<>() {
        private Vec3 horVel = Vec3.ZERO;
        int epoch = 1;

        @Override
        public void enter(KingSlime boss) {
            if (boss.difficultSelector.isExpert() && boss.getHealth() / boss.getMaxHealth() < 0.5f) {
                epoch = 2;
            } else {
                epoch = 1;
            }
        }

        ;

        @Override
        public void tick(KingSlime boss) {
            // 脱战
            List<Player> playersInRange = boss.getNearbyPlayers(100);

            if (!boss.shouldDisappear) {
                // 不要每次都初始化playersInRange2
                if (playersInRange.isEmpty() && boss.getNearbyPlayers(150).isEmpty()) {
                    boss.shouldDisappear = true;
                }
            }

            // 更新BOSS大小
            boss.setSize(boss.getMaxSize(), false);
            // 在地面上/液体中
            boolean inLiquid = boss.isInWater() || boss.isInLava();
            if (boss.onGround() || inLiquid) {
                boss.indexAI++;
                // 缩地消失
                if (boss.shouldDisappear) {
                    boss.toState(STATE_SHRINK);
                    return;
                }
                double horizontalSpd;
                double verticalAcc;
                // 漂浮、移动
                if (inLiquid) {
                    horizontalSpd = boss.SWIM_SPEED_HORIZONTAL;
                    verticalAcc = boss.FLOATING_ACCELERATION;
                }
                // 跳跃
                else {
                    // 血量降低，跳跃加速
                    int reduce = (int) ((1 - boss.getSize() / 127.0f) * 3);
                    if (boss.indexAI > 20 - reduce && boss.indexAI <= 20) {
                        boss.indexAI = 20;
                    } else if (boss.indexAI > 40 - reduce && boss.indexAI <= 40) {
                        boss.indexAI = 40;
                    } else if (boss.indexAI > 60 - reduce && boss.indexAI <= 60) {
                        boss.indexAI = 60;
                    } else if (boss.indexAI > 80 - reduce && boss.indexAI <= 80) {
                        boss.indexAI = 80;
                    } else {
                        boss.indexAI += reduce;
                    }
                    switch (boss.indexAI) {
                        case 20, 40, 60, 80 -> {
                            horizontalSpd = boss.JUMP_SPEED_HORIZONTAL;
                            verticalAcc = (boss.indexAI == 80 ? boss.JUMP_SPEED_VERTICAL_THIRD : boss.JUMP_SPEED_VERTICAL)
                                    * (boss.getSize() + 127) / 256; // 血量降低，跳跃高度减少
                        }
                        default -> {
                            horizontalSpd = 0;
                            verticalAcc = 0;
                        }
                    }
                }
                // 调整速度
                horVel = TEUtils.rotToDir(boss.getYRot(), 0).scale(horizontalSpd);
                if (verticalAcc != 0) {
                    Vec3 motion = boss.getDeltaMovement();
                    motion = motion.add(0, verticalAcc, 0);
                    boss.setDeltaMovement(motion);
                }


                if (boss.indexAI >= 85) {
                    // 下一阶段
                    if (--epoch <= 0) {
                        boss.toState(STATE_SHRINK);
                    } else {
                        boss.indexAI = 0;
                    }
                }
                // 水平方向速度更新
                boss.setHorizontalSpeed(horVel);

            }
            // 重置水平方向速度
            else {
                horVel = Vec3.ZERO;
            }
        }
    };
    private static final State<KingSlime> STATE_SHRINK = new State<>() {


        @Override
        public void tick(KingSlime boss) {
            boss.indexAI++;
            // 防止BOSS水平方向的移动
            boss.setHorizontalSpeed(Vec3.ZERO);
            // 更新BOSS大小
            int maxSize = boss.getMaxSize();
            int s = Mth.clamp(1, boss.getMaxSize() *
                    (boss.SHRINK_ENLARGE_DURATION - boss.indexAI) / boss.SHRINK_ENLARGE_DURATION, maxSize);

            boss.setSize(s, false);
            if (boss.indexAI >= boss.SHRINK_ENLARGE_DURATION) {
                // BOSS脱战
                if (boss.shouldDisappear) {
                    boss.discard();
                    return;
                }
                // TP到玩家位置并开始膨胀
                if (boss.level() instanceof ServerLevel serverLevel) {
                    Vec3 closestPlayerPos;
                    Player player = serverLevel.getRandomPlayer();
                    if (player != null) {
                        if (boss.getTarget() != null) {
                            closestPlayerPos = boss.getTarget().getOnPos().getCenter();
                        } else {
                            closestPlayerPos = player.getOnPos().getCenter();
                        }
                        serverLevel.addFreshEntity(new CrownOfKingSlimeModelEntity(serverLevel, boss.position().add(0.0, boss.getDimensions(boss.getPose()).height(), 0.0)));
                        Vec3 side;
                        if (!boss.difficultSelector.isExpert() && player != null) {
                            side = closestPlayerPos.add(player.getLookAngle().multiply(-1, 0, -1).normalize().scale(5));
                        } else {
                            side = TEUtils.circle(10, boss.getRandom().nextFloat() * 3.14f).add(closestPlayerPos);
                        }

                        boss.teleportTo(side.x, side.y + 2F, side.z);
                    }
                }
                boss.toState(STATE_ENLARGE);
            }
        }
    };
    private static final State<KingSlime> STATE_ENLARGE = new State<>() {
        @Override
        public void tick(KingSlime boss) {
            boss.indexAI++;
            // 防止BOSS水平方向的移动
            boss.setHorizontalSpeed(Vec3.ZERO);
            // 更新BOSS大小
            int maxSize = boss.getMaxSize();

            boss.setSize(Mth.clamp(1, boss.getMaxSize() *
                    boss.indexAI / boss.SHRINK_ENLARGE_DURATION, maxSize), false);
            if (boss.indexAI >= boss.SHRINK_ENLARGE_DURATION) {
                boss.toState(STATE_NORMAL);
            }
        }
    };

    // 变量
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_12).setPlayBossMusic(true);
    private int indexAI;
    //    private final int difficultyIdx;
    private boolean shouldDisappear;
    private State<KingSlime> AIState;
    // 重写跳跃-水平方向的移动
    private Vec3 horMoveDir;
    SkillParams skillParams;
    DifficultSelector difficultSelector;

    // 客户端缓动
    public int lastScale;
    int shrinkDuration;
    int shrinkDelta = 1;
    float cacheTick = 0;
    float cacheSize;

    public KingSlime(EntityType<KingSlime> slime, Level level) {
        super(slime, level);
        this.shouldDisappear = false;
//        this.difficultyIdx = switchByDifficulty(level, 0, 1, 2);
        this.indexAI = 0;
        this.AIState = STATE_NORMAL;

        // 重写跳跃-防止垂直方向的跳跃
        this.jumpControl = new JumpControl(this) {
            @Override
            public void jump() {}
        };
        // 水平方向移动
        horMoveDir = Vec3.ZERO;
        this.setSize(getMaxSize(), false);
        if (level().isClientSide) {
            CustomizeBossHealthBar.registerBossHealthBar(getDisplayName().getString(), this.getType());
        }
        this.difficultSelector = new DifficultSelector(level);

        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.KING_SLIME_PARAMS);
        this.xpReward = skillParams.xpReward;
        this.COLOR_INT = skillParams.color;
        this.SHRINK_ENLARGE_DURATION = skillParams.shrinkDuration;
        this.TOTAL_SPLITS = difficultSelector.switchBy(skillParams.totalSplits);
        this.JUMP_SPEED_HORIZONTAL = difficultSelector.switchBy(skillParams.jumpSpeedHorizontal);
        this.JUMP_SPEED_VERTICAL = difficultSelector.switchBy(skillParams.jumpSpeedVertical);
        this.JUMP_SPEED_VERTICAL_THIRD = difficultSelector.switchBy(skillParams.jumpSpeedVerticalThird);
        this.SWIM_SPEED_HORIZONTAL = difficultSelector.switchBy(skillParams.swimSpeedHorizontal);
        this.FLOATING_ACCELERATION = skillParams.floatingAcceleration;

        this.COLOR = FloatRGB.fromInteger(COLOR_INT);
        this.BLOOD_COLOR = COLOR.mixture(FloatRGB.ZERO, 0.5f).toArray();

        this.shrinkDuration = this.SHRINK_ENLARGE_DURATION;
    }

    public KingSlime(Level level) {
        this(TEBossEntities.KING_SLIME.get(), level);
    }

    @Override
    protected void dropExperience(@org.jetbrains.annotations.Nullable Entity entity) {
        super.dropExperience(entity);
    }

    @Override
    public void toState(State newState) {
        if (newState == this.AIState)
            return;
        this.AIState.leave(this);
        this.AIState = newState;
        this.AIState.enter(this);
        // 重置index
        this.indexAI = 0;
    }

    @Override
    public List<Player> getNearbyPlayers(double radius) {
        return level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(radius));
    }

    public static AttributeSupplier.Builder createSlimeAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 728)
                .add(LibAttributes.getAttackDamage(), 16.5)
                .add(Attributes.ATTACK_KNOCKBACK, 2.2)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.FOLLOW_RANGE, 100.0)
                ;
    }

    private void setHorizontalSpeed(Vec3 newDir) {
        Vec3 vel = getDeltaMovement();
        vel = vel.with(Direction.Axis.X, newDir.x()).with(Direction.Axis.Z, newDir.z());
        setDeltaMovement(vel);
    }

    @Override
    protected float getJumpPower() {
        return super.getJumpPower() * (this.getSize() + 30) / 157;
    }

    // 原版跳跃依旧会略微顿一下，给调整到几乎不会触发的间隔
    @Override
    protected int getJumpDelay() {
        return Short.MAX_VALUE;
    }

    private int getMaxSize() {
        return Math.round(getHealth() / getMaxHealth() * 10) + 6;
    }

    @Override
    public void AI() {
        // tick
        if (!level().isClientSide()) {
            this.AIState.tick(this);
//            ModUtils.testMessage(level(), this.jumping + ", " + getJumpDelay());
        }
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        super.registerGoals();
        this.goalSelector.removeAllGoals(g -> g.getClass().getName().endsWith("SlimeFloatGoal"));
    }

    public float[] getBossEventProgress() {
        return new float[]{this.getHealth(), this.getMaxHealth()};
    }

    public void syncBossHealthBar(ServerPlayer player) {
        float[] datas = getBossEventProgress();
        ((IBossEvent) this.bossEvent).terra_enity$setBossHealth(datas[0]);
        ((IBossEvent) this.bossEvent).terra_enity$setBossMaxHealth(datas[1]);
        AdapterUtils.sendToPlayer(player, new SyncBossEventHealthPacket(bossEvent.getId(), datas[0], datas[1]));
    }

    @Override
    public void tick() {
        // 先进行super.tick()
        super.tick();

        // 更新boss血条
        if (!level().isClientSide()) {
            float[] datas = getBossEventProgress();
            ((IBossEvent) this.bossEvent).terra_enity$setBossHealth(datas[0]);
            ((IBossEvent) this.bossEvent).terra_enity$setBossMaxHealth(datas[1]);
            bossEvent.setProgress(datas[0] / datas[1]);
        } else {
            this.shrinkDuration = Mth.clamp(this.shrinkDuration + this.shrinkDelta, 0, this.SHRINK_ENLARGE_DURATION);

        }

        bossEvent.setName(getDisplayName());
        // 不会受到摔落伤害
        resetFallDistance();
        // 落地的粒子效果，十分的高级
        if (onGround() && !((SlimeAccessor) this).isWasOnGround()) {
            int i = getSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = random.nextFloat() * Mth.TWO_PI;
                float f1 = random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
                level().addParticle(TEParticles.ITEM_GEL.get(), getX() + (double) f2, getY(), getZ() + (double) f3, COLOR.red(), COLOR.green(), COLOR.blue());
            }
        }

        // 额外的AI行为
        if (!isNoAi()) {
            AI();
        }
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer pServerPlayer) {
        super.startSeenByPlayer(pServerPlayer);
        bossEvent.addPlayer(pServerPlayer);
        if (tickCount != 0)
            syncBossHealthBar(pServerPlayer);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer pServerPlayer) {
        super.stopSeenByPlayer(pServerPlayer);
        bossEvent.removePlayer(pServerPlayer);
    }

    private int getSlimesLeft() {
        return (int) (getHealth() / getMaxHealth() * TOTAL_SPLITS);
    }

    private void spawnSlime(LivingEntity target) {
        if (level() instanceof ServerLevel level) {
            BaseSlime slime = new BaseSlime(TEMonsterEntities.BLUE_SLIME.get(), level, COLOR_INT, 2);
            BlockPos pos = blockPosition();
            slime.setPos(pos.getX(), pos.getY() + 0.5, pos.getZ());
            slime.setTarget(target);
            level.addFreshEntity(slime);
            if (LibMathUtils.checkChance(LibUtils.switchByDifficulty(level, pos, 0.0, 0.5, 0.75, 1.0), random)) {
                SpikedSlime spikedSlime = new SpikedSlime(TEMonsterEntities.SPIKED_SLIME.get(), level, 2);
                spikedSlime.setPos(pos.getX(), pos.getY() + 0.5, pos.getZ());
                spikedSlime.setTarget(target);
                level.addFreshEntity(spikedSlime);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.IN_WALL)) {
            return false;
        }
        // 在大小改变时不会受伤
        if (AIState != STATE_NORMAL) {
            return false;
        }
        // 记录受伤前生命对应的剩余分裂次数
        int lastSlimesLeft = getSlimesLeft();

        boolean result = super.hurt(pSource, pAmount);

        // 根据受伤前后剩余分裂次数差生成史莱姆
        for (int i = 0; i < lastSlimesLeft - getSlimesLeft(); i++) {
            if (this.getTarget() != null) {
                spawnSlime(this.getTarget());
            } else {
                spawnSlime(null);
            }
        }

        return result;
    }

    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!level().isClientSide) {
            if (bossEvent != null) {
                bossEvent.getPlayers().forEach(p -> syncBossHealthBar(p));
            }
        }
    }

    // 不要被推来推去
    @Override
    public boolean isPushable() {
        return false;
    }

    // 缩地期间不要伤害玩家
    public void playerTouch(Player pEntity) {
        if (AIState != STATE_NORMAL) {
            return;
        }

        super.playerTouch(pEntity);
    }

    // 不要让史莱姆生成默认落地粒子
    @Override
    protected boolean spawnCustomParticles() {
        return true;
    }

    @Override
    public void setSize(int pSize, boolean pResetHealth) {
        int i = Mth.clamp(pSize, 1, 127);
        this.lastScale = i;
        entityData.set(ID_SIZE, i);
        reapplyPosition();

        refreshDimensions();
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1F * i);
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        brain.clearMemories();
        setRemoved(removalReason);
//        invalidateCaps();
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public float[] getBloodColor() {
        return BLOOD_COLOR;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof Slime);
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

    public float getClientSize(float partialTicks) {
        float cache = this.shrinkDuration + partialTicks * this.shrinkDelta;
        if (cache == this.cacheTick) {
            return this.cacheSize;
        }
        this.cacheTick = cache;
        this.cacheSize = (float) Easing.EASE_OUT_QUAD.transform(cache, 0, this.SHRINK_ENLARGE_DURATION, 0, getMaxSize());
        return this.cacheSize;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(ID_SIZE) && level().isClientSide()) {
            if (this.getSize() == this.lastScale - 1 && this.lastScale == this.getMaxSize()) {
                // 缩小
                this.shrinkDuration = this.SHRINK_ENLARGE_DURATION;
                this.shrinkDelta = -1;
            } else if (this.getSize() == 2 && this.lastScale == 1) {
                // 放大
                this.shrinkDuration = 0;
                this.shrinkDelta = 1;
            }
            this.lastScale = this.getSize();
        }
        super.onSyncedDataUpdated(key);
    }

    public static class HurtByTargetGoal extends TargetGoal {
        private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        private static final int ALERT_RANGE_Y = 10;
        private boolean alertSameType;
        private int timestamp;
        private final Class<?>[] toIgnoreDamage;
        @Nullable
        private Class<?>[] toIgnoreAlert;

        public HurtByTargetGoal(Slime mob, Class<?>... toIgnoreDamage) {
            super(mob, true);
            this.toIgnoreDamage = toIgnoreDamage;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        public boolean canUse() {
            int i = this.mob.getLastHurtByMobTimestamp();
            LivingEntity livingentity = this.mob.getLastHurtByMob();
            if (i != this.timestamp && livingentity != null) {
                if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                    return false;
                } else {
                    Class[] var3 = this.toIgnoreDamage;
                    int var4 = var3.length;

                    for (int var5 = 0; var5 < var4; ++var5) {
                        Class<?> oclass = var3[var5];
                        if (oclass.isAssignableFrom(livingentity.getClass())) {
                            return false;
                        }
                    }

                    return this.canAttack(livingentity, HURT_BY_TARGETING);
                }
            } else {
                return false;
            }
        }

        public HurtByTargetGoal setAlertOthers(Class<?>... reinforcementTypes) {
            this.alertSameType = true;
            this.toIgnoreAlert = reinforcementTypes;
            return this;
        }

        public void start() {
            this.mob.setTarget(this.mob.getLastHurtByMob());
            this.targetMob = this.mob.getTarget();
            this.timestamp = this.mob.getLastHurtByMobTimestamp();
            this.unseenMemoryTicks = 300;
            if (this.alertSameType) {
                this.alertOthers();
            }

            super.start();
        }

        protected void alertOthers() {
            double d0 = this.getFollowDistance();
            AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0, d0);
            List<? extends Mob> list = this.mob.level().getEntitiesOfClass(this.mob.getClass(), aabb, EntitySelector.NO_SPECTATORS);
            Iterator iterator = list.iterator();

            while (true) {
                Mob mob;
                boolean flag;
                do {
                    do {
                        do {
                            do {
                                do {
                                    if (!iterator.hasNext()) {
                                        return;
                                    }

                                    mob = (Mob) iterator.next();
                                } while (this.mob == mob);
                            } while (mob.getTarget() != null);
                        } while (this.mob instanceof TamableAnimal && ((TamableAnimal) this.mob).getOwner() != ((TamableAnimal) mob).getOwner());
                    } while (mob.isAlliedTo(this.mob.getLastHurtByMob()));

                    if (this.toIgnoreAlert == null) {
                        break;
                    }

                    flag = false;
                    Class[] var8 = this.toIgnoreAlert;
                    int var9 = var8.length;

                    for (int var10 = 0; var10 < var9; ++var10) {
                        Class<?> oclass = var8[var10];
                        if (mob.getClass() == oclass) {
                            flag = true;
                            break;
                        }
                    }
                } while (flag);

                this.alertOther(mob, this.mob.getLastHurtByMob());
            }
        }

        protected void alertOther(Mob mob, LivingEntity target) {
            mob.setTarget(target);
        }
    }

    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.BLUE;
    }

    ;
}
