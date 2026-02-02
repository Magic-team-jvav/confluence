package org.confluence.terra_curio.common.item.curio;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.terra_curio.common.init.TCEffects;
import top.theillusivec4.curios.api.SlotContext;

public class NightBonusCurioItem extends BaseCurioItem {
    private final float healPerSecond;

    public NightBonusCurioItem(float healPerSecond, Builder builder) {
        super(builder);
        this.healPerSecond = healPerSecond;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        return living != null && LibDateUtils.isNight(living.level()) ? super.getAttributeModifiers(slotContext, id, stack) : EMPTY_ATTRIBUTE;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (LibDateUtils.isNight(living.level())) {
            TCEffects.healPerSecond(living, healPerSecond);
        }
    }
}
