package org.confluence.mod.common.item.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.client.animation.HandAnimationAction;
import org.confluence.mod.api.client.animation.HandAnimationApi;
import org.confluence.mod.api.client.animation.HandAnimationChannel;
import org.confluence.mod.api.client.animation.HandAnimationProfile;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.gun.definition.FireMode;
import org.confluence.mod.common.item.gun.definition.GunDefinition;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Objects;

/// 枪械物品只保存不可变定义与动画声明，服务端射击由 combat 服务处理。
public class BaseGun extends Item implements GeoItem {
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final GunDefinition definition;
    private final HandAnimationProfile animationProfile;

    public BaseGun(Properties properties, GunDefinition definition) {
        this(properties, definition, HandAnimationProfile.legacy());
    }

    public BaseGun(Properties properties, GunDefinition definition, HandAnimationProfile animationProfile) {
        super(prepareProperties(properties, definition));
        this.definition = Objects.requireNonNull(definition, "definition");
        this.animationProfile = Objects.requireNonNull(animationProfile, "animationProfile");
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private static Properties prepareProperties(Properties properties, GunDefinition definition) {
        return properties.stacksTo(1).component(ModDataComponentTypes.GUN_PROPERTY, definition.component());
    }

    public BaseGun(Properties properties, int cooldown, float damage, float velocity, float knockback,
                   float critical, int penetrate, float inaccuracy, ModRarity rarity) {
        this(properties, new GunDefinition(cooldown, damage, velocity, knockback, critical, penetrate,
                inaccuracy, rarity, FireMode.MANUAL));
    }

    public BaseGun(Properties properties, int cooldown, float damage, float velocity, float knockback,
                   float critical, float inaccuracy, ModRarity rarity) {
        this(properties, cooldown, damage, velocity, knockback, critical, 0, inaccuracy, rarity);
    }

    public GunDefinition getDefinition() {
        return definition;
    }

    public HandAnimationProfile getAnimationProfile() {
        return animationProfile;
    }

    public int getCooldown() {
        return definition.cooldown();
    }

    public boolean isAutomatic(ItemStack stack) {
        return !stack.is(ModTags.Items.MANUAL_GUN)
                && (definition.fireMode() == FireMode.AUTOMATIC || stack.is(ModTags.Items.AUTOMATIC_GUN));
    }

    public String getColorID() {
        return "";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.confluence.ranged_damage", definition.damage())
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.critical_chance",
                String.format("%.1f", definition.critical() * 100)).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.knockback", definition.knockback())
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        for (HandAnimationChannel channel : animationProfile.channels()) {
            AnimationController<BaseGun> controller = new AnimationController<>(this, channel.name(), state -> {
                if (!state.getController().isPlayingTriggeredAnimation()) {
                    channel.idle().ifPresent(idle -> state.getController().setAnimation(idle.rawAnimation()));
                }
                return PlayState.CONTINUE;
            });
            channel.animations().forEach((action, clip) -> controller.triggerableAnim(action.id(), channel.triggeredAnimation(action)));
            controllers.add(controller);
        }
    }

    public void fireAnimator(ItemStack stack, ServerPlayer player) {
        HandAnimationApi.stop(this, stack, player, animationProfile, HandAnimationAction.INSPECT);
        playAnimator(stack, player, HandAnimationAction.SHOOT);
    }

    public void pickAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.DRAW);
    }

    public void reloadAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.RELOAD);
    }

    public void putAwayAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.PUT_AWAY);
    }

    public void inspectAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, HandAnimationAction.INSPECT);
    }

    public boolean playAnimator(ItemStack stack, ServerPlayer player, HandAnimationAction action) {
        return HandAnimationApi.play(this, stack, player, animationProfile, action);
    }

    public boolean isAnimationPlaying(long instanceId, HandAnimationAction action) {
        return cache.getManagerForId(instanceId).getAnimationControllers().values().stream()
                .filter(AnimationController::isPlayingTriggeredAnimation)
                .map(AnimationController::getCurrentAnimation)
                .filter(Objects::nonNull)
                .map(AnimationProcessor.QueuedAnimation::animation)
                .anyMatch(animation -> animationProfile.isAnimation(action, animation.name()));
    }

    public boolean isShootAnimationName(@Nullable String animationName) {
        return animationProfile.isAnimation(HandAnimationAction.SHOOT, animationName);
    }

    public boolean isShootAnimationPlaying(long instanceId) {
        return isAnimationPlaying(instanceId, HandAnimationAction.SHOOT);
    }

    public boolean isCameraAnimationPlaying(long instanceId) {
        return isAnimationPlaying(instanceId, HandAnimationAction.DRAW)
                || isAnimationPlaying(instanceId, HandAnimationAction.PUT_AWAY)
                || isAnimationPlaying(instanceId, HandAnimationAction.INSPECT)
                || isAnimationPlaying(instanceId, HandAnimationAction.SHOOT);
    }

    public boolean isPutAwayAnimationPlaying(ItemStack stack) {
        return isAnimationPlaying(GeoItem.getId(stack), HandAnimationAction.PUT_AWAY);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }
}
