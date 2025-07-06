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
import org.confluence.mod.common.item.armor.NormalArmorItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<ArmorItem> MINING_HELMET = ITEMS.register("mining_helmet", () -> new NormalArmorItem("armor/mining_armor", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)) {
        private final boolean noneLoaded = !ModList.get().isLoaded("sodiumdynamiclights");

        @ParametersAreNonnullByDefault
        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (noneLoaded) tooltipComponents.add(Component.translatable("tooltip.terra_curio.requires_mod_loaded", "sodiumdynamiclights"));
        }
    });
    public static final DeferredItem<ArmorItem> MINING_CHESTPLATE = registerNormalArmor("mining_chestplate", "mining_armor", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> MINING_LEGGINGS = registerNormalArmor("mining_leggings", "mining_armor", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> MINING_BOOTS = registerNormalArmor("mining_boots", "mining_armor", ModArmorMaterials.MINING_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> PLANK_HELMET = registerNormalArmor("plank_helmet", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 55);
    public static final DeferredItem<ArmorItem> PLANK_CHESTPLATE = registerNormalArmor("plank_chestplate", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 80);
    public static final DeferredItem<ArmorItem> PLANK_LEGGINGS = registerNormalArmor("plank_leggings", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 75);
    public static final DeferredItem<ArmorItem> PLANK_BOOTS = registerNormalArmor("plank_boots", "plank_armor", ModArmorMaterials.PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 65);

    public static final DeferredItem<ArmorItem> EBONY_HELMET = registerNormalArmor("ebony_helmet", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<ArmorItem> EBONY_CHESTPLATE = registerNormalArmor("ebony_chestplate", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<ArmorItem> EBONY_LEGGINGS = registerNormalArmor("ebony_leggings", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<ArmorItem> EBONY_BOOTS = registerNormalArmor("ebony_boots", "ebony_armor", ModArmorMaterials.EBONY_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<ArmorItem> SHADOW_PLANK_HELMET = registerNormalArmor("shadow_plank_helmet", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<ArmorItem> SHADOW_PLANK_CHESTPLATE = registerNormalArmor("shadow_plank_chestplate", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<ArmorItem> SHADOW_PLANK_LEGGINGS = registerNormalArmor("shadow_plank_leggings", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<ArmorItem> SHADOW_PLANK_BOOTS = registerNormalArmor("shadow_plank_boots", "shadow_plank_armor", ModArmorMaterials.SHADOW_PLANK_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<ArmorItem> ASH_HELMET = registerNormalArmor("ash_helmet", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 220);
    public static final DeferredItem<ArmorItem> ASH_CHESTPLATE = registerNormalArmor("ash_chestplate", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final DeferredItem<ArmorItem> ASH_LEGGINGS = registerNormalArmor("ash_leggings", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final DeferredItem<ArmorItem> ASH_BOOTS = registerNormalArmor("ash_boots", "ash_armor", ModArmorMaterials.ASH_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 230);

    public static final DeferredItem<ArmorItem> RAIN_CAP = registerNormalArmor("rain_cap", "raincoat_armor", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> RAINCOAT = registerNormalArmor("raincoat", "raincoat_armor", ModArmorMaterials.RAINCOAT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);

    public static final DeferredItem<ArmorItem> SNOW_CAPS = registerNormalArmor("snow_caps", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> SNOW_SUITS = registerNormalArmor("snow_suits", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> INSULATED_PANTS = registerNormalArmor("insulated_pants", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> INSULATED_SHOES = registerNormalArmor("insulated_shoes", "snow_armor", ModArmorMaterials.SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> PINK_SNOW_CAPS = registerNormalArmor("pink_snow_caps", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> PINK_SNOW_SUITS = registerNormalArmor("pink_snow_suits", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> PINK_INSULATED_PANTS = registerNormalArmor("pink_insulated_pants", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> PINK_INSULATED_SHOES = registerNormalArmor("pink_insulated_shoes", "snow_pink_armor", ModArmorMaterials.PINK_SNOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> ANGLER_HAT = registerNormalArmor("angler_hat", "angler_armor", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> ANGLER_VEST = registerNormalArmor("angler_vest", "angler_armor", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> ANGLER_PANTS = registerNormalArmor("angler_pants", "angler_armor", ModArmorMaterials.ANGLER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);

    public static final DeferredItem<ArmorItem> CACTUS_HELMET = registerNormalArmor("cactus_helmet", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<ArmorItem> CACTUS_CHESTPLATE = registerNormalArmor("cactus_chestplate", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<ArmorItem> CACTUS_LEGGINGS = registerNormalArmor("cactus_leggings", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<ArmorItem> CACTUS_BOOTS = registerNormalArmor("cactus_boots", "cactus_armor", ModArmorMaterials.CACTUS_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<ArmorItem> COPPER_HELMET = registerNormalArmor("copper_helmet", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<ArmorItem> COPPER_CHESTPLATE = registerNormalArmor("copper_chestplate", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<ArmorItem> COPPER_LEGGINGS = registerNormalArmor("copper_leggings", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<ArmorItem> COPPER_BOOTS = registerNormalArmor("copper_boots", "copper_armor", ModArmorMaterials.COPPER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<ArmorItem> TIN_HELMET = registerNormalArmor("tin_helmet", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 150);
    public static final DeferredItem<ArmorItem> TIN_CHESTPLATE = registerNormalArmor("tin_chestplate", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 190);
    public static final DeferredItem<ArmorItem> TIN_LEGGINGS = registerNormalArmor("tin_leggings", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 170);
    public static final DeferredItem<ArmorItem> TIN_BOOTS = registerNormalArmor("tin_boots", "tin_armor", ModArmorMaterials.TIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 150);

    public static final DeferredItem<ArmorItem> PUMPKIN_HELMET = registerNormalArmor("pumpkin_helmet", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> PUMPKIN_CHESTPLATE = registerNormalArmor("pumpkin_chestplate", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> PUMPKIN_LEGGINGS = registerNormalArmor("pumpkin_leggings", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> PUMPKIN_BOOTS = registerNormalArmor("pumpkin_boots", "pumpkin_armor", ModArmorMaterials.PUMPKIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> NINJA_HELMET = registerNormalArmor("ninja_helmet", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> NINJA_CHESTPLATE = registerNormalArmor("ninja_chestplate", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> NINJA_LEGGINGS = registerNormalArmor("ninja_leggings", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> NINJA_BOOTS = registerNormalArmor("ninja_boots", "ninja_armor", ModArmorMaterials.NINJA_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> LEAD_HELMET = registerNormalArmor("lead_helmet", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 170);
    public static final DeferredItem<ArmorItem> LEAD_CHESTPLATE = registerNormalArmor("lead_chestplate", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 250);
    public static final DeferredItem<ArmorItem> LEAD_LEGGINGS = registerNormalArmor("lead_leggings", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 230);
    public static final DeferredItem<ArmorItem> LEAD_BOOTS = registerNormalArmor("lead_boots", "lead_armor", ModArmorMaterials.LEAD_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 160);

    public static final DeferredItem<ArmorItem> SILVER_HELMET = registerNormalArmor("silver_helmet", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 190);
    public static final DeferredItem<ArmorItem> SILVER_CHESTPLATE = registerNormalArmor("silver_chestplate", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 270);
    public static final DeferredItem<ArmorItem> SILVER_LEGGINGS = registerNormalArmor("silver_leggings", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 250);
    public static final DeferredItem<ArmorItem> SILVER_BOOTS = registerNormalArmor("silver_boots", "silver_armor", ModArmorMaterials.SILVER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 180);

    public static final DeferredItem<ArmorItem> TUNGSTEN_HELMET = registerNormalArmor("tungsten_helmet", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 210);
    public static final DeferredItem<ArmorItem> TUNGSTEN_CHESTPLATE = registerNormalArmor("tungsten_chestplate", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 290);
    public static final DeferredItem<ArmorItem> TUNGSTEN_LEGGINGS = registerNormalArmor("tungsten_leggings", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 270);
    public static final DeferredItem<ArmorItem> TUNGSTEN_BOOTS = registerNormalArmor("tungsten_boots", "tungsten_armor", ModArmorMaterials.TUNGSTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 200);

    public static final DeferredItem<ArmorItem> GOLDEN_HELMET = registerNormalArmor("golden_helmet", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 250);
    public static final DeferredItem<ArmorItem> GOLDEN_CHESTPLATE = registerNormalArmor("golden_chestplate", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 340);
    public static final DeferredItem<ArmorItem> GOLDEN_LEGGINGS = registerNormalArmor("golden_leggings", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 320);
    public static final DeferredItem<ArmorItem> GOLDEN_BOOTS = registerNormalArmor("golden_boots", "golden_armor", ModArmorMaterials.GOLDEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 260);

    public static final DeferredItem<ArmorItem> PLATINUM_HELMET = registerNormalArmor("platinum_helmet", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 250);
    public static final DeferredItem<ArmorItem> PLATINUM_CHESTPLATE = registerNormalArmor("platinum_chestplate", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 340);
    public static final DeferredItem<ArmorItem> PLATINUM_LEGGINGS = registerNormalArmor("platinum_leggings", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 320);
    public static final DeferredItem<ArmorItem> PLATINUM_BOOTS = registerNormalArmor("platinum_boots", "platinum_armor", ModArmorMaterials.PLATINUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 260);

    public static final DeferredItem<ArmorItem> FOSSIL_HELMET = registerNormalArmor("fossil_helmet", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> FOSSIL_CHESTPLATE = registerNormalArmor("fossil_chestplate", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> FOSSIL_LEGGINGS = registerNormalArmor("fossil_leggings", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> FOSSIL_BOOTS = registerNormalArmor("fossil_boots", "fossil_armor", ModArmorMaterials.FOSSIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> SPORE_ROOT_HELMET = registerNormalArmor("spore_root_helmet", "spore_root_armor", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> SPORE_ROOT_CHESTPLATE = registerNormalArmor("spore_root_chestplate", "spore_root_armor", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> SPORE_ROOT_LEGGINGS = registerNormalArmor("spore_root_leggings", "spore_root_armor", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> SPORE_ROOT_BOOTS = registerNormalArmor("spore_root_boots", "spore_root_armor", ModArmorMaterials.SPORE_ROOT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> COLD_CRYSTAL_HELMET = registerNormalArmor("cold_crystal_helmet", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> COLD_CRYSTAL_CHESTPLATE = registerNormalArmor("cold_crystal_chestplate", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> COLD_CRYSTAL_LEGGINGS = registerNormalArmor("cold_crystal_leggings", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> COLD_CRYSTAL_BOOTS = registerNormalArmor("cold_crystal_boots", "cold_crystal_armor", ModArmorMaterials.COLD_CRYSTAL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> HEIM_HELMET = registerNormalArmor("heim_helmet", "heim_armor", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> HEIM_CHESTPLATE = registerNormalArmor("heim_chestplate", "heim_armor", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> HEIM_LEGGINGS = registerNormalArmor("heim_leggings", "heim_armor", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> HEIM_BOOTS = registerNormalArmor("heim_boots", "heim_armor", ModArmorMaterials.HEIM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> BEE_HELMET = registerNormalArmor("bee_helmet", "bee_armor", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> BEE_CHESTPLATE = registerNormalArmor("bee_chestplate", "bee_armor", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> BEE_LEGGINGS = registerNormalArmor("bee_leggings", "bee_armor", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> BEE_BOOTS = registerNormalArmor("bee_boots", "bee_armor", ModArmorMaterials.BEE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> OBSIDIAN_HELMET = registerNormalArmor("obsidian_helmet", "obsidian_armor", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> OBSIDIAN_CHESTPLATE = registerNormalArmor("obsidian_chestplate", "obsidian_armor", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> OBSIDIAN_LEGGINGS = registerNormalArmor("obsidian_leggings", "obsidian_armor", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> OBSIDIAN_BOOTS = registerNormalArmor("obsidian_boots", "obsidian_armor", ModArmorMaterials.OBSIDIAN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> GLADIATOR_HELMET = registerNormalArmor("gladiator_helmet", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> GLADIATOR_CHESTPLATE = registerNormalArmor("gladiator_chestplate", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> GLADIATOR_LEGGINGS = registerNormalArmor("gladiator_leggings", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> GLADIATOR_BOOTS = registerNormalArmor("gladiator_boots", "gladiator_armor", ModArmorMaterials.GLADIATOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> METEOR_HELMET = registerNormalArmor("meteor_helmet", "meteor_armor", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> METEOR_CHESTPLATE = registerNormalArmor("meteor_chestplate", "meteor_armor", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> METEOR_LEGGINGS = registerNormalArmor("meteor_leggings", "meteor_armor", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> METEOR_BOOTS = registerNormalArmor("meteor_boots", "meteor_armor", ModArmorMaterials.METEOR_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> JUNGLE_HELMET = registerNormalArmor("jungle_helmet", "jungle_armor", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> JUNGLE_CHESTPLATE = registerNormalArmor("jungle_chestplate", "jungle_armor", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> JUNGLE_LEGGINGS = registerNormalArmor("jungle_leggings", "jungle_armor", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> JUNGLE_BOOTS = registerNormalArmor("jungle_boots", "jungle_armor", ModArmorMaterials.JUNGLE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    /* 远古钴套 */

    public static final DeferredItem<ArmorItem> NECRO_HELMET = registerNormalArmor("necro_helmet", "necro_armor", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> NECRO_CHESTPLATE = registerNormalArmor("necro_chestplate", "necro_armor", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> NECRO_LEGGINGS = registerNormalArmor("necro_leggings", "necro_armor", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> NECRO_BOOTS = registerNormalArmor("necro_boots", "necro_armor", ModArmorMaterials.NECRO_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    /* 远古暗影套 */

    public static final DeferredItem<ArmorItem> SHADOW_HELMET = registerNormalArmor("shadow_helmet", "shadow_armor", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> SHADOW_CHESTPLATE = registerNormalArmor("shadow_chestplate", "shadow_armor", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> SHADOW_LEGGINGS = registerNormalArmor("shadow_leggings", "shadow_armor", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> SHADOW_BOOTS = registerNormalArmor("shadow_boots", "shadow_armor", ModArmorMaterials.SHADOW_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> CRIMSON_HELMET = registerNormalArmor("crimson_helmet", "crimson_armor", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> CRIMSON_CHESTPLATE = registerNormalArmor("crimson_chestplate", "crimson_armor", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> CRIMSON_LEGGINGS = registerNormalArmor("crimson_leggings", "crimson_armor", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> CRIMSON_BOOTS = registerNormalArmor("crimson_boots", "crimson_armor", ModArmorMaterials.CRIMSON_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> MOLTEN_HELMET = registerNormalArmor("molten_helmet", "molten_armor", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> MOLTEN_CHESTPLATE = registerNormalArmor("molten_chestplate", "molten_armor", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> MOLTEN_LEGGINGS = registerNormalArmor("molten_leggings", "molten_armor", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> MOLTEN_BOOTS = registerNormalArmor("molten_boots", "molten_armor", ModArmorMaterials.MOLTEN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> PEARL_HELMET = registerNormalArmor("pearl_helmet", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.HELMET, 120);
    public static final DeferredItem<ArmorItem> PEARL_CHESTPLATE = registerNormalArmor("pearl_chestplate", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE, 170);
    public static final DeferredItem<ArmorItem> PEARL_LEGGINGS = registerNormalArmor("pearl_leggings", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS, 150);
    public static final DeferredItem<ArmorItem> PEARL_BOOTS = registerNormalArmor("pearl_boots", "pearl_armor", ModArmorMaterials.PEARL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<ArmorItem> SPIDER_HELMET = registerNormalArmor("spider_helmet", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> SPIDER_CHESTPLATE = registerNormalArmor("spider_chestplate", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> SPIDER_LEGGINGS = registerNormalArmor("spider_leggings", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> SPIDER_BOOTS = registerNormalArmor("spider_boots", "spider_armor", ModRarity.LIGHT_RED, ModArmorMaterials.SPIDER_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> COBALT_MASK = registerNormalArmor("cobalt_mask", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> COBALT_HAT = registerNormalArmor("cobalt_hat", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_HAT_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> COBALT_HELMET = registerNormalArmor("cobalt_helmet", "cobalt_helmet", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> COBALT_CHESTPLATE = registerNormalArmor("cobalt_chestplate", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> COBALT_LEGGINGS = registerNormalArmor("cobalt_leggings", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> COBALT_BOOTS = registerNormalArmor("cobalt_boots", "cobalt_armor", ModRarity.LIGHT_RED, ModArmorMaterials.COBALT_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> PALLADIUM_MASK = registerNormalArmor("palladium_mask", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> PALLADIUM_HEADGEAR = registerNormalArmor("palladium_headgear", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> PALLADIUM_HELMET = registerNormalArmor("palladium_helmet", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> PALLADIUM_CHESTPLATE = registerNormalArmor("palladium_chestplate", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> PALLADIUM_LEGGINGS = registerNormalArmor("palladium_leggings", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> PALLADIUM_BOOTS = registerNormalArmor("palladium_boots", "palladium_armor", ModRarity.PINK, ModArmorMaterials.PALLADIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> MYTHRIL_HOOD = registerNormalArmor("mythril_hood", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_HOOD_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> MYTHRIL_HAT = registerNormalArmor("mythril_hat", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_HAT_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> MYTHRIL_HELMET = registerNormalArmor("mythril_helmet", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> MYTHRIL_CHESTPLATE = registerNormalArmor("mythril_chestplate", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> MYTHRIL_LEGGINGS = registerNormalArmor("mythril_leggings", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> MYTHRIL_BOOTS = registerNormalArmor("mythril_boots", "mythril_armor", ModRarity.LIGHT_RED, ModArmorMaterials.MYTHRIL_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> ORICHALCUM_HEADGEAR = registerNormalArmor("orichalcum_headgear", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> ORICHALCUM_MASK = registerNormalArmor("orichalcum_mask", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> ORICHALCUM_HELMET = registerNormalArmor("orichalcum_helmet", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> ORICHALCUM_CHESTPLATE = registerNormalArmor("orichalcum_chestplate", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> ORICHALCUM_LEGGINGS = registerNormalArmor("orichalcum_leggings", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> ORICHALCUM_BOOTS = registerNormalArmor("orichalcum_boots", "orichalcum_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ORICHALCUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> ADAMANTITE_HEADGEAR = registerNormalArmor("adamantite_headgear", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> ADAMANTITE_MASK = registerNormalArmor("adamantite_mask", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> ADAMANTITE_HELMET = registerNormalArmor("adamantite_helmet", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> ADAMANTITE_CHESTPLATE = registerNormalArmor("adamantite_chestplate", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> ADAMANTITE_LEGGINGS = registerNormalArmor("adamantite_leggings", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> ADAMANTITE_BOOTS = registerNormalArmor("adamantite_boots", "adamantite_armor", ModRarity.LIGHT_RED, ModArmorMaterials.ADAMANTITE_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> TITANIUM_HEADGEAR = registerNormalArmor("titanium_headgear", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> TITANIUM_MASK = registerNormalArmor("titanium_mask", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> TITANIUM_HELMET = registerNormalArmor("titanium_helmet", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> TITANIUM_CHESTPLATE = registerNormalArmor("titanium_chestplate", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> TITANIUM_LEGGINGS = registerNormalArmor("titanium_leggings", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> TITANIUM_BOOTS = registerNormalArmor("titanium_boots", "titanium_armor", ModRarity.LIGHT_RED, ModArmorMaterials.TITANIUM_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> CRYSTAL_ASSASSIN_HELMET = registerNormalArmor("crystal_assassin_helmet", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> CRYSTAL_ASSASSIN_CHESTPLATE = registerNormalArmor("crystal_assassin_chestplate", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> CRYSTAL_ASSASSIN_LEGGINGS = registerNormalArmor("crystal_assassin_leggings", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> CRYSTAL_ASSASSIN_BOOTS = registerNormalArmor("crystal_assassin_boots", "crystal_assassin_armor", ModRarity.PINK, ModArmorMaterials.CRYSTAL_ASSASSIN_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> HALLOWED_MASK = registerNormalArmor("hallowed_mask", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_MASK_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> HALLOWED_HEADGEAR = registerNormalArmor("hallowed_headgear", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_HEADGEAR_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> HALLOWED_HOOD = registerNormalArmor("hallowed_hood", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_HOOD_MATERIAL, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> HALLOWED_HELMET = registerNormalArmor("hallowed_helmet", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> HALLOWED_CHESTPLATE = registerNormalArmor("hallowed_chestplate", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> HALLOWED_LEGGINGS = registerNormalArmor("hallowed_leggings", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> HALLOWED_BOOTS = registerNormalArmor("hallowed_boots", "hallowed_armor", ModRarity.PINK, ModArmorMaterials.HALLOWED_ARMOR_MATERIALS, ArmorItem.Type.BOOTS);

    // 巫师套装
    public static final DeferredItem<ArmorItem> WIZARD_HAT = ITEMS.register("wizard_hat", () -> new ArmorItem(ModArmorMaterials.WIZARD_HAT_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN)));
    public static final DeferredItem<ArmorItem> MAGIC_HAT = ITEMS.register("magic_hat", () -> new ArmorItem(ModArmorMaterials.MAGIC_HAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN)));
    public static final DeferredItem<ArmorItem> AMETHYST_ROBE = ITEMS.register("amethyst_robe", () -> new ArmorItem(ModArmorMaterials.AMETHYST_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE)));
    public static final DeferredItem<ArmorItem> TOPAZ_ROBE = ITEMS.register("topaz_robe", () -> new ArmorItem(ModArmorMaterials.TOPAZ_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE)));
    public static final DeferredItem<ArmorItem> SAPPHIRE_ROBE = ITEMS.register("sapphire_robe", () -> new ArmorItem(ModArmorMaterials.SAPPHIRE_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final DeferredItem<ArmorItem> JADE_ROBE = ITEMS.register("jade_robe", () -> new ArmorItem(ModArmorMaterials.EMERALD_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final DeferredItem<ArmorItem> RUBY_ROBE = ITEMS.register("ruby_robe", () -> new ArmorItem(ModArmorMaterials.RUBY_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final DeferredItem<ArmorItem> MYSTIC_ROBE = ITEMS.register("mystic_robe", () -> new ArmorItem(ModArmorMaterials.MYSTIC_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE)));
    public static final DeferredItem<ArmorItem> DIAMOND_ROBE = ITEMS.register("diamond_robe", () -> new ArmorItem(ModArmorMaterials.DIAMOND_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN)));
    public static final DeferredItem<ArmorItem> AMBER_ROBE = ITEMS.register("amber_robe", () -> new ArmorItem(ModArmorMaterials.AMBER_ROBE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.GREEN)));

    // 其它盔甲
    public static final DeferredItem<ArmorItem> GOGGLES = ITEMS.register("goggles", () -> new ArmorItem(ModArmorMaterials.GOGGLES_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE)));
    public static final DeferredItem<ArmorItem> GREEN_CAP = ITEMS.register("green_cap", () -> new ArmorItem(ModArmorMaterials.GREEN_CAP_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE)));
    public static final DeferredItem<ArmorItem> VIKING_HELMET = registerNormalArmor("viking_helmet","viking_armor", ModRarity.BLUE,ModArmorMaterials.VIKING_ARMOR_MATERIAL, ArmorItem.Type.HELMET);

    private static DeferredItem<ArmorItem> registerNormalArmor(String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type, int durability) {
        return ITEMS.register(name, () -> new NormalArmorItem("armor/" + geoName, material, type, new Item.Properties().stacksTo(1).durability(durability)));
    }

    private static DeferredItem<ArmorItem> registerNormalArmor(String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new NormalArmorItem("armor/" + geoName, material, type, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)));
    }

    private static DeferredItem<ArmorItem> registerNormalArmor(String name, String geoName, ModRarity rarity, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new NormalArmorItem("armor/" + geoName, rarity, material, type, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)));
    }

    public static void register(IEventBus eventBus) {
        ModArmorMaterials.ARMOR_MATERIALS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
