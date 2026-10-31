// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;

/**
 * Convex, independently projected halo strips and round interior joins.
 * Positions are camera-relative in the same space consumed by actualClip.
 * Four consecutive vertices form one quad; these primitives require per-path
 * maximum coverage composition before adding their emission to the scene.
 */
final class HaloGeometry {
    static final double MIN_HALF_PIXELS = .75;
    private static final double DEPTH_INSET = 1e-8;
    private static final double MIN_W = 1e-10;
    private static final double MIN_SEGMENT_PIXELS = 1e-7;

    record Point(Vec3 p, double radius, double along, double alpha) {}
    record Vertex(Vec3 p, float alpha, float u, float v, float[] rgb) {}
    /** OpenGL window coordinates, with the origin at the viewport's lower left. */
    record Bounds(int x, int y, int width, int height) {
        boolean empty() { return width <= 0 || height <= 0; }
    }
    record Mesh(List<Vertex> body, List<Vertex> round, Bounds bounds) {
        boolean empty() { return bounds.empty() || (body.isEmpty() && round.isEmpty()); }
    }
    private record Screen(double x, double y, double depth) {}
    private record Segment(Point a, Point b) {}
    record Projection(Matrix4d clip, Matrix4d inverse, int width, int height) {}
    static Projection prepare(Matrix4f actualClip, int width, int height) {
        if(actualClip==null||width<=0||height<=0)return null;
        Matrix4d clip=new Matrix4d(actualClip);
        double determinant=clip.determinant();
        if(!Double.isFinite(determinant)||determinant==0)return null;
        return new Projection(clip,new Matrix4d(clip).invert(),width,height);
    }

    private HaloGeometry() {}

    static Mesh build(List<Point> points, Matrix4f actualClip, int width, int height, LightningRecipe recipe) {
        return build(points, actualClip, width, height, recipe, true);
    }

    static Mesh build(List<Point> points, Matrix4f actualClip, int width, int height, LightningRecipe recipe, boolean halo) {
        return build(points,prepare(actualClip,width,height),recipe,halo);
    }
    static Mesh build(List<Point> points, Projection projection, LightningRecipe recipe, boolean halo) {
        if(points==null||points.size()<2||projection==null)return emptyMesh();
        Matrix4d clip=projection.clip(),inverse=projection.inverse();
        int width=projection.width(),height=projection.height();
        var palette=LightningAppearance.palette(recipe,halo);
        var body = new ArrayList<Vertex>((points.size() - 1) * 4);
        var round = new ArrayList<Vertex>(Math.max(0, points.size() - 2) * 4);
        boolean[] visibleSegments = new boolean[points.size() - 1];
        double[] tangentX = new double[points.size() - 1], tangentY = new double[points.size() - 1];
        Extent extent = new Extent();

        for (int i = 0; i + 1 < points.size(); i++) {
            Point originalA = points.get(i), originalB = points.get(i + 1);
            Segment segment = clipSegment(originalA, originalB, clip);
            if (segment == null) continue;
            Point a = segment.a(), b = segment.b();
            Screen pa = project(a.p(), clip, width, height), pb = project(b.p(), clip, width, height);
            if (pa == null || pb == null) continue;
            double tx = pb.x() - pa.x(), ty = pb.y() - pa.y(), length = Math.hypot(tx, ty);
            if (!Double.isFinite(length) || length < MIN_SEGMENT_PIXELS) continue;
            double nx = -ty / length, ny = tx / length;
            double unitA = worldPerPixel(a.p(), pa, nx, ny, inverse, width, height);
            double unitB = worldPerPixel(b.p(), pb, nx, ny, inverse, width, height);
            if (!(unitA > 0) || !(unitB > 0)) continue;
            double nativeA = a.radius() / unitA, nativeB = b.radius() / unitB;
            if (!positiveFinite(nativeA) || !positiveFinite(nativeB)) continue;
            double renderedA = Math.max(MIN_HALF_PIXELS, nativeA), renderedB = Math.max(MIN_HALF_PIXELS, nativeB);
            float coverageA = (float)(nativeA / renderedA), coverageB = (float)(nativeB / renderedB);
            if (!(coverageA > 0) || !(coverageB > 0)) continue;
            Screen[] corners = {
                offset(pa, nx * renderedA, ny * renderedA),
                offset(pa, -nx * renderedA, -ny * renderedA),
                offset(pb, -nx * renderedB, -ny * renderedB),
                offset(pb, nx * renderedB, ny * renderedB)
            };
            Vec3[] positions = unproject(corners, inverse, width, height);
            if (positions == null || !convexAfterFloatTransport(positions, clip, width, height)) continue;
            float[] ca = palette.colorAt(a.along());
            float[] cb = palette.colorAt(b.along());
            body.add(vertex(positions[0], a.alpha(), coverageA, 0, ca));
            body.add(vertex(positions[1], a.alpha(), coverageA, 1, ca));
            body.add(vertex(positions[2], b.alpha(), coverageB, 1, cb));
            body.add(vertex(positions[3], b.alpha(), coverageB, 0, cb));
            extent.include(corners);
            visibleSegments[i] = true;
            tangentX[i] = tx / length; tangentY[i] = ty / length;
        }

        // Terminal nodes remain butt caps. A clipped-away or invalid node never
        // receives a disk, and invalid gaps are never bridged by a new segment.
        for (int i = 1; i + 1 < points.size(); i++) {
            if (!visibleSegments[i - 1] || !visibleSegments[i]) continue;
            double turn = tangentX[i - 1] * tangentY[i] - tangentY[i - 1] * tangentX[i];
            double forward = tangentX[i - 1] * tangentX[i] + tangentY[i - 1] * tangentY[i];
            // A same-direction collinear node has no join. An unnecessary full
            // disk there would extend a short, wide line beyond its butt caps.
            if (forward > 0 && Math.abs(turn) < 1e-7) continue;
            Point point = points.get(i);
            if (!valid(point)) continue;
            Screen center = project(point.p(), clip, width, height);
            if (center == null) continue;
            double unitX = worldPerPixel(point.p(), center, 1, 0, inverse, width, height);
            double unitY = worldPerPixel(point.p(), center, 0, 1, inverse, width, height);
            if (!(unitX > 0) || !(unitY > 0)) continue;
            double nativeX = point.radius() / unitX, nativeY = point.radius() / unitY;
            if (!positiveFinite(nativeX) || !positiveFinite(nativeY)) continue;
            double renderedX = Math.max(MIN_HALF_PIXELS, nativeX), renderedY = Math.max(MIN_HALF_PIXELS, nativeY);
            // A disk expands in two axes, so its integrated-energy correction
            // is an area ratio, rather than the strip's one-dimensional ratio.
            float coverage = (float)((nativeX / renderedX) * (nativeY / renderedY));
            if (!(coverage > 0)) continue;
            Screen[] corners = {
                offset(center, -renderedX, -renderedY), offset(center, renderedX, -renderedY),
                offset(center, renderedX, renderedY), offset(center, -renderedX, renderedY)
            };
            Vec3[] positions = unproject(corners, inverse, width, height);
            if (positions == null || !convexAfterFloatTransport(positions, clip, width, height)) continue;
            float[] rgb = palette.colorAt(point.along());
            round.add(vertex(positions[0], point.alpha(), -coverage, -1, rgb));
            round.add(vertex(positions[1], point.alpha(), coverage, -1, rgb));
            round.add(vertex(positions[2], point.alpha(), coverage, 1, rgb));
            round.add(vertex(positions[3], point.alpha(), -coverage, 1, rgb));
            extent.include(corners);
        }
        return new Mesh(List.copyOf(body), List.copyOf(round), extent.bounds(width, height));
    }

    private static Mesh emptyMesh() { return new Mesh(List.of(), List.of(), new Bounds(0, 0, 0, 0)); }

    private static Vertex vertex(Vec3 p, double alpha, float u, float v, float[] rgb) {
        return new Vertex(p, (float)Math.max(0, Math.min(1, alpha)), u, v, rgb);
    }

    private static boolean positiveFinite(double value) { return Double.isFinite(value) && value > 0; }

    private static boolean finite(Vec3 p) {
        return p != null && Double.isFinite(p.x) && Double.isFinite(p.y) && Double.isFinite(p.z)
            && Math.abs(p.x) <= Float.MAX_VALUE && Math.abs(p.y) <= Float.MAX_VALUE && Math.abs(p.z) <= Float.MAX_VALUE;
    }

    private static boolean valid(Point p) {
        return p != null && finite(p.p()) && positiveFinite(p.radius())
            && Double.isFinite(p.along()) && Double.isFinite(p.alpha());
    }

    private static Vector4d clip(Vec3 p, Matrix4d clip) {
        return clip.transform(new Vector4d(p.x, p.y, p.z, 1));
    }

    private static boolean finite(Vector4d p) {
        return Double.isFinite(p.x) && Double.isFinite(p.y) && Double.isFinite(p.z) && Double.isFinite(p.w);
    }

    private static Segment clipSegment(Point a, Point b, Matrix4d clip) {
        if (!valid(a) || !valid(b)) return null;
        Vector4d ca = clip(a.p(), clip), cb = clip(b.p(), clip);
        if (!finite(ca) || !finite(cb)) return null;
        double first = 0, last = 1;
        double[] av = {ca.z + (1 - DEPTH_INSET) * ca.w, (1 - DEPTH_INSET) * ca.w - ca.z, ca.w - MIN_W};
        double[] bv = {cb.z + (1 - DEPTH_INSET) * cb.w, (1 - DEPTH_INSET) * cb.w - cb.z, cb.w - MIN_W};
        for (int i = 0; i < av.length; i++) {
            if (av[i] < 0 && bv[i] < 0) return null;
            if (av[i] < 0) first = Math.max(first, av[i] / (av[i] - bv[i]));
            if (bv[i] < 0) last = Math.min(last, av[i] / (av[i] - bv[i]));
            if (first >= last) return null;
        }
        return new Segment(interpolate(a, b, first), interpolate(a, b, last));
    }

    private static Point interpolate(Point a, Point b, double t) {
        if (t == 0) return a;
        if (t == 1) return b;
        return new Point(a.p().lerp(b.p(), t), a.radius() + (b.radius() - a.radius()) * t,
            a.along() + (b.along() - a.along()) * t, a.alpha() + (b.alpha() - a.alpha()) * t);
    }

    private static Screen project(Vec3 point, Matrix4d clip, int width, int height) {
        Vector4d p = clip(point, clip);
        if (!finite(p) || p.w <= MIN_W) return null;
        double depth = p.z / p.w;
        if (depth < -1 + DEPTH_INSET * .5 || depth > 1 - DEPTH_INSET * .5) return null;
        double x = (p.x / p.w + 1) * width * .5, y = (p.y / p.w + 1) * height * .5;
        return Double.isFinite(x) && Double.isFinite(y) ? new Screen(x, y, depth) : null;
    }

    private static Screen offset(Screen p, double x, double y) { return new Screen(p.x() + x, p.y() + y, p.depth()); }

    private static Vec3 unproject(Screen p, Matrix4d inverse, int width, int height) {
        Vector4d world = inverse.transform(new Vector4d(p.x() * 2 / width - 1, p.y() * 2 / height - 1, p.depth(), 1));
        if (!finite(world) || Math.abs(world.w) <= MIN_W) return null;
        Vec3 result = new Vec3(world.x / world.w, world.y / world.w, world.z / world.w);
        return finite(result) ? result : null;
    }

    private static Vec3[] unproject(Screen[] points, Matrix4d inverse, int width, int height) {
        Vec3[] result = new Vec3[points.length];
        for (int i = 0; i < points.length; i++) {
            result[i] = unproject(points[i], inverse, width, height);
            if (result[i] == null) return null;
        }
        return result;
    }

    private static double worldPerPixel(Vec3 p, Screen screen, double nx, double ny, Matrix4d inverse, int width, int height) {
        Vec3 shifted = unproject(offset(screen, nx, ny), inverse, width, height);
        if (shifted == null) return Double.NaN;
        double result = shifted.distanceTo(p);
        return positiveFinite(result) ? result : Double.NaN;
    }

    private static boolean convexAfterFloatTransport(Vec3[] positions, Matrix4d clip, int width, int height) {
        double[] x = new double[4], y = new double[4];
        for (int i = 0; i < 4; i++) {
            Vec3 original = positions[i];
            Vector4d p = clip(new Vec3((float)original.x, (float)original.y, (float)original.z), clip);
            if (!finite(p) || p.w <= MIN_W) return false;
            x[i] = p.x / p.w * width * .5; y[i] = p.y / p.w * height * .5;
        }
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4, k = (i + 2) % 4;
            double area = (x[j] - x[i]) * (y[k] - y[i]) - (y[j] - y[i]) * (x[k] - x[i]);
            if (!Double.isFinite(area) || area <= 0) return false;
        }
        return true;
    }

    private static final class Extent {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        void include(Screen[] points) {
            for (Screen point : points) {
                minX = Math.min(minX, point.x()); minY = Math.min(minY, point.y());
                maxX = Math.max(maxX, point.x()); maxY = Math.max(maxY, point.y());
            }
        }
        Bounds bounds(int width, int height) {
            if (!Double.isFinite(minX)) return new Bounds(0, 0, 0, 0);
            // A pixel of padding also covers float vertex transport at the GPU.
            int x = (int)Math.floor(Math.max(0, Math.min(width, minX - 1)));
            int y = (int)Math.floor(Math.max(0, Math.min(height, minY - 1)));
            int right = (int)Math.ceil(Math.max(0, Math.min(width, maxX + 1)));
            int top = (int)Math.ceil(Math.max(0, Math.min(height, maxY + 1)));
            return new Bounds(x, y, right - x, top - y);
        }
    }
}
