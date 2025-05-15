package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.map.TreasureBagDrop;
import org.confluence.mod.common.entity.TreasureBagItemEntity;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class TreasureBagItem extends CustomRarityItem {
    private final ResourceLocation lootTable;
    private final BiFunction<ServerLevel, BlockPos, String> suffix;

    public TreasureBagItem(ResourceLocation lootTable, BiFunction<ServerLevel, BlockPos, String> suffix) {
        super(new Properties().fireResistant(), ModRarity.EXPERT);
        this.lootTable = lootTable;
        this.suffix = suffix;
    }

    public TreasureBagItem(ResourceLocation lootTable) {
        this(lootTable, (level, pos) -> LibUtils.switchByDifficulty(level, pos, "/classic", "/expert", "/master"));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            LootParams lootparams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withLuck(player.getLuck())
                    .create(LootContextParamSets.GIFT);
            String string = LibUtils.getItemStackNbt(itemStack).getString("lootTable");
            if (string.isEmpty() && player.isCreative()) {
                string = lootTable.withSuffix(suffix.apply(serverLevel, player.blockPosition())).toString();
            }
            ResourceLocation table = ResourceLocation.parse(string);
            LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, table));
            int count = 1;
            if (player.isCrouching()) count = itemStack.getCount();
            for (int i = 0; i < count; i++) {
                for (ItemStack loot : loottable.getRandomItems(lootparams)) {
                    if (!player.addItem(loot)) player.drop(loot, false, true);
                }
            }
            itemStack.shrink(count);
        } else {
            player.playSound(ModSoundEvents.TERRA_OPERATION.get(), 0.5F, 1.0F);
        }
        return InteractionResultHolder.success(itemStack);
    }

    public static @Nullable ItemStack getTreasureBag(LivingEntity living) {
        if (!(living.level() instanceof ServerLevel serverLevel)) return null;
        EntityType<?> type = living.getType();
        TreasureBagDrop data = type.builtInRegistryHolder().getData(ModDataMaps.TREASURE_BAG);
        if (data == null || !(data.item() instanceof TreasureBagItem item)) return null;
        ItemStack itemStack = item.getDefaultInstance();
        String lootTable = item.lootTable.withSuffix(item.suffix.apply(serverLevel, living.blockPosition())).toString();
        LibUtils.updateItemStackNbt(itemStack, tag -> tag.putString("lootTable", lootTable));
        return itemStack;
    }

    public static void createItemEntity(LivingEntity living, ServerPlayer owner) {
        ItemStack itemStack = getTreasureBag(living);
        if (itemStack != null) {
            living.level().addFreshEntity(new TreasureBagItemEntity(living.level(), living.position(), itemStack, owner));
        }
    }
}
