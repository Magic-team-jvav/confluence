package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixin.world.CompoundContainerAccessor;
import org.confluence.mod.mixin.world.level.block.entity.BaseContainerBlockEntityAccessor;

import java.util.List;

/// 泰拉箱子界面的“重命名”：改当前打开的箱子/木桶的名字（大箱子两半一起改），空名字恢复默认名。
/// 末影箱、矿车箱子、保险箱等不是方块实体容器的不处理。
public record RenameContainerPacketC2S(String name) implements IPacketC2S {
    public static final int MAX_LENGTH = 50;
    public static final Type<RenameContainerPacketC2S> TYPE = Confluence.createType("rename_container");
    public static final StreamCodec<ByteBuf, RenameContainerPacketC2S> STREAM_CODEC = ByteBufCodecs.stringUtf8(MAX_LENGTH * 4)
            .map(RenameContainerPacketC2S::new, RenameContainerPacketC2S::name);

    @Override
    public Type<RenameContainerPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (!(player.containerMenu instanceof ChestMenu menu) || !menu.stillValid(player)) return;
        String trimmed = name.strip();
        if (trimmed.length() > MAX_LENGTH) trimmed = trimmed.substring(0, MAX_LENGTH);
        Component newName = trimmed.isEmpty() ? null : Component.literal(trimmed);
        Container container = menu.getContainer();
        List<Container> parts = container instanceof CompoundContainer compound
                ? List.of(((CompoundContainerAccessor) compound).confluence$getContainer1(), ((CompoundContainerAccessor) compound).confluence$getContainer2())
                : List.of(container);
        for (Container part : parts) {
            if (!(part instanceof BaseContainerBlockEntity blockEntity) || blockEntity.getLevel() == null) continue;
            ((BaseContainerBlockEntityAccessor) blockEntity).confluence$setName(newName);
            blockEntity.setChanged();
            blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
        }
    }

    public static void sendToServer(String name) {
        PacketDistributor.sendToServer(new RenameContainerPacketC2S(name));
    }
}
