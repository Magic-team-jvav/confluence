package org.confluence.mod.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAchievements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.server.commands.AdvancementCommands$Action$2")
public abstract class AdvancementCommands$Action$2Mixin { // REVOKE
    @Inject(method = "perform", at = @At(value = "RETURN", ordinal = 1))
    private void revokeAll(ServerPlayer serverPlayer, AdvancementHolder advancementHolder, CallbackInfoReturnable<Boolean> cir) {
        String path = advancementHolder.id().getPath();
        serverPlayer.getPersistentData().remove(Confluence.MODID + ':' + path.substring(ModAchievements.PREFIX.length()));
    }
}
