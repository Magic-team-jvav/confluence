package org.confluence.mod.mixin.client.multiplayer;

import com.llamalad7.mixinextras.sugar.Local;
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
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.handler.WeatherHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements LevelReader, SelfGetter<ClientLevel> {
    @Unique
    private static final RandomSource confluence$random = new LegacyRandomSource(1234567890L);
    @Unique
    private Map<Block, WeatherHandler.Context> confluence$blockParticles;
    @Unique
    private Map<FluidType, ParticleOptions> confluence$fluidParticles;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "animateTick", at = @At("HEAD"))
    private void preAnimateTick(CallbackInfo ci) {
        if (minecraft.player == null) return;
        Holder<Biome> biome = getBiome(minecraft.player.blockPosition());
        this.confluence$blockParticles = WeatherHandler.getBlockParticles(biome);
        this.confluence$fluidParticles = WeatherHandler.getFluidParticles(biome);
    }

    @Inject(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z"))
    private void tick(CallbackInfo ci, @Local(argsOnly = true) BlockPos.MutableBlockPos blockPos, @Local BlockState blockstate, @Local FluidState fluidstate) {
        int i1 = ClientConfigs.showWindParticles;
        if (i1 == 0 || WeatherHandler.windDirection == null) return;
        i1 = 100 - i1;
        int r = -1;
        if (confluence$blockParticles != null) {
            r = i1 == 0 ? 0 : confluence$random.nextInt(i1);
            if (r == 0) {
                WeatherHandler.handleBlock(confluence$self(), confluence$random, blockstate, blockPos, confluence$blockParticles);
            }
        }
        if (confluence$fluidParticles != null && !fluidstate.isEmpty()) {
            if (r == 0 || confluence$random.nextInt(i1) == 0) {
                WeatherHandler.handleFluid(confluence$self(), confluence$random, fluidstate, blockPos, confluence$fluidParticles);
            }
        }
    }
}
