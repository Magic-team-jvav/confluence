// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/** Raster coverage only: physical widths and path vertices remain in their original recipe. */
final class RibbonCoverage {
    static final double MIN_HALF_PIXELS = .75;
    private RibbonCoverage() {}
    record Join(Vec3 side, double alphaScale, double nativeHalfPixels, double renderedHalfPixels) {}

    static Join cover(Vec3 center, Vec3 side, Matrix4f clip, int viewportWidth, int viewportHeight) {
        return cover(center,side,null,null,clip,viewportWidth,viewportHeight);
    }

    static Join cover(Vec3 center, Vec3 side, Vec3 previous, Vec3 next, Matrix4f clip, int viewportWidth, int viewportHeight) {
        double original = halfPixels(center, side, previous, next, clip, viewportWidth, viewportHeight);
        if (!Double.isFinite(original) || original <= 1e-6 || original >= MIN_HALF_PIXELS)
            return new Join(side, 1, original, original);
        Vec3 expanded = side.scale(MIN_HALF_PIXELS / original);
        double rendered = halfPixels(center, expanded, previous, next, clip, viewportWidth, viewportHeight);
        if (!Double.isFinite(rendered) || rendered <= 0)
            return new Join(side, 1, original, original);
        return new Join(expanded, original / rendered, original, rendered);
    }

    static double halfPixels(Vec3 center, Vec3 side, Matrix4f clip, int viewportWidth, int viewportHeight) {
        return halfPixels(center,side,null,null,clip,viewportWidth,viewportHeight);
    }

    static double halfPixels(Vec3 center, Vec3 side, Vec3 previous, Vec3 next, Matrix4f clip, int viewportWidth, int viewportHeight) {
        if (viewportWidth <= 0 || viewportHeight <= 0) return Double.NaN;
        Vector4f a = clip.transform(new Vector4f((float)(center.x + side.x), (float)(center.y + side.y), (float)(center.z + side.z), 1));
        Vector4f b = clip.transform(new Vector4f((float)(center.x - side.x), (float)(center.y - side.y), (float)(center.z - side.z), 1));
        Vector4f c=clip.transform(new Vector4f((float)center.x,(float)center.y,(float)center.z,1));
        // Do not expand originally clipped geometry across either depth plane.
        if (!insideDepth(c) || !insideDepth(a) || !insideDepth(b)) return Double.NaN;
        double x = (a.x / (double)a.w - b.x / (double)b.w) * viewportWidth * .25;
        double y = (a.y / (double)a.w - b.y / (double)b.w) * viewportHeight * .25;
        double result=Double.POSITIVE_INFINITY;
        Vec3 screenCenter=project(center,clip,viewportWidth,viewportHeight);
        if(screenCenter!=null)for(Vec3 neighbor:new Vec3[]{previous,next}) {
            Vec3 screen=neighbor==null?null:project(neighbor,clip,viewportWidth,viewportHeight);
            if(screen==null)continue;
            double tx=screen.x-screenCenter.x,ty=screen.y-screenCenter.y,length=Math.hypot(tx,ty);
            if(length>1e-6)result=Math.min(result,Math.abs(x*ty-y*tx)/length);
        }
        return result==Double.POSITIVE_INFINITY?Math.hypot(x,y):result;
    }

    private static Vec3 project(Vec3 point,Matrix4f clip,int width,int height) {
        Vector4f p=clip.transform(new Vector4f((float)point.x,(float)point.y,(float)point.z,1));
        if(!insideDepth(p))return null;
        double x=p.x/(double)p.w*width*.5,y=p.y/(double)p.w*height*.5;
        return Double.isFinite(x)&&Double.isFinite(y)?new Vec3(x,y,0):null;
    }

    private static boolean insideDepth(Vector4f p) {
        if(!Float.isFinite(p.x)||!Float.isFinite(p.y)||!Float.isFinite(p.z)||!Float.isFinite(p.w)||p.w<=1e-4)return false;
        double tolerance=Math.max(1e-6,2.0*Math.ulp(p.w));
        return p.z+(double)p.w>tolerance && p.w-(double)p.z>tolerance;
    }

    /** UV.x is float; Color.a remains the original byte-packed envelope. */
    static void emit(VertexConsumer out,Matrix4f matrix,Vec3 point,float alpha,float coverage,float v,float[] rgb) {
        out.vertex(matrix,(float)point.x,(float)point.y,(float)point.z)
            .color(rgb[0],rgb[1],rgb[2],alpha).uv(coverage,v).endVertex();
    }
}
