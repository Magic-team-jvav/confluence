package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.item.enchantment.EnchantmentHelper.PortEnchantmentHelperExtension;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.item.LanceItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.mixed.IServerPlayer;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

/**
 * 通用骑枪类。需要注意的是baseAttackDamage*0.1才是基础伤害，原算法是有问题的，后面可能会改动。
 */
public class BaseLanceItem extends CustomRarityItem implements ILeftClickStateItem, GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int attackInterval;
    private final double attackDistance;
    private final double baseAttackDamage;
    private final double baseKnockback;

    private List<Component> tooltips;
    private TooltipComponent component;

    public BaseLanceItem(Properties properties, ModRarity rarity, int attackInterval, double attackDistance, double baseAttackDamage, double baseKnockback) {
        super(properties.stacksTo(1), rarity);
        this.attackInterval = attackInterval;
        this.attackDistance = attackDistance;
        this.baseAttackDamage = baseAttackDamage;
        this.baseKnockback = baseKnockback;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null) {
            this.component = new AltImageComponent(stack);
        }
        return Optional.of(component);
    }

    @Override
    public void onLeftClick(Player player, ItemStack itemStack) {
        if (!player.level().isClientSide && !player.getCooldowns().isOnCooldown(this)) {
            triggerAnim(player, GeoItem.getOrAssignId(itemStack, (ServerLevel) player.level()), "lance", "sting");
        }
    }

    @Override
    public void onLeftRelease(Player player, ItemStack itemStack) {
        if (!player.level().isClientSide) {
            stopTriggeredAnim(player, GeoItem.getOrAssignId(itemStack, (ServerLevel) player.level()), "lance", "sting");
        }
    }

    @Override
    public boolean canSwitchWithoutRelease(Player player, ItemStack itemStack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected &&
                entity instanceof ServerPlayer owner &&
                WeaponStorage.of(owner).leftClicking &&
                !owner.getCooldowns().isOnCooldown(this) &&
                (attackInterval <= 1 || owner.level().getGameTime() % attackInterval == 0)
        ) {
            Vec3 startVec = new Vec3(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
            Vec3 lanceDirection = owner.getViewVector(1.0F);
            Vec3 endVec = startVec.add(lanceDirection.scale(attackDistance));

            for (Entity victim : level.getEntities(owner, new AABB(startVec, endVec), target -> LibEntityUtils.canHitEntity(target, owner))) {
                if (victim.getBoundingBox().inflate(0.3).clip(startVec, endVec).isEmpty()) continue;
                victim = LibEntityUtils.tryFindBeImpacted(victim);
                owner.setLastHurtMob(victim);
                DamageSource damageSource = ModDamageTypes.of(level, DamageTypes.STING, owner);

                Vec3 attackerVelocity = new Vec3(IServerPlayer.of(owner).confluence$getMovementSpeed()); // 使用者速度
                Vec3 relativeVelocity = attackerVelocity.subtract(victim.getPosition(1).subtract(victim.getPosition(0))); // 计算相对速度，不再使用加速度
                Vec3 projectedVelocity = LibMathUtils.vectorProjection(relativeVelocity, lanceDirection); // 计算投影速度(投影到剑的方向上)
                double impactSpeed = projectedVelocity.length() * 30; // 获得向量长度，第一次乘系数

                victim.hurt(damageSource, Mth.floor(baseAttackDamage * (impactSpeed * 6 / 175 + 0.1F))); // 第二次乘系数，+0.1f在乘baseAttackDamage后得到的是基础数值(其实这里的计算逻辑有点问题)

                if (!victim.getType().is(Tags.EntityTypes.BOSSES)) {
                    double kb = impactSpeed * baseKnockback * 4 / 105;
                    LibEntityUtils.knockBackA2B(owner, victim, kb, kb * 0.3);
                }
                PortEnchantmentHelperExtension.doPostAttackEffects((ServerLevel) level, victim, damageSource);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "lance", state -> PlayState.STOP)
                .triggerableAnim("sting", RawAnimation.begin().thenPlayAndHold("sting")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void appendHoverText(ItemStack stack, PortItem.PortTooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (tooltips == null) {
            ImmutableList.Builder<Component> builder = ImmutableList.builder();
            builder.add(Component.translatable("tooltip." + getDescriptionId() + ".0").withStyle(ChatFormatting.GRAY));
            if (this == LanceItems.JOUSTING_LANCE.get()) {
                builder.add(Component.translatable("tooltip.item.confluence.jousting_lance.1").withStyle(ChatFormatting.GRAY));
            }
            builder.add(
                    Component.translatable("tooltip.confluence.attack_interval", attackInterval).withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("tooltip.confluence.attack_distance", ATTRIBUTE_MODIFIER_FORMAT.format(attackDistance)).withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("tooltip.confluence.attack_damage", ATTRIBUTE_MODIFIER_FORMAT.format(baseAttackDamage)).withStyle(ChatFormatting.DARK_GRAY),
                    Component.translatable("tooltip.confluence.knockback", ATTRIBUTE_MODIFIER_FORMAT.format(baseKnockback)).withStyle(ChatFormatting.DARK_GRAY)
            );
            this.tooltips = builder.build();
        }
        tooltipComponents.addAll(tooltips);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        if (this != LanceItems.JOUSTING_LANCE.get()) return; // todo 还剩两支骑枪
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<BaseLanceItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    String path = BuiltInRegistries.ITEM.getKey(BaseLanceItem.this).getPath();
                    ResourceLocation model = Confluence.asResource("geo/item/lance/" + path + ".geo.json");
                    ResourceLocation texture = Confluence.asResource("textures/item/lance/" + path + ".png");
                    ResourceLocation animation = Confluence.asResource("animations/item/lance.animation.json");
                    this.renderer = new GeoItemRenderer<>(new GeoModel<>() {
                        @Override
                        public ResourceLocation getModelResource(BaseLanceItem animatable) {
                            return model;
                        }

                        @Override
                        public ResourceLocation getTextureResource(BaseLanceItem animatable) {
                            return texture;
                        }

                        @Override
                        public ResourceLocation getAnimationResource(BaseLanceItem animatable) {
                            return animation;
                        }
                    });
                }
                return renderer;
            }
        });
    }

    public static void cancelSting(ServerPlayer player) {
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.getItem() instanceof BaseLanceItem lance) {
            player.getCooldowns().addCooldown(lance, 5);
            lance.stopTriggeredAnim(player, GeoItem.getOrAssignId(itemStack, player.serverLevel()), "lance", "sting");
        }
    }
}
