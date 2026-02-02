package com.xiaohunao.mine_team.common.event.subscriber;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import com.xiaohunao.mine_team.common.team.Team;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = MineTeam.MODID)
public class LivingEventSubscriber {
    @SubscribeEvent
    public static void onLivingPvP(LivingIncomingDamageEvent event) {
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attackEntity = source.getEntity();
        if (hurtEntity.level() instanceof ServerLevel && attackEntity != null && attackEntity != hurtEntity) {
            if (hurtEntity.hasData(MTAttachmentTypes.TEAM) && attackEntity.hasData(MTAttachmentTypes.TEAM)) {
                TeamAttachment hurtEntityTeam = TeamAttachment.of(hurtEntity);
                TeamAttachment attackEntityTeam = TeamAttachment.of(attackEntity);

                if (!hurtEntityTeam.getTeamUid().equals(attackEntityTeam.getTeamUid())){
                    return;
                }
                if (!hurtEntityTeam.isCanPvP() || !attackEntityTeam.isCanPvP()){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSetTeamLastHurtMob(LivingIncomingDamageEvent event) {
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attackEntity = source.getEntity();
        if (hurtEntity.level() instanceof ServerLevel && attackEntity != null){
            if (attackEntity instanceof Player ){
                Team team = TeamManager.getTeam(attackEntity);
                if (team != null){
                    team.setLastHurtByMob(hurtEntity);
                }
            }
            if (hurtEntity instanceof Player && attackEntity instanceof LivingEntity livingEntity){
                Team team = TeamManager.getTeam(hurtEntity);
                if (team != null){
                    team.setLastHurtByMob(livingEntity);
                }
            }
        }
    }
}