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
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.PlayerCloseCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.PanicFleeAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Locale;

public class Fairy extends BaseCritter implements VariantHolder<Fairy.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Fairy.class, EntityDataSerializers.INT);

    public Fairy(EntityType<? extends Fairy> type, Level level) {
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
                        SequenceNode.of(new PlayerCloseCondition(Fairy.this, 5.0), new PanicFleeAction(Fairy.this, 0.8)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40))));
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.BLUE.ordinal());
    }

    @Override public Variant getVariant() { return Variant.values()[this.entityData.get(DATA_VARIANT)]; }
    @Override public void setVariant(Variant v) { this.entityData.set(DATA_VARIANT, v.ordinal()); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        PortDataResultExtension.ifSuccess(Variant.CODEC.encodeStart(NbtOps.INSTANCE, getVariant()), t -> tag.put("Variant", t));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        PortDataResultExtension.ifSuccess(Variant.CODEC.parse(NbtOps.INSTANCE, tag.get("Variant")), this::setVariant);
    }

    @Override public ResourceLocation getModelPath() { return getVariant().modelPath(); }
    @Override public ResourceLocation getTexturePath() { return getVariant().texturePath(); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }

    public enum Variant implements StringRepresentable {
        BLUE, GREEN, PINK;
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        @Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
        public ResourceLocation modelPath() { return Confluence.asResource("animal/fairy"); }
        public ResourceLocation texturePath() { return Confluence.asResource("textures/entity/fairy/" + getSerializedName() + ".png"); }
    }
}
