package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.Map;

public record LootStackSyncPacketS2C(ResourceLocation item, Stack[] stacks) implements CustomPacketPayload {
    public static final Type<LootStackSyncPacketS2C> TYPE = new Type<>(Confluence.asResource("loot_stack_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LootStackSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull LootStackSyncPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            Stack[] array = new Stack[size];
            for (int i = 0; i < size; i++) {
                array[i] = Stack.STREAM_CODEC.decode(buffer);
            }
            return new LootStackSyncPacketS2C(ResourceLocation.STREAM_CODEC.decode(buffer), array);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, LootStackSyncPacketS2C value) {
            buffer.writeVarInt(value.stacks.length);
            for (Stack stack : value.stacks) {
                Stack.STREAM_CODEC.encode(buffer, stack);
            }
            ResourceLocation.STREAM_CODEC.encode(buffer, value.item);
        }
    };

    @Override
    public @NotNull Type<LootStackSyncPacketS2C> type() {
        return TYPE;
    }

    public record Stack(ResourceLocation item, int min, int max) {
        public static final StreamCodec<ByteBuf, Stack> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, p -> p.item,
                ByteBufCodecs.VAR_INT, p -> p.min,
                ByteBufCodecs.VAR_INT, p -> p.max,
                Stack::new
        );
    }

    public static Stack[] calculateStacks(MinecraftServer server, ResourceKey<LootTable> lootTableKey) {
        Map<ResourceLocation, int[]> map = new Hashtable<>();
        LootTable lootTable = server.reloadableRegistries().getLootTable(lootTableKey);
        for (LootPool pool : lootTable.pools) {
            for (LootPoolEntryContainer entry : pool.entries) {
                if (entry instanceof LootItem lootItem) {
                    int[] count = map.computeIfAbsent(BuiltInRegistries.ITEM.getKey(lootItem.item.value()), item -> new int[2]);
                    for (LootItemFunction function : lootItem.functions) {
                        if (function instanceof SetItemCountFunction setCount && setCount.value instanceof UniformGenerator(ConstantValue(float min), ConstantValue(float max))) {
                            int i = setCount.add ? count[0] : 0;
                            int j = setCount.add ? count[1] : 0;
                            count[0] = i + Math.round(min);
                            count[1] = j + Math.round(max);
                        }
                    }
                }
            }
        }
        return map.entrySet().stream().map(entry -> new Stack(entry.getKey(), entry.getValue()[0], entry.getValue()[1])).toArray(Stack[]::new);
    }
}
