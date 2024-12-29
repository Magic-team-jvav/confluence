package org.confluence.mod.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixin.client.accessor.FontAccessor;
import org.confluence.mod.util.ModUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


import static org.confluence.mod.client.ModKeyBindings.*;
import static org.confluence.mod.common.init.block.FunctionalBlocks.*;
import static org.confluence.mod.common.init.block.NatureBlocks.*;
import static org.confluence.mod.common.init.block.OreBlocks.*;


/**
 * 实际上是渲染方块边框的类
 */
@OnlyIn(Dist.CLIENT)
public class SpelunkerHelper {
/** 调参表 **/
    public int refreshTick = 100;//客户端渲染刷新间隔
    public int range = 30;//球形侦测范围
    public int textRange = 30;//球形显示文本范围
    public float maxAlpha = 0.8f;//边框最大alpha(0 - 1)
    public int textRenderType = 0;//0表示文字面向玩家,默认是摄像机方向
    public int centerInternal = 50;//中心块间距的平方


    public static SpelunkerHelper getSingleton(Player player){
        if(blockGen==null || blockGen.player!=player){

            blockGen = new SpelunkerHelper(player);
            return blockGen;
        }
        return blockGen;
    }
    private static SpelunkerHelper blockGen;
    public Map<Block, Tuple> targets = new HashMap<>();
    public Map<BlockPos,BlockPos> centerCache = new HashMap<>();
    public Map<BlockPos,Block> centerCacheFrame = new HashMap<>();//当前帧渲染的cache
    public Map<Block, ArrayList<BlockPos>> centers = new LinkedHashMap<>();
    public Map<Block, List<BlockPos>> blockMap = new HashMap<>();
    private Boolean INIT = false;
    private Player player;
    private Minecraft mc;

    enum ShowType {SPELUNKER, DANGER}

    public SpelunkerHelper(Player player) {
        this.player = player;
        genBlocks();

        if(!INIT){
            mc = Minecraft.getInstance();
            //远古残骸
            putTarget(Blocks.ANCIENT_DEBRIS,Color.MAGENTA,true, ShowType.SPELUNKER);//这个还必须放这个位置
            //钻石矿
            putTarget(Blocks.DIAMOND_ORE,  Color.CYAN,true, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_DIAMOND_ORE, Color.CYAN,true, ShowType.SPELUNKER);


            //红玉矿
            putTarget(RUBY_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_RUBY_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);

            //琥珀矿
            putTarget(AMBER_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);
//            putTarget(DEEPSLATE_AMBER_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);

            //黄玉矿
            putTarget(TOPAZ_ORE.get(),  Color.CYAN,true, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_TOPAZ_ORE.get(),  Color.CYAN,true, ShowType.SPELUNKER);

            //翡翠矿
            putTarget(TR_EMERALD_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_TR_EMERALD_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);

            //蓝玉矿
            putTarget(SAPPHIRE_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_SAPPHIRE_ORE.get(),Color.CYAN,true, ShowType.SPELUNKER);

            //紫晶矿
            putTarget(TR_AMETHYST_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_TR_AMETHYST_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);


            //绿宝石矿
            putTarget(Blocks.EMERALD_ORE,Color.green,true, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_EMERALD_ORE,Color.green,true, ShowType.SPELUNKER);

            //铁矿
            putTarget(Blocks.IRON_ORE, Color.PINK,true, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_IRON_ORE, Color.PINK,true, ShowType.SPELUNKER);

            //金矿
            putTarget(Blocks.GOLD_ORE, Color.ORANGE,true, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_GOLD_ORE, Color.ORANGE,true, ShowType.SPELUNKER);

            //煤矿
            putTarget(Blocks.COAL_ORE, Color.BLACK,false, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_COAL_ORE, Color.BLACK,false, ShowType.SPELUNKER);

            //铜矿
            putTarget(Blocks.COPPER_ORE, Color.LIGHT_GRAY,false, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_COPPER_ORE, Color.LIGHT_GRAY,false, ShowType.SPELUNKER);

            //锡矿
            putTarget(TIN_ORE.get(), Color.LIGHT_GRAY,false, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_TIN_ORE.get(), Color.LIGHT_GRAY,false, ShowType.SPELUNKER);

            //铅矿
            putTarget(LEAD_ORE.get(), Color.PINK,false, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_LEAD_ORE.get(), Color.PINK,false, ShowType.SPELUNKER);

            //铂金矿
            putTarget(PLATINUM_ORE.get(), Color.ORANGE,true, ShowType.SPELUNKER);
            putTarget(DEEPSLATE_PLATINUM_ORE.get(), Color.ORANGE,true, ShowType.SPELUNKER);

            //生命水晶
            putTarget(LIFE_CRYSTAL_BLOCK.get(), Color.RED,true, ShowType.SPELUNKER);
            //箱子
            putTarget(BASE_CHEST_BLOCK.get(), Color.ORANGE,true, ShowType.SPELUNKER);



            //青金石
            putTarget(Blocks.LAPIS_ORE, Color.blue,false, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_LAPIS_ORE, Color.blue,false, ShowType.SPELUNKER);

            //红石
            putTarget(Blocks.REDSTONE_ORE, Color.red,false, ShowType.SPELUNKER);
            putTarget(Blocks.DEEPSLATE_REDSTONE_ORE, Color.red,false, ShowType.SPELUNKER);


            // tip 危险感知
            putTarget(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.STONE_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(STONE_PRESSURE_PLATE.get(), Color.MAGENTA,true, ShowType.DANGER);
            putTarget(INSTANTANEOUS_EXPLOSION_TNT.get(), Color.MAGENTA,true, ShowType.DANGER);
//            putTarget(BoulderBlock.Variant.NORMAL.get(), Color.MAGENTA,true, ShowType.DANGER);
            putTarget(SWITCH.get(), Color.MAGENTA,true, ShowType.DANGER);
            putTarget(DART_TRAP.get(),Color.MAGENTA,true, ShowType.DANGER);
            putTarget(DEEPSLATE_PRESSURE_PLATE.get(),  Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.TNT, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.TRIPWIRE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.SCULK_SHRIEKER, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.SCULK_SENSOR, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.DETECTOR_RAIL, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.ACTIVATOR_RAIL, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.ACACIA_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.BAMBOO_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.BIRCH_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.CRIMSON_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.CHERRY_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.WARPED_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.SPRUCE_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.DARK_OAK_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.OAK_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.JUNGLE_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.MANGROVE_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, Color.MAGENTA,true, ShowType.DANGER);
            putTarget(PLAYER_PRESSURE_PLATE.get(), Color.MAGENTA,true, ShowType.DANGER);




            INIT = true;
        }
    }
    public void putTarget(Block block, Color color, Boolean always, ShowType showType){
        targets.put(block, new Tuple(color,always,showType) );
    }


    public record Tuple(Color color,Boolean showText,ShowType showType) { }


    public void genBlocks() {

        for(var n : blockMap.entrySet()){
            n.getValue().clear();
        }
        blockMap.clear();

        Level level = player.level();
        BlockPos center = player.blockPosition();
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j < range; j++) {
                for (int k = -range; k < range; k++) {
                    BlockPos pos = center.offset(i, j, k);
                    Block block = level.getBlockState(pos).getBlock();
                    if (targets.containsKey(block) /*&&//有目标且
                            (!centerCache.containsKey(pos) ||//未已缓存或
                                    centerCache.containsKey(pos) && player.level().getBlockState(pos).is(Blocks.AIR))*/
                    && (targets.get(block).showType==ShowType.SPELUNKER && mc.player.hasEffect(ModEffects.SPELUNKER) ||
                            targets.get(block).showType==ShowType.DANGER && mc.player.hasEffect(ModEffects.DANGER_SENSE))
                    ) {//已缓存但为空

                        var list = blockMap.computeIfAbsent(block, k1 -> new ArrayList<>());
                        list.add(pos);
                    }
                }
            }
        }
    }

    public Map<Block, List<BlockPos>> getBlockMap() {
        return blockMap;
    }


    static int tick = 20;
    private static VertexBuffer vertexBuffer;
    public static void renderLevel(RenderLevelStageEvent event){

        if(
//                event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER ||
                event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES

        )return;
        Minecraft minecraft = Minecraft.getInstance();
        SpelunkerHelper blockGen= SpelunkerHelper.getSingleton(minecraft.player);
        //效果消失，清除缓存
        if(!Minecraft.getInstance().player.hasEffect(ModEffects.SPELUNKER)
            &&!Minecraft.getInstance().player.hasEffect(ModEffects.DANGER_SENSE)
        ){
            if(blockGen!=null){
                blockGen.centerCache.clear();;
                blockGen.centers.clear();;
                blockGen.blockMap.clear();
                blockGen.centerCacheFrame.clear();
            }
            return;
        }

        boolean ifRefresh = false;

        if(--tick<0){
            tick = blockGen.refreshTick;
            //todo 如果性能消耗太大，应该在破坏方块事件里设定是否刷新
            blockGen.genBlocks();
            ifRefresh = true;
        }

        Vec3 playerPos = minecraft.gameRenderer.getMainCamera().getPosition();

        //重新加载缓存，提速
        Map<Block, ArrayList<BlockPos>> centers = blockGen.centers;
        centers.clear();
        var shouldRenderCache = blockGen.centerCacheFrame;
        if(ifRefresh){
            var mapList = blockGen.getBlockMap();

            vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

            var centerMap = blockGen.centerCache;
            shouldRenderCache.clear();
            for(var n : mapList.entrySet()) {//每种矿石
                //初始化键
                centers.put(n.getKey(),new ArrayList<>());

                Color color = blockGen.targets.get(n.getKey()).color();
                int colorInt = color.getRGB();
                if(n.getValue()==null)return;
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int a = (int) (255*0.2);


                for (BlockPos blockProps : n.getValue()) {//每个矿石
                    if (blockProps == null) {
                        return;
                    }

                    final float size = 1.0f;
                    final int x = blockProps.getX(),
                            y = blockProps.getY(),
                            z = blockProps.getZ();

                    /* 相近同种方块禁止渲染 */
                    boolean ifNear = false;


                    if(Minecraft.getInstance().level.getBlockState(blockProps).is(Blocks.AIR)){//已被挖掘，取消缓存

//                        System.out.println("block break");
                        centerMap.remove(blockProps);
                        for(BlockPos centerPos : centers.get(n.getKey())){//刷新周围中心块
                            double distance = centerPos.distSqr(blockProps);
                            if(distance < 25){//附近有中心块，清除改块
                                centers.get(n.getKey()).remove(centerPos);
                            }
                        }
                    }

                    if(blockGen.targets.get(n.getKey()).showType==ShowType.SPELUNKER){//矿透方块
                        if(!minecraft.player.hasEffect(ModEffects.SPELUNKER))continue;
                        //todo 可以优化
                        for(BlockPos centerPos : centers.get(n.getKey())){//否则查找所有的中心块
                            double distance = centerPos.distSqr(blockProps);
                            if(distance < blockGen.centerInternal){//附近有中心块，添加缓存
                                centerMap.put(blockProps, centerPos);
                                ifNear = true;
                                break;
                            }
                        }

                        if(ifNear){
                            if(!SHOW_DETAIL_SPECULAR.get().isDown())continue;//非中心块或tab按下不渲染

                        }else{
                            centers.get(n.getKey()).add(blockProps);//太远则自己成为中心块
                            shouldRenderCache.put(blockProps,n.getKey());//只渲染中心块文本
                        }
                    }else if(blockGen.targets.get(n.getKey()).showType==ShowType.DANGER){//危险方块
                        if(!minecraft.player.hasEffect(ModEffects.DANGER_SENSE))continue;
                        shouldRenderCache.put(blockProps,n.getKey());//渲染所有危险方块
                    }


                    //距离越远透明度越低
                    a = (int) ((255- Math.min(playerPos.distanceToSqr(blockProps.getX(),blockProps.getY(),blockProps.getZ())/(blockGen.range*blockGen.range)*255,255))* blockGen.maxAlpha);
                    if(a<=0)continue;


                    buffer.addVertex(x, y + size, z).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x, y + size, z).setColor(r,g,b,a);

                    // BOTTaddVertex()
                    buffer.addVertex(x + size, y, z).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x, y, z).setColor(r,g,b,a);
                    buffer.addVertex(x, y, z).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y, z).setColor(r,g,b,a);

                    // EdgeaddVertex()
                    buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);

                    // EdgeaddVertex()
                    buffer.addVertex(x + size, y, z).setColor(r,g,b,a);
                    buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);

                    // EdgeaddVertex()
                    buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
                    buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);

                    // EdgeaddVertex()
                    buffer.addVertex(x, y, z).setColor(r,g,b,a);
                    buffer.addVertex(x, y + size, z).setColor(r,g,b,a);

                }

            }


            var build = buffer.build();
            if (build == null) {
                vertexBuffer = null;
                return;
            } else {
                vertexBuffer.bind();
                vertexBuffer.upload(build);
                VertexBuffer.unbind();
            }
        }


        if (vertexBuffer != null) {

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            PoseStack poseStack = event.getPoseStack();

            poseStack.pushPose();
            poseStack.mulPose(event.getModelViewMatrix());
            poseStack.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());
//            poseStack.mulPose(event.getCamera().rotation());

//            minecraft.getBlockRenderer().renderSingleBlock(minecraft.level.getBlockState(BlockPos.containing(playerPos.subtract(0,-1,0))),poseStack,minecraft.renderBuffers().bufferSource(),15, OverlayTexture.NO_OVERLAY);
            vertexBuffer.bind();
//            vertexBuffer.drawWithShader(poseStack.last().pose(), event.getProjectionMatrix(), RenderSystem.getShader());
            vertexBuffer.drawWithShader(poseStack.last().pose(), event.getProjectionMatrix(), RenderSystem.getShader());


            VertexBuffer.unbind();
            poseStack.popPose();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);

            poseStack.pushPose();

            poseStack.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());

            var map = blockGen.targets;
            int textRange = blockGen.textRange;
            AtomicInteger count = new AtomicInteger();
            float scale = 20;
            int textDir = blockGen.textRenderType;
            for(var n : shouldRenderCache.entrySet()){
                var  pos = n.getKey();
                var block = n.getValue();
                if(block.asItem() == Blocks.ANCIENT_DEBRIS.asItem()) count.getAndIncrement();
                if(playerPos.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())< textRange*textRange){
                    if(SHOW_DETAIL_SPECULAR.get().isDown() && map.get(block).showText()){
                        double x = pos.getX()+0.5;
                        double y = pos.getY()+0.5;
                        double z = pos.getZ()+0.5;

                        var dir = playerPos.subtract(x,y,z).scale(-1);
                        var pf = minecraft.player.getForward();

                        double angle = Math.acos(dir.dot(pf)/dir.length()/pf.length());
                        if(angle<60*Math.PI/180){
                            poseStack.pushPose();
                            poseStack.translate(x,y,z);//调整到中心位置
                            poseStack.scale(-1/scale,-1/scale,-1/scale);

                            if(textDir==0){
                                var fs = ModUtils.dirToRot(dir.scale(-1));
                                Matrix4f m = new Matrix4f().rotate(Axis.YP.rotationDegrees(180-fs[0])).rotate(Axis.XN.rotationDegrees(fs[1]));
                                poseStack.mulPose(m);

                            }else{
                                //Quaternionf quaternionf = new Quaternionf().rotateTo( new Vector3f(0,1,0),dir.toVector3f());
                                Quaternionf q1 = new Quaternionf( event.getCamera().rotation());//旋转到摄像机视角
                                poseStack.mulPose(q1);
                            }

                            Component component = block.asItem().getDefaultInstance().getDisplayName();
                            poseStack.translate(-component.getString().length()/5.0 * scale,0.7 * scale,0);//旋转后偏移


                            ((FontAccessor) (minecraft.font)).callRenderText(Component.literal(component.getString()).withStyle(style -> style.withColor(map.get(block).color().getRGB())).getVisualOrderText(),
                                    -5, -5f, map.get(block).color().getRGB(),
                                    false, poseStack.last().pose(), minecraft.renderBuffers().bufferSource(), Font.DisplayMode.SEE_THROUGH, 0, 15 << 20 | 15 << 4);

                            poseStack.popPose();

                        }
                    }
                }

            }

            poseStack.popPose();




        }



    }

}
