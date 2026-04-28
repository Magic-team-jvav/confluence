package org.confluence.mod.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.entity.AccumulatingEnergyEntity;
import org.confluence.mod.common.init.ModEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements SelfGetter<ServerLevel> {
    @ModifyArg(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0))
    private int modifyFrequency(int bound) {
        if (CommonConfigs.TERRA_STYLE_LIGHTNING_BOLT.get()) {
            return Math.max(bound / CommonConfigs.TERRA_STYLE_LIGHTNING_BOLT_FREQUENCY_MULTIPLIER.get(), 1);
        }
        return bound;
    }

    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private boolean skipVanilla(ServerLevel instance, Entity entity, Operation<Boolean> original) {
        if (CommonConfigs.TERRA_STYLE_LIGHTNING_BOLT.get()) {
            LightningBolt lightningBolt = entity instanceof LightningBolt lb ? lb : null;
            AccumulatingEnergyEntity accumulatingEnergy = new AccumulatingEnergyEntity(ModEntities.ACCUMULATING_ENERGY.get(), confluence$self(), lightningBolt);
            accumulatingEnergy.moveTo(entity.position());
            return original.call(instance, accumulatingEnergy);
        }
        return original.call(instance, entity);
    }
}
