package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.mana.ManaStaffItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * 左键配置下，法杖与枪械的持续使用状态包。
 *
 * <p>数据包只携带按下或松开，不携带物品、数值或目标。服务端会重新读取主手，并且只允许已经接入
 * 统一武器动作的法杖与枪械进入使用状态。</p>
 */
public record WeaponUseStatePacketC2S(boolean pressed)
        implements IPortPacket.C2S {
    public static final ResourceLocation ID =
            Confluence.asResource("weapon_use_state");
    public static final PortStreamCodec<ByteBuf, WeaponUseStatePacketC2S>
            STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public WeaponUseStatePacketC2S decode(ByteBuf buffer) {
            return new WeaponUseStatePacketC2S(buffer.readBoolean());
        }

        @Override
        public void encode(
                ByteBuf buffer,
                WeaponUseStatePacketC2S packet
        ) {
            buffer.writeBoolean(packet.pressed);
        }
    };

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /**
     * 使用状态属于玩家实体状态，只能在服务端主线程修改。
     */
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        if (!pressed) {
            /*
             * 松开包只能结束本体系统自己启动的持续使用，不能让伪造数据包中断弓、盾牌或其他模组物品的
             * 正常使用。
             */
            ItemStack usingStack = player.getUseItem();
            if (isSupported(usingStack)) {
                player.releaseUsingItem();
            }
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (isSupported(stack)) {
            player.startUsingItem(InteractionHand.MAIN_HAND);
        }
    }

    private static boolean isSupported(ItemStack stack) {
        return stack.getItem() instanceof ManaStaffItem<?>
                || stack.getItem() instanceof BaseGun;
    }

    public static void sendPressed() {
        Confluence.NETWORK_HANDLER.sendToServer(
                new WeaponUseStatePacketC2S(true));
    }

    public static void sendReleased() {
        Confluence.NETWORK_HANDLER.sendToServer(
                new WeaponUseStatePacketC2S(false));
    }
}
