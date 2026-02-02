package org.confluence.lib.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.mixed.IExtraSyncedData;

/**
 * @see IExtraSyncedData
 */
public record SetEntityDataPacketS2C(int entityId, Entry... entries) implements CustomPacketPayload {
    public static final byte DATA_BOOLEAN = 0;
    public static final Type<SetEntityDataPacketS2C> TYPE = new Type<>(ConfluenceMagicLib.asResource("set_entity_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetEntityDataPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SetEntityDataPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            int entityId = buffer.readVarInt();
            int size = buffer.readVarInt();
            Entry[] entries = new Entry[size];
            for (int i = 0; i < size; i++) {
                byte dataId = buffer.readByte();
                entries[i] = new Entry(dataId, SetEntityDataPacketS2C.decode(buffer, dataId));
            }
            return new SetEntityDataPacketS2C(entityId, entries);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, SetEntityDataPacketS2C value) {
            buffer.writeVarInt(value.entityId);
            buffer.writeVarInt(value.entries.length);
            for (Entry entry : value.entries) {
                buffer.writeByte(entry.dataId);
                SetEntityDataPacketS2C.encode(buffer, entry.dataId, entry.data);
            }
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
                if (entity instanceof IExtraSyncedData<?> extraSyncedData) {
                    for (Entry entry : entries) {
                        extraSyncedData.confluence$setData(entry.dataId, entry.data);
                    }
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    private static Object decode(RegistryFriendlyByteBuf buffer, byte dataId) {
        if (dataId == DATA_BOOLEAN) {
            return buffer.readBoolean();
        }
        throw new IllegalArgumentException("Unregistered data serializer id " + dataId + "!");
    }

    private static void encode(RegistryFriendlyByteBuf buffer, byte dataId, Object o) {
        if (dataId == DATA_BOOLEAN) {
            buffer.writeBoolean((boolean) o);
        } else {
            throw new IllegalArgumentException("Unregistered data serializer id " + dataId + "!");
        }
    }

    public record Entry(byte dataId, Object data) {}

    public static Entry ofBoolean(boolean data) {
        return new Entry(DATA_BOOLEAN, data);
    }
}
