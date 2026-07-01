package org.confluence.mod.common.data.gen.angler;

import org.confluence.mod.common.data.saved.AnglerQuestEntry;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.AndCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.BiomeCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.PositionHeightCondition;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.QuestedFishes;
import org.confluence.mod.util.OverworldUtils;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.ArrayList;
import java.util.List;

public class AnglerQuestProvider {

    public static List<AnglerQuestEntry> buildEntries() {
        List<AnglerQuestEntry> entries = new ArrayList<>();

        TradeCondition forest   = BiomeCondition.of(PortTags.Biomes.IS_FOREST);
        TradeCondition jungle   = BiomeCondition.of(PortTags.Biomes.IS_JUNGLE, PortTags.Biomes.IS_LUSH);
        TradeCondition desert   = BiomeCondition.of(PortTags.Biomes.IS_DESERT);
        TradeCondition ocean    = BiomeCondition.of(PortTags.Biomes.IS_OCEAN, PortTags.Biomes.IS_BEACH);
        TradeCondition snowy    = BiomeCondition.of(PortTags.Biomes.IS_SNOWY, PortTags.Biomes.IS_ICY);
        TradeCondition corruption = BiomeCondition.of(ModTags.Biomes.THE_CORRUPTION);
        TradeCondition crimson  = BiomeCondition.of(ModTags.Biomes.THE_CRIMSON);
        TradeCondition hallow   = BiomeCondition.of(ModTags.Biomes.THE_HALLOW);
        TradeCondition mushroom = BiomeCondition.of(PortTags.Biomes.IS_MUSHROOM);
        TradeCondition notEvil  = corruption.or(crimson).or(hallow).not();

        TradeCondition surface    = pos(OverworldUtils.getSurfaceY(), OverworldUtils.getSpaceY());
        TradeCondition cave       = pos(OverworldUtils.getCaveY(), OverworldUtils.getSurfaceY());
        TradeCondition underground = pos(OverworldUtils.getCaveY(), OverworldUtils.getUndergroundY());
        TradeCondition sky        = pos(OverworldUtils.getSpaceY(), OverworldUtils.getUltraY());
        TradeCondition surfaceSky = pos(OverworldUtils.getSurfaceY(), OverworldUtils.getUltraY());

        e(entries, QuestedFishes.SLIMEFISH, forest, surface);
        e(entries, QuestedFishes.ZOMBIE_FISH, forest, surface);
        e(entries, QuestedFishes.BUNNYFISH, forest, surface);
        e(entries, QuestedFishes.DYNAMITE_FISH, notEvil, surface);
        e(entries, QuestedFishes.ANGELFISH, notEvil, sky);
        e(entries, QuestedFishes.CLOUDFISH, notEvil, sky);
        e(entries, QuestedFishes.WYVERNTAIL, notEvil, sky);
        e(entries, QuestedFishes.FALLEN_STARFISH, sky, surfaceSky);
        e(entries, QuestedFishes.THE_FISH_OF_CTHULHU, notEvil, surfaceSky);
        e(entries, QuestedFishes.HARPYFISH, notEvil, surfaceSky);
        e(entries, QuestedFishes.BATFISH, notEvil, cave);
        e(entries, QuestedFishes.BONEFISH, notEvil, cave);
        e(entries, QuestedFishes.JEWELFISH, notEvil, cave);
        e(entries, QuestedFishes.SPIDERFISH, notEvil, cave);
        e(entries, QuestedFishes.DIRTFISH, notEvil, pos(OverworldUtils.getUndergroundY(), OverworldUtils.getSpaceY()));
        e(entries, QuestedFishes.DEMONIC_HELLFISH, notEvil, underground);
        e(entries, QuestedFishes.FISHOTRON, notEvil, underground);
        e(entries, QuestedFishes.GUIDE_VOODOO_FISH, notEvil, underground);
        e(entries, QuestedFishes.HUNGERFISH, notEvil, underground);
        e(entries, QuestedFishes.CATFISH, jungle, surface);
        e(entries, QuestedFishes.DERPFISH, jungle, surface);
        e(entries, QuestedFishes.TROPICAL_BARRACUDA, jungle, surface);
        e(entries, QuestedFishes.MUDFISH, jungle);
        e(entries, QuestedFishes.SCARAB_FISH, desert);
        e(entries, QuestedFishes.SCORPIO_FISH, desert);
        e(entries, QuestedFishes.CAPN_TUNABEARD, ocean, surfaceSky);
        e(entries, QuestedFishes.CLOWNFISH, ocean, surfaceSky);
        e(entries, QuestedFishes.PENGFISH, snowy, surface);
        e(entries, QuestedFishes.TUNDRA_TROUT, snowy, surface);
        e(entries, QuestedFishes.FISHRON, snowy, cave);
        e(entries, QuestedFishes.MUTANT_FLINXFIN, snowy, cave);
        e(entries, QuestedFishes.EATER_OF_PLANKTON, corruption);
        e(entries, QuestedFishes.INFECTED_SCABBARDFISH, corruption);
        e(entries, QuestedFishes.CURSEDFISH, corruption);
        e(entries, QuestedFishes.BLOODY_MANOWAR, crimson);
        e(entries, QuestedFishes.ICHORFISH, crimson);
        e(entries, QuestedFishes.PIXIEFISH, hallow, surfaceSky);
        e(entries, QuestedFishes.UNICORN_FISH, hallow, surface);
        e(entries, QuestedFishes.MIRAGE_FISH, hallow, cave);
        e(entries, QuestedFishes.AMANITA_FUNGIFIN, mushroom);

        return entries;
    }

    private static PositionHeightCondition pos(int minY, int maxY) {
        return new PositionHeightCondition(minY, maxY);
    }

    private static void e(List<AnglerQuestEntry> entries, Object item, TradeCondition... conditions) {
        TradeCondition c = conditions.length == 1 ? conditions[0] : new AndCondition(conditions[0], conditions[1]);
        entries.add(new AnglerQuestEntry(((org.mesdag.portlib.registries.PortDeferredItem<?>) item).toStack(), c));
    }
}
