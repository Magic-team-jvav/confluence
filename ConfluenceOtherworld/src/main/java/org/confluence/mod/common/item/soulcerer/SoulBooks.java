package org.confluence.mod.common.item.soulcerer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoulSkills;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// 灵魂技能书定义表：书籍 ID 同时决定外观序号、稀有度、永久解锁的技能与使用前置。
public final class SoulBooks {
    public static final ResourceLocation PSYCHOPOMP = Confluence.asResource("psychopomp");
    public static final ResourceLocation RAIDERS_PATH = Confluence.asResource("raiders_path");
    /// 灵魂回响（原「灵魂激荡」书）：不再解锁灵魂激荡，改为给灵魂激荡加 20% 免费复读的被动。
    public static final ResourceLocation SOUL_ECHO = Confluence.asResource("soul_echo");
    public static final ResourceLocation HARVESTER = Confluence.asResource("harvester");
    /// 灵魂缓冲（原「天穹倒转」）。
    public static final ResourceLocation SOUL_BUFFER = Confluence.asResource("soul_buffer");
    public static final ResourceLocation BLOOD_WORLD_WRATH = Confluence.asResource("blood_world_wrath");

    private static final Map<ResourceLocation, Definition> ALL = new LinkedHashMap<>();
    private static final List<Definition> STYLED = new ArrayList<>();

    /// @param id       书籍 ID，同时也是贴图名与名称键后缀
    /// @param rarity   书籍稀有度，写入物品的 MOD_RARITY 组件
    /// @param skill    永久解锁的技能；为 null 表示只提供被动（引渡者）
    /// @param required 使用前必须已永久学会的技能；为 null 表示无前置
    public record Definition(ResourceLocation id, ModRarity rarity, @Nullable ResourceLocation skill, @Nullable ResourceLocation required) {}

    static {
        // 独立书皮的技能书，登记顺序即模型覆盖序号（从 1 开始）
        styled(PSYCHOPOMP, ModRarity.BLUE, null, null);
        styled(RAIDERS_PATH, ModRarity.BLUE, ModSoulSkills.BOILING_BLOOD.get().getId(), null);
        // 灵魂回响：只提供被动（灵魂激荡 20% 概率免费复读），因此 skill 为 null
        styled(SOUL_ECHO, ModRarity.GREEN, null, null);
        // 收割者：解锁灵魂激荡冲击（surge_blast）。灵魂激荡已是开局技能，因此不再需要前置。
        styled(HARVESTER, ModRarity.ORANGE, ModSoulSkills.SURGE_BLAST.get().getId(), null);
        styled(SOUL_BUFFER, ModRarity.GREEN, ModSoulSkills.SOUL_BUFFER.get().getId(), null);
        styled(BLOOD_WORLD_WRATH, ModRarity.ORANGE, ModSoulSkills.BLOOD_RAGE.get().getId(), null);

        // 沿用通用书皮的既有技能书
        plain(ModSoulSkills.SURVIVAL_INSTINCT.get().getId(), ModRarity.WHITE, ModSoulSkills.SURVIVAL_INSTINCT.get().getId(), null);
    }

    /// 个别书籍学成时使用的专属文案，未登记时回落到通用文案。
    private static final Map<ResourceLocation, String> USE_MESSAGE_KEYS = Map.of(
            HARVESTER, "message.confluence.soul_book.harvester"
    );

    /// 只提供被动的技能书的说明键（书里没有绑定技能，无法自动拼出效果说明）。
    private static final Map<ResourceLocation, String> PASSIVE_TOOLTIP_KEYS = Map.of(
            PSYCHOPOMP, "tooltip.confluence.soulcerer_skill_book.psychopomp",
            SOUL_ECHO, "tooltip.confluence.soulcerer_skill_book.soul_echo"
    );

    /// 旧存档兼容：改过 id 的技能书继续能读（`soul_surge` 就是改名前的「灵魂激荡」书）。
    private static final Map<ResourceLocation, ResourceLocation> LEGACY_IDS = Map.of(
            Confluence.asResource("firmament_reversed"), SOUL_BUFFER,
            Confluence.asResource("soul_surge"), SOUL_ECHO
    );

    private static void styled(ResourceLocation id, ModRarity rarity, @Nullable ResourceLocation skill, @Nullable ResourceLocation required) {
        Definition definition = new Definition(id, rarity, skill, required);
        ALL.put(id, definition);
        STYLED.add(definition);
    }

    private static void plain(ResourceLocation id, ModRarity rarity, @Nullable ResourceLocation skill, @Nullable ResourceLocation required) {
        ALL.put(id, new Definition(id, rarity, skill, required));
    }

    /// 按书籍 ID 读取定义，未登记时返回 null；改过 id 的旧书会回落到新定义。
    @Nullable
    public static Definition get(@Nullable ResourceLocation id) {
        if (id == null) {
            return null;
        }
        Definition definition = ALL.get(id);
        if (definition != null) {
            return definition;
        }
        ResourceLocation legacy = LEGACY_IDS.get(id);
        return legacy == null ? null : ALL.get(legacy);
    }

    /// 返回全部技能书，顺序为登记顺序。
    public static List<Definition> all() {
        return List.copyOf(ALL.values());
    }

    /// 读取书籍稀有度，未登记时回落白色。
    public static ModRarity rarity(ResourceLocation id) {
        Definition definition = ALL.get(id);
        return definition == null ? ModRarity.WHITE : definition.rarity();
    }

    /// 返回带独立书皮的技能书，顺序与模型覆盖序号一致。
    public static List<Definition> styled() {
        return List.copyOf(STYLED);
    }

    /// 返回书籍的模型覆盖序号，1 起算；通用书皮返回 0，对应模型基础书皮。
    public static float styleIndex(@Nullable ResourceLocation id) {
        if (id == null) {
            return 0.0F;
        }
        for (int i = 0; i < STYLED.size(); i++) {
            if (STYLED.get(i).id().equals(id)) {
                return i + 1.0F;
            }
        }
        return 0.0F;
    }

    /// 读取物品上绑定的书籍，取消绑定时返回 null。
    @Nullable
    public static Definition of(ItemStack stack) {
        return get(stack.get(ModDataComponentTypes.SOUL_SKILL.get()));
    }

    /// 读取物品对应的模型覆盖序号。
    public static float styleIndex(ItemStack stack) {
        Definition definition = of(stack);
        return definition == null ? 0.0F : styleIndex(definition.id());
    }

    /// 生成书籍的名称翻译键。
    public static String nameKey(ResourceLocation id) {
        return "confluence.soul_book." + id.getPath() + ".name";
    }

    /// 读取书籍学成时的专属文案键，未登记时返回 null。
    @Nullable
    public static String useMessageKey(ResourceLocation id) {
        return USE_MESSAGE_KEYS.get(id);
    }

    /// 读取只提供被动的技能书的说明键，未登记时返回 null。
    @Nullable
    public static String passiveTooltipKey(ResourceLocation id) {
        return PASSIVE_TOOLTIP_KEYS.get(id);
    }

    private SoulBooks() {}
}
