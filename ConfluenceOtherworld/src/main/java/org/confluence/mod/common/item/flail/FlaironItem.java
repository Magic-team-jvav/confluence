package org.confluence.mod.common.item.flail;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>猪鲨链球物品</h1>
 * 右键：先切换 SPIN/投射模式，再根据新模式创建/推进连枷状态机。
 */
public class FlaironItem extends BaseFlailItem {
    private static final String TAG_PROJECTILE_MODE = "confluence:flairon_projectile";

    public FlaironItem(@NotNull FlailComponent component, @NotNull ModRarity rarity) {
        super(component, rarity);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                            @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.consume(stack);

        if (findExistingFlail(player) == null) {
            boolean current = isProjectileMode(stack);
            setProjectileMode(stack, !current);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isProjectileMode(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(TAG_PROJECTILE_MODE);
    }

    private void setProjectileMode(ItemStack stack, boolean mode) {
        stack.getOrCreateTag().putBoolean(TAG_PROJECTILE_MODE, mode);
    }

    @Override
    public float getLaunchDamageRatio(ItemStack stack) {
        return 33.0F / 67.0F;
    }

    @Override
    protected EntityType<?> getFlailEntityType(FlailComponent component) {
        return ModEntities.FLAIRON_FLAIL.get();
    }
}
