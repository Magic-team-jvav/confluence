package org.confluence.mod.client.gui;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Hashtable;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class AchievementToast implements Toast {
    public static final Component DISPLAY = Component.translatable("achievements.toast.complete");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/achievement/toast.png");
    private static final Hashtable<ResourceLocation, AchievementToast> ACHIEVEMENTS = new Hashtable<>();
    private final ResourceLocation icon;
    private final Display display;
    public boolean playedSound;

    public AchievementToast(ResourceLocation icon, Display display) {
        this.icon = icon;
        this.display = display;
        this.playedSound = false;
    }

    @Override
    public int height() {
        return 64;
    }

    @Override
    public Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        Font font = pToastComponent.getMinecraft().font;
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0F, pGuiGraphics.guiHeight() - height(), 0.0F);
        pGuiGraphics.blit(TEXTURE, 0, 0, 0, 0, width(), height(), 160, 64);
        renderTitle(pGuiGraphics, pTimeSinceLastVisible, font);
        renderDescription(pGuiGraphics, font);
        renderIcon(pGuiGraphics);
        pGuiGraphics.pose().popPose();
        playSound(pToastComponent, pTimeSinceLastVisible);
        return (double) pTimeSinceLastVisible >= 5000.0 * pToastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    private void playSound(ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        if (!playedSound && pTimeSinceLastVisible > 0L) {
            this.playedSound = true;
            if (display.type() == AdvancementType.CHALLENGE) {
                pToastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.ACHIEVEMENTS.get(), 1.0F, 1.0F));
            }
        }
    }

    private void renderDescription(GuiGraphics pGuiGraphics, Font font) {
        List<FormattedCharSequence> list = font.split(display.description(), 141);
        if (list.size() == 1) {
            pGuiGraphics.drawString(font, list.getFirst(), 8, 44, 0, false);
        } else {
            int l = 48 - list.size() * 9 / 2;
            for (FormattedCharSequence formattedcharsequence : list) {
                pGuiGraphics.drawString(font, formattedcharsequence, 8, l, 0, false);
                l += 9;
            }
        }
    }

    private void renderIcon(@NotNull GuiGraphics pGuiGraphics) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(4, 4, 0);
        pGuiGraphics.pose().scale(0.375F, 0.375F, 1.0F);
        pGuiGraphics.blit(icon, 0, 0, 0, 0, 64, 64, 64, 64);
        pGuiGraphics.pose().popPose();
    }

    private void renderTitle(GuiGraphics pGuiGraphics, long pTimeSinceLastVisible, Font font) {
        List<FormattedCharSequence> list = font.split(display.title(), 125);
        int i = display.type() == AdvancementType.CHALLENGE ? 16746751 : 16776960;
        if (list.size() == 1) {
            pGuiGraphics.drawString(font, DISPLAY, 30, 7, i | -16777216, false);
            pGuiGraphics.drawString(font, list.getFirst(), 30, 18, -1, false);
        } else {
            if (pTimeSinceLastVisible < 1500L) {
                int k = Mth.floor(Mth.clamp((float) (1500L - pTimeSinceLastVisible) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                pGuiGraphics.drawString(font, DISPLAY, 30, 11, i | k, false);
            } else {
                int i1 = Mth.floor(Mth.clamp((float) (pTimeSinceLastVisible - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                int l = 16 - list.size() * 9 / 2;

                for (FormattedCharSequence formattedcharsequence : list) {
                    pGuiGraphics.drawString(font, formattedcharsequence, 30, l, 16777215 | i1, false);
                    l += 9;
                }
            }
        }
    }

    public static boolean renderWidgetIcon(ResourceLocation id, GuiGraphics guiGraphics, int x, int y) {
        AchievementToast achievementToast = getToast(id);
        if (achievementToast == null) return true;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(0.25F, 0.25F, 1.0F);
        guiGraphics.blit(achievementToast.icon, 0, 0, 0, 0, 64, 64, 64, 64);
        guiGraphics.pose().popPose();
        return false;
    }

    public static void registerToast(ResourceLocation id) {
        String namespace = id.getNamespace();
        String path = id.getPath().substring(AchievementOffsetLoader.PREFIX.length());
        ACHIEVEMENTS.put(ResourceLocation.fromNamespaceAndPath(namespace, AchievementOffsetLoader.PREFIX + path), new AchievementToast(
                ResourceLocation.fromNamespaceAndPath(namespace, "textures/achievement/" + path + ".png"),
                new Display(AdvancementType.CHALLENGE,
                        Component.translatable("achievements." + namespace + "." + path + ".title"),
                        Component.translatable("achievements." + namespace + "." + path + ".description")
                )));
    }

    public static @Nullable AchievementToast getToast(ResourceLocation advancement) {
        return ACHIEVEMENTS.get(advancement);
    }

    public static void clearToast() {
        ACHIEVEMENTS.clear();
    }

    public record Display(AdvancementType type, Component title, Component description) {}
}
