package org.confluence.terraentity.entity.boss;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.api.entity.IAutoLeaveMob;
import org.confluence.terraentity.api.entity.blur.IMotionBlurHolder;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.ai.fsm.MobSkill;
import org.confluence.terraentity.entity.ai.motion.DashComponent;
import org.confluence.terraentity.entity.blur.MotionBlurManager;
import org.confluence.terraentity.entity.blur.PosRotMotionBlurContext;
import org.confluence.terraentity.entity.blur.PosRotMotionBlurManager;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.TEUtils;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 克眼
 */

public class EyeOfCthulhu extends AbstractTerraBossBase implements GeoEntity, Boss, IAutoLeaveMob, IMotionBlurHolder<PosRotMotionBlurContext> {

    private final float DAMAGE ;//一阶段接触伤害
    private final float CRAZY_DAMAGE ;//二阶段接触伤害
    private final float MOVE_SPEED ;

    private final float dashFactor ; //冲刺增伤
    private final float stage2SpeedFactor ; //二阶段加速加成
    private final float minDashDistanceSqr;

    private float speedFactor = 2f; //冲刺加速

    SkillParams skillParams;

    public static class SkillParams{
        private final float DAMAGE ;//一阶段接触伤害
        private final float CRAZY_DAMAGE ;//二阶段接触伤害
        private final float MOVE_SPEED ;

        private final float dashFactor; //冲刺增伤
        private final float stage2SpeedFactor; //二阶段加速加成
        private final float minDashDistanceSqr;
        private final int xpReward;

        SkillParams(float damage, float crazyDamage, float moveSpeed, float dashFactor, float stage2SpeedFactor, float minDashDistanceSqr, int xpReward){
            this.DAMAGE = damage;
            this.CRAZY_DAMAGE = crazyDamage;
            this.MOVE_SPEED = moveSpeed;

            this.dashFactor = dashFactor;
            this.stage2SpeedFactor = stage2SpeedFactor;
            this.minDashDistanceSqr = minDashDistanceSqr;
            this.xpReward = xpReward;
        }

        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.FLOAT.fieldOf("damage").forGetter(s->s.DAMAGE),
                Codec.FLOAT.fieldOf("crazy_damage").forGetter(s->s.CRAZY_DAMAGE),
                Codec.FLOAT.fieldOf("move_speed").forGetter(s->s.MOVE_SPEED),
                Codec.FLOAT.fieldOf("dash_factor").forGetter(s->s.dashFactor),
                Codec.FLOAT.fieldOf("stage2_speed_factor").forGetter(s->s.stage2SpeedFactor),
                Codec.FLOAT.fieldOf("min_dash_distance_sqr").forGetter(s->s.minDashDistanceSqr),
                Codec.INT.fieldOf("xp_reward").forGetter(s->s.xpReward)

        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams(){
            return new EyeOfCthulhu.SkillParams(4, 6, 0.5f,
                    1.5f, 1.5f, 20, 1000);
        }

    }


    //定义技能参数
    private int summonCDAll = 20; //仆从召唤cd
    private int summonCD = summonCDAll;

    private final int stage2_dashCount_base = -5 + 3;
    private int stage2_dashCount = stage2_dashCount_base; //二阶段冲刺次数，每掉1/10的血+1
    private int stage2_dashCount_max = 3; //二阶段冲刺正常冲刺次数


    private Vec3 dashPos;
    private Vec3 dashDir;

    private int stage1_dashCount = 3;

    DashComponent dashComponent;

    public PosRotMotionBlurManager trails = new PosRotMotionBlurManager(20);

    private static final EntityDataAccessor<Boolean> DATA_ENABLE_TRAILS = SynchedEntityData.defineId(EyeOfCthulhu.class, EntityDataSerializers.BOOLEAN);


    public EyeOfCthulhu(EntityType<EyeOfCthulhu> entityType, Level level) {
        super(entityType, level);
        //初始属性

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
        this.playSound(TESounds.ROAR.get());
        if (ServerConfig.bossNoPhysics())
            this.noPhysics = true;

        collisionProperties.attackInternal = 1;
        collisionProperties.detectInternal = 1;

        dashComponent = new DashComponent(this);
        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.EYE_OF_CTHULHU_PARAMS);
        this.DAMAGE = skillParams.DAMAGE;
        this.CRAZY_DAMAGE = skillParams.CRAZY_DAMAGE;
        this.MOVE_SPEED = skillParams.MOVE_SPEED;
        this.dashFactor = skillParams.dashFactor;
        this.stage2SpeedFactor = skillParams.stage2SpeedFactor;
        this.minDashDistanceSqr = skillParams.minDashDistanceSqr;
        this.xpReward = skillParams.xpReward;
    }


    public EyeOfCthulhu(Level level) {
        this(TEBossEntities.EYE_OF_CTHULHU.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ENABLE_TRAILS, false);
//        builder.define(DATA_SKILL_TICK, 0);
    }

    // 定义技能类型
    MobSkill stage1_stare;
    MobSkill state1_dash;
    MobSkill switch_1_to_2;
    MobSkill stage2_stare;
    MobSkill state2_dash;

    @Override
    public void addSkills() {
        // 定义动画
        RawAnimation type1 = RawAnimation.begin().thenPlay("type_1");
        RawAnimation type1run = RawAnimation.begin().thenPlay("type_1_run");
        RawAnimation switching = RawAnimation.begin().thenPlay("switching");
        RawAnimation type2 = RawAnimation.begin().thenPlay("type_2");
        RawAnimation type2run = RawAnimation.begin().thenPlay("type_2_run");

        // 定义技能实现
        // 定格在玩家正上方
        this.stage1_stare = new MobSkill( type1, 5 * 20, 0,
                terraBossBase -> {},
                terraBossBase -> {
                    if (getTarget() == null) return;
                    lookAt(10);
                    // 生成粒子
//                    for (int i = 0; i < 10; i++) {
//                        BlockPos pos = BlockPos.containing(position());

//                        ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.TR_CRIMSON_STONE.get().defaultBlockState()),
//                                pos.getX() + 0.5F,
//                                pos.getY() + 0.75F,
//                                pos.getZ() + 0.5F,
//                                10, 0.0625F, 0.0625F, 0.0625F, 0.15F);
//                    }
                    // 生成仆从
                    spawnMinions(getTarget());
                    // 向玩家正上方移动
                    dashComponent.hangOn(getTarget(), 3, 1, MOVE_SPEED);
//                    Vec3 tar = getTarget().position().add(new Vec3(0, distanceAbove, 0));
//                    if (distanceToSqr(tar) > followMinDistance) addDeltaMovement(tar.subtract(position()).normalize().scale(MOVE_SPEED / 10));

                },
                terraBossBase -> {}
        );
        // 延迟20tick冲刺10tick
        this.state1_dash = new MobSkill( type1run, 30, 20,
                terraBossBase -> {},
                terraBossBase -> {
                    // 延迟冲刺
                    if (getTarget() == null)
                        return;
                    if (!skills.canContinue()) {
                        // 调整方向
                        lookAt(360);

                        this.addDeltaMovement(new Vec3(0, 0.02, 0));
                        // 不精准度
                        dashPos = getTarget().position().add(0, 1, 0).offsetRandom(this.getRandom(), 1);
                        dashDir = dashPos.subtract(position());
                        return;
                    }
                    if(dashPos != null && dashDir != null){
                        this.lookControl.setLookAt(dashPos);
                        // 冲刺增加伤害
                        //getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(
                        //new AttributeModifier(DASH_UUID.toString(),2, AttributeModifier.Operation.ADDITION));
                        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(DAMAGE * dashFactor);

                        this.setDeltaMovement(dashDir.normalize().scale(MOVE_SPEED * speedFactor));
                    }

                },
                terraBossBase -> {
                    // 结束冲刺移除加成
                    getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(DAMAGE);
                    if (this.getStage() == 1 ){
                        if(--stage1_dashCount <= 0) {
                            stage1_dashCount = 3;
                            skills.forceStartIndex(0);
                            return;
                        }
                    }
                }
        );
        // 转换阶段
        this.switch_1_to_2 = new MobSkill(switching, 23, 0,
                terraBossBase -> {

                    summonCD = 0;
                    summonCDAll = 7;
                    this.playSound(TESounds.HURRIED_ROARING.get());
                },
                terraBossBase -> {
                    spawnMinions(getTarget());
                    },
                terraBossBase -> {
                    // 增加属性
                    getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CRAZY_DAMAGE);

                });
        this.stage2_stare = new MobSkill(type2, 3 * 20, 0,
                terraBossBase -> {
                },
                terraBossBase -> {
                    if (getTarget() == null) return;
                    if(!this.isExpert() && getTarget().distanceTo(this) > 8 && this.getRandom().nextFloat() < 0.5f) {
                        skills.tick -= 1;
                    }
                    if(TEUtils.isFTWWorld((ServerLevel) level())){
                        if(this.getHealthPercentage() < 0.15f) { // 天顶世界接近无限冲刺
                            skills.tick += 1;
                        }
                        for(int i = 0; i < 2; i++) {
                            this.spawnMinions(getTarget()); // 天顶世界生成仆从
                        }
                    }

                    lookAt(10);

                    // 向玩家正上方移动
                    dashComponent.hangOn(getTarget(), 3, 0, MOVE_SPEED * stage2SpeedFactor);
//                    Vec3 tar = getTarget().position().add(new Vec3(0, distanceAbove, 0));
//                    if (distanceToSqr(tar) > followMinDistance) addDeltaMovement(tar.subtract(position()).normalize().scale(MOVE_SPEED * stage2SpeedFactor / 10));
                },
                terraBossBase -> {
                    // 生成冲撞次数
                    if(this.isExpert()) {
                        this.stage2_dashCount = (int) ((stage2_dashCount_base + 10 - this.getHealth() / (getMaxHealth() / 10)) * 1.5);
                        if (this.getHealth() / getMaxHealth() < 0.3f) {
                            this.stage2_dashCount_max = this.stage2_dashCount;
                        }
                    }else{
                        this.stage2_dashCount = 3;
                    }
                    getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CRAZY_DAMAGE);
                }
        );
        this.state2_dash = new MobSkill(type2run, 30, 20,
                terraBossBase -> {
                    if (getTarget() == null) return;
                    if(this.isEnhanceDash()){
                        state2_dash.timeTrigger = 10;
                        state2_dash.timeContinue = 20;
                        speedFactor = 3;
                        this.playSound(TESounds.HURRIED_ROARING.get());
                    }else {
                        state2_dash.timeTrigger = 10;
                        state2_dash.timeContinue = 20;
                        speedFactor = 2;
                        this.playSound(TESounds.ROAR.get());
                    }

                    if(this.isExpert() && distanceTo(getTarget()) < 8){
                        skills.tick -= 1;
                    }
                },
                terraBossBase -> {
                    // 延迟冲刺
                    if (getTarget() == null) return;
//                    lookAt(360);
                    boolean isEnhance = isEnhanceDash();
                    if (!skills.canContinue()) {
                        // 调整方向

                        this.addDeltaMovement(new Vec3(0, 0.02, 0));
                        float inaccuracy = (float) getTarget().getDeltaMovement().length() * 10;
                        if(isEnhance){
                            inaccuracy *= 6;// 疯狗冲刺非常不准确
                            if(this.getRandom().nextFloat() < 0.3f){ // 触发时间可以提前
                                skills.tick++;
                            }
                        }
                        // 不精准度
                        dashPos = getTarget().position().add(0, 1, 0).offsetRandom(this.getRandom(), inaccuracy);
                        dashDir = dashPos.subtract(position());
                        dashPos = dashPos.add(dashDir.normalize().scale(20));
                        //冲撞距离过小则后退
                        if(distanceToSqr(getTarget()) < minDashDistanceSqr) setDeltaMovement(dashDir.normalize().scale(-1));
                        return;
                    }else{
                        if(isEnhance){ // 疯狗冲刺时间不稳定
                            if(skills.tick > 23 && this.getRandom().nextFloat() < 0.2f){
                                skills.forceEnd();
                            }
                        }
                    }
                    if(dashPos != null && dashDir != null) {

                        this.setMotionBlurEnabled(this.isEnhanceDash());

                        this.lookControl.setLookAt(dashPos);
                        this.lookAt(EntityAnchorArgument.Anchor.EYES, dashPos);
                        // 冲刺增加伤害
                        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CRAZY_DAMAGE * dashFactor);
                        this.setDeltaMovement(dashDir.normalize().scale(MOVE_SPEED * speedFactor * stage2SpeedFactor));
                    }
                },
                terraBossBase -> {
                    // 结束冲刺移除加成
                    getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CRAZY_DAMAGE);
                    if (--stage2_dashCount <= 0) {
                        // 冲刺完
                        stage2_dashCount = stage2_dashCount_base;
                        skills.forceStartIndex(5);
                        this.setMotionBlurEnabled(false);
                    } else {
                        //继续冲刺
                        skills.forceStartIndex(6);
                    }
                }
        );
        // 添加技能序列
        addSkill(stage1_stare); // 0
        addSkill(state1_dash); // 1
        addSkill(state1_dash); // 2
        addSkill(state1_dash); // 3
        addSkill(switch_1_to_2); // 4
        addSkill(stage2_stare); // 5

        addSkill(state2_dash); // 6
    }

    private boolean isEnhanceDash(){
        return this.isExpert() && this.getHealth()/getMaxHealth()<0.3f && stage2_dashCount <= stage2_dashCount_max;
    }

    public void tick() {
         super.tick();
         if(!this.level().isClientSide && shouldLeave()){
             doLeave();
         }
         if(this.level().isClientSide){

             this.trails.update(this, this.position(), this.getXRot(), this.getYRot());
         }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof DemonEye);
    }

    private void spawnMinions(LivingEntity target) {
        if (level() instanceof ServerLevel serverLevel) {
            if (--summonCD > 0) return;
            summonCD = summonCDAll;
            DemonEye eye = TEUtils.spawnEntity(()->new DemonEye(TEMonsterEntities.DEMON_EYE.get(), serverLevel) {
                @Override
                protected boolean shouldDropLoot() {
                    return false;
                }
            }, serverLevel, position().add(getForward().normalize().scale(-1)));
            eye.minion_setOwner(this);
            eye.setHealth(3);
            eye.getAttribute(Attributes.MAX_HEALTH).setBaseValue(3);
            eye.setTarget(target);

        }
    }

    @Override
    public boolean isNoGravity(){ return true; }

    @Override
    public void changeState(){
        if(this.getStage() == 1 && this.getHealth() / getMaxHealth() < 0.5){
            this.setStage(2);
            skills.forceStartIndex(4);
            this.getAttribute(Attributes.ARMOR).setBaseValue(0); // 二阶段没有护甲
        }
        this.syncStatus(this.getStage());
    }

    @Override
    protected void initStage(int stage){
        if(stage == 2){
            skills.forceStartIndex(4);
            this.getAttribute(Attributes.ARMOR).setBaseValue(0);
        }
    }

    @Override
    public boolean shouldLeave() {
        return IAutoLeaveMob.super.shouldLeave() && level().isDay();
    }

    @Override
    public void doLeave() {
        dashComponent.setDirection(new Vec3(Math.cos(tickCount * 0.1) * 2,1,Math.sin(tickCount * 0.1) * 2));
        dashComponent.uniformMove(1);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.isExpert()){
            if(this.getHealthPercentage() < 0.4f){
                pAmount += 15;
            }
            if(this.getHealthPercentage() < 0.12f) {
                pAmount += 7;
            }
        }
        return super.hurt(pSource,pAmount);
    }
    protected BossEvent.BossBarColor getBossBarColor(){
        return BossEvent.BossBarColor.RED;
    };

    protected boolean shouldOverPlayer(){
        return true;
    }

    @Override
    public boolean isMotionBlurEnabled() {
        return this.entityData.get(DATA_ENABLE_TRAILS);
    }

    @Override
    public void setMotionBlurEnabled(boolean enabled) {
        this.entityData.set(DATA_ENABLE_TRAILS, enabled);
    }

    @Override
    public MotionBlurManager<PosRotMotionBlurContext> getMotionBlurManager() {
        return trails;
    }

}
