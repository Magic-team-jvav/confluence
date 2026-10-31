// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

/** Short axial channels, plus the polar arc/ring curve used by the circular attachment mode. */
final class OrbitField {
    record Segment(Vec3 a, Vec3 b, float width, float alpha, int strand, float alongA, float alongB) {}
    private final LightningRecipe recipe;
    OrbitField(LightningRecipe recipe) { this.recipe = recipe; }
    record Timing(double interval, double onset, double fade) {}
    Timing timing(long seed) {
        double base=recipe.pulseInterval*(.7+.6*LightningMath.unit(seed,17));
        double onset=base*.02,fade=Math.max(.5,Math.min(4,base*.25));
        double distance=recipe.orbitAnimation.equals("ring")?Math.min(Math.PI*1.85*recipe.orbitRadius,recipe.arcLength):Math.min(recipe.rodLength*.95,recipe.arcLength);
        return new Timing(Math.max(base,onset+20*distance/recipe.orbitExtendSpeed+fade),onset,fade);
    }
    List<Segment> sample(Vec3 center, double tickTime) {
        List<Segment> out = new ArrayList<>(recipe.strands * recipe.samples);
        double seconds = tickTime / 20.0;
        if (recipe.orbitAnimation.equals("spektr")) return sampleSource(center, tickTime);
        for (int lane = 0; lane < recipe.strands; lane++) {
            long seed = recipe.seed + lane * 7919L;
            Timing timing=timing(seed);
            double interval = timing.interval();
            double raw = (tickTime + LightningMath.unit(seed, 18) * interval) / interval;
            long cycle = (long)Math.floor(raw), pulseSeed = seed + cycle * 104729L;
            double age = (raw - cycle) * interval;
            if (LightningMath.unit(pulseSeed, 19) > recipe.throttle) continue;
            boolean ring = recipe.orbitAnimation.equals("ring");
            double env = LightningMath.smooth(age / Math.max(.1,timing.onset()*1.5)) * (1 - LightningMath.smooth((age - interval + timing.fade()) / timing.fade()));
            if (env < .015) continue;
            double target = Math.min(recipe.rodLength * .95, recipe.arcLength) * (1 - recipe.lengthRandomness * LightningMath.unit(pulseSeed, 25));
            double angle = LightningMath.unit(seed, 21) * Math.PI * 2;
            double radius = recipe.rodRadius + recipe.emitterSpread * LightningMath.unit(seed, 22);
            double ringSpan = Math.min(Math.PI * 1.85, recipe.arcLength / Math.max(.1, recipe.orbitRadius)
                * (1 - recipe.lengthRandomness * LightningMath.unit(pulseSeed, 27)));
            double ringStart = LightningMath.unit(seed, 23) * Math.PI * 2;
            double travel=ring?ringSpan*recipe.orbitRadius:target;
            double growth=LightningMath.clamp((age-timing.onset())*recipe.orbitExtendSpeed/20/Math.max(1e-9,travel),0,1);
            // The rod is only the attachment channel. Each arc owns a fixed local root on it.
            double reservation = Math.min(recipe.rodLength * .95, recipe.arcLength);
            double root = -recipe.rodLength * .5 + (recipe.rodLength - reservation) * LightningMath.unit(seed, 23)
                + (recipe.orbitDirection < 0 ? reservation : 0);
            float alpha = (float)(env * (.65 + .35 * LightningMath.unit(pulseSeed, 26)));
            for (int i = 0; i < recipe.samples; i++) {
                double t0 = i / (double)recipe.samples, t1 = Math.min(growth, (i + 1) / (double)recipe.samples);
                if (t0 >= growth) break;
                Vec3 a = ring ? ringPoint(center, seed, lane, ringStart, ringSpan, seconds, t0)
                    : point(center, seed, lane, root, target, angle, radius, false, seconds, t0);
                Vec3 b = ring ? ringPoint(center, seed, lane, ringStart, ringSpan, seconds, t1)
                    : point(center, seed, lane, root, target, angle, radius, false, seconds, t1);
                out.add(new Segment(a, b,
                    (float)recipe.currentWidth, alpha, lane, (float)t0, (float)t1));
            }
        }
        return out;
    }
    // Spektr's pulse window and endpoint jitter, evaluated on the CPU. The seeded
    // hash is deliberately retained for stable Java/JS replay; not Unity float parity.
    private List<Segment> sampleSource(Vec3 center, double ticks) {
        List<Segment> out = new ArrayList<>();
        double seconds = ticks / 20.0;
        for (int lane=0; lane<recipe.strands; lane++) {
            long seed=recipe.seed+lane*7919L;
            double interval=.2+Math.max(.1,recipe.pulseInterval-.4)*LightningMath.unit(seed,17);
            double raw=ticks/interval, phase=raw-Math.floor(raw);
            long pulseSeed=seed+((long)Math.floor(raw)%9973)*104729L;
            if (LightningMath.unit(pulseSeed,19)>=recipe.throttle) continue;
            float alpha=(float)Math.max(0,LightningMath.unit(pulseSeed,26)-.01);
            double fraction=Math.min(1,recipe.arcLength/recipe.rodLength)*(1-recipe.lengthRandomness*LightningMath.unit(pulseSeed,25));
            double p0=-recipe.rodLength*.5+recipe.orbitWobble*LightningMath.signed(pulseSeed,2);
            double p1=recipe.rodLength*.5+recipe.orbitWobble*LightningMath.signed(pulseSeed,3);
            for (int i=0;i<recipe.samples;i++) {
                double t0=i/(double)recipe.samples,t1=(i+1)/(double)recipe.samples;
                out.add(new Segment(sourcePoint(center,pulseSeed,p0,p1,phase,fraction,seconds,t0),
                    sourcePoint(center,pulseSeed,p0,p1,phase,fraction,seconds,t1),
                    (float)recipe.currentWidth,alpha,lane,(float)t0,(float)t1));
            }
        }
        return out;
    }
    private Vec3 sourcePoint(Vec3 center,long seed,double p0,double p1,double phase,double fraction,double seconds,double t) {
        double u=phase*(1-fraction)+t*fraction;
        double x=p0+(p1-p0)*u;
        return center.add(x,recipe.orbitHeight+recipe.orbitWobble*displace(seed+31,u*recipe.rodLength,seconds),
            recipe.orbitWobble*displace(seed+97,u*recipe.rodLength,seconds));
    }
    private double displace(long seed,double distance,double seconds) {
        double offset=LightningMath.unit(seed,41)*289;
        double f=recipe.orbitNoiseFrequency, m=seconds*recipe.orbitFlow;
        return SimplexNoise.sample(distance*f+offset,m)+.1*SimplexNoise.sample(distance*f*10+offset,m*10);
    }
    private Vec3 ringPoint(Vec3 center,long seed,int lane,double start,double span,double seconds,double t) {
        double direction=recipe.orbitDirection<0?-1:1;
        double angle=start+direction*span*t;
        double distance=span*t*recipe.orbitRadius,gate=LightningMath.smooth(t/.18);
        double radial=recipe.orbitRadius+recipe.orbitWobble*gate*displace(seed+401,distance,seconds);
        double height=recipe.orbitWobble*.45*gate*displace(seed+503,distance,seconds);
        return center.add(Math.cos(angle)*radial,recipe.orbitHeight+height,Math.sin(angle)*radial);
    }
    private Vec3 point(Vec3 center,long seed,int lane,double axialStart,double target,double angle,double radius,boolean source,double seconds,double t) {
        double x=axialStart+recipe.orbitDirection*target*t;
        double gate=LightningMath.smooth(t/.18);
        return center.add(x,recipe.orbitHeight+Math.cos(angle)*radius+recipe.orbitWobble*gate*displace(seed+31,target*t,seconds),
            Math.sin(angle)*radius+recipe.orbitWobble*gate*displace(seed+97,target*t,seconds));
    }
}
