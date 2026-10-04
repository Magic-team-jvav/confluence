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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.MinecartAbilityEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.entity.boss.BossMultiplayerEnhancement;
import org.confluence.mod.common.entity.monster.MonsterAttributeScaling;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Confluence.MODID)
public final class EntityEvents {
    /// 统一处理所有生成路径最终都会触发的实体加入事件。
    ///
    ///
    @SubscribeEvent
    public static void joinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!event.isCanceled() && entity instanceof LivingEntity living) {
            MonsterAttributeScaling.apply(living, !event.loadedFromDisk());
        }
        if (!(entity instanceof Boss boss) || entity.level().isClientSide) {
            return;
        }
        if (!event.loadedFromDisk()) {
            Boss.sendBossSpawnMessage(entity);
        }
        if (boss.isMainBody() && boss.shouldEnhanceMultiplayer() && entity instanceof LivingEntity living) {
            BossMultiplayerEnhancement.apply(living);
        }
    }

    @SubscribeEvent
    public static void mount(EntityMountEvent event) {
        Entity beingMounted = event.getEntityBeingMounted();
        if (beingMounted.isRemoved() || !(event.getEntityMounting() instanceof ServerPlayer player)) {
            return;
        }
        if (beingMounted instanceof LivingEntity) {
            AchievementUtils.awardAchievement(player, "the_cavalry");
        }
        if (CommonConfigs.RIGHT_CLICK_RIDE_MINECART.get() && event.isDismounting() && beingMounted instanceof AbstractMinecart minecart) {
            MinecartAbilityEvent.DismountOnMinecart e = NeoForge.EVENT_BUS.post(new MinecartAbilityEvent.DismountOnMinecart(player, minecart));
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

    @SubscribeEvent
    public static void invulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable() || !(event.getEntity() instanceof LivingEntity victim)) return;
        if (ILivingEntity.of(victim).confluence$getExtraInvulnerableTicks() > 0) return;

        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC_KILL)) {
            return;
        }
        @Nullable Entity attacker = damageSource.getEntity();

        if (damageSource.is(LibDamageTypes.BOULDER) && victim.getType().is(Tags.EntityTypes.BOSSES)) {
            event.setInvulnerable(true); // boss 免疫巨石
            return;
        }
        if ((attacker == null || !attacker.getType().is(Tags.EntityTypes.BOSSES)) && victim.hasEffect(ModEffects.SHIMMER)) {
            event.setInvulnerable(true); // 微光状态时免疫非Boss和环境伤害
            return;
        }
        if (damageSource.is(DamageTypeTags.IS_FIRE)) {
            if (victim.getVehicle() instanceof org.confluence.mod.common.entity.mount.RideableLavaSharkMountEntity) {
                victim.clearFire();
                event.setInvulnerable(true);
                return;
            }
            if (victim.hasEffect(ModEffects.OBSIDIAN_SKIN) || (victim instanceof Player player && ModArmorBonus.hasType(player, ModArmorBonus.LAVA$IMMUNE))) {
                victim.clearFire();
                event.setInvulnerable(true); // 免疫熔岩/着火
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
