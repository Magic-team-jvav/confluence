// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.ArrayDeque;
import java.util.Comparator;

/** Deterministic midpoint-displacement channel tree. Endpoints are never displaced. */
final class BoltGeometry {
    record Segment(Vec3 a, Vec3 b, float widthA, float widthB, float intensity, float alongA, float alongB, int depth, long mask) {}
    private final LightningRecipe recipe;
    private final List<Segment> segments = new ArrayList<>();
    private int budget, branches;
    private long nextPath;
    private boolean hierarchical;
    private record Pending(Vec3 start,Vec3 end,long seed,int depth,double alongStart,double alongEnd,double intensity) {}
    private final ArrayDeque<Pending> pending=new ArrayDeque<>();
    private BoltGeometry(LightningRecipe recipe) { this.recipe = recipe; this.budget = recipe.maxSegments; }
    static BoltGeometry generate(Vec3 start, Vec3 end, LightningRecipe recipe) {
        BoltGeometry g = new BoltGeometry(recipe);
        g.branch(start,end,recipe.seed,0,0,1,1);
        // Breadth-first: complete the primary branches before spending budget
        // on their descendants. Pending siblings share the remaining budget.
        while(!g.pending.isEmpty()&&g.budget>=8&&g.branches<recipe.branchLimit){
            Pending next=g.pending.removeFirst();g.branches++;
            g.branch(next.start,next.end,next.seed,next.depth,next.alongStart,next.alongEnd,next.intensity);
        }
        return g;
    }
    List<Segment> segments() { return segments; }
    /** Host topology: reserve branch slots for twigs rather than exhausting them on siblings. */
    static BoltGeometry generateThreeLevel(Vec3 start, Vec3 end, LightningRecipe recipe) {
        BoltGeometry g = new BoltGeometry(recipe);
        g.hierarchical = true;
        g.branch(start, end, recipe.seed, 0, 0, 1, 1);
        int primary = 0;
        while (!g.pending.isEmpty() && g.budget >= 8 && g.branches < recipe.branchLimit) {
            Pending next = g.pending.removeFirst();
            if (next.depth == 1 && primary >= Math.max(1, recipe.branchLimit / 2)) continue;
            if (next.depth == 1) primary++;
            g.branches++;
            g.branch(next.start, next.end, next.seed, next.depth, next.alongStart, next.alongEnd, next.intensity);
        }
        return g;
    }
    static BoltGeometry sampled(LightningRecipe recipe, List<Segment> segments) {
        if (segments.size() > recipe.maxSegments) throw new IllegalArgumentException("Lightning segment budget exceeded");
        BoltGeometry result = new BoltGeometry(recipe);
        result.segments.addAll(segments);
        return result;
    }
    private void branch(Vec3 start, Vec3 end, long seed, int depth, double alongStart, double alongEnd, double intensity) {
        if (budget < 8 && depth > 0) return;
        int generations = Math.max(depth > 0 ? 3 : 1, recipe.strikeGenerations - depth);
        int allowance=budget;
        if(depth>0){
            long siblings=pending.stream().filter(p->p.depth==depth).count()+1;
            siblings=Math.min(siblings,Math.max(1,recipe.branchLimit-branches+1));
            allowance=Math.max(8,budget/(int)siblings);
        }
        while ((1 << generations) > allowance && generations > 1) generations--;
        Vec3[] p = new Vec3[(1 << recipe.strikeGenerations) + 1]; p[0] = start; p[1] = end; int used = 2;
        SplittableRandom random = new SplittableRandom(seed);
        double branchSpan = start.distanceTo(end);
        double amplitude = Math.max(recipe.strikeDisplacement * Math.pow(recipe.branchDecay, depth),
            depth > 0 ? branchSpan * (.10 + .08 * random.nextDouble()) : 0);
        for (int generation = 0; generation < generations; generation++) { used = subdivide(p, used, amplitude, random); monotonicize(p, used, start, end); amplitude *= recipe.strikeRoughness; }
        monotonicize(p, used, start, end);
        double widthScale = depth == 0 ? 1 : Math.pow(recipe.branchDecay, depth);
        long path = nextPath++;
        for (int i = 0; i < used - 1 && budget > 0; i++) {
            double a = i / (double)(used - 1), b = (i + 1) / (double)(used - 1);
            double wa = recipe.strikeWidth * widthScale * (depth == 0 ? .82 + .28 * a : 1 - .82 * a);
            double wb = recipe.strikeWidth * widthScale * (depth == 0 ? .82 + .28 * b : 1 - .82 * b);
            segments.add(new Segment(p[i], p[i + 1], (float)Math.max(.004, wa), (float)Math.max(.002, wb), (float)(intensity * widthScale), (float)(alongStart + (alongEnd-alongStart)*a), (float)(alongStart + (alongEnd-alongStart)*b), depth, path)); budget--;
        }
        if (depth >= recipe.maxBranchDepth || budget <= 0) return;
        var candidates=new ArrayList<Pending>();
        for (int i = 3; i < used - 2; i += 6) {
            double t = i / (double)(used - 1); double weight = (.3 + .95 * Math.sin(Math.PI * t)) * (.6 + .6 * t);
            if (random.nextDouble() > recipe.branchProbability * weight
                    && !(hierarchical && candidates.isEmpty() && i + 6 >= used - 2 && recipe.branchProbability > 0)) continue;
            Vec3 origin = p[i]; Vec3 tangent = p[i + 1].subtract(p[i - 1]).normalize();
            Vec3 radial = randomPerpendicular(tangent, random); double angle = recipe.branchAngle * (.65 + random.nextDouble() * .7);
            Vec3 direction = tangent.scale(Math.cos(angle) * .64).add(radial.scale(Math.sin(angle))).add(0, -.18, 0).normalize();
            if (direction.dot(tangent) < .28) direction = direction.add(tangent.scale(.45)).normalize();
            double length = Math.max(.65, start.distanceTo(end) * (1 - t) * recipe.branchLength * (.65 + random.nextDouble() * .7));
            double jitter = recipe.branchJitter * length;
            Vec3 curve = randomPerpendicular(direction, random).scale(length * (.08 + .16 * random.nextDouble()));
            Vec3 tip = origin.add(direction.scale(length)).add(curve)
                .add(jitter == 0 ? 0 : random.nextDouble(-jitter, jitter), 0,
                    jitter == 0 ? 0 : random.nextDouble(-jitter, jitter));
            candidates.add(new Pending(origin,tip,random.nextLong(),depth+1,
                Math.min(.999,alongStart+(alongEnd-alongStart)*t),
                Math.min(1,alongStart+(alongEnd-alongStart)*(t+(1-t)*.6)),intensity));
        }
        // Spread early choices over the full parent, including when the hard
        // branch cap or tiny segment budget permits only a few candidates.
        candidates.sort(Comparator.comparingDouble(Pending::alongStart));
        int n=candidates.size();if(n==0)return;int bits=32-Integer.numberOfLeadingZeros(Math.max(1,n-1));
        for(int k=0;k<(1<<bits);k++){
            int index=bits==0?0:Integer.reverse(k) >>> (32-bits);
            if(index<n)pending.addLast(candidates.get(index));
        }
    }
    private static int subdivide(Vec3[] p, int count, double amplitude, SplittableRandom r) {
        int target = count * 2 - 1; for (int i = target - 1; i >= 0; i -= 2) p[i] = p[i / 2];
        for (int i = 0; i < count - 1; i++) { Vec3 a = p[i*2], b = p[i*2+2]; Vec3 d = b.subtract(a); double len = d.length(); Vec3 dir = len > 1e-9 ? d.scale(1/len) : new Vec3(0,1,0); Vec3 side = randomPerpendicular(dir, r); Vec3 up = dir.cross(side).normalize(); double lim = len * .32; double lateral = LightningMath.clamp(LightningMath.gaussian(r) * amplitude, -lim, lim); double vertical = LightningMath.clamp(LightningMath.gaussian(r) * amplitude * .65, -lim, lim); p[i*2+1] = a.add(b).scale(.5).add(side.scale(lateral)).add(up.scale(vertical)); }
        return target;
    }
    private static void monotonicize(Vec3[] p, int count, Vec3 start, Vec3 end) {
        Vec3 axis = end.subtract(start); double length = axis.length(); if (length < 1e-9) return;
        Vec3 unit = axis.scale(1 / length);
        for (int i = 1; i < count - 1; i++) {
            double t = i / (double)(count - 1);
            Vec3 center = start.lerp(end, t), delta = p[i].subtract(center);
            Vec3 side = delta.subtract(unit.scale(delta.dot(unit)));
            double limit = length * .25 * Math.sin(Math.PI * t);
            p[i] = center.add(side.scale(Math.min(1, limit / Math.max(1e-9, side.length()))));
        }
    }
    private static Vec3 randomPerpendicular(Vec3 dir, SplittableRandom r) { Vec3 candidate = new Vec3(r.nextDouble(-1,1), r.nextDouble(-.35,.35), r.nextDouble(-1,1)); Vec3 p = candidate.subtract(dir.scale(candidate.dot(dir))); return p.lengthSqr() > 1e-6 ? p.normalize() : (Math.abs(dir.y) < .9 ? dir.cross(new Vec3(0,1,0)).normalize() : new Vec3(1,0,0)); }
}
