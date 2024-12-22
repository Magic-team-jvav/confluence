package org.confluence.mod.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.wiki.MainWikiScreen;
import org.confluence.mod.util.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final ResourceLocation confluence$button = Confluence.asResource("textures/item/sword/copper_short_sword.png");
    @Unique
    private static final MutableComponent confluence$title = Component.translatable("title.confluence.wiki");

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int l = height / 4 + 32;
        addRenderableWidget(GuiUtils.buildIconButton(
                guiGraphics, confluence$button, width / 2 - 124, l + 72, 20, 20, 16, 16,
                button -> minecraft.setScreen(new MainWikiScreen(true)),
                confluence$title
        ));
    }
}
