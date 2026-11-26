package org.confluence.mod.common.item.sword;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import org.confluence.lib.common.component.ModRarity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;


public class GeoSwordItem extends BaseSwordItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeoSwordItem(Tier tier, int rawDamage, float rawSpeed) {
        super(tier, rawDamage, rawSpeed);
    }

    public GeoSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
        super(tier, rarity, rawDamage, rawSpeed);
    }

    public GeoSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, SwordDefinition.Builder definition) {
        super(tier, rarity, rawDamage, rawSpeed, definition);
    }

    protected ResourceLocation textureId(ResourceLocation modelId) {
        return modelId;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<GeoSwordItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    ResourceLocation id = BuiltInRegistries.ITEM.getKey(GeoSwordItem.this);
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<GeoSwordItem>(id).withAltTexture(textureId(id)));
                }
                return renderer;
            }
        });
    }
}
