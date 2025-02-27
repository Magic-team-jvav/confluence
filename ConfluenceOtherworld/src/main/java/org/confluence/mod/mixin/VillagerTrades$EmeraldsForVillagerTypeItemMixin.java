package org.confluence.mod.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.confluence.mod.common.init.ModVillagers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EmeraldsForVillagerTypeItem")
public abstract class VillagerTrades$EmeraldsForVillagerTypeItemMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Map<VillagerType, Item> modifyTrades(Map<VillagerType, Item> pTrades) {
        return ImmutableMap.<VillagerType, Item>builder().putAll(pTrades).put(ModVillagers.SKY_TYPE.get(), Items.OAK_BOAT).build();
    }
}
