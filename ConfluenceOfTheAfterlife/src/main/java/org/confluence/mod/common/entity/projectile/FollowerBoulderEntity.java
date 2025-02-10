package org.confluence.mod.common.entity.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class FollowerBoulderEntity extends BoulderEntity {
    Player target;

    public FollowerBoulderEntity(EntityType<FollowerBoulderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        tick = 0;
    }

    public FollowerBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(level, pos, blockState);
        tick = 0;
    }

    private int tick;

    @Override
    public void tick() {
        super.tick();
        tick++;
        if (target != null) {
            Vec3 vec3 = target.position().subtract(position()).normalize();
            vec3 = new Vec3(vec3.x, 0.0, vec3.z);
            setDeltaMovement(vec3.scale(SPEED / 1.75F));
            setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
            this.yRotO = getYRot();
            if (this.distanceTo(target) >= 30) remove();
        }
        if (tick >= 20 * 20) remove();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        remove();
    }

    @Override
    public void targetTo(@Nullable Player player) {
        if (player != null){
            target = player;
        }
    }
}
