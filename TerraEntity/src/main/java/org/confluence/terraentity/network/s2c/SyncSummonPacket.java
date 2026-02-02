package org.confluence.terraentity.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEAttachments;

public class SyncSummonPacket implements CustomPacketPayload {

    int currentCapability;
    byte type;

//    List<Integer> indexList;
    public static final CustomPacketPayload.Type<SyncSummonPacket> TYPE = new CustomPacketPayload.Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "sync_summon_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSummonPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncSummonPacket::write, SyncSummonPacket::new);

    public SyncSummonPacket(int currentCapability, byte type) {
        this.currentCapability = currentCapability;
        this.type = type;
//        this.indexList = indexList;
    }

    public SyncSummonPacket(FriendlyByteBuf buf) {
        this.currentCapability = buf.readInt();
        this.type = buf.readByte();
//        this.indexList = new LinkedList<>(Arrays.stream(buf.readVarIntArray()).boxed().toList());
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeInt(currentCapability);
        buf.writeByte(type);
//        buf.writeVarIntArray(indexList.stream().mapToInt(Integer::intValue).toArray());

    }

    public static void handle(SyncSummonPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(packet.type == 0) {
                var data = context.player().getData(TEAttachments.SUMMONER_STORAGE.get());
                data.setCurrentCapacity(packet.currentCapability);
            }else if(packet.type == 1) {
                var data = context.player().getData(TEAttachments.SENTRY_STORAGE.get());
                data.setCurrentCapacity(packet.currentCapability);
            }
//            data.setIds(packet.indexList);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    
}
