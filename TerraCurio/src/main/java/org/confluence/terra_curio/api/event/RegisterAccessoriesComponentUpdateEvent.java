package org.confluence.terra_curio.api.event;

import net.minecraft.util.Unit;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;

import java.util.Set;

public abstract class RegisterAccessoriesComponentUpdateEvent extends Event implements IModBusEvent {
    public static class UnitType extends RegisterAccessoriesComponentUpdateEvent {
        private final Set<ValueType<Unit, UnitValue>> set;

        public UnitType(Set<ValueType<Unit, UnitValue>> set) {
            this.set = set;
        }

        public void register(ValueType<Unit, UnitValue> type) {
            set.add(type);
        }
    }

    public static class OtherType extends RegisterAccessoriesComponentUpdateEvent {
        private final Set<ValueType<?, ? extends PrimitiveValue<?>>> set;

        public OtherType(Set<ValueType<?, ? extends PrimitiveValue<?>>> set) {
            this.set = set;
        }

        public void register(ValueType<?, ? extends PrimitiveValue<?>> type) {
            set.add(type);
        }
    }
}
