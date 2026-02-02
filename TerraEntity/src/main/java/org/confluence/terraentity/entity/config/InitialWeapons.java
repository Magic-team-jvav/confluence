package org.confluence.terraentity.entity.config;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.utils.TEUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitialWeapons {
    final Map<EntityType<?>, List<Weight>> weightMap;
    final Map<EntityType<?>, Map<ItemStack, Float>> weightMapMap;
    InitialWeapons(Map<EntityType<?>, List<Weight>> weightMap){
        this.weightMap = weightMap;
        this.weightMapMap = new HashMap<>();
        weightMap.forEach((type, weights)->{
            Map<ItemStack, Float> wt = new HashMap<>();
            weightMapMap.put(type, wt);
            weights.forEach(l-> wt.put(l.stack, l.chance));
        });
    }

    record Weight(ItemStack stack, float chance){
        static Codec<Weight> CODEC = RecordCodecBuilder.create(instance-> instance.group(
                ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(ins->ins.stack),
                Codec.FLOAT.fieldOf("chance").forGetter(ins->ins.chance)
        ).apply(instance, Weight::new));

        static Weight of(Item item, float chance){
            return new Weight(item.getDefaultInstance(), chance);
        }
        static Weight empty(float chance){
            return new Weight(ItemStack.EMPTY, chance);
        }
    }

    public static Codec<InitialWeapons> CODEC = Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), Weight.CODEC.listOf())
            .xmap(InitialWeapons::new, InitialWeapons::weightMap);

    private Map<EntityType<?>, List<Weight>> weightMap() {
        return this.weightMap;
    }

    public static InitialWeapons getDefaultParams(){
        return new InitialWeapons(ImmutableMap.<EntityType<?>, List<Weight>>builder()
                .put(TENpcEntities.GUIDE.get(), List.of(
                        Weight.of(Items.BOW, 3),
                        Weight.empty(3)
                ))
                .build());
    }

    public ItemStack getRandom(EntityType<?> type){
        Map<ItemStack, Float> map = weightMapMap.get(type);
        if(map  == null){
            return ItemStack.EMPTY;
        }
        return TEUtils.getRandomByWeight(map);
    }
}
