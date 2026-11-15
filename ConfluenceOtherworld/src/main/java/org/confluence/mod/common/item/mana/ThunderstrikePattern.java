package org.confluence.mod.common.item.mana;

import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.ArrayList;
import java.util.SplittableRandom;

/** Fixed-endpoint midpoint displacement, shared by server damage and client rendering. */
public final class ThunderstrikePattern {
    public static final int TRAVEL_TICKS = 10;
    public static final int MIN_PRIMARY_BRANCHES = 2, MAX_PRIMARY_BRANCHES = 5;
    public static final int MIN_SECONDARY_BRANCHES = 1, MAX_SECONDARY_BRANCHES = 3;
    private ThunderstrikePattern() {}

    public static Vec3 step(Vec3 position, Vec3 target, int age) {
        return position.add(target.subtract(position).scale(1.0 / Math.max(1, TRAVEL_TICKS - age + 1)));
    }

    public static List<Vec3> create(Vec3 start, Vec3 end, long seed) {
        double displacement = Math.min(12, Math.max(6, start.distanceTo(end) * 0.09));
        var coarse = RedGlovePattern.jagged(start, end, 64, seed, displacement);
        var random = new SplittableRandom(seed ^ 0xBB67AE8584CAA73BL);
        List<Vec3> points = new ArrayList<>();
        points.add(start);
        double detail = Math.min(0.65, Math.max(0.25, start.distanceTo(end) / 256));
        // Spend half the samples on the lower third, where a nearby caster sees the bolt.
        // Coarse bends and independent small-scale displacement stay fixed for the event.
        for (int i = 1; i < 64; i++) {
            double t = i <= 32 ? i / 48.0 : 2.0 / 3 + (i - 32) / 96.0;
            double sample = t * 64;
            int index = Math.min(63, (int) sample);
            Vec3 point = coarse.get(index).lerp(coarse.get(index + 1), sample - index);
            double fade = Math.min(1, point.distanceTo(end) / 1.5);
            points.add(point.add(random.nextDouble(-1, 1) * detail * fade, 0,
                    random.nextDouble(-1, 1) * detail * fade));
        }
        points.add(end);
        return List.copyOf(points);
    }

    /** Each branch starts after the first third of its own parent channel. */
    public static List<RedGlovePattern.Path> paths(Vec3 start, Vec3 end, long seed) {
        return paths(start, end, seed, new Vec3(1, 0, 0));
    }

    public static List<RedGlovePattern.Path> paths(Vec3 start, Vec3 end, long seed, Vec3 spreadAxis) {
        var random = new SplittableRandom(seed ^ 0x6A09E667F3BCC909L);
        Vec3 right = new Vec3(spreadAxis.x, 0, spreadAxis.z).normalize();
        if (right.lengthSqr() < 0.5) right = new Vec3(1, 0, 0);
        Vec3 forward = new Vec3(-right.z, 0, right.x);
        List<RedGlovePattern.Path> paths = new ArrayList<>();
        var trunk = create(start, end, seed);
        paths.add(new RedGlovePattern.Path(0, -1, -1, trunk));
        int primaryCount = random.nextInt(MIN_PRIMARY_BRANCHES, MAX_PRIMARY_BRANCHES + 1);
        double rotation = random.nextDouble(Math.PI * 2);
        for (int i = 0; i < primaryCount; i++) {
            // Keep two distinct lower forks readable to the caster. Additional forks span
            // the broader region after the first third rather than anchoring one compulsory fork far overhead.
            int broadCount = Math.max(1, primaryCount - 2);
            int firstRoot = i < 2 ? 60 + i * 2 : 22 + (i - 2) * 35 / broadCount;
            int lastRoot = i < 2 ? firstRoot + 1 : 22 + (i - 1) * 35 / broadCount - 1;
            int rootIndex = selectBend(trunk, firstRoot, lastRoot, random);
            Vec3 root = trunk.get(rootIndex);
            double angle = (i < 2 ? i * Math.PI : rotation + (i - 2) * 2.399963229728653)
                    + random.nextDouble(-0.3, 0.3);
            double reach = random.nextDouble(2.5, 5.5);
            Vec3 tip = root.add(right.scale(Math.cos(angle) * reach)).add(forward.scale(Math.sin(angle) * reach))
                    .add(0, -random.nextDouble(0.3, 0.65) * reach, 0);
            if (tip.y < end.y + 0.25) tip = new Vec3(tip.x, end.y + 0.25, tip.z);
            var branch = RedGlovePattern.jagged(root, tip, 8, random.nextLong(), 0.65);
            int parent = paths.size();
            paths.add(new RedGlovePattern.Path(1, 0, rootIndex, branch));
            int twigCount = random.nextInt(MIN_SECONDARY_BRANCHES, MAX_SECONDARY_BRANCHES + 1);
            for (int twig = 0; twig < twigCount; twig++) {
                int firstJunction = 3 + twig * 5 / twigCount;
                int lastJunction = 3 + (twig + 1) * 5 / twigCount - 1;
                int junction = selectBend(branch, firstJunction, lastJunction, random);
                Vec3 twigRoot = branch.get(junction);
                double twigAngle = angle + (twig % 2 == 0 ? -1 : 1) * random.nextDouble(0.5, 1.1);
                double length = random.nextDouble(1.1, 2.6);
                Vec3 twigTip = twigRoot.add(right.scale(Math.cos(twigAngle) * length))
                        .add(forward.scale(Math.sin(twigAngle) * length)).add(0, -length * 0.4, 0);
                if (twigTip.y < end.y + 0.15) twigTip = new Vec3(twigTip.x, end.y + 0.15, twigTip.z);
                paths.add(new RedGlovePattern.Path(2, parent, junction,
                        RedGlovePattern.jagged(twigRoot, twigTip, 4, random.nextLong(), 0.3)));
            }
        }
        return List.copyOf(paths);
    }

    /** Squared turning strength strongly favors real corners, without moving the parent channel. */
    public static int selectBend(List<Vec3> points, int first, int last, SplittableRandom random) {
        return RedGlovePattern.selectBend(points, first, last, random);
    }
}
