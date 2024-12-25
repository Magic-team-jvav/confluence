package org.confluence.mod.mixin.entity;

import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.fishing.IBait;
import org.confluence.mod.mixed.IFishingHook;
import org.confluence.mod.mixin.accessor.LootParamsAccessor;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.confluence.terra_curio.util.TCUtils;
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
public abstract class FishingHookMixin implements IFishingHook, SelfGetter<FishingHook> {
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_LAVA = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.BOOLEAN);

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Shadow
    @Final
    private int luck;
    @Unique
    private ItemStack confluence$bait = null;
    @Unique
    private boolean confluence$achievement = false;

    @Unique
    @Override
    public void confluence$setIsLavaHook() {
        self().getEntityData().set(DATA_LAVA, true);
    }

    @Unique
    @Override
    public boolean confluence$isLavaHook() {
        return self().getEntityData().get(DATA_LAVA);
    }

    @Unique
    @Override
    public @Nullable ItemStack confluence$getBait() {
        return confluence$bait;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> isLavaTag(TagKey<Fluid> pTag) {
        if (confluence$isLavaHook()) return ModTags.FISHING_ABLE;
        return ModTags.NOT_LAVA;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void achievement(CallbackInfo ci) {
        if (!confluence$achievement && confluence$isLavaHook() && confluence$isInLava() && getPlayerOwner() instanceof ServerPlayer serverPlayer) {
            PlayerUtils.awardAchievement(serverPlayer, "hot_reels");
            this.confluence$achievement = true;
        }
    }

    @WrapOperation(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean isLavaBlock(BlockState instance, Block block, Operation<Boolean> original) {
        if (confluence$isLavaHook()) return original.call(instance, block) || instance.is(Blocks.LAVA) || instance.is(ModBlocks.HONEY.get()) || instance.is(ModBlocks.SHIMMER.get());
        return original.call(instance, block) || instance.is(ModBlocks.HONEY.get()) || instance.is(ModBlocks.SHIMMER.get());
    }

    @ModifyArg(method = "getOpenWaterTypeForBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> fluidType(TagKey<Fluid> pTag) {
        if (confluence$isLavaHook()) return ModTags.FISHING_ABLE;
        return ModTags.NOT_LAVA;
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 0), index = 0)
    private ParticleOptions smokeParticle(ParticleOptions pType) {
        if (confluence$isInLava()) return ParticleTypes.SMOKE;
        return pType;
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 1), index = 0)
    private ParticleOptions flameParticle(ParticleOptions pType) {
        if (confluence$isInLava()) return ParticleTypes.FLAME;
        return pType;
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 2), index = 0)
    private ParticleOptions flameParticle2(ParticleOptions pType) {
        if (confluence$isInLava()) return ParticleTypes.FLAME;
        return pType;
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 3), index = 0)
    private ParticleOptions smokeParticle2(ParticleOptions pType) {
        if (confluence$isInLava()) return ParticleTypes.SMOKE;
        return pType;
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 4), index = 0)
    private ParticleOptions flameParticle3(ParticleOptions pType) {
        if (confluence$isInLava()) return ParticleTypes.FLAME;
        return pType;
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 5), index = 0)
    private ParticleOptions lavaParticle(ParticleOptions pType) {
        if (confluence$isInLava()) return ParticleTypes.LAVA;
        return pType;
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private LootParams modifyLuck(LootParams pParams) {
        Player owner = getPlayerOwner();
        AtomicDouble fishing = new AtomicDouble(luck);
        if (owner != null) {
            fishing.addAndGet(TCUtils.getAccessoriesValue(owner, AccessoryItems.FISHING$POWER));
            Inventory inventory = owner.getInventory();
            float bonus = 1.0F;
            for (ItemStack itemStack : inventory.offhand) {
                if (itemStack.getItem() instanceof IBait iBait) {
                    this.confluence$bait = itemStack;
                    bonus += iBait.getBaitBonus();
                    break;
                }
                this.confluence$bait = null;
            }
            if (confluence$bait == null) {
                for (ItemStack itemStack : inventory.items) {
                    if (itemStack.getItem() instanceof IBait iBait) {
                        this.confluence$bait = itemStack;
                        bonus += iBait.getBaitBonus();
                        break;
                    }
                }
            }
            if (confluence$bait != null) fishing.set(fishing.get() * bonus);
        }
        ((LootParamsAccessor) pParams).setLuck(fishing.floatValue());
        return pParams;
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void define(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_LAVA, false);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private ResourceKey<LootTable> modifyLoot(ResourceKey<LootTable> lootTableKey) {
        if (confluence$isInLava()) return ModLootTables.FISHING_LAVA;
        if (self().getType() == EntityType.FISHING_BOBBER) return lootTableKey;
        return ModLootTables.FISH;
    }

    @Unique
    private boolean confluence$isInLava() {
        return self().getInBlockState().getFluidState().is(FluidTags.LAVA);
    }

    @ModifyExpressionValue(method = "retrieve",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private ObjectArrayList<ItemStack> addCrate(ObjectArrayList<ItemStack> original) {
        if (getPlayerOwner() instanceof ServerPlayer serverPlayer) {
            float chance = serverPlayer.hasEffect(ModEffects.CRATE) ? 0.25F : 0.1F;
            ServerLevel level = serverPlayer.serverLevel();
            if (level.random.nextFloat() < chance) {
                LootParams lootparams = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, self().position())
                        .withParameter(LootContextParams.THIS_ENTITY, self())
                        .create(LootContextParamSets.GIFT);
                LootTable loottable = level.getServer().reloadableRegistries().getLootTable(ModLootTables.CRATE);
                original.addAll(loottable.getRandomItems(lootparams));
            }
        }
        return original;
    }
}
