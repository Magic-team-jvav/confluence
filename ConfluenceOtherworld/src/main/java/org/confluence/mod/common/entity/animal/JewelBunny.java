package org.confluence.mod.common.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.CritterEntities;

public class JewelBunny extends Bunny {

    private static final Variant[] JEWEL_VARIANTS = {
            Variant.AMBER, Variant.AMETHYST, Variant.DIAMOND,
            Variant.EMERALD, Variant.GOLD, Variant.RUBY,
            Variant.SAPPHIRE, Variant.TOPAZ
    };

    public JewelBunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
        if (!level.isClientSide) {
            setVariant(JEWEL_VARIANTS[random.nextInt(JEWEL_VARIANTS.length)]);
        }
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(CritterVariantUtil.uniform(random, JEWEL_VARIANTS));
    }

    /// 宝石兔的后代仍然属于宝石兔实体。
    ///
    /// <p>具体宝石变体会继续由实体出生初始化流程决定，不能退化成普通兔，
    /// 否则繁殖、命令或其他模组调用后代工厂时会丢失宝石兔专属行为。</p>
    @Override
    public JewelBunny getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return CritterEntities.JEWEL_BUNNY.get().create(level);
    }
}
