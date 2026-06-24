package org.confluence.mod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.BulletPropertyComponent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseBullet extends Item {
    private final BulletPropertyComponent component;
    protected String colorID = "";

    public BaseBullet(Properties properties, float damage, float velocity, float velocityMultiplier, float knockback, ModRarity rarity, int penetrate, boolean infinity) {
        super(setup(properties, damage, velocity, velocityMultiplier, knockback, rarity, penetrate, infinity));
        this.component = properties.getComponent(ModDataComponentTypes.BULLET_PROPERTY);
    }

    private static Properties setup(Properties properties, float damage, float velocity, float velocityMultiplier, float knockback, ModRarity rarity, int penetrate, boolean infinity) {
        BulletPropertyComponent component = new BulletPropertyComponent(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity);
        properties.component(ModDataComponentTypes.BULLET_PROPERTY, component);
        return properties.stacksTo(9999);
    }

    public void tick(BaseBulletEntity baseBulletEntity) {}

    public void onHitBlock(BaseBulletEntity bulletEntity, BlockHitResult result) {
        bulletEntity.discard();
    }

    public void onHitEntity(BaseBulletEntity bulletEntity, EntityHitResult result) {
        result.getEntity().hurt(bulletEntity.getDamageSource(), bulletEntity.damage);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.terra_guns.damage", component.damage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.terra_guns.knockback", component.knockback()).withStyle(ChatFormatting.GRAY));
    }

    public @Nullable String colorID() {
        return colorID;
    }

    public static class EmptyBullet extends BaseBullet {
        public EmptyBullet(Properties properties) {
            super(properties, 0, 0, 0, 0, ModRarity.WHITE, 0, false);
        }

        void colorID(String colorID) {
            this.colorID = colorID;
        }
    }
}
