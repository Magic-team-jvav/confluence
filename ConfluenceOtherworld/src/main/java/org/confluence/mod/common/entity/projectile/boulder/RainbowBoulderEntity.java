package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terra_curio.common.init.TCCommonConfigs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO 彩虹
public class RainbowBoulderEntity extends BoulderEntity {
    private static int count = 0;
    private Player protectedPlayer = null;
    private Object target = null;
    private BlockPos targetPos = null;
    private final List<Vec3> trails = new ArrayList<>();
    private List<Entity> cachedEnemies = new ArrayList<>();
    private final List<BlockPos> cachedRareBlocks = new ArrayList<>();

    public RainbowBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public RainbowBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.RAINBOW_BOULDER.get(), level, pos, blockState);
        minRemoveSpeed = 0;
        setNoGravity(true);
        count++;
        if (count > 10) {
            onRemove();
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        count--;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        count--;
    }

    @Override
    public void targetTo(@Nullable Entity entity) {
        if (entity instanceof Player) {
            return;
        }
        Vec3 deltaMovement = getDeltaMovement();
        Vec3 vec3 = entity == null ? deltaMovement : entity.position().subtract(position());
        vec3 = new Vec3(vec3.x, vec3.y, vec3.z).normalize();
        setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
        setDeltaMovement(vec3.scale(speed));
        this.yRotO = getYRot();
    }

    public void targetToBlock(@Nullable BlockPos pos) {
        if (pos == null) return;
        Vec3 targetCenter = Vec3.atCenterOf(pos);
        Vec3 dir = targetCenter.subtract(position()).normalize();
        setYRot((float) (Mth.atan2(dir.x, dir.z) * Mth.RAD_TO_DEG));
        setDeltaMovement(dir.scale(speed));
        this.yRotO = getYRot();
    }

    @Override
    public void tick() {
        super.tick();
        this.saveTrailPos();
        if (count > 10) {
            onRemove();
        }

        if (protectedPlayer == null) {
            Player player = level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 256, e -> true);
            if (player == null) {
                onRemove();
                return;
            } else {
                protectedPlayer = player;
            }
        }


        if (targetPos != null) {
            double distance = position().subtract(targetPos.getCenter()).length();
            noPhysics = distance > 1.0;
        } else {
            noPhysics = true;
        }

        if (target instanceof Entity entityTarget) {
            if (!entityTarget.isAlive()) {
                target = null;
                targetPos = null;
            } else {
                targetTo(entityTarget);
            }
        } else if (target instanceof BlockState stateTarget) {
            if (targetPos == null || !level().getBlockState(targetPos).equals(stateTarget) || stateTarget.isAir()) {
                target = null;
                targetPos = null;
            } else {
                targetToBlock(targetPos);
            }
        }

        if (protectedPlayer != null && target == null) {
            if (tickCount % 20 == 0) {
                AABB area = new AABB(protectedPlayer.blockPosition()).inflate(64);
                cachedEnemies = level().getEntities((Entity) null, area, e -> e instanceof Enemy && e.isAlive());

                cachedRareBlocks.clear();
                BlockPos.betweenClosedStream(area).forEach(pos -> {
                    BlockState state = level().getBlockState(pos);
                    if (TCCommonConfigs.rareBlocks.containsKey(state)) {
                        cachedRareBlocks.add(pos.immutable());
                    }
                });
            }

            if (!cachedEnemies.isEmpty() && (cachedRareBlocks.isEmpty() || level().random.nextBoolean())) {
                target = cachedEnemies.get(level().random.nextInt(cachedEnemies.size()));
                targetTo((Entity) target);
            } else if (!cachedRareBlocks.isEmpty()) {
                BlockPos pos = cachedRareBlocks.get(level().random.nextInt(cachedRareBlocks.size()));
                targetPos = pos.immutable();
                target = level().getBlockState(pos);
                targetToBlock(pos);
            }
        }
    }

    @Override
    protected void onHit(Vec3 deltaMovement) {
        if (this.noPhysics) {
            deltaMovement = deltaMovement.add(
                    Mth.sign(deltaMovement.x) * radius,
                    Mth.sign(deltaMovement.y) * radius,
                    Mth.sign(deltaMovement.z) * radius
            );
            Vec3 start = position();
            Vec3 end = start.add(deltaMovement);

            BlockHitResult blockHit = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (blockHit.getType() != HitResult.Type.MISS) {
                if (level().random.nextFloat() < 0.3f) {
                    onHitBlock(blockHit);
                }
            }

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level(), this, start, end,
                    getBoundingBox().expandTowards(deltaMovement).inflate(1.0),
                    this::canHitEntity
            );
            if (entityHit != null) {
                onHitEntity(entityHit);
            }
        } else {
            super.onHit(deltaMovement);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof Boss) {
            onRemove();
            return;
        }
        if (!(entityHitResult.getEntity() instanceof Player)) {
            super.onHitEntity(entityHitResult);
        }
        target = null;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Direction direction = blockHitResult.getDirection();
        float x = (level().random.nextBoolean() ? 1 : -1) * (level().random.nextFloat() * 0.5f + 0.5f);
        float z = (level().random.nextBoolean() ? 1 : -1) * (level().random.nextFloat() * 0.5f + 0.5f);
        if (direction == Direction.UP) {
            setDeltaMovement(x, 1.5f, z);
        } else if (direction == Direction.DOWN) {
            setDeltaMovement(x, -1.5f, z);
        } else {
            Vec3 motion = VectorUtils.relativeScale(getDeltaMovement(), blockHitResult.getDirection().getAxis(), -bounceFactor);
            setDeltaMovement(motion);
        }
        target = null;
        targetPos = null;
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
