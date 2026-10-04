package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.minion.goal.terraprisma.TerraprismAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.terraprisma.TerraprismIdleGoal;
import org.confluence.mod.common.summoner.minion.goal.terraprisma.TerraprismPrepGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 泰拉棱镜：跟随主人的浮游剑，蓄势后以直线／椭圆／刺击等轨迹连续斩击目标。
 */
public class TerraprismaMinion extends Minion implements IEntityCollision<TerraprismaMinion> {

    public boolean attacking = false;
    public final Set<LivingEntity> hitTargets = new HashSet<>();
    public int trailTimer = 0;
    public float lastIdleBlend = 0;
    public float idleBlend = 0;

    public TerraprismaMinion() {
        super(SummonerAttachmentEntityTypes.TERRAPRISMA);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> trailTimer, value -> trailTimer = value);
        fields.field(LyraStreamCodecs.FLOAT, () -> lastIdleBlend, value -> lastIdleBlend = value);
        fields.field(LyraStreamCodecs.FLOAT, () -> idleBlend, value -> idleBlend = value);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new TerraprismAttackGoal(this));
        goalSelector.addGoal(1, new TerraprismPrepGoal(this));
        goalSelector.addGoal(2, new TerraprismIdleGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        lastIdleBlend = idleBlend;
        trailTimer = attacking ? Math.min(trailTimer + 1, 12) : 0;
        idleBlend = getGoalSelector().getCurrentGoal() instanceof TerraprismIdleGoal
                ? Math.min(1.0F, idleBlend + 0.05F)
                : Math.max(0.0F, idleBlend - 0.25F);
    }

    @Override
    public LivingEntity searchTarget() {
        int distance = getSearchDistance();
        TargetCache targetCache = getTargetCache();
        List<LivingEntity> targets = targetCache.getEntitiesInRadius(getOwner().getBoundingBox().getCenter(), distance, living -> (attacking || targetCache.isVisibility(getOwner(), living)) && targetCache.isTarget(living));
        return targets.isEmpty() ? null : targetCache.getNewTarget(this, targets, 8, false);
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.2, -0.1, -1.2, 0.2, 0.1, 1.2);
    }

    @Override
    public boolean canCollideAttack() {
        return getTarget() != null;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            if (hitTargets.add(hit.entity())) {
                attack(hit.entity(), getDamage(), 4);
            }
        }
    }

    @Override
    public long getSameHash() {
        return Objects.hash(TerraprismaMinion.class) * 43L;
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        return super.getRenderNode(partialTick).lerp(getInterpolatedIdleState(partialTick), Mth.lerp(partialTick, lastIdleBlend, idleBlend));
    }

    /**
     * 按次序在主人身后错开漂浮，并按呼吸节奏上下浮动。
     */
    public PathNode getInterpolatedIdleState(float partialTick) {
        float bodyYaw = Mth.rotLerp(partialTick, getOwner().yBodyRotO, getOwner().yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, getOwner().yHeadRotO, getOwner().yHeadRot);
        float playerYaw = Mth.wrapDegrees(bodyYaw + Mth.wrapDegrees(headYaw - bodyYaw) * 0.5F);
        float rad = (float) Math.toRadians(-playerYaw + 180);
        float backX = (float) Math.sin(rad);
        float backZ = (float) Math.cos(rad);
        float rightX = (float) Math.cos(rad);
        float rightZ = (float) -Math.sin(rad);
        double localZ = 0.75 + getOrder() * 0.12;
        double floatAngle = (getOwner().tickCount + partialTick) * (0.08 + getOrder() * 0.01) + getOrder() * 1.33;
        Vec3 ownerPos = getOwner().getPosition(partialTick);
        Vec3 targetPos = ownerPos.add(
                localZ * backX + Math.cos(floatAngle) * 0.075 * rightX,
                getOwner().getBbHeight() * 0.6 + Math.sin(floatAngle) * 0.075,
                localZ * backZ + Math.cos(floatAngle) * 0.075 * rightZ);
        return new PathNode(targetPos, playerYaw - 90, 75 - getOrder() * 5.0F, 100);
    }

    /**
     * 按次序错开色相，并随呼吸周期轻微改变饱和度。
     */
    public int getColor(float partialTick) {
        int total = Math.max(1, getSameSize());
        int animationTick = getOwner() != null ? getOwner().tickCount : getTickCount();
        float hueShift = ((float) getOrder() / total + (animationTick + partialTick) * 0.015F) % 1.0F;
        float breathFactor = 0.5F + 0.5F * Mth.sin(hueShift * Mth.TWO_PI);
        return Mth.hsvToRgb(hueShift, 0.75F - 0.35F * breathFactor, 1.0F);
    }
}
