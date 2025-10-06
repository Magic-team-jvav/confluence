package org.confluence.mod.common.item.drill;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.pickaxe_axe.PickaxeAxeItem;
import org.confluence.mod.util.ModUtils;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
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
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true; // 取消挥手
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "drill", 0, state -> PlayState.STOP)
                .triggerableAnim("rotate", RawAnimation.begin().thenLoop("rotate")));
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
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("drill/drax"))) {
                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick) {
                            //super.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay, partialTick);
                        }
                    };
                }
                return renderer;
            }
        });
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public double getBoneResetTime() {
        return 1000000000;
    }

    public static void drillAnimation(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getItemStack().getItem() instanceof DrillItem drillItem) {
            long id = GeoItem.getOrAssignId(event.getItemStack(), serverPlayer.serverLevel());
            if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
                drillItem.triggerAnim(event.getEntity(), id, "drill", "rotate");
            } else if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.ABORT) {
                drillItem.stopTriggeredAnim(event.getEntity(), id, "drill", "rotate");
            }
        }
    }
}
