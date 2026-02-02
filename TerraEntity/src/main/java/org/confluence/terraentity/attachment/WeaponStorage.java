package org.confluence.terraentity.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.terraentity.entity.proj.YoyosEntity;
import org.confluence.terraentity.init.TEAttachments;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

public class WeaponStorage implements INBTSerializable<CompoundTag> {
    private final Map<Item , Integer> boomerangCounter = new HashMap<>();
    public boolean bowFullPull = false;
    public boolean leftClicking = false;
    public YoyosEntity yoyosEntity = null;


    public int tryReduce(Item item){
        return boomerangCounter.compute(item, (k, c) -> c != null && c > 0? c - 1 : 0);
    }
    public int tryIncrease(Item item){
        return boomerangCounter.compute(item, (k, v) -> v == null? 1 : v + 1);
    }
    public int getCount(Item item){
        return boomerangCounter.getOrDefault(item, 0);
    }
    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
//        CompoundTag tag = new CompoundTag();
//        for (Map.Entry<Item, Integer> entry : boomerangCounter.entrySet()) {
//            tag.putInt(entry.getKey().toString(), entry.getValue());
//        }
        return new CompoundTag();
    }


    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
//        for (String key : compoundTag.getAllKeys()) {
//            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(key));
//            boomerangCounter.put(item, compoundTag.getInt(key));
//        }
    }

    public static WeaponStorage of(IAttachmentHolder holder) {
        return holder.getData(TEAttachments.WEAPON_STORAGE);
    }
}
