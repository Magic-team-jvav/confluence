package org.confluence.mod.mixin.integration.terra_entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = DynamicAnglerTradeTask.class, remap = false)
public abstract class DynamicAnglerTradeTaskMixin {
    @Shadow
    @Final
    private List<ItemStack> costPool;

    @Shadow
    private int currentSelected;

    @ModifyExpressionValue(method = "setNext", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;"))
    private <T> T modify(T original, @Local(argsOnly = true) ITradeHolder npc) {
        ItemStack stack = (ItemStack) original;
        while (!KillBoard.INSTANCE.getGamePhase().isHardmode() && stack.is(ModTags.Items.HARDMODE)) {
            stack = costPool.get(this.currentSelected = npc.getRandom().nextInt(costPool.size()));
        }
        return (T) stack;
    }
}
