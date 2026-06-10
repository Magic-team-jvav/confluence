package org.confluence.mod.common.entity.minecart;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class BaseMinecartEntity extends Minecart {
    public static final double MECHANICAL_CART_MAX_SPEED = 1.23;
    public static final double MECHANICAL_CART_ACCELERATION = 2.5;
    public static final double MECHANICAL_CART_DRAG_AIR = 0.99;

    protected ResourceLocation dropItem = ResourceLocation.withDefaultNamespace("air"); // both
    protected float maxSpeed = 0.0F; // both
    protected double acceleration = 0.0; // both
    protected @Nullable LivingEntity driver; // server

    public BaseMinecartEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseMinecartEntity(Level level, double x, double y, double z, Abilities<? extends BaseMinecartEntity> abilities) {
        super(abilities.entityType.get(), level);
        this.dropItem = abilities.dropItem;
        this.acceleration = abilities.acceleration;
        setCurrentCartSpeedCapOnRail(abilities.maxSpeed);
        setDragAir(abilities.dragAir);
        setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            this.driver = getFirstPassenger() instanceof LivingEntity living ? living : null;
            Vec3 movement = getDeltaMovement();
            boolean bx = Math.abs(movement.x) > 0.1;
            boolean bz = Math.abs(movement.z) > 0.1;
            if (bx || bz) {
                double sx = bx ? movement.x : 0.0;
                double sz = bz ? movement.z : 0.0;
                AABB aabb = getBoundingBox().move(sx, 0.0, sz).inflate(Math.abs(sx), 0.0, Math.abs(sz));
                List<Entity> list = level().getEntities(this, aabb, entity -> !hasPassenger(entity) && EntitySelector.pushableBy(this).test(entity));
                if (!list.isEmpty()) {
                    boolean killed = false;
                    for (Entity entity : list) {
                        double distance = movement.horizontalDistance();
                        entity.hurt(damageSources().flyIntoWall(), (float) distance * 5.0F);
                        LibMathUtils.knockBackA2B(this, entity, distance * 0.5, 0.2);
                        if (!entity.isAlive()) killed = true;
                    }
                    if (killed && driver instanceof ServerPlayer serverPlayer) {
                        AchievementUtils.awardAchievement(serverPlayer, "vehicular_manslaughter");
                    }
                }
            }
        }
    }

    @Override
    public boolean isPushable() {
        return driver == null;
    }

    @Override
    public void setDeltaMovement(Vec3 deltaMovement) {
        if (!horizontalCollision || shouldPerformStop()) {
            super.setDeltaMovement(deltaMovement);
        }
    }

    private boolean shouldPerformStop() {
        if (isOnRails()) {
            BlockPos pos = getCurrentRailPosition();
            BlockState state = level().getBlockState(pos);
            if (state.getBlock() instanceof BaseRailBlock block) {
                return !block.getRailDirection(state, level(), pos, this).isAscending();
            }
        }
        return true;
    }

    @Override
    public void moveMinecartOnRail(BlockPos pos) {
        boolean upgradeKit = driver != null && EverBeneficial.of(driver).isMinecartUpgradeKitUsed();
        if (upgradeKit) setDragAir(getUpgradedDragAir());
        double d25 = upgradeKit ? getUpgradedMaxSpeed() : getMaxSpeedWithRail();
        double d24 = upgradeKit ? getUpgradedAcceleration() : (isVehicle() ? acceleration : 1.0);
        Vec3 motion = getDeltaMovement();
        move(MoverType.SELF, new Vec3(Mth.clamp(d24 * motion.x, -d25, d25), 0.0, Mth.clamp(d24 * motion.z, -d25, d25)));
    }

    protected double getUpgradedDragAir() {
        return MECHANICAL_CART_DRAG_AIR;
    }

    protected double getUpgradedMaxSpeed() {
        return MECHANICAL_CART_MAX_SPEED;
    }

    protected double getUpgradedAcceleration() {
        return MECHANICAL_CART_ACCELERATION;
    }

    @Override
    public Item getDropItem() {
        return BuiltInRegistries.ITEM.get(dropItem);
    }

    @Override
    public void setCurrentCartSpeedCapOnRail(float value) {
        this.maxSpeed = value;
        super.setCurrentCartSpeedCapOnRail(value);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return maxSpeed;
    }

    @Override
    protected double getMaxSpeed() {
        return super.getMaxSpeed() * 2.0;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.dropItem = ResourceLocation.tryParse(compound.getString("DropItem"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("DropItem", dropItem.toString());
    }

    public record Abilities<E extends BaseMinecartEntity>(Supplier<EntityType<E>> entityType, ResourceLocation dropItem, float maxSpeed, double acceleration, double dragAir) {}
}
