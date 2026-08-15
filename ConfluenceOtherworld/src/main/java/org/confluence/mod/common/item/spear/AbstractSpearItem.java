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
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
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
import org.confluence.lib.api.projectile.*;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModArmPoses;
import org.confluence.mod.common.component.SpearProjectileComponent;
import org.confluence.mod.common.entity.projectile.spear.SpearProjectile;
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
    /**
     * 当前格式动作版本；1.20.1 不读取或迁移任何旧命中集合。
     */
    public static final String SPEAR_ACTION_VERSION_KEY = "confluence:spear_action_version";
    public static final String SPEAR_ACTION_HITS_KEY = "confluence:spear_action_hits";
    private static final int SPEAR_ACTION_VERSION = 1;
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final int attackDuration;
    protected final int attackInterval;
    protected final List<Keyframe<IValue>> keyframes;
    private TooltipComponent component;

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
            LibUtils.updateItemStackNbt(stack, tag -> {
                tag.putInt(SPEAR_ACTION_VERSION_KEY, SPEAR_ACTION_VERSION);
                tag.putIntArray(SPEAR_ACTION_HITS_KEY, new int[0]);
                tag.putLong(LAST_ATTACK_TIME_KEY, entity.level().getGameTime());
            });
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
                IntSet struckEntities = readActionHits(tag);
                Vec3 viewVector = owner.getViewVector(1.0F);
                Vec3 position = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
                Vec3 startVec = position.add(viewVector.scale(-0.5));
                Vec3 endVec = position.add(viewVector.scale(getDistance(tickCount, owner)));
                /*
                 * 粗筛必须覆盖后续精确射线允许的 0.3 格命中容差，否则目标只要
                 * 略微高于或低于零厚度射线，就会在执行扩张碰撞箱裁剪前被遗漏。
                 */
                AABB searchBox = new AABB(startVec, endVec).inflate(0.3);

                level.getEntities(owner, searchBox, target -> canHitEntity(target, owner)).stream()
                        .filter(victim -> !struckEntities.contains(victim.getId()))
                        .filter(victim -> victim.getBoundingBox().inflate(0.3)
                                .clip(startVec, endVec).isPresent())
                        .min(Comparator.comparingDouble(victim -> victim.distanceToSqr(owner)))
                        .ifPresent(victim -> {
                            struckEntities.add(victim.getId());
                            writeActionHits(stack, struckEntities);
                            owner.setLastHurtMob(victim);
                            Entity impacted = LibEntityUtils.tryFindBeImpacted(victim);
                            onHitEntity(stack, owner.serverLevel(), owner, impacted);
                        });
                onStingTick(stack, owner.serverLevel(), owner, endVec, attackDuration - tickCount < attackInterval);
            }
        }
    }

    /**
     * 读取当前挥击的命中集合；版本缺失或不符时直接创建全新当前格式。
     */
    private static IntSet readActionHits(CompoundTag tag) {
        if (tag.getInt(SPEAR_ACTION_VERSION_KEY) != SPEAR_ACTION_VERSION) {
            tag.putInt(SPEAR_ACTION_VERSION_KEY, SPEAR_ACTION_VERSION);
            tag.putIntArray(SPEAR_ACTION_HITS_KEY, new int[0]);
        }
        return new IntArraySet(tag.getIntArray(SPEAR_ACTION_HITS_KEY));
    }

    /**
     * 把短生命周期命中集合写回当前武器栈，彻底隔离不同玩家和不同长矛。
     */
    private static void writeActionHits(ItemStack stack, IntSet hits) {
        LibUtils.updateItemStackNbt(stack, tag -> {
            tag.putInt(SPEAR_ACTION_VERSION_KEY, SPEAR_ACTION_VERSION);
            tag.putIntArray(SPEAR_ACTION_HITS_KEY, hits.toIntArray());
        });
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

    /**
     * 通过统一 MELEE 事务生成一枚长矛衍生弹幕。
     *
     * <p>伤害在动作创建时冻结为当前攻击伤害乘组件系数；组件只配置运动、寿命、穿透和特效。
     * 零速孢子使用零 launch 倍率静止生成，仍携带完整战斗快照。</p>
     */
    protected final <P extends SpearProjectile> ProjectileFireResult fireDerivedProjectile(
            ItemStack weapon,
            ServerLevel level,
            LivingEntity owner,
            SpearProjectileComponent component,
            P projectile,
            Vec3 position,
            Vec3 direction,
            float baseKnockback,
            Consumer<P> configurator
    ) {
        if (!(owner instanceof ServerPlayer player)) {
            return ProjectileFireResult.PLAYER_UNAVAILABLE;
        }
        if (component == null || projectile == null || position == null || direction == null
                || configurator == null) {
            throw new IllegalArgumentException("Spear projectile action arguments must not be null");
        }
        if (!Float.isFinite(baseKnockback) || baseKnockback < 0.0F) {
            throw new IllegalArgumentException("Spear projectile knockback must be finite and non-negative");
        }

        projectile.setWeapon(weapon);
        projectile.setProjComponent(component, owner);
        projectile.fire(direction, component.baseSpeed(), baseKnockback);
        configurator.accept(projectile);
        float actionVelocity = component.baseSpeed() > 0.0F ? component.baseSpeed() : 1.0F;
        float velocityMultiplier = component.baseSpeed() > 0.0F ? 1.0F : 0.0F;
        float actionDamage = (float) owner.getAttributeValue(LibAttributes.getAttackDamage())
                * component.damageFactor();
        ProjectileFireAction action = ProjectileFireAction.builder(
                        ProjectileDamageChannel.MELEE,
                        ProjectileCost.none(),
                        (context, snapshot) -> List.of(new ProjectileLaunch(
                                projectile, position, direction, velocityMultiplier)))
                .baseDamage(actionDamage)
                .baseVelocity(actionVelocity)
                .baseKnockback(baseKnockback)
                .triggers(ProjectileFireTrigger.MELEE_ATTACK_TICK)
                .build();
        return ServerProjectileFireService.fire(
                player,
                InteractionHand.MAIN_HAND,
                ProjectileFireTrigger.MELEE_ATTACK_TICK,
                action);
    }

    /**
     * 无额外实体配置时使用的简化重载。
     */
    protected final <P extends SpearProjectile> ProjectileFireResult fireDerivedProjectile(
            ItemStack weapon,
            ServerLevel level,
            LivingEntity owner,
            SpearProjectileComponent component,
            P projectile,
            Vec3 position,
            Vec3 direction,
            float baseKnockback
    ) {
        return fireDerivedProjectile(
                weapon, level, owner, component, projectile, position, direction,
                baseKnockback, ignored -> {});
    }

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
            private Boolean hasGeoModel;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                /*
                 * 部分矛目前只有普通 item model，没有 geo/item/spear/<id>.geo.json。
                 * 这种情况下不能强行返回 GeoItemRenderer，否则第一/第三人称会因为模型资源缺失
                 * 直接空手；资源补齐后这里会自动启用 Geo 渲染。
                 */
                if (hasGeoModel == null) {
                    hasGeoModel = Minecraft.getInstance()
                            .getResourceManager()
                            .getResource(Confluence.asResource(
                                    "geo/item/spear/" + BuiltInRegistries.ITEM
                                            .getKey(AbstractSpearItem.this)
                                            .getPath() + ".geo.json"))
                            .isPresent();
                }
                if (!hasGeoModel) return null;
                if (renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(Confluence.asResource("spear/" + BuiltInRegistries.ITEM.getKey(AbstractSpearItem.this).getPath())));
                }
                return renderer;
            }

            @Override
            public HumanoidModel.ArmPose getArmPose(
                    LivingEntity living,
                    InteractionHand hand,
                    ItemStack stack
            ) {
                /*
                 * Forge 1.20.1 的 GeoItemRenderer 与手臂姿态共用同一个客户端扩展。
                 * 必须在这里同时提供两者，后续再注册一个仅含姿态的扩展会覆盖 Geo 渲染器。
                 */
                return ModArmPoses.SPEAR;
            }
        });
    }

    public static PortItemAttributeModifiers attributes(float extraRange, float extraDamage) {
        return PortItemAttributeModifiers.builder()
                .add(PortAttributesExtension.entityInteractionRange(), new PortAttributeModifier(ModItems.BASE_ENTITY_INTERACTION_RANGE_ID, extraRange, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND)
                .add(LibAttributes.getAttackDamage(), new PortAttributeModifier(ModItems.BASE_ATTACK_DAMAGE_ID, extraDamage, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND)
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
