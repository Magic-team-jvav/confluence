package org.confluence.mod.integration.terra_furniture;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.terra_curio.common.init.TCSoundEvents;
import org.confluence.terra_furniture.references.TFConfluenceRefs;

public class TFReferences {
    public static void init() {
        TFConfluenceRefs.poop_task = (entityNull, serverLevel, pos) -> {
            Entity passenger = entityNull.getFirstPassenger();
            if (passenger == null) return;
            if (passenger instanceof LivingEntity livingEntity) {
                MobEffectInstance instance = livingEntity.getEffect(ModEffects.EXQUISITELY_STUFFED.get());
                if (instance == null) return;

                // Duration is tick, so duration / 20 / 60 is minutes. Then, minutes * level is what Terraria do.
                // Don't worry about spare time, terraria is the same.
                int foodTime = instance.duration / 1200 * (instance.amplifier + 1);
                SimpleContainer container = new SimpleContainer(ModBlocks.POO.toStack(foodTime));
                Containers.dropContents(serverLevel, pos, container);
                ClientboundSoundPacket packet = new ClientboundSoundPacket(
                        TCSoundEvents.FART_SOUND.getHolder().orElseThrow(),
                        SoundSource.BLOCKS,
                        pos.getX(), pos.getY(), pos.getZ(),
                        1, 1, 1
                );
                serverLevel.players().forEach(serverPlayer -> serverPlayer.connection.send(packet));
                livingEntity.removeEffect(ModEffects.EXQUISITELY_STUFFED.get());
            }
        };
    }
}
