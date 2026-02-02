package org.confluence.terra_curio.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @WrapOperation(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;findTotem(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack replaceTotem(Player player, Operation<ItemStack> original) {
        ItemStack curio = CuriosUtils.findCurio(player, (Predicate<ItemStack>) itemStack -> {
            PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(itemStack);
            return component != null && component.contains(TCItems.TOTEM$WITH$COOLDOWN);
        });
        return curio == null ? original.call(player) : curio;
    }
}
