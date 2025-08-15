package org.confluence.mod.client.effect;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.terraentity.client.buffer.AbstractBufferManager;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.confluence.mod.client.ModKeyBindings.SHOW_DETAIL_SPECULAR;
import static org.confluence.mod.common.init.block.FunctionalBlocks.*;
import static org.confluence.mod.common.init.block.NatureBlocks.LIFE_CRYSTAL_BLOCK;
import static org.confluence.mod.common.init.block.OreBlocks.*;
import static org.confluence.terraentity.client.util.ShaderUtil.renderDebugBlock;


/**
 * 实际上是渲染方块边框的类
 */
@OnlyIn(Dist.CLIENT)
public class SpelunkerHelper extends AbstractBufferManager {
    /**
     * 调参表
     **/
    public int range = 30;//球形侦测范围
    public int textRange = 30;//球形显示文本范围
    public float maxAlpha = 0.8f;//边框最大alpha(0 - 1)
    public int textRenderType = 0;//0表示文字面向玩家,默认是摄像机方向
    public int centerInternal = 50;//中心块间距的平方

    private final Map<Block, Tuple> targets = new HashMap<>();
    public Map<BlockPos, BlockPos> centerCache = new HashMap<>();
    public Map<BlockPos, Block> centerCacheFrame = new HashMap<>();//当前帧渲染的cache
    public Map<Block, ArrayList<BlockPos>> centers = new LinkedHashMap<>();
    public Map<Block, List<BlockPos>> blockMap = new HashMap<>();

    /**
     * 重载资源包
     */
    public void reloadSpecular() {
        this.targets.clear();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Minecraft.getInstance().getResourceManager().getResource(Confluence.asResource("spelunker/config.json")).ifPresentOrElse(r -> {
            try {
                var reader = r.openAsReader();
                JsonObject jsonobject = GsonHelper.parse(reader);
//                System.out.println(jsonobject);
                while (lock) {
                    Thread.onSpinWait();
                }
                this.targets.putAll(Tuple.BLOCK_MAP_CODEC.codec().parse(JsonOps.INSTANCE, jsonobject).getOrThrow());
                player.sendSystemMessage(Component.literal("successfully load spelunker config"));
            } catch (Exception e) {
                defaultBlocks();
                player.sendSystemMessage(Component.literal("failed to load spelunker config"));

            }
        }, ()->{
            this.defaultBlocks();
            player.sendSystemMessage(Component.literal("failed to load spelunker config"));
        });
    }

    enum ShowType {SPELUNKER, DANGER}

    private record Tuple(Color color, Boolean showText, ShowType showType) {
        public static final Codec<Tuple> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
                Codec.INT.fieldOf("color").forGetter(t -> t.color.getRGB()),
                Codec.BOOL.fieldOf("showText").forGetter(t -> t.showText),
                Codec.INT.fieldOf("showType").forGetter(t -> t.showType.ordinal())
        ).apply(builder, (color, showText, showType)->new Tuple(new Color(color), showText, ShowType.values()[showType])));

        public static final MapCodec<Map<Block, Tuple>> BLOCK_MAP_CODEC =
                Codec.unboundedMap(ResourceLocation.CODEC.xmap(BuiltInRegistries.BLOCK::get,BuiltInRegistries.BLOCK::getKey), Tuple.CODEC).fieldOf("targets").fieldOf("values");

    }
    protected boolean shouldRefresh() {
        return System.currentTimeMillis() - lastRefreshTime > 100;
    }
    private static SpelunkerHelper blockGen;
    public static volatile boolean lock = true;

    public static SpelunkerHelper getSingleton() {
        if (blockGen == null) {
            blockGen = new SpelunkerHelper(100);

        }
        return blockGen;
    }

    public SpelunkerHelper(int refreshTime) {
        super(refreshTime);
        refreshBlocks();

        this.defaultBlocks();
    }

    /**
     * 默认的配置
     */
    public void defaultBlocks() {

        //远古残骸
        putTarget(Blocks.ANCIENT_DEBRIS, Color.MAGENTA, true, ShowType.SPELUNKER);//这个还必须放这个位置

        //钻石矿
        putTarget(Blocks.DIAMOND_ORE, Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_DIAMOND_ORE, Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_DIAMOND_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_DIAMOND_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_DIAMOND_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);


        //红玉矿
        putTarget(RUBY_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_RUBY_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_RUBY_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_RUBY_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_RUBY_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);

        //琥珀矿
        putTarget(AMBER_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_AMBER_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_AMBER_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_AMBER_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
//            putTarget(DEEPSLATE_AMBER_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);

        //黄玉矿
        putTarget(TOPAZ_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TOPAZ_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_TOPAZ_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_TOPAZ_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_TOPAZ_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);

        //翡翠矿
        putTarget(OreBlocks.JADE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.DEEPSLATE_JADE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.CORRUPTION_JADE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.SANCTIFICATION_JADE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.FLESHIFICATION_JADE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);


        //蓝玉矿
        putTarget(SAPPHIRE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_SAPPHIRE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_SAPPHIRE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_SAPPHIRE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_SAPPHIRE_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);

        //紫晶矿
        putTarget(OreBlocks.AMETHYST_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.DEEPSLATE_AMETHYST_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.CORRUPTION_AMETHYST_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.SANCTIFICATION_AMETHYST_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.FLESHIFICATION_AMETHYST_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);


        //绿宝石矿
        putTarget(Blocks.EMERALD_ORE, Color.green, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_EMERALD_ORE, Color.green, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_EMERALD_ORE.get(), Color.green, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_EMERALD_ORE.get(), Color.green, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_EMERALD_ORE.get(), Color.green, true, ShowType.SPELUNKER);


        //铁矿
        putTarget(Blocks.IRON_ORE, Color.PINK, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_IRON_ORE, Color.PINK, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_IRON_ORE.get(), Color.PINK, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_IRON_ORE.get(), Color.PINK, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_IRON_ORE.get(), Color.PINK, true, ShowType.SPELUNKER);


        //金矿
        putTarget(Blocks.GOLD_ORE, Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_GOLD_ORE, Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_GOLD_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_GOLD_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_GOLD_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(Blocks.GILDED_BLACKSTONE, Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(Blocks.NETHER_GOLD_ORE, Color.ORANGE, true, ShowType.SPELUNKER);


        //煤矿
        putTarget(Blocks.COAL_ORE, Color.BLACK, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_COAL_ORE, Color.BLACK, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_COAL_ORE.get(), Color.BLACK, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_COAL_ORE.get(), Color.BLACK, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_COAL_ORE.get(), Color.BLACK, true, ShowType.SPELUNKER);

        //铜矿
        putTarget(Blocks.COPPER_ORE, Color.LIGHT_GRAY, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_COPPER_ORE, Color.LIGHT_GRAY, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_COPPER_ORE.get(), Color.LIGHT_GRAY, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_COPPER_ORE.get(), Color.LIGHT_GRAY, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_COPPER_ORE.get(), Color.LIGHT_GRAY, true, ShowType.SPELUNKER);

        //锡矿
        putTarget(TIN_ORE.get(), Color.LIGHT_GRAY, false, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TIN_ORE.get(), Color.LIGHT_GRAY, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_TIN_ORE.get(), Color.LIGHT_GRAY, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_TIN_ORE.get(), Color.LIGHT_GRAY, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_TIN_ORE.get(), Color.LIGHT_GRAY, true, ShowType.SPELUNKER);

        //铅矿
        putTarget(LEAD_ORE.get(), Color.PINK, false, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_LEAD_ORE.get(), Color.PINK, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_LEAD_ORE.get(), Color.PINK, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_LEAD_ORE.get(), Color.PINK, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_LEAD_ORE.get(), Color.PINK, true, ShowType.SPELUNKER);

        // 银矿
        putTarget(SILVER_ORE.get(), Color.WHITE, false, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_SILVER_ORE.get(), Color.WHITE, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_SILVER_ORE.get(), Color.WHITE, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_SILVER_ORE.get(), Color.WHITE, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_SILVER_ORE.get(), Color.WHITE, true, ShowType.SPELUNKER);

        //绿宝石矿
        putTarget(TUNGSTEN_ORE.get(), Color.green, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TUNGSTEN_ORE.get(), Color.green, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_TUNGSTEN_ORE.get(), Color.green, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_TUNGSTEN_ORE.get(), Color.green, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_TUNGSTEN_ORE.get(), Color.green, true, ShowType.SPELUNKER);

        // 铂金矿
        putTarget(PLATINUM_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_PLATINUM_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_PLATINUM_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_PLATINUM_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_PLATINUM_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);

        // 生命水晶
        putTarget(LIFE_CRYSTAL_BLOCK.get(), Color.RED, true, ShowType.SPELUNKER);
        // 箱子
        for (DeferredBlock<BaseChestBlock> normalChest : ChestBlocks.NORMAL_CHESTS) {
            putTarget(normalChest.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        }
        putTarget(Blocks.CHEST, Color.ORANGE, true, ShowType.SPELUNKER);


        // 青金石
        putTarget(Blocks.LAPIS_ORE, Color.blue, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_LAPIS_ORE, Color.blue, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_LAPIS_ORE.get(), Color.blue, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_LAPIS_ORE.get(), Color.blue, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_LAPIS_ORE.get(), Color.blue, true, ShowType.SPELUNKER);

        // 红石
        putTarget(Blocks.REDSTONE_ORE, Color.red, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_REDSTONE_ORE, Color.red, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_REDSTONE_ORE.get(), Color.red, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_REDSTONE_ORE.get(), Color.red, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_REDSTONE_ORE.get(), Color.red, true, ShowType.SPELUNKER);

        // 化石对标
        putTarget(COLD_CRYSTAL_ORE.get(), Color.red, true, ShowType.SPELUNKER);
        putTarget(GELSTONE_ORE.get(), Color.red, true, ShowType.SPELUNKER);
        putTarget(OPAL_ORE.get(), Color.red, true, ShowType.SPELUNKER);

        // 狱石
        putTarget(HELLSTONE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(ASH_HELLSTONE.get(), Color.ORANGE, true, ShowType.SPELUNKER);

        // 石英
        putTarget(Blocks.NETHER_QUARTZ_ORE, Color.WHITE, true, ShowType.SPELUNKER);

        // 新三矿 todo仅敲除祭坛后可探测
        putTarget(DEEPSLATE_COBALT_ORE.get(), Color.BLUE, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_PALLADIUM_ORE.get(), Color.ORANGE, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_MYTHRIL_ORE.get(), Color.CYAN, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_ORICHALCUM_ORE.get(), Color.MAGENTA, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_ADAMANTITE_ORE.get(), Color.PINK, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TITANIUM_ORE.get(), Color.LIGHT_GRAY, true, ShowType.SPELUNKER);
        putTarget(CHLOROPHYTE_ORE.get(), Color.GREEN, true, ShowType.SPELUNKER);

        // tip 危险感知
        putTarget(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.STONE_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(STONE_PRESSURE_PLATE.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(INSTANTANEOUS_EXPLOSION_TNT.get(), Color.MAGENTA, true, ShowType.DANGER);
//            putTarget(BoulderBlock.Variant.NORMAL.get(), Color.MAGENTA,true, ShowType.DANGER);
        putTarget(SWITCH.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(DART_TRAP.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(DEEPSLATE_PRESSURE_PLATE.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.TNT, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.TRIPWIRE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.SCULK_SHRIEKER, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.SCULK_SENSOR, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.DETECTOR_RAIL, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.ACTIVATOR_RAIL, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.ACACIA_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.BAMBOO_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.BIRCH_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.CRIMSON_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.CHERRY_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.WARPED_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.SPRUCE_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.DARK_OAK_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.OAK_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.JUNGLE_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.MANGROVE_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, Color.MAGENTA, true, ShowType.DANGER);
        putTarget(PLAYER_PRESSURE_PLATE.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(NORMAL_BOULDER.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(OAK_LOG_BOULDER.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(FOLLOWER_BOULDER.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(EXPLODE_BOULDER.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(ROLLING_CACTUS_BOULDER.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(MECHANICAL_FRAGILE_SANDSTONE.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(SCULK_TRAP.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(SHIMMER_TRAP.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(GRAVITATION_TRAP.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(PNEUMATIC_TRAP.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(SPIKE.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(ChestBlocks.DEATH_GOLDEN_CHEST.get(), Color.MAGENTA, true, ShowType.DANGER);
        putTarget(ChestBlocks.DEATH_WOODEN_CHEST.get(), Color.MAGENTA, true, ShowType.DANGER);
    }


    private void putTarget(Block block, Color color, Boolean always, ShowType showType) {
        targets.put(block, new Tuple(color, always, showType));
    }


    /**
     * 刷新周围的矿
     */
    private void refreshBlocks() {
        for (var n : blockMap.entrySet()) {
            n.getValue().clear();
        }
        blockMap.clear();

        Player player = Minecraft.getInstance().player;
        if (player != null){
            Level level = player.level();
            BlockPos center = player.blockPosition();
            for (int i = -range; i <= range; i++) {
                for (int j = -range; j < range; j++) {
                    for (int k = -range; k < range; k++) {
                        BlockPos pos = center.offset(i, j, k);
                        Block block = level.getBlockState(pos).getBlock();
                        if ((targets.containsKey(block) /*&&//有目标且
                            (!centerCache.containsKey(pos) ||//未已缓存或
                                    centerCache.containsKey(pos) && player.level().getBlockState(pos).is(Blocks.AIR))*/
                                && (targets.get(block).showType == ShowType.SPELUNKER && player.hasEffect(ModEffects.SPELUNKER) ||
                                targets.get(block).showType == ShowType.DANGER && player.hasEffect(ModEffects.DANGER_SENSE)) ||
                                level.getBlockState(pos).is(Tags.Blocks.ORES) && player.hasEffect(ModEffects.SPELUNKER) // 显示所有带矿物标签的方块
                        )) {//已缓存但为空

                            var list = blockMap.computeIfAbsent(block, k1 -> new ArrayList<>());
                            list.add(pos);
                        }
                    }
                }
            }
        }
    }

    int buildCount = 10;

    @Override
    protected void buildBuffer(BufferBuilder buffer) {
        //重新加载缓存，提速
        centers.clear();
        centerCacheFrame.clear();
        Player player = Minecraft.getInstance().player;
        if(player == null)
            return;
        if(--buildCount <= 0){
            // 提高周围矿物刷新间隔
            buildCount = 10;
            refreshBlocks();
        }


        for (var n : blockMap.entrySet()) {//每种矿石
            //初始化键
            centers.put(n.getKey(), new ArrayList<>());

            Tuple target = blockGen.targets.get(n.getKey());
            Color color;
            if (target != null){
                color = target.color();
            }else {
                color = new Color(n.getKey().defaultBlockState().getMapColor(player.level(),n.getValue().getFirst()).calculateRGBColor(MapColor.Brightness.HIGH));
            }
            if (n.getValue() == null) return;
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            int a;

            for (BlockPos blockProps : n.getValue()) {//每个矿石
                if (blockProps == null) {
                    return;
                }

                final float size = 1.0f;

                /* 相近同种方块禁止渲染 */
                boolean ifNear = false;
                if (player.level().getBlockState(blockProps).is(Blocks.AIR)) {//已被挖掘，取消缓存

//                        System.out.println("block break");
                    centerCache.remove(blockProps);
                    ArrayList<BlockPos> list = centers.get(n.getKey());
                    if (list != null && !list.isEmpty()) {
                        // 刷新周围中心块
                        // 附近有中心块，清除改块
                        list.removeIf(centerPos -> centerPos.distSqr(blockProps) < 25);
                    }
                }

                if ((target != null && target.showType == ShowType.SPELUNKER) || n.getKey().defaultBlockState().is(Tags.Blocks.ORES)) {//矿透方块
                    if (!player.hasEffect(ModEffects.SPELUNKER)) continue;
                    //todo 可以优化
                    for (BlockPos centerPos : centers.get(n.getKey())) {//否则查找所有的中心块
                        double distance = centerPos.distSqr(blockProps);
                        if (distance < blockGen.centerInternal) {//附近有中心块，添加缓存
                            centerCache.put(blockProps, centerPos);
                            ifNear = true;
                            break;
                        }
                    }
                    if (ifNear) {
                        if (!SHOW_DETAIL_SPECULAR.get().isDown()) continue;//非中心块或tab按下不渲染

                    } else {
                        centers.get(n.getKey()).add(blockProps);//太远则自己成为中心块
                        centerCacheFrame.put(blockProps, n.getKey());//只渲染中心块文本
                    }
                } else if (target != null && target.showType == ShowType.DANGER) {//危险方块
                    if (!player.hasEffect(ModEffects.DANGER_SENSE)) continue;
                    centerCacheFrame.put(blockProps, n.getKey());//渲染所有危险方块
                }


                //距离越远透明度越低
                a = (int) ((255 - Math.min(player.distanceToSqr(blockProps.getX(), blockProps.getY(), blockProps.getZ()) / (blockGen.range * blockGen.range) * 255, 255)) * blockGen.maxAlpha);
                if (a <= 0) continue;

                renderDebugBlock(buffer, blockProps, size, r, g, b, a);

            }
        }
    }

    @Override
    protected void beforeRender() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void afterRender(PoseStack poseStack) {
        Player player = Minecraft.getInstance().player;
        if (player==null) return;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        poseStack.pushPose();
        Vec3 playerPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-playerPos.x(), -playerPos.y(), -playerPos.z());

        var map = blockGen.targets;
        int textRange = blockGen.textRange;
        AtomicInteger count = new AtomicInteger();
        float scale = 20;
        int textDir = blockGen.textRenderType;
        for (var n : centerCacheFrame.entrySet()) {
            var pos = n.getKey();
            var block = n.getValue();
            if (block.asItem() == Blocks.ANCIENT_DEBRIS.asItem()) count.getAndIncrement();
            if (playerPos.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < textRange * textRange) {
                Tuple tuple;
                if (SHOW_DETAIL_SPECULAR.get().isDown() && (tuple = map.get(block)) != null && tuple.showText()) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 0.5;
                    double z = pos.getZ() + 0.5;

                    var dir = playerPos.subtract(x, y, z).scale(-1);
                    var pf = player.getForward();

                    double angle = Math.acos(dir.dot(pf) / dir.length() / pf.length());
                    if (angle < 60 * Math.PI / 180) {
                        poseStack.pushPose();
                        poseStack.translate(x, y, z);//调整到中心位置
                        poseStack.scale(-1 / scale, -1 / scale, -1 / scale);

                        if (textDir == 0) {
                            var fs = VectorUtils.dirToRot(dir.scale(-1), false);
                            Matrix4f m = new Matrix4f().rotate(Axis.YP.rotation(Mth.PI - fs[0])).rotate(Axis.XN.rotation(fs[1]));
                            poseStack.mulPose(m);

                        } else {
                            //Quaternionf quaternionf = new Quaternionf().rotateTo( new Vector3f(0,1,0),dir.toVector3f());
                            Quaternionf q1 = new Quaternionf(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());//旋转到摄像机视角
                            poseStack.mulPose(q1);
                        }

                        Component component = block.asItem().getDefaultInstance().getDisplayName();
                        poseStack.translate(-component.getString().length() / 5.0 * scale, 0.7 * scale, 0);//旋转后偏移


                        Minecraft.getInstance().font.renderText(Component.literal(component.getString()).withStyle(style -> style.withColor(tuple.color().getRGB())).getVisualOrderText(),
                                -5, -5f, tuple.color().getRGB(),
                                false, poseStack.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.SEE_THROUGH, 0, 15 << 20 | 15 << 4);

                        poseStack.popPose();

                    }
                }
            }

        }

        poseStack.popPose();
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }


    public static void renderLevel(RenderLevelStageEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (
//                event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER ||
                        event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES
//                ||event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES
                || player == null
        ) return;
        SpelunkerHelper blockGen = SpelunkerHelper.getSingleton();
        //效果消失，清除缓存
        if (!player.hasEffect(ModEffects.SPELUNKER)
                && !player.hasEffect(ModEffects.DANGER_SENSE)
        ) {
            blockGen.centerCache.clear();
            blockGen.centers.clear();
            blockGen.blockMap.clear();
            blockGen.centerCacheFrame.clear();
            return;
        }
        lock = true;
        blockGen.render(event);
        lock = false;


//         获取json数据  /confluence reload spelunker
//        JsonElement element = Tuple.BLOCK_MAP_CODEC.codec().encodeStart(JsonOps.INSTANCE, blockGen.targets).result().get();
//        System.out.println(element);
    }
}
