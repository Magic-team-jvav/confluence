package org.confluence.mod.client.gui.hud.soulcerer.quick_skill;

import org.mesdag.portlib.client.PortDeltaTicker;
import net.minecraft.client.gui.GuiGraphics;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.BasicHudLayer;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.util.PlayerUtils;

public abstract class BasicSoulQuickSkillHud extends BasicHudLayer {
    protected boolean active = false;
    protected boolean isInit;

    public BasicSoulQuickSkillHud() {
        super();
    }

    /// 准备技能显示数据并渲染快捷栏。
    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        if (!PlayerUtils.isHoldingSoulWeapon(getMinecraft().player)) {
            return;
        }
        if (!isInit){
            update();
        }
        super.render(guiGraphics, deltaTracker);
    }

    /// 标记快捷栏处于打开状态。
    public void open() {
        active = true;
    }

    /// 关闭快捷栏显示。
    public void close() {
        active = false;
    }

    /// 判断快捷栏是否处于打开状态。
    public boolean isActive() {
        return active;
    }

    /// 刷新当前样式使用的技能显示数据。
    public abstract void update();

    /// 提供当前快捷栏对应的配置样式。
    public abstract ClientSoulHandler.Type getType();

    /// 判断当前快捷栏是否为配置选中的样式。
    public boolean isType() {
        return ClientConfigs.soulQuickSkillStyle == getType();
    }
}

