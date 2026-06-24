package org.confluence.mod.common.item.sword;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.common.component.ModRarity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GeoSwordItem extends BaseSwordItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeoSwordItem(Tier tier, int rawDamage, float rawSpeed, Properties properties) {
        super(tier, rawDamage, rawSpeed, properties);
    }

    public GeoSwordItem(Tier tier, int rawDamage, float rawSpeed) {
        super(tier, rawDamage, rawSpeed);
    }

    public GeoSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
        super(tier, rarity, rawDamage, rawSpeed);
    }

    public GeoSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, ModifierBuilder modifier) {
        super(tier, rarity, rawDamage, rawSpeed, modifier);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<GeoSwordItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(BuiltInRegistries.ITEM.getKey(GeoSwordItem.this)));
                }
                return renderer;
            }
        });
    }
}
