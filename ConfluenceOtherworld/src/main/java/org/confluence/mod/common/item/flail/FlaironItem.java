package org.confluence.mod.common.item.flail;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * <h1>猪鲨链球物品</h1>
 * 右键：先切换 SPIN/投射模式，再根据新模式创建/推进连枷状态机。
 */
public class FlaironItem extends BaseFlailItem {
    private static final String TAG_PROJECTILE_MODE = "confluence:flairon_projectile";

    public FlaironItem(@NotNull FlailComponent component, @NotNull ModRarity rarity,
                       @NotNull Supplier<FlailStrategy> strategySupplier) {
        super(component, rarity, strategySupplier);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                            @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.consume(stack);

        if (findExistingFlail(player) == null) {
            // 无活跃连枷：切换模式
            boolean current = isProjectileMode(stack);
            setProjectileMode(stack, !current);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isProjectileMode(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY)
                .copyTag().getBoolean(TAG_PROJECTILE_MODE);
    }

    /** 设置投射模式并同步更新面板伤害属性 */
    private void setProjectileMode(ItemStack stack, boolean mode) {
        stack.update(DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY,
                data -> data.update(tag -> tag.putBoolean(TAG_PROJECTILE_MODE, mode)));
        syncDamageAttribute(stack, mode);
    }

    /** 根据当前模式更新 ItemStack 的 ATTACK_DAMAGE 属性修饰器 */
    private void syncDamageAttribute(ItemStack stack, boolean projectileMode) {
        FlailComponent comp = getComponent();
        float effectiveDamage = projectileMode
                ? comp.damageFactor * comp.launchDamageRatio
                : comp.damageFactor;

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                                effectiveDamage - 1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                comp.spinSpeed - 4, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build());
    }
}