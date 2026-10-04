package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JewelSquirrel extends Squirrel {
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.AMBER, 1)
            .add(Variant.AMETHYST, 1)
            .add(Variant.DIAMOND, 1)
            .add(Variant.EMERALD, 1)
            .add(Variant.RUBY, 1)
            .add(Variant.SAPPHIRE, 1)
            .add(Variant.TOPAZ, 1)
            .build();

    public JewelSquirrel(EntityType<? extends JewelSquirrel> type, Level level) {
        super(type, level);
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(SPAWN_VARIANTS.select(random));
    }
}
