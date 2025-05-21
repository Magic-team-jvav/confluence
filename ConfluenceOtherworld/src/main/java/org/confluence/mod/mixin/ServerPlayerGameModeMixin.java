package org.confluence.mod.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;
    @Unique
    private boolean confluence$finished = false;

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V"))
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (confluence$finished) return;
        CompoundTag data = LibUtils.getOrCreatePersistedData(player);
        int amount = data.getInt("confluence:block_destroyed");
        data.putInt("confluence:block_destroyed", amount + 1);
        if (amount >= 9999) {
            AdvancementHolder advancement = player.server.getAdvancements().get(AchievementOffsetLoader.asAchievement("bulldozer"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "never");
            }
            this.confluence$finished = true;
        }
    }
}
