package org.confluence.mod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.util.PrefixUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public abstract class LootTableMixin {
    @Inject(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
    private void initPrefix(CallbackInfo ci, @Local(argsOnly = true) LootParams params, @Local ItemStack stack, @Local RandomSource randomsource) {
        if (PrefixUtils.canInit(stack)) {
            if (ModSecretSeeds.CELEBRATIONMK10.match(params.getLevel())) {
                PrefixUtils.best(randomsource, stack);
            } else {
                PrefixUtils.initPrefix(randomsource, stack);
            }
        }
    }
}
