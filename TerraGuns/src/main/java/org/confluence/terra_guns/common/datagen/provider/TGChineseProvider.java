package org.confluence.terra_guns.common.datagen.provider;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.confluence.terra_guns.TerraGuns;

import static org.confluence.terra_guns.common.init.TGEntities.BASE_BULLET_ENTITY;
import static org.confluence.terra_guns.common.init.TGItems.*;

public class TGChineseProvider extends LanguageProvider {
    public TGChineseProvider(PackOutput output) {
        super(output, TerraGuns.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("tooltip.terra_guns.damage", "远程伤害：%s");
        add("tooltip.terra_guns.critical", "暴击率：%s%%");
        add("tooltip.terra_guns.knockback", "击退力：%s");

        addItem(HAND_GUN, "手枪");
        addItem(SHOTGUN, "霰弹枪");
        addItem(FLINTLOCK_PISTOL, "燧发枪");
        addItem(BOOMSTICK, "三发猎枪");
        addItem(THE_UNDERTAKER, "夺命枪");
        addItem(MUSKET, "火枪");
        addItem(MINISHARK, "迷你鲨");
        addItem(BLOWGUN, "吹管");
        addItem(TACTICAL_SHOTGUN, "战术霰弹枪");
        addItem(SNOWBALL_CANNON, "雪球炮");
        addItem(PHOENIX_BLASTER, "凤凰爆破枪");

        addItem(MUSKET_BULLET, "火枪子弹");
        addItem(METEOR_SHOT, "流星弹");
        addItem(SILVER_BULLET, "银子弹");
        addItem(CRYSTAL_BULLET, "水晶子弹");
        addItem(CURSED_BULLET, "诅咒弹");
        addItem(CHLOROPHYTE_BULLET, "叶绿弹");
        addItem(HIGH_VELOCITY_BULLET, "高速子弹");
        addItem(ICHOR_BULLET, "灵液弹");
        addItem(VENOM_BULLET, "毒液弹");
        addItem(PARTY_BULLET, "派对弹");
        addItem(NANO_BULLET, "纳米弹");
        addItem(EXPLODING_BULLET, "爆破弹");
        addItem(GOLDEN_BULLET, "金子弹");
        addItem(ENDLESS_MUSKET_POUCH, "无尽火枪袋");
        addItem(LUMINITE_BULLET, "夜明弹");
        addItem(TUNGSTEN_BULLET, "钨子弹");

        addEntityType(BASE_BULLET_ENTITY, "子弹");

        add("key.terra_guns.shoot", "射击");
        add("key.terra_guns.aim", "瞄准");

        add("creative_tab.terra_guns.gun_tab", "泰拉枪械");
        add("death.attack.bullet_damage", "%1%s 被 %2$s 枪击");
    }
}
