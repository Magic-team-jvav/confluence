package org.confluence.mod.common.entity.projectile.flail;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;

/// 滴滴怪致残者在开始收回时抛出的血肉弹。
///
/// 弹幕承受额外重力，可反弹三次并穿透两个有效目标。
public final class DripplerCripplerProjectile extends FlailAuxiliaryProjectile {
    private static final double EXTRA_GRAVITY = 0.08;
    private static final double BOUNCE_DAMPING = 0.6;
    private int bouncesLeft = 3;
    private int hitsLeft = 2;

    public DripplerCripplerProjectile(EntityType<? extends DripplerCripplerProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void afterMove() {
        setDeltaMovement(getDeltaMovement().add(0.0, -EXTRA_GRAVITY, 0.0));
    }

    @Override
    protected boolean onHitBlockAndContinue(BlockHitResult hit) {
        if (--bouncesLeft <= 0) {
            return false;
        }
        Vec3 velocity = getDeltaMovement();
        Direction direction = hit.getDirection();
        switch (direction.getAxis()) {
            case X -> setDeltaMovement(-velocity.x * BOUNCE_DAMPING, velocity.y, velocity.z);
            case Y -> setDeltaMovement(velocity.x, -velocity.y * BOUNCE_DAMPING, velocity.z);
            case Z -> setDeltaMovement(velocity.x, velocity.y, -velocity.z * BOUNCE_DAMPING);
        }
        setPos(hit.getLocation().add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.05)));
        return true;
    }

    @Override
    protected void onHitLiving(LivingEntity target) {
        if (!(getOwner() instanceof Player player)) {
            discard();
            return;
        }
        if (target.hurt(LibDamageTypes.of(level(), LibDamageTypes.SWORD_PROJECTILE, this, player), damage)) {
            LibEntityUtils.knockBackA2B(this, target, 0.15F, 0.08F);
            if (--hitsLeft <= 0) {
                discard();
            }
        }
    }
}
