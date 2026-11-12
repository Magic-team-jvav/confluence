package org.confluence.mod.common.entity;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/// 被砍倒的树（Terraria 式伐木）：倒下的原木和树叶已经从世界里移走，这个实体把它们画成一棵整体倒下的树。
///
/// 整个过程在服务端生成时逐 tick 算好（每 tick 一个姿态：绕水平轴的角度 + 沿倒向的平移 + 竖直平移），随生成数据发给客户端：
/// 1. 绕树桩上的转轴倒下：刚性杆 θ'' = 3g/(2L)·sinθ，开头慢慢倾斜、越倒越快，树越高越慢；只剩砍掉的那一格撑着的树先坐到砍口上；
/// 2. 砸到地面后看重心：重心越过了最前面一个“下面有地”的原木（倒在悬崖边、坑边），就绕那条边翻过去；
/// 3. 翻过去（或者一开始就倒向空处）就脱离，带着当时的速度自由下落、慢慢翻转，直到原木碰到地形；掉出世界底部就什么都不留。
/// 停下后弹两下，停稳了服务端按原版战利品表掉落物品。
public class FallingTreeEntity extends Entity implements IEntityWithComplexSpawn {
    /// 真实重力：开头慢慢倾斜，越倒越快，有“分量”
    private static final float GRAVITY = 9.8F;
    /// 脱离后自由下落的加速度（方块/秒²），和原版下落的东西差不多
    private static final float FALL_GRAVITY = 24.0F;
    private static final float START_ANGLE = 0.04F, START_SPEED = 0.1F;
    private static final float MAX_HINGE = (float) Math.toRadians(100.0);
    /// 翻过边缘时，转到这个角度（或重心掉到边缘以下）就脱离
    private static final float DETACH_ANGLE = 2.4F, MAX_TUMBLE = 3.0F;
    private static final int MAX_HINGE_TICKS = 160, MAX_TIP_TICKS = 100, MAX_FALL_TICKS = 240;
    /// 砸到地上后弹两下再停（tick 数），停稳了才碎
    private static final int BOUNCE_TICKS = 10;
    /// 砍掉的那一格下面空了：树先往下落，约 5 tick 一格
    private static final float DROP_ACCELERATION = 25.0F;

    private BlockPos base = BlockPos.ZERO;
    /// 转轴（相对 base 方块原点）：最低一层朝倒向一侧的边上
    private float pivotX = 0.5F, pivotY = 0.0F, pivotZ = 0.5F;
    private float dirX = 1.0F, dirZ = 0.0F;
    /// 先往下落多少格（砍掉的是树干唯一的一格时为 1：树坐到砍口上再倒）
    private float drop;
    /// 每 tick 末的姿态：角度、沿倒向的平移、竖直平移（转轴本身的位移）
    private float[] angles = {0.0F}, slides = {0.0F}, lifts = {0.0F};
    /// 第一次砸到地面的 tick（之后是弹跳）
    private int impactTick;
    /// 掉出了世界：不掉落东西
    private boolean voided;
    /// 每格相对 base 的坐标（x, y, z 三个一组）和方块
    private int[] offsets = new int[0];
    private BlockState[] states = new BlockState[0];
    // 仅服务端
    private UUID owner;
    private ItemStack tool = ItemStack.EMPTY;
    private boolean noDrops;
    /// 从存档读出来的树：动画已经没了，下一 tick 直接落地掉东西
    private boolean landNow;
    /// 客户端：方块颜色（树叶随群系），第一次渲染时算
    public int[] colors;

    public FallingTreeEntity(EntityType<? extends FallingTreeEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    /// cut：砍的那一格，这时它还在世界里（原版在事件之后才移走），算碰撞时当作空气
    public FallingTreeEntity(ServerLevel level, BlockPos base, float pivotX, float pivotY, float pivotZ, float dirX, float dirZ, float drop,
                             BlockPos cut, List<BlockPos> blocks, List<BlockState> states, ServerPlayer player, ItemStack tool, boolean noDrops) {
        this(ModEntities.FALLING_TREE.get(), level);
        this.base = base.immutable();
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.pivotZ = pivotZ;
        this.drop = drop;
        float length = Math.max((float) Math.sqrt(dirX * dirX + dirZ * dirZ), 1.0E-4F);
        this.dirX = dirX / length;
        this.dirZ = dirZ / length;
        this.offsets = new int[blocks.size() * 3];
        this.states = states.toArray(new BlockState[0]);
        for (int i = 0; i < blocks.size(); i++) {
            BlockPos pos = blocks.get(i);
            offsets[i * 3] = pos.getX() - base.getX();
            offsets[i * 3 + 1] = pos.getY() - base.getY();
            offsets[i * 3 + 2] = pos.getZ() - base.getZ();
        }
        this.owner = player.getUUID();
        this.tool = tool;
        this.noDrops = noDrops;
        computeTrajectory(level, cut);
        moveToPivot();
    }

    // ---------------------------------------------------------------- 访问

    public float pivotX() {
        return pivotX;
    }

    public float pivotY() {
        return pivotY;
    }

    public float pivotZ() {
        return pivotZ;
    }

    public float dirX() {
        return dirX;
    }

    public float dirZ() {
        return dirZ;
    }

    public int blockCount() {
        return states.length;
    }

    public BlockState state(int index) {
        return states[index];
    }

    public int offsetX(int index) {
        return offsets[index * 3];
    }

    public int offsetY(int index) {
        return offsets[index * 3 + 1];
    }

    public int offsetZ(int index) {
        return offsets[index * 3 + 2];
    }

    public BlockPos base() {
        return base;
    }

    private void moveToPivot() {
        setPos(base.getX() + pivotX(), base.getY() + pivotY(), base.getZ() + pivotZ());
    }

    /// 树高：最高一格的顶到转轴
    private float treeLength() {
        int top = 1;
        for (int i = 0; i < states.length; i++) top = Math.max(top, offsets[i * 3 + 1] + 1);
        return Math.max(top - pivotY(), 3.0F);
    }

    public int landTick() {
        return angles.length - 1;
    }

    /// 时刻 t 已经往下落了多少（落到砍口上）
    public float dropAt(float t) {
        if (drop <= 0.0F) return 0.0F;
        float seconds = Math.max(t, 0.0F) * 0.05F;
        return Math.min(drop, 0.5F * DROP_ACCELERATION * seconds * seconds);
    }

    public float angleAt(float t) {
        return sample(angles, t);
    }

    public float slideAt(float t) {
        return sample(slides, t);
    }

    public float liftAt(float t) {
        return sample(lifts, t);
    }

    private static float sample(float[] values, float t) {
        if (t <= 0.0F) return values[0];
        int i = (int) t;
        if (i >= values.length - 1) return values[values.length - 1];
        return values[i] + (values[i + 1] - values[i]) * (t - i);
    }

    // ---------------------------------------------------------------- 姿态

    /// 每格相对转轴：沿倒向、竖直、侧向三个分量
    private double along(int i) {
        return (offsets[i * 3] + 0.5 - pivotX) * dirX + (offsets[i * 3 + 2] + 0.5 - pivotZ) * dirZ;
    }

    private double up(int i) {
        return offsets[i * 3 + 1] + 0.5 - pivotY;
    }

    private double side(int i) {
        return -(offsets[i * 3] + 0.5 - pivotX) * dirZ + (offsets[i * 3 + 2] + 0.5 - pivotZ) * dirX;
    }

    /// 第 i 格方块中心在姿态 (angle, slide, lift) 下的世界坐标；绕水平轴 (dirZ, 0, -dirX) 转 angle：竖直方向转向倒向
    private Vec3 center(int i, float angle, float slide, float lift, float down) {
        double a = along(i), u = up(i), s = side(i);
        double cos = Math.cos(angle), sin = Math.sin(angle);
        double na = a * cos + u * sin + slide;
        double nu = u * cos - a * sin + lift - down;
        return new Vec3(base.getX() + pivotX + na * dirX - s * dirZ, base.getY() + pivotY + nu, base.getZ() + pivotZ + na * dirZ + s * dirX);
    }

    private Vec3 centerAt(int i, int tick) {
        int t = Math.min(Math.max(tick, 0), angles.length - 1);
        return center(i, angles[t], slides[t], lifts[t], dropAt(t));
    }

    // ---------------------------------------------------------------- 轨迹（仅服务端）

    private int[] sampleLogs, sampleLeaves;

    /// 碰撞：自己的原木撞到地形或别的树（树干、树冠都算）；自己的树叶只撞别的树干 —— 倒在别的树上会被挂住，
    /// 但树冠碰到地面不挡（压在地上自己碎），否则树会架在自己的树冠上
    private boolean collides(Level level, BlockPos cut, float angle, float slide, float lift) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i : sampleLogs) {
            Vec3 c = center(i, angle, slide, lift, drop);
            cursor.set(c.x, c.y, c.z);
            if (cursor.equals(cut)) continue;
            if (solid(level, cursor)) return true;
            // 树桩上留下的树叶紧挨着砍口：离转轴 2.5 格以内的原木不算树叶，免得树刚开始倒就被自己的树桩挂住
            double a = along(i), u = up(i);
            if (a * a + u * u > 6.25 && level.getBlockState(cursor).is(BlockTags.LEAVES)) return true;
        }
        for (int i : sampleLeaves) {
            Vec3 c = center(i, angle, slide, lift, drop);
            cursor.set(c.x, c.y, c.z);
            if (!cursor.equals(cut) && level.getBlockState(cursor).is(BlockTags.LOGS)) return true;
        }
        return false;
    }

    private static boolean solid(Level level, BlockPos pos) {
        BlockState hit = level.getBlockState(pos);
        if (hit.isAir() || hit.is(BlockTags.LEAVES) || hit.canBeReplaced()) return false;
        return !hit.getCollisionShape(level, pos).isEmpty();
    }

    private static int[] stride(int[] indices, int max) {
        int stride = Math.max(1, (indices.length + max - 1) / max);
        int[] result = new int[(indices.length + stride - 1) / stride];
        for (int j = 0, k = 0; j < indices.length; j += stride) result[k++] = indices[j];
        return result;
    }

    private void computeTrajectory(Level level, BlockPos cut) {
        if (states.length == 0) return;
        int[] logs = java.util.stream.IntStream.range(0, states.length).filter(i -> states[i].is(BlockTags.LOGS)).toArray();
        if (logs.length == 0) logs = java.util.stream.IntStream.range(0, states.length).toArray();
        sampleLogs = stride(logs, 300);
        sampleLeaves = stride(java.util.stream.IntStream.range(0, states.length).filter(i -> !states[i].is(BlockTags.LOGS)).toArray(), 300);
        // 重心（相对转轴，竖直时）：只按原木算
        double ca = 0.0, cu = 0.0;
        for (int i : logs) {
            ca += along(i);
            cu += up(i);
        }
        ca /= logs.length;
        cu /= logs.length;
        float length = treeLength();

        FloatArrayList a = new FloatArrayList(), s = new FloatArrayList(), l = new FloatArrayList();
        a.add(0.0F);
        s.add(0.0F);
        l.add(0.0F);

        // 1) 绕树桩倒下
        float angle = START_ANGLE, speed = START_SPEED, k = 1.5F * GRAVITY / length, dt = 0.0125F;
        boolean hit = false;
        for (int tick = 1; tick <= MAX_HINGE_TICKS; tick++) {
            float previous = angle;
            for (int step = 0; step < 4; step++) {
                speed += k * (float) Math.sin(angle) * dt;
                angle += speed * dt;
            }
            angle = Math.min(angle, MAX_HINGE);
            if (collides(level, cut, angle, 0.0F, 0.0F)) {
                float lo = previous, hi = angle;
                for (int r = 0; r < 6; r++) {
                    float mid = (lo + hi) * 0.5F;
                    if (collides(level, cut, mid, 0.0F, 0.0F)) hi = mid;
                    else lo = mid;
                }
                angle = lo;
                hit = true;
            }
            a.add(angle);
            s.add(0.0F);
            l.add(0.0F);
            if (hit || angle >= MAX_HINGE) break;
        }

        float slide = 0.0F, lift = 0.0F, omega = speed;
        boolean moved = false;
        float impactSpeed = 0.0F;

        // 2) 重心在不在支撑上：找最前面一个下面有地的原木
        double supportAlong = -Double.MAX_VALUE, supportUp = 0.0;
        boolean supported = false;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int i : sampleLogs) {
            Vec3 c = center(i, angle, slide, lift, drop);
            cursor.set(c.x, c.y - 1.0, c.z);
            if (cursor.equals(cut) || !solid(level, cursor)) continue;
            double cos = Math.cos(angle), sin = Math.sin(angle);
            double na = along(i) * cos + up(i) * sin;
            if (na > supportAlong) {
                supportAlong = na;
                supportUp = up(i) * cos - along(i) * sin;
                supported = true;
            }
        }
        double comAlong = ca * Math.cos(angle) + cu * Math.sin(angle);
        boolean falls = !supported || comAlong > supportAlong + 0.5;
        if (falls && supported) {
            // 翻过边缘：绕最前面那个支撑原木的前下沿转
            double p2a = supportAlong + 0.5, p2u = supportUp - 0.5;
            float phi = 0.0F, base0 = angle;
            float spin = hit ? omega * 0.25F : omega;
            boolean detached = false;
            for (int tick = 1; tick <= MAX_TIP_TICKS; tick++) {
                float previous = phi;
                for (int step = 0; step < 4; step++) {
                    // 绕边缘转的杆：α = g·力臂 / (L²/12 + d²)，d 是重心到边缘的距离
                    double ta = base0 + phi;
                    double da = rotA(ca, cu, ta) + tipSlide(p2a, p2u, phi) - p2a;
                    double du = rotU(ca, cu, ta) + tipLift(p2a, p2u, phi) - p2u;
                    double inertia = length * length / 12.0 + da * da + du * du;
                    spin += (float) (GRAVITY * Math.max(da, 0.05) / inertia) * dt;
                    phi += spin * dt;
                }
                float ta = base0 + phi;
                float ts = (float) tipSlide(p2a, p2u, phi), tl = (float) tipLift(p2a, p2u, phi);
                if (collides(level, cut, ta, ts, tl)) {
                    float lo = previous, hi = phi;
                    for (int r = 0; r < 6; r++) {
                        float mid = (lo + hi) * 0.5F;
                        if (collides(level, cut, base0 + mid, (float) tipSlide(p2a, p2u, mid), (float) tipLift(p2a, p2u, mid))) hi = mid;
                        else lo = mid;
                    }
                    phi = lo;
                    a.add(base0 + phi);
                    s.add((float) tipSlide(p2a, p2u, phi));
                    l.add((float) tipLift(p2a, p2u, phi));
                    impactSpeed = spin * length * 0.5F;
                    break;
                }
                a.add(ta);
                s.add(ts);
                l.add(tl);
                // 重心落到边缘高度以下就脱离（再晚就会像钟摆一样荡回崖壁）
                double comU = rotU(ca, cu, ta) + tl;
                if (ta >= DETACH_ANGLE || comU < p2u) {
                    detached = true;
                    angle = ta;
                    slide = ts;
                    lift = tl;
                    omega = spin;
                    break;
                }
            }
            moved = true;
            if (detached) {
                // 脱离时重心的速度 = ω × (重心 - 边缘)，再加一点离开崖壁的水平速度
                double da = rotA(ca, cu, angle) + slide - p2a, du = rotU(ca, cu, angle) + lift - p2u;
                impactSpeed = freeFall(level, cut, a, s, l, angle, slide, lift, omega, (float) Math.max(omega * du, 0.0) + 1.5F, (float) (-omega * da), ca, cu);
            }
        } else if (falls) {
            // 一开始就倒向空处（悬崖外、坑里）：直接带着倒下的速度脱离
            double comA = rotA(ca, cu, angle), comU = rotU(ca, cu, angle);
            impactSpeed = freeFall(level, cut, a, s, l, angle, 0.0F, 0.0F, omega, (float) (omega * comU), (float) (-omega * comA), ca, cu);
            moved = true;
        }

        // 停下后弹两下：倒在原地的树按角度弹，掉下去的按高度弹
        impactTick = a.size() - 1;
        float fa = a.getFloat(impactTick), fs = s.getFloat(impactTick), fl = l.getFloat(impactTick);
        if (moved) {
            float bounce = Math.min(0.35F, impactSpeed * 0.02F);
            for (int i = 1; i <= BOUNCE_TICKS; i++) {
                float up = i <= 6 ? bounce * (float) Math.sin(Math.PI * i / 6.0) : bounce * 0.3F * (float) Math.sin(Math.PI * (i - 6) / 4.0);
                a.add(fa);
                s.add(fs);
                l.add(fl + (voided ? 0.0F : up));
            }
        } else {
            float bounce = Math.min(0.09F, fa * 0.08F) * Math.min(1.0F, 8.0F / length + 0.35F);
            for (int i = 1; i <= BOUNCE_TICKS; i++) {
                float back = i <= 6 ? bounce * (float) Math.sin(Math.PI * i / 6.0) : bounce * 0.3F * (float) Math.sin(Math.PI * (i - 6) / 4.0);
                a.add(fa - back);
                s.add(fs);
                l.add(fl);
            }
        }
        a.set(a.size() - 1, fa);
        s.set(s.size() - 1, fs);
        l.set(l.size() - 1, fl);
        angles = a.toFloatArray();
        slides = s.toFloatArray();
        lifts = l.toFloatArray();
    }

    /// 自由下落：重心按抛体运动，树慢慢翻转（越转越慢），直到原木碰到地形；返回落地时的速度
    private float freeFall(Level level, BlockPos cut, FloatArrayList a, FloatArrayList s, FloatArrayList l,
                           float angle, float slide, float lift, float omega, float va, float vu, double ca, double cu) {
        double comA = rotA(ca, cu, angle) + slide, comU = rotU(ca, cu, angle) + lift, startU = comU;
        int bottom = level.getMinBuildHeight() - 8;
        for (int tick = 1; tick <= MAX_FALL_TICKS; tick++) {
            float pa = angle, ps = slide, pl = lift;
            vu -= FALL_GRAVITY * 0.05F;
            va *= 0.98F;
            comA += va * 0.05F;
            comU += vu * 0.05F;
            omega *= 0.9F;
            angle = Math.min(angle + omega * 0.05F, MAX_TUMBLE);
            slide = (float) (comA - rotA(ca, cu, angle));
            lift = (float) (comU - rotU(ca, cu, angle));
            boolean blocked = collides(level, cut, angle, slide, lift);
            if (blocked && comU - vu * 0.05F > startU - 2.0) {
                // 还在边缘上（树根那头还压着崖顶）：这一 tick 不往下掉，沿倒向滑出去
                comU -= vu * 0.05F;
                vu = 0.0F;
                va += FALL_GRAVITY * 0.4F * 0.05F;
                comA += FALL_GRAVITY * 0.4F * 0.05F * 0.05F;
                slide = (float) (comA - rotA(ca, cu, angle));
                lift = (float) (comU - rotU(ca, cu, angle));
                blocked = collides(level, cut, angle, slide, lift);
                if (blocked) {
                    angle = pa;
                    slide = (float) (comA - rotA(ca, cu, angle));
                    lift = (float) (comU - rotU(ca, cu, angle));
                    blocked = collides(level, cut, angle, slide, lift);
                }
                if (!blocked) {
                    a.add(angle);
                    s.add(slide);
                    l.add(lift);
                    continue;
                }
            }
            if (blocked) {
                float lo = 0.0F, hi = 1.0F;
                for (int r = 0; r < 6; r++) {
                    float mid = (lo + hi) * 0.5F;
                    if (collides(level, cut, pa + (angle - pa) * mid, ps + (slide - ps) * mid, pl + (lift - pl) * mid)) hi = mid;
                    else lo = mid;
                }
                a.add(pa + (angle - pa) * lo);
                s.add(ps + (slide - ps) * lo);
                l.add(pl + (lift - pl) * lo);
                return (float) Math.sqrt(va * va + vu * vu);
            }
            a.add(angle);
            s.add(slide);
            l.add(lift);
            if (base.getY() + pivotY + comU < bottom) {
                voided = true;
                return 0.0F;
            }
        }
        return (float) Math.sqrt(va * va + vu * vu);
    }

    private static double rotA(double a, double u, double angle) {
        return a * Math.cos(angle) + u * Math.sin(angle);
    }

    private static double rotU(double a, double u, double angle) {
        return u * Math.cos(angle) - a * Math.sin(angle);
    }

    /// 绕边缘 (p2a, p2u) 再转 phi 后，原转轴（在原点）的位置
    private static double tipSlide(double p2a, double p2u, double phi) {
        return p2a + rotA(-p2a, -p2u, phi);
    }

    private static double tipLift(double p2a, double p2u, double phi) {
        return p2u + rotU(-p2a, -p2u, phi);
    }

    // ---------------------------------------------------------------- 碰撞

    /// 两边各自正在倒的树（服务端线程、客户端线程各一份，互不相碰）
    private static final List<FallingTreeEntity> SERVER_TREES = new java.util.ArrayList<>(), CLIENT_TREES = new java.util.ArrayList<>();
    /// 这一 tick 每格的碰撞箱（min x, y, z），按需计算
    private double[] boxes;
    private int boxesTick = -1;

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        (level().isClientSide ? CLIENT_TREES : SERVER_TREES).add(this);
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        (level().isClientSide ? CLIENT_TREES : SERVER_TREES).remove(this);
    }

    /// 实体移动时（Entity.collide，见 EntityFallingTreeCollisionMixin）：倒下中的树每格都是一个方块大小的碰撞箱，
    /// 玩家和生物会被挡住，不能从树干、树冠里穿过去
    public static List<VoxelShape> addCollisions(Entity mover, AABB area, List<VoxelShape> shapes) {
        List<FallingTreeEntity> trees = mover.level().isClientSide ? CLIENT_TREES : SERVER_TREES;
        if (trees.isEmpty() || mover instanceof FallingTreeEntity) return shapes;
        List<VoxelShape> result = shapes;
        for (FallingTreeEntity tree : trees) {
            if (tree.level() != mover.level() || tree.isRemoved() || !tree.getBoundingBoxForCulling().intersects(area)) continue;
            double[] boxes = tree.boxes();
            for (int i = 0; i < boxes.length; i += 3) {
                double x = boxes[i], y = boxes[i + 1], z = boxes[i + 2];
                if (x + 1.0 <= area.minX || x >= area.maxX || y + 1.0 <= area.minY || y >= area.maxY || z + 1.0 <= area.minZ || z >= area.maxZ) continue;
                if (result == shapes) result = new java.util.ArrayList<>(shapes);
                result.add(Shapes.create(x, y, z, x + 1.0, y + 1.0, z + 1.0));
            }
        }
        return result;
    }

    private double[] boxes() {
        if (boxes != null && boxesTick == tickCount) return boxes;
        if (boxes == null || boxes.length != states.length * 3) boxes = new double[states.length * 3];
        for (int i = 0; i < states.length; i++) {
            Vec3 c = centerAt(i, tickCount);
            boxes[i * 3] = c.x - 0.5;
            boxes[i * 3 + 1] = c.y - 0.5;
            boxes[i * 3 + 2] = c.z - 0.5;
        }
        boxesTick = tickCount;
        return boxes;
    }

    // ---------------------------------------------------------------- 每 tick

    @Override
    public void tick() {
        if (level().isClientSide) return;
        if (!landNow && tickCount < impactTick) creak((ServerLevel) level());
        if (!landNow && tickCount == impactTick && !voided) impact((ServerLevel) level());
        if (landNow || tickCount >= landTick()) {
            if (!voided) land((ServerLevel) level());
            discard();
        }
    }

    private Vec3 middleAt(int tick) {
        Vec3 bottom = states.length > 0 ? centerAt(0, tick) : position();
        Vec3 top = states.length > 0 ? centerAt(states.length - 1, tick) : position();
        return bottom.add(top).scale(0.5);
    }

    /// 倒下途中木头的吱嘎声：开头断裂的一声脆响，之后越倒越快、吱嘎声越来越密、越来越尖
    private void creak(ServerLevel level) {
        float progress = Math.min(1.0F, tickCount / (float) Math.max(impactTick, 1));
        float weight = Math.min(1.0F, treeLength() / 16.0F);
        int hinge = hingeLog();
        if (tickCount == 1) {
            // 断开的一下：干脆的断裂声 + 木头碎裂声，砍口飞出一把木屑
            level.playSound(null, getX(), getY() + 0.5, getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.BLOCKS, 1.0F + 0.3F * weight, 0.65F - 0.15F * weight);
            level.playSound(null, getX(), getY() + 0.5, getZ(), SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.2F, 0.7F);
            if (hinge >= 0) chips(level, hinge, 3);
            return;
        }
        // 还挂在树桩上倾斜的时候：纤维一根根断开的“咔嚓”声，砍口不断崩出木屑
        float angle = angleAt(tickCount);
        if (hinge >= 0 && angle < 1.1F && slideAt(tickCount) == 0.0F && tickCount % 3 == 0 && random.nextInt(4) != 0) {
            chips(level, hinge, 1);
            level.playSound(null, getX(), getY() + 0.5, getZ(), SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.6F + 0.4F * angle, 0.55F + random.nextFloat() * 0.35F);
        }
        int interval = progress < 0.3F ? 9 : progress < 0.7F ? 6 : 4;
        if (tickCount % interval != 0) return;
        Vec3 middle = middleAt(tickCount);
        float pitch = 0.45F - 0.1F * weight + 0.25F * progress + random.nextFloat() * 0.06F;
        level.playSound(null, middle.x, middle.y, middle.z, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 0.8F + 0.4F * weight, pitch);
        if (random.nextInt(3) == 0) {
            level.playSound(null, middle.x, middle.y, middle.z, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.9F, 0.5F + 0.2F * progress);
        }
    }

    private int hingeLog = -2;

    /// 最靠近转轴的原木（砍口处），木屑从它那里崩出来
    private int hingeLog() {
        if (hingeLog != -2) return hingeLog;
        hingeLog = -1;
        double best = Double.MAX_VALUE;
        for (int i = 0; i < states.length; i++) {
            if (!states[i].is(BlockTags.LOGS)) continue;
            double a = along(i), u = up(i), d = a * a + u * u;
            if (d < best) {
                best = d;
                hingeLog = i;
            }
        }
        return hingeLog;
    }

    /// 砍口崩出木屑：用原木的破坏粒子（也带着原木碎裂的声音）
    private void chips(ServerLevel level, int log, int count) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        Vec3 c = centerAt(log, tickCount);
        for (int n = 0; n < count; n++) {
            cursor.set(c.x + (random.nextDouble() - 0.5) * 0.6, c.y - 0.3 + (random.nextDouble() - 0.5) * 0.4, c.z + (random.nextDouble() - 0.5) * 0.6);
            level.levelEvent(2001, cursor, Block.getId(states[log]));
        }
    }

    /// 砸到地上：沉闷的撞击声，树干砸地处扬起一点碎屑
    private void impact(ServerLevel level) {
        Vec3 middle = middleAt(impactTick);
        float weight = Math.min(1.0F, treeLength() / 16.0F);
        level.playSound(null, middle.x, middle.y, middle.z, SoundEvents.WOOD_FALL, SoundSource.BLOCKS, 2.0F + weight, 0.55F - 0.15F * weight);
        level.playSound(null, middle.x, middle.y, middle.z, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.BLOCKS, 0.6F + 0.4F * weight, 0.4F);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int dust = 0;
        for (int i = 0; i < states.length && dust < 8; i++) {
            if (!states[i].is(BlockTags.LOGS) || random.nextInt(3) != 0) continue;
            Vec3 c = centerAt(i, impactTick);
            level.levelEvent(2001, cursor.set(c.x, c.y, c.z), Block.getId(states[i]));
            dust++;
        }
    }

    /// 停稳后碎掉：每格按原版规则掉落（带上砍树时的斧头，时运、精准采集照常生效），挑一部分方块放碎裂粒子和声音
    private void land(ServerLevel level) {
        int tick = landTick();
        ServerPlayer player = owner == null ? null : level.getServer().getPlayerList().getPlayer(owner);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int particles = 0;
        boolean leaves = false;
        for (int i = 0; i < states.length; i++) {
            BlockState state = states[i];
            Vec3 c = centerAt(i, tick);
            cursor.set(c.x, c.y, c.z);
            // 掉进地形里的往上挪出来
            for (int up = 0; up < 4 && !level.getBlockState(cursor).getCollisionShape(level, cursor).isEmpty(); up++) cursor.move(0, 1, 0);
            boolean log = state.is(BlockTags.LOGS);
            leaves |= !log;
            if ((log && particles < 40) || (!log && particles < 40 && random.nextInt(6) == 0)) {
                level.levelEvent(2001, cursor, Block.getId(state));
                particles++;
            }
            if (noDrops) continue;
            BlockPos original = base.offset(offsets[i * 3], offsets[i * 3 + 1], offsets[i * 3 + 2]);
            List<ItemStack> drops = Block.getDrops(state, level, original, null, player, tool);
            for (ItemStack stack : drops) {
                if (stack.isEmpty()) continue;
                ItemEntity item = new ItemEntity(level, cursor.getX() + 0.5, cursor.getY() + 0.3, cursor.getZ() + 0.5, stack,
                        (random.nextDouble() - 0.5) * 0.1, 0.15, (random.nextDouble() - 0.5) * 0.1);
                item.setDefaultPickUpDelay();
                level.addFreshEntity(item);
            }
        }
        Vec3 middle = middleAt(tick);
        level.playSound(null, middle.x, middle.y, middle.z, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.5F, 0.55F);
        if (leaves) level.playSound(null, middle.x, middle.y, middle.z, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.5F, 0.7F);
    }

    // ---------------------------------------------------------------- 渲染范围

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        float radius = treeLength() + 1.5F;
        int t = Math.min(tickCount, angles.length - 1);
        double x = getX() + slides[t] * dirX, y = getY() + lifts[t], z = getZ() + slides[t] * dirZ;
        return new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 160.0 * 160.0;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    // ---------------------------------------------------------------- 同步与存档

    @Override
    protected void defineSynchedData(@NotNull net.minecraft.network.syncher.SynchedEntityData.Builder builder) {}

    @Override
    public void writeSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(base);
        buffer.writeFloat(pivotX);
        buffer.writeFloat(pivotY);
        buffer.writeFloat(pivotZ);
        buffer.writeFloat(dirX);
        buffer.writeFloat(dirZ);
        buffer.writeFloat(drop);
        buffer.writeVarInt(impactTick);
        buffer.writeVarInt(angles.length);
        for (int i = 0; i < angles.length; i++) {
            buffer.writeFloat(angles[i]);
            buffer.writeFloat(slides[i]);
            buffer.writeFloat(lifts[i]);
        }
        buffer.writeVarInt(states.length);
        for (int i = 0; i < states.length; i++) {
            buffer.writeVarInt(offsets[i * 3]);
            buffer.writeVarInt(offsets[i * 3 + 1]);
            buffer.writeVarInt(offsets[i * 3 + 2]);
            buffer.writeVarInt(Block.getId(states[i]));
        }
    }

    @Override
    public void readSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        base = buffer.readBlockPos();
        pivotX = buffer.readFloat();
        pivotY = buffer.readFloat();
        pivotZ = buffer.readFloat();
        dirX = buffer.readFloat();
        dirZ = buffer.readFloat();
        drop = buffer.readFloat();
        impactTick = buffer.readVarInt();
        int ticks = buffer.readVarInt();
        angles = new float[ticks];
        slides = new float[ticks];
        lifts = new float[ticks];
        for (int i = 0; i < ticks; i++) {
            angles[i] = buffer.readFloat();
            slides[i] = buffer.readFloat();
            lifts[i] = buffer.readFloat();
        }
        int count = buffer.readVarInt();
        offsets = new int[count * 3];
        states = new BlockState[count];
        for (int i = 0; i < count; i++) {
            offsets[i * 3] = buffer.readVarInt();
            offsets[i * 3 + 1] = buffer.readVarInt();
            offsets[i * 3 + 2] = buffer.readVarInt();
            states[i] = Block.stateById(buffer.readVarInt());
        }
        moveToPivot();
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        base = BlockPos.of(tag.getLong("Base"));
        pivotX = tag.getFloat("PivotX");
        pivotY = tag.getFloat("PivotY");
        pivotZ = tag.getFloat("PivotZ");
        dirX = tag.getFloat("DirX");
        dirZ = tag.getFloat("DirZ");
        drop = tag.getFloat("Drop");
        // 存档里只留最后的姿态：读回来直接在那里碎掉
        angles = new float[]{tag.getFloat("Land")};
        slides = new float[]{tag.getFloat("Slide")};
        lifts = new float[]{tag.getFloat("Lift")};
        voided = tag.getBoolean("Voided");
        offsets = tag.getIntArray("Offsets");
        ListTag list = tag.getList("States", Tag.TAG_COMPOUND);
        var lookup = level().holderLookup(Registries.BLOCK);
        states = new BlockState[list.size()];
        for (int i = 0; i < states.length; i++) states[i] = NbtUtils.readBlockState(lookup, list.getCompound(i));
        if (offsets.length != states.length * 3) {
            offsets = new int[0];
            states = new BlockState[0];
        }
        noDrops = tag.getBoolean("NoDrops");
        if (tag.hasUUID("Owner")) owner = tag.getUUID("Owner");
        if (tag.contains("Tool")) tool = ItemStack.parseOptional(level().registryAccess(), tag.getCompound("Tool"));
        impactTick = 0;
        landNow = true;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putLong("Base", base.asLong());
        tag.putFloat("PivotX", pivotX);
        tag.putFloat("PivotY", pivotY);
        tag.putFloat("PivotZ", pivotZ);
        tag.putFloat("DirX", dirX);
        tag.putFloat("DirZ", dirZ);
        tag.putFloat("Drop", drop);
        int last = angles.length - 1;
        tag.putFloat("Land", angles[last]);
        tag.putFloat("Slide", slides[last]);
        tag.putFloat("Lift", lifts[last]);
        tag.putBoolean("Voided", voided);
        tag.putIntArray("Offsets", offsets);
        ListTag list = new ListTag();
        for (BlockState state : states) list.add(NbtUtils.writeBlockState(state));
        tag.put("States", list);
        tag.putBoolean("NoDrops", noDrops);
        if (owner != null) tag.putUUID("Owner", owner);
        if (!tool.isEmpty()) tag.put("Tool", tool.save(level().registryAccess()));
    }
}
