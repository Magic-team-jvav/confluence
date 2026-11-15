// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

/** One shared, capped miter per centerline vertex; no overlapping segment caps. */
final class RibbonGeometry {
    private RibbonGeometry() {}
    static List<Vec3> joins(List<Vec3> points,Vec3 camera) {
        List<Vec3> sides=new ArrayList<>(),joins=new ArrayList<>();
        for(int i=0;i<points.size()-1;i++) {
            Vec3 a=points.get(i),b=points.get(i+1),dir=b.subtract(a).normalize();
            Vec3 side=dir.cross(a.add(b).scale(.5).subtract(camera));
            if(side.lengthSqr()<1e-12) side=dir.cross(Math.abs(dir.y)<.9?new Vec3(0,1,0):new Vec3(1,0,0));
            side=side.normalize();
            if(!sides.isEmpty()&&side.dot(sides.get(sides.size()-1))<0) side=side.scale(-1);
            sides.add(side);
        }
        if(sides.isEmpty())return joins;
        joins.add(sides.get(0));
        for(int i=1;i<points.size()-1;i++) {
            Vec3 next=sides.get(i),miter=sides.get(i-1).add(next).normalize();
            joins.add(miter.scale(1/Math.max(.5,miter.dot(next))));
        }
        joins.add(sides.get(sides.size()-1));
        return joins;
    }
}
