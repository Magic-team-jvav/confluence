package org.confluence.terraentity.network.s2c;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.IBlackboardHolder;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.KeyType;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 更黑板变量
 */
public class UpdateBlackboardPacket implements CustomPacketPayload {


    public static final Type<UpdateBlackboardPacket> TYPE = new Type<>(TerraEntity.space("update_blackboard_packet_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBlackboardPacket> STREAM_CODEC = CustomPacketPayload.codec(UpdateBlackboardPacket::encode, UpdateBlackboardPacket::new);

    public enum Operator{
        UPDATE,
        REMOVE
    }

    private final Operator operator;
    private final UUID uuid;
    private final String keyType;
    private Object value;

    private UpdateBlackboardPacket(Operator operator, UUID uuid, String keyType, Object value) {
        this.operator = operator;
        this.uuid = uuid;
        this.keyType = keyType;
        this.value = value;
    }

    public UpdateBlackboardPacket(RegistryFriendlyByteBuf buffer) {
        this.operator = buffer.readEnum(Operator.class);
        this.uuid = buffer.readUUID();
        this.keyType = buffer.readUtf();
        Codec<Object> codec = KeyType.getById(keyType).codec();
        if (codec != null) {
            this.value = buffer.readJsonWithCodec(codec);
        }else{
            throw new IllegalArgumentException("Codec not found for keyType: " + keyType);
        }

    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeEnum(operator);
        buf.writeUUID(uuid);
        buf.writeUtf(keyType);
        Codec<Object> codec = KeyType.getById(keyType).codec();
        if (codec != null) {
            buf.writeJsonWithCodec(codec, value);
        }else{
            throw new IllegalArgumentException("Codec not found for keyType: " + keyType);
        }

    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player().level().getEntities().get(this.uuid) instanceof IBlackboardHolder holder){
                if(this.operator == Operator.UPDATE) {
                    holder.getBlackboard().put(KeyType.getById(keyType), value);
                }else if(operator == Operator.REMOVE) {
                    holder.getBlackboard().remove(KeyType.getById(keyType));
                }
            }

        }).exceptionally(e -> null);
    }



    @Override
    public @NotNull Type<UpdateBlackboardPacket> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player, Operator operator, UUID uuid, String keyType, Object value) {
        KeyType<?> type = KeyType.getById(keyType);
        if(type == null || type.codec() == null) {
            TerraEntity.LOGGER.error("Invalid keyType: {}", keyType);
            return;
        }
        AdapterUtils.sendToPlayer(player, new UpdateBlackboardPacket(operator, uuid, keyType, value));
    }

}
