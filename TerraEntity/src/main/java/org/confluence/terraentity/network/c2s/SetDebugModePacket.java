package org.confluence.terraentity.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.item.DebugItem;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;

public record SetDebugModePacket(DebugItem.DebugMode mode) implements CustomPacketPayload {

    public static final Type<SetDebugModePacket> TYPE = new Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "set_debug_mode_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetDebugModePacket> STREAM_CODEC = CustomPacketPayload.codec(SetDebugModePacket::write, SetDebugModePacket::new);

    SetDebugModePacket(FriendlyByteBuf buf) {
        this(buf.readEnum(DebugItem.DebugMode.class));
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(mode);
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.getData(TEAttachments.UNSYNC.get()).setDebugMode(mode);
            player.sendSystemMessage(Component.literal("Debug mode set to: " + mode.name()));
        });
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(DebugItem.DebugMode mode, Player localPlayer) {
        AdapterUtils.sendToServer(new SetDebugModePacket(mode));
        localPlayer.getData(TEAttachments.UNSYNC.get()).setDebugMode(mode);
    }


}
