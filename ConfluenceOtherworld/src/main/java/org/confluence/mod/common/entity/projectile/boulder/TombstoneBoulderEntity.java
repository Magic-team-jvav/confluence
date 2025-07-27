package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
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

public class TombstoneBoulderEntity extends BoulderEntity {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SignText text = new SignText();

    public TombstoneBoulderEntity(EntityType<TombstoneBoulderEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
        this.minimumBreakSpeed = 0.1;
        this.speed = 0.2;
    }

    public TombstoneBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.TOMBSTONE_BOULDER.get(), level, pos, blockState);
        this.minimumBreakSpeed = 0.1;
        this.speed = 0.2;
    }

    @Override
    public void onRemove() {
        if (!level().isClientSide && level().getBlockState(blockPosition()).canBeReplaced()) {
            level().setBlock(blockPosition(), getBlockState(), Block.UPDATE_ALL);
            if (level().getBlockEntity(blockPosition()) instanceof TombstoneBlock.Entity entity) {
                entity.setText(text, true);
            }
        }
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (ModSecretSeeds.FOR_THE_WORTHY.match()) {
            super.onHitEntity(entityHitResult);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("text", SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, text).getOrThrow());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.text = SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag.get("text")).getOrThrow();
    }

    public static void createTombstone(ServerPlayer serverPlayer) {
        if (CommonConfigs.DROPS_TOMBSTONE.get()) {
            boolean isGolden = PlayerUtils.getMoney(serverPlayer, true) >= 100 * 100 * 10;
            BlockState blockState = Util.getRandom(ModBlocks.TOMBSTONES.object2BooleanEntrySet().stream()
                    .filter(entry -> entry.getBooleanValue() == isGolden)
                    .map(entry -> entry.getKey().get().defaultBlockState())
                    .toArray(BlockState[]::new), serverPlayer.getRandom());
            Level level = serverPlayer.level();
            Vec3 position = serverPlayer.position();
            TombstoneBoulderEntity entity = new TombstoneBoulderEntity(level, position, blockState);
            entity.text = entity.text
                    .setMessage(0, serverPlayer.getCombatTracker().getDeathMessage())
                    .setMessage(1, Component.literal(DATE_FORMAT.format(Calendar.getInstance().getTime())));
            if (level.getBlockState(serverPlayer.blockPosition().below()).isAir()) {
                entity.setVertical(true);
            } else {
                entity.targetTo(level.getNearestPlayer(position.x, position.y, position.z, BoulderEntity.SEARCH_RANGE, Entity::isAlive));
                entity.setVertical(false);
            }
            level.addFreshEntity(entity);
        }
    }
}
