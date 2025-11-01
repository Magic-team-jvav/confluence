package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EmptyEntity extends Entity {

	public EmptyEntity(final EntityType<?> entityType, final Level level) {
		super(entityType, level);
	}

	@Override
	protected void defineSynchedData(final SynchedEntityData.Builder builder) {

	}

	@Override
	protected void readAdditionalSaveData(final CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(final CompoundTag compound) {

	}
}
