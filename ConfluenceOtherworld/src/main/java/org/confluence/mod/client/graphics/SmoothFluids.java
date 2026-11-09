package org.confluence.mod.client.graphics;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.ClientConfigs;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.regex.Pattern;

/// 液体表面平滑（Confluence 画质）：水位变化时表面不再一格一格地跳，而是在一个流动周期内连续滑到新高度。
///
/// 原版（以及 Flowing Fluids 的有限液体）每次水位变化都换一份区块网格，表面一下子跳 1/8 格，
/// 网格又是异步重建的，看起来就是一块一块地动。这里在客户端方块变化时（Level.setBlock，见 LevelFluidSmoothingMixin）
/// 按原版 LiquidBlockRenderer 的规则算出受影响的表面角点“原来多高、现在多高”，把正在过渡的角点放进一张小哈希表上传到 GPU；
/// 地形顶点着色器（solid / translucent，见 patch）找到位置和新高度都对得上的顶点，把它从旧高度插值到新高度。
/// 网格还没换成新的时，顶点高度对不上新高度，就保持原样，不会反向跳。
public final class SmoothFluids {
    /// 原版默认纹理单元 0～11，Magic Lib 动态光用 12
    private static final int TEXTURE_UNIT = 13;
    private static final long BASE_TIME = System.nanoTime();
    private static final Pattern POSITION = Pattern.compile("vec3\\s+pos\\s*=\\s*Position\\s*\\+\\s*ChunkOffset\\s*;");

    /// 正在过渡的角点：键 = 角点（x, 方块 y, z）
    private static final Long2ObjectOpenHashMap<Corner> corners = new Long2ObjectOpenHashMap<>();
    private static boolean dirty;
    private static int buffer, texture, mask = -1, slots;
    private static FloatBuffer staging;
    private static int anchorX, anchorY, anchorZ;
    private static float cameraOffsetX, cameraOffsetY, cameraOffsetZ, time;
    /// 这一帧的锚点（相机所在区段原点）；anchorX/Y/Z 是表格上传时用的锚点，两者不同就要重新上传
    private static int frameAnchorX, frameAnchorY, frameAnchorZ;
    private static long frameVersion;

    /// 方块变化前算出来的旧高度。只处理渲染线程上的当前客户端世界（其他线程、预览用的假世界一律不碰），
    /// setBlock 里还可能嵌套 setBlock：只记最外层，depth 计数，每帧归零防止异常留下脏状态
    private static final double[] before = new double[8];
    private static final boolean[] beforeFluid = new boolean[8];
    private static boolean pending;
    private static int depth;

    private SmoothFluids() {}

    /// 过渡走完后再保留一会儿：慢一步重建的区块网格（大片流动时很常见）在这段时间里也被固定在新高度，不会先闪回旧高度
    private static final float HOLD = 1.5F;
    /// 上限：超大规模流动（海水灌进峡谷）时不再给新的变化做过渡，直接跳，避免 CPU 计算和显卡查表压力过大
    private static final int MAX_CORNERS = 8192;
    private static final int MAX_CHANGES_PER_FRAME = 1500;
    private static int changesThisFrame;

    /// oldY → newY 的过渡（缓动和着色器里一致）
    private record Corner(int x, int y, int z, double newY, double oldY, float start, float duration) {
        double displayed(float now) {
            float t = duration <= 0.0F ? 1.0F : Math.min(1.0F, Math.max(0.0F, (now - start) / duration));
            t = t * t * (3.0F - 2.0F * t);
            return oldY + (newY - oldY) * t;
        }
    }

    public static boolean enabled() {
        return ClientConfigs.smoothFluids && !ShaderPackDetector.shaderPackInUse();
    }

    private static float now() {
        return (System.nanoTime() - BASE_TIME) / 1.0E9F;
    }

    // ---------------------------------------------------------------- 方块变化

    /// Level.setBlock 开头：只处理客户端、而且这一格变化前后至少有一边是液体的情况
    private static boolean tracked(Level level) {
        return level.isClientSide && RenderSystem.isOnRenderThread() && level == Minecraft.getInstance().level;
    }

    public static void beforeChange(Level level, BlockPos pos, BlockState newState) {
        if (!tracked(level)) return;
        if (++depth > 1) return;
        pending = false;
        if (!enabled()) return;
        if (level.getFluidState(pos).isEmpty() && newState.getFluidState().isEmpty()) return;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (changesThisFrame >= MAX_CHANGES_PER_FRAME || corners.size() >= MAX_CORNERS
                || camera.getPosition().distanceToSqr(Vec3.atCenterOf(pos)) > 48.0 * 48.0) {
            // 这次变化不做过渡：它的角点上如果还有旧的过渡，必须立刻去掉，
            // 否则旧过渡会把这些角点钉在过时的高度上，新网格的顶点对不上，表面就裂开
            if (!corners.isEmpty()) {
                for (int i = 0; i < 8; i++) {
                    long key = BlockPos.asLong(pos.getX() + (i & 1), pos.getY() - (i >> 2), pos.getZ() + ((i >> 1) & 1));
                    if (corners.remove(key) != null) dirty = true;
                }
            }
            return;
        }
        changesThisFrame++;
        // 这一格和下面一格（上面有没有液体会改变下面一格的表面高度）的 4 个角点
        for (int i = 0; i < 8; i++) {
            int cx = pos.getX() + (i & 1), cz = pos.getZ() + ((i >> 1) & 1), y = pos.getY() - (i >> 2);
            double h = cornerHeight(level, cx, y, cz);
            beforeFluid[i] = h >= 0.0;
            before[i] = h;
        }
        pending = true;
    }

    /// Level.setBlock 结束（改成功了）：再算一遍新高度，记下需要过渡的角点
    public static void afterChange(Level level, BlockPos pos, boolean changed) {
        if (!tracked(level)) return;
        if (depth > 0) depth--;
        if (depth != 0 || !pending) return;
        pending = false;
        if (!changed) return;
        float now = now();
        for (int i = 0; i < 8; i++) {
            int cx = pos.getX() + (i & 1), cz = pos.getZ() + ((i >> 1) & 1), y = pos.getY() - (i >> 2);
            double h = cornerHeight(level, cx, y, cz);
            long key = BlockPos.asLong(cx, y, cz);
            // 只过渡真正的液体表面：没有液体（-1）、满格（=1，上面还有液体，顶面不画，高度和方块顶面重合）都不动，
            // 否则会把同一个角上的石头、玻璃等方块的顶点也拉走
            if (h <= 0.001 || h >= 0.999) {
                if (corners.remove(key) != null) dirty = true;
                continue;
            }
            double newY = y + h;
            Corner previous = corners.get(key);
            // 新出现的液体直接出现（从地面升起来会像往看不见的杯子里倒水）；原来就有表面的才过渡
            if (previous == null && !beforeFluid[i]) continue;
            // 旧的显示高度：上一次过渡还没走完就从当前位置接着走
            double oldY = previous != null ? previous.displayed(now) : y + before[i];
            if (Math.abs(oldY - newY) < 0.002) {
                if (corners.remove(key) != null) dirty = true;
                continue;
            }
            corners.put(key, new Corner(cx, y, cz, newY, oldY, now, duration(level, cx, y, cz)));
            dirty = true;
        }
    }

    /// 过渡时长 = 这种液体流动一次的间隔（Flowing Fluids 会改这个间隔），限制在 0.1～1 秒
    private static float duration(Level level, int cx, int y, int cz) {
        Fluid fluid = cornerFluid(level, cx, y, cz);
        int ticks = fluid instanceof FlowingFluid flowing ? flowing.getTickDelay(level) : 5;
        return Math.max(0.1F, Math.min(1.0F, ticks * 0.05F + 0.03F));
    }

    // ---------------------------------------------------------------- 角点高度（与原版 LiquidBlockRenderer 相同的规则）

    /// 角点 (cx, cz) 在第 y 层的液体表面高度（0～1）；四个相邻方块都不是液体时返回 -1
    private static double cornerHeight(Level level, int cx, int y, int cz) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        // 共用这个角点的 4 个方块，取第一个是液体的那个按它自己的视角算（相邻方块算出来的平均值相同）
        for (int i = 0; i < 4; i++) {
            int bx = cx - 1 + (i & 1), bz = cz - 1 + ((i >> 1) & 1);
            pos.set(bx, y, bz);
            FluidState state = level.getFluidState(pos);
            if (state.isEmpty()) continue;
            Fluid fluid = state.getType();
            int dx = cx - bx, dz = cz - bz;
            float self = height(level, fluid, pos.immutable());
            if (self >= 1.0F) return 1.0;
            float ns = height(level, fluid, new BlockPos(bx, y, dz == 0 ? bz - 1 : bz + 1));
            float ew = height(level, fluid, new BlockPos(dx == 0 ? bx - 1 : bx + 1, y, bz));
            BlockPos diagonal = new BlockPos(dx == 0 ? bx - 1 : bx + 1, y, dz == 0 ? bz - 1 : bz + 1);
            return averageHeight(level, fluid, self, ns, ew, diagonal);
        }
        return -1.0;
    }

    private static Fluid cornerFluid(Level level, int cx, int y, int cz) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 4; i++) {
            FluidState state = level.getFluidState(pos.set(cx - 1 + (i & 1), y, cz - 1 + ((i >> 1) & 1)));
            if (!state.isEmpty()) return state.getType();
        }
        return null;
    }

    private static float height(Level level, Fluid fluid, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = state.getFluidState();
        if (fluid.isSame(fluidState.getType())) {
            return fluid.isSame(level.getFluidState(pos.above()).getType()) ? 1.0F : fluidState.getOwnHeight();
        }
        return !state.isSolid() ? 0.0F : -1.0F;
    }

    private static float averageHeight(Level level, Fluid fluid, float current, float height1, float height2, BlockPos diagonal) {
        if (height2 >= 1.0F || height1 >= 1.0F) return 1.0F;
        float[] sum = new float[2];
        if (height2 > 0.0F || height1 > 0.0F) {
            float h = height(level, fluid, diagonal);
            if (h >= 1.0F) return 1.0F;
            addWeighted(sum, h);
        }
        addWeighted(sum, current);
        addWeighted(sum, height2);
        addWeighted(sum, height1);
        return sum[0] / sum[1];
    }

    private static void addWeighted(float[] sum, float height) {
        if (height >= 0.8F) {
            sum[0] += height * 10.0F;
            sum[1] += 10.0F;
        } else if (height >= 0.0F) {
            sum[0] += height;
            sum[1]++;
        }
    }

    // ---------------------------------------------------------------- 每帧上传

    /// AFTER_SKY（画地形之前）：去掉走完的过渡，必要时重新上传
    public static void prepareFrame(Camera camera) {
        RenderSystem.assertOnRenderThread();
        depth = 0;
        pending = false;
        changesThisFrame = 0;
        time = now();
        // 相机相对锚点（区段原点）的偏移：表面过渡查表和水面波纹都用它，每帧都更新
        Vec3 position = camera.getPosition();
        int ax = SectionPos.blockToSectionCoord(position.x) << 4;
        int ay = SectionPos.blockToSectionCoord(position.y) << 4;
        int az = SectionPos.blockToSectionCoord(position.z) << 4;
        cameraOffsetX = (float) (position.x - ax);
        cameraOffsetY = (float) (position.y - ay);
        cameraOffsetZ = (float) (position.z - az);
        frameAnchorX = ax;
        frameAnchorY = ay;
        frameAnchorZ = az;
        frameVersion++;
        WaterSurface.prepareFrame(camera);
        if (!enabled()) {
            if (!corners.isEmpty()) corners.clear();
            mask = -1;
            return;
        }
        if (!corners.isEmpty()) {
            var iterator = corners.values().iterator();
            while (iterator.hasNext()) {
                Corner corner = iterator.next();
                if (time - corner.start() > corner.duration() + HOLD) {
                    iterator.remove();
                    dirty = true;
                }
            }
        }
        if (corners.isEmpty()) {
            mask = -1;
            dirty = false;
            return;
        }
        if (dirty || ax != anchorX || ay != anchorY || az != anchorZ) {
            anchorX = ax;
            anchorY = ay;
            anchorZ = az;
            upload();
            dirty = false;
        }
    }

    public static void clearWorld() {
        corners.clear();
        mask = -1;
        dirty = false;
    }

    /// 线性探测哈希表：每格 2 个 texel —— (x, y, z, 1)、(新高度, 旧高度, 开始时间, 时长)，坐标都相对锚点
    private static void upload() {
        slots = 1;
        while (slots < corners.size() * 2) slots <<= 1;
        int floats = slots * 8;
        if (staging == null || staging.capacity() < floats) {
            if (staging != null) MemoryUtil.memFree(staging);
            staging = MemoryUtil.memAllocFloat(Integer.highestOneBit(Math.max(floats, 64) - 1) << 1);
        }
        staging.clear();
        for (int i = 0; i < floats; i++) staging.put(i, 0.0F);
        for (Corner corner : corners.values()) {
            int x = corner.x() - anchorX, y = corner.y() - anchorY, z = corner.z() - anchorZ;
            int slot = hash(x, y, z) & (slots - 1);
            while (staging.get(slot * 8 + 3) != 0.0F) slot = (slot + 1) & (slots - 1);
            staging.put(slot * 8, x).put(slot * 8 + 1, y).put(slot * 8 + 2, z).put(slot * 8 + 3, 1.0F);
            staging.put(slot * 8 + 4, (float) (corner.newY() - anchorY));
            staging.put(slot * 8 + 5, (float) (corner.oldY() - anchorY));
            staging.put(slot * 8 + 6, corner.start());
            staging.put(slot * 8 + 7, corner.duration());
        }
        staging.position(0).limit(floats);
        if (buffer == 0) buffer = GL15.glGenBuffers();
        int previous = GL11.glGetInteger(GL31.GL_TEXTURE_BUFFER);
        GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, buffer);
        GL15.glBufferData(GL31.GL_TEXTURE_BUFFER, staging, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL31.GL_TEXTURE_BUFFER, previous);
        if (texture == 0) {
            texture = GL11.glGenTextures();
            int active = GlStateManager._getActiveTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + TEXTURE_UNIT);
            int previousTexture = GL11.glGetInteger(GL31.GL_TEXTURE_BINDING_BUFFER);
            GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, texture);
            GL31.glTexBuffer(GL31.GL_TEXTURE_BUFFER, GL30.GL_RGBA32F, buffer);
            GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, previousTexture);
            GL13.glActiveTexture(active);
        }
        mask = slots - 1;
    }

    private static int hash(int x, int y, int z) {
        return x * 73856093 ^ y * 19349663 ^ z * 83492791;
    }

    // ---------------------------------------------------------------- 着色器

    private static final String FUNCTIONS = """
            uniform samplerBuffer CfFluidData;
            uniform int CfFluidMask;
            uniform vec3 CfCameraOffset;
            uniform float CfTime;

            // Confluence smooth fluids: every liquid vertex at an animating corner is drawn at the corner's current
            // height (old -> new), whichever mesh it comes from, so neighbouring sections rebuilt at different times
            // never leave cracks.
            float cfFluidLift(vec3 pos) {
                if (CfFluidMask < 0) return 0.0;
                vec3 rel = pos + CfCameraOffset;
                // ordinary block vertices sit on whole heights: skip them before any lookup
                float fy = fract(rel.y);
                if (fy < 0.002 || fy > 0.9985) return 0.0;
                vec2 corner = floor(rel.xz + 0.5);
                if (any(greaterThan(abs(rel.xz - corner), vec2(0.002)))) return 0.0;
                ivec3 key = ivec3(int(corner.x), int(floor(rel.y - 0.0005)), int(corner.y));
                int slot = int((uint(key.x) * 73856093u ^ uint(key.y) * 19349663u ^ uint(key.z) * 83492791u) & uint(CfFluidMask));
                for (int probe = 0; probe <= CfFluidMask; ++probe) {
                    vec4 head = texelFetch(CfFluidData, slot * 2);
                    if (head.w == 0.0) return 0.0;
                    if (all(equal(ivec3(head.xyz), key))) {
                        vec4 data = texelFetch(CfFluidData, slot * 2 + 1);
                        // vertices near the old..new range belong to it; the margin is wide for water (a mesh rebuilt
                        // late can be several steps behind) and narrow for the solid layer (slabs, stairs next to lava)
                        float lo = min(data.x, data.y) - CF_FLUID_WINDOW;
                        float hi = max(data.x, data.y) + CF_FLUID_WINDOW;
                        if (rel.y < lo || rel.y > hi) return 0.0;
                        float t = clamp((CfTime - data.z) / data.w, 0.0, 1.0);
                        t = t * t * (3.0 - 2.0 * t);
                        return mix(data.y, data.x, t) - 0.0005 - rel.y;
                    }
                    slot = (slot + 1) & CfFluidMask;
                }
                return 0.0;
            }
            """;

    /// 只改原版 solid（岩浆）和 translucent（水、蜂蜜等）地形顶点着色器：在算出 pos 之后把液体表面顶点抬/压回过渡中的高度
    public static String patch(String source, boolean vertex, String name) {
        if (!vertex) return source;
        String key = name.startsWith("minecraft:") ? name.substring(10) : name;
        if (!key.equals("rendertype_translucent") && !key.equals("rendertype_solid")) return source;
        if (source.contains("cfFluidLift")) return source;
        var position = POSITION.matcher(source);
        int main = source.indexOf("void main(");
        if (!position.find() || main < 0 || main > position.start()) return source;
        boolean translucent = key.equals("rendertype_translucent");
        String window = translucent ? "0.35" : "0.012";
        // translucent：顺带把相机相对位置和天空光传给片元着色器，水面反射和波纹用（见 WaterSurface）
        // cfTop：顶点在小数高度上（液面的边）为 1，在整数高度上（侧面的底边）为 0；顶面四个点都是 1，
        // 片元里插值后仍是 1，侧面会小于 1 —— 不用屏幕导数判断朝向，块与块的接缝处就不会出错
        // 波浪：在 cfTop 算完之后再把水面顶点抬起/压下（见 WaterSurface.WAVE_FUNCTIONS）
        String outputs = translucent ? WaterSurface.WAVE_FUNCTIONS + "out vec3 cfViewPos;\nout float cfSkyLight;\nout float cfTop;\nout float cfWaveAmp;\n" : "";
        String assign = translucent ? "\n    cfSkyLight = float(UV2.y) / 240.0;"
                + "\n    float cfFy = fract(pos.y + CfCameraOffset.y);\n    cfTop = (cfFy > 0.01 && cfFy < 0.998) ? 1.0 : 0.0;"
                + "\n    cfWaveAmp = cfWaveFactor(cfFy, cfTop, UV0);"
                + "\n    pos.y += cfWaveHeight((pos + CfCameraOffset + CfAnchor).xz, CfTime) * cfWaveAmp;"
                + "\n    cfViewPos = pos;" : "";
        return source.substring(0, main) + "#define CF_FLUID_WINDOW " + window + "\n" + FUNCTIONS + "\n" + outputs
                + source.substring(main, position.end())
                + "\n    pos.y += cfFluidLift(pos);" + assign + source.substring(position.end());
    }

    /// 每个 ShaderInstance 自己的 uniform 位置
    public static final class Binding {
        private final int data, maskLocation, offset, timeLocation;
        private final WaterSurface.Uniforms water;
        private int previousTexture;
        private boolean bound, samplerSet;
        private int appliedMask = Integer.MIN_VALUE;
        private long appliedVersion = -1;

        public Binding(ShaderInstance shader) {
            data = GL20.glGetUniformLocation(shader.getId(), "CfFluidData");
            maskLocation = GL20.glGetUniformLocation(shader.getId(), "CfFluidMask");
            offset = GL20.glGetUniformLocation(shader.getId(), "CfCameraOffset");
            timeLocation = GL20.glGetUniformLocation(shader.getId(), "CfTime");
            water = new WaterSurface.Uniforms(shader);
        }

        public void apply() {
            if (maskLocation < 0) return;
            clear();
            if (appliedMask != mask) {
                GL20.glUniform1i(maskLocation, mask);
                appliedMask = mask;
            }
            if (!samplerSet) {
                GL20.glUniform1i(data, TEXTURE_UNIT);
                samplerSet = true;
            }
            if (appliedVersion != frameVersion) {
                if (offset >= 0) GL20.glUniform3f(offset, cameraOffsetX, cameraOffsetY, cameraOffsetZ);
                if (timeLocation >= 0) GL20.glUniform1f(timeLocation, time);
                water.apply(frameAnchorX, frameAnchorY, frameAnchorZ);
                appliedVersion = frameVersion;
            }
            if (mask < 0) return;
            int active = GlStateManager._getActiveTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + TEXTURE_UNIT);
            previousTexture = GL11.glGetInteger(GL31.GL_TEXTURE_BINDING_BUFFER);
            GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, texture);
            GL13.glActiveTexture(active);
            bound = true;
        }

        public void clear() {
            if (!bound) return;
            int active = GlStateManager._getActiveTexture();
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + TEXTURE_UNIT);
            GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, previousTexture);
            GL13.glActiveTexture(active);
            bound = false;
        }
    }
}
