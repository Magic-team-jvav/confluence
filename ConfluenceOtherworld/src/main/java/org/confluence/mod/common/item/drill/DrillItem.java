package org.confluence.mod.common.item.drill;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.item.pickaxe_axe.PickaxeAxeItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DrillItem extends PickaxeAxeItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DrillItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), builder -> {}, rarity);
    }

    public DrillItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, properties, builder -> {}, rarity);
    }

    public DrillItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, properties, consumer, rarity);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true; // 取消挥手
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        RawAnimation work = RawAnimation.begin().thenLoop("work");
        controllers.add(new AnimationController<>(this, "drill", 0, state -> state.setAndContinue(work)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<DrillItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(BuiltInRegistries.ITEM.getKey(DrillItem.this).withPrefix("drill/")));
                }
                return renderer;
            }
        });
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
