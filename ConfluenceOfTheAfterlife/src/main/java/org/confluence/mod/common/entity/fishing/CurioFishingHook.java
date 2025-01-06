package org.confluence.mod.common.entity.fishing;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;

import java.util.function.IntFunction;

public class CurioFishingHook extends AbstractFishingHook implements VariantHolder<CurioFishingHook.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(CurioFishingHook.class, EntityDataSerializers.INT);

    public CurioFishingHook(EntityType<CurioFishingHook> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CurioFishingHook(Player player, Level pLevel, int pLuck, int pLureSpeed, Variant variant) {
        super(ModEntities.CURIO_FISHING_HOOK.get(), pLevel, pLuck, pLureSpeed);
        setVariant(variant);
        setup(player);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, 0);
    }

    @Override
    public void setVariant(Variant pVariant) {
        entityData.set(DATA_VARIANT_ID, pVariant.id);
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(entityData.get(DATA_VARIANT_ID));
    }

    public enum Variant implements StringRepresentable {
        COMMON(0, "common"),
        GLOWING(1, "glowing"),
        LAVA(2, "lava"),
        HELIUM(3, "helium"),
        NEON(4, "neon"),
        ARGON(5, "argon"),
        KRYPTON(6, "krypton"),
        XENON(7, "xenon");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public static Variant byId(int pId) {
            return BY_ID.apply(pId);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
