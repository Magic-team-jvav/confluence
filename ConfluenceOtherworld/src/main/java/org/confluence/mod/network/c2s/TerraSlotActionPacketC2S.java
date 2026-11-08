package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.TerraInventoryActions;

/// 泰拉背包里的单格操作：Ctrl+点击进垃圾桶、右键装备；以及“快速堆叠到附近箱子”按钮（slot 忽略）。
public record TerraSlotActionPacketC2S(byte action, int slot) implements IPacketC2S {
    public static final Type<TerraSlotActionPacketC2S> TYPE = Confluence.createType("terra_slot_action");
    public static final StreamCodec<ByteBuf, TerraSlotActionPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, TerraSlotActionPacketC2S::action,
            ByteBufCodecs.VAR_INT, TerraSlotActionPacketC2S::slot,
            TerraSlotActionPacketC2S::new
    );

    @Override
    public Type<TerraSlotActionPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        TerraInventoryActions.slotAction(player, action, slot);
    }

    public static void sendToServer(byte action, int slot) {
        PacketDistributor.sendToServer(new TerraSlotActionPacketC2S(action, slot));
    }
}
