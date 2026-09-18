package org.confluence.mod.common.summoner;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.api.event.ArmorPenetrationEvent;
import org.confluence.mod.common.summoner.attachment.WhipMarkTracker;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.entity.living.PortLivingDamageEvent;
import org.mesdag.portlib.event.entity.living.PortLivingDeathEvent;
import org.mesdag.portlib.event.tick.PortPlayerTickEvent;

public final class SummonerEvents {

    public static void init() {
        PortEventHandler.addListener((PortPlayerTickEvent.Post event) -> {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getData(SummonerAttachmentTypes.TARGET_CACHE).tick(serverPlayer);
            }
            player.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA).tick(player);
            SummonerHelper.get(player).getEntityData().tick(player);
        });
        PortEventHandler.addListener((PortLivingDamageEvent.Pre event) -> {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            if (source instanceof AttachmentEntityDamageSource damageSource && damageSource.getEntity() instanceof Player attacker && target != attacker) {
                WhipMarkTracker tracker = attacker.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                if (tracker.isSummonMarkTarget(target)) {
                    event.setNewDamage(tracker.getType().damagePre(tracker, target, damageSource, event.getNewDamage()));
                }
            }
        });
        PortEventHandler.addListener((ArmorPenetrationEvent event) -> {
            if (event.getDamageSource() instanceof AttachmentEntityDamageSource source) {
                event.setPenetration(event.getPenetration() + source.getArmorPenetration());
            }
        });
        PortEventHandler.addListener((PortLivingDamageEvent.Post event) -> {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            if (source.getEntity() instanceof LivingEntity attacker && target != attacker) {
                if (target instanceof Player player) {
                    player.getData(SummonerAttachmentTypes.TARGET_CACHE).record(attacker, 200);
                }
                if (attacker instanceof Player player) {
                    player.getData(SummonerAttachmentTypes.TARGET_CACHE).record(target, 200);
                    if (source instanceof AttachmentEntityDamageSource damageSource) {
                        WhipMarkTracker tracker = attacker.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                        if (tracker.isSummonMarkTarget(target)) {
                            tracker.getType().damagePost(tracker, target, damageSource, event.getNewDamage());
                        }
                    }
                }
            }
        });
        PortEventHandler.addListener((PortLivingDeathEvent event) -> {
            DamageSource eventSource = event.getSource();
            if (eventSource instanceof AttachmentEntityDamageSource damageSource) {
                LivingEntity target = event.getEntity();
                AttachmentEntity entity = damageSource.getAttachmentEntity();
                Player owner = entity.getOwner();
                WhipMarkTracker tracker = owner.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                if (tracker.isSummonMarkTarget(target)) {
                    tracker.getType().kill(target, damageSource);
                }
            }
        });
    }
}
