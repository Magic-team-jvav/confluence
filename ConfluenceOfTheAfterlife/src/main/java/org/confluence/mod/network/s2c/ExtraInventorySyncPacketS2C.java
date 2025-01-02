package org.confluence.mod.network.s2c;

import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.mod.common.attachment.ExtraInventory.DYE_START;
import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_DYE_EXCEPT_ACCESSORY_DYE;

public record ExtraInventorySyncPacketS2C(int entityId, List<Tuple<Integer, ItemStack>> list) implements CustomPacketPayload {
    public static final Type<ExtraInventorySyncPacketS2C> TYPE = new Type<>(Confluence.asResource("extra_inventory_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtraInventorySyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ExtraInventorySyncPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            int id = buffer.readInt();
            int size = buffer.readVarInt();
            List<Tuple<Integer, ItemStack>> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int slot = buffer.readVarInt();
                ItemStack itemStack = ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, buffer.readNbt()).getOrThrow();
                list.add(new Tuple<>(slot, itemStack));
            }
            return new ExtraInventorySyncPacketS2C(id, list);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ExtraInventorySyncPacketS2C value) {
            buffer.writeInt(value.entityId);
            buffer.writeVarInt(value.list.size());
            for (Tuple<Integer, ItemStack> tuple : value.list) {
                buffer.writeVarInt(tuple.getA());
                buffer.writeNbt(ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, tuple.getB()).getOrThrow());
            }
        }
    };

    @Override
    public @NotNull Type<ExtraInventorySyncPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.isLocalPlayer() && player.level().getEntity(entityId) instanceof Player entity) {
                ExtraInventory extraInventory = entity.getData(ModAttachmentTypes.EXTRA_INVENTORY);
                extraInventory.setAccessoryDyes(list.size() - SIZE_DYE_EXCEPT_ACCESSORY_DYE);
                for (Tuple<Integer, ItemStack> tuple : list) {
                    extraInventory.setItem(tuple.getA(), tuple.getB());
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer, ServerPlayer player, ExtraInventory extraInventory) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            extraInventory.initialize(serverPlayer);
            List<Tuple<Integer, ItemStack>> list = serialize(extraInventory);
            PacketDistributor.sendToPlayer(serverPlayer, new ExtraInventorySyncPacketS2C(player.getId(), list));
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(ServerPlayer serverPlayer, ServerPlayer player, ExtraInventory extraInventory) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            extraInventory.initialize(serverPlayer);
            List<Tuple<Integer, ItemStack>> list = serialize(extraInventory);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new ExtraInventorySyncPacketS2C(player.getId(), list));
        }
    }

    private static @NotNull List<Tuple<Integer, ItemStack>> serialize(ExtraInventory extraInventory) {
        List<Tuple<Integer, ItemStack>> list = new ArrayList<>();
        for (int i = DYE_START; i < extraInventory.getContainerSize(); i++) {
            list.add(new Tuple<>(i, extraInventory.getItem(i)));
        }
        return list;
    }
}
