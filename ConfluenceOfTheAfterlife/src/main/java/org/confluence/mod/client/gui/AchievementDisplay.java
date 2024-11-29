package org.confluence.mod.client.gui;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.Component;

public record AchievementDisplay(AdvancementType type, Component title, Component description) {}
