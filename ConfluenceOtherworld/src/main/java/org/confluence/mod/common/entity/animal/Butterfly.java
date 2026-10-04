package org.confluence.mod.common.entity.animal;

import com.mojang.serialization.Codec;
import java.util.Locale;
import java.util.function.IntFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;



public class Butterfly extends BaseFlyingCritter implements VariantHolder<Butterfly.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Butterfly.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.JULIA, 399)
            .add(Variant.MONARCH, 399)
            .add(Variant.PURPLE_EMPEROR, 399)
            .add(Variant.RED_ADMIRAL, 399)
            .add(Variant.SULPHUR, 399)
            .add(Variant.TREE_NYMPH, 399)
            .add(Variant.ULYSSES, 399)
            .add(Variant.ZEBRA_SWALLOWTAIL, 399)
            .add(Variant.GOLD, 8)
            .build();

    public Butterfly(EntityType<? extends Butterfly> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.RED_ADMIRAL.ordinal());
    }

    @Override
    public Variant getVariant() {
        return Variant.BY_ID.apply(entityData.get(DATA_VARIANT));
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
        Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)).ifSuccess(this::setVariant);
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(SPAWN_VARIANTS.select(random));
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createEnemyAvoidingFlyingRoutine(), 1.25D);
            }
        };
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

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
        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), RED_ADMIRAL);

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
            return IVariant.resource("textures/entity/animal/butterfly/" + getSerializedName() + "_butterfly.png");
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
