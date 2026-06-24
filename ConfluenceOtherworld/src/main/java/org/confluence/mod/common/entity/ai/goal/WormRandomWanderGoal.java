package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.entity.IHeightControlMob;
import org.confluence.mod.common.entity.ai.motion.curve.Bezier3Curve;

/// 蠕虫游走ai
public class WormRandomWanderGoal<T extends Mob & IHeightControlMob> extends Goal {
    private Vec3 randomTarget;
    private int tickToChangeTarget;

    private final int _tickToChangeTarget;
    public final T worm;
    private final int jumpHeight;
    private final int landDepth;
    private final int offset;

    Curve curve;

    public WormRandomWanderGoal(T worm, int tickToChangeTarget) {
        this(worm, tickToChangeTarget, 9, 1, 20);
    }

    /**
     * @param tickToChangeTarget 一段曲线运动时间
     * @param jumpHeight 飞出地面高度
     */
    public WormRandomWanderGoal(T worm, int tickToChangeTarget, int jumpHeight, int landDepth, int offset) {
        this.worm = worm;
        this._tickToChangeTarget = tickToChangeTarget;
        this.tickToChangeTarget = _tickToChangeTarget;
        this.jumpHeight = jumpHeight;
        this.landDepth = landDepth;
        this.offset = offset;

    }

    protected Vec3 findWanderTarget() {

        Vec3 randomTarget = worm.position()
                .add(TEUtils.circle(offset, worm.getRandom().nextFloat() * 3.14f).add(0,(worm.getRandom().nextFloat() - 0.5f) * offset,0))
                .add(worm.getLookAngle().normalize().scale(10)); // 防止寻路到背后导致突然转向

        BlockPos pos = new BlockPos((int) randomTarget.x, (int) randomTarget.y, (int) randomTarget.z);
        int delta = 0;
        while (worm.level().getBlockState(pos).isAir() && pos.getY() > -65) {
            pos = pos.below();
            delta++;
        }
        float f0 = (float) (this.getBaseHeight(randomTarget.add(0, - delta, 0)) + worm.getRandom().nextIntBetweenInclusive(-3,5)); // 控制高度起伏
//        float f1 = f0 < -65 ? -130 - f0 : f0;
        randomTarget = new Vec3(randomTarget.x, f0, randomTarget.z);

        // 此时randomTarget为接触地面的点或者地面以下
        return randomTarget;
    }

    protected double getBaseHeight(Vec3 pos){ // 控制游走高度
        return worm.wrapWanderHeight(pos);
    }

    @Override
    public boolean canUse() {
        return worm.getTarget() == null && worm.tickCount > 5;
    }


    @Override
    public void start() {
        this.randomTarget = findWanderTarget();
        tickToChangeTarget = _tickToChangeTarget;
        Vec3 dir = worm.getLookAngle().normalize();
        Vec3 mid = worm.position().add(dir.scale(worm.getRandom().nextIntBetweenInclusive(5,10))); // 切线

        // 调整控制点落差
        double dy =  mid.y - worm.position().y;
        mid = mid.add(0, -dy + (dy > 0? 1 : -1) * jumpHeight, 0);

        // 调整开口方向
        if(worm.position().y > mid.y){
            // 开口方向向上
            float raiseHeight = worm.getRandom().nextIntBetweenInclusive(jumpHeight, jumpHeight + 4); // 上升高度
            randomTarget = randomTarget.add(0, worm.position().y  -  randomTarget.y + raiseHeight, 0) ;
            mid = mid.add(0, worm.position().y - this.landDepth * 1.5F  -  mid.y, 0);
        }else{
            // 开口方向向下
            float landDepth = 5 + worm.getRandom().nextIntBetweenInclusive(this.landDepth, this.landDepth + 2); // 着陆深度
            randomTarget = randomTarget.add(0, - landDepth, 0) ;
        }

        this.curve = new Bezier3Curve(worm.position(),
                mid,
                randomTarget
        );
    }

    public boolean canContinueToUse() {
        return worm.distanceToSqr(randomTarget) > 3f && tickToChangeTarget > 0;
    }


    public void stop() {
        tickToChangeTarget = _tickToChangeTarget;
    }

    public void tick() {
        --tickToChangeTarget;
        if(curve != null){
            float time = (_tickToChangeTarget - tickToChangeTarget) * 1.0f / _tickToChangeTarget;
            Vec3 target = curve.calUniformed(time);
//            System.out.println((_tickToChangeTarget - tickToChangeTarget) * 1.0f / _tickToChangeTarget);
            Vec3 dir = target.subtract(worm.position());
            Vec3 lookPos = dir.scale(20).add(worm.position());
            worm.lookAt(EntityAnchorArgument.Anchor.EYES, lookPos);
            worm.getLookControl().setLookAt(lookPos.x, lookPos.y, lookPos.z, 360, 85);
            worm.setDeltaMovement(dir.scale(0.85f)); // * 0.9防止头抽搐
        }
    }
}
