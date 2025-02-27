package org.confluence.mod.common.entity.hook;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.item.hook.BaseHookItem;

import java.util.function.IntFunction;

public abstract class AbstractHookEntity extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_HOOK_STATE = SynchedEntityData.defineId(AbstractHookEntity.class, EntityDataSerializers.INT);
    public final float hookRangeSqr;
    private final BaseHookItem.HookType hookType;
    private BlockPos hookedPos;
    public float lastDelta = 0.0F;

    public AbstractHookEntity(EntityType<? extends AbstractHookEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
        this.hookRangeSqr = 0.0F;
        this.hookType = null;
    }

    public AbstractHookEntity(EntityType<? extends AbstractHookEntity> entityType, BaseHookItem item, Player player, Level level) {
        super(entityType, level);
        this.hookRangeSqr = item.getHookRange() * item.getHookRange();
        this.hookType = item.getHookType();
        setOwner(player);
        setNoGravity(true);
        setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HOOK_STATE, 0);
    }

    public HookState getHookState() {
        return HookState.byId(entityData.get(DATA_HOOK_STATE));
    }

    public void setHookState(HookState state) {
        entityData.set(DATA_HOOK_STATE, state.id);
    }

    public double getPullVelocity() {
        return 0.15;
    }

    protected void onHooked(BlockHitResult hitResult, ItemStack itemStack) {
        BlockPos blockPos = hitResult.getBlockPos();
        level().gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, level().getBlockState(blockPos)));
        setDeltaMovement(Vec3.ZERO);
        setHookState(HookState.HOOKED);
        this.hookedPos = blockPos;
        this.hasImpulse = true;
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (owner == null || owner.isRemoved()) {
            discard();
            return;
        }

        Vec3 motion = getDeltaMovement();
        double x = getX() + motion.x;
        double y = getY() + motion.y;
        double z = getZ() + motion.z;
        setPos(x, y, z);

        HookState hookState = getHookState();
        if (hookState == HookState.POP) {
            setDeltaMovement(getDeltaMovement().scale(0.95).add(owner.position().subtract(position()).normalize().scale(0.1)));
            if (distanceToSqr(owner) < 2.0) {
                discard();
                return;
            }
        }
        if (!level().isClientSide) {
            ItemStack hook = owner.getData(ModAttachmentTypes.EXTRA_INVENTORY).getHook();
            if (hookState != HookState.POP && distanceToSqr(owner) > hookRangeSqr) {
                setHookState(HookState.POP);
            } else if (hookState == HookState.PUSH) {
                Vec3 pos = position();
                Vec3 nextPos = pos.add(motion);
                BlockHitResult hitResult = level().clip(new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hitResult.getType() != HitResult.Type.BLOCK) return;
                motion = motion.scale(0.5);
                if (hitResult.isInside()) {
                    setPos(getX() - motion.x, getY() - motion.y, getZ() - motion.z);
                } else {
                    setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
                }
                onHitBlock(hitResult);
                onHooked(hitResult, hook);
            } else if (hookState == HookState.HOOKED && hookedPos != null && level().getBlockState(hookedPos).canBeReplaced()) {
                setHookState(HookState.POP);
            }
        }
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float x = -Mth.sin(pY * Mth.DEG_TO_RAD) * Mth.cos(pX * Mth.DEG_TO_RAD);
        float y = -Mth.sin((pX + pZ) * Mth.DEG_TO_RAD);
        float z = Mth.cos(pY * Mth.DEG_TO_RAD) * Mth.cos(pX * Mth.DEG_TO_RAD);
        shoot(x, y, z, pVelocity, pInaccuracy);
        Vec3 motion = pShooter.getDeltaMovement();
        setDeltaMovement(getDeltaMovement().add(motion.x, 0.0, motion.z));
    }

    public BaseHookItem.HookType getHookType() {
        return hookType;
    }

    public enum HookState implements StringRepresentable {
        PUSH(0, "push"), // 发射
        POP(1, "pop"), // 收回
        HOOKED(2, "hooked"); // 抓住

        public static final Codec<HookState> CODEC = StringRepresentable.fromEnum(HookState::values);
        private static final IntFunction<HookState> BY_ID = ByIdMap.continuous(HookState::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        final int id;
        private final String name;

        HookState(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public static HookState byId(int pId) {
            return BY_ID.apply(pId);
        }

        public String getSerializedName() {
            return name;
        }
    }

    public static class Impl extends AbstractHookEntity {
        public Impl(EntityType<? extends AbstractHookEntity> entityType, Level pLevel) {
            super(entityType, pLevel);
        }

        public Impl(EntityType<? extends AbstractHookEntity> entityType, BaseHookItem item, Player player, Level level) {
            super(entityType, item, player, level);
        }
    }
}
