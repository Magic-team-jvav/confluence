package org.confluence.mod.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.neoforge.common.TranslatableEnum;

import java.util.Locale;

public enum Food implements TranslatableEnum {
    LEGACY {
        @Override
        public void render(GuiGraphics guiGraphics, Minecraft minecraft) {
            Player player = minecraft.player;
            if (player != null) {
                FoodData foodData = player.getFoodData();
                int foodLevel = foodData.getFoodLevel();
            }
        }
    };

    public abstract void render(GuiGraphics guiGraphics, Minecraft minecraft);

    @Override
    public Component getTranslatedName() {
        return Component.translatable("confluence.configuration.foodStyle." + name().toLowerCase(Locale.ROOT));
    }
}
