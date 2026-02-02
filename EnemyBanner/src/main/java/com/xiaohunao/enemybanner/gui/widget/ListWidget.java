package com.xiaohunao.enemybanner.gui.widget;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ListWidget extends AbstractWidget {
    public static final Logger LOGGER = LogUtils.getLogger();

    private List<AbstractWidget> children;
    private LinearLayout.Orientation orientation;

    public ListWidget(int x, int y){
        this(x, y, 0, 0);
    }

    public ListWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());

        this.children = new ArrayList<>();
        this.orientation = LinearLayout.Orientation.VERTICAL;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateChildrenPos();
        for (AbstractWidget child : children) {
            child.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mouse = orientation == LinearLayout.Orientation.VERTICAL ? mouseY : mouseX;
        for (AbstractWidget widget : children){
            int size = orientation == LinearLayout.Orientation.VERTICAL ? widget.getHeight() : widget.getWidth();
            int pos = orientation == LinearLayout.Orientation.VERTICAL ? widget.getY() : widget.getX();
            if (mouse > pos && mouse < size + pos) {
                widget.mouseClicked(mouseX, mouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void updateChildrenPos(){
        int x = getX();
        int y = getY();

        int longSide = 0;
        int shortSide = 0;

        for (AbstractWidget widget : children){
            widget.setPosition(x, y);
            if (this.orientation == LinearLayout.Orientation.VERTICAL){
                y += widget.getHeight();
                longSide += widget.getHeight();
                shortSide = Math.max(shortSide, widget.getWidth());
            } else {
                x += widget.getWidth();
                longSide += widget.getWidth();
                shortSide = Math.max(shortSide, widget.getHeight());
            }
        }
        if (orientation == LinearLayout.Orientation.VERTICAL){
            width = shortSide;
            height = longSide;
        } else {
            width = longSide;
            height = shortSide;
        }
    }

    public void clear(){
        this.children = new ArrayList<>();
    }

    public LinearLayout.Orientation getOrientation(){
        return this.orientation;
    }

    public void setOrientation(LinearLayout.Orientation orientation){
        this.orientation = orientation;
    }

    public void add(AbstractWidget widget){
        this.children.add(widget);
    }

    public boolean remove(AbstractWidget widget){
        return children.remove(widget);
    }

    public AbstractWidget remove(int index){
        return children.remove(index);
    }

    public AbstractWidget getChild(int index){
        return this.children.get(index);
    }

    public List<AbstractWidget> getChild() {
        return children;
    }
}
