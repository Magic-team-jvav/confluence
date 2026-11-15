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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;



/// 同时承载普通蝎子与黑蝎子的同步变体实体。
///
/// 两种外观共享模型、属性和行为，只把自然生成选择、存档值与纹理映射留在本类。
/// 捕捉物品可以显式写入变体，客户端与重新加载后的服务端仍会得到相同外观。
public class Scorpion extends BaseCritter implements VariantHolder<Scorpion.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Scorpion.class, EntityDataSerializers.INT);
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.BLACK, 1)
            .add(Variant.NORMAL, 1)
            .build();

    public Scorpion(EntityType<? extends Scorpion> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createGroundCritterRoutine(0.7D), 1.0D);
            }
        };
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.NORMAL.ordinal());
    }

    @Override
    public Variant getVariant() {
        return Variant.BY_ID.apply(entityData.get(DATA_VARIANT));
    }

    @Override
    public void setVariant(Variant variant) {
        entityData.set(DATA_VARIANT, variant.ordinal());
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getVariant().serialize(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.NORMAL);
            return;
        }
        Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)).ifSuccess(this::setVariant);
    }

    @Override
    public ResourceLocation getModelPath() {
        return getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTexturePath() {
        return getVariant().texturePath();
    }

    public enum Variant implements IVariant {
        BLACK,
        NORMAL;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), NORMAL);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/scorpion");
        }

        @Override
        public ResourceLocation texturePath() {
            String name = this == BLACK ? "black_scorpion" : "scorpion";
            return IVariant.resource("textures/entity/animal/scorpion/" + name + ".png");
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
