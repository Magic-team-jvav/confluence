package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.EnvironmentLock;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof EnvironmentLock environmentLock)) {
            return y;
        }
        var size = getRecipeSize();

        if (environmentLock.environment().graveyard()) {
            guiGraphics.blitSprite(Confluence.asResource("shop_lock/environment_graveyard"), x, y, size, size);
            drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                    I18n.get("bestiary.confluence.filter.graveyard.title"));
            x += size;
        }

        if (environmentLock.environment().biome().isPresent()) {
            List<Component> tooltipLines = new ArrayList<>();
            tooltipLines.add(Component.translatable("terra_entity.trade_lock.drawer.biome.title"));

            for (var biome : environmentLock.environment().biome().get()) {
                guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(TerraEntity.MODID, "shop_lock/biome"), x, y, size, size);
                ResourceKey<Biome> biomeKey = biome.getKey();
                ResourceLocation biomeLocation = biomeKey == null ? null : biomeKey.location();
                String biomeName = biomeLocation == null ? "[unknown biome]" : biomeLocation.toLanguageKey(Registries.BIOME.location().getPath());
                tooltipLines.add(Component.translatable(biomeName));
            }

            drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, tooltipLines);
            x += size;
        }

        if (environmentLock.environment().block().isPresent()) {
            if (environmentLock.environment().block().get().blocks().isPresent()) {
                List<Component> lines = new ArrayList<>();
                for (var block : environmentLock.environment().block().get().blocks().get()) {
                    lines.add(Component.translatable(block.value().getDescriptionId()));
                }
                String icon = "shop_lock/environment_block";
                String groupTitle = "confluence.trade_lock.drawer.environment.block";
                x = drawEnvironmentGroup(guiGraphics, x, y, mouseX, mouseY, environmentLock, lines, icon, groupTitle, size);
            }

            if (environmentLock.environment().block().get().fluids().isPresent()) {
                List<Component> lines = new ArrayList<>();
                for (var block : environmentLock.environment().block().get().fluids().get()) {
                    var liquid = BuiltInRegistries.FLUID.getKey(block.value()).toLanguageKey(Registries.FLUID.location().getPath());
                    lines.add(Component.translatable(liquid));
                }
                String icon = "shop_lock/environment_fluid";
                String groupTitle = "confluence.trade_lock.drawer.environment.fluid";
                x = drawEnvironmentGroup(guiGraphics, x, y, mouseX, mouseY, environmentLock, lines, icon, groupTitle, size);
            }

            if (!environmentLock.environment().block().get().statePredicates().isEmpty()) {
                List<Component> lines = new ArrayList<>();
                for (var statePredicate : environmentLock.environment().block().get().statePredicates()) {
                    String properties = getPredicateProperties(statePredicate);
                    lines.add(Component.literal(properties));
                }
                String icon = "shop_lock/environment_block_state";
                String groupTitle = "confluence.trade_lock.drawer.environment.block_state_predicate";
                x = drawEnvironmentGroup(guiGraphics, x, y, mouseX, mouseY, environmentLock, lines, icon, groupTitle, size);
            }
        }

        return y + size;
    }

    private int drawEnvironmentGroup(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, EnvironmentLock environmentLock, List<Component> tooltipLines2, String icon, String groupTitle, int size) {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(Component.translatable("confluence.trade_lock.drawer.environment.radius", environmentLock.environment().block().get().inflate()));
        tooltipLines.add(Component.translatable(groupTitle).append(":"));
        tooltipLines.addAll(tooltipLines2);
        guiGraphics.blitSprite(Confluence.asResource(icon), x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, tooltipLines);
        return x + size;
    }

    private static @NotNull String getPredicateProperties(StatePropertiesPredicate statePredicate) {
        StringBuilder properties = new StringBuilder();
        for (var entry : statePredicate.properties()) {
            var name = entry.name();
            var valueMatcher = entry.valueMatcher();
            if (valueMatcher instanceof StatePropertiesPredicate.ExactMatcher exactMatcher) {
                properties.append(name).append("=").append(exactMatcher.value()).append(",");
            } else if (valueMatcher instanceof StatePropertiesPredicate.RangedMatcher rangedMatcher) {
                properties.append(name).append("=").append(rangedMatcher.minValue()).append("..").append(rangedMatcher.maxValue()).append(",");
            }
        }
        String propertiesString = properties.toString();
        return propertiesString.substring(0, Math.max(0, propertiesString.length() - 1));
    }
}
