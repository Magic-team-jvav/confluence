package org.confluence.mod.common.entity.boss.hillofflesh;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;

public class HillOfFleshEye extends HillOfFleshPart implements RangedAttackMob {
    int shootDamage;
    int _shootDelay = 10;
    int _shootInterval = 40;
    final int __shootInterval = _shootInterval;
    int _shootCount = 3;
    int shootDelay;
    int shootCount;

    public HillOfFleshEye(HillOfFlesh parentMob, String name, float width, float height) {
        super(parentMob, name, width, height);
        this.shootDamage = LibUtils.switchByDifficulty(parentMob.level(), parentMob.blockPosition(), 8, 10, 12, 15);
    }

    @Override
    protected void tickPart(double offsetX, double offsetY, double offsetZ, int index) {
        this.findTarget();
        this.getParent().setTarget(index, this.target); // Excuse me?
        if (this.target != null && !this.level().isClientSide() && this.stareCount >= 10) {
            this.shoot(this.target);
        }
    }

    private void shoot(LivingEntity target) {
        if (--this.shootDelay <= 0) {
            --this.shootCount;

            if (this.shootCount <= 0) {
                this.shootCount = _shootCount;
                this.shootDelay = _shootInterval + this.getRandom().nextInt(20);

                this.performRangedAttack(target, 1.0f); // 最后一击增加射速
            } else {
                this.shootDelay = _shootDelay;
                this.performRangedAttack(target, 0.5f);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float v) {
        LineProj proj = TEProjectileEntities.FIRE_BOUND_PROJ.get().create(this.level());
        if (proj != null) {
            proj.setOwner(this.parentMob);
            Vec3 pos = this.getBoundingBox().getCenter();
            proj.setPos(pos.x(), pos.y(), pos.z());
            double x = target.getX() - pos.x();
            double y = target.getY() - pos.y();
            double z = target.getZ() - pos.z();
            proj.shoot(x, y, z, v, 1);
            proj.setDamage(this.shootDamage);
            this.level().addFreshEntity(proj);
            proj.setScale(parentMob.currentScale);
            proj.refreshDimensions();
        }
    }

    @Override
    protected void onParentChangeState(int state) {
        if (state == 2) {
            this._shootInterval = (int) (this.__shootInterval * 0.7f);
        }
    }
}
