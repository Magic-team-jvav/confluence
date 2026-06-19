package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WormholeHandler;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.network.c2s.WormholeRequestPlayerDataPacketC2S;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;
import org.confluence.mod.network.s2c.WormholePlayerDataSyncPacketS2C;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public class WormholeScreen extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = Confluence.asResource("textures/gui/container/wormhole.png");
    private static final Entry[] ENTRIES = new Entry[4];
    private static final List<Pair<WormholePlayerDataSyncPacketS2C.Data, PlayerInfo>> PLAYER_LIST = new ArrayList<>();
    public static final Map<ResourceLocation, ResourceLocation> DIMENSION_TEXTURES = new HashMap<>();
    public static final WormholeScreen INSTANCE = new WormholeScreen();
    public static final int TEXTURE_WIDTH = 192;
    public static final int TEXTURE_HEIGHT = 169;
    public static final int WIDTH = TEXTURE_WIDTH;
    public static final int HEIGHT = 106;

    static {
        for (int i = 0; i < ENTRIES.length; i++) {
            ENTRIES[i] = new Entry();
        }
    }

    private int y;
    private int x;
    private int totalPages;
    private int page;

    protected WormholeScreen() {
        super(Component.translatable("confluence.wormhole.title"));
    }

    public static void setPlayerList(@UnknownNullability List<Pair<WormholePlayerDataSyncPacketS2C.Data, PlayerInfo>> playerList) {
        PLAYER_LIST.clear();
        PLAYER_LIST.addAll(playerList);
        INSTANCE.page = 0;
        INSTANCE.totalPages = (PLAYER_LIST.size() + 3) / 4;
        INSTANCE.update();
    }

    @Override
    protected void init() {
        super.init();
        WormholeRequestPlayerDataPacketC2S.sendToServer();
        x = width / 2 - WIDTH / 2;
        y = height / 2 - HEIGHT / 2;
        for (int i = 0, entriesLength = ENTRIES.length; i < entriesLength; i++) {
            var v = ENTRIES[i];
            v.setX(x + 7);
            v.setY(y + 7 + 24 * i);
            addRenderableWidget(v);
        }
    }

    private void update() {
        if (PLAYER_LIST.isEmpty()) {
            return;
        }
        for (int i = 0; i < ENTRIES.length; i++) {
            var v = ENTRIES[i];
            int index = i + page * 4;
            if (index >= PLAYER_LIST.size()) {
                v.setPlayerData(null);
                v.visible = false;
                v.isAvailable(false);
                continue;
            }
            var data = PLAYER_LIST.get(index);
            v.setPlayerData(data);
            v.visible = true;
            v.isAvailable(true);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (totalPages > 0) {
            setPage((getPage() + (int) mouseY) % totalPages);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public int getPage() {
        return Math.clamp(page, 0, totalPages);
    }

    public void setPage(int page) {
        this.page = Math.clamp(page, 0, totalPages);
        update();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (totalPages <= 0) {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        guiGraphics.blit(BACKGROUND_TEXTURE, x + 180, y + 7, 180, 107, 5, 5, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        guiGraphics.blit(BACKGROUND_TEXTURE, x + 180, y + 94, 180, 117, 5, 5, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        int scrollBarHeight = totalPages <= 0 ? 80 : 80 / totalPages;
        int scrollBarY = y + 13 + scrollBarHeight * page;
        guiGraphics.blit(BACKGROUND_TEXTURE, x + 181, scrollBarY, 3, scrollBarHeight, 181, 113, 3, 3, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public static class Entry extends AbstractWidget {
        public static final ResourceLocation TEAM_ICON = Confluence.asResource("team/icon");
        private static final ResourceLocation WORMHOLE_POTION_STROKE = Confluence.asResource("container/wormhole/wormhole_potion_stroke");
        private final Font font;
        private final Minecraft minecraft;
        private boolean isAvailable;
        private @Nullable Pair<WormholePlayerDataSyncPacketS2C.Data, PlayerInfo> data;
        private static final ItemStack WORMHOLE_POTION_STACK = PotionItems.WORMHOLE_POTION.toStack();
        private WormholePlayerDataSyncPacketS2C.Data playerData;
        private PlayerInfo playerInfo;
        private Component display;
        private Component levelResourceKeyDescription;

        public Entry() {
            super(0, 0, 170, 20, Component.empty());
            this.isAvailable = true;
            minecraft = Minecraft.getInstance();
            setPlayerData(null);
            font = minecraft.font;
        }

        public void setPlayerData(@Nullable Pair<WormholePlayerDataSyncPacketS2C.Data, PlayerInfo> data) {
            this.data = data;
            if (data == null) {
                return;
            }
            playerInfo = data.getSecond();
            playerData = data.getFirst();
            display = getNameForDisplay(playerInfo);
            levelResourceKeyDescription = getLevelResourceKeyDescription();
        }

        public void isAvailable(boolean available) {
            this.isAvailable = available;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (data == null) {
                return;
            }
            int x = this.getX();
            int y = this.getY();
            int uOffset = 7;
            int vOffset = 107 + (isAvailable ? 0 : height + 1);
            guiGraphics.blit(BACKGROUND_TEXTURE, x, y, uOffset, vOffset, width, height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            if (isHoveredOrFocused()) {
                guiGraphics.blit(BACKGROUND_TEXTURE, x, y, uOffset, 149, width, height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }

            int headX = x + 6;
            int headY = y + 6;
            PlayerSkin skin = playerInfo.getSkin();
            guiGraphics.blit(skin.texture(), headX, headY, 8, 8, 8, 8, 64, 64);

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(headX + 4, headY + 4, 0);
            poseStack.scale(1.5f, 1.5f, 1);
            guiGraphics.blit(skin.texture(), -4, -4, 40, 8, 8, 8, 64, 64);
            poseStack.popPose();

            int teamIconX = x + 23;
            int teamIconY = y + 5;
            int teamIconVPosition = 10 * playerData.team().ordinal();
            guiGraphics.blitSprite(TEAM_ICON, 10, 170, 0, teamIconVPosition, teamIconX, teamIconY, 10, 10);

            int txtX = x + 40;
            int txtY = y + (height - 6) / 2;
            Component txt = display;
            guiGraphics.drawString(font, txt, txtX, txtY, 0xffffffff);

            ResourceKey<Level> levelResourceKey = playerData.levelResourceKey();
            if (levelResourceKey == minecraft.player.level().dimension()) {
                Component distanceTxt = Component.literal("%, dm".formatted(WormholeHandler.distanceToSqr(minecraft.player, playerData, false)));
                int distanceTxtX = x + width - 22 - font.width(distanceTxt);
                guiGraphics.drawString(font, distanceTxt, distanceTxtX, txtY, 0xffffffff);
            } else {
                ResourceLocation levelResourceKeyRl = levelResourceKey.location();
                if (DIMENSION_TEXTURES.containsKey(levelResourceKeyRl)) {
                    int dimensionX = x + width - 36;
                    int dimensionY = y + 2;
                    guiGraphics.blitSprite(DIMENSION_TEXTURES.get(levelResourceKeyRl), dimensionX, dimensionY, 16, 16);
                } else {
                    Component dimensionTxt = levelResourceKeyDescription;
                    int dimensionTxtX = x + width - 22 - font.width(dimensionTxt);
                    guiGraphics.drawString(font, dimensionTxt, dimensionTxtX, txtY, 0xffffffff);
                }
            }

            if (isAvailable) {
                int itemX = x + width - 18;
                int itemY = y + 2;
                guiGraphics.renderFakeItem(WORMHOLE_POTION_STACK, itemX, itemY);
                int itemStrokeX = itemX - 1;
                int itemStrokeY = itemY - 1;
                if (isHovered(guiGraphics, mouseX, mouseY, itemStrokeX, itemStrokeY, 18, 18)) {
                    guiGraphics.blitSprite(WORMHOLE_POTION_STROKE, itemStrokeX, itemStrokeY, 18, 18);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int itemX = getX() + width - 19;
            int itemY = getY() + 1;
            if (isFocused() || (mouseX >= itemX && mouseY >= itemY && mouseX < itemX + 18 && mouseY < itemY + 18)) {
                PacketDistributor.sendToServer(new WormholeToPlayerPacketC2S(playerData.uuid(), WormholeToPlayerPacketC2S.ByMod.DEFAULT));
                INSTANCE.onClose();
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        private static boolean isHovered(GuiGraphics guiGraphics, int mouseX, int mouseY, int itemX, int itemY, int width, int height) {
            return guiGraphics.containsPointInScissor(mouseX, mouseY) &&
                    mouseX >= itemX &&
                    mouseY >= itemY &&
                    mouseX < itemX + width &&
                    mouseY < itemY + height;
        }

        public String getLevelResourceKeyDescriptionKey() {
            return playerData.levelResourceKey().location().toLanguageKey(ILevelExtension.TRANSLATION_PREFIX);
        }

        public Component getLevelResourceKeyDescription() {
            return Component.translatableWithFallback(getLevelResourceKeyDescriptionKey(), playerData.levelResourceKey().location().toString());
        }

        public Component getNameForDisplay(PlayerInfo playerInfo) {
            return playerInfo.getTabListDisplayName() != null
                    ? this.decorateName(playerInfo, playerInfo.getTabListDisplayName().copy())
                    : this.decorateName(playerInfo, PlayerTeam.formatNameForTeam(playerInfo.getTeam(), Component.literal(playerInfo.getProfile().getName())));
        }

        private Component decorateName(PlayerInfo playerInfo, MutableComponent name) {
            return playerInfo.getGameMode() == GameType.SPECTATOR ? name.withStyle(ChatFormatting.ITALIC) : name;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }
}
