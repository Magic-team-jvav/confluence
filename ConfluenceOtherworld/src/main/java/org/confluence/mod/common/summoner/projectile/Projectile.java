package org.confluence.mod.common.summoner.projectile;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.MomentumAttachmentEntity;

public abstract class Projectile extends MomentumAttachmentEntity {

    protected int maxTickCount = 200;

    public Projectile(RegistryObject<? extends AttachmentEntityType<?>> type) {
        super(type);
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
            setCurrentPathNode(getCurrentPathNode().faceDirection(velocity));
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
