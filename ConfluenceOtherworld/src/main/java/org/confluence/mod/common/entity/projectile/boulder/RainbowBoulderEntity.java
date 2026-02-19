package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// TODO 彩虹
public class RainbowBoulderEntity extends BoulderEntity {
    private Player protectedPlayer = null;
    private Entity target = null;
    private final List<Vec3> trails = new ArrayList<>();

    public RainbowBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
        setNoGravity(true);
    }

    public RainbowBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.RAINBOW_BOULDER.get(), level, pos, blockState);
        noPhysics = true;
        setNoGravity(true);
    }

    @Override
    public void targetTo(@Nullable Entity entity) {
        Vec3 deltaMovement = getDeltaMovement();
        Vec3 vec3 = entity == null ? deltaMovement : entity.position().subtract(position());
        vec3 = new Vec3(vec3.x, vec3.y, vec3.z).normalize();
        setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
        setDeltaMovement(vec3.scale(speed));
        this.yRotO = getYRot();
    }

    @Override
    public void tick() {
        super.tick();
        this.saveTrailPos();
        if (protectedPlayer == null) {
            Player player = level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 256, e -> true);
            if (player == null) {
                onRemove();
            } else {
                protectedPlayer = player;
            }
        }
        if (target != null) {
            if (!target.isAlive()) {
                target = null;
            }
            targetTo(target);
        }
        if (protectedPlayer != null && target == null){
            Vec3 center = protectedPlayer.position();
            double radius = 64;
            AABB aabb = AABB.ofSize(center, radius * 2, radius * 2, radius * 2);
            List<Entity> enemies = level().getEntities((Entity) null, aabb, e -> e instanceof Enemy);
            if (!enemies.isEmpty()) {
                target = enemies.get(level().random.nextInt(enemies.size()));
                targetTo(target);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {

    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        target = null;
    }

    @Override
    protected void verticalHitBlock(BlockHitResult blockHitResult, Direction direction) {

    }

    @Override
    protected void horizontalHitBlock(BlockHitResult blockHitResult, Direction direction) {

    }

    private void saveTrailPos() {
        if (this.level().isClientSide) {
            Vec3 currentPos = this.position();

            if (trails.isEmpty()) {
                trails.addLast(currentPos);
            }

            Vec3 lastPos = trails.getLast();
            double dist = lastPos.distanceTo(currentPos);

            double spacing = 0.4;
            if (dist > spacing) {
                int steps = Mth.floor(dist / spacing);
                Vec3 delta = currentPos.subtract(lastPos).scale(1.0 / steps);
                for (int i = 1; i <= steps; i++) {
                    trails.addLast(lastPos.add(delta.scale(i)));
                }
            } else {
                trails.addLast(currentPos);
            }

            while (trails.size() > 20) {
                trails.removeFirst();
            }
        }
    }

    public List<Vec3> getTrails() {
        return trails;
    }
}
