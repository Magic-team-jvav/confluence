package org.confluence.mod.common.item.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_curio.common.init.TCSoundEvents;

public class RecallPotionItem extends AbstractPotionItem {
    public RecallPotionItem() {
        super(new Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.BLUE));
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
            serverPlayer.changeDimension(serverPlayer.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING));
        }
        living.playSound(TCSoundEvents.TRANSMISSION.get());
    }
}
