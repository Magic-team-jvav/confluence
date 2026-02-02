package org.confluence.terraentity.network.s2c;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 更新npc交易的单个列表，以节省网络流量
 */
public class UpdateNPCTradePacket implements CustomPacketPayload {

    private final int index;
    private final UUID npcId;
    private final ITrade trade;

    private UpdateNPCTradePacket(int index, UUID npcId, ITrade trade) {
        this.index = index;
        this.npcId = npcId;
        this.trade = trade;
    }

    public UpdateNPCTradePacket(RegistryFriendlyByteBuf buffer) {
       this.index = buffer.readInt();
       this.npcId = buffer.readUUID();
       JsonElement element = GsonHelper.parse(buffer.readBytes(buffer.readVarInt()).toString(StandardCharsets.UTF_8));
       this.trade = ITrade.TYPED_CODEC.decode(buffer.registryAccess().createSerializationContext(JsonOps.INSTANCE), element).result().get().getFirst();
    }

    public static final Type<UpdateNPCTradePacket> TYPE = new Type<>(TerraEntity.space("update_npc_trade_packet_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateNPCTradePacket> STREAM_CODEC = CustomPacketPayload.codec(UpdateNPCTradePacket::encode, UpdateNPCTradePacket::decode);


    @Override
    public @NotNull Type<UpdateNPCTradePacket> type() {
        return TYPE;
    }

    public static UpdateNPCTradePacket decode(RegistryFriendlyByteBuf buffer) {
        return new UpdateNPCTradePacket(buffer);
    }

    public static void encode(UpdateNPCTradePacket packet, RegistryFriendlyByteBuf buf) {
        buf.writeInt(packet.index);
        buf.writeUUID(packet.npcId);
        JsonElement element = ITrade.TYPED_CODEC.encodeStart(buf.registryAccess().createSerializationContext(JsonOps.INSTANCE), packet.trade).result().get();
        byte[] bytes = element.toString().getBytes();
        buf.writeVarInt(bytes.length);
        buf.writeBytes(bytes);
//        buf.writeJsonWithCodec(ITrade.TYPED_CODEC, packet.trade);
        
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player().level().getEntities().get(this.npcId) instanceof ITradeHolder npc){
                var trades = npc.getTradeManager().trades();
                var availableTrades = npc.getTradeManager().availableTrades();
                var availableTrade = availableTrades.get(this.index);

                // 由于客户端传来的index是availableTrades的索引，所以需要将availableTrades的索引转换为trades的索引
                int oriIndex = trades.indexOf(availableTrade);
                npc.getTradeManager().trades().set(oriIndex, this.trade);
                availableTrades.set(this.index, this.trade);
            }
            
        }).exceptionally(e -> null);
    }



    public static <T extends Entity & ITradeHolder> void syncNpcTrade(int index, T npc){
        AdapterUtils.sendToAllPlayers(new UpdateNPCTradePacket(index, npc.getUUID(), npc.getTradeManager().trades().get(index)));
    }

    public static <T extends ITradeHolder> void syncNpcTrade(int index, UUID npcId, T npc){
        AdapterUtils.sendToAllPlayers(new UpdateNPCTradePacket(index, npcId, npc.getTradeManager().trades().get(index)));
    }
}
