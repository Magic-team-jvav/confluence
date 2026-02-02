package org.confluence.terraentity.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.attachment.SummonerAttachment;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.TEAttributes;

import java.util.List;

public class SentryItem<T extends Mob & ISummonMob> extends SummonItem<T> {

    public SentryItem(Properties properties, DeferredHolder<EntityType<?>, EntityType<T>> entityType, int consume, float baseAttackDamage, List<Component> tooltip) {
        super(properties, entityType, consume, baseAttackDamage, TEAttachments.SENTRY_STORAGE, tooltip);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer==null)return;
        float additionAttackDamage = (float) localPlayer.getAttributeValue(TEAttributes.SUMMON_DAMAGE) - 1;
        tooltipComponents.add(Component.translatable("attribute.name.player.summon_damage").append(": " +
                        (baseAttackDamage + (additionAttackDamage > 0 ? "  +%d%%".formatted((int)(additionAttackDamage * 100)): "")))
                .withColor(0x00AB00));

        tooltipComponents.add(Component.translatable("tooltip.terra_entity.sentry_item_cost", consume).withColor(0xABAC00));
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.sentry_item_entity", entityType.get().getDescription()).withColor(0x1E90FF));

        var data = localPlayer.getData(summonType.get());
        int a = data.getCurrentCapacity();
        int b = SummonerAttachment.getMaxCapacity(localPlayer);
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.sentry_info", b - a, b).withColor(a <= 0 ? 0xAB0000 : 0x00ABAC));
    }

}
