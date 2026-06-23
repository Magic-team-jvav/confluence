package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 抓人草
 */
public class Snatcher extends AbstractMonster{

    // 尝试生成的方向
    private static final List<Vec3> _GENERATE_DIRS = List.of(
            new Vec3(1, 0, 0), new Vec3(-1, 0, 0), new Vec3(0, 1, 0),
            new Vec3(0, -1, 0), new Vec3(0, 0, 1), new Vec3(0, 0, -1),
            new Vec3(1, 1, 0), new Vec3(-1, 1, 0), new Vec3(1, -1, 0),
            new Vec3(-1, -1, 0), new Vec3(1, 0, 1), new Vec3(-1, 0, 1),
            new Vec3(1, 0, -1), new Vec3(-1, 0, -1), new Vec3(0, 1, 1),
            new Vec3(0, -1, 1), new Vec3(0, 1, -1), new Vec3(0, -1, -1),
            new Vec3(1, 1, 1), new Vec3(-1, 1, 1), new Vec3(1, -1, 1),
            new Vec3(-1, -1, 1), new Vec3(1, 1, -1), new Vec3(-1, 1, -1),
            new Vec3(1, -1, -1), new Vec3(-1, -1, -1)
    );

    Vec3 initPos;
    Vec3 initDir;
    boolean triggered = false;
    int phase = 0;
    int _phase = 200;

    float forwardSpeed = 0.2f; // 向前速度
    float forwardFreq = 0.05f; // 向前频率
    float backSpeed = 0.1f; // 返回起始点速度
    float backLen = 5;  // 距离起始点方向的距离
    float v_speed = 5f;   // 回到起始方向的速度

    int switchTime = 100; // 切换方向的时间

    private static final EntityDataAccessor<Vector3f> DATA_TRIGGER =  SynchedEntityData.defineId(Snatcher.class, EntityDataSerializers.VECTOR3);


    public Snatcher(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder.setController((state,e)->{
            state.add(DefaultAnimations.genericIdleController(e));
        }));
        this.noPhysics = true;
        this.collisionProperties = new CollisionProperties(5,20,0.3f);
    }

    @Override
    protected void registerGoals() {

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false));

    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return distanceToSqr(entity) < 32 * 32;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public void tick(){
        super.tick();
//        if(initPos != null && !initPos.equals(Vec3.ZERO)){
//            this.setPos(initPos.x, initPos.y + Math.sin(this.tickCount * 0.2f) + 3, initPos.z);
//        }
        phase++;
        int stage = (int) (phase * 2.0F / _phase  + 1);
        if(phase >= _phase){
            phase = 0;
        }
        if(initPos!= null && initDir!= null){

            Vec3 speed = Vec3.ZERO;

            if(getTarget() != null ) {
                Vec3 targetPos = getTarget().position().add(0,getTarget().getEyeHeight() * 0.5f,0);

                this.lookControl.setLookAt(getTarget(), 200, 85);
                this.lookAt(getTarget(), 200, 85);
                // 回复到方向的垂直速度
                Vec3 c = targetPos.subtract(this.initPos);
                Vec3 a = initPos.subtract(position());
                Vec3 b = targetPos.subtract(position());

                float sp = v_speed;
                if(stage == 2){
                    sp = v_speed * 0.05f;
                }
                double s = a.dot(b);
                double c_len = initDir.subtract(targetPos).length();
                double v_len = s / c_len;
                Vec3 v_v = a.cross(b).cross(c).normalize().scale(-v_len * sp );
                speed = speed.add(v_v);
                // 朝向目标的速度
                initDir = c.normalize();
            }else{
                // 当无目标的时候，随机切换方向
                if(--switchTime <= 0){
                    switchTime = random.nextInt(200) + 100;
                    Vec3 testDir = new Vec3(Math.random() - 0.5f, Math.random() - 0.5f, Math.random() - 0.5f).normalize();
                    BlockPos testPos = BlockPos.containing(testDir.scale(5).add(position()));
                    BlockState blockState = level().getBlockState(testPos);
                    // 尝试离开墙体或虚空
                    if(blockState.isAir() && testPos.getY() > -65)
                        initDir = testDir ;
                }
            }

//            Vec3 forward = initDir.normalize().scale(forwardSpeed * Math.sin(this.tickCount * forwardFreq));
//            // 回复到初始位置的速度
//            Vec3 backPos = initPos.add(initDir.scale(backLen));
//            Vec3 v_back = backPos.subtract(position()).scale(backSpeed);

            float flag = getTarget() == null? 1 : 2;
            Vec3 forward = initDir.normalize().scale(forwardSpeed * Math.sin(this.tickCount * forwardFreq * flag));
            // 回复到初始位置的速度
            float lengthFactor = getTarget() == null? 1 : stage;
            Vec3 backPos = initPos.add(initDir.scale(backLen * lengthFactor * 0.5f * (3 + (Math.sin(this.tickCount * 0.05 * flag)))));
            Vec3 v_back = backPos.subtract(position()).scale(backSpeed);

            Vec3 finalSpeed = speed.add(forward).add(v_back);
            double len = finalSpeed.length();
            // 限制速度
            double scale = len > 0.3f? 0.3f / len : 1;
            finalSpeed = finalSpeed.scale(scale);
            this.setDeltaMovement(finalSpeed);

        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRIGGER, new Vector3f(0,0,0));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("initPos")) {
            Vector3f initPos = new Vector3f(tag.getFloat("initPosX"), tag.getFloat("initPosY"), tag.getFloat("initPosZ"));
            this.entityData.set(DATA_TRIGGER, initPos);
            this.initPos = new Vec3(initPos);
        }
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(initPos!= null) {
            tag.putFloat("initPosX", (float) initPos.x);
            tag.putFloat("initPosY", (float) initPos.y);
            tag.putFloat("initPosZ", (float) initPos.z);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(key == DATA_TRIGGER && level().isClientSide){
            this.initPos = new Vec3(this.entityData.get(DATA_TRIGGER));
        }
    }

    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        if(!level().isClientSide) {

            boolean canSurvive = false;
            List<Vec3> generateDirs = new ArrayList<>(_GENERATE_DIRS);
            Collections.shuffle(generateDirs);


            for(Vec3 dir : generateDirs){
                Vec3 vec3 = this.position();
                Vec3 vec31 = vec3.add(dir.scale(50));
                BlockHitResult result = level().clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));

                // 当根紧贴表面
                if (result.getType() == BlockHitResult.Type.BLOCK) {
                    Vec3 hitDir = Vec3.atLowerCornerOf(result.getDirection().getNormal()).scale(0.5f).add(0, 0.5, 0);
                    setInitPos(result.getBlockPos().getCenter().add(hitDir).toVector3f());
                    this.initDir = dir.normalize();
                    canSurvive = true;
                    break;
                }
            }
            if(!canSurvive){
                discard();
            }

        }
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        if(this.initPos == null){
            return super.getBoundingBoxForCulling().inflate(10);
        }
        return new AABB(this.position(), this.initPos);
    }

    public Vec3 getInitPos(){
        return initPos;
    }

    public void setInitPos(Vector3f initPos){
        this.entityData.set(DATA_TRIGGER, initPos);
        this.initPos = new Vec3(initPos);
        this.triggered = true;
    }

}
