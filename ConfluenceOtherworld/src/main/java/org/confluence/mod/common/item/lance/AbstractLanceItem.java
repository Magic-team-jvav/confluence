package org.confluence.mod.common.item.lance;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.ModUtils;
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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractLanceItem extends CustomRarityItem implements GeoItem {
    public static final String LAST_ATTACK_TIME_KEY = "confluence:last_attack_time";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int attackDuration;
    private final int attackInterval;
    private final List<Keyframe<MathValue>> keyframes;

    /**
     * @param attackDuration 攻击持续时间，值越大攻击时间越长
     * @param attackInterval 攻击间隔，每造成两次伤害之间的时间
     * @param keyframes      应用于长矛攻击的关键帧，建议匹配攻击持续时间
     */
    public AbstractLanceItem(Properties properties, ModRarity rarity, int attackDuration, int attackInterval, List<Keyframe<MathValue>> keyframes) {
        super(properties.stacksTo(1), rarity);
        if (attackInterval < 1) throw new IllegalArgumentException("attackInterval must be greater than or equal to 1, currently is " + attackInterval);
        this.attackDuration = attackDuration;
        this.attackInterval = attackInterval;
        this.keyframes = keyframes;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public int getAttackDuration() {
        return attackDuration;
    }

    public int getAttackInterval() {
        return attackInterval;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity.level() instanceof ServerLevel level && entity.level().getGameTime() - LibUtils.getItemStackNbtNoCopy(stack).getLong(LAST_ATTACK_TIME_KEY) > attackDuration) {
            LibUtils.updateItemStackNbt(stack, tag -> tag.putLong(LAST_ATTACK_TIME_KEY, entity.level().getGameTime()));
            triggerAnim(entity, GeoItem.getOrAssignId(stack, level), "lance", "use");
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
            long gameTime = entity.level().getGameTime();
            long tickCount = gameTime - LibUtils.getItemStackNbtNoCopy(stack).getLong(LAST_ATTACK_TIME_KEY);
            if (tickCount <= attackDuration && (attackInterval <= 1 || gameTime % attackInterval == 0)) {
                Vec3 viewVector = entity.getViewVector(1.0F);
                Vec3 position = entity.position().add(0, 1, 0);
                Vec3 startVec = position.add(viewVector.scale(-0.5));
                Vec3 endVec = position.add(viewVector.scale(getDistance(tickCount, owner)));
                AABB boundingBox = new AABB(startVec, endVec);

                for (Entity victim : level.getEntities(entity, boundingBox, target -> canHitEntity(target, owner))) {
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
        return point.keyFrame().easingType().apply(point) * owner.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) / -16;
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
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("lance/" + BuiltInRegistries.ITEM.getKey(AbstractLanceItem.this).getPath())));
                }
                return renderer;
            }
        });
    }

    public static ItemAttributeModifiers entityInteractionRange(float extra) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ModItems.BASE_ENTITY_INTERACTION_RANGE_ID, extra, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static List<Keyframe<MathValue>> createKeyframes(K k, K... ks) {
        List<Keyframe<MathValue>> keyframes = new LinkedList<>();
        keyframes.add(new Keyframe<>(0, new Constant(0), k.toValue(), k.easingType));
        for (K k1 : ks) {
            Keyframe<MathValue> last = keyframes.getLast();
            keyframes.add(new Keyframe<>(k1.toTick() - last.endValue().get(), last.endValue(), k1.toValue(), k.easingType));
        }
        return new ObjectArrayList<>(keyframes);
    }

    public record K(double atTime, double zOffset, EasingType easingType) {
        public double toTick() {
            return atTime * 20;
        }

        public MathValue toValue() {
            return new Constant(zOffset);
        }

        public static K of(double atTime, double zOffset, EasingType easingType) {
            return new K(atTime, zOffset, easingType);
        }
    }
}
