package org.confluence.terraentity.network.s2c;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.utils.AdapterUtils;

import java.util.EnumMap;
import java.util.function.Consumer;

public class ClientBoundEventPacket implements CustomPacketPayload{

    private enum TypeEnum {
        RESET_CRIMSON_STORM

    }
    static EnumMap<TypeEnum, Consumer<Player>> handlers = new EnumMap<>(ImmutableMap.<TypeEnum, Consumer<Player>>builder()
            .put(TypeEnum.RESET_CRIMSON_STORM, (player)-> {
                player.getData(TEAttachments.UNSYNC).triggerInvulnerableStorm(player);
            })
            .build());

    private final TypeEnum _type;

    public static final CustomPacketPayload.Type<ClientBoundEventPacket> TYPE = new CustomPacketPayload.Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "client_bound_event_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundEventPacket> STREAM_CODEC = CustomPacketPayload.codec(ClientBoundEventPacket::write, ClientBoundEventPacket::new);

    ClientBoundEventPacket(ClientBoundEventPacket.TypeEnum type) {
        this._type = type;
    }

    ClientBoundEventPacket(FriendlyByteBuf buf) {
        this._type = buf.readEnum(ClientBoundEventPacket.TypeEnum.class);
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(_type);
    }

    public static void handle(ClientBoundEventPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
//            Player player = Minecraft.getInstance().player;
            ClientBoundEventPacket.TypeEnum type = packet._type;
            if (handlers.containsKey(type)) {
                handlers.get(type).accept(context.player());
            }else{
                TerraEntity.LOGGER.warn("Unknown client-bound event packet type: {}", type);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void resetCrimsonStorm(ServerPlayer player){
        AdapterUtils.sendToPlayer(player, new ClientBoundEventPacket(TypeEnum.RESET_CRIMSON_STORM));
    }

}
