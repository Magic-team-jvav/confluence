package org.confluence.mod.common.entity.hook;

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
import org.confluence.mod.common.item.hook.BaseHookItem;

import java.util.function.IntFunction;

public class MimicHookEntity extends AbstractHookEntity implements VariantHolder<MimicHookEntity.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(MimicHookEntity.class, EntityDataSerializers.INT);

    public MimicHookEntity(EntityType<MimicHookEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public MimicHookEntity(BaseHookItem item, Player player, Level level, Variant variant) {
        super(ModEntities.MIMIC_HOOK.get(), item, player, level);
        setVariant(variant);
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
        ILLUMINANT(0, "illuminant"),
        WORM(1, "worm"),
        TENDON(2, "tendon");

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
