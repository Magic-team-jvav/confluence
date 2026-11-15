package org.confluence.mod.common.entity.monster;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.jetbrains.annotations.Nullable;

public interface WormSegment {
    int getSegmentIndex();
    @Nullable WormSegment getPrev();
    @Nullable WormSegment getNext();
    default boolean isTail() {return false;}

    /// 使用同一帧的整链节点决定朝向，不能分别插值每节的网络角度。
    default Vec3 interpolatedChainTangent(float partialTick) {
        if (!(this instanceof Entity self)) return Vec3.ZERO;
        Vec3 center = interpolatedPosition(self, partialTick);
        WormSegment previous = getPrev();
        WormSegment next = getNext();
        Vec3 front = previous instanceof Entity entity && !entity.isRemoved()
                ? interpolatedPosition(entity, partialTick) : center;
        Vec3 back = next instanceof Entity entity && !entity.isRemoved()
                ? interpolatedPosition(entity, partialTick) : center;
        return front.subtract(back);
    }

    private static Vec3 interpolatedPosition(Entity entity, float partialTick) {
        return new Vec3(Mth.lerp(partialTick, entity.xOld, entity.getX()),
                Mth.lerp(partialTick, entity.yOld, entity.getY()),
                Mth.lerp(partialTick, entity.zOld, entity.getZ()));
    }

    /// 飞行蠕虫不播放钻地音效；虫头、身体和尾节都可独立提供位置声源。
    default boolean supportsDigSound() {return true;}

    static boolean isWormDamage(DamageSource source) {
        // 同时检查攻击主体和射弹所有者，体节转发及弹幕伤害不能绕过同类免伤。
        return source.getEntity() instanceof WormSegment || source.getDirectEntity() instanceof WormSegment
                || source.getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof WormSegment;
    }

    static void orientAlong(Entity segment, Vec3 tangent) {
        if (tangent.lengthSqr() < 1.0E-7) return;
        // 各体节使用同一偏航与俯仰约定，避免等价欧拉角翻转背腹。
        LibEntityUtils.updateEntityRotation(segment, tangent);
    }

}
