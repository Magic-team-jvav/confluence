package nowebsite.makertechno.the_trackers.core.config;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import nowebsite.makertechno.the_trackers.TheTrackers;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRenderCursor;
import nowebsite.makertechno.the_trackers.core.event.TModClient;
import nowebsite.makertechno.the_trackers.core.track.EntityTracker;
import nowebsite.makertechno.the_trackers.core.track.algorithm.ProjectAlgorithmLib;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@EventBusSubscriber(modid = TheTrackers.MOD_ID)
public class TConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /* Basic settings. */
    private static final ModConfigSpec.BooleanValue AVAILABLE = BUILDER
            .comment("Enable tracking pointer")
            .translation("the_trackers.configuration.available")
            .define("Available", true);
    private static final ModConfigSpec.DoubleValue GUI_SCALE = BUILDER
            .comment("Scale of cursor")
            .translation("the_trackers.configuration.gui_scale")
            .defineInRange("Scale",0.6, 0.01, 4);
    private static final ModConfigSpec.IntValue MAX_TRACK_QUANTITY = BUILDER
            .comment("Max tracking quantity")
            .translation("the_trackers.configuration.quantity")
            .defineInRange("Max quantity",10, 1, 400);
    private static final ModConfigSpec.IntValue REFRESH_POS_INTERVAL = BUILDER
            .comment("Interval between world position refresh")
            .translation("the_trackers.configuration.interval")
            .defineInRange("Interval", 1, 1, 160);

    /* HUD mode control. */
    private static final ModConfigSpec.BooleanValue CENTER_RELATIVE_AVAILABLE = BUILDER
            .comment("Enable center relative icon display")
            .translation("the_trackers.configuration.center_relative_available")
            .define("Center relative available", true);
    // Specific setting for center relative mode.
    public static final ModConfigSpec.EnumValue<ProjectAlgorithmLib.Type> PROJECT_ALGORITHM = BUILDER
            .comment("The algorithm of center rela projection")
            .translation("the_trackers.configuration.project_algorithm")
            .defineEnum("Project algorithm", ProjectAlgorithmLib.Type.AITOFF);

    private static final ModConfigSpec.BooleanValue TRACK_FULL_AVAILABLE = BUILDER
            .comment("Enable full track icon display")
            .translation("the_trackers.configuration.track_full_available")
            .define("Track full available", false);
    private static final ModConfigSpec.BooleanValue HEAD_FLAT_AVAILABLE = BUILDER
            .comment("Enable longitude icon display")
            .translation("the_trackers.configuration.head_flat_available")
            .define("Head flat available", false);


    public static final ModConfigSpec.ConfigValue<List<? extends String>> CENTER_RELATIVE_BIND = BUILDER
            .comment("List of entity types with cursors for center relative display, separated with \"|\"")
            .translation("the_trackers.configuration.center_relative_tracking")
            .defineList(
                    "EntityType|PointerIconType|EntityIconType(|OptionalValue)",
                    List.of(
                            "minecraft:ender_dragon|normal|ender_dragon_head",
                            "minecraft:wither|normal_white|wither_head",
                            "terra_entity:king_slime|normal|king_slime",
                            "terra_entity:eye_of_cthulhu|normal|eye_of_cthulhu",
                            "terra_entity:brain_of_cthulhu|normal|brain_of_cthulhu",
                            "terra_entity:eater_of_worlds|normal|eater_of_worlds",
                            "terra_entity:queen_bee|normal|queen_bee",
                            "terra_entity:deerclops|normal|deerclops",
                            "terra_entity:skeletron|normal|skeletron",
                            "terra_entity:hill_of_flesh|normal|wall_of_flesh",
                            "terra_entity:wall_of_flesh|normal|wall_of_flesh",


                            "terra_entity:demon_eye|normal|none",
                            "terra_entity:flying_fish|normal|none",
                            "terra_entity:crimera|normal|none",
                            "terra_entity:eater_of_souls|normal|none",
                            "terra_entity:giant_worm|normal|none",
                            "terra_entity:tomb_crawler|normal|none",
                            "terra_entity:devourer|normal|none",
                            "terra_entity:cave_bat|normal|none",
                            "terra_entity:jungle_bat|normal|none",
                            "terra_entity:snatcher|normal|none",
                            "terra_entity:man_eater|normal|none",
                            "terra_entity:hornet|normal|none",
                            "terra_entity:hell_bat|normal|none",
                            "terra_entity:ice_bat|normal|none",
                            "terra_entity:spore_bat|normal|none",
                            "terra_entity:harpy|normal|none",
                            "terra_entity:cursed_skull|normal|none",
                            "terra_entity:dark_caster|normal|none",
                            "terra_entity:antlion_swarmer|normal|none",
                            "terra_entity:giant_antlion_swarmer|normal|none",
                            "terra_entity:wyvern|normal|none",
                            "terra_entity:granite_elemental|normal|none",
                            "terra_entity:ghost|normal|none",
                            "terra_entity:fire_imp|normal|none",
                            "terra_entity:demon|normal|none",
                            "terra_entity:voodoo_demon|normal|none",
                            "terra_entity:bone_serpent|normal|none",
                            "terra_entity:wither_bone_serpent|normal|none",
                            "terra_entity:meteor_head|normal|none"
                    ),
                    () -> "minecraft:player|normal_green|none",
                    ConfigProcessor::isValidEntityBindCRCursor
            );

    public static final ModConfigSpec.ConfigValue<List<? extends String>> TRACK_FULL_BIND = BUILDER
            .comment("List of entity types with cursors for full track display, separated with \"|\"")
            .translation("the_trackers.configuration.track_full_tracking")
            .defineList(
                    "entityType|type:icon(|optionalPattern)",
                    List.of(
                        "minecraft:wither|normal:point_x"
                    ),
                () -> "minecraft:player|normal:normal_green",
                ConfigProcessor::isValidCREntityBindDTCursor
            );

    public static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean available;
    public static double scale;
    public static int maxTrackingQuantity;
    public static int interval;
    public static boolean centerRelativeAvailable;
    public static boolean trackFullAvailable;
    public static boolean headFlatAvailable;
    public static ProjectAlgorithmLib.Type projectAlgorithm;
    public static Set<Pair<EntityType<?>, Supplier<? extends TRenderCursor>>> CRCursorWithEntities;
    public static Set<Pair<EntityType<?>, Supplier<? extends TRenderCursor>>> DTCursorWithEntities;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        available = AVAILABLE.get();
        scale = GUI_SCALE.get() * 0.6;
        interval = REFRESH_POS_INTERVAL.get();
        maxTrackingQuantity = MAX_TRACK_QUANTITY.get();
        centerRelativeAvailable = CENTER_RELATIVE_AVAILABLE.get();
        trackFullAvailable = TRACK_FULL_AVAILABLE.get();
        headFlatAvailable = HEAD_FLAT_AVAILABLE.get();
        projectAlgorithm = PROJECT_ALGORITHM.get();
        if(TModClient.isLoaded) {
            CRCursorWithEntities = ConfigProcessor.collectCREntityBindCursor(CENTER_RELATIVE_BIND.get());
            DTCursorWithEntities = ConfigProcessor.collectDTEntityBindCursor(TRACK_FULL_BIND.get());
            EntityTracker.reCalcAllEntityGroups();
        }
        EntityTracker.getRENDERING().forEach((uuid, trackerEntityState) -> trackerEntityState.getComponent().flush());
    }

    @SubscribeEvent
    static void onFileChanged(final ModConfigEvent.Reloading event) {
        onLoad(event);
    }
}
