package org.confluence.mod.network.c2s;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.data.saved.BestiaryEntry;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record GiveBannerPacketC2S(String key) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("give_banner");
    public static final PortStreamCodec<ByteBuf, GiveBannerPacketC2S> STREAM_CODEC = PortByteBufCodecs.STRING_UTF8.map(GiveBannerPacketC2S::new, GiveBannerPacketC2S::key);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        BestiaryEntry entry = Bestiary.INSTANCE.getEntries().get(key);
        if (entry != null && entry.isCompleted()) {
            ItemStack stack = ModItems.ENEMY_BANNER.get().getDefaultInstance();
            PortItemStackExtension.setData(stack, ConfluenceMagicLib.NBT, NbtComponent.create(tag -> tag.putString(AbstractEnemyBannerBlock.TAG_ENTRY_KEY, key)));
            player.addItem(stack);
        }
    }

    public static void sendToServer(ClientBestiaryEntry entry) {
        Confluence.NETWORK_HANDLER.sendToServer(new GiveBannerPacketC2S(entry.key));
    }
}
