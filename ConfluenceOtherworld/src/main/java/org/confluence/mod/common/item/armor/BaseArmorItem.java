package org.confluence.mod.common.item.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.fml.ModList;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.TEAttributes;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BaseArmorItem extends ArmorItem {
    private @Nullable List<Component> tooltips;
    private @Nullable String requiresModLoaded;

    public BaseArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (tooltips != null) {
            tooltipComponents.addAll(tooltips);
        }
        if (requiresModLoaded != null && !ModList.get().isLoaded(requiresModLoaded)) {
            tooltipComponents.add(Component.translatable("tooltip.terra_curio.requires_mod_loaded", requiresModLoaded));
        }
    }

    public static Builder builder(String name, Holder<ArmorMaterial> material, Type type) {
        return new Builder(name, material, type);
    }

    public static class Builder {
        private final String name;
        private final Holder<ArmorMaterial> material;
        private final ArmorItem.Type type;
        private final Properties properties = new Properties();
        private String geoName = null;
        private ModRarity rarity = ModRarity.WHITE;
        private int lineCount = 0;
        private int durability = 0;
        private Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> types = null;
        private boolean multiHead = false;
        private String requiresModLoaded = null;
        private ImmutableList.Builder<ItemAttributeModifiers.Entry> vanillaAttributes = null;

        private transient ResourceLocation id;

        public Builder(String name, Holder<ArmorMaterial> material, Type type) {
            this.name = name;
            this.material = material;
            this.type = type;
        }

        public ResourceLocation asId() {
            if (id == null) {
                this.id = Confluence.asResource(name);
            }
            return id;
        }

        public Builder properties(Consumer<Properties> consumer) {
            consumer.accept(properties);
            return this;
        }

        public Builder geo(String geoName) {
            this.geoName = geoName;
            return this;
        }

        public Builder geo(String geoName, boolean multiHead) {
            this.geoName = geoName;
            this.multiHead = multiHead;
            return this;
        }

        public Builder rarity(ModRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder tooltips(int lineCount) {
            this.lineCount = lineCount;
            return this;
        }

        public Builder durability(int value) {
            this.durability = value;
            return this;
        }

        public Builder armorBonus(PrimitiveValueComponent component) {
            if (types == null) this.types = new HashMap<>();
            types.putAll(component.types());
            return this;
        }

        public Builder additionalMana(int value) {
            return armorBonus(PrimitiveValueComponent.of(AccessoryItems.ADDITIONAL$MANA, value));
        }

        public Builder requiresModLoaded(String modid) {
            this.requiresModLoaded = modid;
            return this;
        }

        public Builder attribute(Holder<Attribute> attribute, double value, AttributeModifier.Operation operation) {
            if (vanillaAttributes == null) this.vanillaAttributes = ImmutableList.builder();
            vanillaAttributes.add(new ItemAttributeModifiers.Entry(attribute, new AttributeModifier(asId(), value, operation), EquipmentSlotGroup.bySlot(type.getSlot())));
            return this;
        }

        public Builder meleeDamage(double value) {
            return attribute(Attributes.ATTACK_DAMAGE, value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder rangedDamage(double value) {
            return attribute(TCAttributes.getRangedDamage(), value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder magicDamage(double value) {
            return attribute(TCAttributes.getMagicDamage(), value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder summonDamage(double value) {
            return attribute(TEAttributes.SUMMON_DAMAGE, value, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder fourClassesDamage(double value) {
            return meleeDamage(value).rangedDamage(value).magicDamage(value).summonDamage(value);
        }

        public Builder criticalChance(double value) {
            return attribute(TCAttributes.getCriticalChance(), value, AttributeModifier.Operation.ADD_VALUE);
        }

        public BaseArmorItem build() {
            properties.stacksTo(1);
            if (durability > 0) {
                properties.durability(durability);
            } else {
                properties.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE);
            }
            if (types != null) {
                properties.component(ModDataComponentTypes.ARMOR_BONUS, new PrimitiveValueComponent(types));
            }
            if (vanillaAttributes != null) {
                properties.attributes(new ItemAttributeModifiers(vanillaAttributes.build(), true));
            }

            BaseArmorItem item;
            if (geoName != null) {
                if (multiHead) {
                    item = new MultiHeadGeoArmorItem(geoName, rarity, material, type, properties);
                } else {
                    item = new NormalGeoArmorItem(geoName, rarity, material, type, properties);
                }
            } else {
                item = new BaseArmorItem(material, type, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
            }
            if (lineCount > 0) {
                item.tooltips = TooltipItem.getTooltipsFromString(name, lineCount, ChatFormatting.GRAY);
            }
            item.requiresModLoaded = requiresModLoaded;
            return item;
        }
    }
}
