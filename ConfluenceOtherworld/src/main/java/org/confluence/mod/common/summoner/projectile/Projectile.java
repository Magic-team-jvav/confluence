package org.confluence.mod.common.summoner.projectile;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.MomentumAttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.jetbrains.annotations.NotNull;

public abstract class Projectile extends MomentumAttachmentEntity {

    protected int maxTickCount = 200;

    public Projectile(Holder<AttachmentEntityType<?>> type) {
        super(type);
    }

    @Override
    public @NotNull DamageSource getDamageSource() {
        return new AttachmentEntityDamageSource(
                LibDamageTypes.of(getLevel(), LibDamageTypes.SUMMONER, owner).typeHolder(),
                null,
                owner,
                getPos(),
                this
        );
    }

    @Override
    public void tick() {
        super.tick();
        if (getMaxTickCount() > 0 && getTickCount() >= getMaxTickCount()) {
            setRemove();
        }
    }

    @Override
    public void tickPhysics() {
        super.tickPhysics();
        if (isPhysics()) {
            Vec3 velocity = getVelocity();
            float yaw = (float) Math.toDegrees(Math.atan2(-velocity.x, velocity.z));
            float pitch = (float) Math.toDegrees(Math.atan2(-velocity.y, Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)));
            PathNode pathNode = getCurrentPathNode();
            setCurrentPathNode(pathNode.modifyEuler(yaw, pitch, pathNode.roll()));
        }
    }

    @Override
    public void onLevelChange() {
        setRemove();
    }

    public void setMaxTickCount(int maxTickCount) {
        this.maxTickCount = maxTickCount;
    }

    public int getMaxTickCount() {
        return maxTickCount;
    }
}
