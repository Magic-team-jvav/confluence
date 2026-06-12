package org.confluence.mod.common.item.armor;

import PortLib.extensions.net.minecraft.world.item.Item.PortItemExtension;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseArmorItem extends ArmorItem {
    private @Nullable List<Component> tooltips;
    private @Nullable String requiresModLoaded;
    private boolean golden;

    public BaseArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (tooltips != null) {
            tooltipComponents.addAll(tooltips);
        }
        if (requiresModLoaded != null && !LibUtils.isModLoaded(requiresModLoaded)) {
            tooltipComponents.add(Component.translatable("tooltip.terra_curio.requires_mod_loaded", requiresModLoaded));
        }
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return golden;
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
        private ImmutableMultimap.Builder<Attribute, AttributeModifier> vanillaAttributes = null;
        private boolean golden;

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

        public Builder attribute(Attribute attribute, double value, PortAttributeModifier.PortOperation operation) {
            if (vanillaAttributes == null) this.vanillaAttributes = ImmutableMultimap.builder();
            vanillaAttributes.put(attribute, new AttributeModifier(PortAttributeModifier.rl2uuid(asId()), asId().getPath(), value, operation.unwrap()));
            return this;
        }

        public Builder attribute(Holder<Attribute> attribute, double value, PortAttributeModifier.PortOperation operation) {
            return attribute(attribute.value(), value, operation);
        }

        public Builder meleeDamage(double value) {
            return attribute(LibAttributes.getAttackDamage(), value, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder rangedDamage(double value) {
            return attribute(LibAttributes.getRangedDamage(), value, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder magicDamage(double value) {
            return attribute(LibAttributes.getMagicDamage(), value, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder summonDamage(double value) {
            return attribute(LibAttributes.getSummonDamage(), value, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL);
        }

        public Builder fourClassesDamage(double value) {
            return meleeDamage(value).rangedDamage(value).magicDamage(value).summonDamage(value);
        }

        public Builder criticalChance(double value) {
            return attribute(LibAttributes.getCriticalChance(), value, PortAttributeModifier.PortOperation.ADD_VALUE);
        }

        public Builder setGolden() {
            this.golden = true;
            return this;
        }

        public BaseArmorItem build() {
            properties.stacksTo(1);
            if (durability > 0) {
                properties.durability(durability);
            } else {
                PortItemExtension.Properties.unbreakable(properties);
            }
            if (types != null) {
                PortItemExtension.Properties.component(properties, ModDataComponentTypes.ARMOR_BONUS, new PrimitiveValueComponent(types));
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
            if (vanillaAttributes != null) {
                Supplier<PortItemAttributeModifiers> supplier = item.defaultModifiers;
                item.defaultModifiers = Suppliers.memoize(() -> {
                    vanillaAttributes.addAll(supplier.get().modifiers());
                    return new ItemAttributeModifiers(vanillaAttributes.build(), true);
                });
            }
            if (lineCount > 0) {
                item.tooltips = TooltipItem.getTooltipsFromString(name, lineCount, ChatFormatting.GRAY);
            }
            item.requiresModLoaded = requiresModLoaded;
            item.golden = golden;
            return item;
        }
    }
}
