package org.confluence.mod.mixin.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.util.PlayerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin {
    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ConsumeItemTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;)V"))
    private void achievement(ItemStack stack, Level level, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if (entityLiving.getAirSupply() <= 0 && (stack.is(PotionItems.BOTTLED_WATER) || stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER))) {
            PlayerUtils.awardAchievement((ServerPlayer) entityLiving, "unusual_survival_strategies");
        }
    }
}
