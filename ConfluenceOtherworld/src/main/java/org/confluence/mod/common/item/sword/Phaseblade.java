package org.confluence.mod.common.item.sword;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.PhasebladeRenderer;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.sword.legacy.SwordPrefabs;
import org.mesdag.portlib.diff.Diff;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Consumer;

public class Phaseblade extends BaseSwordItem implements GeoItem {
    @Diff
    public static final ResourceLocation ID = Confluence.asResource("phaseblade");

    private final String color;
    private final Multimap<Attribute, AttributeModifier> turnOnModifiers;
    private final Multimap<Attribute, AttributeModifier> turnOffModifiers;

    public int frame = 0;

    public Phaseblade(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, String color) {
        super(tier, rarity, rawDamage, rawSpeed, SwordPrefabs.withSpecialSweep(0.8F, new ModifierBuilder() {
            @Override
            public Properties buildProperties(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
                return properties.durability(tier.getUses()).component(ConfluenceMagicLib.MOD_RARITY, rarity);
            }
        }));
        this.color = color;
        this.turnOnModifiers = createAttributes(tier, rawDamage, rawSpeed);
        this.turnOffModifiers = createAttributes(tier, 0, 2);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private static Multimap<Attribute, AttributeModifier> createAttributes(Tier tier, int rawDamage, float rawSpeed) {
        UUID uuid = PortAttributeModifier.rl2uuid(ID);
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(LibAttributes.getAttackDamage().value(), new AttributeModifier(uuid, ID.getPath(), ModItems.getAttackDamage(tier, rawDamage), AttributeModifier.Operation.MULTIPLY_TOTAL))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(uuid, ID.getPath(), ModItems.getAttackSpeed(rawSpeed), AttributeModifier.Operation.MULTIPLY_TOTAL))
                .build();
    }

    public static boolean isTurnOn(ItemStack stack) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        if (tag == null || !tag.contains("isTurnOn", Tag.TAG_BYTE)) return true;
        return tag.getBoolean("isTurnOn");
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
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (level.isClientSide) return stack;

        boolean isTurnOn = isTurnOn(stack);
        if (isTurnOn) {
            triggerAnim(living, GeoItem.getOrAssignId(stack, (ServerLevel) level), "light", "on");
            level.playSound(null, living.blockPosition().above(), ModSoundEvents.LIGHTSABER_OPEN.get(), SoundSource.PLAYERS, 2, 1);
        } else {
            triggerAnim(living, GeoItem.getOrAssignId(stack, (ServerLevel) level), "light", "off");
            level.playSound(null, living.blockPosition().above(), ModSoundEvents.LIGHTSABER_OPEN.get(), SoundSource.PLAYERS);
        }
        if (living instanceof Player player) player.getCooldowns().addCooldown(this, 10);
        LibUtils.updateItemStackNbt(stack, tag -> tag.putBoolean("isTurnOn", !isTurnOn));
        return stack;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return isTurnOn(stack) ? turnOnModifiers : turnOffModifiers;
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
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PhasebladeRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    this.renderer = new PhasebladeRenderer(color);
                }
                return renderer;
            }
        });
    }
}
