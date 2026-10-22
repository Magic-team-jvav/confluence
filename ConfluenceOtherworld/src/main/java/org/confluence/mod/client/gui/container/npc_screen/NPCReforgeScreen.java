package org.confluence.mod.client.gui.container.npc_screen;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.menu.NPCReforgeMenu;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.mesdag.portlib.wrapper.common.extensions.IPortInventoryScreenExtension;

import java.util.ArrayDeque;
import java.util.Deque;

public class NPCReforgeScreen extends AbstractContainerScreen<NPCReforgeMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/reforge.png");

    private static final ResourceLocation COIN_PANEL = Confluence.asResource("textures/gui/container/piggy_bank.png");

    // 界面和贴图
    private static final int SCREEN_WIDTH = 176;
    private static final int SCREEN_HEIGHT = 228;
    private static final int BACKGROUND_X = 0;
    private static final int BACKGROUND_Y = 8;
    private static final int BACKGROUND_U = 0;
    private static final int BACKGROUND_V = 0;
    private static final int BACKGROUND_WIDTH = SCREEN_WIDTH;
    private static final int BACKGROUND_HEIGHT = 191;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 300;

    // 工匠预览
    private static final float MODEL_X = 80.0F;
    private static final float MODEL_Y = 108.0F;
    private static final float MODEL_SCALE = 35.0F;
    private static final float HAMMER_RAISED_X = 115.0F;
    private static final float HAMMER_STRIKE_X = 20.0F;
    private static final int HAMMER_RETURN_TICKS = 9;
    private static final float MODEL_YAW = 200.0F;
    private static final float MODEL_LOOK_DISTANCE = 40.0F;
    private static final float MODEL_MAX_HEAD_YAW = 45.0F;
    private static final float MODEL_MAX_HEAD_PITCH = 30.0F;
    private static final float MODEL_PITCH = 0.0F;
    private static final float MODEL_ROLL = 180.0F;
    private static final float MODEL_TRANSLATE_X = 0.0F;
    private static final float MODEL_TRANSLATE_Y = 0.0F;
    private static final float MODEL_TRANSLATE_Z = 0.0F;
    private static final int MODEL_CLIP_LEFT = 0;
    private static final int MODEL_CLIP_TOP = 30;
    private static final int MODEL_CLIP_RIGHT = SCREEN_WIDTH;
    private static final int MODEL_CLIP_BOTTOM = 98;

    // 钱币和词条
    private static final int COST_PLATINUM_ICON_X = 116;
    private static final int COST_PLATINUM_ICON_Y = BACKGROUND_Y + 43;
    private static final int COST_PLATINUM_TEXT_X = 128;
    private static final int COST_PLATINUM_TEXT_Y = BACKGROUND_Y + 46;
    private static final float COST_PLATINUM_ICON_SCALE = 0.8F;
    private static final float COST_PLATINUM_TEXT_SCALE = 0.8F;

    private static final int COST_GOLD_ICON_X = 119;
    private static final int COST_GOLD_ICON_Y = BACKGROUND_Y + 56;
    private static final int COST_GOLD_TEXT_X = 130;
    private static final int COST_GOLD_TEXT_Y = BACKGROUND_Y + 59;
    private static final float COST_GOLD_ICON_SCALE = 0.8F;
    private static final float COST_GOLD_TEXT_SCALE = 0.8F;

    private static final int COST_SILVER_ICON_X = 122;
    private static final int COST_SILVER_ICON_Y = BACKGROUND_Y + 68;
    private static final int COST_SILVER_TEXT_X = 132;
    private static final int COST_SILVER_TEXT_Y = BACKGROUND_Y + 71;
    private static final float COST_SILVER_ICON_SCALE = 0.8F;
    private static final float COST_SILVER_TEXT_SCALE = 0.8F;

    private static final int COST_COPPER_ICON_X = 125;
    private static final int COST_COPPER_ICON_Y = BACKGROUND_Y + 80;
    private static final int COST_COPPER_TEXT_X = 134;
    private static final int COST_COPPER_TEXT_Y = BACKGROUND_Y + 84;
    private static final float COST_COPPER_ICON_SCALE = 0.8F;
    private static final float COST_COPPER_TEXT_SCALE = 0.8F;

    private static final int BALANCE_PANEL_X = -33;
    private static final int BALANCE_PANEL_Y = NPCReforgeMenu.INVENTORY_Y - 8;
    private static final int BALANCE_PANEL_U = 224;
    private static final int BALANCE_PANEL_V = 0;
    private static final int BALANCE_PANEL_WIDTH = 32;
    private static final int BALANCE_PANEL_HEIGHT = 86;
    private static final int BALANCE_X = BALANCE_PANEL_X + 8;
    private static final int BALANCE_Y = BALANCE_PANEL_Y + 8;
    private static final int BALANCE_ROW_SPACING = 18;
    private static final int COST_TEXT_WIDTH = 19;
    private static final int COST_COUNT_COLOR = 0x403326;
    private static final int ITEM_TEXT_GAP = 6;
    private static final int PREFIX_RIGHT_X = NPCReforgeMenu.REFORGE_SLOT_X - ITEM_TEXT_GAP;
    private static final int PREFIX_Y = 73;
    private static final int PREFIX_MAX_WIDTH = 85;
    private static final int PREFIX_COLOR = 0xFFFFFF;
    private static final int PREFIX_POPUP_TICKS = 60;
    private static final int PREFIX_FADE_TICKS = 10;
    private static final int PREFIX_FADE_IN_TICKS = 4;
    private static final int PREFIX_LINE_SPACING = 11;
    private static final int PREFIX_QUEUE_LIMIT = 16;
    private static final int PREFIX_PUSH_TICKS = 4;
    private static final float PREFIX_STACK_SHRINK = 0.86F;
    private static final float PREFIX_MIN_SCALE = 0.55F;
    private static final float PREFIX_STACK_FADE = 0.75F;

    // 点击范围随模型位置移动，限制在可见裁剪区域内。
    /// 预览工匠点击区的半宽，单位界面像素；槽位点击始终优先。
    private static final float MODEL_HIT_HALF_WIDTH = 14.0F;
    private static final float MODEL_HIT_HEIGHT = 1.9F;
    private static final int HINT_MAX_WIDTH = 120;
    private static final int SLOT_SIZE = 16;
    private static final int SLOT_FRAME_PADDING = 1;
    private static final int SLOT_FRAME_LIGHT = 0x70FFFFFF;
    private static final int SLOT_FRAME_FILL = 0x20808080;
    private static final int SLOT_FRAME_DARK = 0x70606060;

    private int hammerSwingAge = HAMMER_RETURN_TICKS;

    private record ReforgePopup(Component text, int createdAt, boolean best) {}

    private final Deque<ReforgePopup> prefixPopups = new ArrayDeque<>();
    private int popupClock;
    private int bestPrefixPauseTicks;
    private boolean hammerImpactPending;
    private TextureTarget previewTarget;
    private BaseNPC tinkererPreview;

    public NPCReforgeScreen(NPCReforgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = SCREEN_WIDTH;
        imageHeight = SCREEN_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        if (tinkererPreview == null && minecraft.level != null) {
            tinkererPreview = NpcEntities.GOBLIN_TINKERER.get().create(minecraft.level);
            if (tinkererPreview != null) {
                tinkererPreview.setNoAi(true);
                tinkererPreview.setRescued();
                tinkererPreview.tickCount = 40;
                tinkererPreview.setYRot(MODEL_YAW);
                tinkererPreview.yRotO = MODEL_YAW;
                tinkererPreview.setXRot(MODEL_PITCH);
                tinkererPreview.xRotO = MODEL_PITCH;
                tinkererPreview.yBodyRot = tinkererPreview.yBodyRotO = MODEL_YAW;
                tinkererPreview.yHeadRot = tinkererPreview.yHeadRotO = MODEL_YAW;
                var controller = tinkererPreview.getAnimatableInstanceCache().getManagerForId(tinkererPreview.getId())
                        .getAnimationControllers().get("movement");
                if (controller != null) controller.transitionLength(0);
                tinkererPreview.setSilent(true);
            }
        }

    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (tinkererPreview != null) tinkererPreview.tickCount++;
        if (hammerSwingAge < HAMMER_RETURN_TICKS) hammerSwingAge++;
        popupClock++;
        if (bestPrefixPauseTicks > 0) bestPrefixPauseTicks--;
        prefixPopups.removeIf(popup -> popupClock - popup.createdAt() >=
                (popup.best() ? NPCReforgeMenu.BEST_PREFIX_PAUSE_TICKS : PREFIX_POPUP_TICKS));
        NPCReforgeMenu.ReforgeResult result;
        while ((result = menu.pollReforgeResult()) != null) {
            hammerSwingAge = 0;
            hammerImpactPending = true;
            PrefixComponent prefix = PrefixUtils.getPrefix(result.item());
            if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                prefixPopups.addLast(new ReforgePopup(ModRarity.withColor(result.item(), prefix.getName()), popupClock, result.best()));
                while (prefixPopups.size() > PREFIX_QUEUE_LIMIT) prefixPopups.removeFirst();
            }
            if (result.best()) bestPrefixPauseTicks = NPCReforgeMenu.BEST_PREFIX_PAUSE_TICKS;
        }
    }

    public boolean isTinkererPreview(BaseNPC npc) {
        return npc == tinkererPreview;
    }

    public float getHammerArmRotation(float partialTick) {
        if (hammerImpactPending) {
            hammerImpactPending = false;
            return (float) Math.toRadians(HAMMER_STRIKE_X);
        }
        float progress = 1.0F - Math.min(1.0F, (hammerSwingAge + partialTick) / HAMMER_RETURN_TICKS);
        float eased = progress * progress * (3.0F - 2.0F * progress);
        return (float) Math.toRadians(HAMMER_RAISED_X + (HAMMER_STRIKE_X - HAMMER_RAISED_X) * eased);
    }

    private boolean isHoveringTinkerer(double mouseX, double mouseY) {
        /// 槽位周边也属于物品操作区，不能被模型按钮截获；拿着物品时不允许误触重铸。
        if (!menu.getCarried().isEmpty()) return false;
        for (var slot : menu.slots) {
            if (slot.isActive() && isHovering(slot.x, slot.y, SLOT_SIZE, SLOT_SIZE, mouseX, mouseY))
                return false;
        }
        return tinkererPreview != null
                && mouseX >= leftPos + Math.max(MODEL_CLIP_LEFT, MODEL_X - MODEL_HIT_HALF_WIDTH)
                && mouseX < leftPos + Math.min(MODEL_CLIP_RIGHT, MODEL_X + MODEL_HIT_HALF_WIDTH)
                && mouseY >= topPos + Math.max(MODEL_CLIP_TOP, MODEL_Y - MODEL_SCALE * MODEL_HIT_HEIGHT)
                && mouseY < topPos + MODEL_CLIP_BOTTOM;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHoveringTinkerer(mouseX, mouseY)) {
            if (canReforge() && menu.clickMenuButton(minecraft.player, 0)) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.SMITHING_TABLE_USE, 1.0F));
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean canReforge() {
        return menu.getCarried().isEmpty() && PrefixUtils.couldReforge(menu.getReforgeItem()) && menu.getCooldownTicks() == 0
                && bestPrefixPauseTicks == 0
                && menu.getCost() < 0x3F3F3F3F && menu.getAvailableMoney() >= menu.getCost();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (tinkererPreview != null) {
            updateTinkererLook(mouseX, mouseY);
            boolean outlined = isHoveringTinkerer(mouseX, mouseY) && NPCTradeItemOutline.getShader() != null;
            if (outlined) {
                graphics.flush();
                int targetWidth = minecraft.getWindow().getWidth();
                int targetHeight = minecraft.getWindow().getHeight();
                if (previewTarget == null || previewTarget.width != targetWidth || previewTarget.height != targetHeight) {
                    if (previewTarget != null) previewTarget.destroyBuffers();
                    previewTarget = new TextureTarget(targetWidth, targetHeight, true, Minecraft.ON_OSX);
                    previewTarget.setClearColor(0, 0, 0, 0);
                }
                try {
                    previewTarget.clear(Minecraft.ON_OSX);
                    previewTarget.bindWrite(true);
                    renderTinkerer(graphics);
                    graphics.flush();
                } finally {
                    minecraft.getMainRenderTarget().bindWrite(true);
                }
                compositePreview(graphics, true);
                compositePreview(graphics, false);
            } else {
                renderTinkerer(graphics);
                graphics.flush();
            }
            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        }
        graphics.blit(BACKGROUND, leftPos + BACKGROUND_X, topPos + BACKGROUND_Y, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        if (menu.getCost() < 0x3F3F3F3F) drawReforgeCost(graphics, menu.getCost());
        graphics.blit(COIN_PANEL, leftPos + BALANCE_PANEL_X, topPos + BALANCE_PANEL_Y,
                BALANCE_PANEL_U, BALANCE_PANEL_V, BALANCE_PANEL_WIDTH, BALANCE_PANEL_HEIGHT);
        drawCoins(graphics, menu.getAvailableMoney(), leftPos + BALANCE_X, topPos + BALANCE_Y);
        if (menu.getReforgeItem().isEmpty()) {
            int slotX = leftPos + NPCReforgeMenu.REFORGE_SLOT_X;
            int slotY = topPos + NPCReforgeMenu.REFORGE_SLOT_Y;
            int edge = SLOT_FRAME_PADDING;
            graphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, SLOT_FRAME_FILL);
            graphics.fill(slotX - edge, slotY - edge, slotX + SLOT_SIZE + edge, slotY, SLOT_FRAME_DARK);
            graphics.fill(slotX - edge, slotY, slotX, slotY + SLOT_SIZE, SLOT_FRAME_DARK);
            graphics.fill(slotX, slotY + SLOT_SIZE, slotX + SLOT_SIZE + edge, slotY + SLOT_SIZE + edge, SLOT_FRAME_LIGHT);
            graphics.fill(slotX + SLOT_SIZE, slotY, slotX + SLOT_SIZE + edge, slotY + SLOT_SIZE, SLOT_FRAME_LIGHT);
            Component hint = Component.translatable("gui.confluence.reforge.click_goblin");
            var lines = font.split(hint, HINT_MAX_WIDTH);
            int hintY = slotY + (SLOT_SIZE - lines.size() * font.lineHeight) / 2;
            for (var line : lines) {
                graphics.drawString(font, line, slotX - ITEM_TEXT_GAP - font.width(line), hintY, PREFIX_COLOR);
                hintY += font.lineHeight;
            }
        }
        float stackDepth = 0.0F;
        var popups = prefixPopups.descendingIterator();
        while (popups.hasNext()) {
            ReforgePopup popup = popups.next();
            float age = popupClock - popup.createdAt() + partialTick;
            int lifetime = popup.best() ? NPCReforgeMenu.BEST_PREFIX_PAUSE_TICKS : PREFIX_POPUP_TICKS;
            float opacity = Math.max(0, Math.min(1, Math.min(age / PREFIX_FADE_IN_TICKS,
                    (lifetime - age) / PREFIX_FADE_TICKS)));
            float scale = Math.max(PREFIX_MIN_SCALE, (float) Math.pow(PREFIX_STACK_SHRINK, stackDepth));
            opacity *= (float) Math.pow(PREFIX_STACK_FADE, stackDepth);
            int alpha = (int) (255 * opacity);
            if (alpha > 3) {
                graphics.pose().pushPose();
                graphics.pose().translate(leftPos + PREFIX_RIGHT_X, topPos + PREFIX_Y - stackDepth * PREFIX_LINE_SPACING, 0);
                graphics.pose().scale(scale, scale, 1);
                var text = Language.getInstance().getVisualOrder(font.substrByWidth(popup.text(), PREFIX_MAX_WIDTH));
                graphics.drawString(font, text, -font.width(text), 0, alpha << 24 | PREFIX_COLOR);
                graphics.pose().popPose();
            }
            float push = Math.min(1.0F, age / PREFIX_PUSH_TICKS);
            stackDepth += push * push * (3.0F - 2.0F * push);
        }
    }


    private void updateTinkererLook(int mouseX, int mouseY) {
        float eyeX = leftPos + MODEL_X;
        float eyeY = topPos + MODEL_Y - tinkererPreview.getEyeHeight() * MODEL_SCALE;
        boolean hasItem = !menu.getReforgeItem().isEmpty();
        float targetX = hasItem ? leftPos + NPCReforgeMenu.REFORGE_SLOT_X + SLOT_SIZE / 2.0F : mouseX;
        float targetY = hasItem ? topPos + NPCReforgeMenu.REFORGE_SLOT_Y + SLOT_SIZE / 2.0F : mouseY;
        float yaw = (float) (Math.atan((eyeX - targetX) / MODEL_LOOK_DISTANCE) * 2 / Math.PI) * MODEL_MAX_HEAD_YAW;
        float pitch = (float) (Math.atan((targetY - eyeY) / MODEL_LOOK_DISTANCE) * 2 / Math.PI) * MODEL_MAX_HEAD_PITCH;
        tinkererPreview.yHeadRot = tinkererPreview.yHeadRotO = MODEL_YAW + yaw;
        tinkererPreview.setYRot(MODEL_YAW + yaw);
        tinkererPreview.yRotO = tinkererPreview.getYRot();
        tinkererPreview.setXRot(pitch);
        tinkererPreview.xRotO = pitch;
    }

    private void renderTinkerer(GuiGraphics graphics) {
        graphics.pose().pushPose();
        graphics.enableScissor(leftPos + MODEL_CLIP_LEFT, topPos + MODEL_CLIP_TOP, leftPos + MODEL_CLIP_RIGHT, topPos + MODEL_CLIP_BOTTOM);
        IPortInventoryScreenExtension.renderEntityInInventory(graphics, leftPos + MODEL_X, topPos + MODEL_Y, MODEL_SCALE,
                new Vector3f(MODEL_TRANSLATE_X, MODEL_TRANSLATE_Y, MODEL_TRANSLATE_Z),
                new Quaternionf().rotationZ((float) Math.toRadians(MODEL_ROLL)), null, tinkererPreview);
        graphics.disableScissor();
        graphics.pose().popPose();
    }


    private void compositePreview(GuiGraphics graphics, boolean outline) {
        graphics.flush();
        ShaderInstance shader = outline ? NPCTradeItemOutline.getShader() : GameRenderer.getPositionTexColorShader();
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, previewTarget.getColorTextureId());
        if (outline) shader.safeGetUniform("OutlineStep").set(1.0F / width, 1.0F / height);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        try {
            Matrix4f matrix = graphics.pose().last().pose();
            BufferBuilder buffer = Tesselator.getInstance().getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            buffer.vertex(matrix, 0, 0, 0).uv(0, 1).color(255, 255, 255, 255).endVertex();
            buffer.vertex(matrix, 0, height, 0).uv(0, 0).color(255, 255, 255, 255).endVertex();
            buffer.vertex(matrix, width, height, 0).uv(1, 0).color(255, 255, 255, 255).endVertex();
            buffer.vertex(matrix, width, 0, 0).uv(1, 1).color(255, 255, 255, 255).endVertex();
            BufferUploader.drawWithShader(buffer.end());
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void removed() {
        if (previewTarget != null) {
            previewTarget.destroyBuffers();
            previewTarget = null;
        }
        super.removed();
    }

    private void drawReforgeCost(GuiGraphics graphics, long money) {
        var entries = PlayerUtils.decodeCoin(money).platinum2CopperEntries().iterator();
        drawCostLine(graphics, entries.next(), COST_PLATINUM_ICON_X, COST_PLATINUM_ICON_Y,
                COST_PLATINUM_TEXT_X, COST_PLATINUM_TEXT_Y, COST_PLATINUM_ICON_SCALE, COST_PLATINUM_TEXT_SCALE);
        drawCostLine(graphics, entries.next(), COST_GOLD_ICON_X, COST_GOLD_ICON_Y,
                COST_GOLD_TEXT_X, COST_GOLD_TEXT_Y, COST_GOLD_ICON_SCALE, COST_GOLD_TEXT_SCALE);
        drawCostLine(graphics, entries.next(), COST_SILVER_ICON_X, COST_SILVER_ICON_Y,
                COST_SILVER_TEXT_X, COST_SILVER_TEXT_Y, COST_SILVER_ICON_SCALE, COST_SILVER_TEXT_SCALE);
        drawCostLine(graphics, entries.next(), COST_COPPER_ICON_X, COST_COPPER_ICON_Y,
                COST_COPPER_TEXT_X, COST_COPPER_TEXT_Y, COST_COPPER_ICON_SCALE, COST_COPPER_TEXT_SCALE);
    }

    private void drawCostLine(GuiGraphics graphics, Object2IntMap.Entry<CoinItem> entry,
                              int iconX, int iconY, int textX, int textY, float iconScale, float countScale) {
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos + iconX, topPos + iconY, 0);
        graphics.pose().scale(iconScale, iconScale, 1);
        graphics.renderItem(new ItemStack(entry.getKey()), 0, 0);
        graphics.pose().popPose();
        String count = Integer.toString(entry.getIntValue());
        float textScale = Math.min(countScale, (float) COST_TEXT_WIDTH / Math.max(1, font.width(count)));
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos + textX, topPos + textY, 0);
        graphics.pose().scale(textScale, textScale, 1);
        graphics.drawString(font, count, 0, 0, COST_COUNT_COLOR, false);
        graphics.pose().popPose();
    }

    private void drawCoins(GuiGraphics graphics, long money, int x, int y) {
        for (Object2IntMap.Entry<CoinItem> entry : PlayerUtils.decodeCoin(money).platinum2CopperEntries()) {
            ItemStack stack = new ItemStack(entry.getKey());
            graphics.renderItem(stack, x, y);
            graphics.renderItemDecorations(font, stack, x, y, Integer.toString(entry.getIntValue()));
            y += BALANCE_ROW_SPACING;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
