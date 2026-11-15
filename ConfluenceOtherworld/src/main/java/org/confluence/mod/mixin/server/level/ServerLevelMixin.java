package org.confluence.mod.mixin.server.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.ModCommonConfigs;
import org.confluence.mod.common.entity.AccumulatingEnergyEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements SelfGetter<ServerLevel> {
    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0))
    private int modifyFrequency(RandomSource instance, int i, Operation<Integer> original) {
        if (ModCommonConfigs.TERRA_STYLE_LIGHTNING_BOLT.get()) {
            i = Math.max(i / ModCommonConfigs.TERRA_STYLE_LIGHTNING_BOLT_FREQUENCY_MULTIPLIER.get(), 1);
        }
        return original.call(instance, i);
    }

    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private boolean skipVanilla(ServerLevel instance, Entity entity, Operation<Boolean> original) {
        if (ModCommonConfigs.TERRA_STYLE_LIGHTNING_BOLT.get()) {
            LightningBolt lightningBolt = entity instanceof LightningBolt lb ? lb : null;
            AccumulatingEnergyEntity accumulatingEnergy = new AccumulatingEnergyEntity(ModEntities.ACCUMULATING_ENERGY.get(), confluence$self(), lightningBolt);
            accumulatingEnergy.moveTo(entity.position());
            return original.call(instance, accumulatingEnergy);
        }
        return original.call(instance, entity);
    }
}
