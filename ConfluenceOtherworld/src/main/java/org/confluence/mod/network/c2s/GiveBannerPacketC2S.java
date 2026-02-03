package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.confluence.mod.common.init.item.ModItems;

public record GiveBannerPacketC2S(String key) implements IPacketC2S {
    public static final Type<GiveBannerPacketC2S> TYPE = Confluence.createType("give_banner");
    public static final StreamCodec<ByteBuf, GiveBannerPacketC2S> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(GiveBannerPacketC2S::new, GiveBannerPacketC2S::key);

    @Override
    public void work(ServerPlayer player) {
        BestiaryEntry entry = Bestiary.INSTANCE.getEntries().get(key);
        if (entry != null && entry.isCompleted()) {
            ItemStack stack = ModItems.ENEMY_BANNER.toStack();
            stack.set(ConfluenceMagicLib.NBT, NbtComponent.create(tag -> tag.putString(AbstractEnemyBannerBlock.TAG_ENTRY_KEY, key)));
            player.addItem(stack);
        }
    }

    @Override
    public Type<GiveBannerPacketC2S> type() {
        return TYPE;
    }

    public static void sendToServer(ClientBestiaryEntry entry) {
        PacketDistributor.sendToServer(new GiveBannerPacketC2S(entry.key));
    }
}
