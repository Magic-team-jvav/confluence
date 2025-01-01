package org.confluence.terraentity.entity.boss;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.BossSkill;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.utils.TEUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class EaterOfWorld extends AbstractTerraBossBase {
    private static final float MAX_HEALTHS = 54f;
    private static final float DAMAGE = 5f;//接触伤害
    private static final float projDamage = 3;

    private float segmentInternal = 1f;
    int segmentCount = 60;//体节长度
    static float turnSpeedBase = 3f;//转向速度
    static float moveSpeedBase = 0.6f;//移动速度
    float wanderPosRadius = 10;//寻点半径

    boolean genSegments = true;//是否生成体节
    boolean ifBaseHead = false;
    int genTick = 5;//生成体节延迟
    boolean shouldMove = true;
    float moveSpeed = moveSpeedBase;
    float turnSpeed = turnSpeedBase;
    Vec3 targetPos = new Vec3(0, 0, 0);
    boolean shouldFollowTarget = true;
    LivingEntity target;
    //public List<AbstractTerraBossBase>segments = new ArrayList<>();
    public List<AbstractTerraBossBase>baseSegments = new ArrayList<>();
    public List<Float>baseSegmentsHealth = new ArrayList<>();


    public enum WonderType {UP,DOWN}
    private WonderType wanderType = WonderType.DOWN;
    public static final EntityDataAccessor<Integer> DATA_SEG_COUNT = SynchedEntityData.defineId(EaterOfWorld.class, EntityDataSerializers.INT);


    public EaterOfWorld(EntityType<? extends Monster> type, Level level) {
        super(type, level,MAX_HEALTHS);
        if(!level.isClientSide){
            if(getTarget()!=null){
                this.moveTo(getTarget().position());
            }
        }
        this.noPhysics = true;
    }

    public EaterOfWorld(Level level, boolean genSegments) {
        this(TEEntities.EATER_OF_WORLD.get(),level);
        this.genSegments = genSegments;
    }

    private void genSegments(){
        Vec3 dir = this.getForward().normalize().scale(-segmentInternal);
        EaterOfWorldSegment temp = null;
        //segments.add(this);
        baseSegments.add(this);
        baseSegmentsHealth.add(this.getMaxHealth());
        for(int i=1;i<=segmentCount;i++){
            EaterOfWorldSegment newSegment = new EaterOfWorldSegment(this,level());
            newSegment.setPos(position().add(dir.scale(i*0.3)));
            newSegment.setLastSegment(Objects.requireNonNullElse(temp, this));
            temp = newSegment;
            baseSegments.add(newSegment);
            baseSegmentsHealth.add(newSegment.getMaxHealth());
            level().addFreshEntity(newSegment);
        }
        ((EaterOfWorldSegment)baseSegments.get(segmentCount)).ifTail = true;
        baseSegments.get(segmentCount).getEntityData().set(EaterOfWorldSegment.DATA_TAIL,true);
        ifBaseHead = true;
    }

    @Override
    public boolean isNoGravity(){
        return true;
    }

    @Override
    public void addSkills() {
        BossSkill<AbstractTerraBossBase> direct = new BossSkill<>(null,300,0,
                (AbstractTerraBossBase)->{
                    isDashing = true;
                    moveSpeed = moveSpeedBase;
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
                    if(TEUtils.angleBetween(getForward(),target.position().subtract(position()))<20    //角度
                            && distanceToSqr(target) < 20   //距离
                    ){
                        skills.forceEnd();
                    }

                },
                (AbstractTerraBossBase)->{

                });
        BossSkill<AbstractTerraBossBase> dash = new BossSkill<>(null,120,0);
        BossSkill<AbstractTerraBossBase> wonder=new BossSkill<>(null,120,0,
                (AbstractTerraBossBase)->{
                    //设置状态触发时目的点
                    wanderType = wanderType==WonderType.DOWN?WonderType.UP:WonderType.DOWN;
                    if(target==null)return;
                    float random1 = random.nextFloat()*360;
                    double random2 = wanderPosRadius * Math.sin(random1);
                    double random3 = wanderPosRadius * Math.cos(random1);
                    if(wanderType ==WonderType.DOWN){
                        targetPos = target.position().add(random2,-8,random3);
                    }else if(wanderType ==WonderType.UP){
                        targetPos = target.position().add(random2,8 ,random3);
                    }else{
                        targetPos = target.position().add(random1,random2,random3);
                    }
                    turnSpeed = 5;
                    moveSpeed = 0.4f;

                },
                (AbstractTerraBossBase)->{
                    shouldFollowTarget = false;
                    isDashing = false;

                    shouldMove = true;

                    //提前结束
                    if(wanderType ==WonderType.DOWN){
                        if(distanceToSqr(targetPos)<=4){
                            skills.forceEnd();
                        }
                    }
                },
                (AbstractTerraBossBase)->{
                    turnSpeed = turnSpeedBase;
                    moveSpeed = moveSpeedBase;
                }
        );

        addSkillNoAnim(wonder);
        addSkillNoAnim(direct);
        addSkillNoAnim(dash);
        addSkillNoAnim(wonder);

    }



    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        this.setAttactDamage(DAMAGE);
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
    private int discardTick = 0;
    boolean firstWander = false;
    @Override
    public void tick(){
        super.tick();
        if(!level().isClientSide){
            entityData.set(DATA_SEG_COUNT,segmentCount);
            //召唤的瞬间位置为初始值，要延迟召唤segments
            if(this.tickCount > genTick && genSegments && this.dirty){
                genSegments();
                genSegments=false;
            }

            //没有目标禁止行为
            target = getTarget();
            if(target==null) return;

            if(!firstWander){
                skills.forceStartIndex(2);
                skills.forceEnd();
                firstWander = true;
            }
            if(targetPos.x==0&&targetPos.y==0&&targetPos.z==0){
                targetPos = target.position();
            }
            //头部发射弹幕
            if(--shootTick <0){
                if(this.position().y>target.yo){
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
            if(shouldFollowTarget){
                this.lookAt(target, turnSpeed,80);

            }else{
                this.lookAtPos(targetPos,turnSpeed,80);
            }

            //移动机制
            if(shouldMove) {
                this.setPos(position().add(getForward().normalize().scale(moveSpeed)));
            }
            //中枢头刷新和重现机制
            if(ifBaseHead){
                int cur = 0;
                EaterOfWorld newHead = null;
                AbstractTerraBossBase lastSeg = null;

                for(int i=0;i<baseSegments.size();i++){
                    baseSegmentsHealth.set(i,baseSegments.get(i).getHealth());

                    if(baseSegments.get(i) instanceof EaterOfWorld  eater){
                        if(!eater.ifBaseHead || !eater.isAlive()){
                            eater.removeBossEvent();
                        }else{
                            if(target instanceof ServerPlayer player){
                                ((EaterOfWorld) baseSegments.get(i)).setBossEvent(player);
                            }
                        }
                    }
                    //死亡则跳过
                    if(baseSegmentsHealth.get(i)<=0.0){
                        if(lastSeg instanceof EaterOfWorldSegment last){
                            last.ifTail = true;
                            last.getEntityData().set(EaterOfWorldSegment.DATA_TAIL,true);
                        }else{//连续的头应该死亡
                            if(lastSeg!=null && lastSeg.isAlive())
                                lastSeg.setHealth(0.0f);
                        }
                        cur = 0;
                        continue;
                    }
                    //头
                    if(cur==0){

                        //错误体节，替换为头                                       //被区块刷新掉的重现
                        if(baseSegments.get(i) instanceof EaterOfWorldSegment || baseSegmentsHealth.get(i)>0 && baseSegments.get(i).isRemoved()){
                            newHead = new EaterOfWorld(level(),false);
                            newHead.setHealth(baseSegments.get(i).getHealth());
                            newHead.setPos(baseSegments.get(i).position());
                            newHead.setYRot(baseSegments.get(i).yRotO);
                            newHead.setXRot(baseSegments.get(i).xRotO);
                            newHead.genSegments =  false;
                            newHead.ifBaseHead = false;
                            //newHead.segments.add(newHead);
                            baseSegments.get(i).discard();
                            baseSegments.set(i,newHead);
                            level().addFreshEntity(newHead);
                        }

                        newHead = (EaterOfWorld) baseSegments.get(i);
                        lastSeg = newHead;
                    }else{//体节
                        EaterOfWorldSegment curSeg = (EaterOfWorldSegment)baseSegments.get(i);

                        //TODO 被区块刷新掉的体节重现
                        if(baseSegments.get(i).isRemoved() && tickCount % 50 == 0){
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
                        if(lastSeg instanceof EaterOfWorldSegment last){
                            last.ifTail = false;
                            last.getEntityData().set(EaterOfWorldSegment.DATA_TAIL,false);
                        }
                        lastSeg = curSeg;
                    }
                    cur++;
                }
            }
        }
    }

    @Override
    public void onRemovedFromLevel() {
        this.bossEvent.removeAllPlayers();
        if(!level().isClientSide && ifBaseHead){
            int aliveCount = 0;
            for(var n : baseSegments){
                if(n.getHealth()>0.0 && n!=this){
                    if(n instanceof EaterOfWorldSegment){
                        EaterOfWorld newHead = new EaterOfWorld(level(),false);
                        newHead.setPos(n.position());
                        transformHead(newHead);
                        level().addFreshEntity(newHead);
                        n.discard();
                    }
                    else{
                        transformHead((EaterOfWorld) n);
                    }
                    break;
                }
                if(n.isAlive()) aliveCount++;
            }
            if(aliveCount==0){
                //生成掉落物

            }
        }
        super.onRemovedFromLevel();
    }

    public void transformHead(EaterOfWorld newHead){
        newHead.setXRot(xRotO);
        newHead.setYRot(yRotO);
        newHead.setPos(position());
        newHead.genSegments = false;
        if(ifBaseHead) {
            newHead.ifBaseHead = true;
            newHead.baseSegments = baseSegments;
            newHead.baseSegmentsHealth = baseSegmentsHealth;
        }
    }

    @Override//boss条更新
    protected void customServerAiStep() {
        super.customServerAiStep();
        if(ifBaseHead){
            float health = 0;
            float maxHp = 0;
            int index = 0;
            for (AbstractTerraBossBase segment : baseSegments) {
                health += baseSegmentsHealth.get(index);
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

    @Override // 受伤音效
    protected SoundEvent getHurtSound(DamageSource damageSource) {return TESounds.ROUTINE_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }
    public void removeBossEvent(){
        this.bossEvent.removeAllPlayers();
    }

    public void setBossEvent(ServerPlayer player){
        this.bossEvent.addPlayer(player);
    }
}
