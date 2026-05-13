package org.confluence.mod.common.init;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.item.ModItems;

import javax.annotation.Nullable;
import java.util.List;

public final class ModStacks {

    public static final Integer WRITE_COLOR = 0x3A2509;

    public static final ResourceLocation FONT_PAPER_IMAGE = Confluence.asResource("paper_image");
    public static final ResourceLocation FONT_UNIFORM = ResourceLocation.fromNamespaceAndPath("minecraft", "uniform");

    public static final Component EMPTY_LINE = Component.empty();

    public static final Component HANDWRITING_0 = Component.translatable("lore.confluence.mysterious_note.handwriting_0")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
    public static final Component HANDWRITING_1 = Component.translatable("lore.confluence.mysterious_note.handwriting_1")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
    public static final Component HANDWRITING_2 = Component.translatable("lore.confluence.mysterious_note.handwriting_2")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
    public static final Component HANDWRITING_3 = Component.translatable("lore.confluence.mysterious_note.handwriting_3")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);

    private static final CompoundTag DUNGEON_EBONY_MURAL_DATA = parseSnbt("""
            {
                "back": [], "front": [{
                    "icon": {
                        "atlasLocation": "confluence:textures/murals/ebony_mural.png",
                        "textureHeight": 176, "textureWidth": 80,
                        "uWidth": 80, "vHeight": 176
                    },
                    "scale": 0.0625, "y": 10.0, "z": -0.001
                }],
                "headPos": 0L, "id": "confluence:mural_entity_block",
                "left": [], "muralHeight": 11, "muralWidth": 5, "right": []
            }
            """);
    private static final CompoundTag DUNGEON_CRIMSON_MURAL_DATA = parseSnbt("""
            {
                "back": [], "front": [{
                    "icon": {
                        "atlasLocation": "confluence:textures/murals/crimson_mural.png",
                        "textureHeight": 176, "textureWidth": 80,
                        "uWidth": 80, "vHeight": 176
                    },
                    "scale": 0.0625, "y": 10.0, "z": -0.001
                }],
                "headPos": 0L, "id": "confluence:mural_entity_block",
                "left": [], "muralHeight": 11, "muralWidth": 5, "right": []
            }
            """);

    // 壁畫
    public static final ItemStack DUNGEON_EBONY_MURAL = createMural(DUNGEON_EBONY_MURAL_DATA, "mural.dungeon.ebony_mural");
    public static final ItemStack DUNGEON_CRIMSON_MURAL = createMural(DUNGEON_CRIMSON_MURAL_DATA, "mural.dungeon.crimson_mural");

    // 紙條
    public static final ItemStack STRUCTURE_NOTE_0_0 = createMysteriousNote("item.confluence.mysterious_note.name_structure_0", List.of(
            HANDWRITING_0,
            makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
                    "lore.confluence.mysterious_note_structure_0_0"),
            makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
                    "lore.confluence.mysterious_note_structure_0_1"),
            EMPTY_LINE, EMPTY_LINE, EMPTY_LINE, EMPTY_LINE, EMPTY_LINE,
            appendComponent(Component.literal(" "), makeComponent(false, ChatFormatting.WHITE.getColor(), false, false, false, false, false, FONT_PAPER_IMAGE,
                    "1"))
            ));
    public static final ItemStack STRUCTURE_NOTE_0_1 = createMysteriousNote("item.confluence.mysterious_note.name_structure_0", List.of(
            HANDWRITING_2,
            makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
                    "lore.confluence.mysterious_note_structure_1_0"),
            EMPTY_LINE, EMPTY_LINE, EMPTY_LINE, EMPTY_LINE, EMPTY_LINE, EMPTY_LINE, EMPTY_LINE, EMPTY_LINE,
            appendComponent(Component.literal(" "), makeComponent(false, ChatFormatting.WHITE.getColor(), false, false, false, false, false, FONT_PAPER_IMAGE,
                    "2"))
    ));
    public static final ItemStack STRUCTURE_NOTE_1 = createMysteriousNote("item.confluence.mysterious_note.name_structure_1", List.of(
            HANDWRITING_0,
            makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
                    "lore.confluence.mysterious_note_structure_2_0"),
            appendComponent(Component.literal(" "), makeComponent(false, ChatFormatting.WHITE.getColor(), false, false, false, false, false, FONT_PAPER_IMAGE,
                    "3")),
            appendComponent(Component.literal("                       "), makeComponent(false, ChatFormatting.WHITE.getColor(), false, false, false, false, false, FONT_PAPER_IMAGE,
                    "4")),
            appendComponent(Component.literal("                             "), makeComponent(true, WRITE_COLOR, false, false, false, false, false, FONT_UNIFORM,
                    "block.minecraft.amethyst_block")),
            appendComponent(Component.literal("                             "), makeComponent(true, WRITE_COLOR, false, false, false, false, false, FONT_UNIFORM,
                    "block.minecraft.chiseled_tuff")),
            EMPTY_LINE,
            appendComponent(Component.literal("                        "), makeComponent(true, WRITE_COLOR, false, false, false, false, false, FONT_UNIFORM,
                    "block.minecraft.crying_obsidian")),
            EMPTY_LINE,
            appendComponent(Component.literal("                        "), makeComponent(true, WRITE_COLOR, false, false, false, false, false, FONT_UNIFORM,
                    "block.minecraft.chiseled_tuff")),
            EMPTY_LINE,
            EMPTY_LINE
    ));
    public static final ItemStack MYSTERIOUS_NOTE_0 = createMysteriousNote("item.confluence.mysterious_note.name_0", List.of(makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
            "lore.confluence.mysterious_note_0")));
    public static final ItemStack MYSTERIOUS_NOTE_1 = createMysteriousNote("item.confluence.mysterious_note.name_1", List.of(makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
            "lore.confluence.mysterious_note_1")));
    public static final ItemStack MYSTERIOUS_NOTE_2 = createMysteriousNote("item.confluence.mysterious_note.name_2", List.of(makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
            "lore.confluence.mysterious_note_2")));
    public static final ItemStack MYSTERIOUS_NOTE_3 = createMysteriousNote("item.confluence.mysterious_note.name_3", List.of(makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
            "lore.confluence.mysterious_note_3")));
    public static final ItemStack MYSTERIOUS_NOTE_4 = createMysteriousNote("item.confluence.mysterious_note.name_4", List.of(makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
            "lore.confluence.mysterious_note_4")));
    public static final ItemStack MYSTERIOUS_NOTE_5 = createMysteriousNote("item.confluence.mysterious_note.name_5", List.of(makeComponent(true, ChatFormatting.GRAY.getColor(), false, false, false, false, false,
            "lore.confluence.mysterious_note_5")));

    // 書籍
    public static final ItemStack VILLAGE_EXPLORATION = createWrittenBook(
            1,
            "item.confluence.village_exploration",
            "author.confluence.the_ancestor_of_explorers",
            "lore.confluence.village_exploration",
            "村庄环游起源",
            List.of("text.confluence.village_exploration", "text.confluence.village_exploration_0")
    );
    public static final ItemStack RESEARCH_ON_WHEAT_MUTATION = createWrittenBook(
            2,
            "item.confluence.research_on_wheat_mutation",
            "author.confluence.sheila",
            "lore.confluence.research_on_wheat_mutation",
            "关于小麦异变的研究",
            List.of("text.confluence.research_on_wheat_mutation", "text.confluence.research_on_wheat_mutation_0")
    );
    public static final ItemStack RESEARCH_ON_CLOUD_BLOCKS_1 = createWrittenBook(
            3,
            "item.confluence.research_on_cloud_blocks_1",
            "author.confluence.lorissa",
            "lore.confluence.research_on_cloud_blocks_1",
            "关于对云块的研究 I",
            List.of("text.confluence.research_on_cloud_blocks_1")
    );
    public static final ItemStack RESEARCH_ON_CLOUD_BLOCKS_2 = createWrittenBook(
            4,
            "item.confluence.research_on_cloud_blocks_2",
            "author.confluence.lorissa",
            "lore.confluence.research_on_cloud_blocks_2",
            "关于对云块的研究 II",
            List.of("text.confluence.research_on_cloud_blocks_2")
    );
    public static final ItemStack METEOR_DIARY = createWrittenBook(
            5,
            "item.confluence.meteor_diary",
            "author.confluence.annaleigh",
            "lore.confluence.meteor_diary",
            "流星日记",
            List.of("text.confluence.meteor_diary")
    );

    private static ItemStack createMural(CompoundTag data, String translationKey) {
        ItemStack stack = new ItemStack(DecorativeBlocks.MURAL_BLOCK.get());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(data));
        stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.translatable(translationKey)
                        .withStyle(ChatFormatting.GRAY)
                        .withStyle(style -> style.withItalic(false))
        )));
        return stack;
    }

    private static CompoundTag parseSnbt(String snbt) {
        try {
            return TagParser.parseTag(snbt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse mural SNBT!", e);
        }
    }

    private static ItemStack createWrittenBook(
            int customModelData,
            String titleKey,
            String authorKey,
            String loreKey,
            String rawTitleStr,
            List<String> pageKeys
    ) {
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(customModelData));
        stack.set(DataComponents.RARITY, Rarity.RARE);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);

        stack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

        stack.set(DataComponents.CUSTOM_NAME, Component.translatable(titleKey).withStyle(s -> s.withItalic(false)));

        Component authorLine = Component.translatable("book.byAuthor", Component.translatable(authorKey))
                .withStyle(ChatFormatting.GRAY)
                .withStyle(s -> s.withItalic(false));
        Component descLine = Component.translatable(loreKey)
                .withStyle(ChatFormatting.DARK_GRAY)
                .withStyle(s -> s.withItalic(false));
        stack.set(DataComponents.LORE, new ItemLore(List.of(authorLine, Component.empty(), descLine, Component.empty())));

        Filterable<String> title = Filterable.passThrough(rawTitleStr);
        List<Filterable<Component>> pages = pageKeys.stream()
                .map(key -> Filterable.<Component>passThrough(
                        Component.translatable(key).withStyle(s -> s.withItalic(false))
                ))
                .toList();

        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(title, "Dev", 0, pages, true));

        return stack;
    }

    public static ItemStack createMysteriousNote(String titleKey, List<Component> loreLines) {
        ItemStack stack = new ItemStack(ModItems.MYSTERIOUS_NOTE.get());

        stack.set(DataComponents.CUSTOM_NAME,
                Component.translatable(titleKey).withStyle(style -> style.withColor(ChatFormatting.WHITE).withItalic(false)));

        stack.set(DataComponents.LORE, new ItemLore(loreLines));

        return stack;
    }

    public static Component makeComponent(
            boolean isTranslatable,
            @Nullable Integer color,
            boolean italic,
            boolean bold,
            boolean strikethrough,
            boolean underline,
            boolean obfuscated,
            @Nullable ResourceLocation font,
            String content
    ) {
        MutableComponent component = isTranslatable ? Component.translatable(content) : Component.literal(content);

        component.withStyle(style -> {
            if (color != null) {
                style = style.withColor(TextColor.fromRgb(color));
            }
            if (font != null) {
                style = style.withFont(font);
            }
            return style
                    .withItalic(italic)
                    .withBold(bold)
                    .withStrikethrough(strikethrough)
                    .withUnderlined(underline)
                    .withObfuscated(obfuscated);
        });

        return component;
    }

    public static Component makeComponent(
            boolean isTranslatable,
            @Nullable Integer color,
            boolean italic,
            boolean bold,
            boolean strikethrough,
            boolean underline,
            boolean obfuscated,
            String content
    ) {
        return makeComponent(isTranslatable, color, italic, bold, strikethrough, underline, obfuscated, null, content);
    }

    public static MutableComponent appendComponent(Component first, Component second) {
        return first.copy().append(second);
    }
}
