package org.confluence.mod.common.item.lance;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.ModUtils;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public abstract class AbstractLanceItem extends CustomRarityItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int attackDuration;
    private final ObjectArrayList<Keyframe<MathValue>> keyframes;

    public AbstractLanceItem(Properties properties, ModRarity rarity, int attackDuration, ObjectArrayList<Keyframe<MathValue>> keyframes) {
        super(properties.stacksTo(1), rarity);
        this.attackDuration = attackDuration;
        this.keyframes = keyframes;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity.level() instanceof ServerLevel serverLevel && entity.level().getGameTime() - LibUtils.getItemStackNbt(stack).getLong("confluence:last_attack_time") > attackDuration) {
            LibUtils.updateItemStackNbt(stack, tag -> tag.putLong("confluence:last_attack_time", entity.level().getGameTime()));
            triggerAnim(entity, GeoItem.getOrAssignId(stack, serverLevel), "lance", "use");
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
        if (isSelected && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity owner) {
            long tickCount = entity.level().getGameTime() - LibUtils.getItemStackNbt(stack).getLong("confluence:last_attack_time");
            if (tickCount <= attackDuration) {
                Vec3 viewVector = entity.getViewVector(1.0F);
                Vec3 position = entity.position().add(0, 1, 0);
                Vec3 startVec = position.add(viewVector.scale(-0.5));
                Vec3 endVec = position.add(viewVector.scale(getDistance(tickCount, owner)));
                AABB boundingBox = new AABB(startVec, endVec);
                for (Entity victim : level.getEntitiesOfClass(Entity.class, boundingBox, target -> canHitEntity(target, owner))) {
                    AABB aabb = victim.getBoundingBox().inflate(0.3);
                    if (aabb.clip(startVec, endVec).isPresent()) {
                        owner.setLastHurtMob(victim);
                        if (victim instanceof PartEntity<?> partEntity) {
                            victim = partEntity.getParent();
                        }
                        DamageSource damageSource = getDamageSource(serverLevel, owner);
                        onHitEntity(damageSource, entity, owner, victim);
                        EnchantmentHelper.doPostAttackEffects(serverLevel, victim, damageSource);
                    }
                }
            }
        }
    }

    protected abstract DamageSource getDamageSource(ServerLevel level, LivingEntity owner);

    protected abstract void onHitEntity(DamageSource damageSource, Entity entity, LivingEntity living, Entity victim);

    protected boolean canHitEntity(Entity target, LivingEntity owner) {
        return ModUtils.canHitEntity(target, owner);
    }

    protected double getDistance(long tickCount, LivingEntity owner) {
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
        return point.keyFrame().easingType().apply(point) * owner.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) * 1.5 / -16;
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
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("lance/" + BuiltInRegistries.ITEM.getKey(AbstractLanceItem.this).getPath()))) {
                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick) {}
                    };
                }
                return renderer;
            }
        });
    }
}
