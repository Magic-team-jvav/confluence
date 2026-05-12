package org.confluence.mod.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.lib.network.SetEntityDataPacketS2C;
import org.confluence.mod.mixed.IFishingHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin implements IFishingHook {
    @Unique
    private static final byte[] confluence$dataIds = {SetEntityDataPacketS2C.DATA_BOOLEAN};

    @Shadow
    @Final
    public int luck;
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
        IFishingHook.super.confluence$setData(dataId, o);
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

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> isLavaTag(TagKey<Fluid> pTag) {
        return IFishingHook.isValidFluid(confluence$self(), pTag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void achievement(CallbackInfo ci) {
        this.confluence$achievement = confluence$achievement || IFishingHook.checkAchievement(confluence$self());
    }

    @ModifyArg(method = "getOpenWaterTypeForBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> fluidType(TagKey<Fluid> pTag) {
        return IFishingHook.isValidFluid(confluence$self(), pTag);
    }

    @WrapOperation(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean isLavaBlock(BlockState instance, Block block, Operation<Boolean> original) {
        return IFishingHook.isValidBlock(confluence$self(), instance, block, original.call(instance, block));
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 0), index = 0)
    private ParticleOptions smokeParticle(ParticleOptions pType) {
        return IFishingHook.getBubbleParticle(confluence$self(), pType);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 1), index = 0)
    private ParticleOptions flameParticle(ParticleOptions pType) {
        return IFishingHook.getFishingParticle(confluence$self(), pType);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 2), index = 0)
    private ParticleOptions flameParticle2(ParticleOptions pType) {
        return IFishingHook.getFishingParticle(confluence$self(), pType);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 3), index = 0)
    private ParticleOptions smokeParticle2(ParticleOptions pType) {
        return IFishingHook.getBubbleParticle(confluence$self(), pType);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 4), index = 0)
    private ParticleOptions flameParticle3(ParticleOptions pType) {
        return IFishingHook.getFishingParticle(confluence$self(), pType);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 5), index = 0)
    private ParticleOptions lavaParticle(ParticleOptions pType) {
        return IFishingHook.getSplashParticle(confluence$self(), pType);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private LootParams modifyLuck(LootParams params) {
        return IFishingHook.modifyLuck(confluence$self(), params);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private ResourceKey<LootTable> redirectLootTable(ResourceKey<LootTable> lootTableKey) {
        return IFishingHook.redirectLootTable(confluence$self(), lootTableKey);
    }

    @ModifyExpressionValue(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private ObjectArrayList<ItemStack> addCrate(ObjectArrayList<ItemStack> original) {
        return IFishingHook.modifyLoot(confluence$self(), original);
    }
}
