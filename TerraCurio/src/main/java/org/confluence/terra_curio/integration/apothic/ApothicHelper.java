package org.confluence.terra_curio.integration.apothic;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.fml.ModList;
import org.confluence.terra_curio.common.init.TCAttributes;

import java.util.Map;

public class ApothicHelper {
    public static final String ATTRIBUTES_ID = "apothic_attributes";
    public static final boolean LOADED = ModList.get().isLoaded(ATTRIBUTES_ID);
    public static final ResourceLocation CRIT_CHANCE = ResourceLocation.fromNamespaceAndPath(ATTRIBUTES_ID, "crit_chance");
    public static final ResourceLocation ARROW_VELOCITY = ResourceLocation.fromNamespaceAndPath(ATTRIBUTES_ID, "arrow_velocity");
    public static final ResourceLocation ARROW_DAMAGE = ResourceLocation.fromNamespaceAndPath(ATTRIBUTES_ID, "arrow_damage");
    public static final ResourceLocation DODGE_CHANCE = ResourceLocation.fromNamespaceAndPath(ATTRIBUTES_ID, "dodge_chance");
    public static final ResourceLocation ARMOR_PIERCE = ResourceLocation.fromNamespaceAndPath(ATTRIBUTES_ID, "armor_pierce");

    public static void preset(Map<Holder<Attribute>, Holder<Attribute>> map) {
        if (LOADED) {
            map.put(TCAttributes.CRIT_CHANCE, BuiltInRegistries.ATTRIBUTE.getHolder(ApothicHelper.CRIT_CHANCE).orElse(null));
            map.put(TCAttributes.RANGED_VELOCITY, BuiltInRegistries.ATTRIBUTE.getHolder(ApothicHelper.ARROW_VELOCITY).orElse(null));
            map.put(TCAttributes.RANGED_DAMAGE, BuiltInRegistries.ATTRIBUTE.getHolder(ApothicHelper.ARROW_DAMAGE).orElse(null));
            map.put(TCAttributes.DODGE_CHANCE, BuiltInRegistries.ATTRIBUTE.getHolder(ApothicHelper.DODGE_CHANCE).orElse(null));
            map.put(TCAttributes.ARMOR_PENETRATION, BuiltInRegistries.ATTRIBUTE.getHolder(ApothicHelper.ARMOR_PIERCE).orElse(null));
        }
    }
}
