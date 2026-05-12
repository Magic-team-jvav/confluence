package org.confluence.mod.mixin.integration.terraentity;

import net.minecraft.server.level.ServerLevel;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.particle.WholeItemParticleOptions;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.entity.monster.slime.GoldenSlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GoldenSlime.class, remap = false)
public abstract class GoldenSlimeMixin implements SelfGetter<GoldenSlime> {
    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void particle(CallbackInfo ci) {
        GoldenSlime living = confluence$self();
        if (living.level() instanceof ServerLevel level) {
            for (int i = 0; i < 3; i++) {
                CoinItem item = PlayerUtils.INDEX_2_COIN.apply(i);
                level.sendParticles(
                        new WholeItemParticleOptions(item.getDefaultInstance(), 1f, 60 + living.getRandom().nextInt(10)),
                        living.getX(), living.getY() + living.getBbHeight() / 2.0, living.getZ(),
                        2, 0.0, 0.5, 0.0, 0.1
                );
            }
        }
    }
}
