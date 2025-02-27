package org.confluence.mod.mixin.integration.terra_curio;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.common.entity.projectile.StarCloakEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StarCloakEntity.class, remap = false)
public abstract class StarClockEntityMixin {
    @Inject(method = "onHitEntity", at= @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/util/TCUtils;forConfluence$Inject()V"))
    private void receiveMana(EntityHitResult pResult, CallbackInfo ci) {
        if (pResult.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerUtils.receiveMana(serverPlayer, () -> 50);
        }
    }
}
