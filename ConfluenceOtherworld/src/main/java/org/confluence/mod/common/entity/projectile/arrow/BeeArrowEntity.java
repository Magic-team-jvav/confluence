package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.common.track.variant.BasisTrack;
import org.jetbrains.annotations.Nullable;

public class BeeArrowEntity extends BaseArrowEntity {
    ITrackType trackType;

    public BeeArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        trackType = new BasisTrack(30, 0.2f);
        this.pickup = Pickup.DISALLOWED;
    }

    public BeeArrowEntity(EntityType<? extends AbstractArrow> pEntityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(pEntityType, owner, pickupItemStack, firedFromWeapon);
        trackType = new BasisTrack(30, 0.2f);
        this.pickup = Pickup.DISALLOWED;
    }


    @Override
    public double getDefaultGravity() {return 0;}

    @Override
    protected int getAutoDiscardTick() {return 50;}

    @Override
    public void tick() {
        super.tick();

        if (!this.inGround && getOwner() != null) {
            LivingEntity target = LibEntityUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize().scale(10)), level(), this, 20, 30, this::canHitEntity);
            if (target != null) {
                Vec3 motion = getDeltaMovement();
                Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(position());
                double angle = LibMathUtils.angleBetween(motion, dir);
                Vec3 movement = trackType.calDeltaMovement(getDeltaMovement(), dir, angle);
                setDeltaMovement(movement);
            }
        }
        Vec3 vec3 = getDeltaMovement();
        Vec3 pos = position();
        move(MoverType.SELF, vec3);
        Vec3 motion = getDeltaMovement();

        if (motion.x != vec3.x || motion.y != vec3.y || motion.z != vec3.z) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            setDeltaMovement(motion);
        } else {
            setPos(pos);
        }
    }
}
