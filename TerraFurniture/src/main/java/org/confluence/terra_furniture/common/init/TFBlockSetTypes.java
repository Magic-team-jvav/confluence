package org.confluence.terra_furniture.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.block.func.TFBlockSetType;

import java.util.List;

/**
 * 仅静态存储。
 */
@SuppressWarnings("unused")
public class TFBlockSetTypes {

    /* 已在原版定义的, 但是家具扩展了BST功能所以你只应使用家具的扩展版 */
    /** 橡木(就是普通木头) */
    public static final TFBlockSetType OAK = new TFBlockSetType(BlockSetType.OAK, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.OAK_FURNITURE), ResourceLocation.withDefaultNamespace("block/oak_planks"));
    /** 云杉木(可能是针叶木, 如果单做了再说的) */
    public static final TFBlockSetType SPRUCE = new TFBlockSetType(BlockSetType.SPRUCE, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.SPRUCE_FURNITURE), ResourceLocation.withDefaultNamespace("block/spruce_planks"));
    /** 白桦木 */
    public static final TFBlockSetType BIRCH = new TFBlockSetType(BlockSetType.BIRCH, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.BIRCH_FURNITURE), ResourceLocation.withDefaultNamespace("block/birch_planks"));
    /** 金合欢木 */
    public static final TFBlockSetType ACACIA = new TFBlockSetType(BlockSetType.ACACIA, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.ACACIA_FURNITURE), ResourceLocation.withDefaultNamespace("block/acacia_planks"));
    /** 樱花木 */
    public static final TFBlockSetType CHERRY = new TFBlockSetType(BlockSetType.CHERRY, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.CHERRY_FURNITURE), ResourceLocation.withDefaultNamespace("block/cherry_planks"));
    /** 丛林木 */
    public static final TFBlockSetType JUNGLE = new TFBlockSetType(BlockSetType.JUNGLE, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.JUNGLE_FURNITURE), ResourceLocation.withDefaultNamespace("block/jungle_planks"));
    /** 深色橡木 */
    public static final TFBlockSetType DARK_OAK = new TFBlockSetType(BlockSetType.DARK_OAK, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.DARK_OAK_FURNITURE), ResourceLocation.withDefaultNamespace("block/dark_oak_planks"));
    /** 绯红木 */
    public static final TFBlockSetType CRIMSON = new TFBlockSetType(BlockSetType.CRIMSON, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.CRIMSON_FURNITURE), ResourceLocation.withDefaultNamespace("block/crimson_planks"));
    /** 诡异木 */
    public static final TFBlockSetType WARPED = new TFBlockSetType(BlockSetType.WARPED, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.WARPED_FURNITURE), ResourceLocation.withDefaultNamespace("block/warped_planks"));
    /** 红树木 */
    public static final TFBlockSetType MANGROVE = new TFBlockSetType(BlockSetType.MANGROVE, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.MANGROVE_FURNITURE), ResourceLocation.withDefaultNamespace("block/mangrove_planks"));
    /** 竹木(就当竹子用的) */
    public static final TFBlockSetType BAMBOO = new TFBlockSetType(BlockSetType.BAMBOO, () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.BAMBOO_FURNITURE), ResourceLocation.withDefaultNamespace("block/bamboo_planks"));
    /** 铁 */
    public static final TFBlockSetType IRON = new TFBlockSetType(BlockSetType.IRON, () -> List.of(TFTags.IRON_FURNITURE));
    /** 铜 */
    public static final TFBlockSetType COPPER = new TFBlockSetType(BlockSetType.COPPER, () -> List.of(TFTags.COPPER_FURNITURE));
    /** 金(泰拉中无法制作，只能通过海盗事件获得的系列) */
    public static final TFBlockSetType GOLD = new TFBlockSetType(BlockSetType.GOLD, () -> List.of(TFTags.GOLD_FURNITURE), ResourceLocation.withDefaultNamespace("block/gold_block"));
    /** 石头(注意不是圆石) */
    public static final TFBlockSetType STONE = new TFBlockSetType(BlockSetType.STONE, () -> List.of(TFTags.STONE_FURNITURE), ResourceLocation.withDefaultNamespace("block/stone"));
    /** 磨制黑石 */
    public static final TFBlockSetType POLISHED_BLACKSTONE = new TFBlockSetType(BlockSetType.POLISHED_BLACKSTONE, () -> List.of(TFTags.POLISHED_BLACKSTONE_FURNITURE));


    /* 极其特殊的(不成一组家具) */
    /** 塑料椅 */
    public static final TFBlockSetType UNBREAKABLE = new TFBlockSetType("unbreakable", List::of);


    /* 下面是不可通过制作获得的材质 */
    /** 黑曜石(废墟建筑获得家具) */
    public static final TFBlockSetType OBSIDIAN = new TFBlockSetType("obsidian", () -> List.of(TFTags.OBSIDIAN_FURNITURE));
    /** 蓝地牢砖() */
    public static final TFBlockSetType BLUE_DUNGEON = new TFBlockSetType(
            "blue_dungeon", true, true, true,
            BlockSetType.PressurePlateSensitivity.MOBS, SoundType.BONE_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON,
            () -> List.of(TFTags.DUNGEON_FURNITURE, TFTags.BLUE_DUNGEON_FURNITURE),
            TerraFurniture.asResource("block/particle/blue_dungeon_sink_particle")
    );
    /** 绿地牢砖 */
    public static final TFBlockSetType GREEN_DUNGEON = new TFBlockSetType(
            "green_dungeon", true, true, true,
            BlockSetType.PressurePlateSensitivity.MOBS, SoundType.BONE_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON,
            () -> List.of(TFTags.DUNGEON_FURNITURE, TFTags.GREEN_DUNGEON_FURNITURE)
    );
    /** 粉地牢砖 */
    public static final TFBlockSetType PINK_DUNGEON = new TFBlockSetType(
            "pink_dungeon", true, true, true,
            BlockSetType.PressurePlateSensitivity.MOBS, SoundType.BONE_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON,
            () -> List.of(TFTags.DUNGEON_FURNITURE, TFTags.PINK_DUNGEON_FURNITURE)
    );
    /** 哥特 */
    public static final TFBlockSetType GOTHIC = new TFBlockSetType(
            "gothic", true, true, true,
            BlockSetType.PressurePlateSensitivity.MOBS, SoundType.BONE_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON,
            () -> List.of(TFTags.GOTHIC_FURNITURE)
    );



    /* 下面是可以通过制作获得的材质 */
    /** 骨头 */
    public static final TFBlockSetType BONE = new TFBlockSetType(
            "bone", true, true, true,
            BlockSetType.PressurePlateSensitivity.MOBS, SoundType.BONE_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON,
            () -> List.of(TFTags.BONE_FURNITURE)
    );
    /** 病变 */
    public static final TFBlockSetType LESION = new TFBlockSetType("lesion", () -> List.of(TFTags.LESION_FURNITURE));
    /** 血肉 */
    public static final TFBlockSetType FLESH = new TFBlockSetType("flesh", () -> List.of(TFTags.FLESH_FURNITURE));
    /** 玻璃 */
    public static final TFBlockSetType GLASS = new TFBlockSetType(
            "glass", true, true, true,
            BlockSetType.PressurePlateSensitivity.MOBS, SoundType.GLASS,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON,
            () -> List.of(TFTags.GLASS_FURNITURE),
            ResourceLocation.withDefaultNamespace("block/glass")
    );
    /** 蜂蜜 */
    public static final TFBlockSetType HONEY = new TFBlockSetType("honey", () -> List.of(TFTags.HONEY_FURNITURE));
    /** 冰冻(冰雪) */
    public static final TFBlockSetType FROZEN = new TFBlockSetType(
            "frozen", true, true, true,
            BlockSetType.PressurePlateSensitivity.MOBS, SoundType.GLASS,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON,
            () -> List.of(TFTags.FROZEN_FURNITURE)
    );
    /** 丛林蜥蜴砖(官方维基就这么拼的) */
    public static final TFBlockSetType LIHZAHRD = new TFBlockSetType("lihzahrd", () -> List.of(TFTags.LIHZAHRD_FURNITURE));
    /** 生命木 */
    public static final TFBlockSetType LIVING_WOOD = new TFBlockSetType("living_wood", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.LIVING_WOOD_FURNITURE));
    /** 天域(日盘块) */
    public static final TFBlockSetType SKYWARE = new TFBlockSetType("skyware", () -> List.of(TFTags.SKYWARE_FURNITURE));
    /** 史莱姆 */
    public static final TFBlockSetType SLIME = new TFBlockSetType("slime", () -> List.of(TFTags.SLIME_FURNITURE));
    /** 蒸汽朋克(齿轮) */
    public static final TFBlockSetType STEAMPUNK = new TFBlockSetType("steampunk", () -> List.of(TFTags.STEAMPUNK_FURNITURE));
    /** 灰烬木 */
    public static final TFBlockSetType ASH_WOOD = new TFBlockSetType("ash_wood", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.ASH_WOOD_FURNITURE));
    /** 气球 */
    public static final TFBlockSetType BALLOON = new TFBlockSetType("balloon", () -> List.of(TFTags.BALLOON_FURNITURE));
    /** 仙人掌 */
    public static final TFBlockSetType CACTUS = new TFBlockSetType("cactus", () -> List.of(TFTags.CACTUS_FURNITURE));
    /** 水晶(水晶块) */
    public static final TFBlockSetType CRYSTAL = new TFBlockSetType("crystal", () -> List.of(TFTags.CRYSTAL_FURNITURE));
    /** 王朝木 */
    public static final TFBlockSetType DYNASTY = new TFBlockSetType("dynasty", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.DYNASTY_FURNITURE));
    /** 乌木 */
    public static final TFBlockSetType EBONWOOD = new TFBlockSetType("ebonwood", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.EBONWOOD_FURNITURE));
    /** 花岗岩 */
    public static final TFBlockSetType GRANITE = new TFBlockSetType("granite", () -> List.of(TFTags.GRANITE_FURNITURE));
    /** 大理石 */
    public static final TFBlockSetType MARBLE = new TFBlockSetType("marble", () -> List.of(TFTags.MARBLE_FURNITURE));
    /** 火星(火星管道护板) */
    public static final TFBlockSetType MARTIAN = new TFBlockSetType("martian", () -> List.of(TFTags.MARTIAN_FURNITURE));
    /** 陨石 */
    public static final TFBlockSetType METEORITE = new TFBlockSetType("meteorite", () -> List.of(TFTags.METEORITE_FURNITURE));
    /** 蘑菇 */
    public static final TFBlockSetType MUSHROOM = new TFBlockSetType("mushroom", () -> List.of(TFTags.MUSHROOM_FURNITURE));
    /** 棕榈木 */
    public static final TFBlockSetType PALM_WOOD = new TFBlockSetType("palm_wood", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.PALM_WOOD_FURNITURE));
    /** 珍珠木 */
    public static final TFBlockSetType PEARLWOOD = new TFBlockSetType("pearlwood", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.PEARLWOOD_FURNITURE));
    /** 松木 */
    public static final TFBlockSetType PINE = new TFBlockSetType("pine", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.PINE_FURNITURE));
    /** 南瓜 */
    public static final TFBlockSetType PUMPKIN = new TFBlockSetType("pumpkin", () -> List.of(TFTags.PUMPKIN_FURNITURE));
    /** 珊瑚礁 */
    public static final TFBlockSetType REEF = new TFBlockSetType("reef", () -> List.of(TFTags.REEF_FURNITURE));
    /** 砂岩 */
    public static final TFBlockSetType SANDSTONE = new TFBlockSetType("sandstone", () -> List.of(TFTags.SANDSTONE_FURNITURE));
    /** 暗影木 */
    public static final TFBlockSetType SHADEWOOD = new TFBlockSetType("shadewood", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.SHADEWOOD_FURNITURE));
    /** 蜘蛛(蜘蛛窝块) */
    public static final TFBlockSetType SPIDER = new TFBlockSetType("spider", () -> List.of(TFTags.SPIDER_FURNITURE));
    /** 阴森木 */
    public static final TFBlockSetType SPOOKY = new TFBlockSetType("spooky", () -> List.of(TFTags.WOODEN_FURNITURE, TFTags.SPOOKY_FURNITURE));


    /* 四柱的 */
    /** 星云 */
    public static final TFBlockSetType NEBULA = new TFBlockSetType("nebula", () -> List.of(TFTags.NEBULA_FURNITURE));
    /** 日耀 */
    public static final TFBlockSetType SOLAR = new TFBlockSetType("solar", () -> List.of(TFTags.SOLAR_FURNITURE));
    /** 星尘 */
    public static final TFBlockSetType STARDUST = new TFBlockSetType("stardust", () -> List.of(TFTags.STARDUST_FURNITURE));
    /** 星旋(这个旋是维基的翻译字) */
    public static final TFBlockSetType VORTEX = new TFBlockSetType("vortex", () -> List.of(TFTags.VORTEX_FURNITURE));
}
