package org.confluence.mod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.advancement.ModTriggers;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.worldgen.feature.ModFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Confluence.MODID)
public class Confluence {
    public static final String MODID = "confluence";
    public static final Logger LOGGER = LoggerFactory.getLogger("Confluence");
    public static GameRules.Key<GameRules.IntegerValue> SPREADABLE_CHANCE;

    public Confluence(IEventBus eventBus, ModContainer container) {
        CommonConfigs.register(container);
        ClientConfigs.register(container);
        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModVillagers.register(eventBus);
        ModFluids.initialize();
        ModTriggers.TYPES.register(eventBus);
        ModTabs.TABS.register(eventBus);
        ModEntities.ENTITIES.register(eventBus);
        ModDataComponentTypes.DATA_COMPONENT_TYPE.register(eventBus);
        ModSoundEvents.SOUND_EVENT.register(eventBus);
        ModAttachments.TYPES.register(eventBus);
        ModEffects.EFFECTS.register(eventBus);
        ModFeatures.FEATURES.register(eventBus);
        ModJukeboxSongs.SONGS.register(eventBus);
        ModAttributes.ATTRIBUTES.register(eventBus);
    }

    public static void registerGameRules() {
        SPREADABLE_CHANCE = GameRules.register("confluenceSpreadableChance", GameRules.Category.MISC, GameRules.IntegerValue.create(10));
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
