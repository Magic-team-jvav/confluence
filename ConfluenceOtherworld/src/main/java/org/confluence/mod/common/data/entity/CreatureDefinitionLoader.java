package org.confluence.mod.common.data.entity;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.Confluence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 生物定义的数据包重载器，读取 {@code data/<namespace>/entity_definition/*.json}。
 *
 * <p>监听器通过 PortLib 的通用重载事件安装，因此 Forge 1.20.1 与后续 1.21 同步侧共享相同的数据目录语义。
 * KubeJS 的 {@code kubejs/data} 本身就是数据包来源，因此也会经过同一条加载链，不需要专用分支。
 * 每轮重载先构造完整的新映射，再以不可变快照一次性替换，避免服务器线程读取到半成品。</p>
 *
 * <p>文件的资源位置必须与目标实体注册 ID 完全一致。例如
 * {@code kubejs/data/confluence/entity_definition/face_monster.json}
 * 对应 {@code confluence:face_monster}。若要覆盖其他模组实体，则将目录中的命名空间换成目标模组 ID。</p>
 */
public final class CreatureDefinitionLoader extends SimpleJsonResourceReloadListener {
    private static final Set<String> ATTRIBUTE_FIELDS = Set.of(
            "max_health", "attack_damage", "armor", "movement_speed",
            "follow_range", "knockback_resistance");
    private static final Set<String> BEHAVIOR_FIELDS = Set.of(
            "move_speed", "melee_range", "wander_speed", "wander_radius",
            "idle_ticks", "charge_speed", "windup_ticks", "shot_cooldown",
            "shot_multiplier", "preferred_range", "orbit_speed", "orbit_radius");

    /**
     * 当前重载轮次的只读快照；volatile 保证网络/服务器线程看到完整替换结果。
     */
    private static volatile Map<EntityType<?>, CreatureDefinition> definitions = Map.of();

    public CreatureDefinitionLoader() {
        super(new GsonBuilder().create(), "entity_definition");
    }

    /**
     * 返回实体类型对应的定义；没有定义时返回共享空对象而不是 {@code null}。
     */
    public static CreatureDefinition get(EntityType<?> type) {
        return definitions.getOrDefault(type, CreatureDefinition.EMPTY);
    }

    /**
     * 将定义中的属性基础值应用到新建生物。
     *
     * <p>若实体应用前处于满血，则最大生命变化后继续保持满血；否则只在旧生命超过新上限时截断，
     * 防止重载或构造阶段意外治疗受伤实体。</p>
     */
    public static void applyAttributes(Mob mob) {
        CreatureDefinition.AttributeOverrides overrides = get(mob.getType()).attributes();
        float oldHealth = mob.getHealth();
        float oldMaxHealth = mob.getMaxHealth();
        boolean wasFullHealth = Math.abs(oldHealth - oldMaxHealth) < 0.001F;

        setBaseValue(mob, Attributes.MAX_HEALTH, overrides.maxHealth());
        setBaseValue(mob, Attributes.ATTACK_DAMAGE, overrides.attackDamage());
        setBaseValue(mob, Attributes.ARMOR, overrides.armor());
        setBaseValue(mob, Attributes.MOVEMENT_SPEED, overrides.movementSpeed());
        setBaseValue(mob, Attributes.FOLLOW_RANGE, overrides.followRange());
        setBaseValue(mob, Attributes.KNOCKBACK_RESISTANCE, overrides.knockbackResistance());

        if (wasFullHealth) {
            mob.setHealth(mob.getMaxHealth());
        } else if (oldHealth > mob.getMaxHealth()) {
            mob.setHealth(mob.getMaxHealth());
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
                         ProfilerFiller profiler) {
        // 先验证整批文件，再一次性替换快照。任意文件损坏时保留上一轮完整结果，
        // 避免拼写或类型错误让部分实体静默恢复默认值。
        Map<EntityType<?>, CreatureDefinition> loaded = new HashMap<>();
        boolean invalidBatch = false;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation id = entry.getKey();
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
            if (type == null) {
                Confluence.LOGGER.warn("Creature definition {} does not match a registered entity", id);
                invalidBatch = true;
                continue;
            }
            if (!hasValidShape(id, entry.getValue())) {
                invalidBatch = true;
                continue;
            }

            DataResult<CreatureDefinition> result = CreatureDefinition.CODEC
                    .parse(JsonOps.INSTANCE, entry.getValue());
            CreatureDefinition definition = result
                    .resultOrPartial(message -> Confluence.LOGGER.warn(
                            "Invalid creature definition {}: {}", id, message))
                    .orElse(null);
            if (definition == null || result.error().isPresent()) {
                invalidBatch = true;
                continue;
            }
            loaded.put(type, definition);
        }
        if (invalidBatch) {
            Confluence.LOGGER.warn(
                    "Creature definition reload was rejected; the previous valid snapshot remains active");
            return;
        }
        definitions = Map.copyOf(loaded);
        Confluence.LOGGER.info("Loaded {} creature definitions", definitions.size());
    }

    /**
     * 1.20.1 的可选 Codec 会把部分字段类型错误当作“字段缺失”。这里仅补上 JSON 形状检查，
     * 数值范围和最终对象构造仍由 {@link CreatureDefinition#CODEC} 负责。
     */
    private static boolean hasValidShape(ResourceLocation id, JsonElement element) {
        if (!element.isJsonObject()) {
            Confluence.LOGGER.warn("Creature definition {} must be a JSON object", id);
            return false;
        }
        boolean attributesValid = hasNumericFields(
                id, element, "attributes", ATTRIBUTE_FIELDS);
        boolean behaviorValid = hasNumericFields(
                id, element, "behavior", BEHAVIOR_FIELDS);
        return attributesValid && behaviorValid;
    }

    private static boolean hasNumericFields(ResourceLocation id, JsonElement root,
                                            String sectionName, Set<String> fields) {
        JsonElement section = root.getAsJsonObject().get(sectionName);
        if (section == null) {
            return true;
        }
        if (!section.isJsonObject()) {
            Confluence.LOGGER.warn(
                    "Creature definition {} field '{}' must be a JSON object", id, sectionName);
            return false;
        }
        boolean valid = true;
        for (String field : fields) {
            JsonElement value = section.getAsJsonObject().get(field);
            if (value != null && (!value.isJsonPrimitive()
                    || !value.getAsJsonPrimitive().isNumber())) {
                Confluence.LOGGER.warn(
                        "Creature definition {} field '{}.{}' must be a number",
                        id, sectionName, field);
                valid = false;
            }
        }
        return valid;
    }

    private static void setBaseValue(Mob mob, Attribute attribute, double value) {
        // 负数、NaN 和无穷值均表示无效覆盖；缺少该属性的实体也安全跳过。
        if (!Double.isFinite(value) || value < 0) return;
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    }
}
