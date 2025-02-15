package org.confluence.mod.mixin.integration.jade;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import snownee.jade.api.BlockAccessor;

@Pseudo
@Mixin(targets = "snownee.jade.addon.core.ObjectNameProvider$ForBlock", remap = false)
public abstract class ObjectNameProvider$ForBlockMixin {
    @ModifyArg(method = "appendTooltip(Lsnownee/jade/api/ITooltip;Lsnownee/jade/api/BlockAccessor;Lsnownee/jade/api/config/IPluginConfig;)V", at = @At(value = "INVOKE", target = "Lsnownee/jade/api/theme/IThemeHelper;title(Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private Object modify(Object o, @Local(argsOnly = true) BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof DeathChestBlock.Entity entity) {
            if (entity.variant == BaseChestBlock.Variant.UNLOCKED_NORMAL) {
                return Blocks.CHEST.getName();
            }
            return Component.translatable("block.confluence.base_chest_block." + entity.variant.getSerializedName());
        }
        return o;
    }
}
