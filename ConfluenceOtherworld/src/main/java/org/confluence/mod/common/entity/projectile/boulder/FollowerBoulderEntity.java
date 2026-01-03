package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

/**
 * 跟踪巨石
 */
public class FollowerBoulderEntity extends AbstractBoulderEntity {
	public static final BlockState DEFAULT_BLOCK_STATE = FunctionalBlocks.FOLLOWER_BOULDER.get().defaultBlockState();
	private int tick = 0;
	private Player target;

	public FollowerBoulderEntity(EntityType<FollowerBoulderEntity> entityType, Level pLevel) {
		super(entityType, pLevel, DEFAULT_BLOCK_STATE, BoulderEntity.Builder.of());
		setTracking(true);
	}

	public FollowerBoulderEntity(Level level, Vec3 pos, BlockState blockState, BoulderEntity.Builder builder) {
		super(ModEntities.FOLLOWER_BOULDER.get(), level, pos, blockState, builder);
		setTracking(true);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.tick++ >= 20 * 20 || (this.target != null && distanceTo(this.target) >= 30)) {
            onRemoveBroken(true);
		}
	}

	@Override
	protected void move() {
		if (!level().getBlockState(getOnPos(-1)).isAir() && this.target != null) {
			var vec3 = this.target.position().subtract(position()).normalize();
			vec3 = new Vec3(vec3.x, 0.0, vec3.z);
			setDeltaMovement(vec3.scale(getSpeed()));
		}
		super.move();
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		if (entityHitResult.getEntity().equals(target)) {
            onRemoveBroken(false);
		}
	}

	@Override
	public boolean targetPlayer(@Nullable Player player) {
		if (player == null) {
			return false;
		}
		super.targetPlayer(player);
		this.target = player;
		return true;
	}

	@Override
	public BlockState getDefaultBlockState() {
		return DEFAULT_BLOCK_STATE;
	}
}
