package org.confluence.mod.common.entity.hook;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.hook.BaseHookItem;

import java.util.function.IntFunction;

public abstract class AbstractHookEntity extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_HOOK_STATE = SynchedEntityData.defineId(AbstractHookEntity.class, EntityDataSerializers.INT);
    public final float hookRangeSqr;
    private final BaseHookItem.HookType hookType;
    private BlockPos hookPos;
    private BlockState hookedState;
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

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (!(owner instanceof Player player) || owner.isRemoved()) {
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
            setDeltaMovement(getDeltaMovement().scale(0.95).add(owner.position().subtract(position()).normalize().scale(0.2)));
            if (distanceToSqr(owner) < 4.0) {
                discard();
                return;
            }
        }
        if (!level().isClientSide) {
            ItemStack hook = ExtraInventory.of(player).getHook(false);
            if (hookState != HookState.POP && distanceToSqr(owner) > hookRangeSqr) {
                setHookState(HookState.POP);
            } else if (hookState == HookState.PUSH) {
                Vec3 pos = position();
                Vec3 nextPos = pos.add(getDeltaMovement());
                BlockHitResult hitResult = level().clip(new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    onHitBlock(hitResult);
                    onHooked(hitResult, hook);
                }
            } else if (hookState == HookState.HOOKED && (hookPos == null || level().getBlockState(hookPos) != hookedState)) {
                setHookState(HookState.POP);
            }
        }
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Vec3 vec3 = result.getLocation().subtract(position());
        setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale(0.05F);
        setPosRaw(getX() - vec31.x, getY() - vec31.y, getZ() - vec31.z);
        setPos(getX() + vec3.x, getY() + vec3.y, getZ() + vec3.z);
    }

    protected void onHooked(BlockHitResult hitResult, ItemStack itemStack) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = level().getBlockState(blockPos);
        level().gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, blockState));
        setDeltaMovement(Vec3.ZERO);
        setHookState(HookState.HOOKED);
        this.hookPos = blockPos;
        this.hookedState = blockState;
        this.hasImpulse = true;
        float ratio = getOwner() == null ? 1 : Mth.sqrt((float) blockPos.distSqr(getOwner().blockPosition())) / Mth.sqrt(hookRangeSqr);
        playSound(ModSoundEvents.HOOK_ATTACH.get(), 0.5F * ratio, 1.0F);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState).setPos(blockPos),
                    getX(), getY() + 0.5, getZ(), (int) (175 * ratio), 0.0, 0.0, 0.0, 0.15);
        }
    }

    @Override
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float cos = Mth.cos(pX * Mth.DEG_TO_RAD);
        float ry = pY * Mth.DEG_TO_RAD;
        float x = -Mth.sin(ry) * cos;
        float y = -Mth.sin((pX + pZ) * Mth.DEG_TO_RAD);
        float z = Mth.cos(ry) * cos;
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
