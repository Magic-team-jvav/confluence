package org.confluence.mod.common.item.whip;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.whip.WhipDirectHitContext;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.confluence.mod.mixed.Immunity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Confluence.MODID)
public final class FirecrackerItem extends BaseWhipItem {
    public static final float TAG_DAMAGE = 0.0F;
    private static final List<Blast> BLASTS = new ArrayList<>();

    public FirecrackerItem(Supplier<? extends SummonMarkType> summonMarkType) {
        super("firecracker", 34F, 0.5F, 1.85F, 15, summonMarkType);
    }

    @Override
    public float damageFalloff() {return 0.66F;}

    @Override
    public float minimumDamageMultiplier() {return 0.0F;}

    @Override
    public boolean shouldApplyTag(int hitIndex) {return hitIndex == 0;}

    @Override
    public void onDirectHit(WhipDirectHitContext context) {
        context.target().addEffect(new MobEffectInstance(ModEffects.HELLFIRE.get(), 80), context.owner());
    }

    public static void explode(Player owner, LivingEntity target, Entity damageRecipient, float hitDamage, float armorPenetration) {
        if (!(target.level() instanceof ServerLevel level)) return;
        float damage = hitDamage * 2.75F;
        DamageSource source = new WhipDamageSource(owner, armorPenetration);
        // 爆炸是独立的一次命中，不继承触发它的召唤物局部无敌帧，也不破坏方块。
        Immunity explosion = new Immunity() {
            @Override
            public Type confluence$getImmunityType() {return Type.LOCAL;}

            @Override
            public int confluence$getImmunityDuration(DamageSource source) {return 0;}
        };
        HashSet<UUID> hit = new HashSet<>();
        hit.add(target.getUUID());
        hit.add(ProjectileHitRules.dedupeIdentity(damageRecipient).getUUID());
        // MC 使用三维区域：以命中部位为中心六格见方，主目标及其所有部件均排除。
        Blast blast = new Blast(level, owner, target.getUUID(), AABB.ofSize(damageRecipient.getBoundingBox().getCenter(), 6, 6, 6),
                source, damage, explosion, hit, level.getGameTime());
        blast.hitNearby();
        BLASTS.add(blast);
        level.sendParticles(ParticleTypes.FLAME, damageRecipient.getX(), damageRecipient.getY(0.5), damageRecipient.getZ(), 24, 0.3, 0.3, 0.3, 0.12);
        level.sendParticles(ParticleTypes.LAVA, damageRecipient.getX(), damageRecipient.getY(0.5), damageRecipient.getZ(), 6, 0.2, 0.2, 0.2, 0);
        level.playSound(null, damageRecipient.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.5F, 1.4F);
    }

    @SubscribeEvent
    public static void tickBlasts(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level))
            return;
        BLASTS.removeIf(blast -> {
            if (blast.level() != level) return false;
            long age = level.getGameTime() - blast.startedAt();
            if (age >= 3 || blast.owner().isRemoved() || blast.owner().level() != level)
                return true;
            if (age > 0) blast.hitNearby();
            return false;
        });
    }

    @SubscribeEvent
    public static void clearBlasts(ServerStoppedEvent event) {
        BLASTS.clear();
    }

    private record Blast(ServerLevel level, Player owner, UUID primaryTarget, AABB bounds,
                         DamageSource source,
                         float damage, Immunity immunity, HashSet<UUID> hit, long startedAt) {
        private void hitNearby() {
            for (Entity candidate : level.getEntities((Entity) null, bounds, entity -> ProjectileHitRules.canHit(owner, entity))) {
                if (ProjectileHitRules.encounterOwner(candidate).getUUID().equals(primaryTarget))
                    continue;
                if (hit.add(ProjectileHitRules.dedupeIdentity(candidate).getUUID())) {
                    damageExplosionTarget(immunity, ProjectileHitRules.damageRecipient(candidate), source, damage);
                }
            }
        }
    }

    private static void damageExplosionTarget(Immunity explosion, Entity target, DamageSource source, float damage) {
        if (target instanceof LivingEntity living) {
            Immunity.hurt(explosion, living, source, damage);
        } else {
            Immunity.withCause(explosion, () -> LibDamageTypes.hurtWithoutKnockback(target, source, damage));
        }
    }
}
