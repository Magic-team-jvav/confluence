package org.confluence.mod.mixin.world.entity.projectile;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.confluence.lib.mixed.ILibExtraSyncedData;
import org.confluence.lib.network.s2c.SetEntityDataPacketS2C;
import org.confluence.mod.common.effect.neutral.ShimmerEffect;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IFishingHook;
import org.confluence.mod.common.entity.fishing.FishingSession;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.mod.util.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin implements IFishingHook {
    @Unique
    private static final byte[] confluence$dataIds = {SetEntityDataPacketS2C.DATA_BOOLEAN};

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Shadow
    private int timeUntilLured;
    @Shadow
    private int timeUntilHooked;
    @Shadow
    protected int nibble;
    @Unique
    private boolean confluence$startingWait;

    /// 只缩放新一轮等待，不改变咬钩窗口，也不逐 tick 重复缩短计时。
    @Inject(method = "catchingFish", at = @At("HEAD"))
    private void beginBiteWait(CallbackInfo ci) {
        confluence$startingWait = nibble <= 0 && timeUntilHooked <= 0 && timeUntilLured <= 0;
    }

    @Inject(method = "catchingFish", at = @At("TAIL"))
    private void adjustBiteWait(CallbackInfo ci) {
        if (confluence$startingWait)
            timeUntilLured = IFishingHook.adjustBiteWait(confluence$self(), timeUntilLured);
    }

    @Unique
    private FishingSession confluence$session;

    @Override
    public FishingSession confluence$getFishingSession() {return confluence$session;}

    @Override
    public void confluence$setFishingSession(FishingSession session) {confluence$session = session;}

    /// 换竿或移走绑定鱼饵即终止本次垂钓；活动期间按变化同步渔力仪表。
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void validateCast(CallbackInfo ci) {
        FishingHook hook = confluence$self();
        if (hook.getPlayerOwner() instanceof ServerPlayer player) {
            if (confluence$session != null && !confluence$session.isValid(player)) {
                hook.discard();
                ci.cancel();
            } else if (confluence$session != null && hook.tickCount % 20 == 0
                    && confluence$session.updateDisplayedPower(PlayerUtils.getFishingPower(player))) {
                FishingPowerInfoPacketS2C.sendToClient(player);
            }
        }
    }

    /// 收线前校验原来的手与鱼饵；禁止另一根钓竿代收，避免扣错耐久或使用错附魔。
    @Inject(method = "retrieve", at = @At("HEAD"), cancellable = true)
    private void validateRetrieve(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        FishingHook hook = confluence$self();
        if (hook.getPlayerOwner() instanceof ServerPlayer player && confluence$session != null
                && (stack != confluence$session.rod() || !confluence$session.isValid(player))) {
            hook.discard();
            cir.setReturnValue(0);
        }
    }

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

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 0))
    private ParticleOptions smokeParticle(ParticleOptions type) {
        return IFishingHook.getBubbleParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 1))
    private ParticleOptions flameParticle(ParticleOptions type) {
        return IFishingHook.getFishingParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 2))
    private ParticleOptions flameParticle2(ParticleOptions type) {
        return IFishingHook.getFishingParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 3))
    private ParticleOptions smokeParticle2(ParticleOptions type) {
        return IFishingHook.getBubbleParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 4))
    private ParticleOptions flameParticle3(ParticleOptions type) {
        return IFishingHook.getFishingParticle(confluence$self(), type);
    }

    @ModifyArg(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 5))
    private ParticleOptions lavaParticle(ParticleOptions type) {
        return IFishingHook.getSplashParticle(confluence$self(), type);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootDataManager;getLootTable(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    private ResourceLocation redirectLootTable(ResourceLocation lootTable) {
        return IFishingHook.redirectLootTable(confluence$self(), lootTable);
    }

    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private ObjectArrayList<ItemStack> resolveCatch(LootTable table, LootParams params, Operation<ObjectArrayList<ItemStack>> original) {
        FishingHook hook = confluence$self();
        if (!(hook.getPlayerOwner() instanceof ServerPlayer player))
            return original.call(table, params);
        float power = IFishingHook.catchPower(hook, player);
        // 只对汇流水中六档表使用渔力权重；原版钓鱼及岩浆、蜂蜜表保留各自上下文。
        LootParams context = hook.getType() != EntityType.FISHING_BOBBER && hook.getInBlockState().getFluidState().is(FluidTags.WATER)
                ? IFishingHook.catchParams(hook, power) : params;
        return IFishingHook.modifyLoot(hook, original.call(table, context), power);
    }

}
