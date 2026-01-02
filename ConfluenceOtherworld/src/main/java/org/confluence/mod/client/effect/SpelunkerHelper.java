package org.confluence.mod.client.effect;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.ChestBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.terraentity.client.buffer.AbstractBufferManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.confluence.mod.client.ModKeyBindings.SHOW_DETAIL_SPECULAR;
import static org.confluence.mod.common.init.block.FunctionalBlocks.*;
import static org.confluence.mod.common.init.block.NatureBlocks.LIFE_CRYSTAL_BLOCK;
import static org.confluence.mod.common.init.block.OreBlocks.*;
import static org.confluence.terraentity.client.util.ShaderUtil.renderDebugBlock;


/**
 * 实际上是渲染方块边框的类
 */
public class SpelunkerHelper extends AbstractBufferManager {
    /**
     * 调参表
     **/
    public int range = 30;//球形侦测范围
    public int textRange = 30;//球形显示文本范围
    public float maxAlpha = 0.8f;//边框最大alpha(0 - 1)
    public int textRenderType = 0;//0表示文字面向玩家,默认是摄像机方向
    public int centerInternal = 50;//中心块间距的平方

    private final Map<Block, Entry> targets = new HashMap<>();
    public Map<BlockPos, BlockPos> centerCache = new HashMap<>();
    public Map<BlockPos, Block> centerCacheFrame = new HashMap<>();//当前帧渲染的cache
    public Map<Block, ArrayList<BlockPos>> centers = new LinkedHashMap<>();
    public Map<Block, List<BlockPos>> blockMap = new HashMap<>();

    /**
     * 重载资源包
     */
//    public void reloadSpecular() {
//        this.targets.clear();
//        LocalPlayer player = Minecraft.getInstance().player;
//        if (player == null) return;
//        Minecraft.getInstance().getResourceManager().getResource(Confluence.asResource("spelunker/config.json")).ifPresentOrElse(r -> {
//            try {
//                var reader = r.openAsReader();
//                JsonObject jsonobject = GsonHelper.parse(reader);
////                System.out.println(jsonobject);
//                while (lock) {
//                    Thread.onSpinWait();
//                }
//                this.targets.putAll(Entry.BLOCK_MAP_CODEC.codec().parse(JsonOps.INSTANCE, jsonobject).getOrThrow());
//                player.sendSystemMessage(Component.literal("successfully load spelunker config"));
//            } catch (Exception e) {
//                defaultBlocks();
//                player.sendSystemMessage(Component.literal("failed to load spelunker config"));
//
//            }
//        }, () -> {
//            this.defaultBlocks();
//            player.sendSystemMessage(Component.literal("failed to load spelunker config"));
//        });
//    }

    private enum ShowType implements StringRepresentable {
        SPELUNKER,
        DANGER;

        private static final Codec<ShowType> CODEC = StringRepresentable.fromEnum(ShowType::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    private record Entry(int color, boolean showText, ShowType showType) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
                Codec.INT.fieldOf("color").forGetter(Entry::color),
                Codec.BOOL.fieldOf("showText").forGetter(Entry::showText),
                ShowType.CODEC.fieldOf("showType").forGetter(Entry::showType)
        ).apply(builder, Entry::new));

        private static final MapCodec<Map<Block, Entry>> BLOCK_MAP_CODEC = Codec.unboundedMap(BuiltInRegistries.BLOCK.byNameCodec(), Entry.CODEC).fieldOf("targets").fieldOf("values");
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
        putTarget(Blocks.ANCIENT_DEBRIS, 0x5f2000, true, ShowType.SPELUNKER);//这个还必须放这个位置

        //钻石矿
        putTarget(Blocks.DIAMOND_ORE, 0xbdfeff, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_DIAMOND_ORE, 0xbdfeff, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_DIAMOND_ORE.get(), 0xbdfeff, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_DIAMOND_ORE.get(), 0xbdfeff, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_DIAMOND_ORE.get(), 0xbdfeff, true, ShowType.SPELUNKER);


        //红玉矿
        putTarget(RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER);

        //琥珀矿
        putTarget(AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER);
//            putTarget(DEEPSLATE_AMBER_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);

        //黄玉矿
        putTarget(TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER);

        //翡翠矿
        putTarget(OreBlocks.JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.DEEPSLATE_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.CORRUPTION_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.SANCTIFICATION_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.FLESHIFICATION_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER);


        //蓝玉矿
        putTarget(SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER);

        //紫晶矿
        putTarget(OreBlocks.AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.DEEPSLATE_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.CORRUPTION_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.SANCTIFICATION_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER);
        putTarget(OreBlocks.FLESHIFICATION_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER);


        //绿宝石矿
        putTarget(Blocks.EMERALD_ORE, 0xa3ff75, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_EMERALD_ORE, 0xa3ff75, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_EMERALD_ORE.get(), 0xa3ff75, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_EMERALD_ORE.get(), 0xa3ff75, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_EMERALD_ORE.get(), 0xa3ff75, true, ShowType.SPELUNKER);


        //铁矿
        putTarget(Blocks.IRON_ORE, 0xbfae8f, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_IRON_ORE, 0xbfae8f, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_IRON_ORE.get(), 0xbfae8f, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_IRON_ORE.get(), 0xbfae8f, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_IRON_ORE.get(), 0xbfae8f, true, ShowType.SPELUNKER);


        //金矿
        putTarget(Blocks.GOLD_ORE, 0xccbe20, true, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_GOLD_ORE, 0xccbe20, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_GOLD_ORE.get(), 0xccbe20, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_GOLD_ORE.get(), 0xccbe20, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_GOLD_ORE.get(), 0xccbe20, true, ShowType.SPELUNKER);
        putTarget(Blocks.GILDED_BLACKSTONE, 0xccbe20, true, ShowType.SPELUNKER);
        putTarget(Blocks.NETHER_GOLD_ORE, 0xccbe20, true, ShowType.SPELUNKER);


        //煤矿
        putTarget(Blocks.COAL_ORE, 0x555555, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_COAL_ORE, 0x555555, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_COAL_ORE.get(), 0x555555, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_COAL_ORE.get(), 0x555555, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_COAL_ORE.get(), 0x555555, true, ShowType.SPELUNKER);

        //铜矿
        putTarget(Blocks.COPPER_ORE, 0x97502d, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_COPPER_ORE, 0x97502d, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_COPPER_ORE.get(), 0x97502d, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_COPPER_ORE.get(), 0x97502d, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_COPPER_ORE.get(), 0x97502d, true, ShowType.SPELUNKER);

        //锡矿
        putTarget(TIN_ORE.get(), 0x96926e, false, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TIN_ORE.get(), 0x96926e, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_TIN_ORE.get(), 0x96926e, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_TIN_ORE.get(), 0x96926e, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_TIN_ORE.get(), 0x96926e, true, ShowType.SPELUNKER);

        //铅矿
        putTarget(LEAD_ORE.get(), 0x304963, false, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_LEAD_ORE.get(), 0x304963, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_LEAD_ORE.get(), 0x304963, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_LEAD_ORE.get(), 0x304963, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_LEAD_ORE.get(), 0x304963, true, ShowType.SPELUNKER);

        // 银矿
        putTarget(SILVER_ORE.get(), 0x6a737c, false, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_SILVER_ORE.get(), 0x6a737c, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_SILVER_ORE.get(), 0x6a737c, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_SILVER_ORE.get(), 0x6a737c, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_SILVER_ORE.get(), 0x6a737c, true, ShowType.SPELUNKER);

        // 钨矿
        putTarget(TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER);

        // 铂金矿
        putTarget(PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER);
        putTarget(CORRUPTION_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER);

        // 生命水晶
        putTarget(LIFE_CRYSTAL_BLOCK.get(), 0xec173e, true, ShowType.SPELUNKER);
        // 箱子
        for (DeferredBlock<BaseChestBlock> normalChest : ChestBlocks.NORMAL_CHESTS) {
            putTarget(normalChest.get(), 0xe8c314, true, ShowType.SPELUNKER);
        }
        putTarget(Blocks.CHEST, 0xe8c314, true, ShowType.SPELUNKER);


        // 青金石
        putTarget(Blocks.LAPIS_ORE, 0x687bff, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_LAPIS_ORE, 0x687bff, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_LAPIS_ORE.get(), 0x687bff, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_LAPIS_ORE.get(), 0x687bff, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_LAPIS_ORE.get(), 0x687bff, true, ShowType.SPELUNKER);

        // 红石
        putTarget(Blocks.REDSTONE_ORE, 0x7d0000, false, ShowType.SPELUNKER);
        putTarget(Blocks.DEEPSLATE_REDSTONE_ORE, 0x7d0000, false, ShowType.SPELUNKER);
        putTarget(CORRUPTION_REDSTONE_ORE.get(), 0x7d0000, true, ShowType.SPELUNKER);
        putTarget(SANCTIFICATION_REDSTONE_ORE.get(), 0x7d0000, true, ShowType.SPELUNKER);
        putTarget(FLESHIFICATION_REDSTONE_ORE.get(), 0x7d0000, true, ShowType.SPELUNKER);

        // 化石对标
        putTarget(COLD_CRYSTAL_ORE.get(), 0x3db7b0, true, ShowType.SPELUNKER);
        putTarget(GELSTONE_ORE.get(), 0x62b73d, true, ShowType.SPELUNKER);
        putTarget(OPAL_ORE.get(), 0x4bbcff, true, ShowType.SPELUNKER);

        // 狱石
        putTarget(HELLSTONE.get(), 0xea650e, true, ShowType.SPELUNKER);
        putTarget(ASH_HELLSTONE.get(), 0xea650e, true, ShowType.SPELUNKER);

        // 石英
        putTarget(Blocks.NETHER_QUARTZ_ORE, 0xe2ccbc, true, ShowType.SPELUNKER);

        // 新三矿 todo仅敲除祭坛后可探测
        putTarget(DEEPSLATE_COBALT_ORE.get(), 0x0060e9, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_PALLADIUM_ORE.get(), 0xe97500, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_MYTHRIL_ORE.get(), 0x00e9ae, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_ORICHALCUM_ORE.get(), 0xe300e9, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_ADAMANTITE_ORE.get(), 0xe90056, true, ShowType.SPELUNKER);
        putTarget(DEEPSLATE_TITANIUM_ORE.get(), 0xf5dcff, true, ShowType.SPELUNKER);
        putTarget(CHLOROPHYTE_ORE.get(), 0x00a41b, true, ShowType.SPELUNKER);

        // tip 危险感知
        putTarget(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.STONE_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(STONE_PRESSURE_PLATE.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(INSTANTANEOUS_EXPLOSION_TNT.get(), 0xff4600, true, ShowType.DANGER);
//            putTarget(BoulderBlock.Variant.NORMAL.get(), 0xff4600,true, ShowType.DANGER);
        putTarget(SWITCH.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(DART_TRAP.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(STONE_DART_TRAP.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(DEEPSLATE_DART_TRAP.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(DEEPSLATE_PRESSURE_PLATE.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.TNT, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.TRIPWIRE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.SCULK_SHRIEKER, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.SCULK_SENSOR, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.DETECTOR_RAIL, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.ACTIVATOR_RAIL, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.ACACIA_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.BAMBOO_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.BIRCH_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.CRIMSON_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.CHERRY_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.WARPED_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.SPRUCE_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.DARK_OAK_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.OAK_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.JUNGLE_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.MANGROVE_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, 0xff4600, true, ShowType.DANGER);
        putTarget(PLAYER_PRESSURE_PLATE.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(NORMAL_BOULDER.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(OAK_LOG_BOULDER.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(FOLLOWER_BOULDER.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(EXPLODE_BOULDER.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(ROLLING_CACTUS_BOULDER.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(MECHANICAL_FRAGILE_SANDSTONE.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(SCULK_TRAP.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(SHIMMER_TRAP.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(GRAVITATION_TRAP.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(PNEUMATIC_TRAP.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(SPIKE.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(ChestBlocks.DEATH_GOLDEN_CHEST.get(), 0xff4600, true, ShowType.DANGER);
        putTarget(ChestBlocks.DEATH_WOODEN_CHEST.get(), 0xff4600, true, ShowType.DANGER);
    }

    @Deprecated
    private void putTarget(Block block, Color color, boolean always, ShowType showType) {
        putTarget(block, color.getRGB(), always, showType);
    }

    private void putTarget(Block block, int rgb, boolean always, ShowType showType) {
        targets.put(block, new Entry(rgb, always, showType));
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
        if (player == null) return;
        Level level = player.level();
        BlockPos center = player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = -range; i <= range; i++) {
            pos.setX(center.getX() + i);
            for (int j = -range; j < range; j++) {
                pos.setY(center.getY() + j);
                for (int k = -range; k < range; k++) {
                    pos.setZ(center.getZ() + k);
                    BlockState blockState = level.getBlockState(pos);
                    if (blockState.isAir()) continue;

                    Block block = blockState.getBlock();
                    if (targets.containsKey(block) &&  /*&&//有目标且
                            (!centerCache.containsKey(pos) ||//未已缓存或
                                    centerCache.containsKey(pos) && player.level().getBlockState(pos).is(Blocks.AIR))*/
                            (targets.get(block).showType == ShowType.SPELUNKER && player.hasEffect(ModEffects.SPELUNKER) ||
                                    targets.get(block).showType == ShowType.DANGER && player.hasEffect(ModEffects.DANGER_SENSE)) ||
                            blockState.is(Tags.Blocks.ORES) && player.hasEffect(ModEffects.SPELUNKER) // 显示所有带矿物标签的方块
                    ) {//已缓存但为空
                        blockMap.computeIfAbsent(block, k1 -> new ArrayList<>()).add(pos.immutable());
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
        if (player == null)
            return;
        if (--buildCount <= 0) {
            // 提高周围矿物刷新间隔
            buildCount = 10;
            refreshBlocks();
        }


        for (var n : blockMap.entrySet()) {//每种矿石
            //初始化键
            centers.put(n.getKey(), new ArrayList<>());

            Entry target = blockGen.targets.get(n.getKey());
            int rgb;
            if (target != null) {
                rgb = target.color();
            } else {
                rgb = n.getKey().defaultBlockState().getMapColor(player.level(), n.getValue().getFirst()).calculateRGBColor(MapColor.Brightness.HIGH);
            }
            if (n.getValue() == null) return;
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
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

                if (SHOW_DETAIL_SPECULAR.get().isDown()) {
                    Block self = player.level().getBlockState(blockProps).getBlock();
                    boolean up = !(player.level().getBlockState(blockProps.above()).getBlock() == self);
                    boolean down = !(player.level().getBlockState(blockProps.below()).getBlock() == self);
                    boolean north = !(player.level().getBlockState(blockProps.north()).getBlock() == self);
                    boolean south = !(player.level().getBlockState(blockProps.south()).getBlock() == self);
                    boolean east = !(player.level().getBlockState(blockProps.east()).getBlock() == self);
                    boolean west = !(player.level().getBlockState(blockProps.west()).getBlock() == self);
                    if (up || down || north || south || east || west)
                        renderDebugBlock(buffer, blockProps, size, r, g, b, a, up, down, north, south, east, west);
                } else renderDebugBlock(buffer, blockProps, size, r, g, b, a);
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
    protected void afterRender(PoseStack poseStack) {}

    public static void renderLevel(RenderLevelStageEvent event, LocalPlayer player) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        SpelunkerHelper blockGen = SpelunkerHelper.getSingleton();
        //效果消失，清除缓存
        if (!player.hasEffect(ModEffects.SPELUNKER) &&
                !player.hasEffect(ModEffects.DANGER_SENSE)
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

    private void reverseBobView(PoseStack poseStack, float partialTicks) {
        if (Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            float f = player.walkDist - player.walkDistO;
            float f1 = -(player.walkDist + f * partialTicks);
            float f2 = Mth.lerp(partialTicks, player.oBob, player.bob);

            // 注意这里的符号和顺序都与原版相反
            poseStack.mulPose(Axis.XP.rotationDegrees(-Math.abs(Mth.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Mth.sin(f1 * (float) Math.PI) * f2 * 3.0F));
            poseStack.translate(-Mth.sin(f1 * (float) Math.PI) * f2 * 0.5F, Math.abs(Mth.cos(f1 * (float) Math.PI) * f2), 0.0F);
        }
    }
}
