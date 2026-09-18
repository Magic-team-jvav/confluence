package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public record PathNode(Vec3 pos, float yaw, float pitch, float roll) {

    public PathNode(Vec3 pos, Vec3 direction) {
        this(pos, yawFromDirection(direction), pitchFromDirection(direction), 0);
    }

    public static float yawFromDirection(Vec3 direction) {
        return (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
    }

    public static float pitchFromDirection(Vec3 direction) {
        if (direction.lengthSqr() < 1.0E-12) {
            return 0;
        }
        Vec3 normalized = direction.normalize();
        return (float) Math.toDegrees(Math.asin(-normalized.y));
    }

    public PathNode lerp(PathNode to, float partialTick) {
        return new PathNode(this.pos().lerp(to.pos(), partialTick), Mth.rotLerp(partialTick, this.yaw(), to.yaw()), Mth.rotLerp(partialTick, this.pitch(), to.pitch()), Mth.rotLerp(partialTick, this.roll(), to.roll()));
    }

    public PathNode modifyPos(Vec3 pos) {
        return new PathNode(pos, yaw, pitch, roll);
    }

    public PathNode modifyEuler(float yaw, float pitch, float roll) {
        return new PathNode(pos, yaw, pitch, roll);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PathNode other) {
            return other.pos().equals(this.pos()) && other.yaw() == this.yaw() && other.pitch() == this.pitch() && other.roll() == this.roll();
        }
        return false;
    }
}
