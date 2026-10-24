package org.confluence.mod.client.handler.soulcerer;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.gui.container.soulcerer.SoulOverviewScreen;
import org.confluence.mod.client.gui.hud.soulcerer.CurrentSelectedSkillHud;
import org.confluence.mod.client.util.soulcerer.SoulQuickSkillHudUtils;
import org.confluence.mod.client.gui.hud.soulcerer.quick_skill.BasicSoulQuickSkillHud;
import org.confluence.mod.client.gui.hud.soulcerer.quick_skill.CardHorizontalHud;
import org.confluence.mod.client.gui.hud.soulcerer.quick_skill.RouletteWheelBigHud;
import org.confluence.mod.client.gui.hud.soulcerer.quick_skill.RouletteWheelSmallHud;
import org.confluence.mod.common.attachment.SoulSkillStorage;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.item.soulcerer.SoulArtifactItem;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.network.c2s.SoulSkillIndexRequestPacketC2S;
import org.confluence.mod.network.c2s.SoulSkillRequestPacketC2S;
import org.confluence.mod.network.c2s.SoulSkillRequestPacketC2S.Action;
import org.confluence.mod.network.c2s.SoulSkillUsePacketC2S;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;

/// 客户端类，服务端请勿调用
public final class ClientSoulHandler {
    public static final RouletteWheelBigHud ROULETTE_WHEEL_BIG_HUD_INSTANCE = new RouletteWheelBigHud();
    public static final RouletteWheelSmallHud ROULETTE_WHEEL_SMALL_HUD_INSTANCE = new RouletteWheelSmallHud();
    public static final CardHorizontalHud CARD_HORIZONTAL_L_HUD_INSTANCE = new CardHorizontalHud(false);
    public static final CardHorizontalHud CARD_HORIZONTAL_R_HUD_INSTANCE = new CardHorizontalHud(true);
    public static final CurrentSelectedSkillHud CURRENT_SELECTED_SKILL_HUD_INSTANCE = new CurrentSelectedSkillHud();

    public static boolean active = false;
    private static boolean wasSpellWheelDown = false;

    private ClientSoulHandler() {}

    /// 释放静态 HUD 实例对旧 LocalPlayer 的引用。
    public static void clearHudPlayers() {
        ROULETTE_WHEEL_BIG_HUD_INSTANCE.clearPlayer();
        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.clearPlayer();
        CARD_HORIZONTAL_L_HUD_INSTANCE.clearPlayer();
        CARD_HORIZONTAL_R_HUD_INSTANCE.clearPlayer();
        CURRENT_SELECTED_SKILL_HUD_INSTANCE.clearPlayer();
    }

    /// 处理魂师快捷技能输入和技能总览打开请求。
    public static void handleSoulSkills(Minecraft minecraft) {
        Player player = minecraft.player;
        if (player != null && !minecraft.isPaused()) {
            for (SoulSkillStack stack : SoulSkillStorage.of(player).getSkillCooldowns().values()) {
                if (stack.getCd() > 0) {
                    stack.setCd(stack.getCd() - 1);
                }
            }
            for (SoulSkillStack stack : PlayerUtils.getEquippedSoulSkills(player)) {
                if (!stack.isEmpty()) {
                    stack.setCd(PlayerUtils.getSoulSkillCooldown(player, stack.getSoulSkill().getId()));
                    stack.setMaxCd(PlayerUtils.getSoulSkillMaxCooldown(player, stack.getSoulSkill().getId()));
                }
            }
        }
        if (!PlayerUtils.isHoldingSoulWeapon(player)) {
            closeSoulSkillHuds();
        }
        handleSoulSkills();
        boolean isSoulOverviewScreen = false;
        while (ModKeyBindings.SOUL_OVERVIEW.get().consumeClick()) {
            if (!isSoulOverviewScreen) {
                isSoulOverviewScreen = true;
            }
        }
        if (isSoulOverviewScreen) {
            minecraft.setScreen(new SoulOverviewScreen());
        }
    }

    /// 按当前玩家的可装备槽数初始化技能槽位。
    public static void initSoulSkills() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PlayerUtils.updateSoulSkillSlots(player);
    }

    /// 刷新各类快捷技能 HUD 和当前选中技能的显示。
    public static void updateSoulSkillHuds() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ROULETTE_WHEEL_BIG_HUD_INSTANCE.update();
        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.update();
        CARD_HORIZONTAL_L_HUD_INSTANCE.update();
        CARD_HORIZONTAL_R_HUD_INSTANCE.update();
        CURRENT_SELECTED_SKILL_HUD_INSTANCE.updateSkill(PlayerUtils.getCurrentSkillStack(player));
    }

    /// 收到技能快照后刷新 HUD 和已打开的技能总览。
    public static void onSoulSkillSync() {
        updateSoulSkillHuds();
        if (Minecraft.getInstance().screen instanceof SoulOverviewScreen screen) {
            screen.rebuildHotbar();
        }
    }

    /// 向服务端请求使用当前技能，等待成功通知后再执行。
    public static void requestSoulSkillUse() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PortPacketDistributor.sendToServer(new SoulSkillUsePacketC2S());
    }

    /// 数字键选中快捷法器槽后，沿原版物品交互请求使用。
    public static void useQuickSoulArtifact() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || minecraft.screen != null || minecraft.getOverlay() != null
                || player.isSpectator() || player.isUsingItem() || player.isHandsBusy() || minecraft.gameMode.isDestroying()) {
            return;
        }

        int slot = player.getInventory().selected;
        ItemStack stack = player.getMainHandItem();
        if (slot < 0 || slot >= SoulSkillParameters.SOUL_ARTIFACT_QUICK_SLOT_COUNT
                || !(stack.getItem() instanceof SoulArtifactItem) || !stack.isItemEnabled(player.level().enabledFeatures())) {
            return;
        }

        InteractionResult result = minecraft.gameMode.useItem(player, InteractionHand.MAIN_HAND);
        if (result.consumesAction()) {
            if (result.shouldSwing()) {
                player.swing(InteractionHand.MAIN_HAND);
            }
            minecraft.gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.MAIN_HAND);
        }
    }

    /// 按服务端成功通知还原施法技能栈，并执行客户端效果。
    public static void onSoulSkillUse(Player localPlayer, int entityId, ResourceLocation skillId, int cd, int maxCd) {
        if (!(localPlayer.level().getEntity(entityId) instanceof Player caster)) {
            return;
        }

        SoulSkill skill = SoulSkillStorage.findSoulSkill(skillId);
        if (skill == null) {
            return;
        }

        SoulSkillStack stack = skill.getStack();
        if (stack.isEmpty()) {
            return;
        }

        stack.setCd(cd);
        stack.setMaxCd(maxCd);
        skill.use(caster, stack);
    }

    /// 处理快捷技能面板按下与松开时的状态切换。
    public static void handleSoulSkills() {
        KeyMapping mapping = getSoulSkillKeyMapping();
        if (mapping == null) {
            return;
        }
        while (mapping.consumeClick()) {
            if (!wasSpellWheelDown) {
                openSoulSkillHuds();
            }
        }

        handleSoulSkillWheelRelease(mapping);
    }

    /// 在支持滚轮的快捷面板中切换当前技能。
    public static boolean scrollSoulSkills(double scrollDeltaY) {
        if (ClientConfigs.soulQuickSkillStyle == ClientSoulHandler.Type.ROULETTE_WHEEL_BIG) {
            return false;
        }
        if (!active) {
            return false;
        }
        adjustSoulSkillTarget((int) scrollDeltaY);
        return true;
    }

    /// 更新快捷面板的选择动画，并切换当前技能槽位。
    ///
    /// 切换时跳过空槽与被动技能槽（两者都不能释放，停在上面只会让施法键无效）；
    /// 一个可切槽都没有时退化为不跳过，高亮照常移动。
    /// 动画与落点用同一份「实际滑过的槽位数」，避免跳格后画面与选中不同步。
    public static void adjustSoulSkillTarget(int offset) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        int maxNumber = PlayerUtils.getEquippedSoulSkillMaxNumber(player);
        int currentIndex = PlayerUtils.getCurrentSoulSkillIndex(player);
        int effectiveOffset = SoulQuickSkillHudUtils.calculateSkippingOffset(offset, currentIndex, maxNumber,
                index -> {
                    SoulSkillStack stack = PlayerUtils.getSkillStack(player, index);
                    return stack.isEmpty() || stack.getSoulSkill().isPassive();
                });
        if (effectiveOffset == 0) {
            return;
        }

        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.adjustTarget(effectiveOffset);
        CARD_HORIZONTAL_L_HUD_INSTANCE.adjustTarget(effectiveOffset);
        CARD_HORIZONTAL_R_HUD_INSTANCE.adjustTarget(effectiveOffset);
        setCurrentSoulSkillIndex(SoulQuickSkillHudUtils.calculateSkillIndex(effectiveOffset, currentIndex, maxNumber));
    }

    /// 检测快捷选择键松开，关闭已打开的技能面板。
    private static void handleSoulSkillWheelRelease(KeyMapping mapping) {
        final boolean isDown = mapping.isDown();

        final boolean wasReleased = wasSpellWheelDown && !isDown;
        if (wasReleased) {
            closeSoulSkillHuds();
        }

        wasSpellWheelDown = isDown;
    }

    /// 读取打开灵魂快捷技能面板使用的按键绑定（「切换灵魂技能」，默认左 Alt）。
    private static @Nullable KeyMapping getSoulSkillKeyMapping() {
        return ModKeyBindings.MAGIC_SKILL_SWITCHING == null ? null : ModKeyBindings.MAGIC_SKILL_SWITCHING.get();
    }

    /// 标记快捷选择为开启，并通知各样式 HUD 打开。
    private static void openSoulSkillHuds() {
        if (!PlayerUtils.isHoldingSoulWeapon(Minecraft.getInstance().player)) {
            return;
        }
        active = true;
        openSoulSkillHud(ROULETTE_WHEEL_BIG_HUD_INSTANCE);
        openSoulSkillHud(ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
        openSoulSkillHud(CARD_HORIZONTAL_L_HUD_INSTANCE);
        openSoulSkillHud(CARD_HORIZONTAL_R_HUD_INSTANCE);
    }

    /// 结束快捷选择，并通知各样式 HUD 关闭。
    private static void closeSoulSkillHuds() {
        active = false;
        closeSoulSkillHud(ROULETTE_WHEEL_BIG_HUD_INSTANCE);
        closeSoulSkillHud(ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
        closeSoulSkillHud(CARD_HORIZONTAL_L_HUD_INSTANCE);
        closeSoulSkillHud(CARD_HORIZONTAL_R_HUD_INSTANCE);
    }

    /// 打开指定样式的快捷技能 HUD。
    private static void openSoulSkillHud(BasicSoulQuickSkillHud hud) {
        hud.open();
    }

    /// 关闭处于开启状态的快捷技能 HUD。
    private static void closeSoulSkillHud(BasicSoulQuickSkillHud hud) {
        if (!hud.isActive()) {
            return;
        }
        hud.close();
    }

    /// 先更新本地槽数与 HUD，再发送服务端槽数设置请求。
    public static void setEquippedSoulSkillMaxNumber(int equippedSkillMaxNumber) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PlayerUtils.setEquippedSoulSkillMaxNumber(player, equippedSkillMaxNumber);
        PlayerUtils.updateSoulSkillSlots(player);
        updateSoulSkillHuds();
        sendSoulSkillRequest(Action.SET_SLOT_COUNT, PlayerUtils.getEquippedSoulSkillMaxNumber(player), SoulSkillStack.Empty);
    }

    /// 先更新本地选择与 HUD，再发送服务端选择请求。
    public static void setCurrentSoulSkillIndex(int currentIndex) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PlayerUtils.setCurrentSoulSkillIndex(player, currentIndex);
        updateSoulSkillHuds();
        PortPacketDistributor.sendToServer(new SoulSkillIndexRequestPacketC2S(PlayerUtils.getCurrentSoulSkillIndex(player)));
    }

    /// 本地装备成功后刷新 HUD，并发送服务端装备请求。
    public static boolean equipSoulSkill(int index, SoulSkillStack skillStack) {
        Player player = Minecraft.getInstance().player;
        if (player == null || (!skillStack.isEmpty() && !PlayerUtils.isSoulSkillAvailable(player, skillStack.getSoulSkill().getId()))
                || !PlayerUtils.equipSoulSkill(player, index, skillStack)) {
            return false;
        }

        PlayerUtils.updateSoulSkillSlots(player);
        updateSoulSkillHuds();
        sendSoulSkillRequest(Action.EQUIP, index, skillStack);
        return true;
    }

    /// 本地卸下成功后刷新 HUD，并发送服务端卸下请求。
    public static boolean unequipSoulSkill(int index) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !PlayerUtils.unequipSoulSkill(player, index)) {
            return false;
        }

        PlayerUtils.updateSoulSkillSlots(player);
        updateSoulSkillHuds();
        sendSoulSkillRequest(Action.UNEQUIP, index, SoulSkillStack.Empty);
        return true;
    }

    /// 本地替换槽位成功后刷新 HUD，并发送服务端替换请求。
    public static boolean setSoulSkillStack(int index, SoulSkillStack skillStack) {
        Player player = Minecraft.getInstance().player;
        if (player == null || (!skillStack.isEmpty() && !PlayerUtils.isSoulSkillAvailable(player, skillStack.getSoulSkill().getId()))
                || !PlayerUtils.setSoulSkillStack(player, index, skillStack)) {
            return false;
        }

        PlayerUtils.updateSoulSkillSlots(player);
        updateSoulSkillHuds();
        sendSoulSkillRequest(Action.SET_STACK, index, skillStack);
        return true;
    }

    /// 发送技能操作请求；空技能统一使用 EMPTY 标识。
    private static void sendSoulSkillRequest(Action action, int index, SoulSkillStack skillStack) {
        if (skillStack.isEmpty()) {
            PortPacketDistributor.sendToServer(new SoulSkillRequestPacketC2S(action, index, ModSoulSkills.EMPTY.get().getId(), 0, 0));
            return;
        }

        PortPacketDistributor.sendToServer(new SoulSkillRequestPacketC2S(action, index, skillStack.getSoulSkill().getId(), skillStack.getCd(), skillStack.getMaxCd()));
    }

    public enum Type {
        ROULETTE_WHEEL_BIG,
        ROULETTE_WHEEL_SMALL,
        CARD_HORIZONTAL_L,
        CARD_HORIZONTAL_R
    }
}

