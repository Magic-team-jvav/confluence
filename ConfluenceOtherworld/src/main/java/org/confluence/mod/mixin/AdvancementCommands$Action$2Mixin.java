package org.confluence.mod.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.server.commands.AdvancementCommands$Action$2")
public abstract class AdvancementCommands$Action$2Mixin {
    @Inject(method = "perform", at = @At(value = "RETURN", ordinal = 1))
    private void revokeAll(ServerPlayer player, AdvancementHolder advancementHolder, CallbackInfoReturnable<Boolean> cir) {
        if (Confluence.MODID.equals(advancementHolder.id().getNamespace())) {
            String key = Confluence.MODID + ':' + advancementHolder.id().getPath().substring(AchievementUtils.PREFIX.length());
            LibUtils.getOrCreatePersistedData(player).remove(key);
        }
    }
}
