package org.confluence.mod.common.event.game.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.MinecartAbilityEvent;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAchievements;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.ILivingEntity;

import static org.confluence.mod.common.attachment.ExtraInventory.EQUIPMENT_START;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class EntityEvents {
    @SubscribeEvent
    public static void entityMount(EntityMountEvent event) {
        Entity beingMounted = event.getEntityBeingMounted();
        if (beingMounted.isRemoved() || !(event.getEntityMounting() instanceof ServerPlayer player)) return;
        if (beingMounted instanceof LivingEntity) {
            ModAchievements.awardAchievement(player, "the_cavalry");
        }
        if (event.isDismounting() && beingMounted instanceof AbstractMinecart minecart) {
            MinecartAbilityEvent.DismountOnMinecart e = NeoForge.EVENT_BUS.post(new MinecartAbilityEvent.DismountOnMinecart(player, minecart));
            ItemStack itemStack = e.getMinecartItem();
            if (e.isCanceled() || itemStack == null) return;
            ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
            if (extraInventory.getMinecart().isEmpty()) {
                extraInventory.setItem(EQUIPMENT_START + 2, itemStack);
            } else {
                player.addItem(itemStack);
            }
            player.vehicle = null;
            minecart.removePassenger(player);
            minecart.discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void entityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable() || !(event.getEntity() instanceof LivingEntity living)) return;
        if (((ILivingEntity) living).confluence$getExtraInvulnerableTicks() > 0) return;

        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) return;

        if (damageSource.is(ModDamageTypes.BOULDER) && living.getType().is(Tags.EntityTypes.BOSSES)) {
            event.setInvulnerable(true); // boss 免疫巨石
        } else if ((damageSource.getEntity() == null || !damageSource.getEntity().getType().is(Tags.EntityTypes.BOSSES)) && living.hasEffect(ModEffects.SHIMMER)) {
            event.setInvulnerable(true); // 微光状态时免疫小怪和环境伤害
        } else if (damageSource.is(DamageTypeTags.IS_FIRE) && living.hasEffect(ModEffects.OBSIDIAN_SKIN)) {
            event.setInvulnerable(true); // 喝黑曜石皮免疫火系
        }
    }
}
