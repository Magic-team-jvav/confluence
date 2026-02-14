package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TitaniumShardsProjectile extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_SHARDS_AMOUNT = SynchedEntityData.defineId(TitaniumShardsProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(TitaniumShardsProjectile.class, EntityDataSerializers.OPTIONAL_UUID);
    public List<Vector3d> shardPos = new ArrayList<>();
    public List<Vector3d> shardPosO = new ArrayList<>();
    private boolean synced;

    public TitaniumShardsProjectile(EntityType<TitaniumShardsProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        setInvulnerable(true);
        this.noPhysics = true;
    }

    public TitaniumShardsProjectile(Player player) {
        this(ModEntities.TITANIUM_SHARDS_PROJECTILE.get(), player.level());
        setPos(player.position());
        setOwner(player);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SHARDS_AMOUNT, 7).define(DATA_OWNER_UUID, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && DATA_OWNER_UUID.equals(key)) {
            this.ownerUUID = entityData.get(DATA_OWNER_UUID).orElse(null);
            this.synced = true;
        }
    }

    @Override
    public void baseTick() {
        Player player = getOwner();
        if (player == null) {
            if (!synced) return;
            discard();
            return;
        } else if (!player.hasEffect(ModEffects.TITANIUM_BARRIER)) {
            discard();
            return;
        }

        int amount = entityData.get(DATA_SHARDS_AMOUNT);
        if (amount <= 0 || isRemoved()) {
            discard();
            return;
        }

        setPos(player.getX(), player.getY(), player.getZ());
        this.xOld = player.xOld;
        this.yOld = player.yOld;
        this.zOld = player.zOld;

        if (amount != shardPos.size()) {
            this.shardPos.clear();
            this.shardPosO.clear();
            double d = Math.TAU / amount;
            for (int i = 0; i < amount; i++) {
                Vector3d e = new Vector3d(1 + amount / 10.0, 0, 0).rotateY(d * i);
                this.shardPos.add(e);
                this.shardPosO.add(new Vector3d(e));
            }
        }

        double r = Math.PI * Mth.lerp(amount / 8.0, 0.05, 0.2);
        Entity targetToHurt = null;

        for (int i = 0; i < amount; i++) {
            Vector3d end = shardPos.get(i);
            end.y = player.getBbHeight() * 0.5;
            shardPosO.get(i).set(end);
            end.rotateY(r);
            if (!level().isClientSide && targetToHurt == null) {
                Vec3 startVec = position().add(shardPosO.get(i).x, shardPosO.get(i).y, shardPosO.get(i).z);
                Vec3 endVec = position().add(end.x, end.y, end.z);
                AABB aabb = new AABB(startVec, endVec).inflate(0.5);
                EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level(), this, startVec, endVec, aabb, this::canHitEntity);
                if (hitResult != null) {
                    targetToHurt = hitResult.getEntity();
                }
            }
        }
        if (targetToHurt != null) {
            final Entity finalTarget = targetToHurt;
            if (player.getServer() != null) {
                player.getServer().tell(new net.minecraft.server.TickTask(player.getServer().getTickCount(), () -> {
                    if (finalTarget.isAlive() && !this.isRemoved()) {
                        if (finalTarget.hurt(damageSources().playerAttack(player), 25.0F)) {
                            VectorUtils.knockBackA2B(this, finalTarget, 1.0, 0.4);
                            int currentAmount = entityData.get(DATA_SHARDS_AMOUNT);
                            this.entityData.set(DATA_SHARDS_AMOUNT, Math.max(0, currentAmount - 1));
                        }
                    }
                }));
            }
        }
    }
    @Override
    protected boolean canHitEntity(Entity target) {
        return LibUtils.canHitEntity(target, getOwner());
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        if (owner != null && !(owner instanceof Player)) {
            throw new IllegalArgumentException("Owner must be a player!");
        }
        super.setOwner(owner);
    }

    @Override
    public @Nullable Player getOwner() {
        if (cachedOwner != null && !cachedOwner.isRemoved()) {
            return (Player) cachedOwner;
        } else if (ownerUUID != null) {
            return (Player) (this.cachedOwner = level().getPlayerByUUID(ownerUUID));
        } else {
            return null;
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
