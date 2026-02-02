package org.confluence.terraentity.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IFlyRideableMob;
import org.confluence.terraentity.utils.AdapterUtils;

public class ServerBoundVehicleExtensionPacket implements CustomPacketPayload {

    public enum Action {
        START_INPUT_JUMP,
        STOP_INPUT_JUMP

    }
    Action action;
    public static final Type<ServerBoundVehicleExtensionPacket> TYPE = new Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "server_bound_vehicle_extension_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundVehicleExtensionPacket> STREAM_CODEC = CustomPacketPayload.codec(ServerBoundVehicleExtensionPacket::write, ServerBoundVehicleExtensionPacket::new);

    public ServerBoundVehicleExtensionPacket(Action action) {
        this.action = action;

    }

    public ServerBoundVehicleExtensionPacket(FriendlyByteBuf buf) {

        this.action = Action.values()[buf.readByte()];

    }


    public void write(FriendlyByteBuf buf) {
        buf.writeByte(action.ordinal());
    }

    public static void handle(ServerBoundVehicleExtensionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player.getVehicle() instanceof IFlyRideableMob mob) {
                switch (packet.action) {
                    case START_INPUT_JUMP:
                        mob.onLocalStartInputJump();
                        break;
                    case STOP_INPUT_JUMP:
                        mob.onLocalStopInputJump();
                        break;

                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendAction(Action action){
        AdapterUtils.sendToServer(new ServerBoundVehicleExtensionPacket(action));
    }
}
