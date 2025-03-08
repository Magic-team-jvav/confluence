package org.confluence.mod.integration.jade;

import net.createmod.ponder.enums.PonderKeybinds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.ModUtils;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class PonderComponentProvider implements IBlockComponentProvider {
    public static final PonderComponentProvider INSTANCE = new PonderComponentProvider();
    public static final ResourceLocation UID = Confluence.asResource("jade_ponder_component");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (ModUtils.hasPonderScene(ModUtils.getMinecraftId(BuiltInRegistries.ITEM, blockAccessor.getBlock().asItem()))){
            Player player = blockAccessor.getPlayer();
            int ponderTime = (player.getPersistentData().getInt("ponderTime")) * 2;
            int tracePonderTime = 40 - ponderTime;
            String s = "|".repeat(Math.max(0, ponderTime));
            String s1 = "|".repeat(Math.max(0, tracePonderTime));
            MutableComponent c = Component.literal(s).withStyle(ChatFormatting.GRAY);
            MutableComponent c1 = Component.literal(s1).withStyle(ChatFormatting.DARK_GRAY);
            iTooltip.add(Component.translatable("ponder.ui.hold_to_ponder", ponderKeybind().getTranslatedKeyMessage()));
            iTooltip.add(c.append(c1));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    public static KeyMapping ponderKeybind() {
        return PonderKeybinds.PONDER.getKeybind();
    }
}
