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

public class BaseHookEntity extends AbstractHookEntity implements VariantHolder<BaseHookEntity.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(BaseHookEntity.class, EntityDataSerializers.INT);

    public BaseHookEntity(EntityType<BaseHookEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BaseHookEntity(BaseHookItem item, Player player, Level level, Variant variant) {
        super(ModEntities.BASE_HOOK.get(), item, player, level);
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
        GRAPPLING(0, "grappling"), // 抓钩
        AMETHYST(1, "amethyst"), // 紫晶钩
        TOPAZ(2, "topaz"), // 黄玉钩
        SAPPHIRE(3, "sapphire"), // 蓝玉钩
        JADE(4, "jade"), // 翡翠钩
        RUBY(5, "ruby"), // 红玉钩
        AMBER(6, "amber"), // 琥珀钩
        DIAMOND(7, "diamond"); // 钻石钩

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
