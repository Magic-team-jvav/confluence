package nowebsite.makertechno.the_trackers.data.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.data.LanguageProvider;
import nowebsite.makertechno.the_trackers.TheTrackers;

public class ModChineseLanguageProvider extends LanguageProvider {
    public ModChineseLanguageProvider(PackOutput packOutput, String locale) {
        super(packOutput, TheTrackers.MOD_ID, locale);
    }

    protected void addTranslations() {
        addConfigurationTitleTranslation("追踪器设置");
        addConfigurationTomlTranslation(
                ModConfig.Type.CLIENT,
                "The Trackers",
                "The Trackers 配置"
        );

        addConfigurationTranslation("available", "开关", "启用或禁用追踪指针");

        addConfigurationTranslation("gui_scale",  "追踪指针大小", "默认值0.6，这在一般1080p设备上表现很好");
        addConfigurationTranslation("quantity", "最大追踪数量", "总是显示离你\"最近的N个\"实体");
        addConfigurationTranslation("interval", "位置刷新间隔", "默认为1的情况下，提供最实时的指示");

        addConfigurationTranslation("center_relative_available", "中心环绕式指针开关", "启用或禁用中心环绕式跟踪");
        addConfigurationTranslation(
                "project_algorithm",
                "投影算法",
                """
                设置特定于"中心环绕型"指针的算法
                MERCATOR: 墨卡托投影
                AITOFF: 艾托夫投影
                WINKEL_TRIPLE: 温克尔三角投影
                """
        );
        addConfigurationTranslation("track_full_available", "锁定定位式指针开关", "启用或禁用锁定定位式指针");
        addConfigurationTranslation("head_flat_available", "经度轴开关", "启用或禁用经度轴条");

        addConfigurationButtonTranslation(
                "center_relative_tracking",
                "中心环绕式跟踪清单",
                "跟踪实体的设置",
                "带有光标类型的实体类型列表，用\"|\"分隔"
        );

        addConfigurationButtonTranslation(
                "track_full_tracking",
                "绑定定位式跟踪清单",
                "跟踪实体的设置",
                "带有光标类型的实体类型列表，用\"|\"分隔"
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
