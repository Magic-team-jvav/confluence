package org.confluence.terra_furniture.common.block.func;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Collection;

public interface MulStateGetter<S extends Enum<S> & StringRepresentable> {
    EnumProperty<S> getContainer();

    default Collection<S> getEnumPropertyObjects() {
        return getContainer().getPossibleValues();
    }
}
