package org.confluence.mod.common.item.sword;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;


/// 剑在运行时使用的不可变能力定义。
public record SwordDefinition(
        boolean canSweep,
        boolean specialSweep,
        boolean tooltipImage,
        List<Consumer<MutableComponent>> tooltips
) {
    public static Builder builder() {
        return new Builder();
    }

    public record BuildResult(SwordDefinition definition, Item.Properties properties, SwordProjectileComponent projectile) {}

    private record AttributeEntry(Holder<Attribute> attribute, AttributeModifier modifier) {}

    public static final class Builder {
        private boolean canSweep = true;
        private boolean specialSweep;
        private boolean tooltipImage;
        private boolean baseAttributes = true;
        private SwordProjectileComponent projectile;
        private int modifierIndex;
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
                attribute(Attributes.SWEEPING_DAMAGE_RATIO, ratio, AttributeModifier.Operation.ADD_VALUE);
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

        public Builder projectile(SwordProjectileComponent projectile) {
            this.projectile = projectile;
            return properties(value -> value.component(ModDataComponentTypes.SWORD_PROJECTILE, projectile));
        }

        public Builder attribute(Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation) {
            attributes.add(new AttributeEntry(attribute, new AttributeModifier(Confluence.asResource("sword.modifier." + modifierIndex++), amount, operation)));
            return this;
        }

        public Builder attribute(Attribute attribute, float amount, AttributeModifier.Operation operation) {
            return attribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), amount, operation);
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
            return properties(p -> p.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE));
        }

        public BuildResult build(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
            Item.Properties properties = new Item.Properties();
            propertyModifiers.forEach(modifier -> modifier.accept(properties));
            if (tier == ModTiers.UNBREAKABLE) properties.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE);
            properties.durability(tier.getUses()).component(ConfluenceMagicLib.MOD_RARITY, rarity);
            ItemAttributeModifiers.Builder attributesBuilder = ItemAttributeModifiers.builder();
            attributes.forEach(entry -> attributesBuilder.add(entry.attribute(), entry.modifier(), EquipmentSlotGroup.MAINHAND));
            if (baseAttributes) {
                attributesBuilder.add(LibAttributes.getAttackDamage(), new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, rawDamage - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                attributesBuilder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, rawSpeed - 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }
            properties.attributes(attributesBuilder.build());
            return new BuildResult(new SwordDefinition(canSweep, specialSweep, tooltipImage, tooltips), properties, projectile);
        }
    }
}
