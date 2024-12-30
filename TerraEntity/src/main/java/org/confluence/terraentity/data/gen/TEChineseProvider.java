package org.confluence.terraentity.data.gen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.confluence.terraentity.init.TEItems;

import static org.confluence.terraentity.TerraEntity.MODID;


public class TEChineseProvider extends LanguageProvider {
    public TEChineseProvider(PackOutput output) {
        super(output, MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.terraentity.title", "泰拉生物");

        add("entity.terra_entity.ice_slime", "冰冻史莱姆");
        add("entity.terra_entity.blue_slime", "蓝色史莱姆");
        add("entity.terra_entity.red_slime", "红色史莱姆");
        add("entity.terra_entity.purple_slime", "紫色史莱姆");
        add("entity.terra_entity.jungle_slime", "丛林史莱姆");
        add("entity.terra_entity.pink_slime", "粉色史莱姆");
        add("entity.terra_entity.yellow_slime", "黄色史莱姆");
        add("entity.terra_entity.honey_slime", "蜂蜜史莱姆");
        add("entity.terra_entity.crimson_slime", "猩红史莱姆");
        add("entity.terra_entity.corrupted_slime", "腐化史莱姆");
        add("entity.terra_entity.desert_slime", "沙漠史莱姆");
        add("entity.terra_entity.tropic_slime", "热带史莱姆");
        add("entity.terra_entity.green_slime", "绿色史莱姆");
        add("entity.terra_entity.black_slime", "史莱姆之母");
        add("entity.terra_entity.lava_slime", "岩浆史莱姆");
        add("entity.terra_entity.demon_eye", "恶魔眼");
        add("entity.terra_entity.flying_fish", "飞鱼");
        add("entity.terra_entity.drippler", "滴滴怪");
        add("entity.terra_entity.blood_zombie", "血腥僵尸");
        add("entity.terra_entity.blood_crawler", "血爬虫");
        add("entity.terra_entity.bloody_spore", "血腥芽孢");
        add("entity.terra_entity.face_monster", "脸怪");
        add("entity.terra_entity.crimson_kemera", "猩红喀迈拉");
        add("entity.terra_entity.eater_of_souls", "噬魂怪");
        add("entity.terra_entity.decayeder", "腐骴");


        add("entity.terra_entity.king_slime", "史莱姆王");
        add("entity.terra_entity.cthulhu_eye", "克苏鲁之眼");
        add("entity.terra_entity.eater_of_world", "世界吞噬怪");

        add("effect.terra_entity.demonic_thoughts", "邪念");
        // 刷怪蛋
        add(TEItems.BLUE_SLIME_SPAWN_EGG.get(), "蓝色史莱姆刷怪蛋");
        add(TEItems.RED_SLIME_SPAWN_EGG.get(), "红色史莱姆刷怪蛋");
        add(TEItems.YELLOW_SLIME_SPAWN_EGG.get(), "黄色史莱姆刷怪蛋");
        add(TEItems.HONEY_SLIME_SPAWN_EGG.get(), "蜂蜜史莱姆刷怪蛋");
        add(TEItems.PURPLE_SLIME_SPAWN_EGG.get(), "紫色史莱姆刷怪蛋");
        add(TEItems.DESERT_SLIME_SPAWN_EGG.get(), "沙漠史莱姆刷怪蛋");
        add(TEItems.JUNGLE_SLIME_SPAWN_EGG.get(), "丛林史莱姆刷怪蛋");
        add(TEItems.PINK_SLIME_SPAWN_EGG.get(), "粉色史莱姆刷怪蛋");
        add(TEItems.ICE_SLIME_SPAWN_EGG.get(), "冰冻史莱姆刷怪蛋");
        add(TEItems.GREEN_SLIME_SPAWN_EGG.get(), "绿色史莱姆刷怪蛋");
        add(TEItems.BLACK_SLIME_SPAWN_EGG.get(), "史莱姆之母刷怪蛋");
        add(TEItems.CRIMSON_SLIME_SPAWN_EGG.get(), "猩红史莱姆刷怪蛋");
        add(TEItems.TROPIC_SLIME_SPAWN_EGG.get(), "热带史莱姆刷怪蛋");
        add(TEItems.LUMINOUS_SLIME_SPAWN_EGG.get(), "夜明史莱姆刷怪蛋");
        add(TEItems.LAVA_SLIME_SPAWN_EGG.get(), "岩浆史莱姆刷怪蛋");

        add(TEItems.DEMON_EYE_SPAWN_EGG.get(), "恶魔眼刷怪蛋");
        add(TEItems.FLYING_FISH_SPAWN_EGG.get(), "飞鱼刷怪蛋");
        add(TEItems.DRIPPLER_SPAWN_EGG.get(), "滴滴怪刷怪蛋");
        add(TEItems.BLOOD_ZOMBIE_SPAWN_EGG.get(), "血腥僵尸刷怪蛋");

        add(TEItems.BLOOD_CRAWLER_SPAWN_EGG.get(), "血爬虫刷怪蛋");
        add(TEItems.BLOODY_SPORE_SPAWN_EGG.get(), "血腥芽孢刷怪蛋");
        add(TEItems.CRIMSON_KEMERA_EGG.get(), "猩红喀迈拉刷怪蛋");
        add(TEItems.FACE_MONSTER_EGG.get(), "脸怪刷怪蛋");

        add(TEItems.EATER_OF_SOULS_SPAWN_EGG.get(), "噬魂怪刷怪蛋");
        add(TEItems.DECAYEDER_SPAWN_EGG.get(), "腐骴刷怪蛋");

        add(TEItems.KING_SLIME_SPAWN_EGG.get(), "史莱姆王刷怪蛋");
        add(TEItems.CTHULHU_EYE_SPAWN_EGG.get(), "克苏鲁之眼刷怪蛋");
        add(TEItems.EATER_OF_WORLD_SPAWN_EGG.get(), "世界吞噬怪刷怪蛋");

        add("message.terraentity.boss_spawn", "%s已苏醒！");
        add("message.terraentity.boss_leave", "%s已被打败！");

    }
}
