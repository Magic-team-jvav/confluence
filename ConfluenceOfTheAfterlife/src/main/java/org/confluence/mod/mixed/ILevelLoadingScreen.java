package org.confluence.mod.mixed;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.confluence.mod.common.init.ModSecretSeeds;

public interface ILevelLoadingScreen {
    void confluence$setSecretFlag(long flag);

    Component SSK = Component.literal("obfuscated").withStyle(ChatFormatting.OBFUSCATED); // §k
    Component PLACING_TRAPS = Component.translatable("worldgen.confluence.placing_traps");
    Component GENERATING_BEES = Component.translatable("worldgen.confluence.generating_bees");
    Component GENERATING_WAVY_CAVES = Component.translatable("worldgen.confluence.generating_wavy_caves");
    Component NOT_PLACING_TRAPS = Component.translatable("worldgen.confluence.not_placing_traps");
    long DW_MASK = ModSecretSeeds.DRUNK_WORLD.getFlag();
    long NTB_MASK = ModSecretSeeds.NOT_THE_BEES.getFlag();
    long FTW_MASK = ModSecretSeeds.FOR_THE_WORTHY.getFlag();
    long C10_MASK = ModSecretSeeds.CELEBRATIONMK10.getFlag();
    long TC_MASK = ModSecretSeeds.THE_CONSTANT.getFlag();
    long NT_MASK = ModSecretSeeds.NO_TRAPS.getFlag();
    long GFB_MASK = ModSecretSeeds.GET_FIXED_BOI.getFlag();
    int PINK = 0xFF96FF;
}
