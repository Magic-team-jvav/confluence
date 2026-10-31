// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

/** Seeded ground discharge and analytical ballistic sparks. Created once per hit. */
final class ImpactGeometry {
    private record Local(Vec3 a,Vec3 b,int path,double alongA,double alongB) {}
    private record Spark(Vec3 velocity,double delay) {}
    private final Vec3 center;
    private final LightningRecipe r;
    private final List<Local> rays=new ArrayList<>(),rings=new ArrayList<>();
    private final List<Spark> sparks=new ArrayList<>();
    ImpactGeometry(Vec3 center,LightningRecipe recipe) {
        this.center=center;this.r=recipe;
        for(int ray=0;ray<r.impactRays;ray++){
            double angle=LightningMath.unit(r.seed,701+ray)*Math.PI*2;
            double radius=r.impactRadius*(.45+.55*LightningMath.unit(r.seed,801+ray));
            Vec3 last=null;
            for(int j=0;j<=8;j++){
                double t=j/8.0,phi=angle+LightningMath.signed(r.seed,900+ray*31+j)*.18*LightningMath.smooth(t*5);
                Vec3 p=new Vec3(Math.cos(phi)*radius*t,.035+.035*Math.sin(Math.PI*t),Math.sin(phi)*radius*t);
                if(last!=null)rays.add(new Local(last,p,ray,(j-1)/8.0,t));last=p;
            }
        }
        for(int ring=0;ring<3;ring++){
            Vec3 last=null;
            double start=ring*Math.PI*2/3+LightningMath.unit(r.seed,2101+ring)*.4;
            for(int j=0;j<=16;j++){
                double t=j/16.0,angle=start+t*Math.PI*.38;
                double radius=r.impactRadius*(.9+.07*LightningMath.signed(r.seed,2201+ring*31+j));
                Vec3 p=new Vec3(Math.cos(angle)*radius,.04,Math.sin(angle)*radius);
                if(last!=null)rings.add(new Local(last,p,r.impactRays+ring,(j-1)/16.0,t));last=p;
            }
        }
        for(int i=0;i<r.impactSparks;i++){
            double angle=LightningMath.unit(r.seed,3101+i)*Math.PI*2,speed=r.impactRadius*(.045+.035*LightningMath.unit(r.seed,3201+i));
            sparks.add(new Spark(new Vec3(Math.cos(angle)*speed,.09+.13*LightningMath.unit(r.seed,3301+i),Math.sin(angle)*speed),LightningMath.unit(r.seed,3401+i)*.8));
        }
    }
    Vec3 center(){return center;}
    List<OrbitField.Segment> sample(double ticks) {
        List<OrbitField.Segment> out=new ArrayList<>();
        double age=ticks-r.progressionTicks;
        if(!r.impactEnabled||age<0||age>=r.impactDuration)return out;
        double phase=age/r.impactDuration,power=Math.exp(-3.4*phase)*(1-LightningMath.smooth((phase-.65)/.35));
        double progress=LightningMath.clamp(age/Math.min(2,r.impactDuration*.25),0,1);
        for(Local s:rays){
            if(s.alongA()>=progress)continue;
            double f=LightningMath.clamp((progress-s.alongA())/(s.alongB()-s.alongA()),0,1);
            // Material coordinates refer to the parent bolt's landing tip,
            // while the independent Local coordinates still control ray growth.
            out.add(new OrbitField.Segment(center.add(s.a()),center.add(s.a().lerp(s.b(),f)),(float)Math.max(.003,r.strikeWidth*.27*(1-.7*s.alongA())),(float)(power*(1-.3*s.alongA())),s.path(),1,1));
        }
        double wave=.35+.75*LightningMath.smooth(age/3);
        for(Local s:rings)out.add(new OrbitField.Segment(center.add(s.a().multiply(wave,1,wave)),center.add(s.b().multiply(wave,1,wave)),(float)Math.max(.003,r.currentWidth*.45),(float)(power*.4),s.path(),1,1));
        for(int i=0;i<sparks.size();i++){
            Spark spark=sparks.get(i);double t=age-spark.delay();if(t<=0)continue;
            Vec3 p=sparkPoint(spark,t);if(p.y<.035)continue;
            out.add(new OrbitField.Segment(center.add(sparkPoint(spark,Math.max(0,t-.65))),center.add(p),(float)Math.max(.003,r.strikeWidth*.12),(float)(power*.85),r.impactRays+3+i,1,1));
        }
        double flash=Math.exp(-age*.9)*power;
        if(flash>.0001)for(int axis=0;axis<2;axis++){
            Vec3 d=axis==0?new Vec3(.08,0,0):new Vec3(0,0,.08),p=center.add(0,.06,0);
            out.add(new OrbitField.Segment(p.subtract(d),p.add(d),(float)(r.strikeWidth*2.5),(float)flash,r.impactRays+3+r.impactSparks+axis,1,1));
        }
        return out;
    }
    private static Vec3 sparkPoint(Spark spark,double t){return spark.velocity().scale(t).add(0,.035-.0125*t*t,0);}
}
