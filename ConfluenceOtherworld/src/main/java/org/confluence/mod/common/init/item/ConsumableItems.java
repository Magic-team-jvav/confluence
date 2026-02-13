package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.entity.projectile.ThrownWaterProjectile;
import org.confluence.mod.common.entity.projectile.bomb.*;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.GoblinArmyGameEvent;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.mana.ArcaneCrystalItem;
import org.confluence.mod.common.item.mana.ManaCrystalItem;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.network.s2c.RightClickSubtractorPacketS2C;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.entity.boss.*;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ConsumableItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<ManaCrystalItem> MANA_CRYSTAL = ITEMS.register("mana_crystal", ManaCrystalItem::new);
    public static final DeferredItem<EverBeneficialItem> LIFE_CRYSTAL = registerBeneficial(
            "life_crystal", 15, Attributes.MAX_HEALTH, 4.0, AttributeModifier.Operation.ADD_VALUE,
            ModSoundEvents.LIFE_CRYSTAL_USE, (e, r) -> {
                if (!r) e.heal(4.0f);
                if (e instanceof ServerPlayer sp && EverBeneficial.of(sp).isLifeCrystalsMaximum() && EverBeneficial.of(sp).isLifeFruitsMaximum() && ManaStorage.of(sp).isStarMaximum())
                    AchievementUtils.awardAchievement(sp, "topped_off");
            }, ModRarity.GREEN, 1, ChatFormatting.GREEN);
    public static final DeferredItem<EverBeneficialItem> LIFE_FRUIT = registerBeneficial(
            "life_fruit", 20, Attributes.MAX_HEALTH, 1.0, AttributeModifier.Operation.ADD_VALUE,
            ModSoundEvents.LIFE_CRYSTAL_USE, (e, r) -> {
                if (!r) e.heal(1.0f);
            }, ModRarity.LIME, 1, ChatFormatting.GREEN);
    public static final DeferredItem<EverBeneficialItem> VITAL_CRYSTAL = registerFunctionalBeneficial("vital_crystal", 1, ModSoundEvents.TRANSMUTATION_USE, ModRarity.LIGHT_PURPLE, ChatFormatting.GREEN);
    public static final DeferredItem<ArcaneCrystalItem> ARCANE_CRYSTAL = ITEMS.register("arcane_crystal", ArcaneCrystalItem::new);
    public static final DeferredItem<EverBeneficialItem> AEGIS_APPLE = registerAttributeBeneficial("aegis_apple", 1, Attributes.ARMOR, 4.0, ModSoundEvents.TRANSMUTATION_USE, ModRarity.LIGHT_PURPLE, ChatFormatting.GREEN);
    public static final DeferredItem<EverBeneficialItem> AMBROSIA = registerBeneficial(
            "ambrosia", 1, Attributes.BLOCK_BREAK_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            ModSoundEvents.TRANSMUTATION_USE, (e, r) -> {
                if (!r && e instanceof ServerPlayer sp) {
                    int v = TCUtils.getValue(sp, org.confluence.terra_curio.common.init.TCItems.RIGHT$CLICK$DELAY$SUBSTRACTOR);
                    PacketDistributor.sendToPlayer(sp, new RightClickSubtractorPacketS2C((byte) Math.min(v + 1, 4)));
                }
            }, ModRarity.LIGHT_PURPLE, 1, ChatFormatting.GREEN);
    public static final DeferredItem<EverBeneficialItem> GUMMY_WORM = registerFunctionalBeneficial("gummy_worm", 1, ModSoundEvents.TRANSMUTATION_USE, ModRarity.LIGHT_PURPLE, ChatFormatting.GREEN);
    public static final DeferredItem<EverBeneficialItem> GALAXY_PEARL = registerAttributeBeneficial("galaxy_pearl", 1, Attributes.LUCK, 0.03, ModSoundEvents.TRANSMUTATION_USE, ModRarity.LIGHT_PURPLE, ChatFormatting.GREEN);
    public static final DeferredItem<EverBeneficialItem> MINECART_UPGRADE_KIT = registerBeneficial(
            "minecart_upgrade_kit", 1, null, 0, AttributeModifier.Operation.ADD_VALUE,
            ModSoundEvents.TRANSMUTATION_USE, (e, r) -> {
                if (!r && e instanceof ServerPlayer sp)
                    sp.drop(MinecartItems.MECHANICAL_CART.get().getDefaultInstance(), true);
            }, ModRarity.EXPERT, 2, ChatFormatting.GREEN);
    public static final DeferredItem<EverBeneficialItem> ARTISAN_LOAF = registerAttributeBeneficial("artisan_loaf", 1, Attributes.BLOCK_INTERACTION_RANGE, 4.0, ModSoundEvents.TRANSMUTATION_USE, ModRarity.ORANGE, ChatFormatting.GREEN);
    public static final DeferredItem<AdvancedCombatTechniquesItem> ADVANCED_COMBAT_TECHNIQUES = ITEMS.register("advanced_combat_techniques", AdvancedCombatTechniquesItem::new);
    public static final DeferredItem<AdvancedCombatTechniquesVolumeTwoItem> ADVANCED_COMBAT_TECHNIQUES_VOLUME_TWO = ITEMS.register("advanced_combat_techniques_volume_two", AdvancedCombatTechniquesVolumeTwoItem::new);
    public static final DeferredItem<PeddlersSatchelItem> PEDDLERS_SATCHEL = ITEMS.register("peddlers_satchel", PeddlersSatchelItem::new);
//    public static final DeferredItem<EverBeneficialItem> FALLEN_SOUL_CORE = registerFunctionalBeneficial("fallen_soul_core", 1, ModSoundEvents.LIFE_CRYSTAL_USE, ModRarity.BLUE, ChatFormatting.GREEN);

    public static final DeferredItem<ThrowableItem<BaseBombEntity>> BOMB = ITEMS.register("bomb", () -> new ThrowableItem<>(0.8F, BaseBombEntity::new));
    public static final DeferredItem<ThrowableItem<BouncyBombEntity>> BOUNCY_BOMB = ITEMS.register("bouncy_bomb", () -> new ThrowableItem<>(0.8F, BouncyBombEntity::new));
    public static final DeferredItem<ThrowableItem<StickyBombEntity>> STICKY_BOMB = ITEMS.register("sticky_bomb", () -> new ThrowableItem<>(0.8F, StickyBombEntity::new));
    public static final DeferredItem<ThrowableItem<SmokeBombEntity>> SMOKE_BOMB = ITEMS.register("smoke_bomb", () -> new ThrowableItem<>(0.8F, SmokeBombEntity::new));
    public static final DeferredItem<ThrowableItem<BombFishEntity>> BOMB_FISH = ITEMS.register("bomb_fish", () -> new ThrowableItem<>(0.8F, BombFishEntity::new));
    public static final DeferredItem<ThrowableItem<ScarabBombEntity>> SCARAB_BOMB = ITEMS.register("scarab_bomb", () -> new ThrowableItem<>(0.8F, ScarabBombEntity::new));
    public static final DeferredItem<ThrowableItem<BaseDynamiteEntity>> DYNAMITE = ITEMS.register("dynamite", () -> new ThrowableItem<>(0.75F, BaseDynamiteEntity::new));
    public static final DeferredItem<ThrowableItem<BouncyDynamiteEntity>> BOUNCY_DYNAMITE = ITEMS.register("bouncy_dynamite", () -> new ThrowableItem<>(0.75F, BouncyDynamiteEntity::new));
    public static final DeferredItem<ThrowableItem<StickyDynamiteEntity>> STICKY_DYNAMITE = ITEMS.register("sticky_dynamite", () -> new ThrowableItem<>(0.75F, StickyDynamiteEntity::new));
    public static final DeferredItem<ThrowableItem<BaseGrenadeEntity>> GRENADE = ITEMS.register("grenade", () -> new ThrowableItem<>(0.7F, BaseGrenadeEntity::new));
    public static final DeferredItem<ThrowableItem<BouncyGrenadeEntity>> BOUNCY_GRENADE = ITEMS.register("bouncy_grenade", () -> new ThrowableItem<>(0.7F, BouncyGrenadeEntity::new));
    public static final DeferredItem<ThrowableItem<StickyGrenadeEntity>> STICKY_GRENADE = ITEMS.register("sticky_grenade", () -> new ThrowableItem<>(0.7F, StickyGrenadeEntity::new));
    public static final DeferredItem<ThrowableItem<BeenadeEntity>> BEENADE = ITEMS.register("beenade", () -> new ThrowableItem<>(0.7F, BeenadeEntity::new));
    public static final DeferredItem<ThrowableItem<BaseDirtBombEntity>> DIRT_BOMB = ITEMS.register("dirt_bomb", () -> new ThrowableItem<>(0.8F, BaseDirtBombEntity::new));
    public static final DeferredItem<ThrowableItem<StickyDirtBombEntity>> STICKY_DIRT_BOMB = ITEMS.register("sticky_dirt_bomb", () -> new ThrowableItem<>(0.8F, StickyDirtBombEntity::new));
    public static final DeferredItem<ThrowableItem<DryBombEntity>> DRY_BOMB = ITEMS.register("dry_bomb", () -> new ThrowableItem<>(0.8F, DryBombEntity::new));
    public static final DeferredItem<ThrowableItem<LiquidBombEntity>> WET_BOMB = ITEMS.register("wet_bomb", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.WET_BOMB.get(), player, Fluids.WATER, 3)));
    public static final DeferredItem<ThrowableItem<LiquidBombEntity>> LAVA_BOMB = ITEMS.register("lava_bomb", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.LAVA_BOMB.get(), player, Fluids.LAVA, 3)));
    public static final DeferredItem<ThrowableItem<LiquidBombEntity>> HONEY_BOMB = ITEMS.register("honey_bomb", () -> new ThrowableItem<>(0.8F, player -> new LiquidBombEntity(ModEntities.HONEY_BOMB.get(), player, ModFluids.HONEY.fluid().get(), 3)));
    public static final DeferredItem<ThrowableDropSelfItem> SHURIKEN = ITEMS.register("shuriken", () -> new ThrowableDropSelfItem(ModEntities.SHURIKEN_PROJECTILE.get(), 4.2f, 1.2f, 0.5f, 5, 3, true));
    public static final DeferredItem<ThrowableDropSelfItem> THROWING_KNIVE = ITEMS.register("throwing_knive", () -> new ThrowableDropSelfItem(ModEntities.THROWN_KNIVE_PROJECTILE.get(), 5f, 1.2f, 0.5f, 5, 3, true));
    public static final DeferredItem<ThrowableDropSelfItem> BONE_THROWING_KNIFE = ITEMS.register("bone_throwing_knife", () -> new ThrowableDropSelfItem(ModEntities.BONE_THROWN_KNIVE_PROJECTILE.get(), 6f, 1.2f, 0.5f, 5, 3, false));
    public static final DeferredItem<ThrowableDropSelfItem> FROST_DAGGERFISH = ITEMS.register("frost_daggerfish", () -> new ThrowableDropSelfItem(ModEntities.FROST_DAGGERFISH_PROJECTILE.get(), 7f, 1.7f, 0.5f, 5, 3, false));
    public static final DeferredItem<ThrowableDropSelfItem> DUNGEON_DEMON_BONE = ITEMS.register("dungeon_demon_bone", () -> new ThrowableDropSelfItem(ModEntities.DUNGEON_DEMON_BONE_PROJECTILE.get(), 5f, 1.2f, 0.5f, 3, 3, false));
    public static final DeferredItem<ThrowableDropSelfItem> JAVELIN = ITEMS.register("javelin", () -> new ThrowableDropSelfItem(ModEntities.JAVELIN_PROJECTILE.get(), 5f, 1.2f, 0.5f, 5, 5, true));
    public static final DeferredItem<SpikyBallItem> SPIKY_BALL = ITEMS.register("spiky_ball", SpikyBallItem::new);
    public static final DeferredItem<ThrowableItem<ThrownWaterProjectile>> HOLY_WATER = ITEMS.register("holy_water", () -> new ThrowableItem<>(0.8F, player -> new ThrownWaterProjectile(player, ISpreadable.Type.PURE)));
    public static final DeferredItem<ThrowableItem<ThrownWaterProjectile>> UNHOLY_WATER = ITEMS.register("unholy_water", () -> new ThrowableItem<>(0.8F, player -> new ThrownWaterProjectile(player, ISpreadable.Type.CORRUPT)));
    public static final DeferredItem<ThrowableItem<ThrownWaterProjectile>> BLOOD_WATER = ITEMS.register("blood_water", () -> new ThrowableItem<>(0.8F, player -> new ThrownWaterProjectile(player, ISpreadable.Type.CRIMSON)));

    public static final DeferredItem<RightClickLootItem> CLAM = ITEMS.register("clam", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CLAM));
    public static final DeferredItem<RightClickLootItem> PINE_CONE = ITEMS.register("pine_cone", () -> new RightClickLootItem(ModRarity.WHITE, ModLootTables.PINE_CONE));
    public static final DeferredItem<RightClickLootItem> HERB_BAG = ITEMS.register("herb_bag", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.HERB_BAG));
    public static final DeferredItem<RightClickLootItem> CAN_OF_WORMS = ITEMS.register("can_of_worms", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CAN_OF_WORMS));
    public static final DeferredItem<RightClickLootItem> RED_ENVELOPE = ITEMS.register("red_envelope", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.RED_ENVELOPE));
    public static final DeferredItem<RightClickLootItem> SUGAR_TANGERINE = ITEMS.register("sugar_tangerine", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.SUGAR_TANGERINE));
    public static final DeferredItem<RightClickLootItem> DELUXE_PACKAGE = ITEMS.register("deluxe_package", () -> new RightClickLootItem(ModRarity.RED, ModLootTables.DELUXE_PACKAGE));
    public static final DeferredItem<RightClickLootItem> GOODIE_BAG = ITEMS.register("goodie_bag", () -> new RightClickLootItem(ModRarity.ORANGE, ModLootTables.GOODIE_GIFT));
    public static final DeferredItem<RightClickLootItem> CHRISTMAS_GIFT = ITEMS.register("christmas_gift", () -> new RightClickLootItem(ModRarity.BLUE, ModLootTables.CHRISTMAS_GIFT));

    public static final DeferredItem<ThrownPowderItem> PURIFICATION_POWDER = ITEMS.register("purification_powder", () -> new ThrownPowderItem(ISpreadable.Type.PURE));
    public static final DeferredItem<ThrownPowderItem> VILE_POWDER = ITEMS.register("vile_powder", () -> new ThrownPowderItem(ISpreadable.Type.CORRUPT));
    public static final DeferredItem<ThrownPowderItem> VICIOUS_POWDER = ITEMS.register("vicious_powder", () -> new ThrownPowderItem(ISpreadable.Type.CRIMSON));
    public static final DeferredItem<FertilizerItem> FERTILIZER = ITEMS.register("fertilizer", FertilizerItem::new);
    public static final DeferredItem<ModBoneMealItem> ROTTEN_BONE_DUST = ITEMS.register("rotten_bone_dust", () -> new ModBoneMealItem(ModRarity.BLUE, "rotten_bone_dust"));
    public static final DeferredItem<ModBoneMealItem> BLOODSTAINED_POWDER = ITEMS.register("bloodstained_powder", () -> new ModBoneMealItem(ModRarity.BLUE, "bloodstained_powder"));

    public static final DeferredItem<BossSummoningItem> SUSPICIOUS_LOOKING_EYE = ITEMS.register("suspicious_looking_eye", () -> new BossSummoningItem(player -> LibDateUtils.isNight(player.level()), EyeOfCthulhu::new, TooltipItem.getTooltipsFromString("suspicious_looking_eye", 3, ChatFormatting.RED)));
    public static final DeferredItem<BossSummoningItem> SLIME_CROWN = ITEMS.register("slime_crown", () -> new BossSummoningItem(player -> true, KingSlime::new, TooltipItem.getTooltipsFromString("slime_crown", 3, ChatFormatting.BLUE)));
    public static final DeferredItem<BossSummoningItem> WORM_FOOD = ITEMS.register("worm_food", () -> new BossSummoningItem(player -> player.level().getBiome(player.blockPosition()).is(ModTags.Biomes.THE_CORRUPTION), level -> new EaterOfWorlds(level, true), TooltipItem.getTooltipsFromString("worm_food", 3, ChatFormatting.DARK_PURPLE)));
    public static final DeferredItem<BossSummoningItem> BLOODY_SPINE = ITEMS.register("bloody_spine", () -> new BossSummoningItem(player -> player.level().getBiome(player.blockPosition()).is(ModTags.Biomes.THE_CRIMSON), BrainOfCthulhu::new, TooltipItem.getTooltipsFromString("bloody_spine", 3, ChatFormatting.RED)));
    public static final DeferredItem<BossSummoningItem> ABEEMINATION = ITEMS.register("abeemination", () -> new BossSummoningItem(player -> {
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        return biome.is(Tags.Biomes.IS_JUNGLE) || biome.is(Tags.Biomes.IS_LUSH);
    }, QueenBee::new, BossSummoningItem.getTooltipsFromString("abeemination", 4, ChatFormatting.YELLOW)));
    public static final DeferredItem<BossSummoningItem> DEER_THING = ITEMS.register("deer_thing", () -> new BossSummoningItem(player -> {
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        return biome.is(Tags.Biomes.IS_SNOWY) || biome.is(Tags.Biomes.IS_ICY);
    }, Deerclops::new, BossSummoningItem.getTooltipsFromString("deer_thing", 3, ChatFormatting.AQUA)));

    public static final DeferredItem<TooltipItem> GOLDEN_LOCK_BOX = ITEMS.register("golden_lock_box", () -> new TooltipItem(new Item.Properties().component(ModDataComponentTypes.LOOT.get(), new LootComponent(ModLootTables.GOLDEN_LOCK_BOX)), ModRarity.GREEN, TooltipItem.getTooltipsFromString("golden_lock_box", 2, ChatFormatting.GRAY)));
    public static final DeferredItem<TooltipItem> OBSIDIAN_LOCK_BOX = ITEMS.register("obsidian_lock_box", () -> new TooltipItem(new Item.Properties().component(ModDataComponentTypes.LOOT.get(), new LootComponent(ModLootTables.OBSIDIAN_LOCK_BOX)), ModRarity.GREEN, TooltipItem.getTooltipsFromString("obsidian_lock_box", 2, ChatFormatting.GRAY)));

    public static final DeferredItem<GameEventItem> BLOOD_TEAR = ITEMS.register("blood_tear", () -> new GameEventItem(new Item.Properties(), ModRarity.GREEN, TooltipItem.getTooltipsFromString("blood_tear", 2, ChatFormatting.GRAY), BloodMoonGameEvent.KEY));
    public static final DeferredItem<GameEventItem> GOBLIN_BATTLE_STANDARD = ITEMS.register("goblin_battle_standard", () -> new GameEventItem(new Item.Properties(), ModRarity.GREEN, TooltipItem.getTooltipsFromString("goblin_battle_standard", 1, ChatFormatting.GRAY), GoblinArmyGameEvent.KEY));


    private static DeferredItem<EverBeneficialItem> registerBeneficial(
            String name, int max, @Nullable Holder<Attribute> attr, double val,
            AttributeModifier.Operation op, Supplier<SoundEvent> sound,
            BiConsumer<LivingEntity, Boolean> effect, ModRarity rarity,
            int lines, ChatFormatting color) {
        return ITEMS.register(name, () -> new EverBeneficialItem(
                Confluence.asResource(name), max, attr, val, op, sound, effect,
                rarity, TooltipItem.getTooltipsFromString(name, lines, color)
        ));
    }

    private static DeferredItem<EverBeneficialItem> registerAttributeBeneficial(
            String name, int max, Holder<Attribute> attr, double val,
            Supplier<SoundEvent> sound, ModRarity rarity, ChatFormatting color) {
        return registerBeneficial(name, max, attr, val, AttributeModifier.Operation.ADD_VALUE,
                sound, (e, r) -> {}, rarity, 1, color);
    }

    private static DeferredItem<EverBeneficialItem> registerFunctionalBeneficial(
            String name, int max, Supplier<SoundEvent> sound, ModRarity rarity, ChatFormatting color) {
        return registerBeneficial(name, max, null, 0, AttributeModifier.Operation.ADD_VALUE,
                sound, (e, r) -> {}, rarity, 1, color);
    }
}
