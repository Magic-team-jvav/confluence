package org.confluence.mod.common.event.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.event.SwitchItemFunctionEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.api.event.*;
import org.confluence.mod.api.event.bestiary.ToBeBestiaryEntryEvent;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.entity.minecart.BaseMinecartEntity;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.MinecartItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.common.BaseMinecartItem;
import org.confluence.mod.common.item.mana.CrystalVileShardItem;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.mixed.IAbstractMinecart;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.AchievementOffsetSyncPacketS2C;
import org.confluence.mod.network.s2c.ExtraInventorySyncPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_curio.api.event.ArmorPenetrationEvent;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terraentity.entity.summon.AbstractSummonMob;
import org.confluence.terraentity.init.entity.TEBossEntities;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.*;

@EventBusSubscriber(modid = Confluence.MODID)
public final class GameEvents {
    @SubscribeEvent
    public static void afterAccessoryAbilitiesFlushed(AfterAccessoryAbilitiesFlushedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerUtils.flushPrimitiveValueData(player);
        }
    }

    @SubscribeEvent
    public static void afterFlushArmorSetBonus(AfterFlushArmorSetBonusEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerUtils.flushPrimitiveValueData(player);
        }
    }

    @SubscribeEvent
    public static void command(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void curioAttributeModifier(CurioAttributeModifierEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
        if (prefix == null) return;
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : prefix.modifiers().get().asMap().entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            for (AttributeModifier modifier : entry.getValue()) {
                event.addModifier(attribute, new AttributeModifier(modifier.id(), modifier.amount(), modifier.operation()));
            }
        }
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        if (StartupConfigs.brewingStandRecipe()) {
            ModRecipes.Brewing.registerRecipes(event.getBuilder()::addRecipe);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW) // 需要晚于Curios Api
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer sendTo = event.getPlayer();
        if (sendTo == null) {
            for (ServerPlayer target : event.getPlayerList().getPlayers()) {
                ExtraInventorySyncPacketS2C.sendToPlayersTrackingEntityAndSelf(target, target);
            }
        } else {
            ExtraInventorySyncPacketS2C.sendToClient(sendTo, sendTo);
            AchievementOffsetSyncPacketS2C.sendToClient(sendTo);
        }
    }

    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (PrefixUtils.canInit(event.getTo())) {
                PrefixUtils.initPrefix(player.getRandom(), event.getTo());
            }
        }
    }

    @SubscribeEvent
    public static void shimmerItemTransmutation$Pre(ShimmerItemTransmutationEvent.Pre event) {
        ItemEntity source = event.getSource();
        if (source.getItem().is(ConsumableItems.SLIME_CROWN) && SlimeRainGameEvent.INSTANCE.forceStart()) {
            source.level().playSound(null, source.getX(), source.getY(), source.getZ(), ModSoundEvents.SHIMMER_EVOLUTION.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
            source.discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void shimmerItemTransmutation$Post(ShimmerItemTransmutationEvent.Post event) {
        if (event.getTargets() == null) return;
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if (currentServer == null) return;

        boolean corrupt = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.THE_CORRUPTION);
        boolean crimson = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.THE_CRIMSON);
        if (corrupt == crimson) return;
        List<ItemStack> targets = new ArrayList<>();
        for (ItemStack targetStack : event.getTargets()) {
            Item target = RegisterEvilMaterialReplacesEvent.getPossible(targetStack.getItem(), corrupt, crimson);
            if (target == null) {
                targets.add(targetStack);
            } else {
                targets.add(new ItemStack(target, targetStack.getCount()));
            }
        }
        event.setTargets(targets);
    }

    @SubscribeEvent
    public static void gun$ShrinkBullet(GunEvent.ShrinkBulletEvent event) {
        if (!event.isInfinity() && PlayerUtils.shouldSkipConsumeAmmo(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(AchievementOffsetLoader.getInstance());
    }

    @SubscribeEvent
    public static void additionalMana(AdditionalManaEvent event) {
        ArsNouveauHelper.additionalMana(event);
        IronSpellHelper.additionalMana(event);
    }

    @SubscribeEvent
    public static void switchItemFunction$Post(SwitchItemFunctionEvent.Post event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            VisibilityPacketS2C.sendEcho(serverPlayer);
        }
        PlayerSpecialData data = PlayerSpecialData.of(player);
        Item item = event.getStack().getItem();
        boolean c = item == ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.get();
        if (c || item == ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP.get()) {
            data.setCouldHurtCritters(event.isEnabled());
        }
        if (c || item == ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION.get()) {
            data.setCouldDamageEnvironment(event.isEnabled());
        }
    }

    @SubscribeEvent
    public static void rightClickRailBlock(MinecartAbilityEvent.RightClickRailBlock event) {
        AbstractMinecart minecart = event.getMinecart();
        if (minecart != null) return;

        ServerLevel level = (ServerLevel) event.getEntity().level();
        BlockPos blockPos = event.getBlockPos();
        boolean ascending = event.getRailBlock().getRailDirection(event.getBlockState(), level, blockPos, null).isAscending();
        double x = blockPos.getX() + 0.5;
        double y = blockPos.getY() + 0.0625 + (ascending ? 0.5 : 0.0);
        double z = blockPos.getZ() + 0.5;
        ItemStack minecartItem = event.getMinecartItem();

        if (minecartItem.isEmpty()) {
            event.setMinecart(new BaseMinecartEntity(level, x, y, z, MinecartItems.Types.WOODEN));
        } else if (minecartItem.getItem() == Items.MINECART) {
            event.setMinecart(new BaseMinecartEntity(level, x, y, z, MinecartItems.Types.VANILLA));
        } else if (minecartItem.getItem() instanceof BaseMinecartItem baseMinecartItem) {
            event.setMinecart(Objects.requireNonNull(baseMinecartItem.createMinecart(level, x, y, z, AbstractMinecart.Type.RIDEABLE, minecartItem, event.getEntity())));
        }
    }

    @SubscribeEvent
    public static void dismountOnMinecart(MinecartAbilityEvent.DismountOnMinecart event) {
        if (event.getMinecartItem() == null && event.getMinecart().getMinecartType() == AbstractMinecart.Type.RIDEABLE) {
            event.setMinecartItem(IAbstractMinecart.of(event.getMinecart()).confluence$getDropItem().getDefaultInstance());
        }
    }

    @SubscribeEvent
    public static void toBeBestiaryEntry(ToBeBestiaryEntryEvent event) {
        LivingEntity living = event.getEntity();
        if (living instanceof AbstractSummonMob) {
            event.setCanceled(true);
        } else {
            EntityType<?> type = living.getType();
            if (type.is(ModTags.EntityTypes.BESTIARY_BLACKLIST)) {
                event.setCanceled(true);
            } else if (type == TEBossEntities.SKELETRON_HAND.get()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void getArmorSetBonus(GetArmorSetBonusDataEvent event) {
        ArmorSetBonusKey key = event.getKey();
        if (key.head().builtInRegistryHolder().is(ModTags.Items.ROBE)) {
            if (key.chest() == ArmorItems.WIZARD_HAT.get()) {
                event.setNeoData(ModArmorBonus.WIZARD_HAT_SET_BONUS);
            } else if (key.chest() == ArmorItems.MAGIC_HAT.get()) {
                event.setNeoData(ModArmorBonus.MAGIC_HAT_SET_BONUS);
            }
        }
    }

    @SubscribeEvent
    public static void armorPenetration(ArmorPenetrationEvent event) {
        Entity direct = event.getDamageSource().getDirectEntity();
        if (direct != null && direct.getType() == ModEntities.CRYSTAL_VILE_SHARD_PROJECTILE.get()) {
            event.setPenetration(event.getPenetration() + CrystalVileShardItem.ARMOR_PENETRATION);
        }
    }
}
