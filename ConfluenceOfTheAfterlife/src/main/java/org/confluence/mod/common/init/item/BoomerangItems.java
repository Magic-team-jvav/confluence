package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.lang3.function.TriFunction;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.sword.Boomerang;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

import static org.confluence.mod.common.item.sword.stagedy.EffectStrategy.SET_FIRE;
import static org.confluence.mod.common.item.sword.stagedy.EffectStrategy.TIME_POSSIBILITY_EFFECT;

public class BoomerangItems {

    private static final TriFunction<Integer,Float,Float,Boomerang.BoomerangModifier> NORMAL_BOOMERANG_MODIFIER =
            (forwardTick, flySpeedFactor, backSpeedFactor) ->   new Boomerang.BoomerangModifier().setForwardTick(forwardTick).setFlySpeedFactor(flySpeedFactor).setBackSpeedFactor(backSpeedFactor);
    private static final TriFunction<Integer,Integer,Boomerang.BoomerangModifier,Boomerang.BoomerangModifier> MULTI_BOOMERANG_MODIFIER =
            (cd, count, modifier) ->    modifier.setNotWaitForBack().setCd(cd).setMaxCount(count);


    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);




    public static DeferredItem<Boomerang> WOOD_BOOMERANG = register("wood_boomerang",3,640,ModRarity.BLUE,
            new Boomerang.BoomerangModifier()
    );
    public static DeferredItem<Boomerang> ENCHANTED_BOOMERANG = register("enchanted_boomerang",4.4f,9999,ModRarity.BLUE,
            NORMAL_BOOMERANG_MODIFIER.apply(15,1.55f,1.55f)

    );
    public static DeferredItem<Boomerang> SHROOMERANG = register("shroomerang",4.4f,9999,ModRarity.BLUE,
            NORMAL_BOOMERANG_MODIFIER.apply(15,1.55f,1.55f)
    );
    public static DeferredItem<Boomerang> ICE_BOOMERANG = register("ice_boomerang",5.5f,9999,ModRarity.BLUE,
            NORMAL_BOOMERANG_MODIFIER.apply(16,1.6f,1.6f)
                    .addOnHitEffect(TIME_POSSIBILITY_EFFECT.apply(ModEffects.FROST_BURN,3 * 20,0.5F))
    );   //50% 的几率造成 3 秒的霜冻。

    public static DeferredItem<Boomerang> TRIMARANG = register("trimarang",5.5f,9999,ModRarity.ORANGE,
            MULTI_BOOMERANG_MODIFIER.apply(10,3,
                    NORMAL_BOOMERANG_MODIFIER.apply(17,1.85f,1.85f))
    );   // 使用三次

    public static DeferredItem<Boomerang> FLAMARANG = register("flamarang",12.5f,9999,ModRarity.ORANGE,
            NORMAL_BOOMERANG_MODIFIER.apply(18,1.85f,1.85f)
                    .addOnHitEffect(SET_FIRE.apply(5 * 20,0.5f))
    );   //50% 的几率造成 5 秒的火焰。



    public static DeferredItem<Boomerang> DEVELOPER_BOOMERANG = register("developer_boomerang",20,100,ModRarity.EPIC,
            new Boomerang.BoomerangModifier().setNotWaitForBack().setCd(10) // 不需要等待返回，设置cd
                    .setForwardTick(50).setFlySpeedFactor(1.5f) // 设置向前飞行速度
                    .setBackSpeedFactor(2f) // 设置后退速度
    );

    public static DeferredItem<Boomerang> register(String name, Supplier<Boomerang> supplier) {
        return ITEMS.register(name, supplier);
    }
    public static DeferredItem<Boomerang> register(String name,float damage,  int durability, Boomerang.BoomerangModifier boomerangModifier) {
        return ITEMS.register(name, () -> new Boomerang(damage, durability,ModRarity.WHITE, boomerangModifier));
    }
    public static DeferredItem<Boomerang> register(String name,float damage,  int durability, ModRarity rarity, Boomerang.BoomerangModifier boomerangModifier) {
        return ITEMS.register(name, () -> new Boomerang(damage,durability,rarity, boomerangModifier));
    }


}
