package com.xiaohunao.enemybanner.gui.widget;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.BannerParameters;
import com.xiaohunao.enemybanner.gui.BannerBoxScreen;
import com.xiaohunao.enemybanner.items.ItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BannerCheckBox extends AbstractButton {
    public static final Logger LOGGER = LogUtils.getLogger();

    private BannerParameters parameters;
    private int bannerCount;

    private String id;
    private boolean isSelected;
    private int boxX;
    private int boxY;
    private final StringWidget bannerCountWidget;

    private List<Function<BannerCheckBox, Void>> listenerList;

    public BannerCheckBox(String id, BannerParameters parameters, int width, int height, Component message){
        this(id, parameters, 0, 0, width, height, message);
    }

    public BannerCheckBox(String id, BannerParameters parameters, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.parameters = parameters;
        this.bannerCount = 0;
        isSelected = false;
        boxX = 0;
        boxY = 166;
        this.id = id;

        bannerCountWidget = new StringWidget(Component.nullToEmpty(bannerCount + ""), Minecraft.getInstance().font);
        bannerCountWidget.setColor(0xffffff);
        bannerCountWidget.setWidth(15);
        bannerCountWidget.alignCenter();

        listenerList = new ArrayList<>();
    }

    public void setBannerCount(int bannerCount) {
        this.bannerCount = bannerCount;
    }

    @Override
    public void onPress() {
        setSelected(true);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ItemStack stack = ItemRegister.ENEMY_BANNER_PLANE.toStack();
        stack.set(BannerParameters.BANNER_DATA_COMPONENT, parameters);
        boxX = isHovered() || isSelected ? 22 : 0;
        bannerCountWidget.setMessage(Component.nullToEmpty(bannerCount + ""));
        bannerCountWidget.setPosition(getX() + getWidth() - 12, getY() + getHeight() - 11);

        guiGraphics.blit(BannerBoxScreen.BACKGROUND_LOCATION, getX() + 1, getY() + 1, boxX, boxY, 22, 42);
        guiGraphics.renderItem(stack, getX() + getWidth() / 2, getY() + getHeight() / 2, 0, -143);

        guiGraphics.pose().translate(0, 0, 1);
        bannerCountWidget.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
    }

    public void addClickListener(Function<BannerCheckBox, Void> listener){
        this.listenerList.add(listener);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        for (Function<BannerCheckBox, Void> listener : listenerList) {
            listener.apply(this);
        }
    }

    public String getId() {
        return id;
    }

    public BannerParameters getParameters() {
        return parameters;
    }

    public void setParameters(BannerParameters parameters){
        this.parameters = parameters;
    }
}
