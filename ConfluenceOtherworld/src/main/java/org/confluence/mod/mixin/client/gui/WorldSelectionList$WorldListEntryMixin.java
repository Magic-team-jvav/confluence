package org.confluence.mod.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldSelectionList$WorldListEntryMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private long confluence$secretFlag;
    @Unique
    private ResourceLocation confluence$worldIcon;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void loadSecretFlag(CallbackInfo ci, @Local(argsOnly = true) LevelSummary summary) {
        try {
            LevelStorageSource.LevelStorageAccess levelStorageAccess = minecraft.getLevelSource().validateAndCreateAccess(summary.getLevelId());
            this.confluence$secretFlag = levelStorageAccess.getDataTag().get("WorldGenSettings").orElseEmptyMap().get("secret_flag").asLong(0L);
            levelStorageAccess.safeClose();
        } catch (Exception ignored) {}
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderSecretFlagIcon(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics guiGraphics, @Local(argsOnly = true, ordinal = 1) int top, @Local(argsOnly = true, ordinal = 2) int left) {
        if (confluence$worldIcon == null) {
            this.confluence$worldIcon = IWorldOptions.getWorldIcon(confluence$secretFlag);
        }
        guiGraphics.blitSprite(confluence$worldIcon, 32, 32, 0, 0, left, top - 1, 32, 32);
    }
}
