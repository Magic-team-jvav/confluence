package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.util.ModUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class TitaniumShardsProjectile extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_SHARDS_AMOUNT = SynchedEntityData.defineId(TitaniumShardsProjectile.class, EntityDataSerializers.INT);
    public final List<Vector3d> shardPos = new ArrayList<>();
    public final List<Vector3d> shardPosO = new ArrayList<>();

    public TitaniumShardsProjectile(EntityType<TitaniumShardsProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public TitaniumShardsProjectile(Player player) {
        super(ModEntities.TITANIUM_SHARDS_PROJECTILE.get(), player.level());
        setPos(player.getX(), player.getY() + 1, player.getZ());
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SHARDS_AMOUNT, 7);
    }

    @Override
    public void baseTick() {
        if (!(getOwner() instanceof Player player) || entityData.get(DATA_SHARDS_AMOUNT) <= 0) {
            discard();
            return;
        }

        setPos(player.getX(), player.getY() + 1, player.getZ());
        this.xo = player.xo;
        this.yo = player.yo + 1;
        this.zo = player.zo;

        int amount = entityData.get(DATA_SHARDS_AMOUNT);
        if (amount != shardPos.size() - 1) {
            shardPos.clear();
            double d = Math.PI / amount;
            for (int i = 0; i < amount; i++) {
                shardPos.add(new Vector3d(1.5, 0, 0).rotateY(d * i));
            }
            shardPosO.clear();
            shardPosO.addAll(shardPos);
        }
        for (int i = 0; i < amount; i++) {
            Vector3d end = shardPos.get(i);
            Vector3d start = shardPosO.get(i).set(end);
            end.rotateY(Math.PI * 0.025 * amount);

            if (!level().isClientSide) {
                Vec3 startVec = position().add(start.x, start.y, start.z);
                Vec3 endVec = position().add(end.x, end.y, end.z);
                AABB aabb = new AABB(start.x, start.y, start.z, end.x, end.y, end.z);
                EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level(), this, startVec, endVec, aabb, this::canHitEntity);
                if (hitResult != null && hitResult.getEntity().hurt(damageSources().mobProjectile(this, player), 10)) {
                    VectorUtils.knockBackA2B(this, hitResult.getEntity(), 1, 0.4);
                    entityData.set(DATA_SHARDS_AMOUNT, amount - 1);
                }
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return ModUtils.canHitEntity(target, getOwner());
    }
}
