package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.PlayerCloseCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.PanicFleeAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Locale;

public class Grasshopper extends BaseCritter implements VariantHolder<Grasshopper.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Grasshopper.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";

    public Grasshopper(EntityType<? extends Grasshopper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createCritterAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new PlayerCloseCondition(Grasshopper.this, 5.0), new PanicFleeAction(Grasshopper.this, 0.9)),
                        SequenceNode.of(new WaitAction(30 + random.nextInt(60)), new HopAction(Grasshopper.this)));
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.GREEN.ordinal());
    }

    @Override
    public Variant getVariant() {return Variant.values()[this.entityData.get(DATA_VARIANT)];}

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
        PortDataResultExtension.ifSuccess(Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), this::setVariant);
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
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
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
            return Confluence.asResource("animal/grasshopper");
        }

        @Override
        public ResourceLocation texturePath() {
            return Confluence.asResource("textures/entity/grasshopper/" + getSerializedName() + ".png");
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

    private static class HopAction extends BTNode {
        private final Grasshopper hopper;
        private int tick;

        HopAction(Grasshopper hopper) {this.hopper = hopper;}

        @Override
        public void start() {tick = 0;}

        @Override
        public BTStatus execute() {
            tick++;
            if (tick <= 5) return BTStatus.RUNNING;
            if (tick == 6) {
                float yaw = hopper.getRandom().nextFloat() * (float) Math.PI * 2;
                Vec3 dir = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
                hopper.setDeltaMovement(dir.x * 0.4, 0.5, dir.z * 0.4);
                hopper.hasImpulse = true;
                return BTStatus.RUNNING;
            }
            if (hopper.onGround() && tick > 7) return BTStatus.SUCCESS;
            if (tick > 40) return BTStatus.SUCCESS;
            return BTStatus.RUNNING;
        }
    }
}
