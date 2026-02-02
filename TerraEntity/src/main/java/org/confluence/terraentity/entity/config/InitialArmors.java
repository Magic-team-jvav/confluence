package org.confluence.terraentity.entity.config;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.data.codec.TECodecs;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.item.ArmorSuit;
import org.confluence.terraentity.init.item.TEArmors;
import org.confluence.terraentity.utils.TEUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 初始化怪物的盔甲，使用{@link org.confluence.terraentity.data.mappeddata.MonsterMappedDatas#MONSTER_ARMOR MonsterMappedDatas数据包}来配置套装权重
 */
public class InitialArmors implements Consumer<LivingEntity> {

    final Map<EntityType<?>, List<InitialArmors.Weight>> weightMap;
    final Map<EntityType<?>, Map<List<Suit>, Float>> weightMapMap;

    public static Codec<InitialArmors> CODEC = Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), InitialArmors.Weight.CODEC.listOf())
            .xmap(InitialArmors::new, InitialArmors::weightMap);

    private Map<EntityType<?>, List<InitialArmors.Weight>> weightMap() {
        return this.weightMap;
    }
    InitialArmors(Map<EntityType<?>, List<InitialArmors.Weight>> weightMap){
        this.weightMap = weightMap;
        this.weightMapMap = new HashMap<>();
        weightMap.forEach((type, weights)->{
            Map<List<Suit>, Float> wt = TEUtils.listToMap(weights.stream());
            weightMapMap.put(type, wt);
        });
    }

    /**
     * 应用随机套装
     * @param entity 实体类型
     */
    @Override
    public void accept(LivingEntity entity) {
        this.getRandom(entity.getType()).accept(entity);
    }

    record Weight(List<Suit> suits, float chance) implements Pair<List<Suit>, Float> {
        static Codec<InitialArmors.Weight> CODEC = RecordCodecBuilder.create(instance-> instance.group(
                Suit.LIST_CODEC.fieldOf("suits").forGetter(ins->ins.suits),
                Codec.FLOAT.fieldOf("chance").forGetter(ins->ins.chance)
        ).apply(instance, InitialArmors.Weight::new));



        static InitialArmors.Weight of(List<InitialArmors.Suit> suits, float chance){
            return new InitialArmors.Weight(suits, chance);
        }
        static InitialArmors.Weight of(Item helmet, Item chestplate, Item leggings, Item boots, float chance){
            return new InitialArmors.Weight(List.of(
                    Suit.of(EquipmentSlot.HEAD, new ItemStack(helmet)),
                    Suit.of(EquipmentSlot.CHEST, new ItemStack(chestplate)),
                    Suit.of(EquipmentSlot.LEGS, new ItemStack(leggings)),
                    Suit.of(EquipmentSlot.FEET, new ItemStack(boots))
            ), chance);
        }
        static InitialArmors.Weight ofSuit(ArmorSuit suit, float chance){
            return new InitialArmors.Weight(List.of(
                    Suit.of(EquipmentSlot.HEAD, new ItemStack(suit.helmet.get())),
                    Suit.of(EquipmentSlot.CHEST, new ItemStack(suit.chestplate.get())),
                    Suit.of(EquipmentSlot.LEGS, new ItemStack(suit.leggings.get())),
                    Suit.of(EquipmentSlot.FEET, new ItemStack(suit.boots.get()))
            ), chance);
        }
        static InitialArmors.Weight empty(float chance){
            return new InitialArmors.Weight(List.of(), chance);
        }

        @Override
        public List<Suit> left() {
            return suits;
        }

        @Override
        public Float right() {
            return chance;
        }
    }

    record Suit(EquipmentSlot slot, ItemStack stack) {
        static Codec<Suit> CODEC = RecordCodecBuilder.create(instance-> instance.group(
                EquipmentSlot.CODEC.fieldOf("slot").forGetter(ins->ins.slot),
                ItemStack.CODEC.fieldOf("item").forGetter(ins->ins.stack)
        ).apply(instance, Suit::new));

        private static final Codec<List<Suit>> _LIST_CODEC = BuiltInRegistries.ITEM.byNameCodec().listOf().flatXmap(
                l -> {
                    if (l.size() != 4) {
                        return DataResult.error(() -> "Suit list must have 4 elements");
                    }
                    return DataResult.success(List.of(
                            Suit.of(EquipmentSlot.HEAD, l.get(0).getDefaultInstance()),
                            Suit.of(EquipmentSlot.CHEST, l.get(1).getDefaultInstance()),
                            Suit.of(EquipmentSlot.LEGS, l.get(2).getDefaultInstance()),
                            Suit.of(EquipmentSlot.FEET, l.get(3).getDefaultInstance())
                    ));
                },
                l -> {
                    if (l.size() != 4) {
                        return DataResult.error(() -> "Suit list must have 4 elements");
                    }
                    return DataResult.success(List.of(
                            l.get(0).stack.getItem(),
                            l.get(1).stack.getItem(),
                            l.get(2).stack.getItem(),
                            l.get(3).stack.getItem()
                    ));
                }
        );

        static Codec<List<Suit>> LIST_CODEC = TECodecs.alternativeCodec(_LIST_CODEC, Codec.list(Suit.CODEC),
                list -> {
                    if (list.size() == 4) {
                        Set<EquipmentSlot> set = list.stream().map(i->i.slot).collect(Collectors.toSet());
                        if(set.contains(EquipmentSlot.HEAD) && set.contains(EquipmentSlot.CHEST) &&
                        set.contains(EquipmentSlot.LEGS) && set.contains(EquipmentSlot.FEET)) {
                            return Either.left(list);
                        }
                    }
                    return Either.right(list);
                }
        );

        static Suit empty = new Suit(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

        public static Suit of(EquipmentSlot equipmentSlot, ItemStack itemStack) {
            return new Suit(equipmentSlot, itemStack);
        }

    }

    /**
     * 按权重选择套装
     * @param type 实体类型
     * @return 盔甲套装
     */
    SuitList getRandom(EntityType<?> type){
        Map<List<Suit>, Float> map = weightMapMap.get(type);
        if(map  == null){
            return SuitList.empty();
        }
        return SuitList.of(TEUtils.getRandomByWeight(map));
    }

    /**
     * 盔甲套装
     * @param suits
     */
    record SuitList(List<Suit> suits) implements Consumer<LivingEntity> {

        @Override
        public void accept(LivingEntity entity) {
            suits.forEach(suit -> {
                entity.setItemSlot(suit.slot, suit.stack);
            });
        }

        static SuitList of(List<Suit> suits){
            return new SuitList(suits);
        }
        static SuitList empty(){
            return new SuitList(List.of());
        }

    }

    public static InitialArmors getDefaultParams(){
        return new InitialArmors(ImmutableMap.<EntityType<?>, List<InitialArmors.Weight>>builder()
                .put(TEMonsterEntities.POSSESS_ARMOR.get(), List.of(
                        InitialArmors.Weight.ofSuit(TEArmors.POSSESSED_ARMOR, 0.1f)
                ))
                .put(TEMonsterEntities.WRAITH.get(), List.of(
                        InitialArmors.Weight.ofSuit(TEArmors.WRAITH_ARMOR, 0.1f)
                ))
                .build());
    }


}
