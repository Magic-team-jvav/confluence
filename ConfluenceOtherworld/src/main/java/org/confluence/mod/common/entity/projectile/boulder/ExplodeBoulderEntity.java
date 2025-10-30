package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.FunctionalBlocks;

/**
 * 爆炸巨石
 */
public class ExplodeBoulderEntity extends AbstractBoulderEntity {
	public static final BlockState DEFAULT_BLOCK_STATE = FunctionalBlocks.EXPLODE_BOULDER.get().defaultBlockState();
	/**
	 * 爆炸威力
	 */
	public final float explosiveRadius;

	public ExplodeBoulderEntity(EntityType<ExplodeBoulderEntity> entityType, Level pLevel) {
		super(entityType, pLevel, DEFAULT_BLOCK_STATE, BoulderEntity.Builder.of());
		this.explosiveRadius = 2.85F;
	}

	public ExplodeBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
		this(level, pos, blockState, BoulderEntity.Builder.of());
	}

	public ExplodeBoulderEntity(Level level, Vec3 pos, BlockState blockState, BoulderEntity.Builder builder) {
		this(level, pos, blockState, 2.85F, builder);
	}

	public ExplodeBoulderEntity(Level level, Vec3 pos, BlockState blockState, float explosiveRadius) {
		this(level, pos, blockState, explosiveRadius, BoulderEntity.Builder.of());
	}

	public ExplodeBoulderEntity(Level level, Vec3 pos, BlockState blockState, float explosiveRadius, BoulderEntity.Builder builder) {
		super(ModEntities.EXPLODE_BOULDER.get(), level, pos, blockState, builder);
		this.explosiveRadius = explosiveRadius;
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		explode(entityHitResult);
	}

	@Override
	protected void onHorizontalBlockHit(final BlockHitResult blockHitResult) {
		super.onHorizontalBlockHit(blockHitResult);
		explode(blockHitResult);
	}

	@Override
	protected void onVerticalBlockHit(final BlockHitResult blockHitResult) {
		super.onVerticalBlockHit(blockHitResult);
		if (getLandingCount() <= 0) {
			explode(blockHitResult);
		}
	}

	protected void explode(HitResult hitResult) {
		if (level().isClientSide) {
			return;
		}
		var vec3 = hitResult.getLocation();
		vec3 = vec3.subtract(vec3.scale(Math.pow(vec3.length(), -(getSizeRadius() * 2))));
		this.level().explode(this, vec3.x, vec3.y, vec3.z, this.explosiveRadius, true, Level.ExplosionInteraction.TNT);
		onRemoveBroken();
	}

	@Override
	public BlockState getDefaultBlockState() {
		return DEFAULT_BLOCK_STATE;
	}
}
