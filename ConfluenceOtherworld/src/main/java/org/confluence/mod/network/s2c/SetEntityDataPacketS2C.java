package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;

public record SetEntityDataPacketS2C(int entityId, int dataId, Object o) implements CustomPacketPayload {
    public static final int DATA_BOOLEAN = 0;
    public static final Type<SetEntityDataPacketS2C> TYPE = new Type<>(Confluence.asResource("set_entity_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetEntityDataPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SetEntityDataPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            int entityId = buffer.readInt();
            int dataId = buffer.readVarInt();
            return new SetEntityDataPacketS2C(entityId, dataId, SetEntityDataPacketS2C.decode(buffer, dataId));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, SetEntityDataPacketS2C value) {
            buffer.writeVarInt(value.entityId);
            buffer.writeVarInt(value.dataId);
            SetEntityDataPacketS2C.encode(buffer, value.dataId, value.o);
        }
    };

    @Override
    public Type<SetEntityDataPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                Entity entity = context.player().level().getEntity(entityId);
                if (entity instanceof IExtraSyncedData extraSyncedData) {
                    extraSyncedData.confluence$setData(dataId, o);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    private static Object decode(RegistryFriendlyByteBuf buffer, int dataId) {
        if (dataId == DATA_BOOLEAN) {
            return buffer.readBoolean();
        }
        throw new IllegalArgumentException("Unregistered data serializer id " + dataId + "!");
    }

    private static void encode(RegistryFriendlyByteBuf buffer, int dataId, Object o) {
        if (dataId == DATA_BOOLEAN) {
            buffer.writeBoolean((boolean) o);
        } else {
            throw new IllegalArgumentException("Unregistered data serializer id " + dataId + "!");
        }
    }

    public interface IExtraSyncedData {
        void confluence$setData(int dataId, Object o);

        Object confluence$getData(int dataId);

        int[] confluence$getAllDataId();
    }
}
