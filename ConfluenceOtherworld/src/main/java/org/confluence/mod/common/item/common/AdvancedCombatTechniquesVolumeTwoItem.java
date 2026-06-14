package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.network.chat.MutableComponent.PortMutableComponentExtension;
import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.init.ModSoundEvents;

public class AdvancedCombatTechniquesVolumeTwoItem extends TooltipItem {
    public AdvancedCombatTechniquesVolumeTwoItem() {
        super(new Properties(), ModRarity.LIGHT_PURPLE, getTooltipsFromString("advanced_combat_techniques_volume_two", 2, ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel) {
            if (!NPCSpawner.INSTANCE.isAdvancedCombatTechniquesVolumeTwoUsed()) {
                NPCSpawner.INSTANCE.setAdvancedCombatTechniquesVolumeTwoUsed(true);
                ResourceLocation id = Confluence.asResource("advanced_combat_techniques_volume_two");
                serverLevel.getEntities().get(EntityTypeTest.forClass(AbstractTerraNPC.class), npc -> {
                    NPCSpawner.applyAdvancedCombatTechniques(npc, id);
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                });
                MutableComponent component = PortMutableComponentExtension.withColor(Component.translatable("message.confluence.advancement_combat_techniques"), GlobalColors.MESSAGE.get());
                for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
                    serverPlayer.sendSystemMessage(component);
                }
                if (!PortPlayerExtension.hasInfiniteMaterials(player)) {
                    itemStack.shrink(1);
                }
            }
        } else {
            player.playSound(ModSoundEvents.TRANSMUTATION_USE.get());
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
