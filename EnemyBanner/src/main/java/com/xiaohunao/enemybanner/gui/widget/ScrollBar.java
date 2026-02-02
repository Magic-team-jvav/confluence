package com.xiaohunao.enemybanner.gui.widget;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ScrollBar extends AbstractWidget {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected ScrollWidget scrollWidget;
    private ScrollBlock scrollBlock;

    protected ResourceLocation textureLocation;
    protected int textureBarPosX;
    protected int textureBarPosY;
    protected int textureScrollBlockPosX;
    protected int textureScrollBlockPosY;
    protected int scrollBlockWidth;
    protected int scrollBlockHeight;

    private ScrollValue scrollOffs;

    private ScrollBar(int x, int y, int width, int height, ScrollWidget scrollWidget, ResourceLocation textureLocation) {
        super(x, y, width, height, Component.empty());
        this.textureLocation = textureLocation;
        this.scrollWidget = scrollWidget;
        scrollOffs = scrollWidget.getScrollOffs();
    }

    public void init(){
        this.scrollBlock = new ScrollBlock(getX(), getY(), scrollBlockWidth, scrollBlockHeight);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        scrollBlock.setSize(scrollBlockWidth, scrollBlockHeight);
        int offs = (int) scrollOffs.getScrollValue(getWidth() - 2 - scrollBlock.getWidth());
        scrollBlock.setPosition(getX() + 1 + offs , getY());

        guiGraphics.blit(textureLocation, getX(), getY(), textureBarPosX, textureBarPosY, width, height);
        scrollBlock.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }

    public ScrollBlock getScrollBlock(){
        return scrollBlock;
    }

    public class ScrollBlock extends AbstractWidget{
        private boolean mouseHold;
        private double lastMouseX;
        public ScrollBlock(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty());
            this.mouseHold = false;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
            guiGraphics.blit(textureLocation, getX(), getY(), textureScrollBlockPosX, mouseHold ? textureScrollBlockPosY + getHeight() : textureScrollBlockPosY, getWidth(), getHeight());
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            if (mouseHold && scrollWidget.canScroll()){
                double dragX = mouseX - lastMouseX;
                scrollOffs.addScrollValue(dragX, ScrollBar.this.getWidth() - 2 - getWidth());
                lastMouseX = mouseX;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)){
                this.mouseHold = true;
                this.lastMouseX = mouseX;
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            this.mouseHold = false;
            return true;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}
    }

    public static class Builder{

        private ScrollBar scrollBar;

        public Builder(int x, int y, int width, int height, ScrollWidget scrollWidget, ResourceLocation textureLocation){
            scrollBar = new ScrollBar(x, y, width, height, scrollWidget, textureLocation);
        }

        public Builder setTextureBarPos(int x, int y){
            scrollBar.textureBarPosX = x;
            scrollBar.textureBarPosY = y;
            return this;
        }

        public Builder setTextureScrollBlockPos(int x, int y){
            scrollBar.textureScrollBlockPosX = x;
            scrollBar.textureScrollBlockPosY = y;
            return this;
        }

        public Builder setScrollBlockSize(int width, int height){
            scrollBar.scrollBlockWidth = width;
            scrollBar.scrollBlockHeight = height;
            return this;
        }

        public ScrollBar build(){
            scrollBar.init();
            return scrollBar;
        }
    }
}
