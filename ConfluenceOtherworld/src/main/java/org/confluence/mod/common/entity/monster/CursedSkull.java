package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class CursedSkull extends AbstractMonster {


    int _phase = 200;
    int phase = _phase;

    public CursedSkull(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder.setNoGravity());
        this.noPhysics = true;

    }

    public void tick() {
        super.tick();
        if (getTarget() != null) {
            phase--;
            this.lookAt(getTarget(), 10, 90);
            LivingEntity target = getTarget();
            Vec3 dir = target.getEyePosition().subtract(getEyePosition()).normalize();
            boolean aggro = phase < 80;
            if (aggro || distanceTo(target) > 5 && phase < 150) {
                float speed = aggro ? 0.05f : 0.02f;

                addDeltaMovement(dir.scale(speed));
                if (getDeltaMovement().length() > 0.5) {
                    this.setDeltaMovement(getDeltaMovement().normalize().scale(0.5));
                }

                double angle = TEUtils.angleBetween(getDeltaMovement(), dir);
                if (angle > 0.3f && aggro) {
                    this.phase = _phase;
                }

            }
        } else {
            phase = _phase;
        }

    }

    public boolean doHurtTarget(Entity entity) {
        this.phase = _phase;
        return super.doHurtTarget(entity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
}
