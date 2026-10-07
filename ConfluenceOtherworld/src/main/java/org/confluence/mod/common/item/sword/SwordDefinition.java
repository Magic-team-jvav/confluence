package org.confluence.mod.common.item.sword;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.AttributeHolder;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/// 剑在运行时使用的不可变能力定义。
public record SwordDefinition(
        boolean canSweep,
        boolean specialSweep,
        boolean tooltipImage,
        List<Consumer<MutableComponent>> tooltips,
        @Nullable Projectile projectile
) {
    public static Builder builder() {
        return new Builder();
    }

    public record BuildResult(SwordDefinition definition, Item.Properties properties) {
        public @Nullable Projectile projectile() {
            return definition.projectile();
        }
    }

    /// 固定的剑气配置；实体保存与生成包沿用原有字段布局，不再写入物品组件。
    public record Projectile(
            float damageFactor,
            float baseSpeed,
            float acceleration,
            int existTicks,
            float gravity,
            int cooldown,
            ResourceLocation soundEvent,
            ResourceLocation projType,
            Optional<ITrackType> trackType,
            IGeneration generation,
            SwordProjectileAppearance appearance,
            List<SwordProjectileParticleEffect> particleEffects) {
        public static final Codec<Projectile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("damageFactor").forGetter(Projectile::damageFactor),
                Codec.FLOAT.fieldOf("baseSpeed").forGetter(Projectile::baseSpeed),
                Codec.FLOAT.fieldOf("acceleration").forGetter(Projectile::acceleration),
                Codec.INT.fieldOf("existTicks").forGetter(Projectile::existTicks),
                Codec.FLOAT.fieldOf("gravity").forGetter(Projectile::gravity),
                Codec.INT.fieldOf("cooldown").forGetter(Projectile::cooldown),
                ResourceLocation.CODEC.fieldOf("soundEvent").forGetter(Projectile::soundEvent),
                ResourceLocation.CODEC.fieldOf("projType").forGetter(Projectile::projType),
                ITrackType.TYPED_CODEC.optionalFieldOf("trackType").forGetter(Projectile::trackType),
                IGeneration.TYPED_CODEC.fieldOf("generation").forGetter(Projectile::generation),
                SwordProjectileAppearance.CODEC.fieldOf("appearance").forGetter(Projectile::appearance),
                SwordProjectileParticleEffect.CODEC.listOf().optionalFieldOf("particleEffects", List.of()).forGetter(Projectile::particleEffects)
        ).apply(instance, Projectile::new));

        public static final PortStreamCodec<ByteBuf, Projectile> STREAM_CODEC = PortByteBufCodecs.fromCodec(CODEC);

        public Projectile(float damageFactor, float baseSpeed, float acceleration, int existTicks, float gravity, int cooldown, ResourceLocation soundEvent, ResourceLocation projType, Optional<ITrackType> trackType, IGeneration generation, SwordProjectileAppearance appearance) {
            this(damageFactor, baseSpeed, acceleration, existTicks, gravity, cooldown, soundEvent, projType, trackType, generation, appearance, List.of());
        }

        public SoundEvent getSoundEvent() {
            return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
        }

        public float getVelocity(LivingEntity living) {
            AttributeInstance rangedVelocity = living.getAttribute(LibAttributes.getRangedVelocity());
            return rangedVelocity == null ? baseSpeed : baseSpeed * (float) rangedVelocity.getValue();
        }

        public int getCooldownTicks(LivingEntity living) {
            AttributeInstance attackSpeed = living.getAttribute(Attributes.ATTACK_SPEED);
            if (attackSpeed == null) return cooldown;
            return Math.max(cooldown - (int) (attackSpeed.getValue() / 3.0), 0);
        }
    }

    private record AttributeEntry(Holder<Attribute> attribute, PortAttributeModifier modifier) {}

    public static final class Builder {
        private boolean canSweep = true;
        private boolean specialSweep;
        private boolean tooltipImage;
        private boolean baseAttributes = true;
        private Projectile projectile;
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
                attribute(Attributes.SWEEPING_DAMAGE_RATIO, ratio, PortAttributeModifier.Operation.ADD_VALUE);
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

        public Builder projectile(Projectile projectile) {
            this.projectile = projectile;
            return this;
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
            if (tier == ModTiers.UNBREAKABLE) properties.unbreakable();
            properties.durability(tier.getUses()).component(ConfluenceMagicLib.MOD_RARITY, rarity);
            PortItemAttributeModifiers.Builder attributesBuilder = PortItemAttributeModifiers.builder();
            attributes.forEach(entry -> attributesBuilder.add(entry.attribute(), entry.modifier(), PortEquipmentSlotGroup.MAINHAND));
            if (baseAttributes) {
                attributesBuilder.add(LibAttributes.getAttackDamage(), new PortAttributeModifier(ModItems.BASE_ATTACK_DAMAGE_ID, rawDamage - 1, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
                attributesBuilder.add(Attributes.ATTACK_SPEED, new PortAttributeModifier(ModItems.BASE_ATTACK_SPEED_ID, rawSpeed - 4, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
            }
            properties.attributes(attributesBuilder.build());
            return new BuildResult(new SwordDefinition(canSweep, specialSweep, tooltipImage, tooltips, projectile), properties);
        }
    }
}
