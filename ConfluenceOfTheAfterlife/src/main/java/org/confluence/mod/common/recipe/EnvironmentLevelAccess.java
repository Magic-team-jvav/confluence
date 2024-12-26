package org.confluence.mod.common.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class EnvironmentLevelAccess implements ContainerLevelAccess {
    protected @Nullable Level level;
    protected @Nullable BlockPos pos;

    public EnvironmentLevelAccess(@Nullable Level level, @Nullable BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public void initializeIfNeeded(Level pLevel, BlockPos pPos) {
        if (level == null) this.level = pLevel;
        if (pos == null) this.pos = pPos;
    }

    public void initializeIfNeeded(Player player) {
        if (level == null) this.level = player.level();
        if (pos == null) {
            Vec3 eyePosition = player.getEyePosition(0.5F);
            Vec3 lookVector = player.getViewVector(0.5F);
            ClipContext context = new ClipContext(eyePosition, eyePosition.add(lookVector.scale(4.0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.of(player));
            BlockHitResult blockResult = player.level().clip(context);
            if (blockResult.getType() == HitResult.Type.BLOCK) {
                this.pos = blockResult.getBlockPos();
            }
        }
    }

    public @Nullable Level getLevel() {
        return level;
    }

    public @Nullable BlockPos getPos() {
        return pos;
    }

    public <R extends Recipe<?>> boolean matches(R recipe) {
        return true;
    }

    public Optional<Holder<Biome>> getBiome() {
        return level == null || pos == null ? Optional.empty() : Optional.of(level.getBiome(pos));
    }

    public boolean isBiome(Function<Holder<Biome>, Boolean> predicate) {
        return getBiome().map(predicate).orElse(false);
    }

    public Iterable<BlockPos> searchBox(int inflate) {
        return level == null || pos == null ? List.of() : BlockPos.betweenClosed(pos.offset(-inflate, -inflate, -inflate), pos.offset(inflate, inflate, inflate));
    }

    public boolean anyMatch(Predicate<BlockState> predicate, int inflate) {
        if (level != null) {
            for (BlockPos blockPos : searchBox(inflate)) {
                if (predicate.test(level.getBlockState(blockPos))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public <T> @NotNull Optional<T> evaluate(@NotNull BiFunction<Level, BlockPos, T> levelPosConsumer) {
        return level == null || pos == null ? Optional.empty() : Optional.of(levelPosConsumer.apply(level, pos));
    }
}
