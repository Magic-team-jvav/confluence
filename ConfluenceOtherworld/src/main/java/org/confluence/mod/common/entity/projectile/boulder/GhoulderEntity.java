package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.terraentity.init.TESounds;
import org.jetbrains.annotations.Nullable;

public class GhoulderEntity extends BoulderEntity {
    public GhoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
        speed = 0.4;
        maxRemoveTick = 500;
    }

    public GhoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.GHOULDER.get(), level, pos, blockState);
        speed = 0.4;
        maxRemoveTick = 500;
    }

    @Override
    protected void playRemoveSound(ServerLevel serverLevel, BlockPos pos) {
        serverLevel.playSound(null, pos, TESounds.SOUL_DEATH.get(), SoundSource.BLOCKS, 5.0F, 1.0F);
    }

    protected void moveAndUpdateNeighbors() {
        Vec3 deltaMovement = getDeltaMovement();
        move(MoverType.SELF, deltaMovement);

        Player nearestPlayer = getNearestPlayer();
        if (nearestPlayer != null && !isLookingAtMe(
                nearestPlayer, 0.5, false,
                this.getEyeY(),
                this.getY() + 0.5 * getViewScale(),
                (this.getEyeY() + this.getY()) / 2.0)
        ) {
            this.noPhysics = true;
            targetTo(nearestPlayer);
        } else {
            this.noPhysics = false;
            applyGravity();
            setDeltaMovement(getDeltaMovement().multiply(0.7, 1, 0.7));
        }


        Vec3 motion = getDeltaMovement();
        if (motion.x != deltaMovement.x || motion.y != deltaMovement.y || motion.z != deltaMovement.z) {
            updateNeighbors();
        }

    }

    @Override
    protected void rotate(Vec3 deltaMovement) {}

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {}

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void targetTo(@Nullable Entity entity) {
        Vec3 deltaMovement = getDeltaMovement();
        Vec3 vec3 = entity == null ? deltaMovement : entity.position().subtract(position()).normalize();
        float yRotTarget = (float) Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG;
        if (yRotTarget != yRotO) {
            float yRotNew = getYRot() + (yRotTarget - getYRot()) * 0.2f;
            if (Mth.abs(yRotTarget - yRotNew) < 0.05f) yRotNew = yRotTarget;
            setYRot(yRotNew);
            yRotO = getYRot();
        }
        rotate = ((float) Mth.atan2(-vec3.y, vec3.horizontalDistance()));
        rotateO = rotate;
        setDeltaMovement(vec3.scale(speed));
    }

    /* Copied from newer version of Minecraft */
    public boolean isLookingAtMe(LivingEntity entity, double tolerance, boolean scaleByDistance, double... yValues) {
        Vec3 vec3 = entity.getViewVector(1.0F).normalize();

        for (double y : yValues) {
            Vec3 targetToMe = new Vec3(this.getX() - entity.getX(), y - entity.getEyeY(), this.getZ() - entity.getZ());
            double distance = targetToMe.length();
            targetToMe = targetToMe.normalize();
            double dot = vec3.dot(targetToMe);
            if (dot > 1.0 - tolerance / (scaleByDistance ? distance : 1.0)) return true;
        }

        return false;
    }
}
