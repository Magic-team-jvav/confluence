package org.confluence.mod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.EffectSwitchableCheckEvent;
import org.confluence.mod.common.block.common.AetheriumCauldronBlock;
import org.confluence.mod.common.block.common.HoneyCauldronBlock;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.gameevent.SlimeRainGameEvent;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class ModUtils {
    public static final Set<String> CONFLUENCE_NAMESPACES = Set.of(Confluence.MODID, TerraCurio.MODID, TerraEntity.MODID, TerraGuns.MODID);

    public static void dropMoney(int amount, double x, double y, double z, Level level) {
        int copper_count = amount % UPGRADES_COUNT;
        int i = ((amount - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        LibUtils.createItemEntity(ModItems.COPPER_COIN.get(), copper_count, x, y, z, level, 0);
        LibUtils.createItemEntity(ModItems.SILVER_COIN.get(), silver_count, x, y, z, level, 0);
        LibUtils.createItemEntity(ModItems.GOLD_COIN.get(), golden_count, x, y, z, level, 0);
        LibUtils.createItemEntity(ModItems.PLATINUM_COIN.get(), k, x, y, z, level, 0);
    }

    public static void dropMoney(long amount, double x, double y, double z, Level level) {
        while (amount > 0x3F3F3F3F) {
            dropMoney(0x3F3F3F3F, x, y, z, level);
            amount -= 0x3F3F3F3F;
        }
        dropMoney((int) amount, x, y, z, level);
    }

    public static <T> boolean isFromConfluence(Registry<T> registry, T obj) {
        ResourceLocation key = registry.getKey(obj);
        return key != null && CONFLUENCE_NAMESPACES.contains(key.getNamespace());
    }

    public static boolean isWaterBottle(ItemStack itemStack) {
        return itemStack.is(PotionItems.BOTTLED_WATER) || itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
    }

    public static void summonBoss(ServerLevel level, BlockPos pos, AbstractTerraBossBase boss, boolean onSurface) {
        double x = pos.getX() + 0.5 + Mth.randomBetweenInclusive(level.random, -50, 50);
        double z = pos.getZ() + 0.5 + Mth.randomBetweenInclusive(level.random, -50, 50);
        double y = (onSurface ? level.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z)) : pos.getY()) + 0.5;
        if (Math.abs(pos.getY() - y) > 50) {
            y = pos.getY();
        }
        boss.setPos(x, y, z);
        if (TEUtils.internalSpawnEntity(boss, level)) {
            level.addFreshEntityWithPassengers(boss);
        }
    }

    public static void summonBoss(ServerLevel level, BlockPos pos, AbstractTerraBossBase boss) {
        summonBoss(level, pos, boss, true);
    }

    public static @Nullable BlockState getLeadAnvilDamage(BlockState state, DirectionProperty FACING) {
        Block block = state.getBlock();
        if (block == FunctionalBlocks.LEAD_ANVIL.get()) {
            return FunctionalBlocks.CHIPPED_LEAD_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING));
        } else if (block == FunctionalBlocks.CHIPPED_LEAD_ANVIL.get()) {
            return FunctionalBlocks.DAMAGED_LEAD_ANVIL.get().defaultBlockState().setValue(FACING, state.getValue(FACING));
        } else if (block == FunctionalBlocks.DAMAGED_LEAD_ANVIL.get()) {
            return null;
        }
        return state;
    }

    public static void bossDeath(ServerLevel level, LivingEntity living) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;

        EntityType<?> type = living.getType();
        KillBoard.INSTANCE.defeat(type);
        boolean isEaterOfWorlds = type == TEBossEntities.EATER_OF_WORLDS.get();
        if (isEaterOfWorlds || type == TEBossEntities.BRAIN_OF_CTHULHU.get()) {
            if (LibDateUtils.isWithinDayTime(LibDateUtils._00$00, LibDateUtils._04$30, level)) {
                MeteoriteTracker.INSTANCE.spawnAtNextNight = true;
            } else if (!MeteoriteTracker.INSTANCE.spawnAtNextNight) {
                MeteoriteTracker.INSTANCE.spawnAtNextNight = level.random.nextBoolean();
            }
        }
        boolean stickySituation = type == TEBossEntities.KING_SLIME.get() && SlimeRainGameEvent.INSTANCE.started();
        boolean is$WallOrHill$OfFlesh = type == TEBossEntities.WALL_OF_FLESH.get() || type == TEBossEntities.HILL_OF_FLESH.get();
        ResourceKey<Level> dimension = living.level().dimension();
        level.players().stream().filter(player -> player.level().dimension() == dimension).forEach(player -> {
            TreasureBagItem.createItemEntity(living, player);
            if (isEaterOfWorlds) {
                AchievementUtils.awardAchievement(player, "worm_fodder");
            } else if (stickySituation) {
                AchievementUtils.awardAchievement(player, "sticky_situation");
            } else if (is$WallOrHill$OfFlesh) {
                AchievementUtils.awardAchievement(player, "still_hungry");
            }
        });
    }

    public static void enemyDropMoney(LivingEntity living, ServerLevel level) {
        double amount = getLivingBaseMoneyDrops(living, level);

        if (living.hasEffect(ModEffects.MIDAS)) {
            amount *= Mth.nextDouble(living.getRandom(), 1.1, 1.49);
        }
        if (IMinecraftServer.isHardmode(level.getServer())) {
            amount *= 1.6;
        }
        if (KillBoard.INSTANCE.getGamePhase().isAtLeast(GamePhase.PLANTERA)) {
            amount *= 1.5;
        }

        dropMoney((int) amount, living.getX(), living.getEyeY() - 0.3, living.getZ(), level);
    }

    public static double getLivingBaseMoneyDrops(LivingEntity living, Level level) {
        AttributeInstance attack = living.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        AttributeInstance knockbackResistance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        double healthFactor = living.getMaxHealth() * 0.15;
        double attackFactor = attack == null ? 0.0 : attack.getValue() * 0.25;
        double armorFactor = armor == null ? 0.0 : armor.getValue() * 0.1;
        double knockbackResistanceFactor = knockbackResistance == null ? 10.0 : (1.0 + knockbackResistance.getValue()) * 10.0;
        double difficultyFactor = level.getCurrentDifficultyAt(living.blockPosition()).getEffectiveDifficulty() * 0.5;
        return Math.min(Math.round((healthFactor + attackFactor + armorFactor + knockbackResistanceFactor) * difficultyFactor) * 7.0, 100000);
    }

    public static void applyBrainOfCthulhuDebuff(ServerLevel level, @Nullable Entity attacker, LivingEntity living) {
        if (attacker != null && LibUtils.isAtLeastExpert(level, living.blockPosition())) {
            EntityType<?> type = attacker.getType();
            if (type == TEMonsterEntities.VISUAL_NEURON.get() || (type == TEBossEntities.BRAIN_OF_CTHULHU.get() && attacker.getRandom().nextFloat() < 0.3333F)) {
                boolean master = LibUtils.isMaster(level, living.blockPosition());
                Holder<MobEffect> debuff;
                float min;
                int i = attacker.getRandom().nextInt(81);
                if (i < 11) {
                    debuff = MobEffects.POISON;
                    min = master ? 6.56F : 5.25F;
                } else if (i < 22) {
                    debuff = MobEffects.BLINDNESS;
                    min = master ? 3.75F : 3.0F;
                } else if (i < 24) {
                    debuff = ModEffects.CURSED;
                    min = master ? 0.94F : 0.75F;
                } else if (i < 35) {
                    debuff = ModEffects.BLEEDING;
                    min = master ? 9.38F : 7.5F;
                } else if (i < 37) {
                    debuff = TCEffects.CONFUSED;
                    min = master ? 1.88F : 1.5F;
                } else if (i < 48) {
                    debuff = MobEffects.MOVEMENT_SLOWDOWN;
                    min = master ? 6.56F : 5.25F;
                } else if (i < 59) {
                    debuff = MobEffects.WEAKNESS;
                    min = master ? 14.06F : 11.25F;
                } else if (i < 70) {
                    debuff = ModEffects.SILENCED;
                    min = master ? 1.88F : 1.5F;
                } else {
                    debuff = ModEffects.BROKEN_ARMOR;
                    min = master ? 12.19F : 9.75F;
                }
                living.addEffect(new MobEffectInstance(debuff, (int) ((attacker.getRandom().nextFloat() * min + min) * 20)));
            }
        }
    }

    public static void applyCursedSkullDebuff(@Nullable Entity attacker, LivingEntity living) {
        if (attacker != null && attacker.getType() == TEMonsterEntities.CURSED_SKULL.get() && attacker.getRandom().nextFloat() < 0.33F) {
            living.addEffect(new MobEffectInstance(ModEffects.CURSED, 80));
        }
    }

    public static boolean canHitEntity(@Nullable Entity target, @Nullable Entity owner) {
        if (target == null || target.isRemoved()) return false; // 有模组把target写成了null
        if (owner == target || !target.isAttackable() || !target.canBeHitByProjectile() || target instanceof ArmorStand)
            return false;
        return owner == null || (!owner.isPassengerOfSameVehicle(target)/* && !target.skipAttackInteraction(owner)*/);
    }

    public static Component formatPrice(int price) {
        int platinum = 0;
        int gold = 0;
        int silver = 0;
        int copper;
        if (price >= 1000000) {
            platinum = price / 1000000;
            price -= platinum * 1000000;
        }
        if (price >= 10000) {
            gold = price / 10000;
            price -= gold * 10000;
        }
        if (price >= 100) {
            silver = price / 100;
            price -= silver * 100;
        }
        copper = price;
        MutableComponent cmp = Component.empty();
        if (platinum > 0)
            cmp.append(Component.literal(platinum + " ").withColor(-4996668)).append(Component.translatable("tooltip.price.platinum").withColor(-4996668));
        if (gold > 0)
            cmp.append(Component.literal(gold + " ").withColor(-3891380)).append(Component.translatable("tooltip.price.gold").withColor(-3891380));
        if (silver > 0)
            cmp.append(Component.literal(silver + " ").withColor(-4532777)).append(Component.translatable("tooltip.price.silver").withColor(-4532777));
        if (copper > 0)
            cmp.append(Component.literal(copper + " ").withColor(-3837899)).append(Component.translatable("tooltip.price.copper").withColor(-3837899));
        return cmp;
    }

    /// 不可破坏物品无法附魔耐久与经验修补
    public static boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        boolean supportedItem = stack.is(enchantment.value().definition().supportedItems());
        if (stack.has(DataComponents.UNBREAKABLE)) {
            return supportedItem && !enchantment.is(Enchantments.UNBREAKING) && !enchantment.is(Enchantments.MENDING);
        }
        return supportedItem;
    }

    /// 由于暮色森林使原版的该方法会访问区块，于是复制一份来用
    ///
    /// @see Level#isRainingAt(BlockPos)
    public static boolean isRainingAt(Level level, BlockPos pos) {
        if (!level.isRaining()) return false;
        if (!level.canSeeSky(pos)) return false;
        if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        return level.getBiome(pos).value().getPrecipitationAt(pos) == Biome.Precipitation.RAIN;
    }

    public static void makeItemAntigravity(ItemEntity entity) {
        if (entity.getItem().is(ModTags.Items.ANTIGRAVITY)) {
            entity.setNoGravity(true);
        }
    }

    /// 铁砧是否能强制合并两个相同物品
    public static boolean couldAnvilForceMerge(ItemStack itemStack) {
        return isFromConfluence(BuiltInRegistries.ITEM, itemStack.getItem());
    }

    public static void registerCauldronInteractions() {
        CauldronInteraction.INTERACTIONS.values().forEach(map -> {
            Map<Item, CauldronInteraction> interactionMap = map.map();
            interactionMap.put(ToolItems.BOTTOMLESS_WATER_BUCKET.get(), CauldronInteraction.FILL_WATER);
            interactionMap.put(ToolItems.BOTTOMLESS_LAVA_BUCKET.get(), CauldronInteraction.FILL_LAVA);
            interactionMap.put(ToolItems.BOTTOMLESS_HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
            interactionMap.put(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), AetheriumCauldronBlock.FILL_AETHERIUM);
            interactionMap.put(ToolItems.HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
            interactionMap.put(NatureBlocks.AETHERIUM_BLOCK.asItem(), AetheriumCauldronBlock.FILL_AETHERIUM);
        });
        CauldronInteraction.EMPTY.map().put(PotionItems.BOTTLED_WATER.get(), (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLE.toStack()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState());
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        });
        CauldronInteraction.WATER.map().put(PotionItems.BOTTLE.get(), (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLED_WATER.toStack()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        });
        CauldronInteraction.WATER.map().put(PotionItems.BOTTLED_WATER.get(), (state, level, pos, player, hand, stack) -> {
            if (state.getValue(LayeredCauldronBlock.LEVEL) == 3) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else if (!level.isClientSide) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLE.toStack()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        });
    }

    /// 决定护士是否能治疗
    public static boolean isDebuff(MobEffectInstance instance) {
        return instance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL && !instance.getCures().contains(ModEffects.CANNOT_REMOVE_BY_NURSE);
    }

    public static boolean isSwitchableEffect(MobEffectInstance instance) {
        MobEffect effect = instance.getEffect().value();
        boolean switchable = effect == TCEffects.GRAVITATION.get() ? instance.getAmplifier() <= 0 : effect.isBeneficial();
        return NeoForge.EVENT_BUS.post(new EffectSwitchableCheckEvent(instance, switchable)).isSwitchable();
    }

    public static boolean useKey(ItemStack carried, ItemStack onSlot, Player player) {
        if ((carried.is(ToolItems.GOLDEN_DUNGEON_KEY) && onSlot.is(ConsumableItems.GOLDEN_LOCK_BOX)) ||
                (carried.is(ToolItems.SHADOW_KEY) && onSlot.is(ConsumableItems.OBSIDIAN_LOCK_BOX))
        ) {
            if (player instanceof ServerPlayer serverPlayer && LootComponent.open(serverPlayer, onSlot)) {
                if (!serverPlayer.hasInfiniteMaterials()) {
                    if (carried.is(ToolItems.GOLDEN_DUNGEON_KEY)) {
                        carried.shrink(1);
                    }
                    onSlot.shrink(1);
                }
            }
            return true;
        }
        return false;
    }
}
