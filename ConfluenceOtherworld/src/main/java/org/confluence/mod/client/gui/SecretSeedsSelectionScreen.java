package org.confluence.mod.client.gui;

import com.google.common.collect.Iterables;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.longs.AbstractLong2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanArrayMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.FileUtil;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.SecretSeed;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.ModUtils;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SecretSeedsSelectionScreen extends Screen {
    public static final WidgetSprites SPRITES = new WidgetSprites(Confluence.asResource("seed_icon"), Confluence.asResource("seed_icon_highlighted"));
    private static final ResourceLocation FIRST = Confluence.asResource("textures/gui/secret_seeds_selection/first.png");
    private static final SecretSeed[] SPECIAL_SEEDS = new SecretSeed[]{
            ModSecretSeeds.NOT_THE_BEES,
            ModSecretSeeds.DRUNK_WORLD,
            ModSecretSeeds.CELEBRATIONMK10,
            ModSecretSeeds.THE_CONSTANT,
            ModSecretSeeds.FOR_THE_WORTHY,
            ModSecretSeeds.NO_TRAPS,
            ModSecretSeeds.DONT_DIG_UP,
            ModSecretSeeds.GET_FIXED_BOI,
            ModSecretSeeds.SKYBLOCK
    };
    private static final Long2ObjectArrayMap<Triple<ResourceLocation, Component, Component>> DESCRIPTIONS = Util.make(new Long2ObjectArrayMap<>(12), map -> {
        ResourceLocation normal = Confluence.asResource("textures/gui/secret_seeds_selection/world_icon/normal.png");
        map.defaultReturnValue(new ImmutableTriple<>(
                normal,
                Component.translatable("title.confluence.secret_seeds_selection.empty"),
                Component.translatable("description.confluence.secret_seeds_selection.empty")
        ));
        map.put(-1, new ImmutableTriple<>(
                Confluence.asResource("textures/gui/secret_seeds_selection/world_icon/secret_seed.png"),
                Component.translatable("title.confluence.secret_seeds_selection.secret_seed"),
                Component.translatable("description.confluence.secret_seeds_selection.secret_seed")
        ));
        map.put(0, new ImmutableTriple<>(
                normal,
                Component.translatable("title.confluence.secret_seeds_selection.normal"),
                Component.translatable("description.confluence.secret_seeds_selection.normal")
        ));
        for (SecretSeed seed : SPECIAL_SEEDS) {
            ResourceLocation id = seed.getId();
            map.put(seed.getFlag(), new ImmutableTriple<>(
                    Confluence.asResource("textures/gui/secret_seeds_selection/world_icon/" + id.getPath() + ".png"),
                    Component.translatable("title." + id.getNamespace() + ".secret_seeds_selection." + id.getPath()),
                    Component.translatable("description." + id.getNamespace() + ".secret_seeds_selection." + id.getPath())
            ));
        }
    });
    private static final Path UNLOCKED_SECRET_SEEDS_PATH = FMLPaths.GAMEDIR.get().resolve("confluence").resolve("unlocked_secret_seeds.json");
    private static final Codec<Set<SecretSeed>> UNLOCKED_SECRET_SEEDS_CODEC = ModSecretSeeds.CODEC.listOf().xmap(LinkedHashSet::new, ArrayList::new);
    private static final SecretSeed[] SECRET_SEEDS = new SecretSeed[]{
            ModSecretSeeds.BOULDER_WORLD
    };
    private final EditBox parentSeedEdit;
    private final WorldCreationUiState uiState;
    private int imageWidth;
    private int imageHeight;
    private int leftPos;
    private int topPos;

    private EditBox seedEdit;
    private Long2BooleanMap selection;
    private Long2BooleanMap.Entry hovered;
    private int lines;
    private int skip;

    private Object2BooleanMap<SecretSeed> unlockedSecretSeeds = new Object2BooleanLinkedOpenHashMap<>();

    public SecretSeedsSelectionScreen(EditBox parentSeedEdit, WorldCreationUiState uiState) {
        super(CommonComponents.EMPTY);
        this.parentSeedEdit = parentSeedEdit;
        this.uiState = uiState;
    }

    @Override
    protected void init() {
        BackgroundLayer.initLayers(width, height);

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
        for (SecretSeed seed : SPECIAL_SEEDS) {
            selection.put(seed.getFlag(), seed.match(flag));
        }

        if (Files.isRegularFile(UNLOCKED_SECRET_SEEDS_PATH)) {
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(UNLOCKED_SECRET_SEEDS_PATH, StandardCharsets.UTF_8))) {
                reader.setLenient(false);
                Set<SecretSeed> set = UNLOCKED_SECRET_SEEDS_CODEC.parse(JsonOps.INSTANCE, Streams.parse(reader)).getOrThrow(JsonParseException::new);
                Object2BooleanLinkedOpenHashMap<SecretSeed> map = new Object2BooleanLinkedOpenHashMap<>();
                set.stream().sorted(Comparator.comparingLong(SecretSeed::getFlag)).forEachOrdered(seed -> map.put(seed, false));
                this.unlockedSecretSeeds = map;
            } catch (JsonIOException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't access unlocked secret seeds in {}", UNLOCKED_SECRET_SEEDS_PATH, ioexception);
            } catch (JsonParseException jsonParseException) {
                Confluence.LOGGER.error("Couldn't parse unlocked secret seeds in {}", UNLOCKED_SECRET_SEEDS_PATH, jsonParseException);
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        BackgroundLayer.renderLayers(guiGraphics, partialTick);

        guiGraphics.blit(FIRST, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        // 确认按钮
        int x = leftPos + 6;
        int y = topPos + 164;
        if (mouseX > x && mouseX < x + 178 && mouseY > y && mouseY < y + 260) {
            guiGraphics.blit(FIRST, x, y, 6, 191, 178, 26, 256, 256);
        } else {
            guiGraphics.blit(FIRST, x, y, 6, 164, 178, 26, 256, 256);
        }
        int centerX = width / 2;
        guiGraphics.drawCenteredString(font, CommonComponents.GUI_CONTINUE, centerX, y + (26 - font.lineHeight) / 2, -1);
        // 选项
        x = leftPos + 6;
        y = topPos + 38;
        int i = 0;
        Long2BooleanMap.Entry hovered = null;
        int offsetX;
        int offsetY;
        int v;
        int x1;
        int y1;
        for (Long2BooleanMap.Entry entry : selection.long2BooleanEntrySet()) {
            offsetX = i % 6 * 30;
            offsetY = i < 6 ? 0 : 32;
            v = entry.getBooleanValue() ? 62 : 0;
            x1 = x + offsetX;
            y1 = y + offsetY;
            if (mouseX > x1 && mouseX < x1 + 28 && mouseY > y1 && mouseY < y1 + 30) {
                v += 31;
                hovered = entry;
            }
            guiGraphics.blit(FIRST, x1, y1, 228, v, 28, 30, 256, 256);
            guiGraphics.blit(DESCRIPTIONS.get(entry.getLongKey()).getLeft(), x1 + 5, y1 + 4, 0, 0, 18, 18, 18, 18);
            ++i;
        }

        if (unlockedSecretSeeds.isEmpty()) {
            guiGraphics.blit(FIRST, x + 120, y + 32, 228, 155, 28, 30, 256, 256);
        } else {
            offsetX = i % 6 * 30;
            offsetY = i < 6 ? 0 : 32;
            v = selectedSecretSeed() ? 62 : 0;
            x1 = x + offsetX;
            y1 = y + offsetY;
            if (mouseX > x1 && mouseX < x1 + 28 && mouseY > y1 && mouseY < y1 + 30) {
                v += 31;
                if (hovered == null) {
                    hovered = new AbstractLong2BooleanMap.BasicEntry(-1, selectedSecretSeed());
                }
            }
            guiGraphics.blit(FIRST, x1, y1, 228, v, 28, 30, 256, 256);
            guiGraphics.blit(DESCRIPTIONS.get(-1).getLeft(), x1 + 5, y1 + 4, 0, 0, 18, 18, 18, 18);
        }
        guiGraphics.blit(FIRST, x + 150, y + 32, 228, 155, 28, 30, 256, 256);

        Triple<ResourceLocation, Component, Component> triple;
        if (hovered == null) {
            triple = DESCRIPTIONS.defaultReturnValue();
        } else {
            triple = DESCRIPTIONS.get(hovered.getLongKey());
        }
        guiGraphics.drawString(font, triple.getMiddle(), centerX - font.width(triple.getMiddle()) / 2, topPos + 108, -1);
        if (font.width(triple.getRight()) > 182) {
            List<FormattedCharSequence> list = font.split(triple.getRight(), 182);
            this.lines = list.size();
            int j = topPos + 108;
            if (hovered == null || this.hovered == null || hovered.getLongKey() != this.hovered.getLongKey()) {
                this.skip = 0;
            }
            int end = Math.min(skip + maxDescLines(), lines);
            if (skip > 0) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, -Mth.sin(System.currentTimeMillis() % 1000 / 1000F), 0);
                guiGraphics.blit(FIRST, leftPos + 181, topPos + 108, 245, 186, 5, 4, 256, 256);
                guiGraphics.pose().popPose();
            }
            if (end < lines && lines > maxDescLines()) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, Mth.sin(System.currentTimeMillis() % 1000 / 1000F), 0);
                guiGraphics.blit(FIRST, leftPos + 181, topPos + 153, 251, 186, 5, 4, 256, 256);
                guiGraphics.pose().popPose();
            }
            for (FormattedCharSequence sequence : list.subList(skip, end)) {
                guiGraphics.drawString(font, sequence, leftPos + 4, j += font.lineHeight, -1);
            }
        } else {
            this.lines = 1;
            guiGraphics.drawString(font, triple.getRight(), leftPos + 4, topPos + 108 + font.lineHeight, -1);
        }

        this.hovered = hovered;
    }

    private boolean selectedSecretSeed() {
        return unlockedSecretSeeds.values().stream().anyMatch(Boolean::booleanValue);
    }

    private int maxDescLines() {
        return 49 / font.lineHeight - 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && BackgroundLayer.clickedLayers(mouseX, mouseY)) {
            return true;
        }
        unlockSecretSeeds();
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

    private void unlockSecretSeeds() {
        if (!seedEdit.isHovered() && seedEdit.isFocused()) {
            seedEdit.setFocused(false);
            ObjectLongPair<OptionalLong> pair = ModSecretSeeds.tryMatch(seedEdit.getValue(), Arrays.stream(SECRET_SEEDS).toList());
            long flag = pair.rightLong();
            boolean save = false;
            for (SecretSeed seed : SECRET_SEEDS) {
                if (seed.match(flag)) {
                    save |= !unlockedSecretSeeds.containsKey(seed);
                    unlockedSecretSeeds.put(seed, true);
                }
            }
            if (save) {
                try {
                    FileUtil.createDirectoriesSafe(UNLOCKED_SECRET_SEEDS_PATH.getParent());
                    try (Writer writer = Files.newBufferedWriter(UNLOCKED_SECRET_SEEDS_PATH, StandardCharsets.UTF_8)) {
                        ModUtils.GSON.toJson(UNLOCKED_SECRET_SEEDS_CODEC.encodeStart(JsonOps.INSTANCE, unlockedSecretSeeds.keySet()).getOrThrow(), ModUtils.GSON.newJsonWriter(writer));
                    }
                } catch (JsonIOException | IOException ioexception) {
                    Confluence.LOGGER.error("Couldn't save unlocked secret seeds to {}", UNLOCKED_SECRET_SEEDS_PATH, ioexception);
                }
            }
        }
    }

    private boolean clickSelections(double mouseX, double mouseY) {
        int x = leftPos + 6;
        int y = topPos + 38;
        int i = 0;
        int offsetX;
        int offsetY;
        int x1;
        int y1;
        long flag = -1;
        boolean selected = false;
        for (Long2BooleanMap.Entry entry : selection.long2BooleanEntrySet()) {
            offsetX = i % 6 * 30;
            offsetY = i < 6 ? 0 : 32;
            x1 = x + offsetX;
            y1 = y + offsetY;
            if (mouseX > x1 && mouseX < x1 + 28 && mouseY > y1 && mouseY < y1 + 30) {
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                flag = entry.getLongKey();
                selected = entry.getBooleanValue();
                break;
            }
            ++i;
        }
        if (flag == -1) {
            // todo
//            if (!unlockedSecretSeeds.isEmpty()) {
//                offsetX = i % 6 * 30;
//                offsetY = i < 6 ? 0 : 32;
//                x1 = x + offsetX;
//                y1 = y + offsetY;
//                if (mouseX > x1 && mouseX < x1 + 28 && mouseY > y1 && mouseY < y1 + 30) {
//                    getMinecraft().pushGuiLayer(new SecondScreen());
//                    return true;
//                }
//            }
            return false;
        }
        if (flag == 0) {
            if (selected) {
                if (Arrays.stream(SPECIAL_SEEDS).anyMatch(seed -> selection.get(seed.getFlag()))) {
                    selection.put(0, false);
                }
            } else {
                selection.put(0, true);
                for (SecretSeed seed : SPECIAL_SEEDS) {
                    selection.put(seed.getFlag(), false);
                }
            }
        } else {
            if (ModSecretSeeds.GET_FIXED_BOI.match(flag)) {
                for (SecretSeed seed : SPECIAL_SEEDS) {
                    if (seed != ModSecretSeeds.SKYBLOCK) {
                        selection.put(seed.getFlag(), !selected);
                    }
                }
            } else {
                selection.put(flag, !selected);
                for (SecretSeed seed : SPECIAL_SEEDS) {
                    if (seed != ModSecretSeeds.SKYBLOCK && !selection.get(seed.getFlag())) {
                        selection.put(ModSecretSeeds.GET_FIXED_BOI.getFlag(), false);
                        break;
                    }
                }
            }
            selection.put(0, Arrays.stream(SPECIAL_SEEDS).noneMatch(seed -> selection.get(seed.getFlag())));
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (hovered != null) {
            if (lines > maxDescLines()) {
                this.skip = Mth.clamp(skip + Mth.sign(-scrollY), 0, lines - maxDescLines());
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && BackgroundLayer.dragLayers(mouseX, mouseY, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && BackgroundLayer.releasedLayers(mouseX, mouseY)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        BackgroundLayer.closeLayers();
        super.onClose();
    }

    public class SecondScreen extends Screen {
        private static final ResourceLocation SECOND = Confluence.asResource("textures/gui/secret_seeds_selection/second.png");

        private int imageWidth;
        private int imageHeight;
        private int leftPos;
        private int topPos;

        protected SecondScreen() {
            super(CommonComponents.EMPTY);
        }

        @Override
        protected void init() {
            this.imageWidth = 190;
            this.imageHeight = 132;

            this.leftPos = (width - imageWidth) / 2;
            this.topPos = (height - imageHeight) / 2;
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.blit(SECOND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

            ObjectSet<Object2BooleanMap.Entry<SecretSeed>> entries = SecretSeedsSelectionScreen.this.unlockedSecretSeeds.object2BooleanEntrySet();
            for (Object2BooleanMap.Entry<SecretSeed> entry : Iterables.limit(Iterables.skip(entries, 1), 6)) {

            }
        }
    }
}
