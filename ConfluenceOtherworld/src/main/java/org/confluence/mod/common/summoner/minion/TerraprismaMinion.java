package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.confluence.mod.common.summoner.attachmentEntity.*;
import org.confluence.mod.common.summoner.minion.goal.terraprisma.TerraprismAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.terraprisma.TerraprismIdleGoal;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class TerraprismaMinion extends Minion implements IEntityCollision<TerraprismaMinion> {

    public boolean attacking = false;
    public final Set<Entity> hitTargets = new HashSet<>();

    public int trailTimer = 0;
    public int attackTimer = 0;
    public float lastIdleBlend = 0f;
    public float idleBlend = 0f;
    private boolean damageActive;

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.INT, () -> trailTimer, value -> trailTimer = value);
        fields.field(LyraStreamCodecs.INT, () -> attackTimer, value -> attackTimer = value);
        fields.field(LyraStreamCodecs.FLOAT, () -> lastIdleBlend, value -> lastIdleBlend = value);
        fields.field(LyraStreamCodecs.FLOAT, () -> idleBlend, value -> idleBlend = value);
        fields.field(LyraStreamCodecs.BOOL, () -> damageActive, value -> damageActive = value);
    }

    public TerraprismaMinion() {
        super(SummonerAttachmentEntityTypes.TERRAPRISMA);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new TerraprismAttackGoal(this));
        goalSelector.addGoal(1, new TerraprismIdleGoal(this));
    }

    @Override
    public LivingEntity searchTarget() {
        return target;
    }

    public LivingEntity findAttackTarget(boolean skipVisibility) {
        TargetCache cache = getTargetCache();
        Vec3 center = owner.getBoundingBox().getCenter();
        List<LivingEntity> targets = cache.getEntitiesInRadius(center, getSearchDistance(), living ->
                (skipVisibility ? cache.canKeep(living) : cache.canAcquire(living)) && (skipVisibility || cache.isVisibility(owner, living)));
        LivingEntity marked = owner.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA).getMarkTarget();
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity living : targets) {
            if (living == marked) {
                return living;
            }
            double distance = living.getBoundingBox().getCenter().distanceToSqr(center);
            if (distance < nearestDistance) {
                nearest = living;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.2, -0.2, -0.9, 0.2, 0.2, 1.05);
    }

    @Override
    public boolean canCollideAttack() {
        return damageActive;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            if (hitTargets.add(LibEntityUtils.dedupeIdentity(hit.entity()))) {
                attack(hit.entity(), getDamage(), 0);
            }
        }
    }

    @Override
    public void tick() {
        if (getPos().distanceToSqr(owner.getBoundingBox().getCenter()) > 125 * 125) {
            resetAttack();
        }
        super.tick();
        lastIdleBlend = idleBlend;
        if (attacking || attackTimer < 0) {
            trailTimer = Math.min(trailTimer + 1, 3);
        } else {
            trailTimer = Math.max(0, trailTimer - 1);
        }
        if (getGoalSelector().getCurrentGoal() instanceof TerraprismIdleGoal && attackTimer == 0) {
            idleBlend = Math.min(1f, idleBlend + 0.25f);
        } else {
            idleBlend = Math.max(0f, idleBlend - 0.25f);
        }
    }

    @Override
    public void onLevelChange() {
        super.onLevelChange();
        resetAttack();
    }

    private void resetAttack() {
        attacking = false;
        attackTimer = 0;
        damageActive = false;
        hitTargets.clear();
        setTarget(null);
        setPlannedPath(null);
    }

    public void move(String identifier, Function<Float, PathNode> sampler, boolean damaging, boolean resetHits, float... boundaries) {
        TreeSet<Float> times = new TreeSet<>();
        for (int i = 1; i < 6; i++) {
            times.add(i / 6f);
        }
        for (float time : boundaries) {
            if (time > 0f && time < 1f) {
                times.add(time);
            }
        }
        List<RenderKeyframe> frames = times.stream().map(time -> new RenderKeyframe(time, sampler.apply(time))).toList();
        PlannedPath path = new PlannedPath(identifier, List.of(sampler.apply(1f))).render(0, frames);
        // 先结算上一段碰撞，再为下一段更新伤害与命中记录。
        path.run(0, () -> {
            damageActive = damaging;
            if (resetHits) {
                hitTargets.clear();
            }
        });
        setPlannedPath(path);
    }

    public int getColor(float partialTick) {
        int order = getOrder();
        int total = Math.max(1, getSameSize());
        int animationTick = owner.tickCount;
        float hueShift = ((float) order / total + (animationTick + partialTick) * 0.015f) % 1.0f;
        return Mth.hsvToRgb(hueShift, 1, 1);
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        PathNode node = super.getRenderNode(partialTick);
        return node.lerp(getInterpolatedIdleState(partialTick), Mth.lerp(partialTick, lastIdleBlend, idleBlend));
    }

    @Override
    public long getSameHash() {
        return Objects.hash(TerraprismaMinion.class) * 43L;
    }

    public PathNode getInterpolatedIdleState(float partialTick) {
        PathNode node = getCurrentPathNode();
        if (owner != null) {
            float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
            float headYaw = Mth.rotLerp(partialTick, owner.yHeadRotO, owner.yHeadRot);
            float playerYaw = Mth.wrapDegrees(bodyYaw + Mth.wrapDegrees(headYaw - bodyYaw) * 0.5f);
            float rad = (float) Math.toRadians(-playerYaw + 180);
            float backX = (float) Math.sin(rad);
            float backZ = (float) Math.cos(rad);
            float rightX = (float) Math.cos(rad);
            float rightZ = (float) -Math.sin(rad);
            int order = getOrder();
            double localZ = 0.75 + order * 0.12;
            double floatSpeed = 0.08 + order * 0.01;
            double floatAngle = (owner.tickCount + partialTick) * floatSpeed + order * 1.33;
            Vec3 playerPos = owner.getPosition(partialTick);
            Vec3 targetPos = playerPos.add(localZ * backX + Math.cos(floatAngle) * 0.075 * rightX, owner.getBbHeight() * 0.6 + Math.sin(floatAngle) * 0.075, localZ * backZ + Math.cos(floatAngle) * 0.075 * rightZ);
            node = new PathNode(targetPos, playerYaw - 90, 75 - order * 5f, 100);
        }
        return node;
    }
}
