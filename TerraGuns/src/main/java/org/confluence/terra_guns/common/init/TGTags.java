package org.confluence.terra_guns.common.init;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.confluence.terra_guns.TerraGuns;

public class TGTags {
    public static final TagKey<Item> AMMO = ItemTags.create(TerraGuns.asResource("ammo"));

    public static final TagKey<Item> GUN = ItemTags.create(TerraGuns.asResource("gun"));
    public static final TagKey<Item> MANUAL_GUN = ItemTags.create(TerraGuns.asResource("manual_gun"));
    public static final TagKey<Item> AUTOMATIC_GUN = ItemTags.create(TerraGuns.asResource("automatic_gun"));
    public static final TagKey<Item> SEED_AMMO = ItemTags.create(TerraGuns.asResource("seed_ammo"));
    public static final TagKey<Item> SNOW_AMMO = ItemTags.create(TerraGuns.asResource("snow_ammo"));
    public static final TagKey<Item> BULLET = ItemTags.create(TerraGuns.asResource("bullet")); // 用于军火商入住
}
