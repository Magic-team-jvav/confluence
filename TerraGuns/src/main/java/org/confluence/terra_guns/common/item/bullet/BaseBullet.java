package org.confluence.terra_guns.common.item.bullet;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.component.BulletPropertyComponent;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;
import org.confluence.terra_guns.common.init.TGDataComponents;

import java.util.List;

public class BaseBullet extends Item {
    private final BulletPropertyComponent component;
    protected String colorID = "";

    public BaseBullet(Properties properties, float damage, float velocity, float velocityMultiplier, float knockback, ModRarity rarity, int penetrate, boolean infinity) {
        super(properties);

        BulletPropertyComponent component = new BulletPropertyComponent(damage, velocity, velocityMultiplier, knockback, penetrate, rarity, infinity);
        properties.component(TGDataComponents.BULLET_PROPERTY_COMPONENT.get(), component);
        DataComponentMap.Builder components = properties.components;
        if ((Integer) components.map.get(DataComponents.MAX_STACK_SIZE) ==99 && TerraGuns.IS_CONFLUENCE_LOADED) properties.stacksTo(9999);

        this.components = Properties.COMPONENT_INTERNER.intern(components.build());
        this.component = component;
    }

    public void tick(BaseBulletEntity baseBulletEntity) {
    }

    public void onHitBlock(BaseBulletEntity bulletEntity, BlockHitResult result) {
        bulletEntity.discard();
    }

    public void onHitEntity(BaseBulletEntity bulletEntity, EntityHitResult result) {
        result.getEntity().hurt(bulletEntity.getDamageSource(), bulletEntity.damage);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.terra_guns.damage", component.damage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.terra_guns.knockback", component.knockback()).withStyle(ChatFormatting.GRAY));
    }

    public String colorID() {
        return colorID;
    }

    public static class EmptyBullet extends BaseBullet {
        public EmptyBullet(Properties properties) {
            super(properties, 0, 0, 0, 0, ModRarity.WHITE, 0, false);
        }

        void setColorID(String colorID) {
            this.colorID = colorID;
        }
    }
}
