package org.confluence.mod.common.init.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModArmorMaterials;
import org.confluence.mod.common.item.armor.MultiHeadArmorItem;
import org.confluence.mod.common.item.armor.NormalArmorItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<ArmorItem> MINING_HELMET = ITEMS.register("mining_helmet", () -> new NormalArmorItem("armor/mining_armor", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PINK)) {
        private final boolean noneLoaded = !ModList.get().isLoaded("sodiumdynamiclights");

        @ParametersAreNonnullByDefault
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (noneLoaded) tooltipComponents.add(Component.translatable("tooltip.terra_curio.requires_mod_loaded", "sodiumdynamiclights"));
        }
    });
    public static final DeferredItem<NormalArmorItem> MINING_CHESTPLATE = registerNormalArmor("mining_chestplate", "mining_armor", ModRarity.PINK, ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> MINING_LEGGINGS = registerNormalArmor("mining_leggings", "mining_armor", ModRarity.PINK, ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> MINING_BOOTS = registerNormalArmor("mining_boots", "mining_armor", ModRarity.PINK, ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> PLANK_HELMET = registerNormalArmor("plank_helmet", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 55);
    public static final DeferredItem<NormalArmorItem> PLANK_CHESTPLATE = registerNormalArmor("plank_chestplate", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 80);
    public static final DeferredItem<NormalArmorItem> PLANK_LEGGINGS = registerNormalArmor("plank_leggings", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 75);
    public static final DeferredItem<NormalArmorItem> PLANK_BOOTS = registerNormalArmor("plank_boots", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 65);

    public static final DeferredItem<NormalArmorItem> EBONY_HELMET = registerNormalArmor("ebony_helmet", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<NormalArmorItem> EBONY_CHESTPLATE = registerNormalArmor("ebony_chestplate", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<NormalArmorItem> EBONY_LEGGINGS = registerNormalArmor("ebony_leggings", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<NormalArmorItem> EBONY_BOOTS = registerNormalArmor("ebony_boots", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<NormalArmorItem> SHADOW_PLANK_HELMET = registerNormalArmor("shadow_plank_helmet", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<NormalArmorItem> SHADOW_PLANK_CHESTPLATE = registerNormalArmor("shadow_plank_chestplate", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<NormalArmorItem> SHADOW_PLANK_LEGGINGS = registerNormalArmor("shadow_plank_leggings", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<NormalArmorItem> SHADOW_PLANK_BOOTS = registerNormalArmor("shadow_plank_boots", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<NormalArmorItem> ASH_HELMET = registerNormalArmor("ash_helmet", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 220);
    public static final DeferredItem<NormalArmorItem> ASH_CHESTPLATE = registerNormalArmor("ash_chestplate", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final DeferredItem<NormalArmorItem> ASH_LEGGINGS = registerNormalArmor("ash_leggings", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final DeferredItem<NormalArmorItem> ASH_BOOTS = registerNormalArmor("ash_boots", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 230);

    public static final DeferredItem<NormalArmorItem> RAIN_CAP = registerNormalArmor("rain_cap", "raincoat_armor", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> RAINCOAT = registerNormalArmor("raincoat", "raincoat_armor", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);

    public static final DeferredItem<NormalArmorItem> SNOW_CAPS = registerNormalArmor("snow_caps", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> SNOW_SUITS = registerNormalArmor("snow_suits", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> INSULATED_PANTS = registerNormalArmor("insulated_pants", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> INSULATED_SHOES = registerNormalArmor("insulated_shoes", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> PINK_SNOW_CAPS = registerNormalArmor("pink_snow_caps", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> PINK_SNOW_SUITS = registerNormalArmor("pink_snow_suits", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> PINK_INSULATED_PANTS = registerNormalArmor("pink_insulated_pants", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> PINK_INSULATED_SHOES = registerNormalArmor("pink_insulated_shoes", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> ANGLER_HAT = registerNormalArmor("angler_hat", "angler_armor", ModRarity.BLUE, ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> ANGLER_VEST = registerNormalArmor("angler_vest", "angler_armor", ModRarity.BLUE, ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> ANGLER_PANTS = registerNormalArmor("angler_pants", "angler_armor", ModRarity.BLUE, ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);

    public static final DeferredItem<NormalArmorItem> CACTUS_HELMET = registerNormalArmor("cactus_helmet", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<NormalArmorItem> CACTUS_CHESTPLATE = registerNormalArmor("cactus_chestplate", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<NormalArmorItem> CACTUS_LEGGINGS = registerNormalArmor("cactus_leggings", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<NormalArmorItem> CACTUS_BOOTS = registerNormalArmor("cactus_boots", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<NormalArmorItem> COPPER_HELMET = registerNormalArmor("copper_helmet", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<NormalArmorItem> COPPER_CHESTPLATE = registerNormalArmor("copper_chestplate", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<NormalArmorItem> COPPER_LEGGINGS = registerNormalArmor("copper_leggings", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<NormalArmorItem> COPPER_BOOTS = registerNormalArmor("copper_boots", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<NormalArmorItem> TIN_HELMET = registerNormalArmor("tin_helmet", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 150);
    public static final DeferredItem<NormalArmorItem> TIN_CHESTPLATE = registerNormalArmor("tin_chestplate", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 190);
    public static final DeferredItem<NormalArmorItem> TIN_LEGGINGS = registerNormalArmor("tin_leggings", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 170);
    public static final DeferredItem<NormalArmorItem> TIN_BOOTS = registerNormalArmor("tin_boots", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 150);

    public static final DeferredItem<NormalArmorItem> PUMPKIN_HELMET = registerNormalArmor("pumpkin_helmet", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> PUMPKIN_CHESTPLATE = registerNormalArmor("pumpkin_chestplate", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> PUMPKIN_LEGGINGS = registerNormalArmor("pumpkin_leggings", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> PUMPKIN_BOOTS = registerNormalArmor("pumpkin_boots", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> NINJA_HELMET = registerNormalArmor("ninja_helmet", "ninja_armor", ModRarity.BLUE, ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> NINJA_CHESTPLATE = registerNormalArmor("ninja_chestplate", "ninja_armor", ModRarity.BLUE, ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> NINJA_LEGGINGS = registerNormalArmor("ninja_leggings", "ninja_armor", ModRarity.BLUE, ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> NINJA_BOOTS = registerNormalArmor("ninja_boots", "ninja_armor", ModRarity.BLUE, ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> LEAD_HELMET = registerNormalArmor("lead_helmet", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 170);
    public static final DeferredItem<NormalArmorItem> LEAD_CHESTPLATE = registerNormalArmor("lead_chestplate", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 250);
    public static final DeferredItem<NormalArmorItem> LEAD_LEGGINGS = registerNormalArmor("lead_leggings", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 230);
    public static final DeferredItem<NormalArmorItem> LEAD_BOOTS = registerNormalArmor("lead_boots", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 160);

    public static final DeferredItem<NormalArmorItem> SILVER_HELMET = registerNormalArmor("silver_helmet", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 190);
    public static final DeferredItem<NormalArmorItem> SILVER_CHESTPLATE = registerNormalArmor("silver_chestplate", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final DeferredItem<NormalArmorItem> SILVER_LEGGINGS = registerNormalArmor("silver_leggings", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final DeferredItem<NormalArmorItem> SILVER_BOOTS = registerNormalArmor("silver_boots", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 180);

    public static final DeferredItem<NormalArmorItem> TUNGSTEN_HELMET = registerNormalArmor("tungsten_helmet", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 210);
    public static final DeferredItem<NormalArmorItem> TUNGSTEN_CHESTPLATE = registerNormalArmor("tungsten_chestplate", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 290);
    public static final DeferredItem<NormalArmorItem> TUNGSTEN_LEGGINGS = registerNormalArmor("tungsten_leggings", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 270);
    public static final DeferredItem<NormalArmorItem> TUNGSTEN_BOOTS = registerNormalArmor("tungsten_boots", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 200);

    public static final DeferredItem<NormalArmorItem> GOLDEN_HELMET = registerNormalArmor("golden_helmet", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 250);
    public static final DeferredItem<NormalArmorItem> GOLDEN_CHESTPLATE = registerNormalArmor("golden_chestplate", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 340);
    public static final DeferredItem<NormalArmorItem> GOLDEN_LEGGINGS = registerNormalArmor("golden_leggings", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 320);
    public static final DeferredItem<NormalArmorItem> GOLDEN_BOOTS = registerNormalArmor("golden_boots", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 260);

    public static final DeferredItem<NormalArmorItem> PLATINUM_HELMET = registerNormalArmor("platinum_helmet", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 250);
    public static final DeferredItem<NormalArmorItem> PLATINUM_CHESTPLATE = registerNormalArmor("platinum_chestplate", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 340);
    public static final DeferredItem<NormalArmorItem> PLATINUM_LEGGINGS = registerNormalArmor("platinum_leggings", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 320);
    public static final DeferredItem<NormalArmorItem> PLATINUM_BOOTS = registerNormalArmor("platinum_boots", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 260);

    public static final DeferredItem<NormalArmorItem> FOSSIL_HELMET = registerNormalArmor("fossil_helmet", "fossil_armor", ModRarity.BLUE, ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> FOSSIL_CHESTPLATE = registerNormalArmor("fossil_chestplate", "fossil_armor", ModRarity.BLUE, ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> FOSSIL_LEGGINGS = registerNormalArmor("fossil_leggings", "fossil_armor", ModRarity.BLUE, ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> FOSSIL_BOOTS = registerNormalArmor("fossil_boots", "fossil_armor", ModRarity.BLUE, ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> SPORE_ROOT_HELMET = registerNormalArmor("spore_root_helmet", "spore_root_armor", ModRarity.BLUE, ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> SPORE_ROOT_CHESTPLATE = registerNormalArmor("spore_root_chestplate", "spore_root_armor", ModRarity.BLUE, ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> SPORE_ROOT_LEGGINGS = registerNormalArmor("spore_root_leggings", "spore_root_armor", ModRarity.BLUE, ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> SPORE_ROOT_BOOTS = registerNormalArmor("spore_root_boots", "spore_root_armor", ModRarity.BLUE, ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> COLD_CRYSTAL_HELMET = registerNormalArmor("cold_crystal_helmet", "cold_crystal_armor", ModRarity.BLUE, ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> COLD_CRYSTAL_CHESTPLATE = registerNormalArmor("cold_crystal_chestplate", "cold_crystal_armor", ModRarity.BLUE, ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> COLD_CRYSTAL_LEGGINGS = registerNormalArmor("cold_crystal_leggings", "cold_crystal_armor", ModRarity.BLUE, ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> COLD_CRYSTAL_BOOTS = registerNormalArmor("cold_crystal_boots", "cold_crystal_armor", ModRarity.BLUE, ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> HEIM_HELMET = registerNormalArmor("heim_helmet", "heim_armor", ModRarity.BLUE, ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> HEIM_CHESTPLATE = registerNormalArmor("heim_chestplate", "heim_armor", ModRarity.BLUE, ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> HEIM_LEGGINGS = registerNormalArmor("heim_leggings", "heim_armor", ModRarity.BLUE, ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> HEIM_BOOTS = registerNormalArmor("heim_boots", "heim_armor", ModRarity.BLUE, ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> BEE_HELMET = registerNormalArmor("bee_helmet", "bee_armor", ModRarity.ORANGE, ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> BEE_CHESTPLATE = registerNormalArmor("bee_chestplate", "bee_armor", ModRarity.ORANGE, ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> BEE_LEGGINGS = registerNormalArmor("bee_leggings", "bee_armor", ModRarity.ORANGE, ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> BEE_BOOTS = registerNormalArmor("bee_boots", "bee_armor", ModRarity.ORANGE, ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> OBSIDIAN_HELMET = registerNormalArmor("obsidian_helmet", "obsidian_armor", ModRarity.BLUE, ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> OBSIDIAN_CHESTPLATE = registerNormalArmor("obsidian_chestplate", "obsidian_armor", ModRarity.BLUE, ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> OBSIDIAN_LEGGINGS = registerNormalArmor("obsidian_leggings", "obsidian_armor", ModRarity.BLUE, ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> OBSIDIAN_BOOTS = registerNormalArmor("obsidian_boots", "obsidian_armor", ModRarity.BLUE, ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> GLADIATOR_HELMET = registerNormalArmor("gladiator_helmet", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> GLADIATOR_CHESTPLATE = registerNormalArmor("gladiator_chestplate", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> GLADIATOR_LEGGINGS = registerNormalArmor("gladiator_leggings", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> GLADIATOR_BOOTS = registerNormalArmor("gladiator_boots", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> METEOR_HELMET = registerNormalArmor("meteor_helmet", "meteor_armor", ModRarity.BLUE, ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> METEOR_CHESTPLATE = registerNormalArmor("meteor_chestplate", "meteor_armor", ModRarity.BLUE, ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> METEOR_LEGGINGS = registerNormalArmor("meteor_leggings", "meteor_armor", ModRarity.BLUE, ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> METEOR_BOOTS = registerNormalArmor("meteor_boots", "meteor_armor", ModRarity.BLUE, ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> JUNGLE_HELMET = registerNormalArmor("jungle_helmet", "jungle_armor", ModRarity.ORANGE, ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> JUNGLE_CHESTPLATE = registerNormalArmor("jungle_chestplate", "jungle_armor", ModRarity.ORANGE, ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> JUNGLE_LEGGINGS = registerNormalArmor("jungle_leggings", "jungle_armor", ModRarity.ORANGE, ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> JUNGLE_BOOTS = registerNormalArmor("jungle_boots", "jungle_armor", ModRarity.ORANGE, ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> NECRO_HELMET = registerNormalArmor("necro_helmet", "necro_armor", ModRarity.GREEN, ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> NECRO_CHESTPLATE = registerNormalArmor("necro_chestplate", "necro_armor", ModRarity.GREEN, ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> NECRO_LEGGINGS = registerNormalArmor("necro_leggings", "necro_armor", ModRarity.GREEN, ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> NECRO_BOOTS = registerNormalArmor("necro_boots", "necro_armor", ModRarity.GREEN, ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> SHADOW_HELMET = registerNormalArmor("shadow_helmet", "shadow_armor", ModRarity.BLUE, ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> SHADOW_CHESTPLATE = registerNormalArmor("shadow_chestplate", "shadow_armor", ModRarity.BLUE, ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> SHADOW_LEGGINGS = registerNormalArmor("shadow_leggings", "shadow_armor", ModRarity.BLUE, ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> SHADOW_BOOTS = registerNormalArmor("shadow_boots", "shadow_armor", ModRarity.BLUE, ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> CRIMSON_HELMET = registerNormalArmor("crimson_helmet", "crimson_armor", ModRarity.BLUE, ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> CRIMSON_CHESTPLATE = registerNormalArmor("crimson_chestplate", "crimson_armor", ModRarity.BLUE, ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> CRIMSON_LEGGINGS = registerNormalArmor("crimson_leggings", "crimson_armor", ModRarity.BLUE, ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> CRIMSON_BOOTS = registerNormalArmor("crimson_boots", "crimson_armor", ModRarity.BLUE, ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> MOLTEN_HELMET = registerNormalArmor("molten_helmet", "molten_armor", ModRarity.ORANGE, ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> MOLTEN_CHESTPLATE = registerNormalArmor("molten_chestplate", "molten_armor", ModRarity.ORANGE, ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> MOLTEN_LEGGINGS = registerNormalArmor("molten_leggings", "molten_armor", ModRarity.ORANGE, ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> MOLTEN_BOOTS = registerNormalArmor("molten_boots", "molten_armor", ModRarity.ORANGE, ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> PEARL_HELMET = registerNormalArmor("pearl_helmet", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<NormalArmorItem> PEARL_CHESTPLATE = registerNormalArmor("pearl_chestplate", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<NormalArmorItem> PEARL_LEGGINGS = registerNormalArmor("pearl_leggings", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<NormalArmorItem> PEARL_BOOTS = registerNormalArmor("pearl_boots", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<NormalArmorItem> SPIDER_HELMET = registerNormalArmor("spider_helmet", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> SPIDER_CHESTPLATE = registerNormalArmor("spider_chestplate", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> SPIDER_LEGGINGS = registerNormalArmor("spider_leggings", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> SPIDER_BOOTS = registerNormalArmor("spider_boots", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<MultiHeadArmorItem> COBALT_MASK = registerMultiHeadArmor("cobalt_mask", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> COBALT_HAT = registerMultiHeadArmor("cobalt_hat", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_HAT_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> COBALT_HELMET = registerMultiHeadArmor("cobalt_helmet", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> COBALT_CHESTPLATE = registerMultiHeadArmor("cobalt_chestplate", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<MultiHeadArmorItem> COBALT_LEGGINGS = registerMultiHeadArmor("cobalt_leggings", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<MultiHeadArmorItem> COBALT_BOOTS = registerMultiHeadArmor("cobalt_boots", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<MultiHeadArmorItem> PALLADIUM_MASK = registerMultiHeadArmor("palladium_mask", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> PALLADIUM_HEADGEAR = registerMultiHeadArmor("palladium_headgear", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> PALLADIUM_HELMET = registerMultiHeadArmor("palladium_helmet", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> PALLADIUM_CHESTPLATE = registerMultiHeadArmor("palladium_chestplate", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<MultiHeadArmorItem> PALLADIUM_LEGGINGS = registerMultiHeadArmor("palladium_leggings", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<MultiHeadArmorItem> PALLADIUM_BOOTS = registerMultiHeadArmor("palladium_boots", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<MultiHeadArmorItem> MYTHRIL_HOOD = registerMultiHeadArmor("mythril_hood", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_HOOD_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> MYTHRIL_HAT = registerMultiHeadArmor("mythril_hat", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_HAT_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> MYTHRIL_HELMET = registerMultiHeadArmor("mythril_helmet", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> MYTHRIL_CHESTPLATE = registerMultiHeadArmor("mythril_chestplate", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<MultiHeadArmorItem> MYTHRIL_LEGGINGS = registerMultiHeadArmor("mythril_leggings", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<MultiHeadArmorItem> MYTHRIL_BOOTS = registerMultiHeadArmor("mythril_boots", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<MultiHeadArmorItem> ORICHALCUM_HEADGEAR = registerMultiHeadArmor("orichalcum_headgear", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> ORICHALCUM_MASK = registerMultiHeadArmor("orichalcum_mask", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> ORICHALCUM_HELMET = registerMultiHeadArmor("orichalcum_helmet", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> ORICHALCUM_CHESTPLATE = registerMultiHeadArmor("orichalcum_chestplate", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<MultiHeadArmorItem> ORICHALCUM_LEGGINGS = registerMultiHeadArmor("orichalcum_leggings", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<MultiHeadArmorItem> ORICHALCUM_BOOTS = registerMultiHeadArmor("orichalcum_boots", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<MultiHeadArmorItem> ADAMANTITE_HEADGEAR = registerMultiHeadArmor("adamantite_headgear", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> ADAMANTITE_MASK = registerMultiHeadArmor("adamantite_mask", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> ADAMANTITE_HELMET = registerMultiHeadArmor("adamantite_helmet", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> ADAMANTITE_CHESTPLATE = registerMultiHeadArmor("adamantite_chestplate", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<MultiHeadArmorItem> ADAMANTITE_LEGGINGS = registerMultiHeadArmor("adamantite_leggings", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<MultiHeadArmorItem> ADAMANTITE_BOOTS = registerMultiHeadArmor("adamantite_boots", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<MultiHeadArmorItem> TITANIUM_HEADGEAR = registerMultiHeadArmor("titanium_headgear", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> TITANIUM_MASK = registerMultiHeadArmor("titanium_mask", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> TITANIUM_HELMET = registerMultiHeadArmor("titanium_helmet", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> TITANIUM_CHESTPLATE = registerMultiHeadArmor("titanium_chestplate", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<MultiHeadArmorItem> TITANIUM_LEGGINGS = registerMultiHeadArmor("titanium_leggings", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<MultiHeadArmorItem> TITANIUM_BOOTS = registerMultiHeadArmor("titanium_boots", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<NormalArmorItem> CRYSTAL_ASSASSIN_HELMET = registerNormalArmor("crystal_assassin_helmet", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> CRYSTAL_ASSASSIN_CHESTPLATE = registerNormalArmor("crystal_assassin_chestplate", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> CRYSTAL_ASSASSIN_LEGGINGS = registerNormalArmor("crystal_assassin_leggings", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<NormalArmorItem> CRYSTAL_ASSASSIN_BOOTS = registerNormalArmor("crystal_assassin_boots", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<MultiHeadArmorItem> HALLOWED_MASK = registerMultiHeadArmor("hallowed_mask", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> HALLOWED_HEADGEAR = registerMultiHeadArmor("hallowed_headgear", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> HALLOWED_HOOD = registerMultiHeadArmor("hallowed_hood", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_HOOD_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> HALLOWED_HELMET = registerMultiHeadArmor("hallowed_helmet", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<MultiHeadArmorItem> HALLOWED_CHESTPLATE = registerMultiHeadArmor("hallowed_chestplate", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<MultiHeadArmorItem> HALLOWED_LEGGINGS = registerMultiHeadArmor("hallowed_leggings", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<MultiHeadArmorItem> HALLOWED_BOOTS = registerMultiHeadArmor("hallowed_boots", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    // 巫师套装
    public static final DeferredItem<NormalArmorItem> WIZARD_HAT = registerNormalArmor("wizard_hat", "wizard_hat", ModRarity.GREEN, ModArmorMaterials.WIZARD_HAT_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> MAGIC_HAT = registerNormalArmor("magic_hat", "magic_hat", ModRarity.GREEN, ModArmorMaterials.MAGIC_HAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> AMETHYST_ROBE = registerNormalArmor("amethyst_robe", "amethyst_robe", ModRarity.WHITE, ModArmorMaterials.AMETHYST_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> TOPAZ_ROBE = registerNormalArmor("topaz_robe", "topaz_robe", ModRarity.WHITE, ModArmorMaterials.TOPAZ_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> SAPPHIRE_ROBE = registerNormalArmor("sapphire_robe", "sapphire_robe", ModRarity.BLUE, ModArmorMaterials.SAPPHIRE_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> JADE_ROBE = registerNormalArmor("jade_robe", "jade_robe", ModRarity.BLUE, ModArmorMaterials.EMERALD_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> RUBY_ROBE = registerNormalArmor("ruby_robe", "ruby_robe", ModRarity.BLUE, ModArmorMaterials.RUBY_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> MYSTIC_ROBE = registerNormalArmor("mystic_robe", "mystic_robe", ModRarity.BLUE, ModArmorMaterials.MYSTIC_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> DIAMOND_ROBE = registerNormalArmor("diamond_robe", "diamond_robe", ModRarity.GREEN, ModArmorMaterials.DIAMOND_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<NormalArmorItem> AMBER_ROBE = registerNormalArmor("amber_robe", "amber_robe", ModRarity.GREEN, ModArmorMaterials.AMBER_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE);
    // 其它盔甲
    public static final DeferredItem<ArmorItem> GOGGLES = ITEMS.register("goggles", () -> new ArmorItem(ModArmorMaterials.GOGGLES_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE)));
    public static final DeferredItem<ArmorItem> GREEN_CAP = ITEMS.register("green_cap", () -> new ArmorItem(ModArmorMaterials.GREEN_CAP_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE)));
    public static final DeferredItem<NormalArmorItem> VIKING_HELMET = registerNormalArmor("viking_helmet","viking_armor", ModRarity.BLUE,ModArmorMaterials.VIKING_ARMOR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<NormalArmorItem> FLINX_FUR_COAT = registerNormalArmor("flinx_fur_coat","flinx_fur_coat_armor", ModRarity.GREEN,ModArmorMaterials.FLINX_FUR_COAT_MATERIAL, ArmorItem.Type.CHESTPLATE);

    private static DeferredItem<NormalArmorItem> registerNormalArmor(String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type, int durability) {
        return ITEMS.register(name, () -> new NormalArmorItem("armor/" + geoName, material, type, new Item.Properties().stacksTo(1).durability(durability)));
    }

    private static DeferredItem<NormalArmorItem> registerNormalArmor(String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new NormalArmorItem("armor/" + geoName, material, type, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)));
    }

    private static DeferredItem<NormalArmorItem> registerNormalArmor(String name, String geoName, ModRarity rarity, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new NormalArmorItem("armor/" + geoName, rarity, material, type, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)));
    }

    private static DeferredItem<MultiHeadArmorItem> registerMultiHeadArmor(String name, String geoName, ModRarity rarity, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new MultiHeadArmorItem("armor/" + geoName, rarity, material, type, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)));
    }

    public static void register(IEventBus eventBus) {
        ModArmorMaterials.ARMOR_MATERIALS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
