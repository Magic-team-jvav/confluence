package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.confluence.mod.common.entity.PartHitTarget;
import org.confluence.mod.common.entity.monster.WormSegment;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.IdentityHashMap;
import java.util.Map;

/// 头、身体和尾节共用所属蠕虫的播放间隔，不为每个体节建立循环音效。
public final class WormDigSoundHandler {
    private static final double SOUND_RANGE_SQUARED = 16.0 * 16.0;
    private static final Map<Entity, Long> NEXT_SOUND = new IdentityHashMap<>();
    private static ClientLevel soundLevel;
    private static long tick;
    private static long nextPlayTick;

    private WormDigSoundHandler() {}

    /// 客户端统一挑选可听见且正在钻地的体节，播放原生短音效，无需管理播放实例。
    public static void tick(Minecraft minecraft) {
        if (minecraft.level != soundLevel || minecraft.player == null) {
            NEXT_SOUND.clear();
            soundLevel = minecraft.level;
            tick = nextPlayTick = 0;
        }
        if (minecraft.level == null || minecraft.player == null || minecraft.isPaused()) return;
        tick++;
        NEXT_SOUND.entrySet().removeIf(entry -> entry.getKey().level() != soundLevel
                || !entry.getKey().isAlive() || entry.getKey().isRemoved() || tick > entry.getValue() + 20);
        if (tick < nextPlayTick) return;

        Entity source = null;
        Entity owner = null;
        double nearest = SOUND_RANGE_SQUARED;
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!(entity instanceof WormSegment segment) || !segment.supportsDigSound()
                    || !entity.isAlive() || entity.isRemoved() || entity.isSilent()) continue;
            Entity head = entity instanceof PartHitTarget part ? part.encounterOwner() : entity;
            if (head == null || !head.isAlive() || tick < NEXT_SOUND.getOrDefault(head, 0L))
                continue;
            double distance = entity.distanceToSqr(minecraft.player);
            if (distance > nearest || !isMoving(entity) || !insideSolid(entity)) continue;
            source = entity;
            owner = head;
            nearest = distance;
        }
        if (source == null) return;
        minecraft.getSoundManager().play(new EntityBoundSoundInstance(ModSoundEvents.DIG_SOUND.get(),
                SoundSource.HOSTILE, 0.75F, 1.0F, source, SoundInstance.createUnseededRandom().nextLong()));
        NEXT_SOUND.put(owner, tick + intervalTicks(owner.distanceTo(minecraft.player)));
        /// 多条虫也不在同一时刻叠播；短音效不循环，体节数量不会放大播放频率。
        nextPlayTick = tick + 4;
    }

    /// 体节通常通过位置同步移动，不能使用其通常为零的速度向量。
    private static boolean isMoving(Entity entity) {
        double dx = entity.getX() - entity.xOld;
        double dy = entity.getY() - entity.yOld;
        double dz = entity.getZ() - entity.zOld;
        return dx * dx + dy * dy + dz * dz >= 1.0E-6;
    }

    /// 将泰拉十至二十 tick 的钻地节奏换算为 MC 的四至七 tick。
    static int intervalTicks(double distanceBlocks) {
        return (int) Math.ceil(Mth.clamp(distanceBlocks * 16.0 / 40.0, 10.0, 20.0) / 3.0);
    }

    /// 只检查已加载方块的实际碰撞形状，空气和液体不触发钻地声。
    private static boolean insideSolid(Entity entity) {
        var bounds = entity.getBoundingBox();
        var entityShape = Shapes.create(bounds);
        for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(bounds.minX), Mth.floor(bounds.minY), Mth.floor(bounds.minZ),
                Mth.floor(bounds.maxX), Mth.floor(bounds.maxY), Mth.floor(bounds.maxZ))) {
            if (!entity.level().getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4))
                continue;
            var shape = entity.level().getBlockState(pos).getCollisionShape(entity.level(), pos);
            if (!shape.isEmpty() && Shapes.joinIsNotEmpty(entityShape, shape.move(pos.getX(), pos.getY(), pos.getZ()), BooleanOp.AND))
                return true;
        }
        return false;
    }
}
