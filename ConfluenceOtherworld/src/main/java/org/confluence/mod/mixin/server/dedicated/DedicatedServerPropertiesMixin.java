package org.confluence.mod.mixin.server.dedicated;

import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Properties;

@Mixin(DedicatedServerProperties.class)
public abstract class DedicatedServerPropertiesMixin {
    @Mutable
    @Shadow
    @Final
    public WorldOptions worldOptions;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modify(Properties properties, CallbackInfo ci) {
        String seed = properties.getProperty("level-seed", "");
        if (!seed.isEmpty()) {
            this.worldOptions = ModSecretSeeds.matchSeed(seed, worldOptions).left();
        }
    }
}
