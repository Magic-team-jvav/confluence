package org.confluence.terra_curio.common.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class PrimitiveValueHolder implements INBTSerializable<CompoundTag> {
    protected final Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> valueMap = new HashMap<>();

    public PrimitiveValueHolder() {
        setToDefaultValue();
    }

    public void setToDefaultValue() {
        valueMap.clear();
    }

    public <T, V extends PrimitiveValue<T>> boolean contains(ValueType<T, V> type) {
        return valueMap.containsKey(type);
    }

    public <T, V extends PrimitiveValue<T>> T getValue(ValueType<T, V> type) {
        V value = getPrimitiveValue(type);
        return value == null ? type.defaultValue() : value.get();
    }

    public <T, V extends PrimitiveValue<T>> @Nullable V getPrimitiveValue(ValueType<T, V> type) {
        return (V) valueMap.get(type);
    }

    protected void flushAbility(LivingEntity living) {
        setToDefaultValue();
    }

    public void compute(PrimitiveValueComponent component) {
        for (Map.Entry<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> entry : component.types().entrySet()) {
            ValueType<?, ? extends PrimitiveValue<?>> type = entry.getKey();
            if (type.defaultValue() == Unit.INSTANCE) {
                putUnitIfPresent(type);
            } else {
                combineValue(type, tryCast(entry.getValue()));
            }
        }
    }

    protected <T, V extends PrimitiveValue<T>> void putUnitIfPresent(ValueType<T, V> type) {
        valueMap.put(type, UnitValue.INSTANCE);
    }

    protected <T, V extends PrimitiveValue<T>> void combineValue(ValueType<T, V> type, V value) {
        V other = (V) valueMap.get(type);
        if (other == null) {
            valueMap.put(type, value);
        } else {
            T t = value.combine(other, type.combineRule());
            valueMap.put(type, type.newInstance(t));
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = new ListTag();
        for (Map.Entry<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> entry : valueMap.entrySet()) {
            PrimitiveValue<?> value = entry.getValue();
            value.codec().encodeStart(NbtOps.INSTANCE, tryCast(value)).result().ifPresent(tag -> {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.put(entry.getKey().key().toString(), tag);
                listTag.add(compoundTag);
            });
        }
        if (!listTag.isEmpty()) {
            nbt.put("valueMap", listTag);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.valueMap.clear();
        ListTag listTag = nbt.getList("valueMap", Tag.TAG_COMPOUND);
        for (Tag tag : listTag) {
            CompoundTag compoundTag = (CompoundTag) tag;
            String key = compoundTag.getAllKeys().stream().findFirst().orElse(null);
            if (key == null) continue;
            ValueType<?, ? extends PrimitiveValue<?>> type = ValueType.TYPES.get(ResourceLocation.tryParse(key));
            if (type == null) continue;
            Codec<PrimitiveValue<?>> codec = ValueType.VALUE_CODECS.get(type);
            if (codec == null) continue;
            RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
            codec.parse(ops, compoundTag.get(key)).result().ifPresent(value -> valueMap.put(type, value));
        }
    }

    @ApiStatus.Internal
    public static <T, V extends PrimitiveValue<T>> V tryCast(PrimitiveValue<?> primitiveValue) {
        return (V) primitiveValue;
    }
}
