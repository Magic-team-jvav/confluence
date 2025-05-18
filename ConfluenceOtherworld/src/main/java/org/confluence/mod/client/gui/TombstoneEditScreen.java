package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.joml.Vector3f;

public class TombstoneEditScreen extends AbstractSignEditScreen {
    private static final Vector3f TEXT_SCALE = new Vector3f(1.5F, 1.5F, 1.5F);
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/tombstone.png");
    private static final ResourceLocation GOLD_BACKGROUND = Confluence.asResource("textures/gui/gold_tombstone.png");
    private final boolean isGold;

    public TombstoneEditScreen(SignBlockEntity sign, boolean isFrontText, boolean isFiltered, boolean isGold) {
        super(sign, isFrontText, isFiltered, Component.empty());
        this.isGold = isGold;
    }

    @Override
    protected void renderSignBackground(GuiGraphics guiGraphics, BlockState state) {
        guiGraphics.pose().translate(0, 31, 0);
        guiGraphics.blit(isGold ? GOLD_BACKGROUND : BACKGROUND, -111, -100, 0, 0, 223, 166);
    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }
}
