package org.confluence.mod.client.gui;

import it.unimi.dsi.fastutil.longs.Long2BooleanArrayMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;
import org.confluence.mod.mixed.IWorldOptions;

import java.util.Arrays;
import java.util.EnumMap;

public class SecretSeedsSelectionScreen extends Screen {
    public static final WidgetSprites INTO_BUTTON = new WidgetSprites(Confluence.asResource(""), Confluence.asResource(""));
    private static EnumMap<Layer, ResourceLocation[]> TEXTURES = Util.make(new EnumMap<>(Layer.class), map -> {
        String prefix = "textures/gui/secret_seeds_selection/background/";
        map.put(Layer.SKY, new ResourceLocation[]{
                Confluence.asResource(prefix + "sky_0")
        });
    });
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/secret_seeds_selection/texture.png");
    private static final SecretSeed[] SEEDS = new SecretSeed[]{
            ModSecretSeeds.DRUNK_WORLD,
            ModSecretSeeds.NOT_THE_BEES,
            ModSecretSeeds.FOR_THE_WORTHY,
            ModSecretSeeds.CELEBRATIONMK10,
            ModSecretSeeds.THE_CONSTANT,
            ModSecretSeeds.DONT_DIG_UP,
            ModSecretSeeds.NO_TRAPS,
            ModSecretSeeds.GET_FIXED_BOI,
            ModSecretSeeds.SKYBLOCK,
    };
    private static final Long2ObjectArrayMap<Triple<ResourceLocation, Component, Component>> DESCRIPTIONS = Util.make(new Long2ObjectArrayMap<>(12), map -> {
        map.defaultReturnValue(new ImmutableTriple<>(
                Confluence.asResource("textures/gui/secret_seeds_selection/world_icon/normal.png"),
                Component.translatable("title.confluence.secret_seeds_selection.normal"),
                Component.translatable("description.confluence.secret_seeds_selection.normal")
        ));
        for (SecretSeed seed : SEEDS) {
            ResourceLocation id = seed.getId();
            map.put(seed.getFlag(), new ImmutableTriple<>(
                    Confluence.asResource("textures/gui/secret_seeds_selection/world_icon/" + id.getPath() + ".png"),
                    Component.translatable("title." + id.getNamespace() + ".secret_seeds_selection." + id.getPath()),
                    Component.translatable("description." + id.getNamespace() + ".secret_seeds_selection." + id.getPath())
            ));
        }
    });
    private final EditBox parentSeedEdit;
    private final WorldCreationUiState uiState;
    private int imageWidth;
    private int imageHeight;
    private int leftPos;
    private int topPos;

    private EditBox seedEdit;
    private Long2BooleanArrayMap selection;

    public SecretSeedsSelectionScreen(EditBox parentSeedEdit, WorldCreationUiState uiState) {
        super(Component.translatable("title.confluence.secret_seeds_selection"));
        this.parentSeedEdit = parentSeedEdit;
        this.uiState = uiState;
    }

    @Override
    protected void init() {
        this.imageWidth = 190;
        this.imageHeight = 161;
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2 - 16;

        this.seedEdit = addRenderableWidget(new EditBox(font, leftPos + 5, topPos + 17, 180, 17, Component.translatable("selectWorld.enterSeed")) {
            @Override
            public boolean isBordered() {
                return false;
            }
        });
        seedEdit.setValue(parentSeedEdit.getValue());
        seedEdit.setHint(Component.translatable("selectWorld.seedInfo"));

        this.selection = new Long2BooleanArrayMap(12);
        selection.put(0, true);
        long flag = IWorldOptions.of(uiState.getSettings().options()).confluence$getSecretFlag();
        for (SecretSeed seed : SEEDS) {
            selection.put(seed.getFlag(), seed.match(flag));
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        // 确认按钮
        int x = leftPos + 6;
        int y = topPos + 164;
        if (mouseX > x && mouseX < x + 178 && mouseY > y && mouseY < y + 260) {
            guiGraphics.blit(BACKGROUND, x, y, 6, 191, 178, 26, 256, 256);
        } else {
            guiGraphics.blit(BACKGROUND, x, y, 6, 164, 178, 26, 256, 256);
        }
        int centerX = width / 2;
        guiGraphics.drawCenteredString(font, CommonComponents.GUI_CONTINUE, centerX, y + (26 - font.lineHeight) / 2, -1);
        // 选项
        x = leftPos + 6;
        y = topPos + 38;
        int i = 0;
        Long2BooleanMap.Entry hovered = null;
        for (Long2BooleanMap.Entry entry : selection.long2BooleanEntrySet()) {
            int offsetX = i % 6 * 30;
            int offsetY = i < 6 ? 0 : 32;
            int v;
            if (entry.getBooleanValue()) {
                v = 62;
            } else {
                v = 0;
            }
            int x1 = x + offsetX;
            int y1 = y + offsetY;
            Triple<ResourceLocation, Component, Component> triple;
            if (mouseX > x1 && mouseX < x1 + 28 && mouseY > y1 && mouseY < y1 + 30) {
                v += 31;
                hovered = entry;
            }
            guiGraphics.blit(BACKGROUND, x1, y1, 228, v, 28, 30, 256, 256);
            guiGraphics.blit(DESCRIPTIONS.get(entry.getLongKey()).getLeft(), x1 + 5, y1 + 6, 0, 0, 18, 18, 18, 18);
            ++i;
        }
        Triple<ResourceLocation, Component, Component> triple;
        if (hovered == null) {
            triple = DESCRIPTIONS.defaultReturnValue();
        } else {
            triple = DESCRIPTIONS.get(hovered.getLongKey());
        }
        guiGraphics.drawString(font, triple.getMiddle(), centerX - font.width(triple.getMiddle()) / 2, topPos + 108, -1);
        if (font.width(triple.getRight()) > 182) {
            int j = topPos + 108 + 1;
            for (FormattedCharSequence sequence : font.split(triple.getRight(), 182)) {
                guiGraphics.drawString(font, sequence, leftPos + 4, j += font.lineHeight, -1);
            }
        } else {
            guiGraphics.drawString(font, triple.getRight(), leftPos + 4, topPos + 108 + font.lineHeight, -1);
        }
        for (int j = i; j < 12; j++) {
            int offsetX = j % 6 * 30;
            int offsetY = j < 6 ? 0 : 32;
            guiGraphics.blit(BACKGROUND, x + offsetX, y + offsetY, 228, 155, 28, 30, 256, 256);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = leftPos + 6;
        int y = topPos + 164;
        if (mouseX > x && mouseX < x + 178 && mouseY > y && mouseY < y + 260) {
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            parentSeedEdit.setValue(seedEdit.getValue());
            uiState.setSettings(uiState.getSettings().withOptions(options -> {
                WorldOptions worldOptions = IWorldOptions.of(options).confluence$copyWithoutSecretFlag();
                long flag = 0;
                for (Long2BooleanMap.Entry entry : selection.long2BooleanEntrySet()) {
                    if (entry.getBooleanValue()) {
                        flag |= entry.getLongKey();
                    }
                }
                IWorldOptions.of(worldOptions).confluence$withSecretFlag(flag);
                return worldOptions;
            }));
            onClose();
            return true;
        }
        if (clickSelections(mouseX, mouseY)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickSelections(double mouseX, double mouseY) {
        int x = leftPos + 6;
        int y = topPos + 38;
        int i = 0;
        long flag = -1;
        boolean selected = false;
        for (Long2BooleanMap.Entry entry : selection.long2BooleanEntrySet()) {
            int offsetX = i % 6 * 30;
            int offsetY = i < 6 ? 0 : 32;
            int x1 = x + offsetX;
            int y1 = y + offsetY;
            if (mouseX > x1 && mouseX < x1 + 28 && mouseY > y1 && mouseY < y1 + 30) {
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                flag = entry.getLongKey();
                selected = entry.getBooleanValue();
                break;
            }
            ++i;
        }
        if (flag != -1) {
            if (flag == 0) {
                if (selected) {
                    if (Arrays.stream(SEEDS).anyMatch(seed -> selection.get(seed.getFlag()))) {
                        selection.put(0, false);
                    }
                } else {
                    selection.put(0, true);
                    for (SecretSeed seed : SEEDS) {
                        selection.put(seed.getFlag(), false);
                    }
                }
            } else {
                if (ModSecretSeeds.FOR_THE_WORTHY.match(flag)) {
                    for (SecretSeed seed : SEEDS) {
                        if (seed != ModSecretSeeds.SKYBLOCK) {
                            selection.put(seed.getFlag(), !selected);
                        }
                    }
                } else {
                    selection.put(flag, !selected);
                    for (SecretSeed seed : SEEDS) {
                        if (seed != ModSecretSeeds.SKYBLOCK && !selection.get(seed.getFlag())) {
                            selection.put(ModSecretSeeds.FOR_THE_WORTHY.getFlag(), false);
                            break;
                        }
                    }
                }
                selection.put(0, Arrays.stream(SEEDS).noneMatch(seed -> selection.get(seed.getFlag())));
            }
            return true;
        }
        return false;
    }

    public enum Layer {
        SKY,
        STAR,
        CLOUD,
        PLANET,
        ENVIRONMENT_0,
        ENVIRONMENT_1,
        ENVIRONMENT_2,
        LIGHT,
        ENVIRONMENT_3
    }
}
