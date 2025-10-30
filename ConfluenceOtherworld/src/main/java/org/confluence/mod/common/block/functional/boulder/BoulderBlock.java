package org.confluence.mod.common.block.functional.boulder;

import org.confluence.mod.common.entity.projectile.boulder.AbstractBoulderEntity;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;

/**
 * 巨石方块
 * @author 尽
 */
public class BoulderBlock extends AbstractBoulderBlock<AbstractBoulderEntity> {
  public BoulderBlock() {
    this(BoulderEntity.Builder.of());
  }

	public BoulderBlock(BoulderEntity.Builder builder) {
    this(BoulderEntity::new, builder);
  }

  public BoulderBlock(BoulderEntityFactory<AbstractBoulderEntity> factory) {
    this(factory, BoulderEntity.Builder.of());
  }

	public BoulderBlock(BoulderEntityFactory<AbstractBoulderEntity> factory, BoulderEntity.Builder builder) {
		this(Properties.of(), factory, builder);
  }

  public BoulderBlock(Properties properties, BoulderEntityFactory<AbstractBoulderEntity> factory) {
	  this(properties, factory, BoulderEntity.Builder.of());
  }

	public BoulderBlock(Properties properties, BoulderEntityFactory<AbstractBoulderEntity> factory, BoulderEntity.Builder builder) {
		super(properties, factory, builder);
	}
}
