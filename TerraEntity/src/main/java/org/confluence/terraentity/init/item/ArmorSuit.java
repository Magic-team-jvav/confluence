package org.confluence.terraentity.init.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.item.BaseArmorItem;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorSuit {

    public DeferredItem<BaseArmorItem> helmet;
    public DeferredItem<BaseArmorItem> chestplate;
    public DeferredItem<BaseArmorItem> leggings;
    public DeferredItem<BaseArmorItem> boots;

    protected ArmorSuit(DeferredRegister.Items registry, String name, Supplier<ArmorMaterial> material, int[] durability){
        this.init(registry, name, material, durability);
    }

    protected void init(DeferredRegister.Items registry, String name, Supplier<ArmorMaterial> material, int[] durability){
        Holder<ArmorMaterial> materialHolder = Holder.direct(material.get());
        this.registerArmors(registry, name, materialHolder, durability);
    }

    protected void registerArmors(DeferredRegister.Items registry, String name, Holder<ArmorMaterial> material, int[] durability){
        helmet = registerNormalArmor(registry, name + "_helmet", name, material, ArmorItem.Type.HELMET, durability[0]);
        chestplate = registerNormalArmor(registry, name + "_chestplate", name, material, ArmorItem.Type.CHESTPLATE, durability[1]);
        leggings = registerNormalArmor(registry, name + "_leggings", name, material, ArmorItem.Type.LEGGINGS, durability[2]);
        boots = registerNormalArmor(registry, name + "_boots", name, material, ArmorItem.Type.BOOTS, durability[3]);
    }

    /**
     * 直接创建一套盔甲，不注册材料
     * @param name id
     * @param sound 穿上盔甲的声音
     * @param material 修复材料
     * @param defense 防御力
     * @param durability 耐久度
     * @param enchantValue 附魔值
     * @param toughness 韧性
     * @param knockbackResistance 击退抗性
     */
    public static ArmorSuit create(DeferredRegister.Items registry, String name,
                                   Holder<SoundEvent> sound, Supplier<Ingredient> material,
                                   int[] defense, int[] durability, int enchantValue,  float toughness, float knockbackResistance
    ){
        return new ArmorSuit(registry, name,
                ()->createArmorMaterial(defense, enchantValue, sound, material,name, toughness,knockbackResistance),
                durability
        );
    }

    /**
     * 使用单个物品作为修复材料创建一套盔甲，不注册材料
     * @param name id
     * @param sound 穿上盔甲的声音
     * @param material 修复材料
     * @param defense 防御力
     * @param durability 耐久度
     * @param enchantValue 附魔值
     * @param toughness 韧性
     * @param knockbackResistance 击退抗性
     */
    public static ArmorSuit createSimple(DeferredRegister.Items registry, String name,
                                         Holder<SoundEvent> sound, Supplier<Item> material,
                                         int[] defense, int[] durability, int enchantValue, float toughness, float knockbackResistance
    ){
        return create(registry,name,sound,()->Ingredient.of(material.get()),defense,durability,enchantValue, toughness,knockbackResistance);
    }


    /**
     * 创建一套盔甲，并注册材料
     * @param name id
     * @param sound 穿上盔甲的声音
     * @param material 修复材料
     * @param defense 防御力
     * @param durability 耐久度
     * @param enchantValue 附魔值
     * @param toughness 韧性
     * @param knockbackResistance 击退抗性
     */
    public static MaterialArmorSuit createWithMaterial(DeferredRegister.Items registry, String name, DeferredRegister<ArmorMaterial> armorMaterialRegister,
                                                       Holder<SoundEvent> sound, Supplier<Ingredient> material,
                                                       int[] defense, int[] durability, int enchantValue,  float toughness, float knockbackResistance
    ){
        return new MaterialArmorSuit(registry, name, armorMaterialRegister,
                () -> createArmorMaterial(defense, enchantValue, sound, material,name, toughness,knockbackResistance),
                durability
        );
    }

    /**
     * 使用单个物品作为修复材料创建一套盔甲，并注册材料
     * @param name id
     * @param sound 穿上盔甲的声音
     * @param material 修复材料
     * @param defense 防御力
     * @param durability 耐久度
     * @param enchantValue 附魔值
     * @param toughness 韧性
     * @param knockbackResistance 击退抗性
     */
    public static MaterialArmorSuit createSimpleWithMaterial(DeferredRegister.Items registry, String name, DeferredRegister<ArmorMaterial> armorMaterialRegister,
                                                             Holder<SoundEvent> sound, Supplier<Item> material,
                                                             int[] defense, int[] durability, int enchantValue, float toughness, float knockbackResistance
    ){
        return createWithMaterial(registry,name,armorMaterialRegister,sound,()->Ingredient.of(material.get()),defense,durability,enchantValue, toughness,knockbackResistance);
    }

    protected static DeferredItem<BaseArmorItem> registerNormalArmor(DeferredRegister.Items registry, String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type, int durability) {
        return registry.register(name, () -> new BaseArmorItem("armor/" + geoName, material, type, new Item.Properties().stacksTo(1).durability(durability)));
    }

    protected static DeferredItem<BaseArmorItem> registerNormalArmor(DeferredRegister.Items registry, String name, String geoName, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return registry.register(name, () -> new BaseArmorItem("armor/" + geoName, material, type, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, new Unbreakable(true))));
    }

    protected static DeferredItem<BaseArmorItem> registerNormalArmor(DeferredRegister.Items registry, String name, String geoName, ModRarity rarity, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        return registry.register(name, () -> new BaseArmorItem("armor/" + geoName, rarity, material, type, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, new Unbreakable(true))));
    }

    protected static ArmorMaterial createArmorMaterial(int[] defense,
                                             int enchantValue,
                                             Holder<SoundEvent> sound,
                                             Supplier<Ingredient> material,
                                             String layersName,
                                             float toughness,
                                             float knockbackResistance){
        return new ArmorMaterial(Map.of(
                ArmorItem.Type.HELMET, defense[0],
                ArmorItem.Type.CHESTPLATE, defense[1],
                ArmorItem.Type.LEGGINGS, defense[2],
                ArmorItem.Type.BOOTS, defense[3]),
                enchantValue, sound, material,
                Collections.singletonList(new ArmorMaterial.Layer(TerraEntity.space(layersName))),
                toughness, knockbackResistance);
    }
}
