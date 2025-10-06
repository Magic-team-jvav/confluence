package org.confluence.mod.mixin.integration.jade;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.HammerItems;
import org.confluence.mod.common.init.item.PickaxeItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;

import java.util.List;

@Pseudo
@Mixin(targets = "snownee.jade.addon.harvest.HarvestToolProvider", remap = false)
public abstract class HarvestToolProviderMixin {
    /**
     * @see org.confluence.mod.common.init.ModTiers#isCorrectToolForDrops
     */
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lsnownee/jade/addon/harvest/SimpleToolHandler;create(Lnet/minecraft/resources/ResourceLocation;Ljava/util/List;)Lsnownee/jade/addon/harvest/SimpleToolHandler;", ordinal = 0))
    private static SimpleToolHandler morePickaxe(ResourceLocation uid, List<Item> tools, Operation<SimpleToolHandler> original) {
        return original.call(uid, ImmutableList.builder().addAll(tools).add(
                PickaxeItems.COBALT_PICKAXE.get(),
                PickaxeItems.MYTHRIL_PICKAXE.get(),
                PickaxeItems.ADAMANTITE_PICKAXE.get()
        ).build());
    }

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lsnownee/jade/addon/harvest/HarvestToolProvider;registerHandler(Lsnownee/jade/addon/harvest/ToolHandler;)V", ordinal = 5, shift = At.Shift.AFTER))
    private static void registerMore(CallbackInfo ci) {
        HarvestToolProvider.registerHandler(SimpleToolHandler.create(Confluence.asResource("hammer"), List.of(HammerItems.WOODEN_HAMMER.get(), HammerItems.PWNHAMMER.get())));
    }

    @ModifyExpressionValue(method = "appendTooltip(Lsnownee/jade/api/ITooltip;Lsnownee/jade/api/BlockAccessor;Lsnownee/jade/api/config/IPluginConfig;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
    private float dummyProgress(float original, @Local BlockState state) {
        if (original <= 0.0F && (state.getBlock() instanceof AltarBlock || state.is(ModTags.Blocks.UNBREAKABLE_IF_CANNOT_HARVEST))) {
            return 0.1F;
        }
        return original;
    }
}
