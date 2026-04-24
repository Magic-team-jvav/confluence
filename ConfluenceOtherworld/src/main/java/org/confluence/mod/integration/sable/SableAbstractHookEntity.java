package org.confluence.mod.integration.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SableAbstractHookEntity {
    private static final boolean SUCCESS = true;
    private static final boolean FAIL = false;

    public static boolean onHitBlock(AbstractHookEntity thiz, BlockHitResult result) {
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(thiz.level(), result.getLocation());
        if (subLevel == null) {
            return FAIL;
        }

        Pose3dc pose3dc = subLevel.logicalPose();
        Vec3 localPosition = pose3dc.transformPositionInverse(thiz.position());
        Vec3 diff = result.getLocation().subtract(localPosition);
        thiz.setDeltaMovement(diff.x, diff.y, diff.z);

        Vec3 nudge = diff.normalize().scale(0.05F);

        Vec3 position = pose3dc.transformPosition(new Vec3(
                localPosition.x - nudge.x + diff.x,
                localPosition.y - nudge.y + diff.y,
                localPosition.z - nudge.z + diff.z
        ));

        thiz.setPosRaw(position.x, position.y, position.z);

        thiz.subLevel[0] = pose3dc;
        Vector3dc pos = pose3dc.rotationPoint();
        thiz.subLevel[1] = localPosition.subtract(pos.x(), pos.y(), pos.z());

        return SUCCESS;
    }

    public static void tick(AbstractHookEntity thiz) {
        if (thiz.subLevel[0] instanceof Pose3dc pose && thiz.subLevel[1] instanceof Vec3 delta) {
            Vector3d mutableDelta = new Vector3d(delta.x, delta.y, delta.z);
            pose.rotationPoint().add(mutableDelta, mutableDelta);
            Vector3d pos = pose.transformPosition(mutableDelta);
            thiz.setPos(pos.x, pos.y, pos.z);
        }
    }
}
