package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TombstoneBoulderEntity extends BoulderEntity {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SignText text = new SignText();

    public TombstoneBoulderEntity(EntityType<TombstoneBoulderEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
        this.minRemoveSpeed = 0.1;
        this.speed = 0.2;
    }

    public TombstoneBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.TOMBSTONE_BOULDER.get(), level, pos, blockState);
        this.minRemoveSpeed = 0.1;
        this.speed = 0.2;
    }

    @Override
    protected void removeEffect(ServerLevel serverLevel) {
        if (!serverLevel.getBlockState(blockPosition()).canBeReplaced()) {
            return;
        }
        serverLevel.setBlock(blockPosition(), getBlockState(), Block.UPDATE_ALL);
        if (serverLevel.getBlockEntity(blockPosition()) instanceof TombstoneBlock.BEntity entity) {
            entity.setText(text, true);
        }
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

    public static void createTombstoneEntity(LivingEntity living) {
        if (!CommonConfigs.DROPS_TOMBSTONE.get()) {
            return;
        }
        BlockState blockState = selectTombstone(living);
        if (blockState == null) return;
        Level level = living.level();
        Vec3 position = living.position();
        TombstoneBoulderEntity entity = new TombstoneBoulderEntity(level, position, blockState);
        entity.text = entity.text
                .setMessage(0, living.getCombatTracker().getDeathMessage())
                .setMessage(1, Component.literal(DATE_FORMAT.format(Calendar.getInstance().getTime())));
        if (!level.getBlockState(living.blockPosition().below()).isAir()) {
            entity.targetTo(level.getNearestPlayer(position.x, position.y, position.z, BoulderEntity.SEARCH_RANGE, BoulderEntity.ENTITY_PREDICATE));
        }
        level.addFreshEntity(entity);
    }

    public static @Nullable BlockState selectTombstone(LivingEntity living) {
        boolean isGolden;
        if (living instanceof Player player) {
            isGolden = PlayerUtils.getMoney(player, true) >= 100 * 100 * 10;
        } else if (living instanceof AbstractTerraNPC && living.level().getLevelData().isHardcore()) {
            isGolden = false;
        } else {
            return null;
        }
        return Util.getRandom(ModBlocks.TOMBSTONES.object2BooleanEntrySet().stream()
                .filter(entry -> entry.getBooleanValue() == isGolden)
                .map(entry -> entry.getKey().get().defaultBlockState())
                .toArray(BlockState[]::new), living.getRandom());
    }
}
