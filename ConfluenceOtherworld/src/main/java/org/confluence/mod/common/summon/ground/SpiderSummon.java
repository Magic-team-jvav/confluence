package org.confluence.mod.common.summon.ground;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.summon.*;
import org.confluence.mod.mixed.Immunity;

import java.util.List;

public final class SpiderSummon extends PhysicalSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 26.0F;
    private static final Immunity[] IMMUNITIES = {new SpiderImmunity(), new SpiderImmunity(), new SpiderImmunity()};
    public static final ResourceLocation VENOM = Confluence.asResource("spider_venom");
    public static final ResourceLocation JUMPER = Confluence.asResource("spider_jumper");
    public static final ResourceLocation DANGEROUS = Confluence.asResource("spider_dangerous");
    private static final ResourceLocation[] FORMS = {VENOM, JUMPER, DANGEROUS};
    private int variant = -1;
    private int biteCooldown;
    private LivingEntity latched;
    private Entity latchedPart;
    private Vec3 latchOffset = Vec3.ZERO;
    private boolean returning;

    public SpiderSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose pose) {
        super(Confluence.asResource("spider"), owner, slotCost, stats, pose, 0.7, 0.5);
        addGoal(1, new SummonGoal<SpiderSummon>(this) {
            @Override
            public boolean canUse() {
                return true;
            }

            @Override
            public void tick() {
                moveSpider();
            }
        });
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), 50.0 + order() * 2.5);
    }

    @Override
    protected boolean usesOwnerRecovery() {
        return false;
    }

    private void moveSpider() {
        if (variant < 0) variant = order() % 3;
        if (biteCooldown > 0) biteCooldown--;
        if (latched != null && (latched != target() || !latched.isAlive() || latchedPart == null || latchedPart.isRemoved())) {
            latched = null;
            latchedPart = null;
        }
        double ownerDistance = position().distanceToSqr(owner().position());
        double returnRange = 87.5 + order() * 2.5;
        if (ownerDistance > returnRange * returnRange) returning = true;
        if (returning && ownerDistance < 16.0) returning = false;
        if (returning) {
            latched = null;
            latchedPart = null;
            Vec3 offset = owner().position().add(0.0, 1.0, 0.0).subtract(position());
            moveWithoutCollision(offset.normalize().scale(Math.min(1.0, offset.length())));
            return;
        }
        if (latched != null) {
            float yaw = latchedPart instanceof LivingEntity living ? living.yBodyRot : latchedPart.getYRot();
            Vec3 offset = latchOffset.yRot(-yaw * Mth.DEG_TO_RAD);
            advanceTo(new SummonPose(latchedPart.position().add(offset), yaw, 0.0F, 0.0F));
            if (biteCooldown == 0 && hurtEntity(ProjectileHitRules.damageRecipient(latchedPart), latched, 1.0F)) {
                biteCooldown = 7;
                latched.addEffect(new MobEffectInstance(ModEffects.ACID_VENOM.get(), 40 + owner().getRandom().nextInt(41)), owner());
            }
            return;
        }
        Vec3 destination = target() == null ? owner().position() : targetBasePosition();
        Vec3 offset = destination.subtract(position());
        Vec3 direction = groundWaypoint(destination).subtract(position()).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 horizontal = direction.scale(target() == null ? 0.15 : 0.3);
        boolean wall = !owner().level().noCollision(null, collisionBox().move(horizontal.x, 0.0, horizontal.z));
        double vertical = wall && offset.y > 0.0 ? 0.25 : onGround() && offset.y > 0.5 ? 0.75 : velocity().y * 0.98 - 0.08;
        if (target() == null && offset.horizontalDistanceSqr() < 4.0) horizontal = Vec3.ZERO;
        moveWithCollision(new Vec3(horizontal.x, vertical, horizontal.z));
        if (target() != null && actualTarget() != null && collisionBox().inflate(0.15).intersects(targetBounds())) {
            latched = target();
            latchedPart = actualTarget();
            float yaw = latchedPart instanceof LivingEntity living ? living.yBodyRot : latchedPart.getYRot();
            latchOffset = position().subtract(latchedPart.position()).yRot(yaw * Mth.DEG_TO_RAD);
        }
    }

    @Override
    protected Immunity damageImmunity() {
        return IMMUNITIES[Math.max(0, variant)];
    }

    @Override
    public void appendRenderParts(List<SummonRenderPart> output) {
        output.add(new SummonRenderPart(uuid(), FORMS[Math.max(0, variant)], currentPose(), visualState(), order()));
    }

    private static final class SpiderImmunity implements Immunity {
        @Override
        public Type confluence$getImmunityType() {
            return Type.STATIC;
        }

        @Override
        public int confluence$getImmunityDuration(DamageSource source) {
            return 5;
        }
    }
}
