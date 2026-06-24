package org.confluence.mod.common.item.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCSoundEvents;
import org.confluence.terra_curio.common.item.MagicMirror;

public class RecallPotionItem extends AbstractPotionItem {
    public RecallPotionItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 4;
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (living instanceof ServerPlayer player) {
            player.getCooldowns().addCooldown(this, 5);
            MagicMirror.recall(player);
        }
        living.playSound(TCSoundEvents.TRANSMISSION.get());
    }
}
