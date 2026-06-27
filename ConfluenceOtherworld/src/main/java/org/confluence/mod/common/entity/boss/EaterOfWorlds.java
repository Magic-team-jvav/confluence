package org.confluence.mod.common.entity.boss;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.TaskScheduler;
import org.confluence.terraentity.api.entity.IHeightControlMob;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.ai.fsm.MobSkill;
import org.confluence.terraentity.entity.ai.goal.WormRandomWanderGoal;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.CameraShakeData;
import org.confluence.terraentity.utils.CameraShakeManager;
import org.confluence.terraentity.utils.TEUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 世吞
 */
public class EaterOfWorlds extends AbstractTerraBossBase implements Boss, IHeightControlMob.Empty {

    final float projDamage;
    final int shootInterval;

    private final float segmentInternal;
    int segmentCount;//体节长度
    final float turnSpeedBase;//转向速度
    final float moveSpeedBase;//移动速度
    final float wanderPosRadius;//寻点半径

    boolean genSegments = true;//是否生成体节
    boolean ifBaseHead = false;
    boolean truthDie = false;
    int genTick = 10;//生成体节延迟
    boolean shouldMove = false;
    float moveSpeed;
    float turnSpeed;
    Vec3 targetPos = new Vec3(0, 0, 0);
    boolean shouldFollowTarget = true;
    LivingEntity target;
    //public List<AbstractTerraBossBase>segments = new ArrayList<>();
    public List<AbstractTerraBossBase>baseSegments = new CopyOnWriteArrayList<>();
    public List<Float>baseSegmentsHealth = new CopyOnWriteArrayList<>();

    TaskScheduler generator;

    public enum WonderType {UP,DOWN}
    private WonderType wanderType = WonderType.DOWN;
    public static final EntityDataAccessor<Integer> DATA_SEG_COUNT = SynchedEntityData.defineId(EaterOfWorlds.class, EntityDataSerializers.INT);
    SkillParams skillParams;

    public EaterOfWorlds(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        if(!level.isClientSide){
            if(getTarget()!=null){
                this.moveTo(getTarget().position());
            }
        }
        this.noPhysics = true;
        this.xpReward = 30;

        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.EATER_OF_WORLDS_PARAMS);
        this.projDamage = skillParams.projDamage;
        this.turnSpeedBase = skillParams.turnSpeed;
        this.moveSpeedBase = skillParams.moveSpeed;
        this.wanderPosRadius = skillParams.wanderPosRadius;
        this.segmentInternal = skillParams.segmentInternal;
        this.segmentCount = skillParams.segmentCount;

        this.moveSpeed = moveSpeedBase;
        this.turnSpeed = turnSpeedBase;
        this.xpReward = skillParams.xpReward;
        this.shootInterval = skillParams.shootInterval;

    }

    public EaterOfWorlds(Level level, boolean genSegments) {
        this(TEBossEntities.EATER_OF_WORLDS.get(),level);
        this.genSegments = genSegments;
    }

    @Override
    protected void registerRandomStrollGoal(){
        if(ServerConfig.BOSS_KEEP_WANDERING.get()) {
            this.goalSelector.addGoal(10, new WormRandomWanderGoal<>(this, 80, 40, 20, 40){
                @Override
                public boolean canUse() {
                    return super.canUse() || skills.index == 0;
                }
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && skills.index == 0;
                }
            });
        }
    }

    int getXpReward(){
        return xpReward;
    }

    public record SkillParams(int segmentCount, float projDamage, float turnSpeed, float moveSpeed, float wanderPosRadius, float segmentInternal,
                              int xpReward, int shootInterval) {
        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.INT.fieldOf("segment_count").forGetter(SkillParams::segmentCount),
                Codec.FLOAT.fieldOf("proj_damage").forGetter(SkillParams::projDamage),
                Codec.FLOAT.fieldOf("turn_speed").forGetter(SkillParams::turnSpeed),
                Codec.FLOAT.fieldOf("move_speed").forGetter(SkillParams::moveSpeed),
                Codec.FLOAT.fieldOf("wander_search_pos_radius").forGetter(SkillParams::wanderPosRadius),
                Codec.FLOAT.fieldOf("segment_internal").forGetter(SkillParams::segmentInternal),
                Codec.INT.fieldOf("xp_reward_per_segment").forGetter(s->s.xpReward),
                Codec.INT.fieldOf("shoot_interval").forGetter(SkillParams::shootInterval)
        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams(){
            return new SkillParams(60,5,2,0.5f,10,2.8f,
                    30, 200);
        }
    }

    private void genSegments(){
        Vec3 dir = this.getForward().multiply(1,0,1).normalize().scale(-segmentInternal);
        final EaterOfWorldsSegment[] temp = {null};
        //segments.add(this);
        baseSegments.add(this);
        baseSegmentsHealth.add(this.getMaxHealth());

        Vec3 lastPos = position();
        this.generator = new TaskScheduler(this.tickCount);
        for(int i=1;i<=segmentCount;i++){
            Vec3 neoDir = dir.yRot((0.2f - i * 0.08f / segmentCount) * i );
            lastPos = lastPos.add(neoDir);

            Vec3 finalLastPos = lastPos;
            int finalI = i;
            generator.schedule(()->{
                if(this.isAlive()) {
                    EaterOfWorldsSegment newSegment = TEUtils.spawnEntity(() -> new EaterOfWorldsSegment(this, level()), (ServerLevel) level(),
                            finalLastPos.add(0, finalI * -0.3, 0));
                    if (newSegment != null) {
                        newSegment.setLastSegment(Objects.requireNonNullElse(temp[0], this));
                        temp[0] = newSegment;
                        baseSegments.add(newSegment);
                        baseSegmentsHealth.add(newSegment.getMaxHealth());
                        if (finalI == segmentCount) {
                            ((EaterOfWorldsSegment) baseSegments.get(segmentCount)).ifTail = true;
                            baseSegments.get(segmentCount).getEntityData().set(EaterOfWorldsSegment.DATA_TAIL, true);
                            ifBaseHead = true;
                        }
                    }
                }
            }, i);
        }

        Boss.sendBossSpawnMessage(this);
    }

    @Override
    public boolean isNoGravity(){
        return true;
    }



    @Override
    public void addSkills() {
        MobSkill<AbstractTerraBossBase> direct = new MobSkill<>(null,300,0,
                (AbstractTerraBossBase)->{
                    isDashing = true;

                    if(this.isFtw()) moveSpeed = moveSpeedBase * 1.5f; // ftw 神吞的冲刺加速
                    else moveSpeed = moveSpeedBase * 1.1f;

                    turnSpeed = 5F;
                    shouldMove = true;
                    shouldFollowTarget=true;
                },
                (AbstractTerraBossBase)->{
                    if(target==null) {
                        target = getTarget();
                        return;
                    }
                    //设置触发条件，否则取消tick
                    if(TEUtils.angleBetween(getForward(),target.position().subtract(position()))<Math.PI / 8    //角度
                            && distanceToSqr(target) < 20   //距离
                    ){
                        skills.forceEnd();
                    }
                    targetPos = target.position();

                },
                (AbstractTerraBossBase)->{

                });
        MobSkill<AbstractTerraBossBase> dash = new MobSkill<>(null,120,0);
        MobSkill<AbstractTerraBossBase> wonder=new MobSkill<>(null,120,0,
                (AbstractTerraBossBase)->{
                    //设置状态触发时目的点
                    wanderType = wanderType==WonderType.DOWN?WonderType.UP:WonderType.DOWN;
                    target = getTarget();
                    if(target==null)return;
                    float random1 = random.nextFloat()*360;
                    double random2 = wanderPosRadius * Math.sin(random1);
                    double random3 = wanderPosRadius * Math.cos(random1);
                    double h = 10 + random.nextFloat() * 4;
                    if(wanderType ==WonderType.DOWN){
                        targetPos = target.position().add(random2,-h,random3);
                    }else if(wanderType ==WonderType.UP){
                        targetPos = target.position().add(random2,h*0.5 ,random3);
                    }else{
                        targetPos = target.position().add(random1,random2,random3);
                    }
                    turnSpeed = 2;
                    moveSpeed = 0.4f;
                    shouldMove = false;
                },
                (AbstractTerraBossBase)->{
                    shouldFollowTarget = false;
                    isDashing = false;
//                    if(target == null ) return;

//                    if(targetPos.distanceToSqr( target.position()) > 25 * 25){
//                        float random1 = random.nextFloat()*360;
//                        double random2 = wanderPosRadius * Math.sin(random1);
//                        double random3 = wanderPosRadius * Math.cos(random1);
//                        double h = 10 + random.nextFloat() * 4;
//                        if(wanderType ==WonderType.DOWN){
//                            targetPos = target.position().add(random2,-h,random3);
//                        }else if(wanderType ==WonderType.UP){
//                            targetPos = target.position().add(random2,h*0.5 ,random3);
//                        }else{
//                            targetPos = target.position().add(random1,random2,random3);
//                        }
//                    }
//
//                    //提前结束
//
//                    if(distanceToSqr(targetPos)<=16){
//                        skills.forceEnd();
//                    }


                },
                (AbstractTerraBossBase)->{
                    turnSpeed = turnSpeedBase;
                    moveSpeed = moveSpeedBase;
                }
        );

        addSkill(wonder);
        addSkill(direct);
        addSkill(dash);
//        addSkillNoAnim(wonder);

    }



    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
         if(key==DATA_SEG_COUNT){
            segmentCount = entityData.get(DATA_SEG_COUNT);
         }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SEG_COUNT, 0);
    }

    @Override
    public boolean shouldBeSaved(){
        return false;
    }

    private int shootTick = 0;
    private int shootTickBase = 20;
    boolean isDashing = false;
    boolean firstWander = false;

    @Override
    public int getMaxHeadXRot() {
        return 85;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if(!level().isClientSide){
            entityData.set(DATA_SEG_COUNT,segmentCount);
            //召唤的瞬间位置为初始值，要延迟召唤segments
            if(this.tickCount > genTick && genSegments && this.dirty){
                genSegments();
                genSegments=false;
                final TargetingConditions attackTargeting = TargetingConditions.forNonCombat().range(64.0);
                level().getNearbyPlayers(attackTargeting,this,this.getBoundingBox().inflate(200))
                        .forEach(p->bossEvent.addPlayer((ServerPlayer) p));
                bossEvent.setProgress(1);

                CameraShakeManager.addCameraShake(new CameraShakeData(
                    200,
                    this.position(), 30));

            }
            if(this.generator != null){
                this.generator.tick(1);
                if(generator.getPendingTaskCount() == 0){
                    this.generator = null;
                }
            }

            //没有目标禁止行为
            target = getTarget();
            if(targetPos!=null) targetPos = targetPos.add(0,0.05,0);

            if(target!=null) {
                if (!firstWander) {
                    skills.forceStartIndex(2);
                    skills.forceEnd();
                    firstWander = true;
                }
                if (targetPos!=null&& targetPos.x == 0 && targetPos.y == 0 && targetPos.z == 0) {
                    targetPos = target.position();
                }
                //头部发射弹幕
                if (--shootTick < 0) {
                    if (this.position().y > target.yo) {
                        shootTick = shootTickBase;
                        //todo
//                    BaseBulletEntity bullet = new BaseBulletEntity(this,level(),EMERALD){
//                        @Override
//                        public boolean canAttack(Entity entity) {
//                            if(entity instanceof AbstractTerraBossBase) return false;
//                            return super.canAttack(entity);
//                        }
//                    };
//                    bullet.setPos(position());
//                    bullet.setDamage(projDamage[difficultyIdx]);
//                    bullet.shoot(target.getX()-getX(),target.getY()+1-getY(),target.getZ()-getZ(),2F,1);
                        //level().addFreshEntity(bullet);
                    }
                }
                //转向机制
                if (shouldFollowTarget) {
                    this.lookAt(target, turnSpeed, 80);

                } else if(skills.index != 0){
                    this.lookAtPos(targetPos, turnSpeed, 80);
                }

                //移动机制
                if (shouldMove && this.isAlive()) {
                    this.setPos(position().add(getForward().normalize().scale(moveSpeed)));
                }

            }
            //中枢头刷新和重现机制
            if(ifBaseHead){
                if(tickCount < 50){//forge 奇怪的bug，延迟加boss条

                    if(level() instanceof  ServerLevel serverLevel){
                        var players = serverLevel.players();
                        for(var p : players){
                            if(p.distanceToSqr(position())<100*100){
                                bossEvent.addPlayer(p);
                            }
                            else bossEvent.removePlayer(p);
                        }
                    }

                }
                int cur = 0;
                EaterOfWorlds newHead = null;
                AbstractTerraBossBase lastSeg = baseSegments.getFirst();

                int aliveCount = 0;
                for(int i=0;i<baseSegments.size();i++){
                    var current = baseSegments.get(i);
                    baseSegmentsHealth.set(i,current.getHealth());
                    if(current.isAlive())
                        aliveCount++;
                    if(current instanceof EaterOfWorlds eater){
                        if(!eater.ifBaseHead){
                            // 普通的头
                            if(!eater.isAlive())
                                eater.removeBossEvent();
                            else{
                                // 给分段目标
                                if(eater.getTarget() == null || !eater.getTarget().isAlive()){
                                    eater.setTarget(target);
                                }
                            }
                        }
                    }

                    // 给自己设置目标
                    if(current instanceof EaterOfWorldsSegment seg)
                        if(current.isAlive() && (this.getTarget() == null || !this.getTarget().isAlive())){
                            LivingEntity tar = seg.getLastHurtByMob();
                            if(tar != null && tar.isAlive() && tar.canBeSeenAsEnemy()){
                                this.setTarget(seg.getLastHurtByMob());
                            }
                        }

                    //死亡则跳过
                    if(baseSegmentsHealth.get(i)<=0.0){
                        if(lastSeg instanceof EaterOfWorldsSegment last){
                            last.ifTail = true;
                            last.getEntityData().set(EaterOfWorldsSegment.DATA_TAIL,true);
                        }else{//连续的头应该死亡
                            DamageSource source = current.getLastDamageSource();
                            if(lastSeg!=null && lastSeg.isAlive()) {
                                if(source!=null){
                                    lastSeg.setHealth(0.0f);
                                    lastSeg.die(source);
                                }
                                else{
                                    lastSeg.setHealth(0.0f);
                                    lastSeg.die(this.getLastDamageSource()==null?damageSources().generic():this.getLastDamageSource());
                                }
                            }
                        }
                        cur = 0;
                        continue;
                    }
                    //头
                    if(cur==0){

                        //错误体节，替换为头                                       //被区块刷新掉的重现
                        if(current instanceof EaterOfWorldsSegment || baseSegmentsHealth.get(i)>0 && current.isRemoved()){
                            newHead = TEUtils.spawnEntity(()->new EaterOfWorlds(level(),false), (ServerLevel)level(),current.position());
                            newHead.setHealth(current.getHealth());
                            newHead.setYRot(current.yRotO);
                            newHead.setXRot(current.xRotO);
                            newHead.genSegments =  false;
                            newHead.ifBaseHead = false;
                            //newHead.segments.add(newHead);
                            current.discard();
                            baseSegments.set(i,newHead);
                        }

                        newHead = (EaterOfWorlds) baseSegments.get(i);
                        lastSeg = newHead;
                    }else{//体节
                        EaterOfWorldsSegment curSeg = (EaterOfWorldsSegment)current;

                        //TODO 被区块刷新掉的体节重现
                        if(current.isRemoved()){
/*
                            curSeg = new EaterOfWorld_Segment(newHead,level());
                            curSeg.setPos(newHead.position());
                            level().addFreshEntity(curSeg);
*/
                        }
//                        if(newHead.segments.size()==cur){
//                            newHead.segments.add(curSeg);
//                        }
                        curSeg.head = newHead;
                        curSeg.lastSegment = lastSeg;
                        if(lastSeg instanceof EaterOfWorldsSegment last){
                            last.ifTail = false;
                            last.getEntityData().set(EaterOfWorldsSegment.DATA_TAIL,false);
                        }
                        lastSeg = curSeg;
                    }
                    cur++;
                }

                if(aliveCount == 1) {
                    if (getLastDamageSource() != null)
                        die(getLastDamageSource());
                    else
                        hurt(damageSources().generic(), 999f);
                }
//                Entity last = baseSegments.getLast();
//                if(last.isAlive() && last instanceof EaterOfWorlds worlds){
//                    worlds.hurt(damageSources().generic(), 999f);
//                }
                // todo 尾部单独死亡
//                if(baseSegments.getLast().isAlive() && !baseSegments.get(baseSegments.size() - 2).isAlive()){
//                    baseSegments.getLast().setHealth(0.0f);
//                    DamageSource source = this.getLastDamageSource()==null?damageSources().generic():this.getLastDamageSource();
//                    baseSegments.getLast().die(source);
//                    if(! baseSegments.get(baseSegments.size() - 2).isRemoved())
//                        baseSegments.get(baseSegments.size() - 2).die(source);
//                }
            }
        }
    }

    @Override
    public void onRemovedFromLevel() {
        this.bossEvent.removeAllPlayers();
        if(!level().isClientSide && ifBaseHead){

            // 区块卸载时防止读写冲突
            var entityies = ((ServerLevel)level()).getPlayers(e->distanceToSqr(e) < 200*200,200);
            if(entityies.isEmpty()){
                super.onRemovedFromLevel();
                return;
            }
            int aliveCount = 0;
//            if(discardTick <= DISCARD_TICK) {
                for (var n : baseSegments) {
                    if (n == null || !n.isAlive()) continue;
                    aliveCount++;
                    if (n instanceof EaterOfWorlds nn && n.getHealth() > 0.0 && n != this) {
                        transformHead(nn);
                        break;
                    }
                }
                // 延迟转换头导致的没有发送死亡事件
                if (!truthDie && aliveCount == 0) {
                    super.die(getLastDamageSource() == null ? damageSources().magic() : getLastDamageSource());
                }
//            }
            // 头未传递，所有的都应该消失
            if(ifBaseHead){
                for (var n : baseSegments) {
                    if (n == null || !n.isAlive() || n != this) continue;
                    n.discard();
                }
            }
        }
        super.onRemovedFromLevel();
    }

    public void transformHead(EaterOfWorlds newHead){
//        newHead.setXRot(xRotO);
//        newHead.setYRot(yRotO);
//        newHead.setPos(position());
//        newHead.genSegments = false;
        if(ifBaseHead) {
            newHead.ifBaseHead = true;
            newHead.truthDie = truthDie;
//            newHead.bossEvent = this.bossEvent;
            newHead.baseSegments = baseSegments;
            newHead.baseSegmentsHealth = baseSegmentsHealth;
            this.ifBaseHead = false;
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof EaterOfWorldsSegment)  && !(target instanceof EaterOfWorlds);
    }

    @Override//boss条更新
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(ifBaseHead){
            float health = 0;
            float maxHp = 0;
            int index = 0;
            for (AbstractTerraBossBase segment : baseSegments) {
                health += segment.getHealth();
                maxHp += segment.getMaxHealth();
                index++;
            }
            this.bossEvent.setProgress(health / maxHp);
        }
    }
    @Override // boss条显示
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (ifBaseHead) this.bossEvent.addPlayer(player);
    }

    @Override
    public boolean shouldShowBossBar(){
        return false;
    }

    @Override // boss条消失
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    public void removeBossEvent(){
        this.bossEvent.removeAllPlayers();
    }

    public void setBossEvent(ServerPlayer player){
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void die(DamageSource damageSource) {
        if(ifBaseHead) {
            int aliveCount = 0;
            for (var n : baseSegments) {
                if (n == null || !n.isAlive()) {
                    if (n != null) {
                        n.bossEvent.removeAllPlayers();
                    }
                    continue;
                }
                aliveCount++;
            }
            if (aliveCount == 0 && !truthDie) {
                //生成掉落物
                this.bossEvent.removeAllPlayers();
                truthDie = true;
                super.die(damageSource);
            }else{
                // 生成体节掉落物
                if(level() instanceof ServerLevel serverLevel){
                    EaterOfWorldsSegment seg = new EaterOfWorldsSegment(this,level());
                    seg.setPos(position());
                    seg.die(damageSource);
                }
            }
        }else{
            // 生成体节掉落物
            if(level() instanceof ServerLevel serverLevel){
                EaterOfWorldsSegment seg = new EaterOfWorldsSegment(this,level());
                seg.setPos(position());
                seg.die(damageSource);
            }
        }
    }


    @Override
    public boolean isMainBody(){
        return ifBaseHead;
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor(){
        return BossEvent.BossBarColor.PURPLE;
    };

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.is(DamageTypes.LAVA) || source.is(DamageTypes.DROWN);
    }
}
