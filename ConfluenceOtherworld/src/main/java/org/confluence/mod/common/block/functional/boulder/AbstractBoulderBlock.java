package org.confluence.mod.common.block.functional.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.projectile.boulder.AbstractBoulderEntity;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * 抽象巨石方块
 *
 * @author 尽
 */
public abstract class AbstractBoulderBlock<T extends AbstractBoulderEntity> extends AbstractMechanicalBlock {
	private final BoulderEntityFactory<T> createEntityFactory;
	private final BoulderEntity.Builder builder;

	public AbstractBoulderBlock(BoulderEntityFactory<T> createEntityFactory) {
		this(Properties.of(), createEntityFactory, BoulderEntity.Builder.of());
	}

	public AbstractBoulderBlock(BoulderEntityFactory<T> createEntityFactory, BoulderEntity.Builder builder) {
		this(Properties.of(), createEntityFactory,builder);
	}

	public AbstractBoulderBlock(Properties properties, BoulderEntityFactory<T> createEntityFactory) {
		this(properties, createEntityFactory, BoulderEntity.Builder.of());
	}

	public AbstractBoulderBlock(Properties properties, BoulderEntityFactory<T> createEntityFactory, BoulderEntity.Builder builder) {
		super(properties);
		this.createEntityFactory = createEntityFactory;
		this.builder = builder;
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return true;
	}

	@Override
	public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
		level.removeBlock(hit.getBlockPos(), false);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		super.onRemove(state, level, pos, newState, movedByPiston);
		summon(level, pos, state, entity1 -> level.getNearestPlayer(entity1, entity1.getTrackingRange()));
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return true;
	}

	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		if (!(pLevel instanceof ServerLevel serverLevel)) {
			return;
		}
		var below = pLevel.getBlockState(pPos.below());
		if (!below.isAir()) {
			return;
		}
		if (pLevel.hasNeighborSignal(pPos)) {
			execute(pState, serverLevel, pPos, true);
			return;
		}
		if (!(pLevel.getBlockEntity(pPos) instanceof INetworkEntity iNetworkEntity)) {
			return;
		}
		onExecute(pState, serverLevel, pPos, -1, iNetworkEntity);
	}

	@Override
	public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
		level.removeBlock(pos, false);
	}

	protected void summon(Level level, BlockPos pos, BlockState blockState, Function<T, Player> function) {
		var entity = this.createEntityFactory.create(level, pos.getBottomCenter(), blockState, this.builder);
		if (!level.getBlockState(pos.below()).isAir()) {
			entity.setTracking(true);
			entity.targetPlayer(function.apply(entity));
		}
		level.addFreshEntity(entity);
	}

	@FunctionalInterface
	public interface BoulderEntityFactory<T extends AbstractBoulderEntity> {
		T create(Level level, Vec3 position, BlockState blockState, BoulderEntity.Builder builder);
	}
}
