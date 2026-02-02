package org.confluence.terraentity.config;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TEAttributeModifierConfig extends AbstractJsonConfig {

    static volatile TEAttributeModifierConfig INSTANCE;
    public static TEAttributeModifierConfig getInstance() {
        if(INSTANCE == null){
            synchronized (TEAttributeModifierConfig.class) {
                if(INSTANCE == null){
                    INSTANCE = new TEAttributeModifierConfig("attribute_config");
                }
            }
        }
        return INSTANCE;
    }

    Map<EntityType<?>, List<Modifier>> modifiers;

    static MapCodec<Map<EntityType<?>, List<Modifier>>> CODEC =
            Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), Codec.list(Modifier.CODEC)).fieldOf("settings");


    record Modifier(Holder<Attribute> attribute, double amount){
        public static Codec<Modifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute),
                Codec.DOUBLE.fieldOf("amount").forGetter(Modifier::amount)
        ).apply(instance, Modifier::new));
    }

    protected TEAttributeModifierConfig(String name) {
        super(name);
    }

    @Override
    protected JsonObject defaultConfig() {
        JsonObject defaultConfig = new JsonObject();
        defaultConfig.addProperty("comment", "This is extension attribute modifier config file to modify monsters' attribute easily due to kjs may not work on some monsters. This file only works on Server side when server starts and this has maximum priority over kjs and builtin attribute. Default file will be created if this file is not exist. To disable, just delete all content in 'settings'");
        Map<EntityType<?>, List<Modifier>> defaultModifiers = new HashMap<>();
        defaultModifiers.put(TEMonsterEntities.BASE_BONES.get(),
                List.of(
                        new Modifier(Attributes.MOVEMENT_SPEED, 1.0),
                        new Modifier(Attributes.ATTACK_DAMAGE, 1.0),
                        new Modifier(Attributes.MAX_HEALTH, 1.0)
                ));
//        defaultConfig.add(CODEC.codec().encodeStart(JsonOps.INSTANCE, defaultModifiers).result().get().getAsJsonObject());
        defaultConfig.add("settings", CODEC.codec().encodeStart(JsonOps.INSTANCE, defaultModifiers).result().get().getAsJsonObject().get("settings"));

        return defaultConfig;
    }

    @Override
    protected void initConfig(JsonObject config) {
        var either = CODEC.codec().parse(JsonOps.INSTANCE, config);
        if(either.result().isPresent()){
            modifiers = either.result().get();
        }else{
            modifiers = null;
            TerraEntity.LOGGER.error("Fail to parse attribute_config.json, using empty modifiers. Try delete the file to create default config. Error: {}", either.error());
        }
    }

    public void modify(LivingEntity mob){
        if(modifiers != null && modifiers.containsKey(mob.getType())){
            List<Modifier> modifyList = modifiers.get(mob.getType());
            for(Modifier modifier : modifyList){
                AttributeInstance attributeInstance = mob.getAttribute(modifier.attribute());
                if(attributeInstance != null){
                    attributeInstance.setBaseValue(modifier.amount());
                    if(attributeInstance.getAttribute() == Attributes.MAX_HEALTH){
                        mob.setHealth((float) modifier.amount());
                    }
                }
            }
        }
    }
}
