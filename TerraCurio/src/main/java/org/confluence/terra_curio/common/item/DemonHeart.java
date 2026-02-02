package org.confluence.terra_curio.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCCommonConfigs;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;

public class DemonHeart extends Item {
    public static final ResourceLocation ID = TerraCurio.asResource("demon_heart");

    public DemonHeart() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.EXPERT).stacksTo(1).fireResistant());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            CuriosApi.getCuriosInventory(player).ifPresent(iCuriosItemHandler -> {
                ICurioStacksHandler iCurioStacksHandler = iCuriosItemHandler.getCurios().get(TerraCurio.CURIO_SLOT);
                if (iCurioStacksHandler != null && iCurioStacksHandler.getSlots() < TCCommonConfigs.MAX_ACCESSORIES.get()) {
                    itemStack.shrink(1);
                    Map<ResourceLocation, AttributeModifier> modifiers = iCurioStacksHandler.getModifiers();
                    double before = modifiers.containsKey(ID) ? modifiers.get(ID).amount() : 0.0;
                    iCurioStacksHandler.removeModifier(ID);
                    iCuriosItemHandler.addPermanentSlotModifier(TerraCurio.CURIO_SLOT, ID, before + 1.0, AttributeModifier.Operation.ADD_VALUE);
                    LibUtils.forMixin$ModifyExpression(serverPlayer);
                }
            });
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
