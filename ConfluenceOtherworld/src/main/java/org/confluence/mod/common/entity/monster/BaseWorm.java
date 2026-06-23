package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.entity.PartEntity;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 不可分裂的蠕虫类
 */
public abstract class BaseWorm<T extends BaseWormPart> extends AbstractMonster implements IHeightControlMob, IWorm<T> {

    protected float segInternal = 1.6f;
    protected List<T> bodySegments;
    int timeToDive = 0;

    public BaseWorm(EntityType<? extends BaseWorm> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        this.collisionProperties = new CollisionProperties(3,3,0);
        this.bodySegments = this.initParts();
        this.noPhysics = true;
        this.noCulling = true;
    }

    public abstract T createPart(int index);

    public static BaseWormPart createSimplePart(BaseWorm worm, int index){
        return new BaseWormPart(worm, index);
    }

    /**
     * 常规的地下蠕虫
     */
    public static BaseWorm<BaseWormPart> simpleWorm(EntityType<? extends BaseWorm> type, Level level, AttributeBuilder builder) {
        return new BaseWorm<>(type, level, builder) {

            @Override
            public BaseWormPart createPart(int index) {
                return createSimplePart(this, index);
            }

            @Override
            public double wrapWanderHeight(Vec3 pos){
                return Math.min(pos.y, 20);
            }

            @Override
            public boolean isAttackableHeight(float originalHeight){
                return originalHeight < 50;
            }

            protected boolean canFly(){
                return false;
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ComeAndBackDashAttackGoal<>(this, 16, this.getMoveSpeedModifier()){
            @Override
            public boolean canUse() {
                if(!this.worm.canFly()){
                    return super.canUse() && BaseWorm.this.timeToDive > 0;
                }
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new WormRandomWanderGoal<>(this, 30){
            @Override
            public boolean canUse() {
                if(!this.worm.canFly()){
                    return super.canUse() || BaseWorm.this.timeToDive < 0;
                }
                return super.canUse();
            }
        });

        this.targetSelector.addGoal(1,new AccelerateOnSeeingGoal(this,0.25f));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class,false, LivingEntity::canBeSeenAsEnemy));
    }

    protected float getMoveSpeedModifier(){
        return 1.0f;
    }

    protected boolean canFly(){
        return true;
    }

    public int getSegmentCount() {
        return 12;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.isInWall()){
            this.timeToDive = 200;
        }else{
            --this.timeToDive;
        }
        if(!isAlive()) {
            this.noPhysics = false;
            addDeltaMovement(new Vec3(0,-0.05f,0));
//            return;
        }

        this.tickWormMove();

    }

    @Override
    public void tickWormMove() {
        for (int i = 0; i < this.bodySegments.size(); i++) {

            Entity leader = i == 0 ? this : this.bodySegments.get(i - 1);
            BaseWormPart cur = this.bodySegments.get(i);

            cur.tickPart(leader, this.segInternal);

        }
    }

    @Override
    protected void tickDeath() {
        if(this.onGround()) {
            super.tickDeath();
        }
    }
    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if(this.getHealth() <= 0) return false;
        return super.hurt(source, amount);
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return IWorm.super.getWormParts();
//        return bodySegments.toArray(new PartEntity[0]);
    }

    @Override
    public void die(DamageSource damageSource) {
        this.setDeltaMovement(0,0,0);
        super.die(damageSource);

    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.recreateWormFromPacket();
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public boolean isInWall() {
        float f = this.getDefaultDimensions(Pose.STANDING).width() * 0.8F;
        AABB aabb = AABB.ofSize(this.getEyePosition(), (double)f, 1.0E-6, (double)f);
        return BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
            BlockState blockstate = this.level().getBlockState(p_201942_);

            return !blockstate.isAir() && blockstate.isSuffocating(this.level(), p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
        });
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.is(DamageTypes.IN_WALL);
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        this.onWormRemovedFromLevel();
    }

    @Override
    public double wrapWanderHeight(Vec3 pos){
        return pos.y;
    }

    @Override
    public boolean isAttackableHeight(float originalHeight){
        return true;
    }

    @Override
    public List<T> getBodySegments() {
        return this.bodySegments;
    }

}
