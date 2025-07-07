package org.confluence.mod.common.item.enchantment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record SummonItemEntityEffect(Holder<Item> item, LevelBasedValue count) implements EnchantmentEntityEffect {
    public static final MapCodec<SummonItemEntityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(SummonItemEntityEffect::item),
            LevelBasedValue.CODEC.fieldOf("count").orElseGet(() -> LevelBasedValue.constant(1)).forGetter(SummonItemEntityEffect::count)
    ).apply(instance, SummonItemEntityEffect::new));

    public SummonItemEntityEffect(Holder<Item> item) {
        this(item, LevelBasedValue.constant(1));
    }

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin) {
        float calculated = this.count.calculate(enchantmentLevel);
        int count = (int) calculated;
        if (entity.getRandom().nextFloat() < calculated - count) count += 1;
        if (count < 1) return;
        ItemEntity itemEntity = new ItemEntity(level, origin.x, entity.getEyeY(), origin.z, new ItemStack(this.item, count));
        itemEntity.setNoPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    @Override
    public MapCodec<SummonItemEntityEffect> codec() {
        return CODEC;
    }
}
