package org.confluence.mod.common.item.sword;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.client.renderer.item.PhasebladeRenderer;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terraentity.data.component.SingleBooleanComponent;
import org.confluence.terraentity.init.TEDataComponentTypes;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Phaseblade extends BaseSwordItem implements GeoItem {
    public int frame = 0;
    private final String color;
    private final ItemAttributeModifiers turnOnModifiers;
    private final ItemAttributeModifiers turnOffModifiers;

    public Phaseblade(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, String color) {
        super(tier, rarity, 0, rawSpeed, new ModifierBuilder() {
            @Override
            public Properties buildProperties(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
                if (modifier != null) modifier.forEach(m -> m.accept(properties));
                return this.properties = properties.durability(tier.getUses()).component(ConfluenceMagicLib.MOD_RARITY, rarity);
            }
        }.modifyProperties(p -> p.component(TEDataComponentTypes.BOOMERANG_READY.get(), SingleBooleanComponent.TRUE)));
        this.color = color;
        this.turnOnModifiers = createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1, rawSpeed - 4);
        this.turnOffModifiers = createAttributes(tier, 1 - tier.getAttackDamageBonus(), -2);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public static boolean isTurnOn(ItemStack itemStack) {
        return itemStack.getComponents().getOrDefault(TEDataComponentTypes.BOOMERANG_READY.get(), SingleBooleanComponent.TRUE).value();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return itemStack;
        SingleBooleanComponent component = itemStack.set(TEDataComponentTypes.BOOMERANG_READY.get(), new SingleBooleanComponent(!isTurnOn(itemStack)));
        boolean turnOn = component == null || !component.value();
        if (turnOn) {
            triggerAnim(living, GeoItem.getOrAssignId(itemStack, (ServerLevel) level), "light", "on");
            level.playSound(null, living.getOnPos().above(), ModSoundEvents.LIGHTSABER_OPEN.get(), SoundSource.PLAYERS, 2, 1);
        } else {
            triggerAnim(living, GeoItem.getOrAssignId(itemStack, (ServerLevel) level), "light", "off");
            level.playSound(null, living.getOnPos().above(), ModSoundEvents.LIGHTSABER_OPEN.get(), SoundSource.PLAYERS);
        }
        if (living instanceof Player player) player.getCooldowns().addCooldown(this, 10);
        return itemStack;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return stack.getOrDefault(TEDataComponentTypes.BOOMERANG_READY, SingleBooleanComponent.TRUE).value() ? turnOnModifiers : turnOffModifiers;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "light", state -> PlayState.STOP)
                .triggerableAnim("off", RawAnimation.begin().thenPlay("turn_off"))
                .triggerableAnim("on", RawAnimation.begin().thenPlay("turn_on"))
        );
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private PhasebladeRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    this.renderer = new PhasebladeRenderer(color);
                }
                return renderer;
            }
        });
    }
}
