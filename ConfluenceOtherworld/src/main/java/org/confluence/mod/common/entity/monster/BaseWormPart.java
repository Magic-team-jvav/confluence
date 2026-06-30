package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 蠕虫体节。每 tick 跟随前一个体节（或头部），保持固定间距。
 */
public class BaseWormPart extends Entity implements WormSegment {
    private static final float SEGMENT_SPACING = 1.6F;
    private static final float COLLISION_DAMAGE = 5.0F;
    private static final int COLLISION_COOLDOWN = 10;

    protected Entity leader;
    protected int index;
    protected boolean tail;
    protected int hurtCooldown;

    public BaseWormPart(EntityType<?> type, Level level) {
        super(type, level);
    }

    public BaseWormPart(Entity leader, int index, Level level) {
        super(leader.getType(), level);
        this.leader = leader;
        this.index = index;
        this.noPhysics = true;
        this.setPos(leader.getX(), leader.getY(), leader.getZ());
    }

    @Override
    public int getSegmentIndex() { return index; }

    @Override
    public WormSegment getPrev() {
        return leader instanceof BaseWormMonster head
                ? head.getSegment(index - 1) : null;
    }

    @Override
    public WormSegment getNext() {
        return leader instanceof BaseWormMonster head
                ? head.getSegment(index + 1) : null;
    }

    @Override
    public void updateSegmentPosition() {
        Vec3 myPos = position();
        Vec3 leaderPos = leader.position();
        Vec3 diff = myPos.subtract(leaderPos);
        if (diff.lengthSqr() < 0.001) diff = new Vec3(0, 1, 0);
        diff = diff.normalize().scale(SEGMENT_SPACING);
        Vec3 dest = leaderPos.add(diff);

        double dx = dest.x - myPos.x;
        double dy = dest.y - myPos.y;
        double dz = dest.z - myPos.z;
        setPos(dest.x, dest.y, dest.z);

        // Rotation
        float yaw = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
        float horizDist = Mth.sqrt((float) (dx * dx + dz * dz));
        float pitch = (float) (-Mth.atan2(dy, horizDist) * (180F / Math.PI));
        setRot(yaw, pitch);
        yRotO = yaw;
        xRotO = pitch;

        // Collision attack
        if (hurtCooldown > 0) hurtCooldown--;
        else doCollisionAttack();
    }

    private void doCollisionAttack() {
        if (!(leader instanceof BaseWormMonster head) || !head.isAlive()) return;
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(0.5))) {
            if (target == leader) continue;
            if (target.getType() == getType()) continue;
            target.hurt(damageSources().mobAttack(head), COLLISION_DAMAGE);
            hurtCooldown = COLLISION_COOLDOWN;
            break;
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPickable() { return true; }

    @Override
    public boolean canBeCollidedWith() { return leader != null && leader.isAlive(); }

    @Override
    public void tick() {
        super.tick();
        if (leader == null || !leader.isAlive()) {
            discard();
            return;
        }
        if (!level().isClientSide) {
            updateSegmentPosition();
            xo = getX();
            yo = getY();
            zo = getZ();
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(0.5F, 0.5F);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity;
    }
}
