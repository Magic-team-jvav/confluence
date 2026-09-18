package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Supplier;

public abstract class MomentumAttachmentEntity extends AttachmentEntity implements IMomentumAttachmentEntity {

    private Vec3 velocity = Vec3.ZERO;
    private boolean physics = true;
    private float drag = 0.92F;
    private float gravity = -0.08F;

    public MomentumAttachmentEntity(RegistryObject<? extends AttachmentEntityType<?>> type) {
        super(type);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.VEC_3, this::getVelocity, this::setVelocity);
    }

    @Override
    public void tick() {
        super.tick();
        tickPhysics();
    }

    public void tickPhysics() {
        if (physics) {
            velocity = velocity.add(0, gravity, 0).scale(drag);
            Vec3 newPos = getPos().add(velocity);
            setPath(new PlannedPath("physics", Collections.singletonList(new PathNode(newPos, getYaw(), getPitch(), getRoll()))));
        }
    }

    public boolean isPhysics() {
        return physics;
    }

    public void setPhysics(boolean physics) {
        this.physics = physics;
    }

    @Override
    public @NotNull Vec3 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(@NotNull Vec3 velocity) {
        this.velocity = velocity;
    }

    @Override
    public void addVelocity(@NotNull Vec3 velocity) {
        this.velocity = this.velocity.add(velocity);
    }

    @Override
    public float getDrag() {
        return drag;
    }

    @Override
    public void setDrag(float drag) {
        this.drag = Mth.clamp(drag, 0, 1);
    }

    @Override
    public float getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
}
