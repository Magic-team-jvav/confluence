package org.confluence.mod.common.block.functional.terraform;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.confluence.mod.common.block.natural.spreadable.conversion_table.ConversionTable;

import java.util.*;

// 机器专用的数据包改造表，不调用自然感染的概率、阶段门槛和蔓延逻辑。
public final class MachineConversionTable extends SimpleJsonResourceReloadListener {
    public static final MachineConversionTable INSTANCE = new MachineConversionTable();
    private Map<Solution, Table> tables = Map.of();

    private record Rule(Block block, TagKey<Block> tag, Block target,
                        Map<String, String> properties) {
        boolean matches(BlockState state) {
            if (!(block != null ? state.is(block) : state.is(tag))) return false;
            for (var entry : properties.entrySet()) {
                Property<?> property = state.getBlock().getStateDefinition().getProperty(entry.getKey());
                if (property == null || !valueName(state, property).equals(entry.getValue()))
                    return false;
            }
            return true;
        }

        private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {return property.getName(state.getValue(property));}
    }

    // 复用现有转换表基类的状态缓存和属性继承，只把机器规则与自然感染分开。
    private static final class Table extends ConversionTable {
        private final List<Rule> rules;

        private Table(List<Rule> rules) {this.rules = List.copyOf(rules);}

        @Override
        protected Block getTarget(BlockState source, boolean hardmode) {
            for (Rule rule : rules) if (rule.matches(source)) return rule.target;
            return null;
        }
    }

    private MachineConversionTable() {super(new Gson(), "machine_conversion");}

    // 整张表验证成功才替换；未知方块立即报告，避免静默变成空气。
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        Map<Solution, List<Rule>> loaded = new EnumMap<>(Solution.class);
        resources.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            Solution solution = Solution.valueOf(entry.getValue().getAsJsonObject().get("solution").getAsString().toUpperCase(Locale.ROOT));
            List<Rule> rules = loaded.computeIfAbsent(solution, key -> new ArrayList<>());
            for (JsonElement element : entry.getValue().getAsJsonObject().getAsJsonArray("rules")) {
                JsonObject json = element.getAsJsonObject();
                String source = json.get("source").getAsString();
                rules.add(new Rule(source.startsWith("#") ? null : block(source),
                        source.startsWith("#") ? TagKey.create(Registries.BLOCK, ResourceLocation.parse(source.substring(1))) : null,
                        block(json.get("target").getAsString()), properties(json)));
            }
        });
        Map<Solution, Table> next = new EnumMap<>(Solution.class);
        loaded.forEach((solution, rules) -> next.put(solution, new Table(rules)));
        tables = next;
    }

    private static Block block(String id) {
        ResourceLocation key = ResourceLocation.parse(id);
        if (!BuiltInRegistries.BLOCK.containsKey(key))
            throw new IllegalArgumentException("Unknown machine conversion block: " + id);
        return BuiltInRegistries.BLOCK.get(key);
    }

    private static Map<String, String> properties(JsonObject json) {
        Map<String, String> properties = new HashMap<>();
        if (json.has("properties"))
            json.getAsJsonObject("properties").entrySet().forEach(e -> properties.put(e.getKey(), e.getValue().getAsString()));
        return Map.copyOf(properties);
    }

    // 显式改造不受自然感染的困难模式门槛控制；没有匹配规则时保留原方块。
    public BlockState convert(Solution solution, BlockState source) {
        Table table = tables.get(solution);
        if (table == null) return source;
        BlockState result = table.get(source, true);
        return result == null ? source : result;
    }
}
