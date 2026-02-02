package org.confluence.terraentity.integration.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.init.item.TERideableItems;
import org.confluence.terraentity.item.RideableItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

public class CuriosHelper {

    public static final String MOUNT_KEY = "mount";
    public static final String PET_KEY = "pet";
    public static final String LIGHT_PET_KEY = "light_pet";


    public static void registerCurios() {
        TERideableItems.ITEMS.getEntries().forEach(item->{
            CuriosApi.registerCurio(item.get(), new ICurioItem() {
                @Override
                public boolean hasCurioCapability(ItemStack stack) {
                    return ICurioItem.super.hasCurioCapability(stack);
                }

                @Override
                public boolean canEquip(SlotContext slotContext, ItemStack stack) {
                    return ICurioItem.super.canEquip(slotContext, stack);
                }
            });
        });

    }

    public static void rideOrLeave(Player player){
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
        if(!player.isPassenger()) {
            curiosInventory.ifPresent(handler -> {
                ICurioStacksHandler itemStackHandler = handler.getCurios().get(CuriosHelper.MOUNT_KEY);
                if (itemStackHandler != null) {
                    IDynamicStackHandler list = itemStackHandler.getStacks();
                    if (list != null && list.getSlots() > 0) {
                        ItemStack stack = list.getStackInSlot(0);
                        if (stack.getItem() instanceof RideableItem<?> rideable) {
                            rideable.summonRideableEntity(player);
                        }
                    }
                }
            });
        }else{
            player.stopRiding();
        }
    }



}
