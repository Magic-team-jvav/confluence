package org.confluence.mod.common.item.lance;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.value.Constant;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DarkLanceItem extends CustomRarityItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DarkLanceItem() {
        super(new Properties().stacksTo(1), ModRarity.ORANGE);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (!entity.level().isClientSide && entity.level().getGameTime() - LibUtils.getItemStackNbt(stack).getLong("confluence:last_attack_time") > 15) {
            LibUtils.updateItemStackNbt(stack, tag -> tag.putLong("confluence:last_attack_time", entity.level().getGameTime()));
            triggerAnim(entity, GeoItem.getId(stack), "lance", "use");
        }
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && !entity.level().isClientSide && entity instanceof LivingEntity living) {
            long tickCount = entity.level().getGameTime() - LibUtils.getItemStackNbt(stack).getLong("confluence:last_attack_time");
            if (tickCount <= 15) {
                Vec3 viewVector = entity.getViewVector(1.0F);
                Vec3 position = entity.position().add(0, 1, 0);
                Vec3 startVec = position.add(viewVector.scale(-0.5));
                Vec3 endVec = position.add(viewVector.scale(getDistance(tickCount) * living.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) / -16));
                AABB boundingBox = new AABB(startVec, endVec);
                for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, boundingBox, entity1 -> entity1 != entity)) {
                    AABB aabb = victim.getBoundingBox().inflate(0.3);
                    if (aabb.clip(startVec, endVec).isPresent() && victim.hurt(ModDamageTypes.of(level, DamageTypes.STING, entity), 6.8F)) {
                        victim.addEffect(new MobEffectInstance(ModEffects.SHADOWFLAME, 300));
                        VectorUtils.knockBackA2B(entity, victim, 0.5, 0.1);
                    }
                }
            }
        }
    }

    private static final ObjectArrayList<Keyframe<MathValue>> keyframes = ObjectArrayList.of(
            new Keyframe<>(0.0, new Constant(0.0), new Constant(0.0), EasingType.LINEAR),
            new Keyframe<>(5.0, new Constant(0.0), new Constant(6.0), EasingType.EASE_OUT_BACK),
            new Keyframe<>(5.0, new Constant(6.0), new Constant(-16.0), EasingType.EASE_IN_EXPO),
            new Keyframe<>(5.0, new Constant(-16.0), new Constant(0.0), EasingType.LINEAR)
    );

    private static double getDistance(long tickCount) {
        double totalFrameTime = 0;
        Keyframe<MathValue> currentFrame = null;
        double startTick = tickCount;
        for (Keyframe<MathValue> frame : keyframes) {
            totalFrameTime += frame.length();
            if (totalFrameTime > tickCount) {
                currentFrame = frame;
                startTick = (tickCount - (totalFrameTime - frame.length()));
                break;
            }
        }
        if (currentFrame == null) currentFrame = keyframes.getLast();
        AnimationPoint point = new AnimationPoint(currentFrame, startTick, currentFrame.length(), currentFrame.startValue().get(), currentFrame.endValue().get());
        return point.keyFrame().easingType().apply(point);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "lance", state -> PlayState.STOP)
                .triggerableAnim("use", RawAnimation.begin().thenPlay("use")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<DarkLanceItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("lance/dark_lance"))) {
                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick) {}
                    };
                }
                return renderer;
            }
        });
    }
}
