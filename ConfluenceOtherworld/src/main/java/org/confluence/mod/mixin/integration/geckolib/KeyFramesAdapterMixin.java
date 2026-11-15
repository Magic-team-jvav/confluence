package org.confluence.mod.mixin.integration.geckolib;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.ParticleKeyframeData;
import software.bernie.geckolib.animation.keyframe.event.data.SoundKeyframeData;
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// 让 GeckoLib 支持基岩版风格的“同一时间码多个效果”写法：
/// <pre>
/// "particle_effects": { "0.5": [ { "effect": "minecraft:flame" }, { "effect": "minecraft:smoke" } ] }
/// "sound_effects":    { "0.5": [ { "effect": "..." }, { "effect": "..." } ] }
/// </pre>
/// GeckoLib 原生实现是 `entry.getValue().getAsJsonObject()`，遇到数组会抛
/// `IllegalStateException: Not a JSON Object`，因此这里在反序列化入口直接接管：
/// 只在检测到数组写法时自行构建 [Animation.Keyframes]，其它情况（含时间线 timeline、
/// 普通对象写法）完全走 GeckoLib 原逻辑，保证行为一致。
///
/// 兼容性说明：
///
///   - GeckoLib 4.8.x 中 `particle_effects` 只被这个 adapter 读取，所以一个注入点即可。
///   - 这里**不做任何时间偏移**：数组里的每个元素都使用完全相同的 `startTick`。
///     所以“同一时间码 + 内容完全相同”的条目会被 `AnimationController` 的 `Set#add`
///     去重（`KeyFrameData#equals` 比较的是 `hashCode`）而只触发一次；
///     只要 effect / locator / pre_effect_script 任一不同就互不影响，全部正常触发。
///   - GeckoLib 5 的动画加载结构完全不同（`ActorAnimation` + `ActorAnimationParticleEffect`），
///     升级时需要另写一份。
///
@Mixin(value = KeyFramesAdapter.class, remap = false)
public abstract class KeyFramesAdapterMixin {
    /// 这里写明完整描述符：`JsonDeserializer` 会生成一个返回 `Object` 的 bridge 方法，
    /// 只写方法名会让 Mixin 有可能选错目标（`injectors.defaultRequire = 1` 时会直接崩）。
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lsoftware/bernie/geckolib/animation/Animation$Keyframes;", at = @At("HEAD"), cancellable = true)
    private void confluence$supportArrayEffects(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<Animation.Keyframes> cir) {
        JsonObject root = json.getAsJsonObject();
        if (!(root.get("particle_effects") instanceof JsonObject) && !(root.get("sound_effects") instanceof JsonObject)) {
            return;
        }

        // 没有数组写法就交回 GeckoLib 原生实现，行为完全不变
        if (!confluence$containsArray(root, "particle_effects") && !confluence$containsArray(root, "sound_effects")) {
            return;
        }

        cir.setReturnValue(new Animation.Keyframes(
                confluence$buildSounds(root),
                confluence$buildParticles(root),
                confluence$buildCustomInstructions(root)
        ));
    }

    /// 某个效果字段下是否有“数组写法”的条目
    @Unique
    private static boolean confluence$containsArray(JsonObject root, String key) {
        if (!(root.get(key) instanceof JsonObject effects)) {
            return false;
        }

        for (Map.Entry<String, JsonElement> entry : effects.entrySet()) {
            if (entry.getValue() instanceof JsonArray) {
                return true;
            }
        }

        return false;
    }

    @Unique
    private static SoundKeyframeData[] confluence$buildSounds(JsonObject root) {
        JsonObject sounds = confluence$getObject(root, "sound_effects");
        List<SoundKeyframeData> result = new ArrayList<>(sounds.size());

        for (Map.Entry<String, JsonElement> entry : sounds.entrySet()) {
            double tick = Double.parseDouble(entry.getKey()) * 20.0D;

            for (JsonObject effect : confluence$effectObjects(entry.getValue())) {
                result.add(new SoundKeyframeData(tick, confluence$getString(effect, "effect", "")));
            }
        }

        return result.toArray(new SoundKeyframeData[0]);
    }

    @Unique
    private static ParticleKeyframeData[] confluence$buildParticles(JsonObject root) {
        JsonObject particles = confluence$getObject(root, "particle_effects");
        List<ParticleKeyframeData> result = new ArrayList<>(particles.size());

        for (Map.Entry<String, JsonElement> entry : particles.entrySet()) {
            double tick = Double.parseDouble(entry.getKey()) * 20.0D;

            for (JsonObject effect : confluence$effectObjects(entry.getValue())) {
                result.add(new ParticleKeyframeData(
                        tick,
                        confluence$getString(effect, "effect", ""),
                        confluence$getString(effect, "locator", ""),
                        confluence$getString(effect, "pre_effect_script", "")
                ));
            }
        }

        return result.toArray(new ParticleKeyframeData[0]);
    }

    /// timeline 通道本身就支持数组，这里保持与 GeckoLib 原生实现一致的语义
    @Unique
    private static CustomInstructionKeyframeData[] confluence$buildCustomInstructions(JsonObject root) {
        JsonObject timeline = confluence$getObject(root, "timeline");
        List<CustomInstructionKeyframeData> result = new ArrayList<>(timeline.size());

        for (Map.Entry<String, JsonElement> entry : timeline.entrySet()) {
            JsonElement value = entry.getValue();
            String instructions = "";

            if (value instanceof JsonArray array) {
                instructions = KeyFramesAdapter.GEO_GSON.fromJson(array, ObjectArrayList.class).toString();
            } else if (value instanceof JsonPrimitive primitive) {
                instructions = primitive.getAsString();
            }

            result.add(new CustomInstructionKeyframeData(Double.parseDouble(entry.getKey()) * 20.0D, instructions));
        }

        return result.toArray(new CustomInstructionKeyframeData[0]);
    }

    /// 把一个时间码下的值统一成“效果对象列表”：对象 -> 1 个，数组 -> 多个（跳过 null 与非对象元素）
    @Unique
    private static List<JsonObject> confluence$effectObjects(JsonElement value) {
        if (value instanceof JsonArray array) {
            List<JsonObject> objects = new ArrayList<>(array.size());

            for (JsonElement element : array) {
                if (element instanceof JsonObject object) {
                    objects.add(object);
                }
            }

            return objects;
        }

        return value instanceof JsonObject object ? List.of(object) : List.of();
    }

    @Unique
    private static JsonObject confluence$getObject(JsonObject root, String key) {
        return root.get(key) instanceof JsonObject object ? object : new JsonObject();
    }

    @Unique
    private static String confluence$getString(JsonObject root, String key, String fallback) {
        return root.get(key) instanceof JsonPrimitive primitive ? primitive.getAsString() : fallback;
    }
}
