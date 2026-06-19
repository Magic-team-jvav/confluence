package org.confluence.mod.util;

import PortLib.extensions.net.minecraft.world.effect.MobEffectInstance.PortMobEffectInstanceExtension;
import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibMathUtils;
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
import org.confluence.terra_furniture.TerraFurniture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.event.PortEventHandler;

import java.util.Map;
import java.util.Set;

import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class ModUtils {
    public static final Set<String> CONFLUENCE_NAMESPACES = Set.of(Confluence.MODID, TerraCurio.MODID, TerraFurniture.MODID);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void dropMoney(int amount, double x, double y, double z, Level level) {
        int copper_count = amount % UPGRADES_COUNT;
        int i = ((amount - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        LibEntityUtils.createItemEntity(ModItems.COPPER_COIN.get(), copper_count, x, y, z, level, 0);
        LibEntityUtils.createItemEntity(ModItems.SILVER_COIN.get(), silver_count, x, y, z, level, 0);
        LibEntityUtils.createItemEntity(ModItems.GOLD_COIN.get(), golden_count, x, y, z, level, 0);
        LibEntityUtils.createItemEntity(ModItems.PLATINUM_COIN.get(), k, x, y, z, level, 0);
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

    public static boolean isWaterBottle(ItemStack stack) {
        return stack.is(PotionItems.BOTTLED_WATER.get()) || PotionUtils.getPotion(stack).equals(Potions.WATER);
    }

    public static void summonBoss(ServerLevel level, BlockPos pos, AbstractTerraBossBase boss, boolean onSurface) {
        double x = pos.getX() + 0.5 + LibMathUtils.randomFromTo(level.random, pos.getX(), 30, 50);
        double z = pos.getZ() + 0.5 + LibMathUtils.randomFromTo(level.random, pos.getZ(), 30, 50);
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

        if (living.hasEffect(ModEffects.MIDAS.get())) {
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
        AttributeInstance attack = living.getAttribute(LibAttributes.getAttackDamage().value());
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
                MobEffect debuff;
                float min;
                int i = attacker.getRandom().nextInt(81);
                if (i < 11) {
                    debuff = MobEffects.POISON;
                    min = master ? 6.56F : 5.25F;
                } else if (i < 22) {
                    debuff = MobEffects.BLINDNESS;
                    min = master ? 3.75F : 3.0F;
                } else if (i < 24) {
                    debuff = ModEffects.CURSED.get();
                    min = master ? 0.94F : 0.75F;
                } else if (i < 35) {
                    debuff = ModEffects.BLEEDING.get();
                    min = master ? 9.38F : 7.5F;
                } else if (i < 37) {
                    debuff = TCEffects.CONFUSED.get();
                    min = master ? 1.88F : 1.5F;
                } else if (i < 48) {
                    debuff = MobEffects.MOVEMENT_SLOWDOWN;
                    min = master ? 6.56F : 5.25F;
                } else if (i < 59) {
                    debuff = MobEffects.WEAKNESS;
                    min = master ? 14.06F : 11.25F;
                } else if (i < 70) {
                    debuff = ModEffects.SILENCED.get();
                    min = master ? 1.88F : 1.5F;
                } else {
                    debuff = ModEffects.BROKEN_ARMOR.get();
                    min = master ? 12.19F : 9.75F;
                }
                living.addEffect(new MobEffectInstance(debuff, (int) ((attacker.getRandom().nextFloat() * min + min) * 20)));
            }
        }
    }

    public static void applyCursedSkullDebuff(@Nullable Entity attacker, LivingEntity living) {
        if (attacker != null && attacker.getType() == TEMonsterEntities.CURSED_SKULL.get() && attacker.getRandom().nextFloat() < 0.33F) {
            living.addEffect(new MobEffectInstance(ModEffects.CURSED.get(), 80));
        }
    }

    @Deprecated(since = "1.3.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.4.0")
    public static Component formatPrice(int price) {
        return ClientUtils.formatPrice(price);
    }

    /// 不可破坏物品无法附魔耐久与经验修补
    public static boolean supportsEnchantment(ItemStack stack, Enchantment enchantment) {
        boolean supportedItem = enchantment.category.canEnchant(stack.getItem());
        if (PortItemStackExtension.getUnbreakable(stack)) {
            return supportedItem && enchantment != Enchantments.UNBREAKING && enchantment != Enchantments.MENDING;
        }
        return supportedItem;
    }

    /// 由于暮色森林使原版的该方法会访问区块，于是复制一份来用
    ///
    /// @see Level#isRainingAt(BlockPos)
    public static boolean isRainingAt(ServerLevel level, BlockPos pos) {
        if (!level.isRaining()) return false;
        if (!level.canSeeSky(pos)) return false;
        ChunkAccess chunk = LibUtils.getChunkIfLoaded(level, pos);
        if (chunk == null || chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) > pos.getY()) {
            return false;
        }
        Holder<Biome> biome = LibUtils.getBiomeManagerThatChunkMustBeLoaded(level).getBiome(pos);
        if (biome.is(Biomes.THE_VOID)) return false;
        return biome.value().getPrecipitationAt(pos) == Biome.Precipitation.RAIN;
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
        defaulted(CauldronInteraction.EMPTY);
        defaulted(CauldronInteraction.WATER);
        defaulted(CauldronInteraction.LAVA);
        defaulted(CauldronInteraction.POWDER_SNOW);
        CauldronInteraction.EMPTY.put(PotionItems.BOTTLED_WATER.get(), (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLE.toStack()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState());
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return InteractionResult.SUCCESS;
        });
        CauldronInteraction.WATER.put(PotionItems.BOTTLE.get(), (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLED_WATER.toStack()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return InteractionResult.SUCCESS;
        });
        CauldronInteraction.WATER.put(PotionItems.BOTTLED_WATER.get(), (state, level, pos, player, hand, stack) -> {
            if (state.getValue(LayeredCauldronBlock.LEVEL) == 3) {
                return InteractionResult.PASS;
            } else if (!level.isClientSide) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLE.toStack()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return InteractionResult.SUCCESS;
        });
    }

    private static void defaulted(Map<Item, CauldronInteraction> map) {
        map.put(ToolItems.BOTTOMLESS_WATER_BUCKET.get(), CauldronInteraction.FILL_WATER);
        map.put(ToolItems.BOTTOMLESS_LAVA_BUCKET.get(), CauldronInteraction.FILL_LAVA);
        map.put(ToolItems.BOTTOMLESS_HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
        map.put(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), AetheriumCauldronBlock.FILL_AETHERIUM);
        map.put(ToolItems.HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
        map.put(NatureBlocks.AETHERIUM_BLOCK.asItem(), AetheriumCauldronBlock.FILL_AETHERIUM);
    }

    /// 决定护士是否能治疗
    public static boolean isDebuff(MobEffectInstance instance) {
        return instance.getEffect().getCategory() == MobEffectCategory.HARMFUL &&
                !PortMobEffectInstanceExtension.getCures(instance).contains(ModEffects.CANNOT_REMOVE_BY_NURSE);
    }

    public static boolean isSwitchableEffect(MobEffectInstance instance) {
        MobEffect effect = instance.getEffect();
        boolean switchable = effect == TCEffects.GRAVITATION.get() ? instance.getAmplifier() <= 0 : effect.isBeneficial();
        return PortEventHandler.postEventWithReturn(new EffectSwitchableCheckEvent(instance, switchable)).isSwitchable();
    }

    public static boolean useKey(ItemStack carried, ItemStack onSlot, Player player) {
        if ((carried.is(ToolItems.GOLDEN_DUNGEON_KEY) && onSlot.is(ConsumableItems.GOLDEN_LOCK_BOX)) ||
                (carried.is(ToolItems.SHADOW_KEY) && onSlot.is(ConsumableItems.OBSIDIAN_LOCK_BOX))
        ) {
            if (player instanceof ServerPlayer serverPlayer && LootComponent.open(serverPlayer, onSlot)) {
                if (!PortPlayerExtension.hasInfiniteMaterials(player)) {
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

    public static boolean shouldDisplayTeam() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && !server.isSingleplayer();
    }
}
