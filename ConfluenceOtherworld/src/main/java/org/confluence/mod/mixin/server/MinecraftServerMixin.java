package org.confluence.mod.mixin.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.WorldData;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {
    @Shadow
    public abstract WorldData getWorldData();

    @Unique
    private long confluence$secretFlag;

    @Override
    public void confluence$updateSecretFlag(long flag) {
        IWorldOptions iWorldOptions = IWorldOptions.of(getWorldData().worldGenOptions());
        iWorldOptions.confluence$withSecretFlag(flag);
        this.confluence$secretFlag = iWorldOptions.confluence$getSecretFlag();
    }

    @Override
    public long confluence$getSecretFlag() {
        return confluence$secretFlag;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(CallbackInfo ci) {
        this.confluence$secretFlag = IWorldOptions.of(getWorldData().worldGenOptions()).confluence$getSecretFlag();
    }
}
