package org.confluence.mod.client.handler.soul;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.gui.container.soul.SoulOverviewScreen;
import org.confluence.mod.client.gui.hud.soul.CurrentSelectedSkillHud;
import org.confluence.mod.client.gui.hud.soul.quick_skill.BasicSoulQuickSkillHud;
import org.confluence.mod.client.gui.hud.soul.quick_skill.CardHorizontalHud;
import org.confluence.mod.client.gui.hud.soul.quick_skill.RouletteWheelBigHud;
import org.confluence.mod.client.gui.hud.soul.quick_skill.RouletteWheelSmallHud;
import org.confluence.mod.client.util.soul.SoulQuickSkillHudUtils;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.item.soul.SoulArtifactItem;
import org.confluence.mod.common.soul.skill.SoulSkillStack;
import org.confluence.mod.network.c2s.SoulSkillRequestPacketC2S;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// 客户端类，服务端请勿调用
public final class ClientSoulHandler {
    public static final ClientSoulHandler INSTANCE = new ClientSoulHandler();
    public static final RouletteWheelBigHud ROULETTE_WHEEL_BIG_HUD_INSTANCE = new RouletteWheelBigHud();
    public static final RouletteWheelSmallHud ROULETTE_WHEEL_SMALL_HUD_INSTANCE = new RouletteWheelSmallHud();
    public static final CardHorizontalHud CARD_HORIZONTAL_L_HUD_INSTANCE = new CardHorizontalHud(false);
    public static final CardHorizontalHud CARD_HORIZONTAL_R_HUD_INSTANCE = new CardHorizontalHud(true);
    public static final CurrentSelectedSkillHud CURRENT_SELECTED_SKILL_HUD_INSTANCE = new CurrentSelectedSkillHud();

    public boolean active = false;
    private boolean wasSpellWheelDown = false;
    private int targetCurrentIndex;

    public void init() {
        allUpdate();
    }

    public void allUpdate() {
        if (!active) {
            targetCurrentIndex = PlayerUtils.getCurrentIndex(Minecraft.getInstance().player);
        }

        ROULETTE_WHEEL_BIG_HUD_INSTANCE.update();
        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.update();
        CARD_HORIZONTAL_L_HUD_INSTANCE.update();
        CARD_HORIZONTAL_R_HUD_INSTANCE.update();
        CURRENT_SELECTED_SKILL_HUD_INSTANCE.updateSkill(PlayerUtils.getCurrentSkill(Minecraft.getInstance().player));
    }

    public static void tryUseSoulArtifact(int slot) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (minecraft.gameMode == null) {
            return;
        }
        if (minecraft.screen != null) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        if (player.isUsingItem()) {
            return;
        }
        if (player.isHandsBusy()) {
            return;
        }
        if (minecraft.gameMode.isDestroying()) {
            return;
        }
        if (slot < 0 || slot >= PlayerUtils.SOUL_ARTIFACT_QUICK_SLOT_COUNT) {
            return;
        }
        if (player.getInventory().selected != slot) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SoulArtifactItem) || PlayerUtils.isSoulArtifactOnCooldown(stack)) {
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

    public void handle(Minecraft minecraft) {
        if (active && !PlayerUtils.isHoldingSoulWeapon(minecraft.player)) {
            allClose();
        }
        while (getKeyMapping().consumeClick()) {
            if (!wasSpellWheelDown) {
                allOpen();
            }
        }

        handleSpellWheelRelease();
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

    public boolean scrolling(double scrollDeltaY) {
        if (ClientConfigs.soulQuickSkillStyle == ClientSoulHandler.Type.ROULETTE_WHEEL_BIG) {
            return false;
        }
        if (!active) {
            return false;
        }
        adjustTarget((int) scrollDeltaY);
        return true;
    }

    public void adjustTarget(int offset) {
        ROULETTE_WHEEL_SMALL_HUD_INSTANCE.adjustTarget(offset);
        CARD_HORIZONTAL_L_HUD_INSTANCE.adjustTarget(offset);
        CARD_HORIZONTAL_R_HUD_INSTANCE.adjustTarget(offset);
        setCurrentIndex(SoulQuickSkillHudUtils.calculateSkillIndex(offset, targetCurrentIndex, PlayerUtils.getEquippedSkillMaxNumber(Minecraft.getInstance().player)));
    }

    private void handleSpellWheelRelease() {
        final boolean isDown = getKeyMapping().isDown();

        final boolean wasReleased = wasSpellWheelDown && !isDown;
        if (wasReleased) {
            allClose();
        }

        wasSpellWheelDown = isDown;
    }

    private @NotNull KeyMapping getKeyMapping() {
        return ModKeyBindings.MAGIC_QUICK_SKILL_SWITCHING.get();
    }

    private void allOpen() {
        if (!PlayerUtils.isHoldingSoulWeapon(Minecraft.getInstance().player)) {
            return;
        }
        targetCurrentIndex = PlayerUtils.getCurrentIndex(Minecraft.getInstance().player);
        active = true;
        open(ROULETTE_WHEEL_BIG_HUD_INSTANCE);
        open(ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
        open(CARD_HORIZONTAL_L_HUD_INSTANCE);
        open(CARD_HORIZONTAL_R_HUD_INSTANCE);
    }

    private void allClose() {
        active = false;
        close(ROULETTE_WHEEL_BIG_HUD_INSTANCE);
        close(ROULETTE_WHEEL_SMALL_HUD_INSTANCE);
        close(CARD_HORIZONTAL_L_HUD_INSTANCE);
        close(CARD_HORIZONTAL_R_HUD_INSTANCE);
    }

    private void open(BasicSoulQuickSkillHud hud) {
        hud.open();
    }

    private void close(BasicSoulQuickSkillHud hud) {
        if (!hud.isActive()) {
            return;
        }
        hud.close();
    }

    public void setCurrentIndex(int currentIndex) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (currentIndex < 0) {
            return;
        }
        if (currentIndex >= PlayerUtils.getEquippedSkillMaxNumber(player)) {
            return;
        }
        targetCurrentIndex = currentIndex;
        PacketDistributor.sendToServer(new SoulSkillRequestPacketC2S(SoulSkillRequestPacketC2S.ACTION_SELECT, currentIndex, ModSoulSkills.EMPTY.get().getId()));
    }

    public boolean equipSkill(int index, @Nullable SoulSkillStack skillStack) {
        if (SoulSkillStack.isEmpty(skillStack)) {
            return unequipSkill(index);
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        if (index < 0) {
            return false;
        }
        if (index >= PlayerUtils.getEquippedSkillMaxNumber(player)) {
            return false;
        }
        if (!PlayerUtils.isSoulSkillAvailable(player, skillStack.getSoulSkill().getId())) {
            return false;
        }
        PacketDistributor.sendToServer(new SoulSkillRequestPacketC2S(SoulSkillRequestPacketC2S.ACTION_EQUIP, index, skillStack.getSoulSkill().getId()));
        return true;
    }

    public boolean unequipSkill(int index) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        if (index < 0) {
            return false;
        }
        if (index >= PlayerUtils.getEquippedSkillMaxNumber(player)) {
            return false;
        }
        PacketDistributor.sendToServer(new SoulSkillRequestPacketC2S(SoulSkillRequestPacketC2S.ACTION_UNEQUIP, index, ModSoulSkills.EMPTY.get().getId()));
        return true;
    }

    public boolean setSoulSkillStack(int index, @Nullable SoulSkillStack skillStack) {
        return equipSkill(index, skillStack);
    }

    public enum Type {
        ROULETTE_WHEEL_BIG,
        ROULETTE_WHEEL_SMALL,
        CARD_HORIZONTAL_L,
        CARD_HORIZONTAL_R
    }
}
