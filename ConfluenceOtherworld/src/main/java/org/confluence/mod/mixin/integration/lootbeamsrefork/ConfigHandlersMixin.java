package org.confluence.mod.mixin.integration.lootbeamsrefork;

import com.google.common.collect.ImmutableList;
import me.clefal.lootbeams.config.impl.ModifyingConfigHandler;
import org.confluence.mod.integration.loot_beams_refork.ConfigModRarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "me.clefal.lootbeams.config.ConfigHandlers", remap = false)
public abstract class ConfigHandlersMixin {
    @Shadow private List<ModifyingConfigHandler> handlers;

    @Inject(method = "registerAll", at = @At("TAIL"))
    private void registerModRarity(CallbackInfo ci) {
        if (handlers.stream().noneMatch(handler -> handler.getClass() == ConfigModRarity.class)) {
            this.handlers = ImmutableList.<ModifyingConfigHandler>builder().addAll(handlers).add(new ConfigModRarity()).build();
        }
    }
}
