package org.confluence.mod.client.gui.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.GuiUtils;

import java.util.ArrayList;

public class MainWikiScreen extends BaseWikiScreen {
    private ArrayList<GroupWikiScreen> groups;
    private boolean isInTitleScreen;

    public MainWikiScreen(boolean isInTitleScreen) {
        super(Component.translatable("title.confluence.wiki"));
        groups = new ArrayList<>();
        groups.add(new GroupWikiScreen(ResourceLocation.fromNamespaceAndPath(
                Confluence.MODID, "textures/item/materials/copper_coin.png")
                , Component.translatable("wiki.confluence.item"), "item",
                TypeWikiScreen.WikiRenderType.ITEM, isInTitleScreen));
        this.isInTitleScreen = isInTitleScreen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int z = groups.size() * 20;
        int l = this.width / 2;
        for (int i = 0; i < groups.size(); i++) {
            int finalI = i;
            Button g = GuiUtils.buildIconButton(guiGraphics, groups.get(finalI).getIcon(),
                    (l - (z / 2)) + (i * 20 + (i * 14)), height - 150, 20, 20, 16, 16,
                    (e) -> this.minecraft.setScreen(groups.get(finalI)),
                    groups.get(finalI).getName());
            this.addRenderableWidget(g);
        }
    }

    @Override
    public boolean useExitIcon() {
        return true;
    }

    @Override
    public Screen getParentScreen() {
        if (isInTitleScreen) {
            return new TitleScreen();
        } else {
            return new PauseScreen(true);
        }
    }
}
