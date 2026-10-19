package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.ModRenderTypes;
import org.confluence.mod.common.block.functional.terraform.EnvironmentalMachineBlock;
import org.confluence.mod.common.block.functional.terraform.Solution;
import org.confluence.mod.common.data.saved.EnvironmentalMachines;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.Arrays;

public final class EnvironmentalMachineRenderer extends GeoBlockRenderer<EnvironmentalMachineBlock.BEntity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/clentamachine.geo.json");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/clentamachine.animation.json");

    // 模型尺寸、贴面偏移与字号密度。
    private static final float SCREEN_CUBE_WIDTH = 16;
    private static final float SCREEN_CUBE_HEIGHT = 2;
    private static final float SCREEN_CUBE_DEPTH = 8;
    private static final double SCREEN_SURFACE_OFFSET = 0.005;
    private static final float SCREEN_ROTATION = 90;
    private static final float SCREEN_PIXEL_DENSITY = 256;
    private static final double TEXT_SCROLL_SPEED = 12;
    private static final double TEXT_SCROLL_PAUSE_SECONDS = 1.5;
    private static final double MILLIS_PER_SECOND = 1000;
    private static final int GLYPH_VERTEX_COUNT = 4;
    private static final double TEXT_DEPTH = -0.384;
    private static final float BAR_TRACK_DEPTH = 0;
    private static final float BAR_FILL_DEPTH = -0.128F;
    // 白边内侧的文字边界、行距和每列可用宽度。
    private static final float TEXT_LEFT = -88;
    private static final float TEXT_RIGHT = 88;
    private static final float HEADER_Y = -26;
    private static final float PROGRESS_TEXT_Y = -15;
    private static final float SOLUTION_TEXT_Y = 4;
    private static final float TARGET_TEXT_Y = 16;
    private static final float FOOTER_Y = 28;
    private static final float HEADER_STATE_WIDTH = 98;
    private static final float HEADER_AREA_WIDTH = 72;
    private static final float PROGRESS_LABEL_WIDTH = 76;
    private static final float PROGRESS_VALUE_WIDTH = 94;
    private static final float FULL_TEXT_WIDTH = TEXT_RIGHT - TEXT_LEFT;
    private static final float STOCK_TEXT_WIDTH = 112;
    private static final float RATE_TEXT_WIDTH = 58;
    private static final int RUNNING_COLOR = 0xFF73FFD3;
    private static final int STOPPED_COLOR = 0xFFFFDF92;
    private static final int WARNING_COLOR = 0xFFFF7064;
    private static final int LABEL_COLOR = 0xFFE1F6FF;
    private static final int VALUE_COLOR = 0xFFFFFFFF;
    private static final int TRACK_COLOR = 0xFF90B9C7;
    private static final int PAUSED_FILL_COLOR = 0xFFCCDDE3;

    // 屏幕局部像素，256 像素等于一个方块；加宽时左右对称展开。
    private static final float PROGRESS_BAR_WIDTH = 184;
    private static final float PROGRESS_BAR_HEIGHT = 6;
    private static final float PROGRESS_BAR_X = -PROGRESS_BAR_WIDTH / 2;
    private static final float PROGRESS_BAR_Y = -4;

    public static ResourceLocation texture(boolean world) {return Confluence.asResource("textures/block/" + (world ? "terraformer" : "clentaminator") + ".png");}

    public EnvironmentalMachineRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(EnvironmentalMachineBlock.BEntity entity) {return MODEL;}

            @Override
            public ResourceLocation getTextureResource(EnvironmentalMachineBlock.BEntity entity) {return texture(((EnvironmentalMachineBlock) entity.getBlockState().getBlock()).world);}

            @Override
            public ResourceLocation getAnimationResource(EnvironmentalMachineBlock.BEntity entity) {return ANIMATION;}
        });
    }

    @Override
    public void defaultRender(PoseStack pose, EnvironmentalMachineBlock.BEntity entity, MultiBufferSource source,
                              RenderType type, VertexConsumer buffer, float yaw, float partialTick, int light) {
        if (entity.isBase)
            super.defaultRender(pose, entity, source, type, buffer, yaw, partialTick, light);
    }

    // 贴图只有全透明和不透明像素；使用带背面剔除的裁切渲染，保留负体积且避免透明面排序穿插。
    @Override
    public RenderType getRenderType(EnvironmentalMachineBlock.BEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
        return RenderType.entityCutout(texture);
    }

    // 只染色独立的溶液骨骼，机器金属外壳和罐体保持贴图原色；无燃料时不显示液体。
    @Override
    public void renderRecursively(PoseStack pose, EnvironmentalMachineBlock.BEntity entity, GeoBone bone, RenderType type,
                                  MultiBufferSource source, VertexConsumer buffer, boolean rerender, float partialTick, int light, int overlay, float red, float green, float blue, float alpha) {
        if (bone.getName().endsWith("solutions")) {
            int slot = bone.getName().startsWith("left_") ? 0 : bone.getName().startsWith("right_") ? 1 : bone.getName().startsWith("back_") ? 2 : -1;
            if (slot >= 0 && !entity.hasSolutionInSlot(slot)) return;
            Solution solution = slot >= 0 ? entity.slotSolutions[slot] : entity.solution;
            if (solution == null) return;
            red = (solution.color >> 16 & 255) / 255.0F;
            green = (solution.color >> 8 & 255) / 255.0F;
            blue = (solution.color & 255) / 255.0F;
        }
        super.renderRecursively(pose, entity, bone, type, source, buffer, rerender, partialTick, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void actuallyRender(PoseStack pose, EnvironmentalMachineBlock.BEntity entity, BakedGeoModel model,
                               RenderType type, MultiBufferSource source, VertexConsumer buffer, boolean rerender,
                               float partialTick, int light, int overlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(pose, entity, model, type, source, buffer, rerender, partialTick, light, overlay, red, green, blue, alpha);
        if (!rerender)
            getGeoModel().getBone("main").ifPresent(bone -> renderScreen(pose, entity, bone, source));
    }

    // 使用屏幕 cube 的顶面中心和局部法线，随 cube、骨骼及方块朝向一起变换。
    private void renderScreen(PoseStack pose, EnvironmentalMachineBlock.BEntity entity, GeoBone main, MultiBufferSource source) {
        for (GeoCube cube : main.getCubes()) {
            if (cube.size().x != SCREEN_CUBE_WIDTH || cube.size().y != SCREEN_CUBE_HEIGHT || cube.size().z != SCREEN_CUBE_DEPTH || cube.quads() == null)
                continue;
            for (GeoQuad face : cube.quads()) {
                if (face == null || face.direction() != Direction.UP) continue;
                Vector3f center = new Vector3f();
                for (GeoVertex vertex : face.vertices()) center.add(vertex.position());
                center.div(face.vertices().length);
                pose.pushPose();
                applyBonePose(pose, main);
                RenderUtils.translateToPivotPoint(pose, cube);
                RenderUtils.rotateMatrixAroundCube(pose, cube);
                RenderUtils.translateAwayFromPivotPoint(pose, cube);
                // 以方块为单位沿面的法线外移，避免与原屏幕贴图产生深度闪烁。
                pose.translate(center.x, center.y + SCREEN_SURFACE_OFFSET, center.z);
                pose.mulPose(Axis.XP.rotationDegrees(SCREEN_ROTATION));
                // 贴图白边内侧只有 12×5 模型像素，使用更细的屏幕坐标并留出安全边距。
                pose.scale(-1 / SCREEN_PIXEL_DENSITY, -1 / SCREEN_PIXEL_DENSITY, 1 / SCREEN_PIXEL_DENSITY);
                int progress = Mth.clamp(entity.fuelProgress, 0, EnvironmentalMachines.FUEL_PROGRESS_MAX);
                int stock = Arrays.stream(entity.stored).sum();
                boolean working = entity.isWorking();
                String state = entity.mixedSolutions ? "mixed" : working ? "running" : entity.solution == null ? "empty" : "stopped";
                int accent = entity.mixedSolutions ? WARNING_COLOR : working ? RUNNING_COLOR : STOPPED_COLOR;
                screenText(source, pose, screenLabel(state), TEXT_LEFT, HEADER_Y, HEADER_STATE_WIDTH, false, accent);
                screenText(source, pose, screenLabel("area", entity.rangeChunks,
                                entity.rangeChunks),
                        TEXT_RIGHT, HEADER_Y, HEADER_AREA_WIDTH, true, LABEL_COLOR);
                if (entity.mixedSolutions) {
                    screenText(source, pose, screenLabel(entity.mixedExplosionEnabled ? "danger" : "blocked"), TEXT_LEFT, PROGRESS_TEXT_Y, FULL_TEXT_WIDTH, false, WARNING_COLOR);
                    screenText(source, pose, screenLabel("resolve"), TEXT_LEFT, SOLUTION_TEXT_Y, FULL_TEXT_WIDTH, false, STOPPED_COLOR);
                } else {
                    screenText(source, pose, screenLabel("fuel"), TEXT_LEFT, PROGRESS_TEXT_Y, PROGRESS_LABEL_WIDTH, false, LABEL_COLOR);
                    screenText(source, pose, progress + "/" + EnvironmentalMachines.FUEL_PROGRESS_MAX + " (" + progress + "%)", TEXT_RIGHT, PROGRESS_TEXT_Y, PROGRESS_VALUE_WIDTH, true, VALUE_COLOR);
                    screenQuad(source, pose.last().pose(), PROGRESS_BAR_X, PROGRESS_BAR_Y,
                            PROGRESS_BAR_X + PROGRESS_BAR_WIDTH, PROGRESS_BAR_Y + PROGRESS_BAR_HEIGHT, BAR_TRACK_DEPTH, TRACK_COLOR);
                    if (progress > 0)
                        screenQuad(source, pose.last().pose(), PROGRESS_BAR_X, PROGRESS_BAR_Y,
                                PROGRESS_BAR_X + PROGRESS_BAR_WIDTH * progress / EnvironmentalMachines.FUEL_PROGRESS_MAX, PROGRESS_BAR_Y + PROGRESS_BAR_HEIGHT,
                                BAR_FILL_DEPTH, working ? RUNNING_COLOR : PAUSED_FILL_COLOR);
                    Component solution = entity.solution == null ? Component.translatable("message.confluence.environmental_machine.empty")
                            : entity.solution.item().getDescription();
                    screenText(source, pose, screenLabel("solution", solution), TEXT_LEFT, SOLUTION_TEXT_Y, FULL_TEXT_WIDTH, false, VALUE_COLOR);
                }
                // 转换目标与当前生效溶液一致，混装时不显示单一目标。
                String target = entity.mixedSolutions ? "target.mixed" : entity.solution == null ? "target.none" : "target." + entity.solution.id();
                screenText(source, pose, screenLabel("target", screenLabel(target)), TEXT_LEFT, TARGET_TEXT_Y,
                        FULL_TEXT_WIDTH, false, entity.mixedSolutions ? WARNING_COLOR : VALUE_COLOR);
                screenText(source, pose, screenLabel("stock", stock, EnvironmentalMachines.CAPACITY * EnvironmentalMachines.SLOT_COUNT),
                        TEXT_LEFT, FOOTER_Y, STOCK_TEXT_WIDTH, false, LABEL_COLOR);
                boolean world = ((EnvironmentalMachineBlock) entity.getBlockState().getBlock()).world;
                String footer = entity.mixedSolutions ? screenLabel("locked") : screenLabel("rate",
                        world ? EnvironmentalMachines.TERRAFORMER_DAILY_COST : EnvironmentalMachines.CLENTAMINATOR_DAILY_COST);
                screenText(source, pose, footer, TEXT_RIGHT, FOOTER_Y, RATE_TEXT_WIDTH, true, entity.mixedSolutions ? WARNING_COLOR : LABEL_COLOR);
                pose.popPose();
                return;
            }
        }
    }

    private static String screenLabel(String key, Object... arguments) {
        return Component.translatable("screen.confluence.environmental_machine." + key, arguments).getString();
    }

    // 超长文字在两端停留后往返滚动，裁切使用屏幕局部坐标，不受玩家视角影响。
    private static void screenText(MultiBufferSource source, PoseStack pose, String text, float x, float y,
                                   float maxWidth, boolean rightAligned, int color) {
        Font font = Minecraft.getInstance().font;
        int width = font.width(text);
        if (width == 0) return;
        float left = rightAligned ? x - maxWidth : x;
        float right = left + maxWidth;
        float start = rightAligned ? x - width : x;
        if (width > maxWidth) {
            start = left - textScrollOffset(width - maxWidth, Util.getMillis() / MILLIS_PER_SECOND);
        }
        pose.pushPose();
        pose.translate(0, 0, TEXT_DEPTH);
        Matrix4f matrix = new Matrix4f(pose.last().pose());
        ScreenTextConsumer[] active = {null};
        MultiBufferSource clipped = type -> {
            if (active[0] != null) active[0].flush();
            active[0] = new ScreenTextConsumer(source.getBuffer(type), matrix, left, right);
            return active[0];
        };
        font.drawInBatch(text, start, y, color, false, new Matrix4f(), clipped,
                Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        if (active[0] != null) active[0].flush();
        pose.popPose();
    }

    // 计算超长文本的往返偏移，两端停留时间与移动速度独立配置。
    private static float textScrollOffset(float overflow, double elapsedSeconds) {
        double travel = overflow / TEXT_SCROLL_SPEED;
        double cycle = (travel + TEXT_SCROLL_PAUSE_SECONDS) * 2;
        double time = elapsedSeconds % cycle;
        double offset = time < TEXT_SCROLL_PAUSE_SECONDS ? 0
                : time < TEXT_SCROLL_PAUSE_SECONDS + travel ? (time - TEXT_SCROLL_PAUSE_SECONDS) * TEXT_SCROLL_SPEED
                : time < TEXT_SCROLL_PAUSE_SECONDS * 2 + travel ? overflow
                : (cycle - time) * TEXT_SCROLL_SPEED;
        return (float) offset;
    }

    // 每四个顶点对应一个字形，裁掉越界部分时同步插值 UV，保留边缘的半个字形。
    private static final class ScreenTextConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final Matrix4f matrix;
        private final float left;
        private final float right;
        private final float[][] vertices = new float[GLYPH_VERTEX_COUNT][9];
        private int count;

        private ScreenTextConsumer(VertexConsumer delegate, Matrix4f matrix, float left, float right) {
            this.delegate = delegate;
            this.matrix = matrix;
            this.left = left;
            this.right = right;
        }

        private void emit() {
            float minimum = Float.POSITIVE_INFINITY;
            float maximum = Float.NEGATIVE_INFINITY;
            float uMinimum = 0;
            float uMaximum = 0;
            for (float[] vertex : vertices) {
                if (vertex[0] < minimum) {
                    minimum = vertex[0];
                    uMinimum = vertex[3];
                }
                if (vertex[0] > maximum) {
                    maximum = vertex[0];
                    uMaximum = vertex[3];
                }
            }
            if (maximum > left && minimum < right && maximum > minimum) {
                for (float[] vertex : vertices) {
                    float x = Mth.clamp(vertex[0], left, right);
                    float u = uMinimum + (x - minimum) / (maximum - minimum) * (uMaximum - uMinimum);
                    delegate.vertex(matrix, x, vertex[1], vertex[2]).color((int) vertex[5], (int) vertex[6], (int) vertex[7], (int) vertex[8])
                            .uv(u, vertex[4]).uv2(LightTexture.FULL_BRIGHT).endVertex();
                }
            }
            count = 0;
        }

        private void flush() {}

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            vertices[count][0] = (float) x;
            vertices[count][1] = (float) y;
            vertices[count][2] = (float) z;
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            vertices[count][5] = red;
            vertices[count][6] = green;
            vertices[count][7] = blue;
            vertices[count][8] = alpha;
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            vertices[count][3] = u;
            vertices[count][4] = v;
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {return this;}

        @Override
        public VertexConsumer uv2(int u, int v) {return this;}

        @Override
        public VertexConsumer normal(float x, float y, float z) {return this;}

        @Override
        public void endVertex() {if (++count == GLYPH_VERTEX_COUNT) emit();}

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {color(red, green, blue, alpha);}

        @Override
        public void unsetDefaultColor() {}

    }

    // 包含父骨骼变换，避免模型将来调整主骨骼时屏幕内容脱离模型。
    private static void applyBonePose(PoseStack pose, GeoBone bone) {
        if (bone.getParent() != null) applyBonePose(pose, bone.getParent());
        RenderUtils.prepMatrixForBone(pose, bone);
    }

    // 底色、轨道、填充和文字沿面法线分层，避免批次顺序导致互相遮挡。
    private static void screenQuad(MultiBufferSource source, Matrix4f matrix, float left, float top, float right, float bottom, float depth, int color) {
        VertexConsumer buffer = source.getBuffer(ModRenderTypes.MACHINE_SCREEN);
        buffer.vertex(matrix, left, top, depth).color(color).endVertex();
        buffer.vertex(matrix, right, top, depth).color(color).endVertex();
        buffer.vertex(matrix, right, bottom, depth).color(color).endVertex();
        buffer.vertex(matrix, left, bottom, depth).color(color).endVertex();
    }

    // 物品形态也使用负尺寸剔除；空机器不绘制储液罐中的液体。
    public static IClientItemExtensions itemRenderer(boolean world) {
        return new IClientItemExtensions() {
            private GeoItemRenderer<EnvironmentalMachineBlock.BItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null)
                    renderer = new GeoItemRenderer<>(new GeoModel<EnvironmentalMachineBlock.BItem>() {
                        @Override
                        public ResourceLocation getModelResource(EnvironmentalMachineBlock.BItem item) {return MODEL;}

                        @Override
                        public ResourceLocation getTextureResource(EnvironmentalMachineBlock.BItem item) {return texture(world);}

                        @Override
                        public ResourceLocation getAnimationResource(EnvironmentalMachineBlock.BItem item) {return ANIMATION;}
                    }) {
                        @Override
                        public RenderType getRenderType(EnvironmentalMachineBlock.BItem item, ResourceLocation texture, MultiBufferSource source, float partialTick) {
                            return RenderType.entityCutout(texture);
                        }

                        @Override
                        public void renderRecursively(PoseStack pose, EnvironmentalMachineBlock.BItem item, GeoBone bone,
                                                      RenderType type, MultiBufferSource source, VertexConsumer buffer, boolean rerender,
                                                      float partialTick, int light, int overlay, float red, float green, float blue, float alpha) {
                            if (bone.getName().endsWith("solutions") || bone.getName().equals("left_point")
                                    || bone.getName().equals("right_point") || bone.getName().equals("back_point"))
                                return;
                            super.renderRecursively(pose, item, bone, type, source, buffer, rerender, partialTick, light, overlay, red, green, blue, alpha);
                        }
                    };
                return renderer;
            }
        };
    }
}
