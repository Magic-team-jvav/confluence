package org.confluence.mod.mixin.server.commands;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.server.commands.AdvancementCommands$Action$2")
public abstract class AdvancementCommands$Action$2Mixin {
    @Dynamic
    @Inject(method = "perform", at = @At(value = "RETURN", ordinal = 1))
    private void revokeAll(ServerPlayer player, Advancement advancement, CallbackInfoReturnable<Boolean> cir) {
        if (Confluence.MODID.equals(advancement.getId().getNamespace())) {
            String key = Confluence.asPlainId(advancement.getId().getPath().substring(AchievementUtils.PREFIX.length()));
            LibEntityUtils.getOrCreatePersistedData(player).remove(key);
        }
    }
}
