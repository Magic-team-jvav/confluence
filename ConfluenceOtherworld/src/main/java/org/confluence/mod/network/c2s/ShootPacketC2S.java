package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.util.ModGunUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum ShootPacketC2S implements IPortPacket.C2S {
    INSTANCE;

    public static final ResourceLocation ID = Confluence.asResource("gun_shoot");
    public static final PortStreamCodec<ByteBuf, ShootPacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);

    @Override
    public void work(ServerPlayer player) {
        ItemStack gunStack = player.getMainHandItem();
        if (gunStack.getItem() instanceof BaseGun baseGun) {
            ItemStack ammo = ModGunUtils.getAmmo(player, gunStack);
            ammo = ammo.equals(ItemStack.EMPTY) ? GunItems.EMPTY_BULLET.get().getDefaultInstance() : ammo;

            baseGun.shoot(player, ammo, gunStack);
            baseGun.fireAnimator(gunStack, player);

            BulletPropertyComponent component = ammo.get(ModDataComponentTypes.BULLET_PROPERTY);
            boolean infinity = component != null && component.infinity();
            GunEvent.ShrinkBulletEvent event = new GunEvent.ShrinkBulletEvent(player, baseGun, gunStack, ammo, infinity);
            PortEventHandler.postEvent(event);

            if (!event.isInfinity() && !event.isCanceled() && !player.isCreative()) {
                event.getBulletStack().shrink(event.getShrink());
            }
        }
    }

    @Override
    public ResourceLocation identifier() {
        return null;
    }

    public static void sendToServer() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
