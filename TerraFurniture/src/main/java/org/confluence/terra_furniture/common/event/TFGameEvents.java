package org.confluence.terra_furniture.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.block.misc.BathtubBlock;

@EventBusSubscriber(modid = TerraFurniture.MODID)
public final class TFGameEvents {
    @SubscribeEvent
    public static void canPlayerSleep(CanPlayerSleepEvent event) {
        if (event.getState().getBlock() instanceof BathtubBlock) {
            event.setProblem(null);
        }
    }

    @SubscribeEvent
    public static void canContinueSleeping(CanContinueSleepingEvent event) {
        if (event.mayContinueSleeping()) return;
        LivingEntity living = event.getEntity();
        living.getSleepingPos().ifPresent(pos -> {
            if (living.level().getBlockState(pos).getBlock() instanceof BathtubBlock) {
                event.setContinueSleeping(true);
            }
        });
    }
}
