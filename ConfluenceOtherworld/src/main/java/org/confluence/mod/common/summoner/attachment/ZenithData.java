package org.confluence.mod.common.summoner.attachment;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.item.SummonItems;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.Ellipse;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.projectile.Zenith;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.register.SummonerSoundEvents;
import org.confluence.mod.common.summoner.util.EasingCurve;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;

import java.util.ArrayList;
import java.util.List;

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
        if (owner.getMainHandItem().is(SummonItems.ZENITH)) {
            if (power > 3.33f) {
                playZenithSound();
                do {
                    power -= 3.33f;
                    Vec3 lookAngle = owner.getLookAngle();
                    Vec3 center = owner.getBoundingBox().getCenter();
                    RandomSource random = owner.getRandom();
                    Vec3 eyePos = owner.getEyePosition();
                    int reach = 32;
                    Vec3 endEndPos = eyePos.add(lookAngle.scale(reach));
                    EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(owner, eyePos, endEndPos, owner.getBoundingBox().inflate(reach), entity -> entity instanceof LivingEntity living && living.isAlive() && living != owner, reach * reach);
                    if (entityHit != null && entityHit.getEntity() instanceof LivingEntity) {
                        endEndPos = entityHit.getLocation();
                    }
                    TargetCache targetCache = owner.getData(SummonerAttachmentTypes.TARGET_CACHE);
                    List<LivingEntity> targetList = targetCache.getEntitiesInRadius(endEndPos, 32, null);
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
                    float distance = (float) eyePos.distanceTo(endEndPos);
                    Zenith zenith = new Zenith();
                    zenith.setOwner(owner);
                    zenith.setDamage((float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    zenith.setKnockback(1);
                    Vec3 endPos = endEndPos.add(Vec3.ZERO.subtract(Vec3.ZERO.offsetRandom(random, distance * 0.2f)));
                    if (random.nextFloat() < 0.33) {
                        zenith.renderType = Zenith.RenderType.ZENITH;
                    }
                    if (zenith.renderType != Zenith.RenderType.ZENITH) {
                        List<LivingEntity> radius = zenith.getTargetCache().getEntitiesInRadius(endPos, 10, null);
                        if (!radius.isEmpty()) {
                            endPos = radius.get(owner.getRandom().nextInt(radius.size())).getBoundingBox().getCenter();
                        } else {
                            if (best != null) {
                                endPos = best.getBoundingBox().getCenter();
                            }
                        }
                    }
                    Vec3 startPos = center.add(center.subtract(endPos).normalize().scale(1.5f));
                    Vec3 normal = Ellipse.randomPlaneNormal(random, endPos, startPos);
                    float distanceTo = (float) (startPos.distanceTo(endPos));
                    float curvature = 0.3f + random.nextFloat() * 0.6f - Math.min(0.1f, distanceTo * 0.05f);
                    if (distanceTo < 4) {
                        curvature *= 1 + ((4 - distanceTo) / 4);
                    }
                    Ellipse ellipse = new Ellipse(endPos, startPos, normal, curvature);
                    ArrayList<PathNode> list = new ArrayList<>();
                    for (int j = 1; j < 11; j++) {
                        float progress = (float) j / 10;
                        progress = EasingCurve.bezier()
                                .control(0.1f)
                                .control(0.2f)
                                .control(0.3f)
                                .control(0.4f)
                                .control(0.45f)
                                .control(0.475f)
                                .control(0.5f)
                                .control(0.525f)
                                .control(0.55f)
                                .control(0.6f)
                                .control(0.7f)
                                .control(0.8f)
                                .control(0.9f)
                                .control(1f)
                                .build()
                                .apply(progress);
                        Vec3 point = ellipse.getPoint(progress);
                        Vec3 tipPoint;
                        if (progress < 0.5) {
                            tipPoint = startPos.lerp(endPos, Mth.clamp(progress * 2, 0.01F, 0.99F));
                        } else {
                            tipPoint = endPos.lerp(startPos, Mth.clamp(progress * 2 - 1, 0.01F, 0.99F));
                        }
                        Vec3 tipDir = point.subtract(tipPoint).normalize();
                        list.add(zenith.getEulerNode(point, tipDir, normal));
                    }
                    list.add(list.get(list.size() - 1));
                    list.add(list.get(list.size() - 1));
                    zenith.init(list.get(0));
                    zenith.setPath(list);
                    zenith.initialPosition = owner.getPosition(1.0F);
                    SummonerHelper.get(owner).add(zenith);
                } while (power > 3.33f);
            }
        } else {
            power = 0;
        }
    }

    public boolean swing() {
        if (owner.getMainHandItem().is(SummonItems.ZENITH)) {
            owner.swing(InteractionHand.MAIN_HAND, true);
            owner.resetAttackStrengthTicker();
            return true;
        }
        return false;
    }

    public void addPower() {
        power = (float) (power + owner.getAttributeValue(Attributes.ATTACK_SPEED));
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
