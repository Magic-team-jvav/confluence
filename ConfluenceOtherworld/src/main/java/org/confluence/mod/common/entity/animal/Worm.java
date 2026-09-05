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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.mesdag.portlib.wrapper.common.extensions.IPortAttributesExtension;

import java.util.Locale;

public class Worm extends BaseCritter implements VariantHolder<Worm.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Worm.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final Variant[] COMMON_SPAWN_VARIANTS = {
            Variant.NORMAL, Variant.NIGHTCRAWLER
    };

    public Worm(EntityType<? extends Worm> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createInsectAttributes().add(IPortAttributesExtension.fallDamageMultiplier().get(), 0.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createGroundCritterRoutine(1.0), 1.5);
            }
        };
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(Variant.values(), this.entityData.get(DATA_VARIANT), Variant.NORMAL);
    }

    @Override
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.NORMAL.ordinal());
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
        PortDataResultExtension.ifSuccess(Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), this::setVariant);
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(CritterVariantUtil.withRareVariant(random, COMMON_SPAWN_VARIANTS, Variant.GOLD));
    }

    /// 蚯蚓的三种外观共用模型，但纹理存放在独立子目录中。
    /// 通用小动物的平铺纹理规则不适用于这里，因此直接返回当前同步变体声明的纹理。
    @Override
    public ResourceLocation getTexturePath() {
        return getVariant().texturePath();
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    public enum Variant implements IVariant {
        NORMAL, GOLD, NIGHTCRAWLER;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        @Override
        public Codec<Variant> codec() {
            return CODEC;
        }

        @Override
        public String serializeKey() {
            return VARIANT_KEY;
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/worm");
        }

        @Override
        public ResourceLocation texturePath() {
            String name = switch (this) {
                case NORMAL -> "worm";
                case GOLD -> "gold_worm";
                case NIGHTCRAWLER -> "enchanted_nightcrawler";
            };
            return IVariant.resource("textures/entity/animal/worm/" + name + ".png");
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
