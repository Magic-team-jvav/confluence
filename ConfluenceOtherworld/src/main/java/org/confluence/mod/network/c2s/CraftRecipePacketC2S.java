package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.StationCrafting;

/// 泰拉式合成列表中点击一个配方；max 为 Shift+点击（尽量多做）。服务端重新校验制作站、环境与材料。
public record CraftRecipePacketC2S(ResourceLocation recipe, boolean max) implements IPacketC2S {
    public static final Type<CraftRecipePacketC2S> TYPE = Confluence.createType("craft_recipe");
    public static final StreamCodec<ByteBuf, CraftRecipePacketC2S> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, CraftRecipePacketC2S::recipe,
            ByteBufCodecs.BOOL, CraftRecipePacketC2S::max,
            CraftRecipePacketC2S::new
    );

    @Override
    public Type<CraftRecipePacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        StationCrafting.craft(player, recipe, max);
    }

    public static void sendToServer(ResourceLocation recipe, boolean max) {
        PacketDistributor.sendToServer(new CraftRecipePacketC2S(recipe, max));
    }
}
