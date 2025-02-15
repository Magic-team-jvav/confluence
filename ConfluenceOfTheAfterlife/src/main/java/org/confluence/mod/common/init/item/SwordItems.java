package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffectStrategies;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.common.item.sword.LightSaber;
import org.confluence.mod.common.item.sword.stagedy.InventoryTickStrategy;
import org.confluence.mod.common.item.sword.stagedy.SwordPrefabs;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.confluence.mod.common.init.ModEffectStrategies.*;
import static org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy.*;
import static org.confluence.mod.common.item.sword.stagedy.SwordPrefabs.*;



public class SwordItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 普通短剑
    public static final DeferredItem<SwordItem> COPPER_SHORT_SWORD = register("copper_short_sword", ModTiers.COPPER, 0, -1, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> TIN_SHORT_SWORD = register("tin_short_sword", ModTiers.TIN, 0, -1, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> IRON_SHORT_SWORD = register("iron_short_sword", ModTiers.IRON, 1, -1, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> LEAD_SHORT_SWORD = register("lead_short_sword", ModTiers.LEAD, 1, -1, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> SILVER_SHORT_SWORD = register("silver_short_sword", ModTiers.SILVER, 1, -1, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> TUNGSTEN_SHORT_SWORD = register("tungsten_short_sword", ModTiers.TUNGSTEN, 2, -1, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> GOLDEN_SHORT_SWORD = register("golden_short_sword", ModTiers.GOLD, 2, -1, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> PLATINUM_SHORT_SWORD = register("platinum_short_sword", Tiers.DIAMOND, 3, -1, SHORT_SWORD.get());
    // 初始速度4，攻击2

    public static final DeferredItem<SwordItem> BREATHING_REED = register("breathing_reed", ModTiers.TITANIUM, 0, -2.4F, SHORT_SWORD.get());
    public static final DeferredItem<SwordItem> UMBRELLA = register("umbrella", ModTiers.TITANIUM, 0, -2.4F,ModRarity.BLUE, UMBRELLA_SWORD.get());
    public static final DeferredItem<SwordItem> TRAGIC_UMBRELLA = register("tragic_umbrella", ModTiers.TITANIUM, 0, -2.4F,ModRarity.BLUE, UMBRELLA_SWORD.get());


    //普通宽剑 默认横扫*1.5
    public static final DeferredItem<SwordItem> CACTUS_SWORD = register("cactus_sword", ModTiers.CACTUS, 3, -2.4F,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> COPPER_BOARD_SWORD = register("copper_board_sword", ModTiers.COPPER, 3, -2.4F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> TIN_BOARD_SWORD = register("tin_board_sword", ModTiers.TIN, 3, -2.4F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> LEAD_BOARD_SWORD = register("lead_board_sword", ModTiers.LEAD, 3, -2.4F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> SILVER_BOARD_SWORD = register("silver_board_sword", ModTiers.SILVER, 3, -2.4F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> TUNGSTEN_BOARD_SWORD = register("tungsten_board_sword", ModTiers.TUNGSTEN, 3, -2.4F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> GOLDEN_BOARD_SWORD = register("golden_board_sword", ModTiers.GOLD, 3, -2.4F, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> PLATINUM_BOARD_SWORD = register("platinum_board_sword", ModTiers.PLATINUM, 4, -2.4F, NORMAL_SWORD.get());

    //tip 注册剑的特殊功能只需修改最后一个参数即可，只需要把 NORMAL_SWORD替换成prefab的其他预制效果，还可以追加效果
    public static final DeferredItem<SwordItem> FAKE_SWORD = register("fake_sword",ModTiers.CANDY_CANE, 0, -2.4F, ModRarity.GRAY, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> CANDY_CANE_SWORD = register("candy_cane_sword",ModTiers.CANDY_CANE,2,-1.4f,NORMAL_SWORD.get());

    public static final DeferredItem<SwordItem> FALCON_BLADE = register("falcon_blade",ModTiers.TITANIUM, 3, -1.45F, ModRarity.BLUE, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> ZOMBIE_ARM = register("zombie_arm",ModTiers.TITANIUM,3,-1.4f,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> MANDIBLE_BLADE = register("mandible_blade", ModTiers.TITANIUM, 4,-1.4f,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> BONE_SWORD = register("bone_sword",ModTiers.TITANIUM, 5, -1.1F, ModRarity.ORANGE, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> PURPLE_CLUBBERFISH = register("purple_clubberfish", ModTiers.TITANIUM, 10,-3.5f,NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> STYLISH_SCISSORS = register("stylish_scissors",ModTiers.TITANIUM, 3, -1.2f, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> EXOTIC_SCIMITAR = register("exotic_scimitar",ModTiers.TITANIUM, 5, -1.1f, ModRarity.GREEN, NORMAL_SWORD.get());
    public static final DeferredItem<SwordItem> KATANA = register("katana",ModTiers.TITANIUM, 4, -0.3f, ModRarity.BLUE, NORMAL_SWORD.get());



    //改横扫大小的宽剑
    public static final DeferredItem<SwordItem> TERRAGRIM = register("terragrim", ModTiers.TITANIUM, -1, 5.0F, ModRarity.ORANGE, BOARD_SWORD.apply(1.2f));

    //效果剑
    public static final DeferredItem<SwordItem> LIGHTS_BANE = register("lights_bane",ModTiers.TITANIUM, 5, -1.0f,
            ModRarity.BLUE,     EFFECT_SWORD.apply(LIGHTS_BANE_EFFECT));
    public static final DeferredItem<SwordItem> BLOOD_BUTCHERER = register("blood_butchere",ModTiers.TITANIUM, 7, -2.7F,
            ModRarity.BLUE,     EFFECT_SWORD.apply(BLOOD_BUTCHERED_EFFECT));
    public static final DeferredItem<SwordItem> VOLCANO = register("volcano",ModTiers.TITANIUM, 13, -3F,
            ModRarity.ORANGE,   EFFECT_SWORD.apply(HELL_FIRE_EFFECT).addOnHitEffect(SET_FIRE_EFFECT)
                    .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 2f, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, 0.5f, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final DeferredItem<SwordItem> BAT_BAT = register("bat_bat", ModTiers.TITANIUM,12,-3.7f,
            ModRarity.ORANGE,   EFFECT_SWORD.apply(BAT_FANG_EFFECT));
    public static final DeferredItem<SwordItem> TENTACLE_MACE = register("tentacle_mace",ModTiers.TITANIUM, 5, -1.4F,
            ModRarity.GREEN,    EFFECT_SWORD.apply(TENTACLE_SPIKES_EFFECT));



    //弹幕剑
    public static final DeferredItem<SwordItem> ICE_BLADE = register("ice_blade",ModTiers.TITANIUM, 5, -1.0F,
            ModRarity.BLUE,     PROJ_SWORD.apply(ICE_PROJ));
    public static final DeferredItem<SwordItem>STARFURY = register("starfury",ModTiers.TITANIUM, 6, -1.1F,
            ModRarity.GREEN,    PROJ_SWORD.apply(STAR_FURY_PROJ));
    public static final DeferredItem<SwordItem> ENCHANTED_SWORD = register("enchanted_sword",ModTiers.TITANIUM, 7, -1.1F,
            ModRarity.ORANGE,   PROJ_SWORD.apply(ENCHANTED_SWORD_PROJ));

    // 光剑
    public static final DeferredItem<SwordItem> RED_LIGHT_SABER = register("red_light_saber", ()->new LightSaber.Red(ModTiers.TITANIUM, ModRarity.BLUE,10, -2));
    public static final DeferredItem<SwordItem> ORANGE_LIGHT_SABER = register("orange_light_saber", ()->new LightSaber.Orange(ModTiers.TITANIUM, ModRarity.BLUE,10, -2));
    public static final DeferredItem<SwordItem> YELLOW_LIGHT_SABER = register("yellow_light_saber", ()->new LightSaber.Yellow(ModTiers.TITANIUM, ModRarity.BLUE,10, -2));
    public static final DeferredItem<SwordItem> GREEN_LIGHT_SABER = register("green_light_saber", ()->new LightSaber.Green(ModTiers.TITANIUM, ModRarity.BLUE,10, -2));
    public static final DeferredItem<SwordItem> BLUE_LIGHT_SABER = register("blue_light_saber", ()->new LightSaber.Blue(ModTiers.TITANIUM, ModRarity.BLUE,10, -2));
    public static final DeferredItem<SwordItem> PURPLE_LIGHT_SABER = register("purple_light_saber", ()->new LightSaber.Purple(ModTiers.TITANIUM, ModRarity.BLUE,10, -2));
    public static final DeferredItem<SwordItem> WHITE_LIGHT_SABER = register("white_light_saber", ()->new LightSaber.White(ModTiers.TITANIUM, ModRarity.BLUE,10, -2));


    // 特殊剑
    public static final DeferredItem<SwordItem> CROWBAR = register("crowbar",ModTiers.TITANIUM, 16, -1.0F,ModRarity.MASTER, BOARD_SWORD.apply(2.0f));
    public static final DeferredItem<SwordItem> DEVELOPER_SWORD = register("developer_sword",ModTiers.TITANIUM, 20, 20F, ModRarity.MASTER,
            Stream.of(
            SwordPrefabs.BOARD_SWORD.apply(10.0f)                                //宽剑
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,1.5f,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE)        //手持属性加成
//                    .addOnHitEffect(UNDEFINED)                //命中效果
                    .setProj(ENCHANTED_SWORD_PROJ)                                  //弹幕
                    .setInventoryTick(InventoryTickStrategy.INVINCIBLE)             //背包每刻效果
            ).peek(it-> EFFECT_STRATEGY.getEntries().stream().toList().forEach(it::addOnHitEffect)).findFirst().get()
    );

    public static DeferredItem<SwordItem> register(String name, Supplier<SwordItem> supplier) {
        return ITEMS.register(name, supplier);
    }
    public static DeferredItem<SwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed, BaseSwordItem.ModifierBuilder modifierBuilder) {
        return register(name, tier, rawDamage, rawSpeed, ModRarity.WHITE, modifierBuilder);
    }
    public static DeferredItem<SwordItem> register(String name, Tier tier, int rawDamage, float rawSpeed ,ModRarity rarity, BaseSwordItem.ModifierBuilder modifierBuilder) {
        if(tier == ModTiers.TITANIUM) {
            modifierBuilder.modifyProperties(p->p.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
        }
        return register(name, () -> new BaseSwordItem(tier, rarity, rawDamage, rawSpeed, modifierBuilder));
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }

}
