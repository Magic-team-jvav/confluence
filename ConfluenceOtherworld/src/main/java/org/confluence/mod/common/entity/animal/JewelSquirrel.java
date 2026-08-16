package org.confluence.mod.common.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.CritterEntities;

public class JewelSquirrel extends Squirrel {
    private static final Variant[] JEWEL_VARIANTS = {
            Variant.AMBER, Variant.AMETHYST, Variant.DIAMOND,
            Variant.EMERALD, Variant.GOLD, Variant.RUBY,
            Variant.SAPPHIRE, Variant.TOPAZ
    };

    public JewelSquirrel(EntityType<? extends JewelSquirrel> type, Level level) {
        super(type, level);
        if (!level.isClientSide) {
            setVariant(JEWEL_VARIANTS[random.nextInt(JEWEL_VARIANTS.length)]);
        }
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(CritterVariantUtil.uniform(random, JEWEL_VARIANTS));
    }

    /// 宝石松鼠的后代必须保留宝石松鼠实体类型。
    ///
    /// <p>这里显式覆盖普通松鼠的后代工厂，避免附属模组或运行时繁殖调用
    /// 把宝石松鼠错误地转换成普通松鼠。</p>
    @Override
    public JewelSquirrel getBreedOffspring(
            ServerLevel level,
            AgeableMob otherParent) {
        return CritterEntities.JEWEL_SQUIRREL.get().create(level);
    }
}
