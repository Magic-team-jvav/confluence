package org.confluence.mod.common.item.sword;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.AttributeHolder;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/// 剑在运行时使用的不可变能力定义。
public record SwordDefinition(boolean canSweep, boolean specialSweep, boolean tooltipImage,
                              List<SwordBehavior> behaviors,
                              List<Consumer<MutableComponent>> tooltips) {
    public SwordDefinition {
        behaviors = List.copyOf(behaviors);
        tooltips = List.copyOf(tooltips);
    }

    public static Builder builder() {
        return new Builder();
    }

    public record BuildResult(SwordDefinition definition, Item.Properties properties) {}

    private record AttributeEntry(Holder<Attribute> attribute, PortAttributeModifier modifier) {}

    public static final class Builder {
        private boolean canSweep = true;
        private boolean specialSweep;
        private boolean tooltipImage;
        private boolean baseAttributes = true;
        private int modifierIndex;
        private final List<SwordBehavior> behaviors = new ArrayList<>();
        private final List<Consumer<MutableComponent>> tooltips = new ArrayList<>();
        private final List<AttributeEntry> attributes = new ArrayList<>();
        private final List<Consumer<Item.Properties>> propertyModifiers = new ArrayList<>();

        public Builder withoutSweep() {
            canSweep = false;
            return this;
        }

        public Builder specialSweep(float ratio) {
            specialSweep = true;
            if (ratio > 0.0F)
                attribute(PortAttributesExtension.sweepingDamageRatio(), ratio, PortAttributeModifier.Operation.ADD_VALUE);
            return this;
        }

        public Builder tooltipImage() {
            tooltipImage = true;
            return this;
        }

        public Builder withoutBaseAttributes() {
            baseAttributes = false;
            return this;
        }

        public Builder behavior(SwordBehavior behavior) {
            behaviors.add(behavior);
            return this;
        }

        public Builder projectile(SwordProjectileComponent projectile) {
            return properties(value -> value.component(ModDataComponentTypes.SWORD_PROJECTILE, projectile));
        }

        public Builder attribute(Holder<Attribute> attribute, float amount, PortAttributeModifier.Operation operation) {
            attributes.add(new AttributeEntry(attribute, new PortAttributeModifier(Confluence.asResource("sword.modifier." + modifierIndex++), amount, operation)));
            return this;
        }

        public Builder attribute(Attribute attribute, float amount, PortAttributeModifier.Operation operation) {
            return attribute(AttributeHolder.wrap(attribute), amount, operation);
        }

        public Builder tooltip() {
            return tooltip(value -> {});
        }

        public Builder tooltips(int count) {
            for (int index = 0; index < count; index++) tooltip();
            return this;
        }

        public Builder tooltip(Consumer<MutableComponent> modifier) {
            tooltips.add(modifier);
            return this;
        }

        public Builder properties(Consumer<Item.Properties> modifier) {
            propertyModifiers.add(modifier);
            return this;
        }

        public Builder unbreakable() {
            return properties(Item.Properties::unbreakable);
        }

        public BuildResult build(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
            Item.Properties properties = new Item.Properties();
            propertyModifiers.forEach(modifier -> modifier.accept(properties));
            properties.durability(tier.getUses()).component(ConfluenceMagicLib.MOD_RARITY, rarity);
            PortItemAttributeModifiers.Builder attributesBuilder = PortItemAttributeModifiers.builder();
            attributes.forEach(entry -> attributesBuilder.add(entry.attribute(), entry.modifier(), PortEquipmentSlotGroup.MAINHAND));
            if (baseAttributes) {
                attributesBuilder.add(LibAttributes.getAttackDamage(), new PortAttributeModifier(ModItems.BASE_ATTACK_DAMAGE_ID, rawDamage - 1, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
                attributesBuilder.add(Attributes.ATTACK_SPEED, new PortAttributeModifier(ModItems.BASE_ATTACK_SPEED_ID, rawSpeed - 4, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
            }
            properties.attributes(attributesBuilder.build());
            return new BuildResult(new SwordDefinition(canSweep, specialSweep, tooltipImage, behaviors, tooltips), properties);
        }
    }
}
