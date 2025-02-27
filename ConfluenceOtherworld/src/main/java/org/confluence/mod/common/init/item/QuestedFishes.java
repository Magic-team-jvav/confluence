package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;

public class QuestedFishes {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<Item> AMANITA_FUNGIFIN = register("amanita_fungifin"), // 发光蘑菇鱼
            ANGELFISH = register("angelfish"), // 天使鱼
            BATFISH = register("batfish"), // 蝙蝠鱼
            BLOODY_MANOWAR = register("bloody_manowar"), // 血腥战神
            BONEFISH = register("bonefish"), // 骨鱼
            BUMBLEBEE_TUNA = register("bumblebee_tuna"), // 大黄蜂金枪鱼
            BUNNYFISH = register("bunnyfish"), // 兔兔鱼
            CAP_TUNABEARD = register("cap_tunabeard"), // 金枪鱼船长
            INFECTED_SCABBARDFISH = register("infected_scabbardfish"), // 染病鞘鱼
            JEWELFISH = register("jewelfish"), // 宝石鱼
            MIRAGE_FISH = register("mirage_fish"), // 混沌鱼
            MUDFISH = register("mudfish"), // 泥鱼
            MUTANT_FLINXFIN = register("mutant_flinxfin"), // 突变弗林鱼
            CLOUDFISH = register("cloudfish"), // 云朵鱼
            TR_CLOWNFISH = register("tr_clownfish"), // 异域小丑鱼
            DEMONIC_HELLFISH = register("demonic_hellfish"), // 地狱恶魔鱼
            DERPFISH = register("derpfish"), // 跳跳鱼
            ICHORFISH = register("ichorfish"), // 灵液鱼
            SLIMEFISH = register("slimefish"), // 史莱姆鱼
            ZOMBIE_FISH = register("zombie_fish"), // 僵尸鱼
            TROPICAL_BARRACUDA = register("tropical_barracuda"), // 热带梭鱼
            UNICORN_FISH = register("unicorn_fish"), // 独角兽鱼
            WYVERNTAIL = register("wyverntail"), // 飞龙尾
            DIRTFISH = register("dirtfish"), // 土鱼
            CURSEDFISH = register("cursedfish"), // 诅咒鱼
            DYNAMITE_FISH = register("dynamite_fish"), // 雷管鱼
            FALLEN_STARFISH = register("fallen_starfish"), // 坠落星鱼
            EATER_OF_PLANKTON = register("eater_of_plankton"), // 浮游噬鱼
            THE_FISH_OF_CTHULHU = register("the_fish_of_cthulhu"), // 克苏鲁之鱼
            FISHRON = register("fishron"), // 猪龙鱼
            GUIDE_VOODOO_FISH = register("guide_voodoo_fish"), // 向导巫毒鱼
            HARPYFISH = register("harpyfish"), // 鸟妖鱼
            FISHOTRON = register("fishotron"), // 骷髅王鱼
            SCORPIO_FISH = register("scorpio_fish"), // 毒蝎鱼
            HUNGERFISH = register("hungerfish"), // 饥饿鱼
            CATFISH = register("catfish"), // 猫猫鱼
            PENGFISH = register("pengfish"), // 企鹅鱼
            PIXIEFISH = register("pixiefish"), // 妖精鱼
            SCARAB_FISH = register("scarab_fish"), // 甲虫鱼
            SPIDERFISH = register("spiderfish"), // 蜘蛛鱼
            TUNDRA_TROUT = register("tundra_trout"); // 苔原鳟鱼

    public static DeferredItem<Item> register(String name) {
        return ITEMS.register(name, () -> new CustomRarityItem(new Item.Properties().fireResistant(), ModRarity.QUEST));
    }
}
