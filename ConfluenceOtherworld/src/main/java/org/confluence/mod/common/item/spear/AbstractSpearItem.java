package org.confluence.mod.common.item.spear;

import PortLib.extensions.java.util.List.PortListExtension;
import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import com.eliotlash.mclib.math.Constant;
import com.eliotlash.mclib.math.IValue;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.ReturnException;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.util.ModUtils;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.keyframe.AnimationPoint;
import software.bernie.geckolib.core.keyframe.Keyframe;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class AbstractSpearItem extends TooltipItem implements GeoItem {
    public static final String LAST_ATTACK_TIME_KEY = "confluence:last_attack_time";
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final int attackDuration;
    protected final int attackInterval;
    protected final List<Keyframe<IValue>> keyframes;
    private TooltipComponent component;
    /// 本次挥击已命中的实体 ID，挥击开始时清空
    private final IntSet struckEntities = new IntArraySet();

    /// @param attackDuration 攻击持续时间，值越大攻击时间越长
    /// @param attackInterval 攻击间隔，每造成两次伤害之间的时间
    /// @param keyframes      应用于长矛攻击的关键帧，建议匹配攻击持续时间
    public AbstractSpearItem(Properties properties, ModRarity rarity, int attackDuration, int attackInterval, List<Keyframe<IValue>> keyframes) {
        super(properties.stacksTo(1), rarity, collectTooltips(attackDuration, attackInterval));
        if (attackInterval < 1)
            throw new IllegalArgumentException("attackInterval must be greater than or equal to 1, currently is " + attackInterval);
        this.attackDuration = attackDuration;
        this.attackInterval = attackInterval;
        this.keyframes = keyframes;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private static List<Component> collectTooltips(int attackDuration, int attackInterval) {
        return List.of(
                Component.translatable("tooltip.confluence.attack_duration", attackDuration).withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.confluence.attack_interval", attackInterval).withStyle(ChatFormatting.GRAY)
        );
    }

    public int getAttackDuration() {
        return attackDuration;
    }

    public int getAttackInterval() {
        return attackInterval;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(component);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level && entity.level().getGameTime() - LibUtils.getItemStackNbtNoCopy(stack).getLong(LAST_ATTACK_TIME_KEY) > attackDuration) {
            struckEntities.clear();
            LibUtils.updateItemStackNbt(stack, tag -> tag.putLong(LAST_ATTACK_TIME_KEY, entity.level().getGameTime()));
            triggerAnim(entity, GeoItem.getOrAssignId(stack, level), "spear", "use");
            onStartSting(stack, level, entity);
        }
        return true;
    }

    protected void onStartSting(ItemStack stack, ServerLevel level, LivingEntity owner) {}

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
        if (isSelected && entity instanceof ServerPlayer owner) {
            long gameTime = owner.level().getGameTime();
            CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);
            long tickCount = gameTime - tag.getLong(LAST_ATTACK_TIME_KEY);
            if (tickCount <= attackDuration && (attackInterval <= 1 || gameTime % attackInterval == 0)) {
                Vec3 viewVector = owner.getViewVector(1.0F);
                Vec3 position = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
                Vec3 startVec = position.add(viewVector.scale(-0.5));
                Vec3 endVec = position.add(viewVector.scale(getDistance(tickCount, owner)));

                try {
                    level.getEntities(owner, new AABB(startVec, endVec), target -> canHitEntity(target, owner)).stream()
                            .filter(v -> !struckEntities.contains(v.getId()))
                            .sorted(Comparator.comparingDouble(a -> a.distanceToSqr(owner)))
                            .forEachOrdered(victim -> {
                                if (victim.getBoundingBox().inflate(0.3).clip(startVec, endVec).isEmpty()) {
                                    return;
                                }
                                struckEntities.add(victim.getId());
                                owner.setLastHurtMob(victim);
                                victim = LibEntityUtils.tryFindBeImpacted(victim);
                                onHitEntity(stack, owner.serverLevel(), owner, victim);
                                throw new ReturnException();
                            });
                } catch (Exception ignored) {}
                onStingTick(stack, owner.serverLevel(), owner, endVec, attackDuration - tickCount < attackInterval);
            }
        }
    }

    protected abstract void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim);

    protected DamageSource getDamageSource(ServerLevel level, LivingEntity owner) {
        return ModDamageTypes.of(level, DamageTypes.STING, owner);
    }

    protected void onHitEntity(ItemStack stack, ServerLevel level, LivingEntity owner, Entity victim) {
        DamageSource damageSource = getDamageSource(level, owner);
        onHitEntity(damageSource, owner, victim);
        PortEnchantmentHelperExtension.doPostAttackEffects(level, victim, damageSource);
    }

    protected void onStingTick(ItemStack stack, ServerLevel level, LivingEntity owner, Vec3 tipPos, boolean last) {}

    protected boolean hurtVictim(DamageSource damageSource, LivingEntity owner, Entity victim) {
        return victim.hurt(damageSource, (float) owner.getAttributeValue(LibAttributes.getAttackDamage()));
    }

    protected boolean canHitEntity(Entity target, LivingEntity owner) {
        return LibEntityUtils.canHitEntity(target, owner);
    }

    protected double getDistance(long tickCount, LivingEntity owner) {
        double totalFrameTime = 0;
        Keyframe<IValue> currentFrame = null;
        double startTick = tickCount;
        for (Keyframe<IValue> frame : keyframes) {
            totalFrameTime += frame.length();
            if (totalFrameTime > tickCount) {
                currentFrame = frame;
                startTick = (tickCount - (totalFrameTime - frame.length()));
                break;
            }
        }
        if (currentFrame == null) currentFrame = PortListExtension.getLast(keyframes);
        AnimationPoint point = new AnimationPoint(currentFrame, startTick, currentFrame.length(), currentFrame.startValue().get(), currentFrame.endValue().get());
        return point.keyFrame().easingType().apply(point) * owner.getAttributeValue(PortAttributesExtension.entityInteractionRange()) / -16;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "spear", state -> PlayState.STOP)
                .triggerableAnim("use", RawAnimation.begin().thenPlay("use")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<AbstractSpearItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("spear/" + BuiltInRegistries.ITEM.getKey(AbstractSpearItem.this).getPath())));
                }
                return renderer;
            }
        });
    }

    public static PortItemAttributeModifiers attributes(float extraRange, float extraDamage) {
        return PortItemAttributeModifiers.builder()
                .add(PortAttributesExtension.entityInteractionRange(), new PortAttributeModifier(ModItems.BASE_ENTITY_INTERACTION_RANGE_ID, extraRange, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND)
                .add(LibAttributes.getAttackDamage(), new PortAttributeModifier(ModItems.BASE_ATTACK_DAMAGE_ID, extraDamage, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static List<Keyframe<IValue>> createKeyframes(K k, K... ks) {
        List<Keyframe<IValue>> keyframes = new LinkedList<>();
        keyframes.add(new Keyframe<>(0, new Constant(0), k.toValue(), k.easingType));
        for (K k1 : ks) {
            Keyframe<IValue> last = PortListExtension.getLast(keyframes);
            keyframes.add(new Keyframe<>(k1.toTick() - last.endValue().get(), last.endValue(), k1.toValue(), k.easingType));
        }
        return new ObjectArrayList<>(keyframes);
    }

    public record K(double atTime, double zOffset, EasingType easingType) {
        public double toTick() {
            return atTime * 20;
        }

        public IValue toValue() {
            return new Constant(zOffset);
        }

        public static K of(double atTime, double zOffset, EasingType easingType) {
            return new K(atTime, zOffset, easingType);
        }
    }
}
