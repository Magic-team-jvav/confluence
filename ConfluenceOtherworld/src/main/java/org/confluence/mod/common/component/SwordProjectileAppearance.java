package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

/// 剑气的客户端表现配置。
public interface SwordProjectileAppearance {
    Codec<SwordProjectileAppearance> CODEC = Type.CODEC.dispatch("type", SwordProjectileAppearance::type, Type::codec);

    Type type();

    record Hidden() implements SwordProjectileAppearance {
        public static final Hidden INSTANCE = new Hidden();
        public static final Codec<Hidden> CODEC = Codec.unit(INSTANCE);

        @Override
        public Type type() {
            return Type.HIDDEN;
        }
    }

    record Geo(ResourceLocation model, ResourceLocation texture,
               Optional<ResourceLocation> animation, Optional<String> animationClip,
               float scale, float offsetY, float rollSpeed, Lifecycle lifecycle,
               Material material) implements SwordProjectileAppearance {
        public static final Codec<Geo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(Geo::model),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Geo::texture),
                ResourceLocation.CODEC.optionalFieldOf("animation").forGetter(Geo::animation),
                Codec.STRING.optionalFieldOf("animationClip").forGetter(Geo::animationClip),
                Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Geo::scale),
                Codec.FLOAT.optionalFieldOf("offsetY", 0.0F).forGetter(Geo::offsetY),
                Codec.FLOAT.optionalFieldOf("rollSpeed", 0.0F).forGetter(Geo::rollSpeed),
                Lifecycle.CODEC.optionalFieldOf("lifecycle", Lifecycle.GROW).forGetter(Geo::lifecycle),
                Material.CODEC.optionalFieldOf("material", Material.CUTOUT).forGetter(Geo::material)
        ).apply(instance, Geo::new));

        @Override
        public Type type() {
            return Type.GEO;
        }
    }

    record Model(ResourceLocation model, ResourceLocation texture, float scale, float offsetY,
                 float offsetZ, float rollSpeed, Lifecycle lifecycle,
                 Material material) implements SwordProjectileAppearance {
        public static final Codec<Model> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(Model::model),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Model::texture),
                Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Model::scale),
                Codec.FLOAT.optionalFieldOf("offsetY", 0.0F).forGetter(Model::offsetY),
                Codec.FLOAT.optionalFieldOf("offsetZ", 0.0F).forGetter(Model::offsetZ),
                Codec.FLOAT.optionalFieldOf("rollSpeed", 0.0F).forGetter(Model::rollSpeed),
                Lifecycle.CODEC.optionalFieldOf("lifecycle", Lifecycle.GROW).forGetter(Model::lifecycle),
                Material.CODEC.optionalFieldOf("material", Material.CUTOUT).forGetter(Model::material)
        ).apply(instance, Model::new));

        @Override
        public Type type() {
            return Type.MODEL;
        }
    }

    record Item(float scale, Transform transform,
                Optional<ResourceLocation> effect) implements SwordProjectileAppearance {
        public static final Codec<Item> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Item::scale),
                Transform.CODEC.optionalFieldOf("transform", Transform.FORWARD_SPIN).forGetter(Item::transform),
                ResourceLocation.CODEC.optionalFieldOf("effect").forGetter(Item::effect)
        ).apply(instance, Item::new));

        @Override
        public Type type() {
            return Type.ITEM;
        }
    }

    record Cross(ResourceLocation texture, int color, float scale, float spinSpeed,
                 int blockLight) implements SwordProjectileAppearance {
        public static final Codec<Cross> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Cross::texture),
                Codec.INT.optionalFieldOf("color", 0xFFFFFFFF).forGetter(Cross::color),
                Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Cross::scale),
                Codec.FLOAT.optionalFieldOf("spinSpeed", 0.0F).forGetter(Cross::spinSpeed),
                Codec.intRange(-1, 15).optionalFieldOf("blockLight", -1).forGetter(Cross::blockLight)
        ).apply(instance, Cross::new));

        @Override
        public Type type() {
            return Type.CROSS;
        }
    }

    enum Type implements StringRepresentable {
        HIDDEN,
        GEO,
        MODEL,
        ITEM,
        CROSS;

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private Codec<? extends SwordProjectileAppearance> codec() {
            return switch (this) {
                case HIDDEN -> Hidden.CODEC;
                case GEO -> Geo.CODEC;
                case MODEL -> Model.CODEC;
                case ITEM -> Item.CODEC;
                case CROSS -> Cross.CODEC;
            };
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    enum Lifecycle implements StringRepresentable {
        GROW,
        GROW_FADE;

        public static final Codec<Lifecycle> CODEC = StringRepresentable.fromEnum(Lifecycle::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    enum Material implements StringRepresentable {
        CUTOUT,
        ENERGY_SWIRL;

        public static final Codec<Material> CODEC = StringRepresentable.fromEnum(Material::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    enum Transform implements StringRepresentable {
        FORWARD_SPIN,
        OWNER_SWING;

        public static final Codec<Transform> CODEC = StringRepresentable.fromEnum(Transform::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
