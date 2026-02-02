package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.confluence.terra_curio.TerraCurio;

import java.util.function.Function;

public class UnitValue implements PrimitiveValue<Unit> {
    public static final UnitValue INSTANCE = new UnitValue();
    public static final Function<Unit, UnitValue> UNIT_2_VALUE = unit -> INSTANCE;
    public static final Codec<UnitValue> CODEC = Unit.CODEC.xmap(UNIT_2_VALUE, unitValue -> Unit.INSTANCE);
    public static final CombineRule<Unit, UnitValue> GET_SELF = CombineRule.register(new CombineRule<>() {
        private static final ResourceLocation id = TerraCurio.asResource("unit_get_self");

        @Override
        public Unit combine(Unit componentA, Unit componentB) {
            return componentA;
        }

        @Override
        public ResourceLocation id() {
            return id;
        }
    });

    @Override
    public Unit get() {
        return Unit.INSTANCE;
    }

    @Override
    public Codec<? extends UnitValue> codec() {
        return CODEC;
    }
}
