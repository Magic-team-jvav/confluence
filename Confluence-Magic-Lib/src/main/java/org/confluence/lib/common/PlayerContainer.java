package org.confluence.lib.common;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlayerContainer<C extends BlockEntity & PlayerContainer.ValidEntity> extends SimpleContainer implements INBTSerializable<ListTag> {
    @Nullable
    protected C activeContainer;

    public PlayerContainer(int rows) {
        super(9 * rows);
    }

    public void setActiveContainer(@Nullable C container) {
        this.activeContainer = container;
    }

    public boolean isActiveContainer(C container) {
        return this.activeContainer == container;
    }

    public void setItemNoUpdate(int index, ItemStack stack) {
        getItems().set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));
    }

    @Override
    public void fromTag(ListTag tag, HolderLookup.Provider levelRegistry) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            setItemNoUpdate(i, ItemStack.EMPTY);
        }

        for (int k = 0; k < tag.size(); k++) {
            CompoundTag compoundtag = tag.getCompound(k);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < getContainerSize()) {
                setItemNoUpdate(j, ItemStack.parse(levelRegistry, compoundtag).orElse(ItemStack.EMPTY));
            }
        }

        setChanged();
    }

    @Override
    public ListTag createTag(HolderLookup.Provider levelRegistry) {
        ListTag listtag = new ListTag();

        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack itemstack = getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte) i);
                listtag.add(itemstack.save(levelRegistry, compoundtag));
            }
        }

        return listtag;
    }

    @Override
    public boolean stillValid(Player player) {
        return (activeContainer == null || activeContainer.stillValid(player)) && super.stillValid(player);
    }

    @Override
    public void stopOpen(Player player) {
        super.stopOpen(player);
        this.activeContainer = null;
    }

    @Override
    public ListTag serializeNBT(HolderLookup.Provider provider) {
        return createTag(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, ListTag nbt) {
        fromTag(nbt, provider);
    }

    public interface ValidEntity {
        BlockEntity self();

        default boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(self(), player);
        }
    }
}
