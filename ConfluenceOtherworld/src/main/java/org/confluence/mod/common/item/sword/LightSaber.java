package org.confluence.mod.common.item.sword;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.client.renderer.item.LightSaberRenderer;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terraentity.data.component.SingleBooleanComponent;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public abstract class LightSaber extends BaseSwordItem implements GeoItem {

    public int frame = 0;
    private String color;

    private final AttributeModifier modifier;
    public LightSaber(Tier tier, ModRarity rarity, int damage, float attackSpeed, String color) {
        super(tier, rarity, 0, attackSpeed, new ModifierBuilder().modifyProperties(p->p.component(TEDataComponentTypes.BOOMERANG_READY.get(), SingleBooleanComponent.TRUE)));
        this.color = color;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);

        modifier = new AttributeModifier(ResourceLocation.withDefaultNamespace("generic.attack_damage"), damage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE);
    }

    public static boolean isTurnOn(ItemStack itemStack) {
        return itemStack.getComponents().getOrDefault(TEDataComponentTypes.BOOMERANG_READY.get(), SingleBooleanComponent.TRUE).value();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity living) {
        if (level.isClientSide) return itemStack;
        boolean turnOn = !itemStack.set(TEDataComponentTypes.BOOMERANG_READY.get(), new SingleBooleanComponent(!isTurnOn(itemStack))).value();
        var builder = ItemAttributeModifiers.builder();
        if(turnOn){
            itemStack.getComponents().get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers().forEach(m -> {
                builder.add(m.attribute(), m.modifier(), m.slot());

            });
            builder.add(Attributes.ATTACK_DAMAGE, modifier, EquipmentSlotGroup.MAINHAND);
            triggerAnim(living, GeoItem.getOrAssignId(itemStack, (ServerLevel) level), "light", "on");
            level.playSound(null, living.getOnPos().above(), ModSoundEvents.LIGHTSABER_OPEN.get(), SoundSource.PLAYERS, 2, 1);

        }else{
            itemStack.getComponents().get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers().forEach(m -> {
                if(m.attribute() != Attributes.ATTACK_DAMAGE){
                    builder.add(m.attribute(), m.modifier(), m.slot());
                }
            });
            triggerAnim(living, GeoItem.getOrAssignId(itemStack, (ServerLevel) level), "light", "off");
            level.playSound(null, living.getOnPos().above(), ModSoundEvents.LIGHTSABER_OPEN.get(), SoundSource.PLAYERS);
        }
        itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());

        if (living instanceof Player player) player.getCooldowns().addCooldown(this, 10);
        return itemStack;
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
            private LightSaberRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer  getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new LightSaberRenderer(color);
                }
                return renderer;
            }
        });
    }

    // 沟槽的geo
    public static class Red extends LightSaber {
        public Red( Tier tier, ModRarity rarity, int damage, float attackSpeed) {
            super(tier, rarity, damage, attackSpeed, "red");
        }
    }
    public static class Blue extends LightSaber {
        public Blue( Tier tier, ModRarity rarity, int damage, float attackSpeed) {
            super(tier, rarity, damage, attackSpeed, "blue");
        }
    }
    public static class Green extends LightSaber {
        public Green( Tier tier, ModRarity rarity, int damage, float attackSpeed) {
            super(tier, rarity, damage, attackSpeed, "green");
        }
    }
    public static class Yellow extends LightSaber {
        public Yellow( Tier tier, ModRarity rarity, int damage, float attackSpeed) {
            super(tier, rarity, damage, attackSpeed, "yellow");
        }
    }
    public static class Purple extends LightSaber {
        public Purple( Tier tier, ModRarity rarity, int damage, float attackSpeed) {
            super(tier, rarity, damage, attackSpeed, "purple");
        }
    }
    public static class Orange extends LightSaber {
        public Orange( Tier tier, ModRarity rarity, int damage, float attackSpeed) {
            super(tier, rarity, damage, attackSpeed, "orange");
        }
    }
    public static class White extends LightSaber {
        public White( Tier tier, ModRarity rarity, int damage, float attackSpeed) {
            super(tier, rarity, damage, attackSpeed, "white");
        }
    }

}
