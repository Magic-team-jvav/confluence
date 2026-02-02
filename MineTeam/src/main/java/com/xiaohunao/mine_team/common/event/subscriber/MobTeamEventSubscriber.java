package com.xiaohunao.mine_team.common.event.subscriber;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import com.xiaohunao.mine_team.common.network.TeamAttachmentSyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

@EventBusSubscriber(modid = MineTeam.MODID)
public class MobTeamEventSubscriber {
    @SubscribeEvent
    public static void onPlayerInteractEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (MineTeam.IS_CONFLUENCE_LOADED) return;
        Level level = event.getLevel();
        if (level.isClientSide() || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Player player = event.getEntity();
        ItemStack itemstack = player.getItemInHand(event.getHand());
        Entity target = event.getTarget();
        Ingredient tamingMaterial = MineTeamConfig.getTamingMaterial(target.getType());
        CompoundTag tag = target.getPersistentData();
        if (tamingMaterial.test(itemstack) && target instanceof LivingEntity livingEntity && !tag.contains("teamConversionTime")) {
            ServerLevel serverLevel = (ServerLevel) level;
            PlayerTeam playersTeam = serverLevel.getServer().getScoreboard().getPlayersTeam(target.getScoreboardName());
            if (livingEntity.hasEffect(MobEffects.WEAKNESS) && playersTeam == null) {
                itemstack.consume(1, player);
                int conversionTime = livingEntity.level().random.nextInt(2401) + 3600;
//                int conversionTime = livingEntity.level().random.nextInt(5);
                tag.putInt("teamConversionTime", conversionTime);
                tag.putUUID("teamUid", TeamAttachment.of(player).getTeamUid());
                livingEntity.removeEffect(MobEffects.WEAKNESS);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Math.min(livingEntity.level().getDifficulty().getId() - 1, 0)));
                livingEntity.setGlowingTag(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (MineTeam.IS_CONFLUENCE_LOADED) return;
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide) {
            CompoundTag tag = livingEntity.getPersistentData();
            if (!tag.contains("teamConversionTime")) {
                return;
            }
            int time = tag.getInt("teamConversionTime");
            if (time <= 0) {
                UUID teamUid = tag.getUUID("teamUid");
                ServerLevel serverLevel = (ServerLevel) livingEntity.level();
                TeamAttachment teamAttachment = new TeamAttachment(teamUid, false);
                livingEntity.setData(MTAttachmentTypes.TEAM, teamAttachment);
                PacketDistributor.sendToPlayersInDimension(serverLevel, new TeamAttachmentSyncPayload(livingEntity.getId(), teamAttachment));
                livingEntity.setGlowingTag(true);
                tag.remove("teamConversionTime");
                tag.remove("teamUid");
            } else {
                tag.putInt("teamConversionTime", time - 1);
            }
        }
    }
}