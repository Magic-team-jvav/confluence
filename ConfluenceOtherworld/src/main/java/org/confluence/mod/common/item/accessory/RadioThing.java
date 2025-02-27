package org.confluence.mod.common.item.accessory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.network.s2c.TheConstantPostEffectPacketS2C;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import top.theillusivec4.curios.api.SlotContext;

public class RadioThing extends BaseCurioItem {
    public RadioThing(Builder builder) {
        super(builder);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer serverPlayer) {
            TheConstantPostEffectPacketS2C.sendToClient(serverPlayer);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof ServerPlayer serverPlayer) {
            TheConstantPostEffectPacketS2C.sendToClient(serverPlayer);
        }
    }
}
