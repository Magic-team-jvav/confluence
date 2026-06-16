package org.confluence.mod.common.effect.harmful;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.AttributeInstance.PortAttributeInstanceExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.util.OverworldUtils;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.UUID;

public class WaterCandleEffect extends PortMobEffect {
    private final UUID uuid;
    private final String name;

    public WaterCandleEffect(ResourceLocation id) {
        super(MobEffectCategory.HARMFUL, 0xa8d5e6);
        this.uuid = PortAttributeModifier.rl2uuid(id);
        this.name = id.getPath();
//        addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER.get(), uuid, name, AttributeModifier.Operation.ADDITION, amplifier -> (amplifier + 1) * 0.17);
        addAttributeModifier(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER.get(), uuid, name, AttributeModifier.Operation.ADDITION, amplifier -> (amplifier + 1) * 0.25);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        AttributeInstance instance = living.getAttributes().getInstance(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER);
        if (instance != null) {
            double amount = (amplifier + 1) * (living.getY() >= OverworldUtils.getSpaceY() ? 0.8 : 0.17);
            AttributeModifier modifier = instance.getModifier(uuid);
            if (modifier == null || modifier.getAmount() != amount) {
                PortAttributeInstanceExtension.addOrReplacePermanentModifier(instance, new AttributeModifier(uuid, name, amount, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
