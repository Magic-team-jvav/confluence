package org.confluence.terraentity.network.s2c;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.animation.HillOfFleshModelAnimationTable;
import org.confluence.terraentity.entity.animation.ModelPositionTable;
import org.confluence.terraentity.entity.npc.misc.NPCDialogs;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public record SyncDataS2C(int dataId, Object data) implements CustomPacketPayload {

    /**
     * @param <T> 用于类型推断
     */
    public interface DataType<T> {
        int getId();
        @Contract(pure = true)
        static <T> @NotNull DataType<T> create(int id) {
            return () -> id;
        }
    }

    private static final Map<Integer, Handler<Object>> handlers = new HashMap<>();

    public static final DataType<Map<EntityType<?>, NPCDialogs>> NPC_DIALOGS = register(NPCDialogs.Loader.CODEC, NPCDialogs.Loader::handle);
    public static final DataType<Map<EntityType<?>, NPCMood.EntityMood>> NPC_MOODS = register(NPCMood.Loader.CODEC, NPCMood.Loader::handle);
    public static final DataType<ModelPositionTable> HILL_ANIMATION = register(ModelPositionTable.CODEC, HillOfFleshModelAnimationTable::handle);


    public static final Type<SyncDataS2C> TYPE = new Type<>(TerraEntity.space("sync_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDataS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncDataS2C decode(RegistryFriendlyByteBuf buffer) {
            int dataId = buffer.readVarInt();
            return new SyncDataS2C(dataId, buffer.readJsonWithCodec(handlers.get(dataId).codec));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, SyncDataS2C value) {
            buffer.writeVarInt(value.dataId);
            buffer.writeJsonWithCodec(handlers.get(value.dataId).codec, value.data);
        }
    };

    @SuppressWarnings("unchecked")
    private static <T> DataType<T> register(Codec<T> codec, Consumer<T> consumer) {
        DataType<T> id = DataType.create(handlers.size());
        handlers.put(id.getId(), (Handler<Object>) new Handler<>(codec, consumer));
        return id;
    }

    @Override
    public @NotNull Type<SyncDataS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> handlers.get(dataId).consumer.accept(data)).exceptionally(e -> null);
    }

    public static <T> void sync(ServerPlayer player, DataType<T> dataId, T value) {
        AdapterUtils.sendToPlayer(player, new SyncDataS2C(dataId.getId(), value));
    }

    public static void syncAll(ServerPlayer player){
        syncNpcDialogs(player);
        syncNpcMoods(player);
        syncHillAnimation(player);
    }

    public static void syncNpcDialogs(ServerPlayer player) {
        sync(player, NPC_DIALOGS, NPCDialogs.Loader.getInstance().getDialogs());
    }

    public static void syncNpcMoods(ServerPlayer player) {
        sync(player, NPC_MOODS, NPCMood.Loader.getInstance().getByType());
    }

    public static void syncHillAnimation(ServerPlayer player) {
        sync(player, HILL_ANIMATION, HillOfFleshModelAnimationTable.getTable());
    }

    public record Handler<T>(Codec<T> codec, Consumer<T> consumer) {}
}
