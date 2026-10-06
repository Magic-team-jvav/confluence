package org.confluence.mod.common.summoner.attachment;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.projectile.Zenith;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.register.SummonerSoundEvents;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;

import java.util.List;
import java.util.function.Predicate;

public class ZenithData {

    private final Player owner;
    private float power = 0;

    public ZenithData(IPortAttachmentHolder holder) {
        if (holder instanceof Player player) {
            this.owner = player;
        } else {
            throw new IllegalArgumentException("ZenithData can only be used with an Player");
        }
    }

    public void tick() {
        if (owner.getMainHandItem().is(SwordItems.ZENITH)) {
            if (power > 3.33f) {
                playZenithSound();
                Vec3 lookAngle = owner.getLookAngle();
                Vec3 center = owner.getBoundingBox().getCenter();
                Vec3 eyePos = owner.getEyePosition();
                int reach = 32;
                Vec3 endEndPos = eyePos.add(lookAngle.scale(reach));
                EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(owner, eyePos, endEndPos, owner.getBoundingBox().inflate(reach), entity -> entity instanceof LivingEntity living && living.isAlive() && living != owner, reach * reach);
                if (entityHit != null && entityHit.getEntity() instanceof LivingEntity) {
                    endEndPos = entityHit.getLocation();
                }
                TargetCache targetCache = owner.getData(SummonerAttachmentTypes.TARGET_CACHE);
                Predicate<LivingEntity> predicate = target -> owner != target && !(target instanceof Player player && (player.isSpectator() || player.isCreative()));
                List<LivingEntity> targetList = targetCache.getEntitiesInRadius(endEndPos, 32, predicate);
                LivingEntity best = null;
                if (!targetList.isEmpty()) {
                    int searchRange = 32;
                    double fovAngle = 30;
                    double distanceWeight = 0.2;
                    double bestScore = Double.MAX_VALUE;
                    for (LivingEntity living : targetList) {
                        Vec3 targetPoint = living.getBoundingBox().getCenter();
                        Vec3 toEntity = targetPoint.subtract(center);
                        double angle = Math.toDegrees(Math.acos(toEntity.dot(lookAngle) / toEntity.length()));
                        if (angle <= fovAngle) {
                            // 角度、距离分别归一化到 [0,1]
                            double normalizedAngle = angle / fovAngle;
                            double normalizedDistance = toEntity.length() / searchRange;
                            // 综合分数：按权重合成角度与距离，越小越优先
                            double score = (1.0 - distanceWeight) * normalizedAngle + distanceWeight * normalizedDistance;
                            if (score < bestScore) {
                                bestScore = score;
                                best = living;
                            }
                        }
                    }
                }
                do {
                    power -= 3.33f;
                    Zenith zenith = new Zenith();
                    zenith.setOwner(owner);
                    zenith.setDamage((float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    zenith.setKnockback(1);
                    RandomSource random = owner.getRandom();
                    if (random.nextFloat() < 0.33) {
                        zenith.renderType = Zenith.RenderType.ZENITH;
                    }
                    float distance = (float) eyePos.distanceTo(endEndPos);
                    Vec3 endPos = endEndPos.offsetRandom(random, distance * 0.1f);
                    if (zenith.renderType != Zenith.RenderType.ZENITH) {
                        List<LivingEntity> radius = zenith.getTargetCache().getEntitiesInRadius(endPos, 10, predicate);
                        if (!radius.isEmpty()) {
                            LivingEntity living = radius.get(random.nextInt(radius.size()));
                            endPos = living.getBoundingBox().getCenter();
                            zenith.target = living;
                        } else {
                            if (best != null) {
                                endPos = best.getBoundingBox().getCenter();
                                zenith.target = best;
                            }
                        }
                    }
                    zenith.offest = endPos.subtract(center);
                    zenith.init(zenith.getRenderNode(1));
                    SummonerHelper.get(owner).add(zenith);
                } while (power > 3.33f);
            }
        } else {
            power = 0;
        }
    }

    public boolean swing() {
        if (owner.getMainHandItem().is(SwordItems.ZENITH)) {
            owner.swing(InteractionHand.MAIN_HAND, true);
            owner.resetAttackStrengthTicker();
            return true;
        }
        return false;
    }

    public void addPower() {
        power = (float) (power + owner.getAttributeValue(Attributes.ATTACK_SPEED) * 0.5);
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * 源实现在此处调用 {@code Playable.play(sound, level, pos, source)}：先判空，
     * 再以 0.9~1.1 的随机音量与音调播放。这里保留该行为（含消耗两次 random）。
     */
    private void playZenithSound() {
        SoundEvent soundEvent = SummonerSoundEvents.ZENITH.get();
        RandomSource random = owner.level().getRandom();
        Vec3 pos = owner.position();
        owner.level().playSound(null, pos.x, pos.y, pos.z, soundEvent, owner.getSoundSource(), 0.9f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.2f);
    }
}
