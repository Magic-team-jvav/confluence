package org.confluence.mod.common.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.confluence.lib.api.event.ProcessCriticalDamageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.SoulCollectionEvent;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.potion.HealingPotionItem;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.util.EnchantmentUtils;

import javax.annotation.Nullable;
import java.util.List;

/// 附魔效果的公共结算类。
///
/// 这里**只放效果本身**，事件订阅一律写在各自的事件类里（{@code LivingEntityEvents} / {@code PlayerEvents} /
/// {@code TickEvents}），避免出现第二套事件总线。
///
/// 状态一律放在玩家/生物的持久数据里，键统一以 `confluence.enchant.` 开头：
/// 这样退出存档、跨维度都不会丢，也不需要额外注册附件类型。
public final class EnchantmentBehaviors {
    // region 键与常量

    private static final String POTION_SHIELD_UNTIL = "confluence.enchant.potion_shield_until";
    private static final String SOUL_HASTE_UNTIL = "confluence.enchant.soul_haste_until";
    private static final String SOUL_RESONANCE_UNTIL = "confluence.enchant.soul_resonance_until";
    private static final String SOUL_SIPHON_UNTIL = "confluence.enchant.soul_siphon_until";
    private static final String SPEED_SYNERGY_UNTIL = "confluence.enchant.speed_synergy_until";
    private static final String ARTIFACT_SYNERGY_READY = "confluence.enchant.artifact_synergy_ready";
    private static final String BEAST_INSPIRE_UNTIL = "confluence.enchant.beast_inspire_until";
    private static final String VIGOROUS_STACKS = "confluence.enchant.vigorous_stacks";
    private static final String VIGOROUS_PROGRESS = "confluence.enchant.vigorous_progress";
    private static final String BURST_PROGRESS = "confluence.enchant.burst_progress";
    private static final String FOOD_RESERVE_READY_AT = "confluence.enchant.food_reserve_ready_at";
    private static final String FLANKING_STRIKE_READY_AT = "confluence.enchant.flanking_strike_ready_at";
    private static final String RECKLESS_CANCELLED_ARMOR = "confluence.enchant.reckless_cancelled_armor";
    private static final String LAST_X = "confluence.enchant.last_x";
    private static final String LAST_Y = "confluence.enchant.last_y";
    private static final String LAST_Z = "confluence.enchant.last_z";
    private static final String STUN_IMMUNE_UNTIL = "confluence.enchant.stun_immune_until";

    private static final ResourceLocation POTION_SHIELD_ARMOR_ID = Confluence.asResource("enchantment.potion_shield");
    private static final ResourceLocation SOUL_HASTE_SPEED_ID = Confluence.asResource("enchantment.soul_haste");
    private static final ResourceLocation SPEED_SYNERGY_SPEED_ID = Confluence.asResource("enchantment.speed_synergy");
    private static final ResourceLocation RECKLESS_ARMOR_ID = Confluence.asResource("enchantment.reckless");

    /// 精力充沛：每 7 格位移叠 1 层，最多 15 层，每层下一次攻击 +10%。
    private static final double VIGOR_BLOCKS_PER_STACK = 7.0;
    private static final int VIGOR_MAX_STACKS = 15;
    private static final float VIGOR_DAMAGE_PER_STACK = 0.1F;
    /// 爆裂弓弦：位移 7 格后自动射击。
    private static final double BURST_BLOCKS = 7.0;
    private static final double BURST_RANGE = 12.0;
    /// 食物储备冷却 60 秒。
    private static final int FOOD_RESERVE_COOLDOWN_TICKS = 60 * 20;
    /// 灵魂虹吸：命中后的易伤窗口 5 秒。
    private static final int SOUL_SIPHON_DURATION_TICKS = 5 * 20;
    /// 束魂 / 灵魂虹吸的概率按等级取值。
    private static final float[] SOUL_CHANCE_BY_LEVEL = {0.0F, 0.05F, 0.10F, 0.15F};
    private static final float[] SOUL_SIPHON_BY_LEVEL = {0.0F, 0.03F, 0.05F, 0.08F};
    /// 虚空强袭：固定 5 秒，易伤从起始倍率线性爬到结束倍率。
    private static final int VOID_STRIKE_DURATION_TICKS = 5 * 20;
    private static final float[] VOID_STRIKE_START = {0.03F, 0.06F, 0.09F};
    private static final float[] VOID_STRIKE_END = {0.15F, 0.30F, 0.50F};
    /// 眩晕：2 秒，之后 10 秒免疫。
    private static final int STUN_DURATION_TICKS = 2 * 20;
    private static final int STUN_IMMUNE_TICKS = 10 * 20;

    private EnchantmentBehaviors() {}

    // endregion

    // region 通用工具

    /// 按条件维护一条瞬态属性修饰符，条件失效自动摘除。
    private static void transientModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id,
                                          double value, AttributeModifier.Operation operation, boolean active) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        if (active) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(id, value, operation));
        } else if (instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }

    private static long gameTime(Player player) {
        return player.level().getGameTime();
    }

    /// 到点判定：记录的是「生效截止时刻」。
    private static boolean activeUntil(Player player, String key, long gameTime) {
        return player.getPersistentData().getLong(key) > gameTime;
    }

    private static void setUntil(Player player, String key, long gameTime, int ticks) {
        player.getPersistentData().putLong(key, gameTime + ticks);
    }

    /// 按等级取表里的值，等级越界时取最后一个。
    private static float byLevel(float[] table, int level) {
        if (level <= 0) return 0.0F;
        return table[Math.min(level, table.length) - 1];
    }

    // endregion

    // region 治疗药水（食物储备 / 万兽鼓舞 / 药水护盾 / 惊喜礼物）

    /// 使用治疗药水成功后结算四个附魔。冷却类（食物储备）自己管理冷却时刻。
    public static void onHealingPotionUsed(ServerPlayer player, HealingPotionItem potion) {
        long gameTime = gameTime(player);
        foodReserve(player, gameTime);
        beastInspire(player, gameTime);
        potionShield(player, gameTime);
        surpriseGift(player);
    }

    /// 食物储备：随机食物，冷却 60 秒。
    private static void foodReserve(ServerPlayer player, long gameTime) {
        if (EnchantmentUtils.getLevel(player, ModEnchantments.FOOD_RESERVE) <= 0) return;
        if (player.getPersistentData().getLong(FOOD_RESERVE_READY_AT) > gameTime) return;
        player.getPersistentData().putLong(FOOD_RESERVE_READY_AT, gameTime + FOOD_RESERVE_COOLDOWN_TICKS);
        Item[] foods = {Items.BREAD, Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.APPLE, Items.COOKED_COD};
        ItemStack food = new ItemStack(foods[player.getRandom().nextInt(foods.length)]);
        if (!player.addItem(food)) {
            player.drop(food, false);
        }
    }

    /// 万兽鼓舞：治疗药水给召唤物 +2 伤害，持续 10 秒（md 里原本就是 10 秒）。
    private static void beastInspire(Player player, long gameTime) {
        if (EnchantmentUtils.getLevel(player, ModEnchantments.BEAST_INSPIRE) <= 0) return;
        setUntil(player, BEAST_INSPIRE_UNTIL, gameTime, 200);
    }

    /// 药水护盾：3/5/7 秒内 +4 防御。
    private static void potionShield(Player player, long gameTime) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.POTION_SHIELD);
        if (level <= 0) return;
        setUntil(player, POTION_SHIELD_UNTIL, gameTime, (3 + 2 * (level - 1)) * 20);
    }

    /// 惊喜礼物：红心 / 魔力星 / 经验 / 1 点灵魂 四选一。
    private static void surpriseGift(ServerPlayer player) {
        if (EnchantmentUtils.getLevel(player, ModEnchantments.SURPRISE_GIFT) <= 0) return;
        switch (player.getRandom().nextInt(4)) {
            case 0 -> giveOrDrop(player, ModItems.HEART.toStack());
            case 1 -> giveOrDrop(player, ModItems.STAR.toStack());
            case 2 -> player.giveExperiencePoints(10 + player.getRandom().nextInt(15));
            default -> {
                if (Confluence.SOUL_SKILLS) {
                    SoulStorage.of(player).receive(1.0F);
                } else {
                    giveOrDrop(player, ModItems.STAR.toStack());
                }
            }
        }
    }

    private static void giveOrDrop(Player player, ItemStack stack) {
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }

    // endregion

    // region 受伤（偏转 / 舍己为人 / 虚空强袭）

    /// 受击侧结算：投射物闪避、治疗队友、虚空强袭增伤。
    /// @return 结算后的伤害
    public static float incomingDamage(LivingIncomingDamageEvent event, LivingEntity victim, float amount) {
        DamageSource damageSource = event.getSource();
        amount = voidStrikeAmplify(victim, amount);
        if (!(victim instanceof ServerPlayer player)) return amount;
        if (deflect(player, damageSource)) {
            event.setCanceled(true);
            return 0.0F;
        }
        selflessSacrifice(player);
        return amount;
    }

    /// 偏转：4%/6%/8% 概率闪避投射物。
    private static boolean deflect(ServerPlayer player, DamageSource damageSource) {
        if (!damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.DEFLECT);
        if (level <= 0) return false;
        return player.getRandom().nextFloat() < 0.02F * (level + 1);
    }

    /// 舍己为人：受到攻击时治疗周围队友固定值 1/3/5 点生命值，范围 8/16/24 格。
    private static void selflessSacrifice(ServerPlayer player) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SELFLESS_SACRIFICE);
        if (level <= 0) return;
        float heal = 2.0F * level - 1.0F;
        double range = 8.0 * level;
        for (Player other : player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(range), p -> p != player && p.isAlive())) {
            other.heal(heal);
        }
    }

    /// 虚空强袭：5 秒内受到的伤害从起始倍率线性爬到结束倍率。
    private static float voidStrikeAmplify(LivingEntity victim, float amount) {
        MobEffectInstance instance = victim.getEffect(ModEffects.VOID_STRIKE);
        if (instance == null) return amount;
        float progress = 1.0F - Mth.clamp(instance.getDuration() / (float) VOID_STRIKE_DURATION_TICKS, 0.0F, 1.0F);
        float start = byLevel(VOID_STRIKE_START, instance.getAmplifier() + 1);
        float end = byLevel(VOID_STRIKE_END, instance.getAmplifier() + 1);
        return amount * (1.0F + Mth.lerp(progress, start, end));
    }

    // endregion

    // region 攻击侧（法器协同 / 精力充沛 / 莽撞 / 万兽鼓舞 / 虚空强袭）

    /// 攻击伤害结算：法器协同、精力充沛、莽撞、万兽鼓舞的召唤物加伤。
    /// @return 结算后的伤害
    public static float attackerDamage(@Nullable Entity attacker, DamageSource damageSource, LivingEntity victim, float amount) {
        /// 万兽鼓舞：召唤物伤害 +2
        if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && damageSource instanceof AttachmentEntityDamageSource
                && attacker instanceof ServerPlayer summoner) {
            if (activeUntil(summoner, BEAST_INSPIRE_UNTIL, gameTime(summoner))) {
                amount += 2.0F;
            }
        }
        if (!(attacker instanceof ServerPlayer player)) return amount;
        if (player == victim) return amount;
        amount = artifactSynergy(player, amount);
        amount = vigorous(player, amount);
        return reckless(player, amount);
    }

    /// 法器协同：使用法器后强化下一次攻击 40%/60%/80%，不可叠加。
    private static float artifactSynergy(ServerPlayer player, float amount) {
        if (!player.getPersistentData().getBoolean(ARTIFACT_SYNERGY_READY)) return amount;
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.ARTIFACT_SYNERGY);
        if (level <= 0) return amount;
        player.getPersistentData().remove(ARTIFACT_SYNERGY_READY);
        return amount * (1.0F + 0.2F * (level + 1));
    }

    /// 精力充沛：消耗全部层数，每层 +10% 伤害。
    private static float vigorous(ServerPlayer player, float amount) {
        int stacks = player.getPersistentData().getInt(VIGOROUS_STACKS);
        if (stacks <= 0) return amount;
        if (EnchantmentUtils.getLevel(player, ModEnchantments.VIGOROUS) <= 0) {
            player.getPersistentData().remove(VIGOROUS_STACKS);
            return amount;
        }
        player.getPersistentData().remove(VIGOROUS_STACKS);
        return amount * (1.0F + VIGOR_DAMAGE_PER_STACK * stacks);
    }

    /// 莽撞：护甲值被转移到近战伤害上，护甲值越高的部分按 (X/10)% 加成。
    private static float reckless(ServerPlayer player, float amount) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.RECKLESS);
        if (level <= 0) return amount;
        double armor = player.getPersistentData().getDouble(RECKLESS_CANCELLED_ARMOR);
        if (armor <= 0.0) return amount;
        return amount * (1.0F + (float) (armor / 10.0 / 100.0));
    }

    /// 命中后的结算：眩晕、灵魂虹吸、虚空强袭、回击。
    public static void afterAttack(ServerPlayer player, LivingEntity victim, DamageSource damageSource) {
        boolean melee = damageSource.getDirectEntity() == player;
        voidStrike(player, victim);
        if (!melee) return;
        stun(player, victim);
        soulSiphon(player);
        flankingStrike(player);
    }

    /// 虚空强袭：命中叠加固定 5 秒的易伤。
    private static void voidStrike(ServerPlayer player, LivingEntity victim) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.VOID_STRIKE);
        if (level <= 0) return;
        victim.addEffect(new MobEffectInstance(ModEffects.VOID_STRIKE, VOID_STRIKE_DURATION_TICKS, level - 1, false, false, true));
    }

    /// 眩晕：5%/10%/15% 概率，Boss 免疫；眩晕后 10 秒不可再被眩晕。
    private static void stun(ServerPlayer player, LivingEntity victim) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.STUN);
        if (level <= 0) return;
        if (victim instanceof BaseBoss || !(victim instanceof Enemy)) return;
        long gameTime = gameTime(player);
        if (victim.getPersistentData().getLong(STUN_IMMUNE_UNTIL) > gameTime) return;
        if (player.getRandom().nextFloat() >= 0.05F * level) return;
        victim.getPersistentData().putLong(STUN_IMMUNE_UNTIL, gameTime + STUN_IMMUNE_TICKS);
        victim.addEffect(new MobEffectInstance(ModEffects.STUN, STUN_DURATION_TICKS, 0, false, false, true));
    }

    /// 灵魂虹吸：命中后 5 秒内提升灵魂获取概率 3%/5%/8%。
    private static void soulSiphon(ServerPlayer player) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SOUL_SIPHON);
        if (level <= 0) return;
        setUntil(player, SOUL_SIPHON_UNTIL, gameTime(player), SOUL_SIPHON_DURATION_TICKS);
    }

    /// 回击：攻击后立刻回满攻速条，冷却 5/4/3 秒。
    private static void flankingStrike(ServerPlayer player) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.FLANKING_STRIKE);
        if (level <= 0) return;
        long gameTime = gameTime(player);
        if (player.getPersistentData().getLong(FLANKING_STRIKE_READY_AT) > gameTime) return;
        player.getPersistentData().putLong(FLANKING_STRIKE_READY_AT, gameTime + (6 - level) * 20L);
        player.resetAttackStrengthTicker();
    }

    // endregion

    // region 暴击（致命一击 / 灵魂共振）

    /// 暴击倍率：致命一击按等级直接设定，灵魂共振在窗口期内再拔高一档，取两者较大值。
    public static void criticalDamage(ServerPlayer player, ProcessCriticalDamageEvent event) {
        if (!event.isCritical()) return;
        float multiplier = event.getCriticalDamageMultiplier();
        int criticalStrike = EnchantmentUtils.getLevel(player, ModEnchantments.CRITICAL_STRIKE);
        if (criticalStrike > 0) {
            multiplier = Math.max(multiplier, 1.8F + 0.2F * criticalStrike);
        }
        long gameTime = gameTime(player);
        int resonance = EnchantmentUtils.getLevel(player, ModEnchantments.SOUL_RESONANCE);
        if (resonance > 0 && activeUntil(player, SOUL_RESONANCE_UNTIL, gameTime)) {
            multiplier = Math.max(multiplier, 2.0F + 0.2F * resonance);
        }
        event.setCriticalDamageMultiplier(multiplier);
    }

    // endregion

    // region 灵魂（灵魂疏导 / 束魂 / 灵魂促行 / 灵魂共振）

    /// 收集灵魂时结算：额外获取概率、回血、限时增益。
    public static void onSoulCollected(SoulCollectionEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        long gameTime = gameTime(serverPlayer);
        soulBind(serverPlayer, event);
        soulSiphonBonus(serverPlayer, event);
        soulConduit(serverPlayer);
        soulHaste(serverPlayer, gameTime);
        soulResonance(serverPlayer, gameTime);
    }

    /// 束魂：提升生物死亡时的灵魂获取概率 5%/10%/15%。
    private static void soulBind(ServerPlayer player, SoulCollectionEvent event) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SOUL_BIND);
        if (level <= 0) return;
        if (player.getRandom().nextFloat() < byLevel(SOUL_CHANCE_BY_LEVEL, level)) {
            event.setAmount(event.getAmount() + 1.0F);
        }
    }

    /// 灵魂疏导：获取灵魂回复 1/2/3 点生命值。
    private static void soulConduit(ServerPlayer player) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SOUL_CONDUIT);
        if (level <= 0) return;
        player.heal(level);
    }

    /// 灵魂促行：获取灵魂后 3/5/7 秒内提升 15% 移速。
    private static void soulHaste(Player player, long gameTime) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SOUL_HASTE);
        if (level <= 0) return;
        setUntil(player, SOUL_HASTE_UNTIL, gameTime, (1 + 2 * level) * 20);
    }

    /// 灵魂共振：获取灵魂后 3/6/9 秒内暴击伤害拔高到 2.2/2.4/2.6。
    private static void soulResonance(Player player, long gameTime) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SOUL_RESONANCE);
        if (level <= 0) return;
        setUntil(player, SOUL_RESONANCE_UNTIL, gameTime, 3 * level * 20);
    }

    /// 灵魂虹吸的额外概率，由 {@link SoulCollectionEvent} 侧统一调用。
    public static void soulSiphonBonus(ServerPlayer player, SoulCollectionEvent event) {
        if (!activeUntil(player, SOUL_SIPHON_UNTIL, gameTime(player))) return;
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SOUL_SIPHON);
        if (level <= 0) return;
        if (player.getRandom().nextFloat() < byLevel(SOUL_SIPHON_BY_LEVEL, level)) {
            event.setAmount(event.getAmount() + 1.0F);
        }
    }

    // endregion

    // region 法器（法器协同 / 速度协同）

    /// 使用法器成功后：登记法力协同标记与速度协同窗口。
    public static void onArtifactUsed(ServerPlayer player) {
        long gameTime = gameTime(player);
        if (EnchantmentUtils.getLevel(player, ModEnchantments.ARTIFACT_SYNERGY) > 0) {
            player.getPersistentData().putBoolean(ARTIFACT_SYNERGY_READY, true);
        }
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.SPEED_SYNERGY);
        if (level > 0) {
            setUntil(player, SPEED_SYNERGY_UNTIL, gameTime, level * 20);
        }
    }

    // endregion

    // region 死亡交易

    /// 死亡交易：本次死亡不掉落任何物品，触发后移除护腿上的附魔。
    /// @return true 表示本次死亡应当拦下全部掉落
    public static boolean triggerDeathBargain(ServerPlayer player) {
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEmpty()) return false;
        if (EnchantmentUtils.getLevel(leggings, ModEnchantments.DEATH_BARGAIN) <= 0) return false;
        EnchantmentHelper.updateEnchantments(leggings, enchantments ->
                removeEnchantment(enchantments, ModEnchantments.DEATH_BARGAIN));
        return true;
    }

    /// 从可变附魔集合里删掉指定附魔。
    private static void removeEnchantment(ItemEnchantments.Mutable enchantments, net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key) {
        enchantments.removeIf(holder -> holder.is(key));
    }

    // endregion

    // region 每刻结算（药水护盾 / 灵魂促行 / 速度协同 / 万兽鼓舞 / 莽撞 / 位移相关）

    /// 每刻结算与附魔有关的限时增益与位移统计。
    public static void tick(ServerPlayer player) {
        long gameTime = gameTime(player);
        potionShieldTick(player, gameTime);
        soulHasteTick(player, gameTime);
        speedSynergyTick(player, gameTime);
        recklessTick(player);
        travelTick(player);
    }

    private static void potionShieldTick(Player player, long gameTime) {
        boolean active = activeUntil(player, POTION_SHIELD_UNTIL, gameTime);
        transientModifier(player, Attributes.ARMOR, POTION_SHIELD_ARMOR_ID, 4.0, AttributeModifier.Operation.ADD_VALUE, active);
    }

    private static void soulHasteTick(Player player, long gameTime) {
        boolean active = activeUntil(player, SOUL_HASTE_UNTIL, gameTime);
        transientModifier(player, Attributes.MOVEMENT_SPEED, SOUL_HASTE_SPEED_ID, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
    }

    private static void speedSynergyTick(Player player, long gameTime) {
        boolean active = activeUntil(player, SPEED_SYNERGY_UNTIL, gameTime);
        transientModifier(player, Attributes.MOVEMENT_SPEED, SPEED_SYNERGY_SPEED_ID, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
    }

    /// 莽撞：把护甲值抵消掉，并把抵消掉的数值记下来给伤害加成用。
    private static void recklessTick(ServerPlayer player) {
        int level = EnchantmentUtils.getLevel(player, ModEnchantments.RECKLESS);
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor == null) return;
        if (level <= 0) {
            if (armor.getModifier(RECKLESS_ARMOR_ID) != null) {
                armor.removeModifier(RECKLESS_ARMOR_ID);
            }
            player.getPersistentData().remove(RECKLESS_CANCELLED_ARMOR);
            return;
        }
        double cancelled = player.getPersistentData().getDouble(RECKLESS_CANCELLED_ARMOR);
        double base = armor.getValue() + cancelled;
        if (base <= 0.0) {
            if (armor.getModifier(RECKLESS_ARMOR_ID) != null) {
                armor.removeModifier(RECKLESS_ARMOR_ID);
            }
            player.getPersistentData().remove(RECKLESS_CANCELLED_ARMOR);
            return;
        }
        player.getPersistentData().putDouble(RECKLESS_CANCELLED_ARMOR, base);
        armor.addOrUpdateTransientModifier(new AttributeModifier(RECKLESS_ARMOR_ID, -base, AttributeModifier.Operation.ADD_VALUE));
    }

    /// 位移统计：精力充沛叠层与爆裂弓弦自动射击共用同一份位移数据。
    private static void travelTick(ServerPlayer player) {
        var data = player.getPersistentData();
        double lastX = data.getDouble(LAST_X);
        double lastY = data.getDouble(LAST_Y);
        double lastZ = data.getDouble(LAST_Z);
        Vec3 current = player.position();
        if (!data.contains(LAST_X)) {
            data.putDouble(LAST_X, current.x);
            data.putDouble(LAST_Y, current.y);
            data.putDouble(LAST_Z, current.z);
            return;
        }
        double distance = Math.sqrt((current.x - lastX) * (current.x - lastX)
                + (current.y - lastY) * (current.y - lastY)
                + (current.z - lastZ) * (current.z - lastZ));
        data.putDouble(LAST_X, current.x);
        data.putDouble(LAST_Y, current.y);
        data.putDouble(LAST_Z, current.z);
        if (distance <= 0.0 || distance > 8.0) return; // 传送/换维度直接跳过

        vigorousTravel(player, distance);
        burstBowstring(player, distance);
    }

    private static void vigorousTravel(ServerPlayer player, double distance) {
        var data = player.getPersistentData();
        if (EnchantmentUtils.getLevel(player, ModEnchantments.VIGOROUS) <= 0) {
            data.remove(VIGOROUS_PROGRESS);
            data.remove(VIGOROUS_STACKS);
            return;
        }
        double progress = data.getDouble(VIGOROUS_PROGRESS) + distance;
        int stacks = data.getInt(VIGOROUS_STACKS);
        while (progress >= VIGOR_BLOCKS_PER_STACK && stacks < VIGOR_MAX_STACKS) {
            progress -= VIGOR_BLOCKS_PER_STACK;
            stacks++;
        }
        data.putDouble(VIGOROUS_PROGRESS, progress);
        data.putInt(VIGOROUS_STACKS, stacks);
    }

    /// 爆裂弓弦：手上拿着附魔弓弩时，每累计 7 格位移向最近的 1/2 个敌人射一支 40% 伤害的箭。
    private static void burstBowstring(ServerPlayer player, double distance) {
        var data = player.getPersistentData();
        ItemStack held = player.getMainHandItem();
        int level = EnchantmentUtils.getLevel(held, ModEnchantments.BURST_BOWSTRING);
        if (level <= 0) {
            data.remove(BURST_PROGRESS);
            return;
        }
        if (!(player.level() instanceof ServerLevel level0)) return;
        double progress = data.getDouble(BURST_PROGRESS) + distance;
        if (progress < BURST_BLOCKS) {
            data.putDouble(BURST_PROGRESS, progress);
            return;
        }
        data.putDouble(BURST_PROGRESS, progress - BURST_BLOCKS);
        int targets = level >= 2 ? 2 : 1;
        List<LivingEntity> enemies = level0.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(BURST_RANGE),
                entity -> entity != player && entity.isAlive() && entity instanceof Enemy);
        if (enemies.isEmpty()) return;
        enemies.sort(java.util.Comparator.comparingDouble(player::distanceToSqr));
        double damage = player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.4;
        for (int i = 0; i < Math.min(targets, enemies.size()); i++) {
            LivingEntity target = enemies.get(i);
            Arrow arrow = new Arrow(player.level(), player, ItemStack.EMPTY, held);
            arrow.setBaseDamage(damage);
            arrow.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            Vec3 direction = target.getEyePosition().subtract(arrow.position());
            if (direction.lengthSqr() < 1.0E-4) continue;
            arrow.shoot(direction.x, direction.y, direction.z, 2.5F, 0.5F);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            player.level().addFreshEntity(arrow);
        }
    }

    // endregion

    // region 虚空液体

    /// 虚空液体同样赋予「虚空强袭」：浸泡时挂上 5 秒的 1 级版本，到点后重新挂，
    /// 这样易伤会跟着 5 秒周期一段段往上爬，而不是永远停在起始倍率。
    public static void voidLiquidTick(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide) return;
        if (living.hasEffect(ModEffects.VOID_STRIKE)) return;
        if (living.getFluidTypeHeight(ModFluids.VOID.type().get()) <= 0.0) return;
        living.addEffect(new MobEffectInstance(ModEffects.VOID_STRIKE, VOID_STRIKE_DURATION_TICKS, 0, false, false, true));
    }

    // endregion
}
