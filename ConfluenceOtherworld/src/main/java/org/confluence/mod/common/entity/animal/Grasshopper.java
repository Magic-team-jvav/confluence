package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Locale;

public class Grasshopper extends BaseCritter implements VariantHolder<Grasshopper.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Grasshopper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_JUMPING =
            SynchedEntityData.defineId(
                    Grasshopper.class,
                    EntityDataSerializers.BOOLEAN);
    public static final String VARIANT_KEY = "Variant";

    public Grasshopper(EntityType<? extends Grasshopper> type, Level level) {
        super(type, level);
        this.moveControl = new JumpMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createInsectAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(
                        SequenceNode.of(new RandomStrollAction(
                                Grasshopper.this,
                                1.0,
                                10)),
                        0.9);
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.GREEN.ordinal());
        this.entityData.define(DATA_JUMPING, false);
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(
                Variant.values(), this.entityData.get(DATA_VARIANT), Variant.GREEN);
    }

    @Override
    public void setVariant(Variant v) {this.entityData.set(DATA_VARIANT, v.ordinal());}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getVariant().serialize(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.GREEN);
            return;
        }
        PortDataResultExtension.ifSuccess(Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), this::setVariant);
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(random.nextInt(CritterVariantUtil.GOLD_RARITY) == 0
                ? Variant.GOLD
                : Variant.GREEN);
    }

    @Override
    public ResourceLocation getModelPath() {
        return getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTexturePath() {
        return getVariant().texturePath();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "Jump",
                0,
                state -> {
                    if (entityData.get(DATA_JUMPING)) {
                        return state.setAndContinue(DefaultAnimations.JUMP);
                    }
                    state.resetCurrentAnimation();
                    return PlayState.STOP;
                }));
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance,
            float multiplier,
            DamageSource source) {
        return false;
    }

    public enum Variant implements IVariant {
        GREEN, GOLD;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/grasshopper");
        }

        @Override
        public ResourceLocation texturePath() {
            String name = this == GOLD ? "gold_grasshopper" : "grasshopper";
            return IVariant.resource("textures/entity/animal/grasshopper/" + name + ".png");
        }

        @Override
        public Codec<Variant> codec() {
            return CODEC;
        }

        @Override
        public String serializeKey() {
            return VARIANT_KEY;
        }
    }

    @Override
    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        entityData.set(DATA_JUMPING, jumping);
    }

    @Override
    protected float getJumpPower() {
        return super.getJumpPower()
                * (1.0F + random.nextFloat() * 0.5F);
    }

    /**
     * 把地面导航目标转换为间歇跳跃，避免蚱蜢像普通昆虫一样贴地滑行。
     */
    static final class JumpMoveControl extends MoveControl {
        private final Grasshopper grasshopper;
        private int jumpDelay;

        JumpMoveControl(Grasshopper grasshopper) {
            super(grasshopper);
            this.grasshopper = grasshopper;
        }

        @Override
        public void tick() {
            double deltaX = wantedX - mob.getX();
            double deltaY = wantedY - mob.getY();
            double deltaZ = wantedZ - mob.getZ();
            double distanceSquared =
                    deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distanceSquared < 2.500000277905201E-7) {
                mob.setZza(0.0F);
                return;
            }

            if (grasshopper.onGround()) {
                grasshopper.setJumping(false);
            }
            if (operation != Operation.MOVE_TO) {
                mob.setZza(0.0F);
                return;
            }

            float targetYaw = (float) (Mth.atan2(deltaZ, deltaX)
                    * Mth.RAD_TO_DEG) - 90.0F;
            if (distanceSquared > 1.0 && grasshopper.onGround()) {
                mob.setYRot(rotlerp(mob.getYRot(), targetYaw, 90.0F));
                mob.yHeadRot = mob.getYRot();
                mob.yBodyRot = mob.getYRot();
            }

            if (!grasshopper.onGround()) {
                mob.setSpeed((float) (speedModifier
                        * mob.getAttributeValue(
                        Attributes.MOVEMENT_SPEED)));
                return;
            }

            mob.setSpeed((float) (speedModifier
                    * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (jumpDelay-- > 0) {
                mob.setXxa(0.0F);
                mob.setZza(0.0F);
                mob.setSpeed(0.0F);
                return;
            }

            jumpDelay = randomDelay();
            mob.getJumpControl().jump();
            double horizontalDistance =
                    Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            if (horizontalDistance > 1.0E-5) {
                double impulse = Math.min(
                        distanceSquared * 0.4,
                        0.5)
                        + grasshopper.getRandom().nextDouble() * 0.5;
                grasshopper.addDeltaMovement(
                        new net.minecraft.world.phys.Vec3(
                                deltaX / horizontalDistance * impulse,
                                0.0,
                                deltaZ / horizontalDistance * impulse));
            }
            grasshopper.setJumping(true);
        }

        private int randomDelay() {
            return grasshopper.getRandom().nextInt(10) + 5;
        }
    }
}
