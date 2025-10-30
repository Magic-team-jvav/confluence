package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.util.PlayerUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 墓碑巨石
 */
public class TombstoneBoulderEntity extends AbstractBoulderEntity {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final BlockState DEFAULT_BLOCK_STATE = ModBlocks.TOMBSTONE.get().defaultBlockState();
	private SignText text = new SignText();

	private static final BoulderEntity.Builder BUILDER = BoulderEntity.Builder.of()
			.minRemoveSpeed(0.1)
			.speed(0.2);

	public TombstoneBoulderEntity(EntityType<TombstoneBoulderEntity> entityType, Level pLevel) {
		super(entityType, pLevel, DEFAULT_BLOCK_STATE, BUILDER);
	}

	public TombstoneBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
		this(level, pos, blockState, BUILDER);
	}

	public TombstoneBoulderEntity(Level level, Vec3 pos, BlockState blockState, BoulderEntity.Builder builder) {
		super(ModEntities.TOMBSTONE_BOULDER.get(), level, pos, blockState, BUILDER.inherit(builder));
	}

	@Override
	public void onRemoveBroken() {
		if (!level().getBlockState(blockPosition()).canBeReplaced()) {
			return;
		}
		super.onRemoveBroken();
	}

	@Override
	protected void brokenFunction(final ServerLevel serverLevel) {
		var level = level();
		level.setBlock(blockPosition(), getBlockState(), Block.UPDATE_ALL);
		if (!(level.getBlockEntity(blockPosition()) instanceof TombstoneBlock.BEntity entity)) {
			return;
		}
		entity.setText(text, true);
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		if (!ModSecretSeeds.FOR_THE_WORTHY.match()) {
			return;
		}
		super.onHitEntity(entityHitResult);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("text", SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, text).getOrThrow());
	}

	@Override
	public BlockState getDefaultBlockState() {
		return DEFAULT_BLOCK_STATE;
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.text = SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag.get("text")).getOrThrow();
	}

	public static void createTombstone(ServerPlayer serverPlayer) {
		if (!CommonConfigs.DROPS_TOMBSTONE.get()) {
			return;
		}
		boolean isGolden = PlayerUtils.getMoney(serverPlayer, true) >= 100 * 100 * 10;
		var blockState = Util.getRandom(ModBlocks.TOMBSTONES.object2BooleanEntrySet().stream()
				.filter(entry -> entry.getBooleanValue() == isGolden)
				.map(entry -> entry.getKey().get().defaultBlockState())
				.toArray(BlockState[]::new), serverPlayer.getRandom());
		var level = serverPlayer.level();
		var position = serverPlayer.position();
		var entity = new TombstoneBoulderEntity(level, position, blockState);
		entity.text = entity.text
				.setMessage(0, serverPlayer.getCombatTracker().getDeathMessage())
				.setMessage(1, Component.literal(DATE_FORMAT.format(Calendar.getInstance().getTime())));
		if (!level.getBlockState(serverPlayer.blockPosition().below()).isAir()) {
			entity.setTracking(true);
			entity.targetPlayer(level.getNearestPlayer(position.x, position.y, position.z, entity.getTrackingRange(), Entity::isAlive));
		}
		level.addFreshEntity(entity);
	}
}
