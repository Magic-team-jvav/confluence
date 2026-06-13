package org.confluence.mod.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class MinecartAbilityEvent extends PlayerEvent {
    protected @Nullable AbstractMinecart minecart;

    public MinecartAbilityEvent(Player player, @Nullable AbstractMinecart minecart) {
        super(player);
        this.minecart = minecart;
    }

    public @Nullable AbstractMinecart getMinecart() {
        return minecart;
    }

    @Cancelable
    public static class RightClickRailBlock extends MinecartAbilityEvent {
        private final ItemStack minecartItem;
        private final BlockState blockState;
        private final BaseRailBlock railBlock;
        private final BlockPos blockPos;

        public RightClickRailBlock(Player player, ItemStack minecartItem, BlockState blockState, BaseRailBlock railBlock, BlockPos blockPos) {
            super(player, null);
            this.minecartItem = minecartItem;
            this.blockState = blockState;
            this.railBlock = railBlock;
            this.blockPos = blockPos;
        }

        public ItemStack getMinecartItem() {
            return minecartItem;
        }

        public BlockState getBlockState() {
            return blockState;
        }

        public BaseRailBlock getRailBlock() {
            return railBlock;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public void setMinecart(@NotNull AbstractMinecart minecart) {
            this.minecart = minecart;
        }
    }

    @Cancelable
    public static class DismountOnMinecart extends MinecartAbilityEvent {
        private @Nullable ItemStack minecartItem;

        public DismountOnMinecart(Player player, AbstractMinecart minecart) {
            super(player, minecart);
        }

        public void setMinecartItem(@NotNull ItemStack minecartItem) {
            this.minecartItem = minecartItem;
        }

        public @Nullable ItemStack getMinecartItem() {
            return minecartItem;
        }

        @Override
        public @NotNull AbstractMinecart getMinecart() {
            return Objects.requireNonNull(minecart);
        }
    }
}
