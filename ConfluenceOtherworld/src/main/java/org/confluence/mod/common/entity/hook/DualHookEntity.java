package org.confluence.mod.common.entity.hook;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.hook.BaseHookItem;

import java.util.function.IntFunction;

public class DualHookEntity extends AbstractHookEntity implements VariantHolder<DualHookEntity.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(DualHookEntity.class, EntityDataSerializers.INT);

    public DualHookEntity(EntityType<DualHookEntity> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public DualHookEntity(BaseHookItem item, Player player, Level level, Variant variant) {
        super(ModEntities.DUAL_HOOK.get(), item, player, level);
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

    @Override
    protected void onHooked(BlockHitResult hitResult, ItemStack itemStack) {
        super.onHooked(hitResult, itemStack);
        if (LibUtils.getItemStackNbtNoCopy(itemStack).get("hooks") instanceof ListTag list) {
            list.forEach(tag -> {
                AbstractHookEntity hookEntity = BaseHookItem.getHookEntity(tag, level());
                if (hookEntity != null && hookEntity != this)
                    hookEntity.setHookState(HookState.POP);
            });
        }
    }

    public enum Variant implements StringRepresentable {
        RED(0, "red"),
        BLUE(1, "blue");

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
