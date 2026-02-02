package org.confluence.terraentity.entity.boss;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.api.entity.IAngryMob;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.ai.fsm.MobSkill;
import org.confluence.terraentity.entity.ai.motion.DashComponent;
import org.confluence.terraentity.entity.monster.LittleHornet;
import org.confluence.terraentity.entity.proj.LineProj;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.TEUtils;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 蜂后
 */
public class QueenBee extends AbstractTerraBossBase implements Boss, IAngryMob {

    private final int summonBeeInterval;
    private final int summonProjInterval;
    private final float dashSpeedModifier;
    private final float angryDashSpeedModifier;
    private final float dashMaxRangeSqr;

    DashComponent dashComponent;
    public int minionCount = 0;
    final int _minionCount;
    public static final EntityDataAccessor<Boolean> DATA_ANGRY = SynchedEntityData.defineId(QueenBee.class, EntityDataSerializers.BOOLEAN);
    SkillParams skillParams;
    public QueenBee(EntityType<? extends Monster> type, Level level) {
        super(type, level);

        this.collisionProperties.setDetectInterval(1);
        this.noPhysics = true;

        if (ServerConfig.bossNoPhysics())
            this.noPhysics = true;

        this.dashComponent = new DashComponent(this);
        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.QUEEN_BEE_PARAMS);
        this.xpReward = skillParams.xpReward;
        this.summonBeeInterval = skillParams.summonBeeInterval;
        this.summonProjInterval = skillParams.summonProjInterval;
        this.dashSpeedModifier = skillParams.dashSpeedModifier;
        this.angryDashSpeedModifier = skillParams.angryDashSpeedModifier;
        this.dashMaxRangeSqr = skillParams.dashMaxRange * skillParams.dashMaxRange;
        this._minionCount = skillParams.maxMinionCount;
    }

    public QueenBee(Level level) {
        this(TEBossEntities.QUEEN_BEE.get(), level);
    }

    public record SkillParams(int xpReward, int summonBeeInterval, int summonProjInterval, int maxMinionCount,
                              float dashSpeedModifier, float angryDashSpeedModifier, float dashMaxRange
                              ) {
        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("xp_reward").forGetter(SkillParams::xpReward),
                Codec.INT.fieldOf("summon_bee_interval").forGetter(SkillParams::summonBeeInterval),
                Codec.INT.fieldOf("summon_proj_interval").forGetter(SkillParams::summonProjInterval),
                Codec.INT.fieldOf("max_minion_count").forGetter(SkillParams::maxMinionCount),
                Codec.FLOAT.fieldOf("dash_speed_modifier").forGetter(SkillParams::dashSpeedModifier),
                Codec.FLOAT.fieldOf("angry_dash_speed_modifier").forGetter(SkillParams::angryDashSpeedModifier),
                Codec.FLOAT.fieldOf("dash_max_range").forGetter(SkillParams::dashMaxRange)
        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams(){
            return new SkillParams(1500,10, 10, 10,
                    2f, 1.5f,
                    15);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANGRY, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    MobSkill<QueenBee> first_spawn;
    MobSkill<QueenBee> idle;
    MobSkill<QueenBee> summon_bee;
    MobSkill<QueenBee> summon_proj;

    MobSkill<QueenBee> pre_dash_idle;
    MobSkill<QueenBee> pre_dash;
    MobSkill<QueenBee> dash;

    RawAnimation wing = RawAnimation.begin().thenPlay("wing");

    @Override
    public void addSkills() {
        RawAnimation idle_animation = RawAnimation.begin().thenPlay("idle");
        RawAnimation summon_animation = RawAnimation.begin().thenPlay("summon");
        RawAnimation pre_dash_animation = RawAnimation.begin().thenPlay("pre_dash");
        RawAnimation dash_animation = RawAnimation.begin().thenPlay("dash");


        first_spawn = new MobSkill<QueenBee>(summon_animation, 50, 0)
                .onTick(e->{
                    if(e.tickCount > 50)
                        skills.forceStartIndex(1);
                })
        ;
        idle = new MobSkill<QueenBee>(idle_animation, 50, 0)
                .onInit(e->{
                    if(e.target != null) {
                        dashComponent.setDirection(e.target.position().subtract(e.position()).normalize());
                    }
                })
                .onTick(e->{
                    if(e.target != null) {
                        lookAt(10);
//                    dashComponent.hangOn(getTarget(), 5, 1.5f, getMoveSpeed());
                        dashComponent.uniformMove(getMoveSpeed());

                        if (e.skills.tick > 10 && e.distanceToSqr(e.target) > e.dashMaxRangeSqr) {
                            e.skills.forceEnd();
                        }
                    }
                })
        ;

        summon_bee = new MobSkill<QueenBee>(summon_animation, 60, 10)
                .onTick(e->{
                    if(e.minionCount >= this._minionCount){
                        skills.forceEnd();
                        return;
                    }
                    lookAt(10);
                    dashComponent.hangOn(getTarget(), 5, 4, getMoveSpeed());
                    if(skills.tick % this.summonBeeInterval == 0) {

                        LittleHornet bee = TEUtils.spawnEntity(TEMonsterEntities.LITTLE_HORNET.get(), (ServerLevel) level(), e.position());
                        if (bee!=null) {
                            bee.minion_setOwner(e);
                            bee.setPos(e.position());
                            bee.setYRot(e.getYRot());
                        }
                    }
                })
        ;
        summon_proj = new MobSkill<QueenBee>(summon_animation, 60, 10)
                .onTick(e->{
                    LivingEntity target = e.getTarget();
                    if(target!=null){
                        lookAt(10);
                        if(position().y < target.position().y + 2) addDeltaMovement(new Vec3(0,0.02f,0));
                        if( skills.tick % this.summonProjInterval ==0) {
                            LineProj proj = TEProjectileEntities.BEE_STICK_PROJ.get().create(level());
                            if (proj!=null) {
                                proj.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                                proj.setOwner(e);
                                proj.setPos(e.position());
                                proj.addEffect(new MobEffectInstance(MobEffects.POISON, 100, isAngry() ? 1 : 0));
                                Vec3 dir = target.getEyePosition().subtract(e.position());
                                proj.shoot(dir.x, dir.y, dir.z, 1, 5f);
                                level().addFreshEntity(proj);
                            }
                        }
                    }
                    // 低血量减少弹幕次数
                    if(getHealthPercentage() < 0.3f && skills.tick > 50){
                        skills.forceEnd();
                    }
                })
        ;

        pre_dash_idle = new MobSkill<QueenBee>(idle_animation, 20, 0)
                .onTick(e->{
                    if(target== null) return;
                    if(
                            distanceToSqr(target) > 10 * 10 ||
                            Math.abs(target.getY() - e.getY()) > 2 ||
                            Math.abs(this.getXRot()) > 10
                    ){
                        if(this.isExpert() && random.nextBoolean())
                            skills.tick--;
                    }

                    dashComponent.hangOn(getTarget(), 5, 0, getMoveSpeed() * 1.2f);
                    lookAt(10);
                })
        ;
        pre_dash = new MobSkill<QueenBee>(pre_dash_animation, 15, 0)
                .onTick(e->{
                    setDeltaMovement(0,0,0);
                    if(target!=null) {
//                        // 预判冲
                        dashComponent.setPredictDirection(target);
                        dashComponent.lookAtDirection();
                    }
                })
                .onOver(e->{
                    if(getTarget()!=null) {
                        // 只在水平位置冲刺
                        Vec3 pos = dashComponent.getDirection();
                        dashComponent.setDirection(new Vec3(pos.x, 0, pos.z));
                    }
                })

        ;
        dash = new MobSkill<QueenBee>(dash_animation, 50, 0)

                .onTick(e->{
                    if(getTarget() == null) return;
                    dashComponent.uniformMove(getMoveSpeed() * this.dashSpeedModifier
                            * (isAngry() && this.isExpert()? this.angryDashSpeedModifier : 1f));
                    if(distanceToSqr(target) > this.dashMaxRangeSqr && skills.tick > 20) skills.forceEnd();

                })
        ;

        addSkill(first_spawn);
        addSkill(idle);
        addSkill(summon_bee);
        addSkill(summon_proj);

        addSkill(pre_dash_idle);
        addSkill(pre_dash);
        addSkill(dash);

        addSkill(pre_dash_idle);
        addSkill(pre_dash);
        addSkill(dash);

        addSkill(pre_dash_idle); // 10
        addSkill(pre_dash);
        addSkill(dash);

        addSkill(pre_dash_idle);
        addSkill(pre_dash);
        addSkill(dash);
    }

/* anger */

    @Override
    public boolean isAngry() {
        return this.getEntityData().get(DATA_ANGRY);
    }

    @Override
    public boolean shouldAnger() {
        return !level().getBiome(blockPosition()).is(Biomes.JUNGLE);
    }

    @Override
    public void setAngry(boolean angry) {
        this.getEntityData().set(DATA_ANGRY, angry);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            if(getTarget() == null){
                skills.forceStartIndex(1);
            }
            setAngry(shouldAnger());
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof LittleHornet);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(fsmAnimationController(),
                new AnimationController<GeoAnimatable>(this, "wing",10, state->{
                    state.setAnimation(wing);
                    return PlayState.CONTINUE;
                })
        );
    }


    @Override
    public boolean isNoGravity(){ return true; }

    protected BossEvent.BossBarColor getBossBarColor(){
        return BossEvent.BossBarColor.YELLOW;
    };

    protected boolean shouldOverPlayer(){
        return true;
    }
}
