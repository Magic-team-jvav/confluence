package org.confluence.mod.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {
    @Shadow
    public abstract WorldData getWorldData();

    @Unique
    private long confluence$secretFlag;

    @Override
    public void confluence$updateSecretFlag(long flag) {
        IWorldOptions iWorldOptions = (IWorldOptions) getWorldData().worldGenOptions();
        iWorldOptions.confluence$withSecretFlag(flag);
        this.confluence$secretFlag = iWorldOptions.confluence$getSecretFlag();
    }

    @Override
    public long confluence$getSecretFlag() {
        return confluence$secretFlag;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initialize(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer fixerUpper, Services services, ChunkProgressListenerFactory progressListenerFactory, CallbackInfo ci) {
        this.confluence$secretFlag = ((IWorldOptions) getWorldData().worldGenOptions()).confluence$getSecretFlag();
    }
}
