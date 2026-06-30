package org.confluence.mod.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.confluence.mod.api.SecretFlagMatcher;
import org.confluence.mod.common.init.ModFeatures;

import java.util.stream.Stream;

public class SecretFlagPlacement extends PlacementModifier implements SecretFlagMatcher {
    public static final Codec<SecretFlagPlacement> CODEC = SecretFlagMatcher.createCodec(SecretFlagPlacement::new).codec();

    private final long flag;
    private final boolean flip;

    public SecretFlagPlacement(long flag, boolean flip) {
        this.flag = flag;
        this.flip = flip;
    }

    /// @see org.confluence.mod.mixed.IWorldOptions
    public static SecretFlagPlacement of(long flag) {
        return new SecretFlagPlacement(flag, false);
    }

    /// @param flip 反向判断
    /// @see org.confluence.mod.mixed.IWorldOptions
    public static SecretFlagPlacement of(long flag, boolean flip) {
        return new SecretFlagPlacement(flag, flip);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        return matchesSecretFlag() ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModFeatures.SECRET_FLAG_PLACEMENT_MODIFIER.get();
    }

    @Override
    public long secretFlag() {
        return flag;
    }

    @Override
    public boolean flipMatch() {
        return flip;
    }
}
