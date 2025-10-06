package org.confluence.mod.common.item.potion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModSoundEvents;

public class RandomTeleportPotionItem extends AbstractPotionItem {
    public static final double RANGE = 200.0;

    public RandomTeleportPotionItem() {
        super(new Properties());
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (!level.isClientSide) {
            double x = living.getX();
            double y = living.getY();
            double z = living.getZ();

            for(int i = 0; i < 16; ++i) {
                double nx = living.getX() + (living.getRandom().nextDouble() - 0.5) * RANGE;
                double ny = Mth.clamp(living.getY() + (living.getRandom().nextDouble() - 0.5) * RANGE, level.getMinBuildHeight(), level.getMinBuildHeight() + ((ServerLevel) level).getLogicalHeight() - 1);
                double nz = living.getZ() + (living.getRandom().nextDouble() - 0.5) * RANGE;
                if (living.isPassenger()) {
                    living.stopRiding();
                }

                Vec3 vec3 = living.position();
                level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(living));
                if (living.randomTeleport(nx, ny, nz, true)) {
                    level.playSound(null, x, y, z, ModSoundEvents.TRANSMISSION.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    living.playSound(ModSoundEvents.TRANSMISSION.get(), 1.0F, 1.0F);
                    break;
                }
            }

            if (living instanceof Player player) {
                player.getCooldowns().addCooldown(this, 20);
            }
        }
    }
}
