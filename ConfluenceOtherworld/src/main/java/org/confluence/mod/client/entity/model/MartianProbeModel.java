package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.MartianProbe;

/// 火星探测器的资源映射与状态贴图。
///
/// 模型的 `bone4` 由三个同心立方体组成：两层负体积外壳（13 与 11.5 像素，法线朝内）包住
/// 最内侧 11 像素的外星大脑。渲染器检测到负体积立方体后会把渲染类型切换为剔除背面的
/// “外面模式”，外壳朝向相机的一面因此被剔除，于是能透过球壳看到里面的大脑。
public final class MartianProbeModel extends GeoNormalModel<MartianProbe> {
    private static final ResourceLocation ALERT_TEXTURE = Confluence.asResource("textures/entity/martian_probe_alert.png");

    public MartianProbeModel() {
        super(Confluence.asResource("martian_probe"), false);
    }

    /// 扫描阶段使用常态贴图，警觉与逃离阶段换成红色警报贴图。
    @Override
    public ResourceLocation getTextureResource(MartianProbe animatable) {
        return switch (animatable.getProbeState()) {
            case ALERT, FLEEING -> ALERT_TEXTURE;
            default -> super.getTextureResource(animatable);
        };
    }
}
