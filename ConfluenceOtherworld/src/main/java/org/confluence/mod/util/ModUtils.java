package org.confluence.mod.util;

import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceManager;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.jetbrains.annotations.Nullable;

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
        LibUtils.createItemEntity(ModItems.GOLDEN_COIN.get(), golden_count, x, y, z, level, 0);
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

    public static void summonBoss(ServerLevel level, BlockPos pos, AbstractTerraBossBase<?> boss) {
        double x = pos.getX() + 0.5 + level.random.nextInt(-50, 51);
        double z = pos.getZ() + 0.5 + level.random.nextInt(-50, 51);
        boss.setPos(x, pos.getY() + 0.5 + level.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z)), z);
        boss.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
        level.addFreshEntity(boss);
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
        EntityType<?> type = living.getType();
        KillBoard.INSTANCE.defeat(type);
        boolean isEaterOfWorlds = type == TEBossEntities.EATER_OF_WORLDS.get();
        if (isEaterOfWorlds || type == TEBossEntities.BRAIN_OF_CTHULHU.get()) {
            if (DateUtils.isWithinDayTime(DateUtils._00$00, DateUtils._04$30, DateUtils.getDayTime(level))) {
                MeteoriteTracker.INSTANCE.spawnAtNextNight = true;
            } else if (!MeteoriteTracker.INSTANCE.spawnAtNextNight) {
                MeteoriteTracker.INSTANCE.spawnAtNextNight = level.random.nextBoolean();
            }
        }
        boolean stickySituation = type == TEBossEntities.KING_SLIME.get() && MomentInstanceManager.of(level).hasMoment(TMMoments.SLIME_RAIN.getKey());
        boolean is$WallOrMountain$OfFlesh = type == TEBossEntities.WALL_OF_FLESH.get(); // todo 还差Mountain of Flesh
        ResourceKey<Level> dimension = living.level().dimension();
        level.players().stream().filter(player -> player.level().dimension() == dimension).forEach(player -> {
            TreasureBagItem.createItemEntity(living, player);
            if (isEaterOfWorlds) {
                AchievementUtils.awardAchievement(player, "worm_fodder");
            } else if (stickySituation) {
                AchievementUtils.awardAchievement(player, "sticky_situation");
            } else if (is$WallOrMountain$OfFlesh) {
                AchievementUtils.awardAchievement(player, "still_hungry");
            }
        });
    }

    public static void enemyDropMoney(LivingEntity living, ServerLevel level) {
        AttributeInstance attack = living.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
        AttributeInstance knockbackResistance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        double healthFactor = living.getMaxHealth() * 0.15;
        double attackFactor = attack == null ? 0.0 : attack.getValue() * 0.25;
        double armorFactor = armor == null ? 0.0 : armor.getValue() * 0.1;
        double knockbackResistanceFactor = knockbackResistance == null ? 10.0 : (1.0 + knockbackResistance.getValue()) * 10.0;
        double difficultyFactor = level.getCurrentDifficultyAt(living.blockPosition()).getEffectiveDifficulty() * 0.5;
        double amount = Math.min(Math.round((healthFactor + attackFactor + armorFactor + knockbackResistanceFactor) * difficultyFactor) * 7.0, 100000);

        if (living.hasEffect(ModEffects.MIDAS)) {
            amount *= Mth.nextDouble(living.getRandom(), 1.1, 1.49);
        }
        if (IMinecraftServer.isHardmode(level.getServer())) {
            amount *= 1.6;
        }
        if (KillBoard.INSTANCE.getGamePhase().isAboveThan(GamePhase.PLANTERA)) {
            amount *= 1.5;
        }

        dropMoney((int) amount, living.getX(), living.getEyeY() - 0.3, living.getZ(), level);
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
        if (target == null) return false; // 有模组把target写成了null
        if (owner == target || !target.isAttackable() || !target.canBeHitByProjectile() || target instanceof ArmorStand || target instanceof Npc) return false;
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
        if (platinum > 0) cmp.append(Component.literal(platinum + " ").withColor(-4996668)).append(Component.translatable("tooltip.price.platinum").withColor(-4996668));
        if (gold > 0) cmp.append(Component.literal(gold + " ").withColor(-3891380)).append(Component.translatable("tooltip.price.gold").withColor(-3891380));
        if (silver > 0) cmp.append(Component.literal(silver + " ").withColor(-4532777)).append(Component.translatable("tooltip.price.silver").withColor(-4532777));
        if (copper > 0) cmp.append(Component.literal(copper + " ").withColor(-3837899)).append(Component.translatable("tooltip.price.copper").withColor(-3837899));
        return cmp;
    }

    /**
     * 不可破坏物品无法附魔耐久与经验修补
     */
    public static boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        boolean supportedItem = stack.is(enchantment.value().definition().supportedItems());
        if (stack.has(DataComponents.UNBREAKABLE)) {
            return supportedItem && !enchantment.is(Enchantments.UNBREAKING) && !enchantment.is(Enchantments.MENDING);
        }
        return supportedItem;
    }
}
