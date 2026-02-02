package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.client.gui.team.TeamRender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    @Unique
    private TeamRender mine_team$teamRender;

    public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (MineTeam.IS_CONFLUENCE_LOADED) return;
        this.mine_team$teamRender = new TeamRender(this);
        mine_team$teamRender.initButton();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (MineTeam.IS_CONFLUENCE_LOADED) return;
        mine_team$teamRender.renderTeamIcon(guiGraphics, mouseX, mouseY, partialTick);
    }
}
