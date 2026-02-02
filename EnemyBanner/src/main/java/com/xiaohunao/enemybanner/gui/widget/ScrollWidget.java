package com.xiaohunao.enemybanner.gui.widget;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ScrollWidget extends AbstractWidget {
    public static final Logger LOGGER = LogUtils.getLogger();

    private AbstractWidget child;
    private LinearLayout.Orientation orientation;
    private ScrollValue scrollOffs;
    private int scrollRate;

    public ScrollWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        orientation = LinearLayout.Orientation.VERTICAL;
        scrollOffs = new ScrollValue();
        scrollRate = 4;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
        double offs = scrollOffs.getScrollValue(getLongSide(child) + 2 - getLongSide(this));
        if (orientation == LinearLayout.Orientation.VERTICAL)
            child.setY((int) (getY() - offs));
        else
            child.setX((int) (getX() - offs));
        child.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.child.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (canScroll())
            scrollOffs.addScrollValue(-scrollY * scrollRate, getLongSide(child) + 2);
        return true;
    }

    private int getLongSide(AbstractWidget widget){
        if (orientation == LinearLayout.Orientation.VERTICAL)
            return widget.getHeight();
        else
            return widget.getWidth();
    }

    private int getShortSide(AbstractWidget widget){
        if (orientation == LinearLayout.Orientation.VERTICAL)
            return widget.getWidth();
        else
            return widget.getHeight();
    }

    public AbstractWidget getChild() {
        return child;
    }

    public void setChild(AbstractWidget child){
        this.child = child;
        this.child.setPosition(getX(), getY());
    }

    public boolean canScroll(){
        return getLongSide(this) < getLongSide(child);
    }

    public int getScrollRate(){
        return scrollRate;
    }

    public void setScrollRate(int scrollRate) {
        this.scrollRate = scrollRate;
    }

    public void setOrientation(LinearLayout.Orientation orientation) {
        this.orientation = orientation;
    }

    public ScrollValue getScrollOffs(){
        return scrollOffs;
    }

    public void setScrollOffs(ScrollValue scrollOffs){
        this.scrollOffs = scrollOffs;
    }
}
