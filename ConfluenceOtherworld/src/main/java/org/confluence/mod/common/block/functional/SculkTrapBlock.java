package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.LibUtils;
import org.jetbrains.annotations.Nullable;

public class SculkTrapBlock extends SculkSensorBlock {
    /// 一次触发生成的监守者数量。
    public static final int WARDEN_COUNT = 2;

    public SculkTrapBlock(Properties properties) {
        super(properties);
    }

    /// 只接受玩家发出的声音：振动来源实体不是玩家（含其它生物、方块与无来源振动）时完全不激活。
    ///
    /// 原版 {@link SculkSensorBlock#stepOn} 的踩踏振动已被本类屏蔽，所以此处只会有真正的振动来源。
    @Override
    public void activate(@Nullable Entity entity, Level level, BlockPos pos, BlockState state, int power, int frequency) {
        if (!(entity instanceof Player) || !(level instanceof ServerLevel serverLevel)) return;
        super.activate(entity, level, pos, state, power, frequency);

        boolean spawned = false;
        for (int i = 0; i < WARDEN_COUNT; i++) {
            var warden = SpawnUtil.trySpawnMob(EntityType.WARDEN, MobSpawnType.TRIGGERED, serverLevel, pos, 20, 5, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER);
            if (warden.isEmpty()) continue;
            warden.get().addTag(LibUtils.NO_DROPS_TAG);
            spawned = true;
        }
        if (spawned) {
            SoundEvent soundevent = SoundEvents.WARDEN_LISTENING_ANGRY;
            int i = pos.getX() + Mth.randomBetweenInclusive(level.random, -10, 10);
            int j = pos.getY() + Mth.randomBetweenInclusive(level.random, -10, 10);
            int k = pos.getZ() + Mth.randomBetweenInclusive(level.random, -10, 10);
            level.playSound(null, i, j, k, soundevent, SoundSource.HOSTILE, 5.0F, 1.0F);
            level.destroyBlock(pos, false);
        }
    }

    /// 幽匿陷阱不再被任何实体踩踏触发，只认可玩家发出的声音。
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        // 有意留空：踩踏不再产生振动。
    }
}
