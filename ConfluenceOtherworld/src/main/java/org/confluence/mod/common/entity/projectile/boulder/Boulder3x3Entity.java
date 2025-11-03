package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModTiers;

import java.util.ArrayList;

import static org.confluence.mod.common.entity.projectile.boulder.BoulderEntity.DEFAULT_BLOCK_STATE;

/**
 * 3x3巨石
 */
public class Boulder3x3Entity extends AbstractBoulderEntity {
	private static final EntityDataAccessor<Direction> DATA_FACING = SynchedEntityData.defineId(Boulder3x3Entity.class, EntityDataSerializers.DIRECTION);
	private final ItemStack simulated = Items.IRON_PICKAXE.getDefaultInstance();
	private final int power = ModTiers.getPowerForVanillaTiers(Tiers.IRON);

	private static final BoulderEntity.Builder BUILDER = BoulderEntity.Builder.of()
			.sizeRadius(1.5F)
			.defaultGravity(0.04);

	public Boulder3x3Entity(EntityType<? extends AbstractBoulderEntity> entityType, Level level) {
		super(entityType, level, Vec3.ZERO, DEFAULT_BLOCK_STATE, BUILDER);
	}

	public Boulder3x3Entity(Level level, Vec3 pos, BlockState blockState) {
		this(level, pos, blockState, BUILDER);
	}

	public Boulder3x3Entity(Level level, Vec3 pos, BlockState blockState, BoulderEntity.Builder builder) {
		super(ModEntities.BOULDER_3X.get(), level, pos, blockState, BUILDER.inherit(builder));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder.define(DATA_FACING, Direction.WEST));
	}

	@Override
	public void brokenParticles(final ServerLevel serverLevel, final BlockPos pos) {
		serverLevel.sendParticles(new BlockParticleOption(getParticleType(), getParticleBlockState()).setPos(pos),
				getX(), getY() + 1.5, getZ(), 400, 1, 1, 1, 0.15);
	}

	@Override
	protected void move() {
		var vec3 = getDeltaMovement();
		setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
		move(MoverType.SELF, vec3.add(0, -getGravity(), 0));
		var motion = getDeltaMovement();
		if (motion.x == vec3.x &&
				motion.y == vec3.y &&
				motion.z == vec3.z) {
			return;
		}
		var toDestroy = new ArrayList<BlockPos>();
		var facing = this.entityData.get(DATA_FACING);
		var blockPos = blockPosition().relative(facing, 2);
		for (var pos : BlockPos.betweenClosed(blockPos.relative(facing.getClockWise()), blockPos.above(2).relative(facing.getCounterClockWise()))) {
			var blockState = level().getBlockState(pos);
			if (blockState.getCollisionShape(level(), pos, CollisionContext.of(this)).isEmpty()) {
				continue;
			}
			if (!ModTiers.isCorrectToolForDrops(this.power, this.simulated, blockState)) {
				return;
			}
			toDestroy.add(pos.immutable());
		}
		if (!level().isClientSide) {
			for (BlockPos pos : toDestroy) {
				level().destroyBlock(pos, true);
			}
		}
		setDeltaMovement(vec3.scale(1 - toDestroy.size() * 0.01));
	}

	/**
	 * 发射巨石
	 */
	public void shoot(Direction facing, double speed) {
		if (facing == null){
			facing = Direction.Plane.HORIZONTAL.getRandomDirection(getRandom());
		}
		if (speed == 0) {
			speed = 0.1;
		}
		setYRot(facing.toYRot());
		setDeltaMovement(new Vec3(facing.getStepX() * speed, facing.getStepY() * speed, facing.getStepZ() * speed));
		entityData.set(DATA_FACING, facing);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Facing")) {
			entityData.set(DATA_FACING, Direction.CODEC.parse(NbtOps.INSTANCE, tag.get("Facing")).result().orElse(Direction.WEST));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		Direction.CODEC.encodeStart(NbtOps.INSTANCE, entityData.get(DATA_FACING))
				.ifSuccess(nbt -> tag.put("Facing", nbt));
	}

	@Override
	public BlockState getDefaultBlockState() {
		return DEFAULT_BLOCK_STATE;
	}
}
