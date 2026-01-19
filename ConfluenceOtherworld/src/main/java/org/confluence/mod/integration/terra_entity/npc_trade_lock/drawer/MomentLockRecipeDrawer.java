package org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer;

import com.xiaohunao.heaven_destiny_moment.common.init.HDMRegistries;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.MomentLock;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public class MomentLockRecipeDrawer extends TradeLockRecipeDrawer {
    @Override
    public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (!(lock instanceof MomentLock momentLock)) {
            return y;
        }

        var size = getRecipeSize();
        guiGraphics.blitSprite(Confluence.asResource("shop_lock/moment"), x, y, size, size);

        drawTooltip(guiGraphics, x, y, size, size, mouseX, mouseY, I18n.get(HDMRegistries.Keys.MOMENT.location().toLanguageKey()) + ": " + I18n.get(momentLock.moment().location().toLanguageKey(HDMRegistries.Keys.MOMENT.location().getPath())));
        return y + size;
    }
}
