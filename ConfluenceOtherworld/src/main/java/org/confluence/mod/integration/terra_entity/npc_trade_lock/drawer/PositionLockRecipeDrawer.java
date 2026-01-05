package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.PositionLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof PositionLock positionLock)) {
            return y;
        }
        List<Component> position = new ArrayList<>();
        position.add(Component.translatable("confluence.trade_lock.drawer.position.title").append(Component.literal(":")));
        updateWith("X", positionLock.x()).ifPresent(position::add);
        updateWith("Y", positionLock.y()).ifPresent(position::add);
        updateWith("Z", positionLock.z()).ifPresent(position::add);
        var size = getRecipeSize();
        guiGraphics.blitSprite(Confluence.asResource("shop_lock/position"), x, y, size, size);
        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, position);
        return y + size;
    }

    private Optional<MutableComponent> updateWith(String axis, MinMaxBounds.Ints bounds) {
        if (bounds.min().isPresent() || bounds.max().isPresent()) {
            var axisComponent = Component.literal(axis + ": ");
            var minPresent = bounds.min().isPresent();
            if (minPresent) {
                axisComponent.append(Component.literal("≥ " + bounds.min().get()));
            }
            if (bounds.max().isPresent()) {
                if (minPresent) {
                    axisComponent.append(Component.literal(" ")).append(Component.translatable("confluence.trade_lock.drawer.position.and")).append(Component.literal(" "));
                }
                axisComponent.append(Component.literal("≤ " + bounds.max().get()));
            }
            return Optional.of(axisComponent);
        }

        return Optional.empty();
    }
}
