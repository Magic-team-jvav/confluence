package org.confluence.mod.common.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// todo
public class ShineEffect extends MobEffect {
    public ShineEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFF66);
    }

//    public static void onAdd(Holder<MobEffect> mobEffect, LivingEntity living) {
//        if (mobEffect == ModEffects.SHINE && living instanceof ServerPlayer serverPlayer) {
//            NetworkHandler.CHANNEL.send(
//                PacketDistributor.PLAYER.with(() -> serverPlayer),
//                new PlayerLightPacketS2C(serverPlayer.getUUID(), true)
//            );
//        }
//    }
//
//    public static void onRemove(Holder<MobEffect> mobEffect, LivingEntity living) {
//        if (mobEffect == ModEffects.SHINE && living instanceof ServerPlayer serverPlayer) {
//            boolean curio = CuriosUtils.hasCurio(living, CurioItems.MAGILUMINESCENCE.getPrefab());
//            NetworkHandler.CHANNEL.send(
//                PacketDistributor.PLAYER.with(() -> serverPlayer),
//                new PlayerLightPacketS2C(serverPlayer.getUUID(), curio)
//            );
//        }
//    }
}
