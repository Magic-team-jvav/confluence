package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.sword.BaseSwordItem;

public final class SwordProjectilePacketC2S implements IPacketC2S {
    private static final SwordProjectilePacketC2S INSTANCE = new SwordProjectilePacketC2S();
    public static final Type<SwordProjectilePacketC2S> TYPE = Confluence.createType("sword_projectile");
    public static final StreamCodec<ByteBuf, SwordProjectilePacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private SwordProjectilePacketC2S() {}

    @Override
    public Type<SwordProjectilePacketC2S> type() {
        return TYPE;
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
        }
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }
}
