package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class JewelSquirrel extends Squirrel {
    private static final Variant[] JEWEL_VARIANTS = {
            Variant.AMETHYST, Variant.TOPAZ, Variant.SAPPHIRE,
            Variant.EMERALD, Variant.RUBY, Variant.AMBER, Variant.DIAMOND
    };

    public JewelSquirrel(EntityType<? extends JewelSquirrel> type, Level level) {
        super(type, level);
        if (!level.isClientSide) {
            setVariant(JEWEL_VARIANTS[random.nextInt(JEWEL_VARIANTS.length)]);
        }
    }
}
