package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.lib.util.LibUtils;

public record ExtractinatorData(ResourceKey<LootTable> lootTable) {
    public static final Codec<ExtractinatorData> CODEC = ResourceKey.codec(Registries.LOOT_TABLE).xmap(ExtractinatorData::new, ExtractinatorData::lootTable);

    public static void extract(Level level, BlockPos pos, Player player, InteractionHand hand, ServerLevel serverLevel, ItemStack itemStack, ExtractinatorData data) {
        ParticleOptions options = itemStack.getItem() instanceof BlockItem blockItem
                ? new BlockParticleOption(ParticleTypes.BLOCK, blockItem.getBlock().defaultBlockState())
                : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
        serverLevel.sendParticles(options, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, 100, 0F, 0.0625F, 0F, 0.25F);

        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(data.lootTable());
        LootParams lootparams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.GIFT);
        for (ItemStack loot : lootTable.getRandomItems(lootparams)) {
            LibUtils.createItemEntity(loot, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, level, 40);
        }

        itemStack.shrink(1);
        if (itemStack.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
    }
}
