package org.confluence.mod.common.entity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.mixin.world.entity.EntityType$BuilderAccessor;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class InverseEntityType<E extends Entity> extends EntityType<E> {
    public static final SpawnPlacementType ON_CEIL = new SpawnPlacementType() {
        @Override
        public boolean isSpawnPositionOk(LevelReader level, BlockPos pos, @Nullable EntityType<?> type) {
            if (type == null || !level.getWorldBorder().isWithinBounds(pos)) {
                return false;
            }
            BlockPos above = pos.above();
            BlockPos below = pos.below();
            BlockState aboveState = level.getBlockState(above);
            return aboveState.isValidSpawn(level, above, type) &&
                    isValidEmptySpawnBlock(level, pos, type) &&
                    isValidEmptySpawnBlock(level, below, type);
        }

        private boolean isValidEmptySpawnBlock(LevelReader level, BlockPos pos, EntityType<?> entityType) {
            BlockState state = level.getBlockState(pos);
            return NaturalSpawner.isValidEmptySpawnBlock(level, pos, state, state.getFluidState(), entityType);
        }

        @Override
        public BlockPos adjustSpawnPosition(LevelReader level, BlockPos pos) {
            BlockPos above = pos.above();
            return level.getBlockState(above).isPathfindable(PathComputationType.LAND) ? above : pos;
        }
    };

    private InverseEntityType(EntityType$BuilderAccessor<E> accessor, String key) {
        super(
                accessor.getFactory(),
                accessor.getCategory(),
                accessor.isSerialize(),
                accessor.isSummon(),
                accessor.isFireImmune(),
                accessor.isCanSpawnFarFromPlayer(),
                accessor.getImmuneTo(),
                accessor.getDimensions().withAttachments(accessor.getAttachments()),
                accessor.getSpawnDimensionsScale(),
                accessor.getClientTrackingRange(),
                accessor.getUpdateInterval(),
                accessor.getRequiredFeatures(),
                accessor.getVelocityUpdateSupplier(),
                accessor.getTrackingRangeSupplier(),
                accessor.getUpdateIntervalSupplier()
        );
        if (accessor.isSerialize()) {
            Util.fetchChoiceType(References.ENTITY_TREE, key);
        }
    }

    private InverseEntityType(EntityType.Builder<E> builder, String key) {
        this((EntityType$BuilderAccessor<E>) builder, key);
    }

    @Override
    public AABB getSpawnAABB(double x, double y, double z) {
        AABB aabb = super.getSpawnAABB(x, y, z);
        double deltaY = 1 - aabb.getYsize();
        return new AABB(aabb.minX, aabb.minY + deltaY, aabb.minZ, aabb.maxX, aabb.maxY + deltaY, aabb.maxZ);
    }

    public static <E extends Entity> InverseEntityType<E> create(EntityFactory<E> factory, MobCategory category, String key, Consumer<EntityType.Builder<E>> consumer) {
        EntityType.Builder<E> builder = EntityType.Builder.of(factory, category);
        consumer.accept(builder);
        return create(builder, key);
    }

    public static <E extends Entity> InverseEntityType<E> create(EntityType.Builder<E> builder, String key) {
        return new InverseEntityType<>(builder, key);
    }
}
