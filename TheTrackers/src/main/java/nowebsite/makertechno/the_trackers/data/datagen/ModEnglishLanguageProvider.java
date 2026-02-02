package nowebsite.makertechno.the_trackers.data.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.data.LanguageProvider;
import nowebsite.makertechno.the_trackers.TheTrackers;

public class ModEnglishLanguageProvider extends LanguageProvider {
    public ModEnglishLanguageProvider(PackOutput packOutput, String locale) {
        super(packOutput, TheTrackers.MOD_ID, locale);
    }

    protected void addTranslations() {
        addConfigurationTitleTranslation("The Trackers' Settings");
        addConfigurationTomlTranslation(
                ModConfig.Type.CLIENT,
                "The Trackers",
                "The Trackers Configuration"
        );

        addConfigurationTranslation("available", "Available", "Enable or disable the automatic tracking process");


        addConfigurationTranslation(
                "gui_scale",
                "The scale of tracking cursors",
                "Usually 0.6 on 1080p monitors"
        );
        addConfigurationTranslation(
                "quantity",
                "The max quantity of tracking",
                "Always shows those \"the top N closest\" to you"
        );
        addConfigurationTranslation(
                "interval",
                "Interval between world pos update",
                "Choose 1 by default, provides the fastest reaction"
        );


        addConfigurationTranslation(
                "center_relative_available",
                "Center-relative hud available",
                "Enable or disable center-relative cursors"
        );
        addConfigurationTranslation(
                "project_algorithm",
                "Project Algorithm",
                "Set the algorithm specific for the center-relative pointer"
        );
        addConfigurationTranslation(
                "track_full_available",
                "Full screen tag hud available",
                "Enable or disable full screen tag cursors"
        );
        addConfigurationTranslation(
                "head_flat_available",
                "Longitude hud available",
                "Enable or disable longitude display"
        );

        addConfigurationButtonTranslation(
                "center_relative_tracking",
                "Tracking settings for center-relative cursors",
                "Settings for tracking entities",
                "List of entity types with cursor types, separated with \"|\""
        );
        addConfigurationButtonTranslation(
                "track_full_tracking",
                "Tracking settings for full-track cursors",
                "Settings for tracking entities",
                "List of entity types with cursor types, separated with \"|\""
        );

    }
    public void addConfigurationTitleTranslation(String description) {
        add(TheTrackers.MOD_ID + ".configuration.title", description);
    }
    public void addConfigurationTomlTranslation(ModConfig.Type type, String name, String title) {
        add(TheTrackers.MOD_ID + ".configuration.the.trackers." + type.extension() + ".toml", name);
        add(TheTrackers.MOD_ID + ".configuration.the.trackers." + type.extension() + ".toml.title", title);
    }
    public void addConfigurationTranslation(String id, String description, String tooltip) {
        add(TheTrackers.MOD_ID + ".configuration." + id, description);
        add(TheTrackers.MOD_ID + ".configuration." + id + ".tooltip", tooltip);
    }
    public void addConfigurationButtonTranslation(String id, String description, String button, String tooltip) {
        add(TheTrackers.MOD_ID + ".configuration." + id, description);
        add(TheTrackers.MOD_ID + ".configuration." + id + ".button", button);
        add(TheTrackers.MOD_ID + ".configuration." + id + ".tooltip", tooltip);
    }
}
