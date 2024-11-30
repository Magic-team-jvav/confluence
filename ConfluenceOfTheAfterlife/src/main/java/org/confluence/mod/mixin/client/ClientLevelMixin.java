package org.confluence.mod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements LevelReader, SelfGetter<ClientLevel> {
    @Unique
    private Map<Block, ParticleOptions> confluence$blockParticles;
    @Unique
    private Map<FluidType, ParticleOptions> confluence$fluidParticles;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "animateTick", at = @At("HEAD"))
    private void preAnimateTick(int posX, int posY, int posZ, CallbackInfo ci) {
        if (minecraft.player == null) return;
        Holder<Biome> biome = getBiome(minecraft.player.blockPosition());
        this.confluence$blockParticles = WeatherHandler.getBlockParticles(biome);
        this.confluence$fluidParticles = WeatherHandler.getFluidParticles(biome);
    }

    @Inject(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void tick(int posX, int posY, int posZ, int range, RandomSource random, Block block, BlockPos.MutableBlockPos blockPos, CallbackInfo ci, int i, int j, int k, BlockState blockstate, FluidState fluidstate) {
        if (WeatherHandler.windDirection == null) return;
        int r = -1;
        if (confluence$blockParticles != null) {
            r = random.nextInt(10);
            if (r == 0) {
                WeatherHandler.handleBlock(self(), random, blockstate, blockPos, confluence$blockParticles);
            }
        }
        if (confluence$fluidParticles != null && !fluidstate.isEmpty()) {
            if (r == 0 || random.nextInt(10) == 0) {
                WeatherHandler.handleFluid(self(), random, fluidstate, blockPos, confluence$fluidParticles);
            }
        }
    }
}
