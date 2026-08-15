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
import org.confluence.mod.common.entity.IVariant;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Locale;

public class Butterfly extends Bird implements VariantHolder<Butterfly.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Butterfly.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final Variant[] COMMON_SPAWN_VARIANTS = {
            Variant.JULIA, Variant.MONARCH, Variant.PURPLE_EMPEROR, Variant.RED_ADMIRAL,
            Variant.SULPHUR, Variant.TREE_NYMPH, Variant.ULYSSES, Variant.ZEBRA_SWALLOWTAIL
    };

    public Butterfly(EntityType<? extends Butterfly> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingCritter.createFlyingCritterAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.RED_ADMIRAL.ordinal());
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(
                Variant.values(), this.entityData.get(DATA_VARIANT), Variant.RED_ADMIRAL);
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
            setVariant(Variant.RED_ADMIRAL);
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
        setVariant(CritterVariantUtil.withRareVariant(
                random, COMMON_SPAWN_VARIANTS, Variant.GOLD));
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        registerFlyOnlyController(controllers);
    }

    public enum Variant implements IVariant {
        JULIA,
        MONARCH,
        PURPLE_EMPEROR,
        RED_ADMIRAL,
        SULPHUR,
        TREE_NYMPH,
        ULYSSES,
        ZEBRA_SWALLOWTAIL,
        GOLD;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/butterfly");
        }

        @Override
        public ResourceLocation texturePath() {
            return IVariant.resource("textures/entity/animal/butterfly/"
                    + getSerializedName() + "_butterfly.png");
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
}
