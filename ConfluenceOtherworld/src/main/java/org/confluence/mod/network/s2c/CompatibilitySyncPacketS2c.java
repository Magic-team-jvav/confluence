package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.CommonConfigs;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record CompatibilitySyncPacketS2c(int data) implements IPortPacket.S2C {
    private static ModConfigSpec.BooleanValue[] configs;
    public static final ResourceLocation ID = Confluence.asResource("compatibility_sync");
    public static final PortStreamCodec<ByteBuf, CompatibilitySyncPacketS2c> STREAM_CODEC = PortByteBufCodecs.VAR_INT.map(CompatibilitySyncPacketS2c::new, CompatibilitySyncPacketS2c::data);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        CompatibilityHandler.handle(data);
    }

    public static void sendToAll() {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(collectPacket());
        }
    }

    public static void sendToClient(ServerPlayer player) {
        CompatibilitySyncPacketS2c packet = collectPacket();
        Confluence.NETWORK_HANDLER.sendToPlayer(packet);
    }

    private static CompatibilitySyncPacketS2c collectPacket() {
        int data = 0;
        ModConfigSpec.BooleanValue[] arr = getConfigs();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].get()) data |= (1 << i);
        }
        return new CompatibilitySyncPacketS2c(data);
    }

    public static ModConfigSpec.BooleanValue[] getConfigs() {
        if (configs == null) {
            configs = new ModConfigSpec.BooleanValue[]{
                    CommonConfigs.CONVERT_ARS_NOUVEAU_MANA,
                    CommonConfigs.CONVERT_IRONS_SPELL_MANA,
                    CommonConfigs.FTB_CHUNKS_WORMHOLE_POTION,
                    CommonConfigs.WAYSTONES_PYLON_NON_COST
            };
        }
        return configs;
    }
}
