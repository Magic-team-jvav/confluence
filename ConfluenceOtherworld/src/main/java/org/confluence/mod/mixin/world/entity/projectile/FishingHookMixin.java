package org.confluence.mod.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.confluence.lib.mixed.ILibExtraSyncedData;
import org.confluence.lib.network.s2c.SetEntityDataPacketS2C;
import org.confluence.mod.common.effect.neutral.ShimmerEffect;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IFishingHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin implements IFishingHook {
    @Unique
    private static final byte[] confluence$dataIds = {SetEntityDataPacketS2C.DATA_BOOLEAN};

    @Shadow
    @Final
    public int luck;

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Unique
    private boolean confluence$achievement = false;
    @Unique
    private boolean confluence$lavaHook = false;

    @Unique
    @Override
    public void confluence$setIsLavaHook() {
        confluence$setData(SetEntityDataPacketS2C.DATA_BOOLEAN, true);
    }

    @Unique
    @Override
    public boolean confluence$isLavaHook() {
        return confluence$lavaHook;
    }

    @Override
    public void confluence$setData(byte dataId, Object o) {
        ILibExtraSyncedData.defaultSetData(confluence$self(), dataId, o);
        this.confluence$lavaHook = (boolean) o;
    }

    @Override
    public Object confluence$getData(byte dataId) {
        return confluence$lavaHook;
    }

    @Override
    public byte[] confluence$getAllDataId() {
        return confluence$dataIds;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0), cancellable = true)
    private void shimmer(CallbackInfo ci, @Local FluidState fluidstate) {
        if (fluidstate.is(ModTags.Fluids.SHIMMER)) {
            Player player = getPlayerOwner();
            if (player != null) {
                ShimmerEffect.applyShimmerEffect(player, 1);
                confluence$self().discard();
                ci.cancel();
            }
        }
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> isLavaTag(TagKey<Fluid> tag) {
        return IFishingHook.isValidFluid(confluence$self());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void achievement(CallbackInfo ci) {
        this.confluence$achievement = confluence$achievement || IFishingHook.checkAchievement(confluence$self());
    }

    @ModifyArg(method = "getOpenWaterTypeForBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> fluidType(TagKey<Fluid> tag) {
        return IFishingHook.isValidFluid(confluence$self());
    }

    @WrapOperation(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean isLavaBlock(BlockState instance, Block block, Operation<Boolean> original) {
        return IFishingHook.isValidBlock(confluence$self(), instance, original.call(instance, block));
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 0), index = 0)
    private ParticleOptions smokeParticle(ParticleOptions type) {
        return IFishingHook.getBubbleParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 1), index = 0)
    private ParticleOptions flameParticle(ParticleOptions type) {
        return IFishingHook.getFishingParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 2), index = 0)
    private ParticleOptions flameParticle2(ParticleOptions type) {
        return IFishingHook.getFishingParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 3), index = 0)
    private ParticleOptions smokeParticle2(ParticleOptions type) {
        return IFishingHook.getBubbleParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 4), index = 0)
    private ParticleOptions flameParticle3(ParticleOptions type) {
        return IFishingHook.getFishingParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 5), index = 0)
    private ParticleOptions lavaParticle(ParticleOptions type) {
        return IFishingHook.getSplashParticle(confluence$self(), type);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private LootParams modifyLuck(LootParams params) {
        return IFishingHook.modifyLuck(confluence$self(), params);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootDataManager;getLootTable(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private ResourceLocation redirectLootTable(ResourceLocation lootTable) {
        return IFishingHook.redirectLootTable(confluence$self(), lootTable);
    }

    @ModifyExpressionValue(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private ObjectArrayList<ItemStack> addCrate(ObjectArrayList<ItemStack> original) {
        return IFishingHook.modifyLoot(confluence$self(), original);
    }
}
