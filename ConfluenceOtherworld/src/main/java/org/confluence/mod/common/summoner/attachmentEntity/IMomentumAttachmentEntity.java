package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface IMomentumAttachmentEntity {
    @NotNull Vec3 getVelocity();

    void setVelocity(@NotNull Vec3 velocity);

    void addVelocity(@NotNull Vec3 velocity);

    float getDrag();

    void setDrag(float drag);

    float getGravity();

    void setGravity(float gravity);
}
