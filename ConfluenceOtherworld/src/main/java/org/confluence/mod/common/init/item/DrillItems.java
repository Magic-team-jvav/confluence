package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.drill.DrillItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCAttributes;

import java.util.Map;
import java.util.function.Supplier;

public class DrillItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<DrillItem> DRAX = ITEMS.registerItem("drax", properties -> new DrillItem(ModTiers.HALLOWED, 35, 4, ModRarity.LIGHT_RED, Map.of(
            Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ModItems.BASE_ATTACK_KNOCKBACK_ID, -0.25, AttributeModifier.Operation.ADD_VALUE),
            TCAttributes.getCriticalChance(), new AttributeModifier(ModItems.BASE_CRITICAL_CHANCE_ID, 0.04, AttributeModifier.Operation.ADD_VALUE),
            Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(ModItems.BASE_BLOCK_INTERACTION_RANGE_ID, -1.0, AttributeModifier.Operation.ADD_VALUE)
    )));

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
