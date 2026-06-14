package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.attachment.ChunkDropletsData;
import org.confluence.mod.network.s2c.DropletsSyncPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MagicDropperItem extends Item {
    private final @Nullable ParticleOptions particle;

    public MagicDropperItem(@Nullable ParticleOptions particle) {
        super(new Properties().stacksTo(LibUtils.MAX_STACK_SIZE));
        this.particle = particle;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.SUCCESS;
        BlockPos pos = context.getClickedPos();
        if (!context.isInside()) {
            pos = pos.relative(context.getClickedFace());
        }
        Player player = context.getPlayer();
        if (player != null) {
            ChunkDropletsData data = ChunkDropletsData.of(level);
            ChunkPos chunkPos = new ChunkPos(pos);
            Map<BlockPos, ParticleOptions> map = data.getDataMap().computeIfAbsent(chunkPos, c -> new HashMap<>());
            if (particle == null) {
                map.remove(pos);
            } else {
                map.put(pos, particle);
            }
            DropletsSyncPacketS2C.sendToPlayersTrackingChunk(level, chunkPos, data.getDataMap(player, true));

            if (!PortPlayerExtension.hasInfiniteMaterials(player)) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.CONSUME;
    }
}
