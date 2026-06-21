package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.data.saved.NPCSpawner;

public class NPCInvitationItem extends TooltipItem {
    public NPCInvitationItem() {
        super(new Properties().stacksTo(1), ModRarity.WHITE, "tooltip.item.confluence.npc_invitation.0");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            NPCSpawner.Region region = new NPCSpawner.Region(player.chunkPosition());
            for (EntityType<?> entityType : NPCSpawner.INSTANCE.getNpcSpawned()) {
                NPCSpawner.INSTANCE.setNPCAlive(region, entityType, false);
            }
            player.sendSystemMessage(Component.translatable(
                    "event.confluence.npc_invitation",
                    region.x(), region.z(),
                    region.x() + 15, region.z() + 15
            ).withColor(GlobalColors.TIPS.get()));
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
