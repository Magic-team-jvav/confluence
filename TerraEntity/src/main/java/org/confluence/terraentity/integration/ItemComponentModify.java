package org.confluence.terraentity.integration;

import net.minecraft.core.component.DataComponentPatch;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TEWhipItems;

import java.util.function.Consumer;

public class ItemComponentModify {

    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        Consumer<DataComponentPatch.Builder> orange = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.ORANGE);
        Consumer<DataComponentPatch.Builder> blue = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.BLUE);
        Consumer<DataComponentPatch.Builder> purple = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.PURPLE);
        Consumer<DataComponentPatch.Builder> yellow = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.YELLOW);
        Consumer<DataComponentPatch.Builder> green = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.GREEN);
        Consumer<DataComponentPatch.Builder> red = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.RED);
        Consumer<DataComponentPatch.Builder> light_red = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.LIGHT_RED);
        Consumer<DataComponentPatch.Builder> master = builder -> builder.set(ConfluenceMagicLib.MOD_RARITY.get(), ModRarity.MASTER);

        // 鞭子
        event.modify(TEWhipItems.SLUB_WHIP.get(), blue);
        event.modify(TEWhipItems.LEATHER_WHIP.get(), blue);
        event.modify(TEWhipItems.RUBY_WHIP.get(), blue);
        event.modify(TEWhipItems.AMBER_WHIP.get(), blue);
        event.modify(TEWhipItems.TOPAZ_WHIP.get(), blue);
        event.modify(TEWhipItems.JADE_WHIP.get(), blue);
        event.modify(TEWhipItems.DIAMOND_WHIP.get(), blue);
        event.modify(TEWhipItems.SAPPHIRE_WHIP.get(), blue);
        event.modify(TEWhipItems.AMETHYST_WHIP.get(), blue);
        event.modify(TEWhipItems.SWAMP_WHIP.get(), orange);
        event.modify(TEWhipItems.SNAPTHORN.get(), orange);
        event.modify(TEWhipItems.SPINAL_TAP.get(), green);
        event.modify(TEWhipItems.FIRECRACKER.get(), light_red);

        // 回旋镖
        event.modify(TEBoomerangItems.WOOD_BOOMERANG.get(), blue);
        event.modify(TEBoomerangItems.ENCHANTED_BOOMERANG.get(), blue);
        event.modify(TEBoomerangItems.SHROOMERANG.get(), blue);
        event.modify(TEBoomerangItems.ICE_BOOMERANG.get(), blue);
        event.modify(TEBoomerangItems.TRIMARANG.get(), green);
        event.modify(TEBoomerangItems.FLAMARANG.get(), orange);
        event.modify(TEBoomerangItems.COMBAT_WRENCH.get(), orange);

        event.modify(TEBoomerangItems.BeiDou_BOOMERANG.get(), master);
        event.modify(TEBoomerangItems.DEVELOPER_BOOMERANG.get(), master);

    }
}