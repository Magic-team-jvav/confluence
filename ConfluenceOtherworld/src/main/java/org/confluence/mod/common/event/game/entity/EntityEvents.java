package org.confluence.mod.common.event.game.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.event.MinecartAbilityEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.entity.PortEntityInvulnerabilityCheckEvent;
import org.mesdag.portlib.event.entity.PortEntityMountEvent;

public final class EntityEvents {
    public static void init() {
        PortEventHandler.addListener(EntityEvents::mount);
        PortEventHandler.addListener(EntityEvents::invulnerabilityCheck);
    }

    private static void mount(PortEntityMountEvent event) {
        Entity beingMounted = event.getEntityBeingMounted();
        if (beingMounted.isRemoved() || !(event.getEntityMounting() instanceof ServerPlayer player)) {
            return;
        }
        if (beingMounted instanceof LivingEntity) {
            AchievementUtils.awardAchievement(player, "the_cavalry");
        }
        if (CommonConfigs.RIGHT_CLICK_RIDE_MINECART.get() && event.isDismounting() && beingMounted instanceof AbstractMinecart minecart) {
            MinecartAbilityEvent.DismountOnMinecart e = PortEventHandler.postEventWithReturn(new MinecartAbilityEvent.DismountOnMinecart(player, minecart));
            ItemStack itemStack = e.getMinecartItem();
            if (e.isCanceled() || itemStack == null) return;
            ExtraInventory extraInventory = ExtraInventory.of(player);
            if (extraInventory.getMinecart(false).isEmpty()) {
                extraInventory.setEquipment(ExtraInventory.MINECART_INDEX, itemStack, false);
            } else {
                player.addItem(itemStack);
            }
            player.vehicle = null;
            minecart.removePassenger(player);
            minecart.discard();
            event.setCanceled(true);
        }
    }

    private static void invulnerabilityCheck(PortEntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable() || !(event.getEntity() instanceof LivingEntity victim)) return;
        if (ILivingEntity.of(victim).confluence$getExtraInvulnerableTicks() > 0) return;

        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) {
            return;
        }
        @Nullable Entity attacker = damageSource.getEntity();

        if (damageSource.is(ModDamageTypes.BOULDER) && victim.getType().is(Tags.EntityTypes.BOSSES)) {
            event.setInvulnerable(true);
            return;
        }
        if ((attacker == null || !attacker.getType().is(Tags.EntityTypes.BOSSES)) && victim.hasEffect(ModEffects.SHIMMER.get())) {
            event.setInvulnerable(true);
            return;
        }
        if (damageSource.is(DamageTypeTags.IS_FIRE)) {
            if (victim.hasEffect(ModEffects.OBSIDIAN_SKIN.get()) || (victim instanceof Player player && ModArmorBonus.hasType(player, ModArmorBonus.LAVA$IMMUNE))) {
                victim.clearFire();
                event.setInvulnerable(true);
                return;
            }
        }
        if (attacker instanceof Player player &&
                !PlayerSpecialData.of(player).isCouldHurtCritters() &&
                !victim.getType().is(ModTags.EntityTypes.CRITTER_COMPANIONSHIP_BLACKLIST) &&
                (LibEntityUtils.isAnimal(victim) || victim.getType().is(ModTags.EntityTypes.CRITTER_COMPANIONSHIP_WHITELIST))
        ) {
            event.setInvulnerable(true);
            return;
        }
        if (attacker instanceof Player a && victim instanceof Player v && (!PlayerSpecialData.of(a).isPvP() || !PlayerSpecialData.of(v).isPvP())) {
            event.setInvulnerable(true);
            return;
        }
        if (CommonConfigs.NPC_INVULNERABLE_TO_PLAYER.get() &&
                victim.getType().is(ModTags.EntityTypes.NPC_INVULNERABLE_TO_PLAYER) &&
                LibEntityUtils.getOwner(damageSource) instanceof Player player &&
                !player.isCreative()
        ) {
            event.setInvulnerable(true);
            return;
        }
        if (ILivingEntity.of(victim).confluence$getImmunityTicks().containsKey(Immunity.getCause(event.getSource()))) {
            event.setInvulnerable(true);
        }
    }
}
