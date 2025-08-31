package org.confluence.mod.mixed;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ILevelLoadingScreen {
    void confluence$setSecretFlag(long flag);

    Component SSK = Component.literal("obfuscated").withStyle(ChatFormatting.OBFUSCATED); // §k
    Component PLACING_TRAPS = Component.translatable("worldgen.confluence.placing_traps");
    Component GENERATING_BEES = Component.translatable("worldgen.confluence.generating_bees");
    Component GENERATING_WAVY_CAVES = Component.translatable("worldgen.confluence.generating_wavy_caves");
    Component NOT_PLACING_TRAPS = Component.translatable("worldgen.confluence.not_placing_traps");
    Component PLACING_BOULDERS = Component.translatable("worldgen.confluence.placing_boulders");
    int PINK = 0xFF96FF;

    static ILevelLoadingScreen of(LevelLoadingScreen screen) {
        return (ILevelLoadingScreen) screen;
    }
}
