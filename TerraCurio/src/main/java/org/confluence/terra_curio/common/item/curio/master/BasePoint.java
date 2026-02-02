package org.confluence.terra_curio.common.item.curio.master;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.client.TCClientConfigs;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.confluence.terra_curio.common.init.TCSoundEvents;
import org.confluence.terra_curio.common.item.curio.movement.BaseSpeedBoots;
import org.confluence.terra_curio.network.c2s.SpeedBootsNBTPacketC2S;
import top.theillusivec4.curios.api.SlotContext;

public class BasePoint extends BaseSpeedBoots {
    public BasePoint() {
        super(2, 80, builder("base_point").rarity(ModRarity.MASTER));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living.getBlockStateOn().is(BlockTags.SAND)) {
            speedUp(slotContext, stack, 4, 140);
        } else {
            speedUp(slotContext, stack, 2, 80);
        }
    }

    @Override
    protected void speedUp(SlotContext slotContext, ItemStack stack, int acceleration, int maxSpeed) {
        if (slotContext.entity() instanceof Player player && player.isLocalPlayer()) {
            int speed = LibUtils.getItemStackNbtNoCopy(stack).getInt(KEY);
            if (player.zza != 0.0F || player.xxa != 0.0F) {
                if (TCClientPacketHandler.isHasMagiluminescence()) acceleration *= 3;
                int actually = Math.min(maxSpeed - speed, acceleration);
                int value = speed + actually;
                if (actually > 0) {
                    PacketDistributor.sendToServer(new SpeedBootsNBTPacketC2S(slotContext.index(), value));
                }
                if (player.onGround()) {
                    float ratio = (float) value / maxSpeed;
                    if (TCClientConfigs.playShoesSound && player.level().getGameTime() % (ratio < 0.5F ? 6L : 4L) == 0) {
                        player.playSound(TCSoundEvents.SHOES_WALK.get());
                    }
                }
                if (TCClientConfigs.showShoesParticle) {
                    // todo particle
                }
            } else if (speed != 0) {
                PacketDistributor.sendToServer(new SpeedBootsNBTPacketC2S(slotContext.index(), 0));
            }
        }
    }
}
