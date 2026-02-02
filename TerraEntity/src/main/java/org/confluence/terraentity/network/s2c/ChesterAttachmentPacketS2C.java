package org.confluence.terraentity.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.attachment.SummonerAttachment;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.registries.chester.ChesterConditionalType;
import org.confluence.terraentity.utils.AdapterUtils;

import java.util.Map;

public class ChesterAttachmentPacketS2C implements CustomPacketPayload {

    /**
     * chestType: 0-999, additionalType: 0-999
     */
    int code;

    Map<SummonerAttachment.Key, ChesterConditionalType> bandedBlocks;

    public static final CustomPacketPayload.Type<ChesterAttachmentPacketS2C> TYPE = new CustomPacketPayload.Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "chester_event_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChesterAttachmentPacketS2C> STREAM_CODEC = CustomPacketPayload.codec(ChesterAttachmentPacketS2C::write, ChesterAttachmentPacketS2C::new);

    public ChesterAttachmentPacketS2C(int code, Map<SummonerAttachment.Key, ChesterConditionalType> bandedBlocks) {
        this.code = code;
        this.bandedBlocks = bandedBlocks;
    }

    public ChesterAttachmentPacketS2C(FriendlyByteBuf buf) {
        this.code = buf.readInt();
        this.bandedBlocks = buf.readJsonWithCodec(SummonerAttachment.bandedBlocksCodec);
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeInt(code);
        buf.writeJsonWithCodec(SummonerAttachment.bandedBlocksCodec, bandedBlocks);
    }

    public static void handle(ChesterAttachmentPacketS2C packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            int code = packet.code;
            int chestType = code % 1000;
            int additionalType = code / 1000;
            Player player = context.player();
            var data = player.getData(TEAttachments.SUMMONER_STORAGE);
            data.chestType = chestType;
            data.chestTypeAdditional = additionalType;
            data.boundBlocks = packet.bandedBlocks;
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void syncChesterOpenType(int chestType,int additionalType, ServerPlayer player){
        AdapterUtils.sendToPlayer(player,new ChesterAttachmentPacketS2C(chestType + additionalType * 1000, player.getData(TEAttachments.SUMMONER_STORAGE).boundBlocks));
    }
}
