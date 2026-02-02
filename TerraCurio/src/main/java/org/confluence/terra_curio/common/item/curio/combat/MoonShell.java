package org.confluence.terra_curio.common.item.curio.combat;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.item.curio.NightBonusCurioItem;
import org.confluence.terra_curio.util.CuriosUtils;
import top.theillusivec4.curios.api.SlotContext;

public class MoonShell extends NightBonusCurioItem implements ICosmetic {
    public static final ResourceLocation ID = TerraCurio.asResource("moon_shell");
    private static final ImmutableMultimap<Holder<Attribute>, AttributeModifier> UNDER_WATER = ImmutableMultimap.of(
            Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(ID, 0.8, AttributeModifier.Operation.ADD_VALUE)
    );

    public MoonShell(Builder builder) {
        super(0.1F, builder);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        return CuriosUtils.noSameCurio(living, this) && CuriosUtils.noSameCurio(living, ICosmetic.class);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living != null && living.isInWaterOrBubble()) {
            return UNDER_WATER;
        }
        return super.getAttributeModifiers(slotContext, id, stack);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.entity().isInWaterOrBubble()) {
            super.curioTick(slotContext, stack);
        }
    }
}
