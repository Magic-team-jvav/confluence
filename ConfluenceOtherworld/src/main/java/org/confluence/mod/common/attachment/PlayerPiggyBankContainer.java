package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.block.functional.PiggyBankBlock;

import javax.annotation.Nullable;

public class PlayerPiggyBankContainer extends SimpleContainer implements INBTSerializable<ListTag> {
    @Nullable
    private PiggyBankBlock.Entity activeBank;

    public PlayerPiggyBankContainer() {
        super(9 * 6);
    }

    public void setActiveBank(@Nullable PiggyBankBlock.Entity activeBank) {
        this.activeBank = activeBank;
    }

    public boolean isActiveBank(PiggyBankBlock.Entity bank) {
        return this.activeBank == bank;
    }

    @Override
    public void fromTag(ListTag tag, HolderLookup.Provider levelRegistry) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (int k = 0; k < tag.size(); k++) {
            CompoundTag compoundtag = tag.getCompound(k);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < getContainerSize()) {
                setItem(j, ItemStack.parse(levelRegistry, compoundtag).orElse(ItemStack.EMPTY));
            }
        }
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
        return (activeBank == null || activeBank.stillValid(player)) && super.stillValid(player);
    }

    @Override
    public void stopOpen(Player player) {
        super.stopOpen(player);
        this.activeBank = null;
    }

    @Override
    public ListTag serializeNBT(HolderLookup.Provider provider) {
        return createTag(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, ListTag nbt) {
        fromTag(nbt, provider);
    }
}
