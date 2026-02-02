package org.confluence.terra_furniture.common.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.init.TFBlocks;

public class TFChineseProvider extends LanguageProvider {
    public TFChineseProvider(PackOutput output) {
        super(output, TerraFurniture.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("creativetab.terra_furniture", "泰拉家具");

        add("msg.terra_furniture.sit", "出现某些错误导致乘坐方块失败。");

        add("container.terra_furniture.glass_kiln", "玻璃窑");
        add("container.terra_furniture.living_loom", "生命木织机");
        add("container.terra_furniture.ice_machine", "冰雪机");
        add("container.terra_furniture.trash_can", "垃圾桶");

        add("title.terra_furniture.glass_kiln", "玻璃窑");
        add("title.terra_furniture.living_loom", "生命木织机");
        add("title.terra_furniture.ice_machine", "冰雪机");

        add("info.terra_furniture.time", "时间: [%s:%s]");

        add(TFBlocks.GLASS_KILN.get(), "玻璃窑");
        add(TFBlocks.LIVING_LOOM.get(), "生命木织机");
        add(TFBlocks.ICE_MACHINE.get(), "冰雪机");

        add(TFBlocks.FISH_BOWL.get(), "鱼缸");
        add(TFBlocks.GOLD_FISH_BOWL.get(), "金鱼缸");
        add(TFBlocks.PUPFISH_BOWL.get(), "鳉鱼缸");
        add(TFBlocks.LAVA_SERPENT_BOWL.get(), "熔岩蛇缸");
        add(TFBlocks.TRASH_CAN.get(), "垃圾桶");
        add(TFBlocks.HANGING_POT.get(), "吊挂盆");

        add(TFBlocks.PLASTIC_CHAIR.get(), "塑料椅");
        add(TFBlocks.GLASS_DOOR.get(), "玻璃门");
        add(TFBlocks.GLASS_CHAIR.get(), "玻璃椅");
        add(TFBlocks.GLASS_TOILET.get(), "玻璃马桶");
        add(TFBlocks.GLASS_TABLE.get(), "玻璃桌");
        add(TFBlocks.GLASS_CANDLE.get(), "玻璃蜡烛");
        add(TFBlocks.GLASS_LANTERN.get(), "玻璃灯笼");
        add(TFBlocks.GLASS_SOFA.get(), "玻璃沙发");
        add(TFBlocks.GLASS_LAMP.get(), "玻璃灯");
        add(TFBlocks.GLASS_CANDELABRAS.get(), "玻璃烛台");
        add(TFBlocks.GLASS_SINK.get(), "玻璃水槽");
        add(TFBlocks.GLASS_CLOCK.get(), "玻璃时钟");
        add(TFBlocks.GLASS_BATHTUB.get(), "玻璃浴缸");

        add(TFBlocks.BLUE_DUNGEON_DOOR.get(), "蓝地牢门");
        add(TFBlocks.BLUE_DUNGEON_CHAIR.get(), "蓝地牢椅");
        add(TFBlocks.BLUE_DUNGEON_TOILET.get(), "蓝地牢马桶");
        add(TFBlocks.BLUE_DUNGEON_TABLE.get(), "蓝地牢桌");
        add(TFBlocks.BLUE_DUNGEON_CANDLE.get(), "蓝地牢蜡烛");
        add(TFBlocks.BLUE_DUNGEON_LANTERN.get(), "蓝地牢灯笼");
        add(TFBlocks.BLUE_DUNGEON_SOFA.get(), "蓝地牢沙发");
        add(TFBlocks.BLUE_DUNGEON_LAMP.get(), "蓝地牢灯");
        add(TFBlocks.BLUE_DUNGEON_CANDELABRAS.get(), "蓝地牢烛台");
        add(TFBlocks.BLUE_DUNGEON_SINK.get(), "蓝地牢水槽");
        add(TFBlocks.BLUE_DUNGEON_CLOCK.get(), "蓝地牢时钟");
        add(TFBlocks.BLUE_DUNGEON_BATHTUB.get(), "蓝地牢浴缸");

        add(TFBlocks.WOODEN_CHAIR.get(), "木椅");
        add(TFBlocks.WOODEN_TABLE.get(), "木桌");

        add(TFBlocks.PIN_WHEEL.get(), "风车");

        add(TFBlocks.BLUE_DUNGEON_CHANDELIER.get(), "蓝地牢大型吊灯");

    }
}
