package org.confluence.mod.common.event.game.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.MinecartAbilityEvent;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.entity.npc.AbstractTerraNPC;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.mod.mixin.accessor.EntityAccessor;
import org.confluence.mod.network.s2c.ExtraInventorySyncPacketS2C;

import static org.confluence.mod.common.attachment.ExtraInventory.EQUIPMENT_START;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class EntityEvents {
    @SubscribeEvent
    public static void entityMount(EntityMountEvent event) {
        if (event.isMounting() || event.getLevel().isClientSide || event.getEntityBeingMounted().isRemoved()) return;
        if (event.getEntityMounting() instanceof Player player && event.getEntityBeingMounted() instanceof AbstractMinecart minecart) {
            MinecartAbilityEvent.DismountOnMinecart e = NeoForge.EVENT_BUS.post(new MinecartAbilityEvent.DismountOnMinecart(player, minecart));
            ItemStack itemStack = e.getMinecartItem();
            if (e.isCanceled() || itemStack == null) return;
            ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
            if (extraInventory.getMinecart().isEmpty()) {
                extraInventory.setItem(EQUIPMENT_START + 2, itemStack);
            } else {
                player.addItem(itemStack);
            }
            ((EntityAccessor) player).setVehicle(null);
            ((EntityAccessor) minecart).callRemovePassenger(player);
            minecart.discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void entityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable() || !(event.getEntity() instanceof LivingEntity living)) return;
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.GENERIC_KILL)) return;

        if (damageSource.is(ModDamageTypes.BOULDER) && living.getType().is(Tags.EntityTypes.BOSSES)) {
            event.setInvulnerable(true); // boss 免疫巨石
        } else if ((damageSource.getEntity() == null || !damageSource.getEntity().getType().is(Tags.EntityTypes.BOSSES)) && living.hasEffect(ModEffects.SHIMMER)) {
            event.setInvulnerable(true); // 微光状态时免疫小怪和环境伤害
        } else if (damageSource.is(DamageTypeTags.IS_FIRE) && living.hasEffect(ModEffects.OBSIDIAN_SKIN)) {
            event.setInvulnerable(true); // 喝黑曜石皮免疫火系
        }
    }

    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ExtraInventorySyncPacketS2C.sendToClient(serverPlayer, serverPlayer, serverPlayer.getData(ModAttachmentTypes.EXTRA_INVENTORY));
        }
    }

    // 打开戴夫商店
    @SubscribeEvent
    public static void interactEntity(PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof AbstractTerraNPC npc) {
            ((IPlayer) event.getEntity()).rhyme$setDaveTrades(npc.trades);
            ((IPlayer) event.getEntity()).rhyme$setInteractingEntity(npc);
        }
    }
}
