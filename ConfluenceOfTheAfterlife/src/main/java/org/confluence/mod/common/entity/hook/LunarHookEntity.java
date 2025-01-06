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

public class LunarHookEntity extends AbstractHookEntity implements VariantHolder<LunarHookEntity.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(LunarHookEntity.class, EntityDataSerializers.INT);

    public LunarHookEntity(EntityType<LunarHookEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public LunarHookEntity(BaseHookItem item, Player player, Level level, Variant variant) {
        super(ModEntities.LUNAR_HOOK.get(), item, player, level);
        setVariant(variant);
    }

    @Override
    public double getPullVelocity() {
        return 0.2196;
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
        NEBULA(0, "nebula"), // 星云
        SOLAR(1, "solar"), // 日耀
        STARDUST(2, "stardust"), // 星尘
        VORTEX(3, "vortex"); // 星旋

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
