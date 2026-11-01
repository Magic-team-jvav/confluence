package org.confluence.mod.common.summoner.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.item.mana.RedGloveItem;
import org.confluence.mod.common.item.mana.RedGlovePattern;
import org.confluence.mod.common.summoner.projectile.Zenith;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.util.ZenithTargeting;
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.network.s2c.RedGloveArcPacketS2C;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;

import java.util.*;

/** The held state belongs to the player, never a global item singleton. */
public final class RedGloveData {
    private final Player owner;
    private ItemStack active = ItemStack.EMPTY;
    private long started, lastCast = Long.MIN_VALUE, seed;
    private int slot;
    private net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension;
    public RedGloveData(IPortAttachmentHolder holder) {
        if (!(holder instanceof Player player)) throw new IllegalArgumentException("Player attachment required");
        owner = player;
    }
    public void press(ItemStack stack) {
        if (!(owner instanceof ServerPlayer) || !(stack.getItem() instanceof RedGloveItem) || !active.isEmpty()) return;
        active = stack; slot = owner.getInventory().selected;
        started = owner.level().getGameTime(); seed = owner.getRandom().nextLong();
        lastCast = Long.MIN_VALUE;
        dimension = owner.level().dimension();
    }
    public void reset() {
        active = ItemStack.EMPTY;
        lastCast = Long.MIN_VALUE;
    }
    public void tick() {
        if (active.isEmpty() || !(owner instanceof ServerPlayer player)) return;
        if (!owner.isAlive() || owner.isSpectator() || owner.getMainHandItem() != active
                || owner.getInventory().selected != slot || owner.level().dimension() != dimension
                || !(active.getItem() instanceof RedGloveItem item)) {
            reset(); return;
        }
        long now = owner.level().getGameTime();
        // 每 CAST_TICKS 放电一次；伤害结算与线段下发在同一拍完成。
        if (lastCast != Long.MIN_VALUE && now - lastCast < RedGlovePattern.CAST_TICKS) return;
        lastCast = now;
        // Restore the requested lava exception: it can be used there, but all rays still clip lava.
        Vec3 eye = player.getEyePosition(), look = player.getLookAngle();
        Vec3 source = player.level().clip(new ClipContext(eye, eye.add(look.scale(0.35)).add(0, -0.12, 0),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player)).getLocation();
        if (blockedByFluid(player)) return;
        if (!PlayerUtils.extractMana(player, active, () -> PrefixUtils.calculateManaCost(active, RedGloveItem.MANA_COST))) return;
        ServerLevel level = player.serverLevel();
        if (!level.getFluidState(BlockPos.containing(source)).isEmpty()) return;
        var clipper = (java.util.function.BiFunction<Vec3, Vec3, Vec3>) (a, b) -> level.clip(new ClipContext(
                a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player)).getLocation();
        List<Entity> targets = new ArrayList<>(player.getData(SummonerAttachmentTypes.TARGET_CACHE).getHitTargets(
                player.getBoundingBox().inflate(RedGlovePattern.RANGE), target -> Zenith.TARGETING.canAcquire(player, target)));
        targets.sort(Comparator.comparingDouble((Entity target) -> ZenithTargeting.score(source, look,
                target.getBoundingBox().getCenter(), RedGlovePattern.RANGE, RedGlovePattern.HALF_ANGLE)).thenComparingInt(Entity::getId));
        Map<UUID, Entity> identities = new LinkedHashMap<>();
        for (Entity target : targets) {
            Vec3 point = target.getBoundingBox().getCenter();
            if (RedGlovePattern.inRange(source, look, point) && clipper.apply(eye, point).distanceToSqr(point) < 1.0E-8
                    && clipper.apply(source, point).distanceToSqr(point) < 1.0E-8)
                identities.putIfAbsent(LibEntityUtils.dedupeIdentity(target).getUUID(), target);
        }
        long castSeed = seed + (now - started) / RedGlovePattern.CAST_TICKS * 104729L;
        var points = identities.values().stream().map(e -> new RedGlovePattern.Target(e.getId(), e.getBoundingBox().getCenter(), e.getBoundingBox())).toList();
        List<RedGlovePattern.Path> paths = RedGlovePattern.create(source, look, castSeed, points, clipper, (a, b) -> {
            var direction = level.clip(new ClipContext(a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player)).getDirection();
            return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        });
        Set<UUID> resolved = new HashSet<>();
        // 候选由实际线段包围盒发现，是否可打沿用原有敌对阵营策略；索敌列表不再限制伤害范围。
        for (Entity recipient : LibEntityUtils.queryHitCandidates(level, player, RedGlovePattern.bounds(paths),
                candidate -> Zenith.TARGETING.canAcquire(player, candidate))) {
            var living = LibEntityUtils.logicalLivingTarget(recipient);
            if (living == null || !RedGlovePattern.intersects(paths, recipient.getBoundingBox())
                    || !resolved.add(LibEntityUtils.dedupeIdentity(recipient).getUUID())) continue;
            var damage = LibDamageTypes.of(level, LibDamageTypes.MAGICAL_PROJECTILE, player, player);
            if (Immunity.hurt(item, recipient, living, damage, RedGloveItem.BASE_DAMAGE)) player.setLastHurtMob(living);
        }
        player.awardStat(Stats.ITEM_USED.get(item));
        RedGloveArcPacketS2C.send(player, source, castSeed, paths);
    }
    private static boolean blockedByFluid(ServerPlayer player) {
        var box = player.getBoundingBox().deflate(1.0E-5);
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(box.minX, box.minY, box.minZ),
                BlockPos.containing(box.maxX, box.maxY, box.maxZ))) {
            var fluid = player.level().getFluidState(pos);
            if (!fluid.isEmpty() && !fluid.is(FluidTags.LAVA)
                    && pos.getY() + fluid.getHeight(player.level(), pos) > box.minY) return true;
        }
        return false;
    }
}
