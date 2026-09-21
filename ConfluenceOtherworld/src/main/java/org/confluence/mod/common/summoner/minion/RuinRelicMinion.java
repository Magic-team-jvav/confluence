package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoalSelector;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.goal.ruin_relic.RuinRelicAttackGoal;
import org.confluence.mod.common.summoner.minion.goal.ruin_relic.RuinRelicIdleGoal;
import org.confluence.mod.common.summoner.particle.GenericParticleBuilder;
import org.confluence.mod.common.summoner.particle.ParticleHelper;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.projectile.ForbiddenOrb;

import java.util.List;

/** 禁戒遗迹：待机时在玩家背后竖直圆环环绕，攻击时向目标发射禁戒弹。 */
public class RuinRelicMinion extends MomentumMinion {

    private int cooldown;

    public RuinRelicMinion() {
        super(SummonerAttachmentEntityTypes.RUIN_RELIC);
        setGravity(0);
    }

    @Override
    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
        goalSelector.addGoal(0, new RuinRelicAttackGoal(this));
        goalSelector.addGoal(1, new RuinRelicIdleGoal(this));
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public void tick() {
        super.tick();
        if (cooldown > 0) {
            cooldown--;
        }
    }

    /** 向目标发射禁戒弹，并按同类数量提高伤害和返还后坐力。 */
    public void shootForbiddenOrb(LivingEntity target) {
        Vec3 start = getPos();
        Vec3 direction = target.getBoundingBox().getCenter().subtract(start).normalize();
        ForbiddenOrb orb = new ForbiddenOrb();
        orb.setOwner(getOwner());
        orb.init(new PathNode(start, direction));
        orb.copyAttributes(this);
        orb.setVelocity(direction.scale(4));
        SummonerHelper.get(getOwner()).add(orb);
        addVelocity(direction.scale(-0.4));
        ParticleHelper.create(getLevel())
                .generic(GenericParticleBuilder.create()
                        .centerColor(0xFFD700)
                        .edgeColor(0xFFA500)
                        .lifetime(5)
                        .lifetimeRandom(5)
                        .spin(0.2F)
                        .spinRandom(0.1F)
                        .friction(0.7F)
                        .scale(0.035F)
                        .scaleRandom(0.008F))
                .pos(start)
                .velocity(direction)
                .count(5)
                .speed(0.6)
                .spread(0.4)
                .emit();
    }

    public int getAttackCooldown() {
        return 35 + getRandom().nextInt(4);
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /** 获取与自身目标一致的同类遗迹数量。 */
    public int getSameTargetCount() {
        LivingEntity currentTarget = getTarget();
        List<RuinRelicMinion> list = SummonerHelper.get(getOwner()).getEntityData().get(SummonerAttachmentEntityTypes.RUIN_RELIC.get());
        int count = 0;
        for (RuinRelicMinion entity : list) {
            if (entity.getTarget() == currentTarget) {
                count++;
            }
        }
        return Math.max(1, count);
    }

    /** 获取自身在同目标分组中的次序。 */
    public int getSameTargetOrder() {
        LivingEntity currentTarget = getTarget();
        List<RuinRelicMinion> list = SummonerHelper.get(getOwner()).getEntityData().get(SummonerAttachmentEntityTypes.RUIN_RELIC.get());
        int order = 0;
        for (RuinRelicMinion entity : list) {
            if (entity == this) {
                return order;
            }
            if (entity.getTarget() == currentTarget) {
                order++;
            }
        }
        return 0;
    }

    /** 待机圆环：竖直圆环位于玩家背后，看向玩家视线方向。 */
    public PathNode getIdleRingNode(float partialTick) {
        LivingEntity player = getOwner();
        PathNode node = getCurrentPathNode();
        if (player != null) {
            int total = getSameTargetCount();
            int order = getSameTargetOrder();
            float playerYaw = Mth.rotLerp(partialTick, player.yHeadRotO, player.yHeadRot);
            float playerPitch = Mth.lerp(partialTick, player.xRotO, player.getXRot());
            float radians = (float) Math.toRadians(-playerYaw);
            Vec3 normal = new Vec3(Mth.sin(radians), 0.0, Mth.cos(radians));
            Vec3 up = new Vec3(0.0, 1.0, 0.0);
            Vec3 right = normal.cross(up).normalize();
            double px = Mth.lerp(partialTick, player.xo, player.getX());
            double py = Mth.lerp(partialTick, player.yo, player.getY());
            double pz = Mth.lerp(partialTick, player.zo, player.getZ());
            Vec3 center = new Vec3(px, py + player.getEyeHeight(), pz).add(normal.scale(-0.8));
            float angle = (player.tickCount + partialTick) * 0.06F + order * Mth.TWO_PI / total;
            float radius = total > 1 ? 0.8F : 0.0F;
            Vec3 pos = center.add(right.scale(Math.cos(angle) * radius)).add(up.scale(Math.sin(angle) * radius));
            node = new PathNode(pos, playerYaw, playerPitch, 0);
        }
        return node;
    }

    /** 攻击圆环：目标距离玩家超过 8 格时阻隔在两者之间，否则在待机位置看向目标。 */
    public PathNode getAttackRingNode(LivingEntity target, float partialTick) {
        PathNode node = getCurrentPathNode();
        if (getOwner() != null) {
            int total = getSameTargetCount();
            int order = getSameTargetOrder();
            Vec3 playerPos = getOwner().getPosition(partialTick).add(0.0, getOwner().getBbHeight() * 0.5, 0.0);
            Vec3 targetPos = target.getPosition(partialTick).add(0.0, target.getBbHeight() * 0.5, 0.0);
            double distance = playerPos.distanceTo(targetPos);
            if (distance <= 8.0) {
                PathNode idleNode = getIdleRingNode(partialTick);
                node = new PathNode(idleNode.pos(), target.getBoundingBox().getCenter().subtract(idleNode.pos()));
            } else {
                Vec3 axis = targetPos.subtract(playerPos).normalize();
                Vec3 side = axis.cross(new Vec3(0.0, 1.0, 0.0));
                if (side.lengthSqr() < 1.0E-6) {
                    side = axis.cross(new Vec3(1.0, 0.0, 0.0));
                }
                side = side.normalize();
                Vec3 up = axis.cross(side).normalize();
                Vec3 center = playerPos.add(axis.scale(Math.min(4.0, distance * 0.2)));
                float angle = (getOwner().tickCount + partialTick) * 0.06F + order * Mth.TWO_PI / total;
                float radius = total > 1 ? 1.6F : 0.0F;
                Vec3 pos = center.add(up.scale(Math.cos(angle) * radius)).add(side.scale(Math.sin(angle) * radius));
                node = new PathNode(pos, target.getBoundingBox().getCenter().subtract(pos));
            }
        }
        return node;
    }
}
