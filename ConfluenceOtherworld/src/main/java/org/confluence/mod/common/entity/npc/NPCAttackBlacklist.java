package org.confluence.mod.common.entity.npc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.Confluence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/// 城镇 NPC 主动攻击黑名单；配置重载时编译规则，索敌时只做匹配。
public final class NPCAttackBlacklist {
    private static volatile Rules rules = new Rules(Set.of(), Set.of(), Set.of(), List.of());

    private NPCAttackBlacklist() {}

    /// 与弹药栏黑名单一样，裸命名空间匹配 modid，完整 ID 匹配实体，# 前缀匹配标签；regex: 匹配完整 ID。
    public static boolean isValid(String entry) {
        try {
            parse(entry);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /// 无效条目跳过并记录；配置加载和重载都调用这里。
    public static void reload(List<? extends String> entries) {
        Set<String> modIds = new HashSet<>();
        Set<ResourceLocation> ids = new HashSet<>();
        Set<TagKey<EntityType<?>>> tags = new HashSet<>();
        List<Pattern> patterns = new ArrayList<>();
        for (String entry : entries) {
            try {
                Rule rule = parse(entry);
                switch (rule.kind()) {
                    case MODID -> modIds.add(rule.value());
                    case ID -> ids.add(ResourceLocation.parse(rule.value()));
                    case TAG ->
                            tags.add(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(rule.value())));
                    case REGEX -> patterns.add(Pattern.compile(rule.value()));
                }
            } catch (IllegalArgumentException exception) {
                Confluence.LOGGER.warn("Ignoring invalid NPC attack blacklist entry: {}", entry, exception);
            }
        }
        rules = new Rules(Set.copyOf(modIds), Set.copyOf(ids), Set.copyOf(tags), List.copyOf(patterns));
    }

    /// 命中任一规则就不允许 NPC 选择该实体作为攻击目标。
    public static boolean contains(EntityType<?> type) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
        if (id == null) return false;
        Rules current = rules;
        if (current.modIds().contains(id.getNamespace()) || current.ids().contains(id)) return true;
        for (TagKey<EntityType<?>> tag : current.tags()) {
            if (type.is(tag)) return true;
        }
        String fullId = id.toString();
        for (Pattern pattern : current.patterns()) {
            if (pattern.matcher(fullId).matches()) return true;
        }
        return false;
    }

    private static Rule parse(String entry) {
        if (entry == null) throw new IllegalArgumentException("null entry");
        if (entry.startsWith("#")) return new Rule(Kind.TAG, requireLocation(entry.substring(1)));
        if (entry.startsWith("regex:")) {
            String expression = entry.substring("regex:".length());
            try {
                Pattern.compile(expression);
            } catch (PatternSyntaxException exception) {
                throw new IllegalArgumentException("invalid regular expression", exception);
            }
            return new Rule(Kind.REGEX, expression);
        }
        if (entry.startsWith("modid:") || entry.startsWith("id:") || entry.startsWith("tag:"))
            throw new IllegalArgumentException("obsolete prefixed blacklist entry");
        if (entry.contains(":")) return new Rule(Kind.ID, requireLocation(entry));
        return new Rule(Kind.MODID, requireModId(entry));
    }

    private static String requireModId(String value) {
        if (value.isEmpty() || value.indexOf(':') >= 0 || ResourceLocation.tryParse(value + ":entity") == null)
            throw new IllegalArgumentException("invalid mod ID");
        return value;
    }

    private static String requireLocation(String value) {
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null || !value.contains(":"))
            throw new IllegalArgumentException("invalid full resource location");
        return id.toString();
    }

    private enum Kind {MODID, ID, TAG, REGEX}

    private record Rule(Kind kind, String value) {}

    private record Rules(Set<String> modIds, Set<ResourceLocation> ids,
                         Set<TagKey<EntityType<?>>> tags, List<Pattern> patterns) {}
}
