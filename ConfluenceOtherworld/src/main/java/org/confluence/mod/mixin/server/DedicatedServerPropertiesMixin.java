package org.confluence.mod.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
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

@Mixin(DedicatedServerProperties.class)
public abstract class DedicatedServerPropertiesMixin {
    @Mutable
    @Shadow
    @Final
    public WorldOptions worldOptions;

    @Inject(method = "<init>", at = @At(value = "NEW", target = "(Lcom/google/gson/JsonObject;Ljava/lang/String;)Lnet/minecraft/server/dedicated/DedicatedServerProperties$WorldDimensionData;"))
    private void modify(CallbackInfo ci, @Local String s) {
        if (!s.isEmpty()) {
            this.worldOptions = ModSecretSeeds.matchSeed(s, worldOptions).left();
        }
    }
}
