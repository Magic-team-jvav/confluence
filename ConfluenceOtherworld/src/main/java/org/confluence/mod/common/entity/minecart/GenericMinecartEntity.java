package org.confluence.mod.common.entity.minecart;

import com.mojang.serialization.Codec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class GenericMinecartEntity extends BaseMinecartEntity implements VariantHolder<GenericMinecartEntity.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(GenericMinecartEntity.class, EntityDataSerializers.INT);

    public GenericMinecartEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GenericMinecartEntity(Level level, double x, double y, double z, Abilities<? extends BaseMinecartEntity> abilities, Variant variant) {
        super(level, x, y, z, abilities);
        setVariant(variant);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, 0);
    }

    @Override
    public void setVariant(Variant variant) {
        entityData.set(DATA_VARIANT_ID, variant.id);
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(entityData.get(DATA_VARIANT_ID));
    }

    public record Variant(String name, int id) implements StringRepresentable {
        public static final List<Variant> VALUES = new ArrayList<>();

        public static final Variant DESERT = register("desert"),
                BEE = register("bee"),
                LADYBUG = register("ladybug"),
                PIGRON = register("pigron"),
                SUNFLOWER = register("sunflower"),
                SHROOM = register("shroom"),
                AMETHYST = register("amethyst"),
                TOPAZ = register("topaz"),
                SAPPHIRE = register("sapphire"),
                JADE = register("jade"),
                RUBY = register("ruby"),
                DIAMOND = register("diamond"),
                AMBER = register("amber"),
                BEETLE = register("beetle"),
                PARTY = register("party"),
                DUTCHMAN = register("dutchman"),
                STEAMPUNK = register("steampunk"),
                COFFIN = register("coffin"),
                FART = register("fart"),
                TERRA_FART = register("terra_fart");

        public static final Codec<Variant> CODEC = StringRepresentable.fromValues(() -> VALUES.toArray(Variant[]::new));

        public static Variant byId(int id) {
            return VALUES.get(id);
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        private static Variant register(String name) {
            Variant variant = new Variant(name, VALUES.size());
            VALUES.add(variant);
            return variant;
        }
    }
}
