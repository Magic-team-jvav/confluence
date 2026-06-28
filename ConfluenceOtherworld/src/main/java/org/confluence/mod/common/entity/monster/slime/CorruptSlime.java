package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;

/**
 * 腐化史莱姆 —— 死亡时分裂出 1-3 只小史莱姆灵（Slimeling）。
 */
public class CorruptSlime extends BaseSlime {
    private static final ResourceLocation SLIMELING_ID = Confluence.asResource("slimeling");

    public CorruptSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xC91717, false, 40);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(28.0f, 20, 170.0f, 40);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && !isRemoved()) {
            EntityType<?> slimelingType = BuiltInRegistries.ENTITY_TYPE.get(SLIMELING_ID);
            if (slimelingType == null) return;
            int count = 1 + random.nextInt(3);
            for (int i = 0; i < count; i++) {
                Slimeling baby = (Slimeling) slimelingType.create(level());
                if (baby != null) {
                    baby.setPos(getX() + random.nextGaussian() * 0.5,
                            getY(),
                            getZ() + random.nextGaussian() * 0.5);
                    level().addFreshEntity(baby);
                }
            }
        }
    }
}
