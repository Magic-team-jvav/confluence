package org.confluence.mod.integration.terra_curio;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.attachment.AccessoriesAttachment;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrappedAccessoriesAttachment extends AccessoriesAttachment {
    private final AccessoriesAttachment original;
    private final Player player;

    public WrappedAccessoriesAttachment(Player player, AccessoriesAttachment original) {
        this.original = original;
        this.player = player;
    }

    @Override
    public void setToDefaultValue() {
        if (original == null) {
            super.setToDefaultValue();
        } else {
            original.setToDefaultValue();
        }
    }

    @Override
    public boolean hasPanicNecklace() {
        return original.hasPanicNecklace();
    }

    @Override
    public void increaseLavaImmuneTicks() {
        original.increaseLavaImmuneTicks();
    }

    @Override
    public boolean decreaseLavaImmuneTicks() {
        return original.decreaseLavaImmuneTicks();
    }

    @Override
    public void flushAbility(LivingEntity living) {
        original.flushAbility(living);
    }

    @Override
    public void compute(PrimitiveValueComponent component) {
        original.compute(component);
    }

    @Override
    public <T, V extends PrimitiveValue<T>> void putUnitIfPresent(ValueType<T, V> type) {
        original.putUnitIfPresent(type);
    }

    @Override
    public <T, V extends PrimitiveValue<T>> void combineValue(ValueType<T, V> type, V value) {
        original.combineValue(type, value);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return original.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        original.deserializeNBT(provider, nbt);
    }

    @Override
    public <T, V extends PrimitiveValue<T>> boolean contains(ValueType<T, V> type) {
        return original.contains(type) || ModArmorBonus.hasType(player, type);
    }

    @Override
    public <T, V extends PrimitiveValue<T>> T getValue(ValueType<T, V> type) {
        T called = original.getValue(type);
        T t = ModArmorBonus.getValue(player, type);
        if (called == null) return t;
        return type.combineRule().combine(called, t);
    }

    @Override
    public <T, V extends PrimitiveValue<T>> @Nullable V getPrimitiveValue(ValueType<T, V> type) {
        V called = original.getPrimitiveValue(type);
        V v = ModArmorBonus.getPrimitiveValue(player, type);
        if (called == null) return v;
        if (v == null) return called;
        return type.newInstance(type.combineRule().combineValue(called, v));
    }
}
