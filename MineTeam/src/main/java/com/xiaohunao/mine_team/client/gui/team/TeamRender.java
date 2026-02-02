package com.xiaohunao.mine_team.client.gui.team;

import com.google.common.collect.Maps;
import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.network.TeamAttachmentSyncPayload;
import com.xiaohunao.mine_team.common.team.Team;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeamRender {
    private final EffectRenderingInventoryScreen<? extends AbstractContainerMenu> screen;

    private ImageButton teamIcon;
    private ImageButton teamPVPOn;
    private ImageButton teamPVPOff;
    private final Map<String, ImageButton> teamSmallIcons = Maps.newHashMap();

    public TeamRender(EffectRenderingInventoryScreen<? extends AbstractContainerMenu> screen) {
        this.screen = screen;
    }

    public void renderTeamIcon(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (teamIcon == null || teamPVPOff == null || teamPVPOn == null || teamSmallIcons.isEmpty()) {
            return;
        }
        this.teamIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOff.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOn.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTeamSmallIcon(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderTeamSmallIcon(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (ImageButton button : teamSmallIcons.values()) {
            button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    public void initButton() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        TeamManager teamManager = TeamManager.of(localPlayer.level());

        TeamAttachment teamAttachment = TeamAttachment.of(localPlayer);
        Team team = teamManager.getTeam(teamAttachment.getTeamUid());
        DyeColor dyeColor = teamManager.getDyeColor(team);
        if (dyeColor != null) {
            String teamColor = dyeColor.getName();
            int iconSize = 16;
            int off = 6;
            this.teamIcon = new ImageButton(screen.leftPos - iconSize, screen.topPos, iconSize, iconSize, createWidgetSprites("team/" + teamColor + "_team_icon"), button -> {
                this.teamIcon.visible = false;
                this.teamPVPOn.visible = false;
                this.teamPVPOff.visible = false;
                visibleTeamSmallIcon(true);
            });

            this.teamPVPOff = new ImageButton(screen.leftPos - iconSize, screen.topPos + iconSize + off, iconSize, iconSize,
                    createWidgetSprites("team/pvp/" + teamColor + "_pvp_off"),
                    button -> setTeamPvP(localPlayer, true));
            this.teamPVPOn = new ImageButton(screen.leftPos - iconSize, screen.topPos + iconSize + off, iconSize, iconSize,
                    createWidgetSprites("team/pvp/" + teamColor + "_pvp_on"),
                    button -> setTeamPvP(localPlayer, false));
            initSmallIcon(localPlayer);
            hasEnableTeamPvP(localPlayer);
            addRenderableWidget();
        }
    }

    private void initSmallIcon(LocalPlayer localPlayer) {
        List<String> teamColors = Arrays.stream(DyeColor.values())
                .map(DyeColor::getName)
                .toList().reversed();

        int size = 8;
        int firstOff = MineTeam.IS_CONFLUENCE_LOADED ? 22 : 0;
        for (int i = 0; i < teamColors.size(); i++) {
            String newTeamColor = teamColors.get(i);
            int x = screen.leftPos - size - (i / 8) * size - (i / 8) * 2;
            int y = screen.topPos + (i % 8) * size + (i % 8) * 2;

            ImageButton teamSmallIconBtn = new ImageButton(x, y + firstOff, size, size, createWidgetSprites("team/small/" + newTeamColor + "_team_small_icon"),
                    button -> teamSmallIconButtonPressed(localPlayer, newTeamColor));
            teamSmallIconBtn.visible = false;
            teamSmallIcons.put(newTeamColor, teamSmallIconBtn);
        }
    }

    public void addRenderableWidget() {
        screen.addRenderableWidget(this.teamIcon);
        screen.addRenderableWidget(this.teamPVPOn);
        screen.addRenderableWidget(this.teamPVPOff);
        for (ImageButton button : teamSmallIcons.values()) {
            screen.addRenderableWidget(button);
        }
    }

    private void teamSmallIconButtonPressed(LocalPlayer localPlayer, String teamColor) {
        setTeamColor(localPlayer, teamColor);
        this.teamIcon.visible = true;
        visibleTeamSmallIcon(false);
        hasEnableTeamPvP(localPlayer);
    }

    private void setTeamColor(LocalPlayer localPlayer, String teamColor) {
        TeamAttachment.of(localPlayer).setTeamUid(TeamManager.of(localPlayer.clientLevel).getTeam(DyeColor.valueOf(teamColor.toUpperCase(Locale.ROOT))).getUid());
        TeamAttachmentSyncPayload.sendToServer(localPlayer);
        setImageButtonSprites(this.teamIcon, "team/" + teamColor + "_team_icon");
        setImageButtonSprites(this.teamPVPOn, "team/pvp/" + teamColor + "_pvp_on");
        setImageButtonSprites(this.teamPVPOff, "team/pvp/" + teamColor + "_pvp_off");
    }

    public void setTeamPvP(LocalPlayer localPlayer, boolean friendlyFire) {
        TeamAttachment.of(localPlayer).setCanPvP(friendlyFire);
        TeamAttachmentSyncPayload.sendToServer(localPlayer);
        this.teamPVPOn.visible = friendlyFire;
        this.teamPVPOff.visible = !friendlyFire;
    }

    private void hasEnableTeamPvP(LocalPlayer localPlayer) {
        boolean teamPvP = TeamAttachment.of(localPlayer).isCanPvP();
        this.teamPVPOn.visible = teamPvP;
        this.teamPVPOff.visible = !teamPvP;
    }

    private void visibleTeamSmallIcon(boolean visible) {
        for (ImageButton button : teamSmallIcons.values()) {
            button.visible = visible;
        }
    }

    private void setImageButtonSprites(ImageButton button, String path) {
        button.sprites = createWidgetSprites(path);
    }

    private WidgetSprites createWidgetSprites(String path) {
        return new WidgetSprites(MineTeam.asResource(path), MineTeam.asResource(path));
    }
}
