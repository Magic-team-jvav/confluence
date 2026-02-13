package org.confluence.mod.mixin.integration.lootr;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Pseudo
@Mixin(targets = "noobanidus.mods.lootr.common.api.replacement.BlockReplacementMap", remap = false)
public abstract class BlockReplacementMapMixin {
    @Shadow
    @Final
    private Set<Block> ignoredBlocks;

    @Inject(method = "clear", at = @At("TAIL"))
    private void addIgnored(CallbackInfo ci) { // where's your blacklist tag?
        ignoredBlocks.addAll(ChestBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::value).toList());
    }
}
