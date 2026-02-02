package org.confluence.terraentity.entity.monster.demoneye;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.utils.TEUtils;

import java.util.List;

import static org.confluence.terraentity.utils.TEUtils.angleBetween;

/** 平时的AI
 * @author voila  */
public class DemonEyeSurroundTargetGoal extends Goal {
    protected final PathfinderMob mob;
    protected final RandomSource random;
    protected int locateCount = 0;  // 用来确定目标Y坐标
    protected int ticksLeft = 40;  // 每40刻无条件换目标
    public Vec3 targetPos;
    public double maxSpeed = 0.4;  // 给游荡眼球怪用的，以后估计还有别的字段

    public DemonEyeSurroundTargetGoal(PathfinderMob mob){
        this.mob = mob;
        random = mob.getRandom();
    }

    @Override
    public boolean canUse(){
        return mob.getTarget() != null && mob.getTarget().isAlive() && mob.level().isNight();
    }

    @Override
    public void start(){
        LivingEntity target = mob.getTarget();
        if(target == null) return;
        locateCount++;
        ticksLeft=40;
        mob.setDeltaMovement(mob.getDeltaMovement().with(Direction.Axis.Y, 0));
        Vec3 targetDir = mob.position().with(Direction.Axis.Y,target.position().y).vectorTo(target.position());  // 先确定水平方向
        float[] offsetAngle = TEUtils.dirToRot(targetDir);
        double offsetY = getOffsetY();
        if(random.nextInt(3) == 0){
            offsetAngle[0] += random.nextBoolean() ? 20 : -20;
        }
        Vec3 direction = Vec3.directionFromRotation(offsetAngle[1], offsetAngle[0]);
        targetPos = direction.normalize().scale(4).with(Direction.Axis.Y, offsetY).add(target.position());  // 然后设置垂直坐标
    }

    /** 按余弦函数确定Y坐标偏移 */
    public float getOffsetY(){
        float period = 6.1f;
        float radians = Mth.TWO_PI * (locateCount % period) / period;
        return 2.57f * Mth.cos(radians) + 1;
    }

    @Override
    public boolean canContinueToUse(){
        Vec3 pos = mob.position();
        return canUse()
            && Math.abs(pos.x - targetPos.x) > 0.1
            && Math.abs(pos.y - targetPos.y) > 0.1
            && Math.abs(pos.z - targetPos.z) > 0.1
            && mob.position().distanceToSqr(targetPos)>0.09 // 0.3 * 0.3
            && mob.getTarget()!=null
            && mob.getTarget().position().distanceToSqr(targetPos) < 100  // 10 * 10; canUse保证了target!=null
            && ticksLeft > 0;
    }



    @Override
    public void tick(){
        Vec3 motion = mob.getDeltaMovement();
        Vec3 targetDir = mob.position().vectorTo(targetPos).normalize().multiply(0.08,0.03,0.08); // Y轴老是冲过头，给少一点
        Vec3 targetMotion = motion.add(targetDir);
        if(angleBetween(targetDir, motion) > 15 || targetMotion.length() < maxSpeed){
            mob.setDeltaMovement(targetMotion);
        }
//        ((ServerLevel) (mob.level())).sendParticles(ParticleTypes.FLAME, targetPos.x, targetPos.y, targetPos.z, 1, 0, 0, 0, 0);
        List<Player> entities = mob.level().getEntities(EntityType.PLAYER, mob.getBoundingBox().expandTowards(targetMotion).inflate(0.15),e -> !e.isSpectator());
        for(Player player : entities){
            mob.doHurtTarget(player);
        }

        ticksLeft--;
    }
}
