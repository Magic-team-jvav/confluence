package org.confluence.mod.common.item.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.client.gui.container.WormholeScreen;

public class WormholePotionItem extends AbstractPotionItem {
    public WormholePotionItem(Properties properties) {
        super(properties);
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (!level.isClientSide() || !(living instanceof Player player)) {
            return;
        }
        applyClient();
    }

    public static void applyClient() {
        Minecraft.getInstance().setScreen(WormholeScreen.INSTANCE);
    }
}
