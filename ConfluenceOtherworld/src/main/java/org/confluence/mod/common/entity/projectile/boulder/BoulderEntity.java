package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.FunctionalBlocks;

/**
 * 巨石
 */
public class BoulderEntity extends AbstractBoulderEntity {
	public static final BlockState DEFAULT_BLOCK_STATE = FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState();

	public BoulderEntity(final EntityType<? extends AbstractBoulderEntity> entityType, final Level level) {
		this(entityType, level, Builder.of());
	}

	public BoulderEntity(final EntityType<? extends AbstractBoulderEntity> entityType, final Level level, Builder builder) {
		this(entityType, level, DEFAULT_BLOCK_STATE, builder);
	}

	public BoulderEntity(final EntityType<? extends AbstractBoulderEntity> entityType, final Level level, BlockState blockState) {
		this(entityType, level, blockState, Builder.of());
	}

	public BoulderEntity(final EntityType<? extends AbstractBoulderEntity> entityType, final Level level, BlockState blockState, Builder builder) {
		super(entityType, level, blockState, builder);
	}

	public BoulderEntity(final Level level, final Vec3 pos, final BlockState blockState) {
		this(level, pos, blockState, Builder.of());
	}

	public BoulderEntity(final Level level, final Vec3 pos, final BlockState blockState, Builder builder) {
		this(ModEntities.BOULDER.get(), level, pos, blockState, builder);
	}

	public BoulderEntity(final EntityType<? extends AbstractBoulderEntity> entityType, final Level level, final Vec3 pos, final BlockState blockState) {
		this(entityType, level, pos, blockState, Builder.of());
	}

	public BoulderEntity(final EntityType<? extends AbstractBoulderEntity> entityType, final Level level, final Vec3 pos, final BlockState blockState, Builder builder) {
		super(entityType, level, pos, blockState, builder);
	}

	@Override
	public BlockState getDefaultBlockState() {
		return DEFAULT_BLOCK_STATE;
	}

	public static class Builder {
		/**
		 * 追踪范围
		 */
		protected float trackingRange =  31.5F;
		/**
		 * 移动速度
		 */
		protected double speed = 0.7;
		/**
		 * 最低被移除速度
		 */
		protected double minRemoveSpeed = 0.007;
		/**
		 * 大小半径
		 */
		protected float sizeRadius = 0.5F;
		/**
		 * 着陆次数
		 */
		protected int landingCount = 3;
		/**
		 * 粒子方块状态 如果设置为空则使用方块状态
		 */
		protected BlockState particleBlockState;
		/**
		 * 粒子类型
		 */
		protected ParticleType<BlockParticleOption> particleType = ParticleTypes.BLOCK;
		/**
		 * 音效
		 */
		protected SoundEvent sound = SoundEvents.STONE_BREAK;
		/**
		 * 音效类别
		 */
		protected SoundSource soundCategory = SoundSource.BLOCKS;
		/**
		 * 默认重力
		 */
		protected double defaultGravity = 0.08;

		private Builder(){}

		public static Builder of(){
			return new Builder();
		}

		public Builder copy(Builder builder){
			this.trackingRange = builder.trackingRange;
			this.speed = builder.speed;
			this.minRemoveSpeed = builder.minRemoveSpeed;
			this.sizeRadius = builder.sizeRadius;
			this.landingCount = builder.landingCount;
			this.particleBlockState = builder.particleBlockState;
			this.particleType = builder.particleType;
			this.sound = builder.sound;
			this.soundCategory = builder.soundCategory;
			this.defaultGravity = builder.defaultGravity;
			return this;
		}

		public Builder inherit(Builder builder){
			var o = of();
			if (o.trackingRange != builder.trackingRange) {
				this.trackingRange = builder.trackingRange;
			}
			if (o.speed != builder.speed) {
				this.speed = builder.speed;
			}
			if (o.minRemoveSpeed != builder.minRemoveSpeed) {
				this.minRemoveSpeed = builder.minRemoveSpeed;
			}
			if (o.sizeRadius != builder.sizeRadius) {
				this.sizeRadius = builder.sizeRadius;
			}
			if (o.landingCount != builder.landingCount) {
				this.landingCount = builder.landingCount;
			}
			if (o.particleBlockState != builder.particleBlockState) {
				this.particleBlockState = builder.particleBlockState;
			}
			if (o.particleType != builder.particleType) {
				this.particleType = builder.particleType;
			}
			if (o.sound != builder.sound) {
				this.sound = builder.sound;
			}
			if (o.soundCategory != builder.soundCategory) {
				this.soundCategory = builder.soundCategory;
			}
			if (o.defaultGravity != builder.defaultGravity) {
				this.defaultGravity = builder.defaultGravity;
			}
			return this;
		}

		public Builder trackingRange(float trackingRange) {
			this.trackingRange = trackingRange;
			return this;
		}

		public Builder speed(double speed) {
			this.speed = speed;
			return this;
		}

		public Builder sizeRadius(float sizeRadius) {
			this.sizeRadius = sizeRadius;
			return this;
		}

		public Builder landingCount(int landingCount) {
			this.landingCount = landingCount;
			return this;
		}

		public Builder minRemoveSpeed(double minRemoveSpeed) {
			this.minRemoveSpeed = minRemoveSpeed;
			return this;
		}

		/**
		 * 除非设置不然会自动使用创建时的方块状态
		 */
		public Builder particle(BlockState blockState) {
			this.particleBlockState = blockState;
			return this;
		}

		public Builder particle(ParticleType<BlockParticleOption> particleType) {
			this.particleType = particleType;
			return this;
		}

		public Builder particle(BlockState blockState,
		                        ParticleType<BlockParticleOption> particleType) {
			this.particleBlockState = blockState;
			this.particleType = particleType;
			return this;
		}

		public Builder sound(SoundEvent sound) {
			this.sound = sound;
			return this;
		}

		public Builder sound(SoundSource soundCategory) {
			this.soundCategory = soundCategory;
			return this;
		}

		public Builder sound(SoundEvent sound,
		                     SoundSource soundCategory) {
			this.sound = sound;
			this.soundCategory = soundCategory;
			return this;
		}

		public Builder defaultGravity(double defaultGravity) {
			this.defaultGravity = defaultGravity;
			return this;
		}

		public BoulderEntity build(final Level level, final Vec3 pos, final BlockState blockState){
			return new BoulderEntity(level, pos, blockState, this);
		}

		public BoulderEntity build(final EntityType<? extends AbstractBoulderEntity> entityType, final Level level, final Vec3 pos, final BlockState blockState) {
			return new BoulderEntity(entityType, level, pos, blockState, this);
		}
	}
}
