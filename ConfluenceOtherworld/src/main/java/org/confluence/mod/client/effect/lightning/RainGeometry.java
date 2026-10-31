// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import com.google.gson.Gson;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/** A bounded field of mostly vertical bolts with intentionally shallow branching. */
final class RainGeometry {
    record Bolt(BoltGeometry geometry, double delay, LightningRecipe recipe, ImpactGeometry impact) {}
    private RainGeometry() {}

    static boolean finished(List<Bolt> bolts, double ticks) {
        for (Bolt bolt : bolts) {
            double duration=Math.max(bolt.recipe().durationTicks,bolt.recipe().impactEnabled?bolt.recipe().progressionTicks+bolt.recipe().impactDuration:0);
            if(ticks-bolt.delay()<duration)return false;
        }
        return true;
    }

    static List<Bolt> generate(LightningRecipe recipe) { return generate(recipe, Vec3.ZERO); }
    static List<Bolt> generate(LightningRecipe recipe, Vec3 anchor) {
        return generate(recipe,anchor,(start,end)->end);
    }
    static List<Bolt> generate(LightningRecipe recipe, Vec3 anchor,BiFunction<Vec3,Vec3,Vec3> ground) {
        List<Bolt> out = new ArrayList<>(recipe.rainCount);
        Gson gson = new Gson();
        for (int i = 0; i < recipe.rainCount; i++) {
            long seed = recipe.seed + 104729L * (i + 1);
            boolean cluster=recipe.rainStyle.equals("cluster");
            double angle=i*2.399963229728653+LightningMath.unit(recipe.seed,70)*Math.PI*2;
            double radius=Math.sqrt((i+.5)/recipe.rainCount),u=(i+.5)/recipe.rainCount-.5;
            double x=cluster?Math.cos(angle)*radius*recipe.rainSpread*.5:u*recipe.rainSpread+LightningMath.signed(seed,1)*recipe.rainSpread/recipe.rainCount*.35;
            double z=cluster?Math.sin(angle)*radius*recipe.rainDepthSpread*.5:LightningMath.signed(seed,2)*recipe.rainDepthSpread;
            Vec3 end=anchor.add(x+(cluster?0:LightningMath.signed(seed,3)*.35),0,z+(cluster?0:LightningMath.signed(seed,4)*.35));
            Vec3 start = anchor.add(x, recipe.strikeHeight, z);
            end=ground.apply(start,end);
            LightningRecipe child = gson.fromJson(gson.toJson(recipe), LightningRecipe.class);
            child.mode = "strike";
            child.seed = seed;
            child.branchProbability = Math.min(recipe.rainBranchProbability, recipe.branchProbability);
            child.maxBranchDepth = Math.min(recipe.rainMaxBranchDepth, 1);
            child.branchLimit = recipe.rainMaxBranches;
            child.branchLength = Math.min(recipe.branchLength, .15);
            child.branchJitter = Math.min(recipe.branchJitter, .12);
            out.add(new Bolt(BoltGeometry.generate(start, end, child), i * recipe.rainStagger, child,new ImpactGeometry(end,child)));
        }
        return out;
    }
}
