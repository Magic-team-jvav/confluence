package org.confluence.mod.common.item.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.item.MinecartItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.network.s2c.RightClickSubtractorPacketS2C;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EverBeneficialItem extends TooltipItem {
    public static final Post DO_NOTHING = (id, player, everBeneficial, isRespawn) -> {};
    public static final Beneficial LIFE_CRYSTAL = new Beneficial(Confluence.asResource("life_crystal"), EverBeneficial::increaseCrystals, (id, player, everBeneficial, isRespawn) -> {
        if (everBeneficial.isLifeCrystalsMaximum() && everBeneficial.isLifeFruitsMaximum() && ManaStorage.of(player).isStarMaximum()) {
            AchievementUtils.awardAchievement(player, "topped_off");
        }
        AttributeInstance attributeInstance = player.getAttributes().getInstance(Attributes.MAX_HEALTH);
        if (attributeInstance == null) return;
        attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(id, everBeneficial.getUsedLifeCrystals() * 4.0, AttributeModifier.Operation.ADD_VALUE));
        if (!isRespawn) player.heal(4.0F);
    });
    public static final Beneficial LIFE_FRUITS = new Beneficial(Confluence.asResource("life_fruit"), EverBeneficial::increaseFruits, (id, player, everBeneficial, isRespawn) -> {
        AttributeInstance attributeInstance = player.getAttributes().getInstance(Attributes.MAX_HEALTH);
        if (attributeInstance == null) return;
        attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(id, everBeneficial.getUsedLifeFruits(), AttributeModifier.Operation.ADD_VALUE));
        if (!isRespawn) player.heal(1.0F);
    });
    public static final Beneficial VITAL_CRYSTAL = new Beneficial(Confluence.asResource("vital_crystal"), EverBeneficial::setVitalCrystalUsed, DO_NOTHING);
    public static final Beneficial AEGIS_APPLE = new Beneficial(Confluence.asResource("aegis_apple"), EverBeneficial::setAegisAppleUsed, (id, player, everBeneficial, isRespawn) -> {
        AttributeInstance attributeInstance = player.getAttributes().getInstance(Attributes.ARMOR);
        if (attributeInstance == null) return;
        attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(id, 4.0, AttributeModifier.Operation.ADD_VALUE));
    });
    public static final Beneficial AMBROSIA = new Beneficial(Confluence.asResource("ambrosia"), EverBeneficial::setAmbrosiaUsed, (id, player, everBeneficial, isRespawn) -> {
        int value = TCUtils.getAccessoriesValue(player, TCItems.RIGHT$CLICK$DELAY$SUBSTRACTOR);
        PacketDistributor.sendToPlayer(player, new RightClickSubtractorPacketS2C((byte) Math.min(value + 1, 4)));
        AttributeInstance attributeInstance = player.getAttributes().getInstance(Attributes.BLOCK_BREAK_SPEED);
        if (attributeInstance == null) return;
        attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(id, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    });
    public static final Beneficial GUMMY_WORM = new Beneficial(Confluence.asResource("gummy_worm"), EverBeneficial::setGummyWormUsed, DO_NOTHING);
    public static final Beneficial GALAXY_PEARL = new Beneficial(Confluence.asResource("galaxy_pearl"), EverBeneficial::setGalaxyPearlUsed, (id, player, everBeneficial, isRespawn) -> {
        AttributeInstance attributeInstance = player.getAttributes().getInstance(Attributes.LUCK);
        if (attributeInstance == null) return;
        attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(id, 0.03, AttributeModifier.Operation.ADD_VALUE));
    });
    public static final Beneficial MINECART_UPGRADE_KIT = new Beneficial(Confluence.asResource("minecart_upgrade_kit"), EverBeneficial::setMinecartUpgradeKitUsed, (id, player, everBeneficial, isRespawn) -> {
        player.drop(MinecartItems.MECHANICAL_CART.get().getDefaultInstance(), true);
    });
    public static final Beneficial ARTISAN_LOAF = new Beneficial(Confluence.asResource("artisan_loaf"), EverBeneficial::setArtisanLoafUsed, (id, player, everBeneficial, isRespawn) -> {
        AttributeInstance attributeInstance = player.getAttributes().getInstance(Attributes.BLOCK_INTERACTION_RANGE);
        if (attributeInstance == null) return;
        attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(id, 4, AttributeModifier.Operation.ADD_VALUE));
    });

    private final Beneficial beneficial;
    private final @Nullable Supplier<SoundEvent> sound;

    public EverBeneficialItem(Properties properties, ModRarity rarity, Beneficial beneficial, @Nullable Supplier<SoundEvent> sound, List<Component> tooltips) {
        super(properties, rarity, tooltips);
        this.beneficial = beneficial;
        this.sound = sound;
    }

    public EverBeneficialItem(ModRarity rarity, Beneficial beneficial, @Nullable Supplier<SoundEvent> sound, List<Component> tooltips) {
        super(new Properties(), rarity, tooltips);
        this.beneficial = beneficial;
        this.sound = sound;
    }

    public EverBeneficialItem(ModRarity rarity, Beneficial beneficial, List<Component> tooltips) {
        super(new Properties(), rarity, tooltips);
        this.beneficial = beneficial;
        this.sound = null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide && sound != null) player.playSound(sound.get());

        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            EverBeneficial data = EverBeneficial.of(player);
            if (beneficial.pre.test(data)) {
                beneficial.post.accept(beneficial.id, serverPlayer, data, false);
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    public record Beneficial(ResourceLocation id, Predicate<EverBeneficial> pre, Post post) {
        public void recovery(EverBeneficial everBeneficial, Predicate<EverBeneficial> condition, ServerPlayer player) {
            if (condition.test(everBeneficial)) {
                post.accept(id, player, everBeneficial, true);
            }
        }
    }

    @FunctionalInterface
    public interface Post {
        void accept(ResourceLocation id, ServerPlayer player, EverBeneficial everBeneficial, boolean isRespawn);
    }
}
