package org.confluence.terra_guns.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terra_guns.common.component.BulletPropertyComponent;
import org.confluence.terra_guns.common.init.TGDataComponents;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terra_guns.common.item.gun.BaseGun;
import org.confluence.terra_guns.impl.BulletHandler;

import java.util.function.Supplier;


public final class ShootPacketC2S implements CustomPacketPayload {
    private static final ShootPacketC2S INSTANCE = new ShootPacketC2S();
    public static final Type<ShootPacketC2S> TYPE = new Type<>(TerraGuns.asResource("shoot"));
    public static final StreamCodec<ByteBuf, ShootPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ShootPacketC2S() {}

    @Override
    public Type<ShootPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack gunStack = player.getMainHandItem();
                if (gunStack.getItem() instanceof BaseGun baseGun) {
                    ItemStack ammo = BulletHandler.getAmmo(player, gunStack);
                    ammo = ammo.equals(ItemStack.EMPTY) ? TGItems.EMPTY_BULLET.toStack() : ammo;
                    Supplier<DataComponentType<BulletPropertyComponent>> bulletComponent = TGDataComponents.BULLET_PROPERTY_COMPONENT;

                    baseGun.shoot(player, ammo, gunStack);
                    baseGun.fireAnimator(gunStack, player);

                    BulletPropertyComponent component = ammo.get(bulletComponent);
                    boolean infinity = component != null && component.infinity();
                    GunEvent.ShrinkBulletEvent shrinkBulletEvent = new GunEvent.ShrinkBulletEvent(player, baseGun, gunStack, ammo, infinity);
                    NeoForge.EVENT_BUS.post(shrinkBulletEvent);

                    if (!shrinkBulletEvent.isInfinity() && !shrinkBulletEvent.isCanceled()) {
                        shrinkBulletEvent.getBulletStack().shrink(shrinkBulletEvent.getShrink());
                    }
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
