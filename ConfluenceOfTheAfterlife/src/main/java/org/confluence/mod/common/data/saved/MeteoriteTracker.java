package org.confluence.mod.common.data.saved;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.confluence.mod.Confluence;

import java.util.concurrent.CompletableFuture;

public class MeteoriteTracker {
    public static final MeteoriteTracker INSTANCE = new MeteoriteTracker();

    BlockPos location = BlockPos.ZERO;
    int tickUntilLanding = 0;

    public void tick(ServerLevel level) {
        if (tickUntilLanding == 0) {
            this.location = BlockPos.ZERO;
        } else if (tickUntilLanding > 0) {
            this.tickUntilLanding--;
            if (tickUntilLanding == 0) {
                ChunkPos chunkPos = new ChunkPos(location);
                place(level, chunkPos.x, chunkPos.z, !level.getForcedChunks().contains(chunkPos.toLong()));
            }
        }
    }

    private void place(ServerLevel level, int chunkX, int chunkZ, boolean withForceChunk) {
        CompletableFuture.supplyAsync(() -> {
            boolean placed = false;
            if (withForceChunk) {
                try {
                    level.setChunkForced(chunkX, chunkZ, true);
                    level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                            .getHolder(Confluence.asResource("normal_meteorite")).orElseThrow().value()
                            .place(level, level.getChunkSource().getGenerator(), level.random, location);
                    level.setChunkForced(chunkX, chunkZ, false);
                } catch (Exception ignored) {}
            }
            return placed;
        }, Util.backgroundExecutor()).thenAccept(success -> {
            Component message = Component.translatable("event.confluence.meteorite").withStyle(ChatFormatting.DARK_PURPLE);
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
        });
    }

    public void deserialize(CompoundTag nbt) {
        this.location = NbtUtils.readBlockPos(nbt, "meteoriteLocation").orElse(BlockPos.ZERO);
        this.tickUntilLanding = nbt.getInt("tickUtilMeteoriteLanding");
    }

    public void serialize(CompoundTag nbt) {
        nbt.put("meteoriteLocation", NbtUtils.writeBlockPos(location));
        nbt.putInt("tickUtilMeteoriteLanding", tickUntilLanding);
    }
}
