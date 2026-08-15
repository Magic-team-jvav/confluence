package org.confluence.mod.common.entity.animal;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;

/**
 * 昆虫及简单小动物共用的被动逃生与日常巡游行为树。
 */
public class SimpleCritter extends BaseCritter {

    public SimpleCritter(EntityType<? extends SimpleCritter> type, Level level) {
        super(type, level);
        getAttribute(PortAttributesExtension
                .stepHeight()
                .value()).setBaseValue(0.3);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createInsectAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(
                        createGroundCritterRoutine(1.0),
                        1.5);
            }
        };
    }

    /**
     * 此类承载的蜗牛、幼虫、蛆虫和蝎子在 1.21 中使用零摔落伤害倍率。
     */
    @Override
    public boolean causeFallDamage(
            float fallDistance,
            float multiplier,
            DamageSource source) {
        return false;
    }
}
