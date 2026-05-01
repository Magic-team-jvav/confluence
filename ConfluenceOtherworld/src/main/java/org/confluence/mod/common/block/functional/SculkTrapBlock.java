package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.LibUtils;
import org.jetbrains.annotations.Nullable;

public class SculkTrapBlock extends SculkSensorBlock {
    public SculkTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void activate(@Nullable Entity entity, Level level, BlockPos pos, BlockState state, int power, int frequency) {
        super.activate(entity, level, pos, state, power, frequency);
        if (entity instanceof ServerPlayer player) {
            SpawnUtil.trySpawnMob(EntityType.WARDEN, MobSpawnType.TRIGGERED, player.serverLevel(), pos, 20, 5, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER).ifPresent(warden -> {
                warden.addTag(LibUtils.NO_DROPS_TAG);
                SoundEvent soundevent = SoundEvents.WARDEN_LISTENING_ANGRY;
                int i = pos.getX() + Mth.randomBetweenInclusive(level.random, -10, 10);
                int j = pos.getY() + Mth.randomBetweenInclusive(level.random, -10, 10);
                int k = pos.getZ() + Mth.randomBetweenInclusive(level.random, -10, 10);
                level.playSound(null, i, j, k, soundevent, SoundSource.HOSTILE, 5.0F, 1.0F);
                level.destroyBlock(pos, false);
            });
        }
    }
}
