package org.confluence.mod.common.item.common;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
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
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EverBeneficialItem extends TooltipItem {
    public static final Post DO_NOTHING = (id, name, player, everBeneficial, isRespawn) -> {};
    public static final Beneficial LIFE_CRYSTAL = new Beneficial(Confluence.asResource("life_crystal"), EverBeneficial::increaseCrystals, (id, name, player, everBeneficial, isRespawn) -> {
        if (everBeneficial.isLifeCrystalsMaximum() && everBeneficial.isLifeFruitsMaximum() && ManaStorage.of(player).isStarMaximum()) {
            AchievementUtils.awardAchievement(player, "topped_off");
        }
        AttributeInstance instance = player.getAttributes().getInstance(Attributes.MAX_HEALTH);
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(new AttributeModifier(
                id, name,
                everBeneficial.getUsedLifeCrystals() * 4.0,
                AttributeModifier.Operation.ADDITION
        ));
        if (!isRespawn) player.heal(4.0F);
    });
    public static final Beneficial LIFE_FRUITS = new Beneficial(Confluence.asResource("life_fruit"), EverBeneficial::increaseFruits, (id, name, player, everBeneficial, isRespawn) -> {
        AttributeInstance instance = player.getAttributes().getInstance(Attributes.MAX_HEALTH);
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(new AttributeModifier(
                id, name,
                everBeneficial.getUsedLifeFruits(),
                AttributeModifier.Operation.ADDITION
        ));
        if (!isRespawn) player.heal(1.0F);
    });
    public static final Beneficial VITAL_CRYSTAL = new Beneficial(Confluence.asResource("vital_crystal"), EverBeneficial::setVitalCrystalUsed, DO_NOTHING);
    public static final Beneficial AEGIS_APPLE = new Beneficial(Confluence.asResource("aegis_apple"), EverBeneficial::setAegisAppleUsed, (id, name, player, everBeneficial, isRespawn) -> {
        AttributeInstance instance = player.getAttributes().getInstance(Attributes.ARMOR);
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(new AttributeModifier(
                id, name,
                4.0,
                AttributeModifier.Operation.ADDITION
        ));
    });
    public static final Beneficial AMBROSIA = new Beneficial(Confluence.asResource("ambrosia"), EverBeneficial::setAmbrosiaUsed, (id, name, player, everBeneficial, isRespawn) -> {
        int value = TCUtils.getValue(player, TCItems.RIGHT$CLICK$DELAY$SUBSTRACTOR);
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new RightClickSubtractorPacketS2C((byte) Math.min(value + 1, 4)));
        AttributeInstance instance = player.getAttributes().getInstance(PortAttributesExtension.blockBreakSpeed());
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(new AttributeModifier(
                id, name,
                0.05,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    });
    public static final Beneficial GUMMY_WORM = new Beneficial(Confluence.asResource("gummy_worm"), EverBeneficial::setGummyWormUsed, DO_NOTHING);
    public static final Beneficial GALAXY_PEARL = new Beneficial(Confluence.asResource("galaxy_pearl"), EverBeneficial::setGalaxyPearlUsed, (id, name, player, everBeneficial, isRespawn) -> {
        AttributeInstance instance = player.getAttributes().getInstance(Attributes.LUCK);
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(new AttributeModifier(
                id, name,
                0.03,
                AttributeModifier.Operation.ADDITION
        ));
    });
    public static final Beneficial MINECART_UPGRADE_KIT = new Beneficial(Confluence.asResource("minecart_upgrade_kit"), EverBeneficial::setMinecartUpgradeKitUsed, (id, name, player, everBeneficial, isRespawn) -> {
        player.drop(MinecartItems.MECHANICAL_CART.get().getDefaultInstance(), true);
    });
    public static final Beneficial ARTISAN_LOAF = new Beneficial(Confluence.asResource("artisan_loaf"), EverBeneficial::setArtisanLoafUsed, (id, name, player, everBeneficial, isRespawn) -> {
        AttributeInstance instance = player.getAttributes().getInstance(PortAttributesExtension.blockInteractionRange());
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(new AttributeModifier(
                id, name,
                4,
                AttributeModifier.Operation.ADDITION
        ));
    });
//    public static final Beneficial FALLEN_SOUL_CORE = new Beneficial(
//            Confluence.asResource("fallen_soul_core"),
//            everBeneficial -> true,
//            (id, player, everBeneficial, isRespawn) -> {
//                if (!isRespawn) {
//                    everBeneficial.changeFallenSoulCore();
//                    PlayerSpecialData data = PlayerSpecialData.of(player);
//                    boolean active = everBeneficial.getFallenSoulCore();
//                    data.setFallenSoulCoreActive(active);
//                    SoulStorage soulStorage = SoulStorage.of(player);
//
//                    // 关键：同步到客户端
//                    if (player instanceof ServerPlayer serverPlayer) {
//                        Confluence.NETWORK_HANDLER.sendToPlayer(//                                new SoulPacketS2C(
//                                        soulStorage.getMaxSoul(),
//                                        soulStorage.getCurrentSoul(),
//                                        active
//                                ));
//                    }
//                }
//            }
//    );

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
                beneficial.post.accept(beneficial.id, beneficial.name, serverPlayer, data, false);
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    public record Beneficial(UUID id, String name, Predicate<EverBeneficial> pre, Post post) {
        public Beneficial(ResourceLocation id, Predicate<EverBeneficial> pre, Post post) {
            this(PortAttributeModifier.rl2uuid(id), id.getPath(), pre, post);
        }

        public void recovery(EverBeneficial everBeneficial, Predicate<EverBeneficial> condition, ServerPlayer player) {
            if (condition.test(everBeneficial)) {
                post.accept(id, name, player, everBeneficial, true);
            }
        }
    }

    @FunctionalInterface
    public interface Post {
        void accept(UUID id, String name, ServerPlayer player, EverBeneficial everBeneficial, boolean isRespawn);
    }
}
