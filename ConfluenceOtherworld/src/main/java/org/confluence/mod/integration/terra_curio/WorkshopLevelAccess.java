package org.confluence.mod.integration.terra_curio;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkshopLevelAccess extends EnvironmentLevelAccess {
    public WorkshopLevelAccess(@Nullable Level level, @Nullable BlockPos pos) {
        super(level, pos);
    }

    @Override
    public <R extends Recipe<?>> boolean matches(@NotNull R recipe) {
        if (level == null || pos == null) return false;
        ItemStack resultItem = recipe.getResultItem(level.registryAccess());
        if (resultItem.is(AccessoryItems.BAND_OF_STARPOWER.get()) || resultItem.is(TCItems.PANIC_NECKLACE.get())) {
            return DynamicBiomeUtils.getISection(level, pos).confluence$isGraveyard();
        }
        return true;
    }
}
