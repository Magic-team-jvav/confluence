package org.confluence.mod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.gun.definition.BulletBehavior;
import org.confluence.mod.common.item.gun.definition.BulletDefinition;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseBullet extends Item {
    private final BulletDefinition definition;
    private final BulletPropertyComponent component;
    protected String colorID = "";

    public BaseBullet(Properties properties, float damage, float velocity, float velocityMultiplier, float knockback, ModRarity rarity, int penetrate, boolean infinity) {
        this(properties, new BulletDefinition(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity));
    }

    public BaseBullet(Properties properties, BulletDefinition definition) {
        super(setup(properties, definition));
        this.definition = definition;
        this.component = definition.component();
    }

    public BaseBullet(Properties properties, BulletDefinition definition, BulletBehavior behavior) {
        this(properties, definition.withBehavior(behavior));
    }

    private static Properties setup(Properties properties, BulletDefinition definition) {
        properties.component(ModDataComponentTypes.BULLET_PROPERTY, definition.component());
        Object maxStackSize = properties.components == null ? null : properties.components.map.get(DataComponents.MAX_STACK_SIZE);
        if (maxStackSize instanceof Integer stackSize && stackSize == 99) properties.stacksTo(LibUtils.MAX_STACK_SIZE);
        return properties;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.confluence.ranged_damage", component.damage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.knockback", component.knockback()).withStyle(ChatFormatting.GRAY));
        String abilityTooltip = definition.behavior().tooltipKey();
        if (!abilityTooltip.isEmpty()) {
            tooltipComponents.add(Component.translatable(abilityTooltip).withStyle(ChatFormatting.AQUA));
        }
    }

    public @Nullable String colorID() {
        return colorID;
    }

    public BulletDefinition getDefinition() {
        return definition;
    }

    public BulletBehavior getBehavior() {
        return definition.behavior();
    }

    public BulletImpactEffect getImpactEffect() {
        return definition.impactEffect();
    }

    public static class Dummy extends BaseBullet {
        public Dummy(Properties properties) {
            super(properties, new BulletDefinition(0, 0, 1, 0, 0, ModRarity.WHITE, false));
        }

        void colorID(String colorID) {
            this.colorID = colorID;
        }
    }
}
