package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.function.TriFunction;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SingleBooleanComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModEffectStrategies;
import org.confluence.mod.common.item.sword.Boomerang;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;


public class BoomerangItems {

    private static final TriFunction<Integer,Float,Float,Boomerang.BoomerangModifier> NORMAL_BOOMERANG_MODIFIER =
            (forwardTick, flySpeedFactor, backSpeedFactor) ->   new Boomerang.BoomerangModifier().setForwardTick(forwardTick).setFlySpeedFactor(flySpeedFactor).setBackSpeedFactor(backSpeedFactor);
    private static final TriFunction<Integer,Integer,Boomerang.BoomerangModifier,Boomerang.BoomerangModifier> MULTI_BOOMERANG_MODIFIER =
            (cd, count, modifier) ->    modifier.setNotWaitForBack().setCd(cd).setMaxCount(count);


    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);


    public static final DeferredItem<Boomerang> WOOD_BOOMERANG = register("wood_boomerang",3,ModRarity.BLUE,
            new Boomerang.BoomerangModifier()
    );
    public static final DeferredItem<Boomerang> ENCHANTED_BOOMERANG = register("enchanted_boomerang",4.4f,ModRarity.BLUE,
            NORMAL_BOOMERANG_MODIFIER.apply(15,1.55f,1.55f)

    );
    public static final DeferredItem<Boomerang> SHROOMERANG = register("shroomerang",4.4f,ModRarity.BLUE,
            NORMAL_BOOMERANG_MODIFIER.apply(15,1.55f,1.55f)
    );
    public static final DeferredItem<Boomerang> ICE_BOOMERANG = register("ice_boomerang",5.5f,ModRarity.BLUE,
            NORMAL_BOOMERANG_MODIFIER.apply(16,1.6f,1.6f)
                    .setOnHitEffect(ModEffectStrategies.Components.FROST_BURN_BOOMERANG_EFFECT.get())
    );   //50% 的几率造成 3 秒的霜冻。

    public static final DeferredItem<Boomerang> TRIMARANG = register("trimarang",5.5f,ModRarity.ORANGE,
            MULTI_BOOMERANG_MODIFIER.apply(10,3,
                    NORMAL_BOOMERANG_MODIFIER.apply(17,1.85f,1.85f))
    );   // 使用三次

    public static final DeferredItem<Boomerang> FLAMARANG = register("flamarang",12.5f,ModRarity.ORANGE,
            NORMAL_BOOMERANG_MODIFIER.apply(18,1.85f,1.85f)
                    .setOnHitEffect(ModEffectStrategies.Components.HELL_FIRE_EFFECT.get())
    );   //狱炎效果。


    public static final DeferredItem<Boomerang> DEVELOPER_BOOMERANG = register("developer_boomerang",20, ModRarity.MASTER,
            new Boomerang.BoomerangModifier().setNotWaitForBack().setCd(10) // 不需要等待返回，设置cd
                    .setForwardTick(50)
                    .setFlySpeedFactor(1.5f) // 设置向前飞行速度
                    .setBackSpeedFactor(2f) // 设置后退速度
    );


    public static final DeferredItem<Boomerang> BeiDou_BOOMERANG = register("bei_dou_boomerang",10, ModRarity.MASTER,
            MULTI_BOOMERANG_MODIFIER.apply(5, 4,
                    NORMAL_BOOMERANG_MODIFIER.apply(40, 3.0f, 3.0f)
                            .setMaxPenetration(7)
                            .setOnHitEffect(ModEffectStrategies.Components.BEI_DOU_EFFECT.get())
            )
    );

    private static DeferredItem<Boomerang> register(String name, float damage, ModRarity rarity, Boomerang.BoomerangModifier boomerangModifier) {
        return ITEMS.register(name, () -> new Boomerang(damage,boomerangModifier,boomerangModifier.properties.stacksTo(1)
                .component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(ModDataComponentTypes.BOOMERANG_READY, SingleBooleanComponent.TRUE)
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                .component(DataComponents.ATTRIBUTE_MODIFIERS, boomerangModifier.attributeModifiersBuilder.build())));
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(item -> tag.add(item.get()));
    }
}
