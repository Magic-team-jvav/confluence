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
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import nowebsite.makertechno.the_trackers.api.component.ComponentBuilder;
import nowebsite.makertechno.the_trackers.api.component.StaticComponent;
import nowebsite.makertechno.the_trackers.core.track.TrackersMonitor;
import nowebsite.makertechno.the_trackers.core.track.WorldSingletonTracker;
import org.confluence.mod.Confluence;
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
import static org.confluence.mod.common.init.item.MaterialItems.*;
import static org.confluence.mod.common.init.block.NatureBlocks.LIFE_CRYSTAL_BLOCK;
import static org.confluence.mod.common.init.block.OreBlocks.*;
import static org.confluence.terraentity.client.util.ShaderUtil.renderDebugBlock;


/**
 * 方块探测类，处理矿物探测与危险方块探测
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
    public Map<BlockPos, Block> centerCacheFrame = new HashMap<>(); // 当前帧渲染的cache
    public Map<BlockPos, StaticComponentLifeController> cachedPointers = new HashMap<>(); // 根据当前中心cache缓存的对应指针
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

    private record Entry(int color, boolean showText, ShowType showType, ResourceLocation showItemRL) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
                Codec.INT.fieldOf("color").forGetter(Entry::color),
                Codec.BOOL.fieldOf("showText").forGetter(Entry::showText),
                ShowType.CODEC.fieldOf("showType").forGetter(Entry::showType),
                ResourceLocation.CODEC.fieldOf("showItemRL").forGetter(Entry::showItemRL)
        ).apply(builder, Entry::new));

        private static final MapCodec<Map<Block, Entry>> BLOCK_MAP_CODEC = Codec.unboundedMap(BuiltInRegistries.BLOCK.byNameCodec(), Entry.CODEC).fieldOf("targets").fieldOf("values");
    }

    /**
     * 指针生命周期最简化控制。
     */
    public static final class StaticComponentLifeController {
        public final StaticComponent component;
        public boolean isAlive;
        public StaticComponentLifeController(StaticComponent component) {
            this.component = component;
        }
    }

    protected boolean shouldRefresh() {
        return System.currentTimeMillis() - lastRefreshTime > 100;
    }

    private static SpelunkerHelper blockGen;
    private final WorldSingletonTracker tracker;
    public static volatile boolean lock = true;

    public static final ComponentBuilder COMPONENT_BUILDER = new ComponentBuilder()
            .setComponentType(ComponentBuilder.ComponentType.DIRECT)
            .setAffectedByPlayerSettingsScale(false)
            .defineScaleMultiple(scale -> scale*0.8f);
    public static final ResourceLocation EMPTY = TextureMapping.getItemTexture(Items.AIR);

    public static SpelunkerHelper getSingleton() {
        if (blockGen == null) {
            blockGen = new SpelunkerHelper(100);

        }
        return blockGen;
    }

    public SpelunkerHelper(int refreshTime) {
        super(refreshTime);
        tracker = TrackersMonitor.getTracker();
        refreshBlocks();

        this.defaultBlocks();
    }

    /**
     * 默认的配置
     */
    public void defaultBlocks() {

        //远古残骸
        putTarget(Blocks.ANCIENT_DEBRIS, 0x5f2000, true, ShowType.SPELUNKER, Items.NETHERITE_SCRAP);//这个还必须放这个位置

        //钻石矿
        putTarget(Blocks.DIAMOND_ORE, 0xbdfeff, true, ShowType.SPELUNKER, Items.DIAMOND);
        putTarget(Blocks.DEEPSLATE_DIAMOND_ORE, 0xbdfeff, true, ShowType.SPELUNKER, Items.DIAMOND);
        putTarget(CORRUPTION_DIAMOND_ORE.get(), 0xbdfeff, true, ShowType.SPELUNKER, Items.DIAMOND);
        putTarget(SANCTIFICATION_DIAMOND_ORE.get(), 0xbdfeff, true, ShowType.SPELUNKER, Items.DIAMOND);
        putTarget(FLESHIFICATION_DIAMOND_ORE.get(), 0xbdfeff, true, ShowType.SPELUNKER, Items.DIAMOND);


        //红玉矿
        putMaterialTarget(RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER, RUBY);
        putMaterialTarget(DEEPSLATE_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER, RUBY);
        putMaterialTarget(CORRUPTION_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER, RUBY);
        putMaterialTarget(SANCTIFICATION_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER, RUBY);
        putMaterialTarget(FLESHIFICATION_RUBY_ORE.get(), 0xa80000, true, ShowType.SPELUNKER, RUBY);

        //琥珀矿
        putMaterialTarget(AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER, AMBER);
        putMaterialTarget(CORRUPTION_AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER, AMBER);
        putMaterialTarget(SANCTIFICATION_AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER, AMBER);
        putMaterialTarget(FLESHIFICATION_AMBER_ORE.get(), 0xa85c00, true, ShowType.SPELUNKER, AMBER);
//            putTarget(DEEPSLATE_AMBER_ORE.get(), Color.CYAN,true, ShowType.SPELUNKER);

        //黄玉矿
        putMaterialTarget(TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER, TOPAZ);
        putMaterialTarget(DEEPSLATE_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER, TOPAZ);
        putMaterialTarget(CORRUPTION_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER, TOPAZ);
        putMaterialTarget(SANCTIFICATION_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER, TOPAZ);
        putMaterialTarget(FLESHIFICATION_TOPAZ_ORE.get(), 0xa88300, true, ShowType.SPELUNKER, TOPAZ);

        //翡翠矿
        putMaterialTarget(OreBlocks.JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER, JADE);
        putMaterialTarget(OreBlocks.DEEPSLATE_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER, JADE);
        putMaterialTarget(OreBlocks.CORRUPTION_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER, JADE);
        putMaterialTarget(OreBlocks.SANCTIFICATION_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER, JADE);
        putMaterialTarget(OreBlocks.FLESHIFICATION_JADE_ORE.get(), 0x00a87b, true, ShowType.SPELUNKER, JADE);


        //蓝玉矿
        putMaterialTarget(SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER, SAPPHIRE);
        putMaterialTarget(DEEPSLATE_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER, SAPPHIRE);
        putMaterialTarget(CORRUPTION_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER, SAPPHIRE);
        putMaterialTarget(SANCTIFICATION_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER, SAPPHIRE);
        putMaterialTarget(FLESHIFICATION_SAPPHIRE_ORE.get(), 0x0052a8, true, ShowType.SPELUNKER, SAPPHIRE);

        //紫晶矿
        putMaterialTarget(OreBlocks.AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER, AMETHYST);
        putMaterialTarget(OreBlocks.DEEPSLATE_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER, AMETHYST);
        putMaterialTarget(OreBlocks.CORRUPTION_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER, AMETHYST);
        putMaterialTarget(OreBlocks.SANCTIFICATION_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER, AMETHYST);
        putMaterialTarget(OreBlocks.FLESHIFICATION_AMETHYST_ORE.get(), 0x7d00a8, true, ShowType.SPELUNKER, AMETHYST);


        //绿宝石矿
        putTarget(Blocks.EMERALD_ORE, 0xa3ff75, true, ShowType.SPELUNKER, Items.EMERALD);
        putTarget(Blocks.DEEPSLATE_EMERALD_ORE, 0xa3ff75, true, ShowType.SPELUNKER, Items.EMERALD);
        putTarget(CORRUPTION_EMERALD_ORE.get(), 0xa3ff75, true, ShowType.SPELUNKER, Items.EMERALD);
        putTarget(SANCTIFICATION_EMERALD_ORE.get(), 0xa3ff75, true, ShowType.SPELUNKER, Items.EMERALD);
        putTarget(FLESHIFICATION_EMERALD_ORE.get(), 0xa3ff75, true, ShowType.SPELUNKER, Items.EMERALD);


        //铁矿
        putTarget(Blocks.IRON_ORE, 0xbfae8f, true, ShowType.SPELUNKER, Items.RAW_IRON);
        putTarget(Blocks.DEEPSLATE_IRON_ORE, 0xbfae8f, true, ShowType.SPELUNKER, Items.RAW_IRON);
        putTarget(CORRUPTION_IRON_ORE.get(), 0xbfae8f, true, ShowType.SPELUNKER, Items.RAW_IRON);
        putTarget(SANCTIFICATION_IRON_ORE.get(), 0xbfae8f, true, ShowType.SPELUNKER, Items.RAW_IRON);
        putTarget(FLESHIFICATION_IRON_ORE.get(), 0xbfae8f, true, ShowType.SPELUNKER, Items.RAW_IRON);


        //金矿
        putTarget(Blocks.GOLD_ORE, 0xccbe20, true, ShowType.SPELUNKER, Items.RAW_GOLD);
        putTarget(Blocks.DEEPSLATE_GOLD_ORE, 0xccbe20, true, ShowType.SPELUNKER, Items.RAW_GOLD);
        putTarget(CORRUPTION_GOLD_ORE.get(), 0xccbe20, true, ShowType.SPELUNKER, Items.RAW_GOLD);
        putTarget(SANCTIFICATION_GOLD_ORE.get(), 0xccbe20, true, ShowType.SPELUNKER, Items.RAW_GOLD);
        putTarget(FLESHIFICATION_GOLD_ORE.get(), 0xccbe20, true, ShowType.SPELUNKER, Items.RAW_GOLD);
        putTarget(Blocks.GILDED_BLACKSTONE, 0xccbe20, true, ShowType.SPELUNKER, Items.RAW_GOLD);
        putTarget(Blocks.NETHER_GOLD_ORE, 0xccbe20, true, ShowType.SPELUNKER, Items.RAW_GOLD);


        //煤矿
        putTarget(Blocks.COAL_ORE, 0x555555, false, ShowType.SPELUNKER, Items.COAL);
        putTarget(Blocks.DEEPSLATE_COAL_ORE, 0x555555, false, ShowType.SPELUNKER, Items.COAL);
        putTarget(CORRUPTION_COAL_ORE.get(), 0x555555, true, ShowType.SPELUNKER, Items.COAL);
        putTarget(SANCTIFICATION_COAL_ORE.get(), 0x555555, true, ShowType.SPELUNKER, Items.COAL);
        putTarget(FLESHIFICATION_COAL_ORE.get(), 0x555555, true, ShowType.SPELUNKER, Items.COAL);

        //铜矿
        putTarget(Blocks.COPPER_ORE, 0x97502d, false, ShowType.SPELUNKER, Items.RAW_COPPER);
        putTarget(Blocks.DEEPSLATE_COPPER_ORE, 0x97502d, false, ShowType.SPELUNKER, Items.RAW_COPPER);
        putTarget(CORRUPTION_COPPER_ORE.get(), 0x97502d, true, ShowType.SPELUNKER, Items.RAW_COPPER);
        putTarget(SANCTIFICATION_COPPER_ORE.get(), 0x97502d, true, ShowType.SPELUNKER, Items.RAW_COPPER);
        putTarget(FLESHIFICATION_COPPER_ORE.get(), 0x97502d, true, ShowType.SPELUNKER, Items.RAW_COPPER);

        //锡矿
        putMaterialTarget(TIN_ORE.get(), 0x96926e, false, ShowType.SPELUNKER, RAW_TIN);
        putMaterialTarget(DEEPSLATE_TIN_ORE.get(), 0x96926e, false, ShowType.SPELUNKER, RAW_TIN);
        putMaterialTarget(CORRUPTION_TIN_ORE.get(), 0x96926e, true, ShowType.SPELUNKER, RAW_TIN);
        putMaterialTarget(SANCTIFICATION_TIN_ORE.get(), 0x96926e, true, ShowType.SPELUNKER, RAW_TIN);
        putMaterialTarget(FLESHIFICATION_TIN_ORE.get(), 0x96926e, true, ShowType.SPELUNKER, RAW_TIN);

        //铅矿
        putMaterialTarget(LEAD_ORE.get(), 0x304963, false, ShowType.SPELUNKER, RAW_LEAD);
        putMaterialTarget(DEEPSLATE_LEAD_ORE.get(), 0x304963, false, ShowType.SPELUNKER, RAW_LEAD);
        putMaterialTarget(CORRUPTION_LEAD_ORE.get(), 0x304963, true, ShowType.SPELUNKER, RAW_LEAD);
        putMaterialTarget(SANCTIFICATION_LEAD_ORE.get(), 0x304963, true, ShowType.SPELUNKER, RAW_LEAD);
        putMaterialTarget(FLESHIFICATION_LEAD_ORE.get(), 0x304963, true, ShowType.SPELUNKER, RAW_LEAD);

        // 银矿
        putMaterialTarget(SILVER_ORE.get(), 0x6a737c, false, ShowType.SPELUNKER, RAW_SILVER);
        putMaterialTarget(DEEPSLATE_SILVER_ORE.get(), 0x6a737c, false, ShowType.SPELUNKER, RAW_SILVER);
        putMaterialTarget(CORRUPTION_SILVER_ORE.get(), 0x6a737c, true, ShowType.SPELUNKER, RAW_SILVER);
        putMaterialTarget(SANCTIFICATION_SILVER_ORE.get(), 0x6a737c, true, ShowType.SPELUNKER, RAW_SILVER);
        putMaterialTarget(FLESHIFICATION_SILVER_ORE.get(), 0x6a737c, true, ShowType.SPELUNKER, RAW_SILVER);

        // 钨矿
        putMaterialTarget(TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER, RAW_TUNGSTEN);
        putMaterialTarget(DEEPSLATE_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER, RAW_TUNGSTEN);
        putMaterialTarget(CORRUPTION_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER, RAW_TUNGSTEN);
        putMaterialTarget(SANCTIFICATION_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER, RAW_TUNGSTEN);
        putMaterialTarget(FLESHIFICATION_TUNGSTEN_ORE.get(), 0x86be9c, true, ShowType.SPELUNKER, RAW_TUNGSTEN);

        // 铂金矿
        putMaterialTarget(PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER, RAW_PLATINUM);
        putMaterialTarget(DEEPSLATE_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER, RAW_PLATINUM);
        putMaterialTarget(CORRUPTION_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER, RAW_PLATINUM);
        putMaterialTarget(SANCTIFICATION_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER, RAW_PLATINUM);
        putMaterialTarget(FLESHIFICATION_PLATINUM_ORE.get(), 0x81b9dd, true, ShowType.SPELUNKER, RAW_PLATINUM);

        // 生命水晶
        putTarget(LIFE_CRYSTAL_BLOCK.get(), 0xec173e, true, ShowType.SPELUNKER);
        // 箱子
        for (DeferredBlock<BaseChestBlock> normalChest : ChestBlocks.NORMAL_CHESTS) {
            putTarget(normalChest.get(), 0xe8c314, true, ShowType.SPELUNKER);
        }
        putTarget(Blocks.CHEST, 0xe8c314, true, ShowType.SPELUNKER);


        // 青金石
        putTarget(Blocks.LAPIS_ORE, 0x687bff, false, ShowType.SPELUNKER, Items.LAPIS_LAZULI);
        putTarget(Blocks.DEEPSLATE_LAPIS_ORE, 0x687bff, false, ShowType.SPELUNKER, Items.LAPIS_LAZULI);
        putTarget(CORRUPTION_LAPIS_ORE.get(), 0x687bff, true, ShowType.SPELUNKER, Items.LAPIS_LAZULI);
        putTarget(SANCTIFICATION_LAPIS_ORE.get(), 0x687bff, true, ShowType.SPELUNKER, Items.LAPIS_LAZULI);
        putTarget(FLESHIFICATION_LAPIS_ORE.get(), 0x687bff, true, ShowType.SPELUNKER, Items.LAPIS_LAZULI);

        // 红石
        putTarget(Blocks.REDSTONE_ORE, 0x7d0000, false, ShowType.SPELUNKER, Items.REDSTONE);
        putTarget(Blocks.DEEPSLATE_REDSTONE_ORE, 0x7d0000, false, ShowType.SPELUNKER, Items.REDSTONE);
        putTarget(CORRUPTION_REDSTONE_ORE.get(), 0x7d0000, true, ShowType.SPELUNKER, Items.REDSTONE);
        putTarget(SANCTIFICATION_REDSTONE_ORE.get(), 0x7d0000, true, ShowType.SPELUNKER, Items.REDSTONE);
        putTarget(FLESHIFICATION_REDSTONE_ORE.get(), 0x7d0000, true, ShowType.SPELUNKER, Items.REDSTONE);

        // 化石对标
        putTarget(COLD_CRYSTAL_ORE.get(), 0x3db7b0, true, ShowType.SPELUNKER/*,COLD_CRYSTAL*/);
        putTarget(GELSTONE_ORE.get(), 0x62b73d, true, ShowType.SPELUNKER/*,GELSTONE*/);
        putTarget(OPAL_ORE.get(), 0x4bbcff, true, ShowType.SPELUNKER/*,OPAL*/);

        // 狱石
        putMaterialTarget(HELLSTONE.get(), 0xea650e, true, ShowType.SPELUNKER, RAW_HELLSTONE);
        putMaterialTarget(ASH_HELLSTONE.get(), 0xea650e, true, ShowType.SPELUNKER, RAW_HELLSTONE);

        // 石英
        putTarget(Blocks.NETHER_QUARTZ_ORE, 0xe2ccbc, true, ShowType.SPELUNKER, Items.QUARTZ);

        // 新三矿 todo仅敲除祭坛后可探测
        putMaterialTarget(DEEPSLATE_COBALT_ORE.get(), 0x0060e9, true, ShowType.SPELUNKER, RAW_COBALT);
        putMaterialTarget(DEEPSLATE_PALLADIUM_ORE.get(), 0xe97500, true, ShowType.SPELUNKER, RAW_PALLADIUM);
        putMaterialTarget(DEEPSLATE_MYTHRIL_ORE.get(), 0x00e9ae, true, ShowType.SPELUNKER, RAW_MYTHRIL);
        putMaterialTarget(DEEPSLATE_ORICHALCUM_ORE.get(), 0xe300e9, true, ShowType.SPELUNKER, RAW_ORICHALCUM);
        putMaterialTarget(DEEPSLATE_ADAMANTITE_ORE.get(), 0xe90056, true, ShowType.SPELUNKER, RAW_ADAMANTITE);
        putMaterialTarget(DEEPSLATE_TITANIUM_ORE.get(), 0xf5dcff, true, ShowType.SPELUNKER, RAW_TITANIUM);
        putMaterialTarget(CHLOROPHYTE_ORE.get(), 0x00a41b, true, ShowType.SPELUNKER, RAW_CHLOROPHYTE);

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
        putTarget(block, rgb, always, showType, Items.AIR);
    }
    /**
     * 原版用的
     */
    private void putTarget(Block block, int rgb, boolean always, ShowType showType, Item showItem) {
        targets.put(block, new Entry(rgb, always, showType, TextureMapping.getItemTexture(showItem).withPrefix("textures/")));
    }
    /**
     * 咱汇流用的
     */
    private void putMaterialTarget(Block block, int rgb, boolean always, ShowType showType, DeferredItem<?> item) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(item.get());
        targets.put(block, new Entry(rgb, always, showType, resourcelocation.withPrefix("textures/item/materials/")));
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
        if (!cachedPointers.isEmpty()) cachedPointers.values().forEach(lc -> lc.isAlive = false);
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

            for (BlockPos blockPos : n.getValue()) {//每个矿石
                if (blockPos == null) {
                    return;
                }

                final float size = 1.0f;

                /* 相近同种方块禁止渲染 */
                boolean ifNear = false;
                if (player.level().getBlockState(blockPos).is(Blocks.AIR)) {//已被挖掘，取消缓存

//                        System.out.println("block break");
                    centerCache.remove(blockPos);
                    ArrayList<BlockPos> list = centers.get(n.getKey());
                    if (list != null && !list.isEmpty()) {
                        // 刷新周围中心块
                        // 附近有中心块，清除改块
                        list.removeIf(centerPos -> centerPos.distSqr(blockPos) < 25);
                    }
                }

                if ((target != null && target.showType == ShowType.SPELUNKER) || n.getKey().defaultBlockState().is(Tags.Blocks.ORES)) {//矿透方块
                    if (!player.hasEffect(ModEffects.SPELUNKER)) continue;
                    //todo 可以优化
                    for (BlockPos centerPos : centers.get(n.getKey())) {//否则查找所有的中心块
                        double distance = centerPos.distSqr(blockPos);
                        if (distance < blockGen.centerInternal) {//附近有中心块，添加缓存
                            centerCache.put(blockPos, centerPos);
                            ifNear = true;
                            break;
                        }
                    }
                    if (ifNear) {
                        if (!SHOW_DETAIL_SPECULAR.get().isDown()) continue;//非中心块或tab按下不渲染

                    } else {
                        centers.get(n.getKey()).add(blockPos);//太远则自己成为中心块
                        centerCacheFrame.put(blockPos, n.getKey());//只渲染中心块文本

                    }
                } else if (target != null && target.showType == ShowType.DANGER) {//危险方块
                    if (!player.hasEffect(ModEffects.DANGER_SENSE)) continue;
                    centerCacheFrame.put(blockPos, n.getKey());//渲染所有危险方块
                }


                //距离越远透明度越低
                a = (int) ((255 - Math.min(player.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) / (blockGen.range * blockGen.range) * 255, 255)) * blockGen.maxAlpha);
                if (a <= 0) continue;

                if (SHOW_DETAIL_SPECULAR.get().isDown()) {
                    cachedPointers.values().forEach(controller -> controller.component.setVisible(true));
                    Block self = player.level().getBlockState(blockPos).getBlock();
                    boolean up = !(player.level().getBlockState(blockPos.above()).getBlock() == self);
                    boolean down = !(player.level().getBlockState(blockPos.below()).getBlock() == self);
                    boolean north = !(player.level().getBlockState(blockPos.north()).getBlock() == self);
                    boolean south = !(player.level().getBlockState(blockPos.south()).getBlock() == self);
                    boolean east = !(player.level().getBlockState(blockPos.east()).getBlock() == self);
                    boolean west = !(player.level().getBlockState(blockPos.west()).getBlock() == self);
                    if (up || down || north || south || east || west)
                        renderDebugBlock(buffer, blockPos, size, r, g, b, a, up, down, north, south, east, west);
                } else {
                    cachedPointers.values().forEach(controller -> controller.component.setVisible(false));
                    renderDebugBlock(buffer, blockPos, size, r, g, b, a);
                }
            }
            centerCacheFrame.keySet().forEach(pos -> tryComputePointers(pos, target));
        }
        cachedPointers.values().removeIf(controller -> {
            if(!controller.isAlive) {
                controller.component.close();
                return true;
            }
            return false;
        });
    }

    private void tryComputePointers(BlockPos blockPos, Entry target) {
        if (tracker != null) { // 创建对应指针
            COMPONENT_BUILDER.setIcon1(target != null ? target.showItemRL : EMPTY);

            cachedPointers.compute(blockPos, (pos, controller) -> {
                if (controller == null) {
                    StaticComponent component = tracker.addStaticPosComponent(
                            Confluence.MODID,
                            "oreTrack",
                            COMPONENT_BUILDER.build(),
                            false
                    );
                    Vec3 pos1 = blockPos.getCenter();
                    component.posUpdater(pos2 -> pos1);
                    return new StaticComponentLifeController(component);
                } else {
                    controller.isAlive = true;
                    return controller;
                }
            });
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
