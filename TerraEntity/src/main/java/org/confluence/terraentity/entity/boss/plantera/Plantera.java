package org.confluence.terraentity.entity.boss.plantera;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.data.codec.TECodecs;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.proj.SeedProjectile;
import org.confluence.terraentity.entity.proj.SpikeBallProjectile;
import org.confluence.terraentity.entity.proj.SporeProjectile;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.network.s2c.SyncBossEventHealthPacket;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.List;

/**
 * 世花本体
 */
public class Plantera extends AbstractPlanteraTentacleSrc implements GeoEntity, Boss {
    public static final double SECOND_PHASE_HEALTH = 0.5;
    // 数组 - 0：一阶段 1：二阶段 2：狂暴
    // 世界之花钩 - 射程范围和移动速度
    private float hookRange;
    protected float hookSpeed;
    // 本体的移动速度
    private float[] moveSpeed;
    private float[] acceleration;
    // 弹幕的发射间隔和速度
    private int[] spikeBallInterval;
    private float spikeBallSpeed;
    private float spikeBallDamage;
    private int[] seedInterval;
    private float seedSpeed;
    private float seedDamage;
    private int[] sporeInterval;
    private float sporeSpeed;
    private float sporeDamage;
    // 二阶段爆触手的数量
    private int tentacleCountSelf, tentacleCountHook;

    protected PlanteraHook[] hooks;

    private int indexAI = 0, tentacleRespawnCounter = 0, phase;
    private int enragedCounter = 0; // 狂暴剩余时长
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(Plantera.class, EntityDataSerializers.INT);
    SkillParams skillParams;
    public Plantera(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        setDiscardFriction(true);
        if (ServerConfig.bossNoPhysics()) {
            noPhysics = true;
        }

        // 钩子
        hooks = new PlanteraHook[3];

        setPhase(0);

        // 各种数据
        collisionProperties = new CollisionProperties(1,1,0.5f);
        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.PLANTERA_PARAMS);
        this.xpReward = skillParams.xpReward;
        this.hookRange = this.difficultSelector.switchBy(skillParams.hookRange);
        this.hookSpeed = this.difficultSelector.switchBy(skillParams.hookSpeed);
        this.moveSpeed = new float[]{
                this.difficultSelector.switchBy(skillParams.moveSpeed, 0),
                this.difficultSelector.switchBy(skillParams.moveSpeed, 4),
                this.difficultSelector.switchBy(skillParams.moveSpeed, 8),
        };
        this.acceleration = new float[]{
                this.difficultSelector.switchBy(skillParams.acceleration, 0),
                this.difficultSelector.switchBy(skillParams.acceleration, 4),
                this.difficultSelector.switchBy(skillParams.acceleration, 8),
        };
        this.spikeBallInterval = new int[]{
                this.difficultSelector.switchBy(skillParams.spikeBallInterval, 0),
                this.difficultSelector.switchBy(skillParams.spikeBallInterval, 4),
        };
        this.spikeBallSpeed = this.difficultSelector.switchBy(skillParams.spikeBallSpeed);
        this.spikeBallDamage = this.difficultSelector.switchBy(skillParams.spikeBallDamage);
        this.seedInterval = new int[]{
                this.difficultSelector.switchBy(skillParams.seedInterval, 0),
                this.difficultSelector.switchBy(skillParams.seedInterval, 4),
        };
        this.seedSpeed = this.difficultSelector.switchBy(skillParams.seedSpeed);
        this.seedDamage = this.difficultSelector.switchBy(skillParams.seedDamage);
        this.sporeInterval = new int[]{
                this.difficultSelector.switchBy(skillParams.sporeInterval, 0),
                this.difficultSelector.switchBy(skillParams.sporeInterval, 4),
        };
        this.sporeSpeed = this.difficultSelector.switchBy(skillParams.sporeSpeed);
        this.sporeDamage = this.difficultSelector.switchBy(skillParams.sporeDamage);
        this.tentacleCountSelf = this.difficultSelector.switchBy(skillParams.tentacleCount, 0);
        this.tentacleCountHook = this.difficultSelector.switchBy(skillParams.tentacleCount, 4);
    }

    @Override
    public void firstSpawn() {
        if (isMainBody() && !level().isClientSide) {
            if (level() instanceof ServerLevel) {
                for (int i = 0; i < hooks.length; i++) {
                    int finalI = i;
                    TEUtils.spawnEntity(
                            () -> new PlanteraHook(TEBossEntities.PLANTERA_HOOK.get(), level(), Plantera.this, finalI),
                            (ServerLevel) level(), position());
                }
            }

            this.playSound(TESounds.ROAR.get());
        }
    }

    public record SkillParams(int xpReward,
                              List<Float> hookRange, List<Float> hookSpeed,
                              List<Float> moveSpeed, List<Float> acceleration,
                              List<Integer> spikeBallInterval, List<Float> spikeBallSpeed, List<Float> spikeBallDamage,
                              List<Integer> seedInterval, List<Float> seedSpeed, List<Float> seedDamage,
                              List<Integer> sporeInterval, List<Float> sporeSpeed, List<Float> sporeDamage,
                              List<Integer> tentacleCount

    ){
        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("xp_reward").forGetter(SkillParams::xpReward),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("hook_range").forGetter(SkillParams::hookRange),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("hook_speed").forGetter(SkillParams::hookSpeed),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("move_speed").forGetter(SkillParams::moveSpeed),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("acceleration").forGetter(SkillParams::acceleration),
                TECodecs.INT_LIST_CODEC.fieldOf("spike_ball_interval").forGetter(SkillParams::spikeBallInterval),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("spike_ball_speed").forGetter(SkillParams::spikeBallSpeed),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("spike_ball_damage").forGetter(SkillParams::spikeBallDamage),
                TECodecs.INT_LIST_CODEC.fieldOf("seed_interval").forGetter(SkillParams::seedInterval),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("seed_speed").forGetter(SkillParams::seedSpeed),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("seed_damage").forGetter(SkillParams::seedDamage),
                TECodecs.INT_LIST_CODEC.fieldOf("spore_interval").forGetter(SkillParams::sporeInterval),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("spore_speed").forGetter(SkillParams::sporeSpeed),
                TECodecs.FLOAT_LIST_CODEC.fieldOf("spore_damage").forGetter(SkillParams::sporeDamage),
                TECodecs.INT_LIST_CODEC.fieldOf("tentacle_count").forGetter(SkillParams::tentacleCount)
        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams(){
            return new SkillParams(2000,
                    // 钩子距离和速度
                    List.of(48f, 48f, 48f, 48f),
                    List.of(2f, 2f, 2f, 2f),
                    // 移速[]和加速度[]; → 囊度, ↓ 阶段
                    List.of(0.1f, 0.1f, 0.1f, 0.1f,
                            0.2f, 0.2f, 0.2f, 0.2f,
                            0.2f, 0.2f, 0.2f, 0.2f),
                    List.of(0.1f, 0.1f, 0.1f, 0.1f,
                            0.1f, 0.1f, 0.1f, 0.1f,
                            0.1f, 0.1f, 0.1f, 0.1f),
                    // 刺球攻击间隔（仅限一阶段或狂暴，狂暴使用满额射速）；第一行-满血，第二行-极限射速
                    List.of(22, 22, 22, 22,
                            14, 14, 14, 14),
                    // 刺球弹幕速度，伤害
                    List.of(0.85f, 0.85f, 0.85f, 0.85f),
                    List.of(18f, 28f, 42f, 42f),
                    // 种子攻击间隔（仅限一阶段或狂暴，狂暴使用满额射速）；第一行-满血，第二行-极限射速
                    List.of(27, 27, 27, 27,
                            13, 13, 13, 13),
                    // 种子弹幕速度，伤害
                    List.of(2.5f, 2.5f, 2.5f, 2.5f),
                    List.of(12f, 19f, 28f, 28f),
                    // 孢子攻击间隔（仅限二阶段或狂暴，狂暴使用满额射速）；第一行-半血，第二行-极限射速
                    List.of(27, 27, 27, 27,
                            13, 13, 13, 13),
                    // 孢子弹幕速度，伤害
                    List.of(2.5f, 2.5f, 2.5f, 2.5f),
                    List.of(12f, 19f, 28f, 28f),
                    // 触手数量：本体↓钩子  ->囊度
                    List.of(8, 8, 8, 8,
                            3, 3, 3, 3)
            );
        }
    }

    // 考虑狂暴和生命值后得出数值array中的索引
    public int getPhaseIndex() {
        if (enragedCounter > 0) return 2;
        return getHealth() / getMaxHealth() > SECOND_PHASE_HEALTH ? 0 : 1;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void addSkills() {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return TESounds.ROUTINE_HURT.get();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }
        return super.hurt(pSource, pAmount); // confluence mixin here
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new MoveGoal());
        targetSelector.addGoal(1, new HookGoal());
        targetSelector.addGoal(1, new ProjectileGoal());
        targetSelector.addGoal(1, new TentacleGoal());

        this.registerRandomStrollGoal();

    }

    public void setPhase(int newPhase) {
        if (newPhase < 0) {
            newPhase = getEntityData().get(DATA_PHASE);
        } else {
            if (! level().isClientSide) {
                getEntityData().set(DATA_PHASE, newPhase);
            }
        }

        phase = newPhase;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Phase", phase);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (level() instanceof ServerLevel) {
            if (tag.contains("Phase")) {
                getEntityData().set(DATA_PHASE, tag.getInt("Phase"));
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PHASE, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_PHASE) {
            setPhase(-1);
        }
    }

    @Override
    public void tick() {
        boolean server = false;
        if (level() instanceof ServerLevel) {
            server = true;
        }

        super.tick();

        if (server) {
            // 服务端 - 对于加载区块中解冻部件的处理
            if (tickCount == 1) {
                setPhase(-1);
            }

            indexAI ++;
            enragedCounter --;

            System.out.println(phase);
            if (tentacles != null) {
                System.out.println(tentacleRespawnCounter + " | " + tentacles.size());
            }
            for (PlanteraHook hook : hooks) {
                System.out.println(hook + "");
            }
        }
    }

    @Override
    public float[] getBossEventProgress(){
        float value = getHealth();
        float getMax = getMaxHealth();
        // 这个，不需要了
//        if (tentacles == null) {
//            double tentaclesMaxHealth = (tentacleCountSelf + hooks.length * tentacleCountHook) * PlanteraTentacle.MAX_HEALTH;
//            value += (float) (tentaclesMaxHealth);
//            getMax += (float) (tentaclesMaxHealth);
//        }
//        else {
//            for (PlanteraTentacle tentacle : tentacles) {
//                value += tentacle.getHealth();
//                getMax += tentacle.getMaxHealth();
//            }
//            for (PlanteraHook hook : hooks) {
//                if (hook == null || hook.tentacles == null) continue;
//                for (PlanteraTentacle tentacle : hook.tentacles) {
//                    value += tentacle.getHealth();
//                    getMax += tentacle.getMaxHealth();
//                }
//            }
//        }
        PacketDistributor.sendToAllPlayers(new SyncBossEventHealthPacket(bossEvent.getId(), value, getMax));
        return new float[]{value , getMax};
    }



    @Override
    public boolean canAttack(LivingEntity entity) {
        if (!super.canAttack(entity)) return false;
        return !(entity instanceof Plantera);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldDoCollision() {
        return super.shouldDoCollision();
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor(){
        return BossEvent.BossBarColor.GREEN;
    };

    @Override
    protected boolean shouldOverPlayer(){
        return false;
    }

    protected void enrage() {
        if (this.enragedCounter <= 0) {
            playSound(TESounds.ROAR.get());
        }
        this.enragedCounter = 200;
    }

    /*
     * GOALS
     */

    public class MoveGoal extends Goal {

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (getTarget() == null){
                return;
            }

            float spd = moveSpeed[getPhaseIndex()];
            float accMag = acceleration[getPhaseIndex()];
            // 向玩家奔去
            Vec3 acc = getTarget().position().subtract(position());
            acc = acc.normalize().scale( accMag );
            // 触手最远距离
            for (PlanteraHook hook : hooks) {
                if (hook == null) continue;
                if (!(hook.state == PlanteraHook.STATE_GRABBED)) continue;

                Vec3 offset = hook.position().subtract(position());
                double lenSqr = offset.lengthSqr();
                if (lenSqr > hookRange * hookRange) {
                    double len = Math.sqrt(lenSqr);
                    // 在5格时获得与acc相等的拉力，且拉力随距离增长
                    offset = offset.scale( (len - hookRange) * accMag / (len * 25) );
                    acc = acc.add(offset);
                }
            }
            // 更新速度
            Vec3 vel = getDeltaMovement().add(acc);
            if (vel.lengthSqr() > spd * spd ) {
                vel = vel.normalize().scale( spd );
            }
            setDeltaMovement(vel);

            lookAt(90);
        }
    }

    public class HookGoal extends Goal {
        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        private BlockPos getHookPos() {
            Vec3 axis = getTarget().position().subtract(position());
            Vec3 dir = TEUtils.rotToDir(TEUtils.dirToRot(axis)[0] + 45, 0).scale( hookRange );
            Quaterniond rotator = new Quaterniond().fromAxisAngleDeg(
                    axis.x, axis.y, axis.z, 20 * Math.signum(random.nextFloat()));
            // 初始化方向
            Vector3d dir3d = new Vector3d(dir.x, dir.y, dir.z);
            Quaterniond initRotator = new Quaterniond().fromAxisAngleDeg(
                    axis.x, axis.y, axis.z, random.nextFloat() * 360);
            initRotator.transform(dir3d, dir3d);

            for (int i = 0; i < 18; i ++) {
                BlockHitResult result = level().isBlockInLine(
                        new ClipBlockStateContext(position(), position().add(dir3d.x(), dir3d.y(), dir3d.z()),
                                BlockBehaviour.BlockStateBase::canOcclude) );
                if (result.getType() == HitResult.Type.BLOCK) {
                    return result.getBlockPos();
                }
                rotator.transform(dir3d, dir3d);
            }

            // 找不到
            return null;
        }

        @Override
        public void tick() {
            if (getTarget() == null){
                return;
            }

            // 如果玩家离得太远直接红温
            if (getTarget().position().distanceToSqr(position()) > hookRange * hookRange) {
                enrage();
            }

            // 发射钩子
            if (indexAI % 50 == 0) {
                for (PlanteraHook hook : hooks) {
                    if (hook == null) continue;
                    if (! (hook.state == PlanteraHook.STATE_IDLE)) continue;
                    BlockPos hookPos = getHookPos();
                    // 抓不到方块直接红温
                    if (hookPos == null) {
                        enrage();
                    }
                    // 抓方块
                    else {
                        hook.setTargetBlock(hookPos);
                        hook.setState(PlanteraHook.STATE_EXTENDING);
                    }
                }
            }
            // 收回钩子
            else if (indexAI % 50 == 25) {
                double maxDistSqr = 0;
                PlanteraHook hookToRemove = null;
                int hooksGrabbed = 0;
                for (PlanteraHook hook : hooks) {
                    if (hook == null) continue;
                    if (! hook.isAlive()) continue;
                    if (! (hook.state == PlanteraHook.STATE_GRABBED)) continue;
                    hooksGrabbed ++;
                    double distSqr = hook.position().distanceToSqr(getTarget().position());
                    if (distSqr > maxDistSqr) {
                        maxDistSqr = distSqr;
                        hookToRemove = hook;
                    }
                }
                // 收回钩子
                if (hooksGrabbed >= 3 && hookToRemove != null) {
                    hookToRemove.state = PlanteraHook.STATE_RETRACTING;
                }
            }
        }
    }

    public class ProjectileGoal extends Goal {
        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (getTarget() == null){
                return;
            }

            // 防止阴险的零帧起手
            if (indexAI < 100) return;

            int sdItv, sbItv, sprItv;
            switch (getPhaseIndex()) {
                case 0: {
                    double multi = (getHealth() / getMaxHealth() * 2) - 1; // 100% -> 50% 血: 1 -> 0
                    sdItv = (int) Math.round(seedInterval[0] * multi + seedInterval[1] * (1 - multi));
                    sbItv = (int) Math.round(spikeBallInterval[0] * multi + spikeBallInterval[1] * (1 - multi));
                    sprItv = -1;
                    break;
                }
                case 1: {
                    double multi = getHealth() / getMaxHealth() * 2; // 50% -> 0% 血: 1 -> 0
                    sdItv = -1;
                    sbItv = -1;
                    sprItv = (int) Math.round(sporeInterval[0] * multi + sporeInterval[1] * (1 - multi));
                    break;
                }
                case 2:
                default: {
                    sdItv = seedInterval[1];
                    sbItv = spikeBallInterval[1];
                    sprItv = sporeInterval[1];
                    break;
                }
            }

            // 种子
            if (sdItv > 0 && indexAI % sdItv == 0) {
                Vec3 velocity = getTarget().position().subtract(position()).normalize().scale(seedSpeed);
                SeedProjectile seed = new SeedProjectile(TEProjectileEntities.SEED.get(), level(), getTarget());
                seed.addDamage(seedDamage);
                seed.setPos(position());
                seed.setOwner(Plantera.this);
                seed.shoot(velocity.x, velocity.y, velocity.z, seedSpeed, 0f);
                level().addFreshEntity(seed);
            }

            // 刺球
            if (sbItv > 0 && indexAI % sbItv == 0) {
                Vec3 velocity = getTarget().position().subtract(position()).normalize().scale(spikeBallSpeed);
                SpikeBallProjectile spikeBall = new SpikeBallProjectile(TEProjectileEntities.SPIKE_BALL.get(), level(), getTarget());
                spikeBall.addDamage(spikeBallDamage);
                spikeBall.setPos(position());
                spikeBall.setOwner(Plantera.this);
                spikeBall.shoot(velocity.x, velocity.y, velocity.z, spikeBallSpeed, 0f);
                level().addFreshEntity(spikeBall);
            }

            // 孢子
            if (sprItv > 0 && indexAI % sprItv == 0) {
                Vec3 velocity = getTarget().position().subtract(position())
                        .with(Direction.Axis.Y, 0).normalize().scale(sporeSpeed);
                SporeProjectile spore = new SporeProjectile(TEProjectileEntities.SPORE.get(), level(), getTarget());
                spore.addDamage(sporeDamage);
                spore.setPos(position());
                spore.setOwner(Plantera.this);
                spore.shoot(velocity.x, velocity.y, velocity.z, sporeSpeed, 0f);
                level().addFreshEntity(spore);
            }
        }
    }

    public class TentacleGoal extends Goal {
        @Override
        public boolean canUse() {
            return getPhaseIndex() >= 1;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            // 爆触手
            if (phase == 0 && getHealth() / getMaxHealth() < SECOND_PHASE_HEALTH) {
                setPhase(1);
                // 本体
                for (int i = 0; i < tentacleCountSelf; i++) {
                    spawnTentacle();
                }
                // 钩子
                for (PlanteraHook hook : hooks) {
                    if (hook == null) continue;
                    for (int i = 0; i < tentacleCountHook; i++) {
                        hook.spawnTentacle();
                    }
                }
            }
            // 触手重生
            if (tentacles != null) {
                // 清理死去的触手
                for (int i = 0; i < tentacles.size(); i ++) {
                    if (tentacles.get(i).isAlive()) continue;
                    tentacles.remove(i);
                    i --;
                }
                // 5秒基础，每多一个触手减缓5秒重生间隔
                if (tentacles.size() < tentacleCountSelf && ++tentacleRespawnCounter > (tentacles.size() + 1) * 100) {
                    tentacleRespawnCounter = 0;
                    spawnTentacle();
                }
            }
        }
    }
}
