package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ForTheWorthy extends SecretSeed {
    public static final BlockState LAVA = Blocks.LAVA.defaultBlockState();

    public ForTheWorthy(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "fortheworthy".equals(seed) || "for the worthy".equals(seed);
    }

    public static Component getDifficultyName(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> Component.translatable("options.difficulty.easy");
            case EASY -> Component.translatable("options.difficulty.normal");
            case NORMAL -> Component.translatable("options.difficulty.hard");
            case HARD -> Component.translatable("options.difficulty.legendary");
        };
    }
}
