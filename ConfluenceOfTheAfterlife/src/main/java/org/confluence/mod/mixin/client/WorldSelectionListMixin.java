package org.confluence.mod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.confluence.mod.mixed.ILevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldSelectionList.class)
public abstract class WorldSelectionListMixin extends ObjectSelectionList<WorldSelectionList.Entry> {
    public WorldSelectionListMixin(Minecraft p_94442_, int p_94443_, int p_94444_, int p_94445_, int p_94446_) {
        super(p_94442_, p_94443_, p_94444_, p_94445_, p_94446_);
    }

    @Inject(method = "fillLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldSelectionList;addEntry(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)I"))
    private void loadSecretFlags(String filter, List<LevelSummary> levels, CallbackInfo ci) {
        try {
            for (LevelSummary level : levels) {
                LevelStorageSource.LevelStorageAccess levelStorageAccess = minecraft.getLevelSource().validateAndCreateAccess(level.getLevelId());
                long flag = levelStorageAccess.getDataTag().get("WorldGenSettings").orElseEmptyMap().get("secret_flag").asLong(0L);
                ((ILevelSummary) level).confluence$setSecretFlag(flag);
                levelStorageAccess.safeClose();
            }
        } catch (Exception ignored) {}
    }

    @Mixin(WorldSelectionList.WorldListEntry.class)
    public abstract static class WorldListEntryMixin {
        @Shadow
        @Final
        LevelSummary summary;

        @Shadow
        @Final
        private Minecraft minecraft;

        @Inject(method = "render", at = @At("TAIL"))
        private void renderSecretFlagIcon(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick, CallbackInfo ci) {
            long flag = ((ILevelSummary) summary).confluence$getSecretFlag();
            guiGraphics.drawString(minecraft.font, Long.toString(flag), left, top, 0xFFFFFF);
        }
    }
}
