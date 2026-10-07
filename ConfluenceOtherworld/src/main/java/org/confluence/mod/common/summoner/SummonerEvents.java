package org.confluence.mod.common.summoner;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.confluence.lib.api.event.ArmorPenetrationEvent;
import org.confluence.lib.mixed.ILibDamageSource;
import org.confluence.mod.common.summoner.attachment.InfoData;
import org.confluence.mod.common.summoner.attachment.WhipMarkTracker;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.common.summoner.particle.SummonerParticleData;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.summonMark.SummonMarkInstance;

import java.util.List;

public final class SummonerEvents {

    public static void init() {
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post event) -> {
            SummonerParticleData.tick(event);
            InfoData.tick(event);
        });
        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Post event) -> {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getData(SummonerAttachmentTypes.TARGET_CACHE).tick();
                serverPlayer.getData(SummonerAttachmentTypes.ZENITH_DATA).tick();
            }
            player.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA).tick();
            SummonerHelper.get(player).getEntityData().tick(player);
        });
        NeoForge.EVENT_BUS.addListener((LivingDamageEvent.Pre event) -> {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            if (!target.level().isClientSide() && source instanceof AttachmentEntityDamageSource damageSource && damageSource.getEntity() instanceof Player attacker && target != attacker) {
                WhipMarkTracker tracker = attacker.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                if (tracker.isSummonMarkTarget(target)) {
                    List<SummonMarkInstance> summonMarkInstances = tracker.getSummonMarkInstances();
                    for (SummonMarkInstance instance : summonMarkInstances) {
                        event.setNewDamage(instance.getType().damagePre(tracker, instance, target, damageSource, event.getNewDamage()));
                    }
                }
            }
        });
        NeoForge.EVENT_BUS.addListener((ArmorPenetrationEvent event) -> {
            if (event.getDamageSource() instanceof AttachmentEntityDamageSource source) {
                event.setPenetration(event.getPenetration() + source.getArmorPenetration());
            }
        });
        NeoForge.EVENT_BUS.addListener((LivingDamageEvent.Post event) -> {
            LivingEntity target = event.getEntity();
            DamageSource source = event.getSource();
            Level level = target.level();
            if (!level.isClientSide()) {
                if (source.getEntity() instanceof LivingEntity attacker && target != attacker) {
                    if (target instanceof Player player) {
                        player.getData(SummonerAttachmentTypes.TARGET_CACHE).record(attacker, 200);
                    }
                    if (attacker instanceof Player player) {
                        player.getData(SummonerAttachmentTypes.TARGET_CACHE).record(target, 200);
                        if (source instanceof AttachmentEntityDamageSource damageSource) {
                            WhipMarkTracker tracker = attacker.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                            if (tracker.isSummonMarkTarget(target)) {
                                List<SummonMarkInstance> summonMarkInstances = tracker.getSummonMarkInstances();
                                for (SummonMarkInstance instance : summonMarkInstances) {
                                    instance.getType().damagePost(tracker, instance, target, damageSource, event.getNewDamage());
                                }
                            }
                        }
                    }
                }
                Vec3 pos = target.getBoundingBox().getCenter();
                Vec3 velocity = new Vec3(0, 2, 0).offsetRandom(level.getRandom(), 0.5F).subtract(Vec3.ZERO).scale(0.4);
                ILibDamageSource libSource = ILibDamageSource.of(source);
                InfoData.record(level, event.getNewDamage(), pos, velocity, libSource.confluence$isCritical() ? InfoData.Type.CRITICAL : InfoData.Type.DAMAGE);
            }
        });
        NeoForge.EVENT_BUS.addListener((LivingHealEvent event) -> {
            LivingEntity living = event.getEntity();
            float amount = event.getAmount();
            Level level = living.level();
            if (!level.isClientSide()) {
                Vec3 pos = living.getBoundingBox().getCenter();
                Vec3 velocity = new Vec3(0, 2, 0).offsetRandom(level.getRandom(), 0.5F).subtract(Vec3.ZERO).scale(0.4);
                InfoData.record(level, amount, pos, velocity, InfoData.Type.HEAL);
            }
        });
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent event) -> {
            LivingEntity target = event.getEntity();
            DamageSource eventSource = event.getSource();
            if (target.level().isClientSide() && eventSource instanceof AttachmentEntityDamageSource damageSource) {
                AttachmentEntity entity = damageSource.getAttachmentEntity();
                Player owner = entity.getOwner();
                WhipMarkTracker tracker = owner.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA);
                if (tracker.isSummonMarkTarget(target)) {
                    List<SummonMarkInstance> summonMarkInstances = tracker.getSummonMarkInstances();
                    for (SummonMarkInstance instance : summonMarkInstances) {
                        instance.getType().kill(tracker, instance, target, damageSource);
                    }
                }
            }
        });
    }
}
