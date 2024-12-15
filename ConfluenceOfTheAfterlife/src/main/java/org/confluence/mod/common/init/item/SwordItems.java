package org.confluence.mod.common.init.item;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.item.sword.stagedy.EffectStrategy;
import org.confluence.mod.common.item.sword.stagedy.InventoryTickStrategy;
import org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy;
import org.confluence.mod.common.item.sword.stagedy.SwordPrefabs;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

import static org.confluence.mod.common.item.sword.stagedy.EffectStrategy.BLOOD_BUTCHERED_EFFECT;
import static org.confluence.mod.common.item.sword.stagedy.EffectStrategy.UNDEFINED_EFFECT;
import static org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy.*;
import static org.confluence.mod.common.item.sword.stagedy.SwordPrefabs.*;



public class SwordItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 普通短剑
    public static final DeferredItem<SwordItem> COPPER_SHORT_SWORD = register("copper_short_sword", ModTiers.COPPER, 2, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> TIN_SHORT_SWORD = register("tin_short_sword", ModTiers.TIN, 3, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> LEAD_SHORT_SWORD = register("lead_short_sword", ModTiers.LEAD, 3, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> SILVER_SHORT_SWORD = register("silver_short_sword", ModTiers.SILVER, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> TUNGSTEN_SHORT_SWORD = register("tungsten_short_sword", ModTiers.TUNGSTEN, 4, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> GOLDEN_SHORT_SWORD = register("golden_short_sword", ModTiers.GOLD, 5, 3, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> PLATINUM_SHORT_SWORD = register("platinum_short_sword", Tiers.DIAMOND, 5, 3, SHORT_SWORD.get());


    public static final DeferredItem<SwordItem> BREATHING_REED = register("breathing_reed", ModTiers.COPPER, 0, 0, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> UMBRELLA = register("umbrella", ModTiers.TITANIUM, 0, -2.4F,ModRarity.BLUE, UMBRELLA_SWORD.get());
    public static final DeferredItem<SwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella", ModTiers.TITANIUM, 0, -2.4F,ModRarity.BLUE, UMBRELLA_SWORD.get());


    //普通宽剑 默认横扫*1.5
    public static final DeferredItem<SwordItem> CACTUS_SWORD = register("cactus_sword", ModTiers.CACTUS, 4, 1.6F,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> COPPER_BOARD_SWORD = register("copper_board_sword", ModTiers.COPPER, 4, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> TIN_BOARD_SWORD = register("tin_board_sword", ModTiers.TIN, 4, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> LEAD_BOARD_SWORD = register("lead_board_sword", ModTiers.LEAD, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> SILVER_BOARD_SWORD = register("silver_board_sword", ModTiers.SILVER, 5, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> TUNGSTEN_BOARD_SWORD = register("tungsten_board_sword", ModTiers.TUNGSTEN, 6, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> GOLDEN_BOARD_SWORD = register("golden_board_sword", ModTiers.GOLD, 7, 1.6F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> PLATINUM_BOARD_SWORD = register("platinum_board_sword", Tiers.DIAMOND, 7, 1.6F, NORMAL_SWORD.get());

    //tip 注册剑的特殊功能只需修改最后一个参数即可，只需要把 NORMAL_SWORD替换成prefab的其他预制效果，还可以追加效果
    public static final DeferredItem<SwordItem> FAKE_SWORD = register("fake_sword",ModTiers.CANDY, 0, -1.4f, ModRarity.GRAY, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> CANDY_CANE_SWORD = register("candy_cane_sword",ModTiers.CANDY,0,-1.4f,NORMAL_SWORD.get());

    public static final DeferredItem<SwordItem> FALCON_BLADE = register("falcon_blade",ModTiers.TITANIUM, 3, -1.45F, ModRarity.BLUE, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> ZOMBIE_ARM = register("zombie_arm",ModTiers.TITANIUM,3,-1.4f,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> MANDIBLE_BLADE = register("mandible_blade", ModTiers.TITANIUM, 4,-1.4f,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> BONE_SWORD = register("bone_sword",ModTiers.TITANIUM, 5, -0.2F, ModRarity.ORANGE, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish", ModTiers.TITANIUM, 10,-3.5f,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> STYLISH_SCISSORS = register("stylish_scissors",ModTiers.TITANIUM, 3, -1.2f, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar",ModTiers.TITANIUM, 5, -0.3f, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> KATANA = register("katana",ModTiers.TITANIUM, 4, -0.3f, ModRarity.BLUE, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> TENTACLE_MACE = register("tentacle_mace",ModTiers.TITANIUM, 5, -1.4F, ModRarity.GREEN, NORMAL_SWORD.get());



    //改横扫大小的宽剑
    public static final DeferredItem<SwordItem> TERRAGRIM = register("terragrim", ModTiers.TITANIUM, 1, 5.2F, ModRarity.ORANGE, BOARD_SWORD.apply(1.2f));

    //效果剑
    public static final DeferredItem<SwordItem> LIGHTS_BANE = register("lights_bane",ModTiers.TITANIUM, 5, -0.1F,
            ModRarity.BLUE,     EFFECT_SWORD.apply(UNDEFINED_EFFECT).addOnHitEffect(BLOOD_BUTCHERED_EFFECT)); //TODO 魔光剑,暂时发光效果,同时添加血腥屠刀效果
    public static final DeferredItem<SwordItem> BLOOD_BUTCHERER = register("blood_butchere",ModTiers.TITANIUM, 7, -2.7F,
            ModRarity.BLUE,     EFFECT_SWORD.apply(BLOOD_BUTCHERED_EFFECT));
    public static final DeferredItem<SwordItem> VOLCANO = register("volcano",ModTiers.TITANIUM, 10, -0.4f,
            ModRarity.ORANGE,   EFFECT_SWORD.apply(UNDEFINED_EFFECT));  //todo 
    public static final DeferredItem<SwordItem> BAT_BAT = register("bat_bat", ModTiers.TITANIUM,12,-3.7f,
            ModRarity.ORANGE,   EFFECT_SWORD.apply(UNDEFINED_EFFECT));  //todo


    //弹幕剑
    public static final DeferredItem<SwordItem> ICE_BLADE = register("ice_blade",ModTiers.TITANIUM, 5, -0.1F,
            ModRarity.BLUE,     PROJ_SWORD.apply(ICE_PROJ));
    public static final DeferredItem<SwordItem>STARFURY = register("starfury",ModTiers.TITANIUM, 6, -0.1F,
            ModRarity.GREEN,    PROJ_SWORD.apply(STAR_FURY_PROJ));
    public static final DeferredItem<SwordItem> ENCHANTED_SWORD = register("enchanted_sword",ModTiers.TITANIUM, 7, -0.2F,
            ModRarity.ORANGE,   PROJ_SWORD.apply(ENCHANTED_SWORD_PROJ));


    // 特殊剑
    public static final DeferredItem<SwordItem> CROWBAR = register("crowbar",ModTiers.TITANIUM, 16, -1.0F,ModRarity.MASTER, BOARD_SWORD.apply(2.0f));
    public static final DeferredItem<SwordItem> DEVELOPER_SWORD = register("developer_sword",ModTiers.TITANIUM, 20, 20F, ModRarity.MASTER,
            SwordPrefabs.BOARD_SWORD.apply(10.0f)                                //宽剑
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,1.5f,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE)        //手持属性加成
                    .addOnHitEffect(EffectStrategy.UNDEFINED_EFFECT)                //命中效果
                    .setProj(ProjectileStrategy.ICE_PROJ)                           //弹幕效果
                    .setInventoryTick(InventoryTickStrategy.INVINCIBLE)             //背包每刻效果
    );


/*
    RED_LIGHT_SABER("red_light_saber", LightSaber.Red::new),
    ORANGE_LIGHT_SABER("orange_light_saber", LightSaber.Orange::new),
    YELLOW_LIGHT_SABER("yellow_light_saber", LightSaber.Yellow::new),
    GREEN_LIGHT_SABER("green_light_saber", LightSaber.Green::new),
    BLUE_LIGHT_SABER("blue_light_saber", LightSaber.Blue::new),
    PURPLE_LIGHT_SABER("purple_light_saber", LightSaber.Purple::new),
    WHITE_LIGHT_SABER("white_light_saber", LightSaber.White::new),

    */


    public static DeferredItem<SwordItem> register(String name, Supplier<SwordItem> supplier) {
        return ITEMS.register(name, supplier);
    }
    public static DeferredItem<SwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, BaseSwordItem.ModifierBuilder modifierBuilder) {
        return register(name, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
    }
    public static DeferredItem<SwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed ,ModRarity rarity, BaseSwordItem.ModifierBuilder modifierBuilder) {
        if(tier == ModTiers.TITANIUM) modifierBuilder.setUnbreakable();
        return register(name, () -> new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, modifierBuilder));
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(sword -> tag.add(sword.get()));
    }

}
