package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public final class SwordProjectilePacketC2S implements IPortPacket.C2S {
    private static final SwordProjectilePacketC2S INSTANCE = new SwordProjectilePacketC2S();
    public static final ResourceLocation ID = Confluence.asResource("sword_projectile");
    public static final PortStreamCodec<ByteBuf, SwordProjectilePacketC2S> STREAM_CODEC = PortPortStreamCodec.unit(INSTANCE);

    private SwordProjectilePacketC2S() {}

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        // TODO: 这是飞龙、波涌之刃的发剑气方式，还要写泰拉刃的
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BaseSwordItem sword && !player.getCooldowns().isOnCooldown(sword)) {
            SwordProjectileComponent data = stack.get(ModDataComponentTypes.SWORD_PROJECTILE);
            if (data == null) return;
            sword.genProjectile(player, stack, data);
            player.getCooldowns().addCooldown(sword, data.getAttackSpeed(player));
            player.swing(InteractionHand.MAIN_HAND, true);
        }
    }

    public static void sendToServer() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
