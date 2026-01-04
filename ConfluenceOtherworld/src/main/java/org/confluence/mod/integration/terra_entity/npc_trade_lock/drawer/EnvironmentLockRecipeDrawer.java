package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.EnvironmentLock;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.NotNull;

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
            for (var biome : environmentLock.environment().biome().get()) {
                guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(TerraEntity.MODID, "shop_lock/biome"), x, y, size, size);
                ResourceKey<Biome> biomeKey = biome.getKey();
                ResourceLocation biomeLocation = biomeKey == null ? null : biomeKey.location();
                String biomeName = biomeLocation == null ? "[unknown biome]" : biomeLocation.getPath();
                drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                        I18n.get("terra_entity.trade_lock.drawer.biome.title") + ": " + biomeName);
                x += size;
            }
        }

        if (environmentLock.environment().block().isPresent()) {
            if (environmentLock.environment().block().get().blocks().isPresent()) {
                for (var block : environmentLock.environment().block().get().blocks().get()) {
                    guiGraphics.blitSprite(Confluence.asResource("shop_lock/block"), x, y, size, size);
                    drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                            I18n.get("confluence.trade_lock.drawer.environment.block") + ": " + I18n.get(BuiltInRegistries.BLOCK.getKey(block.value()).toLanguageKey()));
                    x += size;
                }
            }

            if (environmentLock.environment().block().get().fluids().isPresent()) {
                for (var fluid : environmentLock.environment().block().get().fluids().get()) {
                    guiGraphics.blitSprite(Confluence.asResource("shop_lock/fluid"), x, y, size, size);
                    drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY,
                            I18n.get("confluence.trade_lock.drawer.environment.fluid") + ": " + I18n.get(BuiltInRegistries.FLUID.getKey(fluid.value()).toLanguageKey()));
                    x += size;
                }
            }

            if (!environmentLock.environment().block().get().statePredicates().isEmpty()) {
                for (var statePredicate : environmentLock.environment().block().get().statePredicates()) {
                    String properties = getPredicateProperties(statePredicate);
                    guiGraphics.blitSprite(Confluence.asResource("shop_lock/block_state"), x, y, size, size);
                    drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get("confluence.trade_lock.drawer.environment.block_state_predicate") + ": " + properties);
                    x += size;
                }
            }
        }

        return y + size;
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
        return properties.toString();
    }
}
