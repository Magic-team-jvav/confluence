package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.common.component.AccessoriesComponent;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public record EchoVisibilityPacketS2C(boolean visible) implements CustomPacketPayload {
    public static final Type<EchoVisibilityPacketS2C> TYPE = new Type<>(Confluence.asResource("echo_visibility"));
    public static final StreamCodec<ByteBuf, EchoVisibilityPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, p -> p.visible,
            EchoVisibilityPacketS2C::new
    );

    @Override
    public @NotNull Type<EchoVisibilityPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                ClientPacketHandler.handleEcho(visible);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        boolean b = TCUtils.hasAccessoriesType(serverPlayer, AccessoryItems.SPECTRE$GOGGLES) &&
                CuriosUtils.hasCurio(serverPlayer, (Predicate<ItemStack>) itemStack -> {
                    AccessoriesComponent component = TCUtils.getAccessoriesComponent(itemStack);
                    if (component == null) return false;
                    return component.contains(AccessoryItems.SPECTRE$GOGGLES);
                });
        serverPlayer.getPersistentData().putBoolean("confluence:hasEchoVisibility", b);
        PacketDistributor.sendToPlayer(serverPlayer, new EchoVisibilityPacketS2C(b));
    }
}
