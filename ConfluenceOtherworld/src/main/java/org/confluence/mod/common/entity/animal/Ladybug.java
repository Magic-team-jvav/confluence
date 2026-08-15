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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.IVariant;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Locale;

public class Ladybug extends Bird implements VariantHolder<Ladybug.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Ladybug.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";

    public Ladybug(EntityType<? extends Ladybug> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18)
                .add(Attributes.FLYING_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.RED.ordinal());
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(
                Variant.values(), this.entityData.get(DATA_VARIANT), Variant.RED);
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
            setVariant(Variant.RED);
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
                : Variant.RED);
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        /*
         * 瓢虫资源只定义 move.fly，没有空闲动画。始终循环翅膀动画可避免停顿阶段持续
         * 查询不存在的 misc.idle，同时保持 1.21 侧的飞行观感。
         */
        registerFlyOnlyController(controllers);
    }

    public enum Variant implements IVariant {
        RED, GOLD;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/ladybug");
        }

        @Override
        public ResourceLocation texturePath() {
            String name = this == GOLD ? "gold_ladybug" : "ladybug";
            return IVariant.resource("textures/entity/animal/ladybug/" + name + ".png");
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
