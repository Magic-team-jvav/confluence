package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

/**
 * 共用水陆移动，不决定物种的呼吸能力、陆上活动条件或攻击行为。
 */
public abstract class SwimmingCritter extends BaseCritter {
    private final MoveControl landControl;
    private final MoveControl swimControl;

    protected SwimmingCritter(EntityType<? extends SwimmingCritter> type, Level level) {
        super(type, level);
        landControl = moveControl;
        swimControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, false);
        setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public void tick() {
        MoveControl nextControl = isInWater() ? swimControl : landControl;
        if (moveControl != nextControl) {
            navigation.stop();
            moveControl = nextControl;
            moveControl.setWantedPosition(getX(), getY(), getZ(), 0.0);
            setSpeed(0.0F);
            setXxa(0.0F);
            setYya(0.0F);
            setZza(0.0F);
            if (nextControl == landControl) setXRot(0.0F);
        }
        super.tick();
    }

    @Override
    public void travel(Vec3 input) {
        if (isEffectiveAi() && isInWater()) {
            moveRelative(getSpeed(), input);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9));
        } else {
            super.travel(input);
        }
    }
}
