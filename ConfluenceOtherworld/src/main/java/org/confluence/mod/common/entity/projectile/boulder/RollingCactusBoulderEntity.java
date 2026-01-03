package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.FunctionalBlocks;

/**
 * 仙人掌巨石
 */
public class RollingCactusBoulderEntity extends AbstractBoulderEntity {
	private static final BoulderEntity.Builder BUILDER = BoulderEntity.Builder.of()
			.minRemoveSpeed(0.05)
			.speed(0.3)
			.particle(Blocks.CACTUS.defaultBlockState())
			.sound(SoundEvents.WOOL_BREAK);
	public static final BlockState DEFAULT_BLOCK_STATE = FunctionalBlocks.ROLLING_CACTUS_BOULDER.get().defaultBlockState();

	public RollingCactusBoulderEntity(EntityType<RollingCactusBoulderEntity> entityType, Level pLevel) {
		super(entityType, pLevel, DEFAULT_BLOCK_STATE, BUILDER);
	}

	public RollingCactusBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
		this(level, pos, blockState, BUILDER);
	}

	public RollingCactusBoulderEntity(Level level, Vec3 pos, BlockState blockState, BoulderEntity.Builder builder) {
		super(ModEntities.ROLLING_CACTUS_BOULDER.get(), level, pos, blockState, BUILDER.inherit(builder));
	}

	protected void brokenFunction(final ServerLevel serverLevel, boolean timeoutUrNot) {
		int count = random.nextInt(6, 13);
		float y = random.nextFloat() * Mth.PI;
		float d = Mth.TWO_PI / count;
		for (int i = 0; i < count; i++) {
			float x = -Mth.nextFloat(random, Mth.PI * 0.25F, Mth.PI * 0.4F);
			var projectile = new SpikeProjectile(ModEntities.ROLLING_CACTUS_SPIKE.get(), level());
			float cos = Mth.cos(x);
			float f = -Mth.sin(y) * cos;
			float f1 = -Mth.sin(x);
			float f2 = Mth.cos(y) * cos;
			projectile.setPos(position().add(0.0, 1.25, 0.0));
			projectile.shoot(f, f1, f2, 0.4F, 0.1F);
			level().addFreshEntity(projectile);
			y += d;
		}
		super.brokenFunction(serverLevel, timeoutUrNot);
	}

	/**
	 * 仙人掌巨石的尖刺
	 */
	public static class SpikeProjectile extends Projectile {
		public SpikeProjectile(EntityType<? extends Projectile> entityType, Level level) {
			super(entityType, level);
		}

		@Override
		protected void defineSynchedData(SynchedEntityData.Builder builder) {
		}

		@Override
		public void tick() {
			super.tick();
			var vec3 = getDeltaMovement();
			move(MoverType.SELF, vec3);
			var motion = getDeltaMovement();
			if (motion.x != vec3.x || motion.y != vec3.y || motion.z != vec3.z) {
				discard();
			} else if (!level().isClientSide) {
				if (ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity) instanceof EntityHitResult entityHitResult) {
					var entity = entityHitResult.getEntity();
					if (entity.hurt(damageSources().cactus(), 8.0F)) {
						VectorUtils.knockBackA2B(this, entity, 1.0, 0.2);
					}
				}
			}
			setDeltaMovement(motion.add(0.0, -0.08, 0.0));
			if (tickCount > 200) {
				discard();
			}
		}

		@Override
		protected boolean canHitEntity(Entity target) {
			return target.canBeHitByProjectile() && target != getOwner();
		}
	}

	@Override
	public BlockState getDefaultBlockState() {
		return DEFAULT_BLOCK_STATE;
	}
}
