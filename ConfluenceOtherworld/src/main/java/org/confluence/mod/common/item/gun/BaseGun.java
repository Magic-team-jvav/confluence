package org.confluence.mod.common.item.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.api.animation.first_person.HandAnimationApi;
import org.confluence.lib.api.animation.first_person.HandAnimationChannel;
import org.confluence.lib.api.animation.first_person.HandAnimationProfile;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.common.CommonConfigs;
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

public class BaseGun extends Item implements GeoItem {
    public static final String DRAW_ACTION = "draw";
    public static final String PUT_AWAY_ACTION = "put_away";
    public static final String SHOOT_ACTION = "shoot";
    public static final String RELOAD_ACTION = "reload";
    public static final String INSPECT_ACTION = "inspect";

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final GunDefinition definition;
    private final HandAnimationProfile animationProfile;

    public BaseGun(Properties properties, GunDefinition definition) {
        this(properties, definition, HandAnimationProfile.builder()
                .channel(HandAnimationChannel.builder("gun")
                        .animation(SHOOT_ACTION, "fire")
                        .animation(DRAW_ACTION, "pick up")
                        .animation(RELOAD_ACTION, "reloading")
                        .build())
                .build());
    }

    public BaseGun(Properties properties, GunDefinition definition, HandAnimationProfile animationProfile) {
        super(prepareProperties(properties, definition));
        this.definition = definition;
        this.animationProfile = animationProfile;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private static Properties prepareProperties(Properties properties, GunDefinition definition) {
        return properties.stacksTo(1).component(ModDataComponentTypes.GUN_PROPERTY, definition.component());
    }

    public BaseGun(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity) {
        this(properties, new GunDefinition(cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity, FireMode.MANUAL));
    }

    public BaseGun(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, float inaccuracy, ModRarity rarity) {
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
        return CommonConfigs.AUTO_FIRE_ALL_GUNS.get() || stack.is(ModTags.Items.AUTOMATIC_GUN);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.confluence.ranged_damage", definition.damage()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.critical_chance", String.format("%.1f", definition.critical() * 100)).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.knockback", definition.knockback()).withStyle(ChatFormatting.GRAY));
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
            channel.animations().forEach((action, clip) -> controller.triggerableAnim(action, channel.triggeredAnimation(action)));
            controller.setSoundKeyframeHandler(event -> {
                ResourceLocation key = ResourceLocation.tryParse(event.getKeyframeData().getSound());
                if (key == null) return;
                SoundEvent value = ForgeRegistries.SOUND_EVENTS.getValue(key);
                if (value == null) return;
                Player player = LibClientUtils.getPlayer();
                if (player == null) return;
                player.level().playLocalSound(player.getX(), player.getEyeY(), player.getZ(), value, SoundSource.PLAYERS, 1, 1, false);
            });
            controllers.add(controller);
        }
    }

    public void fireAnimator(ItemStack stack, ServerPlayer player) {
        HandAnimationApi.stop(this, stack, player, animationProfile, INSPECT_ACTION);
        playAnimator(stack, player, SHOOT_ACTION);
    }

    public void pickAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, DRAW_ACTION);
    }

    public void reloadAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, RELOAD_ACTION);
    }

    public void putAwayAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, PUT_AWAY_ACTION);
    }

    public void inspectAnimator(ItemStack stack, ServerPlayer player) {
        playAnimator(stack, player, INSPECT_ACTION);
    }

    public boolean playAnimator(ItemStack stack, ServerPlayer player, String action) {
        return HandAnimationApi.play(this, stack, player, animationProfile, action);
    }

    public boolean isAnimationPlaying(long instanceId, String action) {
        return cache.getManagerForId(instanceId).getAnimationControllers().values().stream()
                .filter(AnimationController::isPlayingTriggeredAnimation)
                .map(AnimationController::getCurrentAnimation)
                .filter(Objects::nonNull)
                .map(AnimationProcessor.QueuedAnimation::animation)
                .anyMatch(animation -> animationProfile.isAnimation(action, animation.name()));
    }

    public boolean isShootAnimationName(@Nullable String animationName) {
        return animationProfile.isAnimation(SHOOT_ACTION, animationName);
    }

    public boolean isShootAnimationPlaying(long instanceId) {
        return isAnimationPlaying(instanceId, SHOOT_ACTION);
    }

    public boolean isCameraAnimationPlaying(long instanceId) {
        return isAnimationPlaying(instanceId, DRAW_ACTION)
                || isAnimationPlaying(instanceId, PUT_AWAY_ACTION)
                || isAnimationPlaying(instanceId, INSPECT_ACTION)
                || isAnimationPlaying(instanceId, SHOOT_ACTION);
    }

    public boolean isPutAwayAnimationPlaying(ItemStack stack) {
        return isAnimationPlaying(GeoItem.getId(stack), PUT_AWAY_ACTION);
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
