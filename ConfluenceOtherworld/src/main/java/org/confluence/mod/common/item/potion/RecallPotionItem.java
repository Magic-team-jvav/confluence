package org.confluence.mod.common.item.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCSoundEvents;

public class RecallPotionItem extends AbstractPotionItem {
    public RecallPotionItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE));
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 4;
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (living instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getVehicle() != null) {
                serverPlayer.removeVehicle();
            }
            serverPlayer.getCooldowns().addCooldown(this, 5);
            serverPlayer.changeDimension(Player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING));
        }
        living.playSound(TCSoundEvents.TRANSMISSION.get());
    }
}
