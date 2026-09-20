package org.confluence.mod.client.summon;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.common.summon.SummonTypes;
import org.confluence.mod.common.summon.ground.DesertTigerSummon;
import org.confluence.mod.common.summon.ground.SpiderSummon;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Map;

final class ClientSummonModels {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation STRIKE = RawAnimation.begin().thenLoop("attack.strike");
    private static final RawAnimation ROLL = RawAnimation.begin().thenLoop("attack.roll");
    private static final Animations WALKING = new Animations(IDLE, WALK, Map.of(), 4);
    private static final Animations TIGER = new Animations(IDLE, WALK, Map.of(SummonAnimation.SPIN_X, ROLL, SummonAnimation.MELEE_ATTACK, STRIKE), 4);

    static final Map<ResourceLocation, Binding> MODELS = Map.ofEntries(
            Map.entry(SpiderSummon.VENOM, model("summon/spider", Material.CUTOUT, WALKING)),
            Map.entry(SpiderSummon.JUMPER, new Binding("summon/spider", "summon/spider/jumper", "summon/spider", Material.CUTOUT, WALKING)),
            Map.entry(SpiderSummon.DANGEROUS, new Binding("summon/spider", "summon/spider/dangerous", "summon/spider", Material.CUTOUT, WALKING)),
            Map.entry(DesertTigerSummon.TIER1, model("summon/desert_tiger_tier1", Material.DOUBLE_SIDED, TIGER)),
            Map.entry(DesertTigerSummon.TIER2, model("summon/desert_tiger_tier2", Material.DOUBLE_SIDED, TIGER)),
            Map.entry(DesertTigerSummon.TIER3, model("summon/desert_tiger_tier3", Material.DOUBLE_SIDED, TIGER))
    );

    private ClientSummonModels() {}

    private static Binding model(String resource, Material material, Animations animations) {
        return new Binding(resource, resource, resource, material, animations);
    }

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
