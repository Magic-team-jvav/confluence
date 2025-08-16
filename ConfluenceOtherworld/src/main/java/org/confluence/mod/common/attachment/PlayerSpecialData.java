package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;

public class PlayerSpecialData implements INBTSerializable<CompoundTag> {
    private ItemStack currentQuestedFish = ItemStack.EMPTY;
    private ITradeLock currentQuestedFishCondition = ITradeLock.alwaysTrue();

    public void setCurrentQuestedFish(ItemStack cost, ITradeLock lock) {
        this.currentQuestedFish = cost;
        this.currentQuestedFishCondition = lock;
    }

    public void removeCurrentQuestedFish() {
        setCurrentQuestedFish(ItemStack.EMPTY, ITradeLock.alwaysTrue());
    }

    public ItemStack getCurrentQuestedFish(Player player) {
        if (currentQuestedFishCondition.canTrade(player, ITradeHolder.dummy(player), 0)) {
            return currentQuestedFish;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        CompoundTag tag = new CompoundTag();
        tag.put("CurrentQuestedFish", ItemStack.OPTIONAL_CODEC.encodeStart(ops, currentQuestedFish).result().orElseGet(CompoundTag::new));
        tag.put("CurrentQuestedFishCondition", ITradeLock.TYPED_CODEC.encodeStart(ops, currentQuestedFishCondition).result().orElseGet(CompoundTag::new));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        this.currentQuestedFish = ItemStack.OPTIONAL_CODEC.parse(ops, nbt.get("CurrentQuestedFish")).result().orElse(ItemStack.EMPTY);
        this.currentQuestedFishCondition = ITradeLock.TYPED_CODEC.parse(ops, nbt.get("CurrentQuestedFishCondition")).result().orElse(ITradeLock.alwaysTrue());
    }

    public static PlayerSpecialData of(Player player) {
        return player.getData(ModAttachmentTypes.SPECIAL_DATA);
    }
}
