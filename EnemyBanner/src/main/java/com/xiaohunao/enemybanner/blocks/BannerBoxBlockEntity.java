package com.xiaohunao.enemybanner.blocks;

import com.xiaohunao.enemybanner.gui.BannerBoxMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class BannerBoxBlockEntity extends BaseContainerBlockEntity {
    public static final int SIZE = 3;

    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private final ItemStackHandler itemHandler = new ItemStackHandler(3);
    private int bannerCount;

    public BannerBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockRegister.BANNER_BOX_ENTITY.get(), pos, blockState);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("bannerCount", bannerCount);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        bannerCount = tag.getInt("bannerCount");
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("menu.enemybanner.banner_box.title");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new BannerBoxMenu(containerId, inventory, ContainerLevelAccess.create(getLevel(), getBlockPos()));
    }
    public IItemHandler getItemHandler(){
        return this.itemHandler;
    }

    public int getBannerCount(){
        return bannerCount;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }
}
