package org.confluence.mod.common.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.CritterEntities;

public class JewelBunny extends Bunny {

    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.AMBER, 1)
            .add(Variant.AMETHYST, 1)
            .add(Variant.DIAMOND, 1)
            .add(Variant.EMERALD, 1)
            .add(Variant.RUBY, 1)
            .add(Variant.SAPPHIRE, 1)
            .add(Variant.TOPAZ, 1)
            .build();

    public JewelBunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
    }

    @Override
    protected void initializeSpawnVariant() {
        setBunnyVariant(SPAWN_VARIANTS.select(random));
    }

    /// 宝石兔的后代仍然属于宝石兔实体。
    ///
    /// 具体宝石变体会继续由实体出生初始化流程决定，不能退化成普通兔，
    /// 否则繁殖、命令或其他模组调用后代工厂时会丢失宝石兔专属行为。
    @Override
    public JewelBunny getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        JewelBunny child = CritterEntities.JEWEL_BUNNY.get().create(level);
        if (child != null) child.initializeSpawnVariant();
        return child;
    }
}
