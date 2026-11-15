package org.confluence.mod.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class SelectiveBlockCollision {
    private SelectiveBlockCollision() {}

    public static Vec3 resolve(Entity entity, Vec3 movement, Predicate<BlockState> passable) {
        AABB box = entity.getBoundingBox();
        AABB swept = box.expandTowards(movement).inflate(1.0E-7);
        List<VoxelShape> shapes = new ArrayList<>(entity.level().getEntityCollisions(entity, swept));
        shapes.add(entity.level().getWorldBorder().getCollisionShape());
        CollisionContext context = CollisionContext.of(entity);
        // 栅栏等碰撞形状可伸出所属方块，扫描范围必须包含相邻方块。
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(swept.minX, swept.minY, swept.minZ).offset(-1, -1, -1), BlockPos.containing(swept.maxX, swept.maxY, swept.maxZ).offset(1, 1, 1))) {
            BlockState state = entity.level().getBlockState(pos);
            if (!passable.test(state)) {
                VoxelShape shape = state.getCollisionShape(entity.level(), pos, context);
                if (!shape.isEmpty()) shapes.add(shape.move(pos.getX(), pos.getY(), pos.getZ()));
            }
        }
        double y = Shapes.collide(Direction.Axis.Y, box, shapes, movement.y);
        box = box.move(0.0, y, 0.0);
        boolean zFirst = Math.abs(movement.x) < Math.abs(movement.z);
        double x = movement.x;
        double z = movement.z;
        if (zFirst) {
            z = Shapes.collide(Direction.Axis.Z, box, shapes, z);
            box = box.move(0.0, 0.0, z);
        }
        x = Shapes.collide(Direction.Axis.X, box, shapes, x);
        if (!zFirst) z = Shapes.collide(Direction.Axis.Z, box.move(x, 0.0, 0.0), shapes, z);
        return new Vec3(x, y, z);
    }
}
