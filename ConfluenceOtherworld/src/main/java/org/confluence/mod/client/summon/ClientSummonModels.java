package org.confluence.mod.client.summon;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.SummonAnimation;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Map;

final class ClientSummonModels {
    static final Map<ResourceLocation, Binding> MODELS = Map.of();

    private ClientSummonModels() {}

    enum Material {CUTOUT, DOUBLE_SIDED, SLIME}

    record Animations(RawAnimation idle, RawAnimation moving,
                      Map<SummonAnimation, RawAnimation> actions, int transitionTicks) {
        RawAnimation select(SummonAnimation action, boolean isMoving) {
            return actions.getOrDefault(action, isMoving ? moving : idle);
        }
    }

    record Binding(ResourceLocation model, ResourceLocation texture, ResourceLocation animation,
                   Material material,
                   Animations animations, float scale, float offsetY, float yawOffset,
                   boolean rotatesWithPitch) {
        private Binding(String model, String texture, String animation, Material material, Animations animations) {
            this(Confluence.asResource("geo/entity/" + model + ".geo.json"),
                    Confluence.asResource("textures/entity/" + texture + ".png"),
                    Confluence.asResource("animations/entity/" + animation + ".animation.json"), material, animations, 1, 0, 0, false);
        }

        private Binding transform(float scale, float offsetY, float yawOffset, boolean rotatesWithPitch) {
            return new Binding(model, texture, animation, material, animations, scale, offsetY, yawOffset, rotatesWithPitch);
        }
    }
}
