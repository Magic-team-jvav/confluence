package org.confluence.mod.api.whip;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.common.data.map.LivingInvulnerableEffects;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 按“玩家 + 目标”隔离的鞭子召唤标记存储。
///
/// <p>鞭痕属于施加标记的玩家，而不是目标身上的全局增伤。两名玩家鞭打同一目标时，
/// 各自召唤物只能读取自己的最后一次标记；同一玩家的新标记会替换旧标记。</p>
///
/// <p>这里仅保存短生命周期的战斗状态，不写入世界存档。世界卸载后由弱引用释放，
/// 每次读写同时清理过期项。</p>
public final class WhipTagTracker {
    public static final int DEFAULT_DURATION_TICKS = 80;

    private static final Map<ServerLevel, Map<Key, Entry>> TAGS = new WeakHashMap<>();
    private static long nextApplicationSequence;

    private WhipTagTracker() {}

    /// 用当前鞭子的独立 Effect 替换该玩家对目标施加的旧标记。
    public static void apply(Player owner, LivingEntity target, ItemStack whipStack, WhipTagEffect effect) {
        Objects.requireNonNull(owner, "Whip tag owner must not be null");
        Objects.requireNonNull(target, "Whip tag target must not be null");
        Objects.requireNonNull(whipStack, "Whip tag weapon must not be null");
        Objects.requireNonNull(effect, "Whip tag effect must not be null");
        if (!(target.level() instanceof ServerLevel level)) {
            throw new IllegalStateException("Whip tags can only be applied on the logical server");
        }
        if (whipStack.isEmpty()) {
            throw new IllegalArgumentException("Whip tag weapon must not be empty");
        }
        if (LivingInvulnerableEffects.isInvulnerableTo(target, effect)) {
            Map<Key, Entry> levelTags = TAGS.get(level);
            if (levelTags != null) {
                removeEntry(levelTags, new Key(owner.getUUID(), target.getUUID()), target);
            }
            return;
        }

        target.addEffect(new MobEffectInstance(effect, DEFAULT_DURATION_TICKS, 0, false, true), owner);
        if (!target.hasEffect(effect)) {
            Map<Key, Entry> levelTags = TAGS.get(level);
            if (levelTags != null) {
                removeEntry(levelTags, new Key(owner.getUUID(), target.getUUID()), target);
            }
            return;
        }

        Map<Key, Entry> levelTags = levelTags(level);
        long gameTime = level.getGameTime();
        purgeExpired(levelTags, gameTime);
        Key key = new Key(owner.getUUID(), target.getUUID());
        Entry previous = levelTags.put(key, new Entry(whipStack, effect, gameTime + DEFAULT_DURATION_TICKS, nextApplicationSequence++));
        if (previous != null && previous.effect() != effect && !isEffectUsedByAnotherTag(levelTags, key.targetId(), previous.effect())) {
            target.removeEffect(previous.effect());
        }
    }

    /// 将该玩家最后施加的有效标记应用到一次召唤物伤害。
    ///
    /// @param baseDamage 已完成召唤物自身计算、尚未应用鞭痕的伤害
    public static float modifyDamage(Player owner, OwnedSummon summon, LivingEntity target, float baseDamage) {
        Objects.requireNonNull(owner, "Whip tag owner must not be null");
        Objects.requireNonNull(summon, "Whip tag summon must not be null");
        Objects.requireNonNull(target, "Whip tag target must not be null");
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Whip tag base damage must be finite and non-negative");
        }
        Entry entry = find(owner, target);
        if (entry == null) {
            return baseDamage;
        }

        float damage = entry.effect().modifyDamage(new WhipTagDamageContext(owner, summon, target, entry.whipStack()), baseDamage);
        if (!Float.isFinite(damage) || damage < 0.0F) {
            throw new IllegalStateException("Whip tag effect returned invalid damage");
        }
        return damage;
    }

    /// 查询该玩家对目标是否仍有有效标记。
    public static boolean hasActiveTag(Player owner, LivingEntity target) {
        return find(owner, target) != null;
    }

    /// 返回玩家最后鞭打且仍带有效标记的目标。
    ///
    /// <p>一次挥鞭可能在同一游戏刻命中多个目标，因此使用运行期递增序号还原真实命中顺序。</p>
    public static @Nullable LivingEntity lastTaggedTarget(Player owner) {
        if (!(owner.level() instanceof ServerLevel level)) {
            return null;
        }
        Map<Key, Entry> levelTags = TAGS.get(level);
        if (levelTags == null) {
            return null;
        }
        purgeExpired(levelTags, level.getGameTime());

        LivingEntity latestTarget = null;
        long latestSequence = Long.MIN_VALUE;
        Iterator<Map.Entry<Key, Entry>> iterator = levelTags.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Key, Entry> tagged = iterator.next();
            if (!tagged.getKey().ownerId().equals(owner.getUUID())) {
                continue;
            }
            Entity entity = level.getEntity(tagged.getKey().targetId());
            if (!(entity instanceof LivingEntity target) || !target.isAlive() || !target.hasEffect(tagged.getValue().effect())) {
                iterator.remove();
                continue;
            }
            if (tagged.getValue().applicationSequence() > latestSequence) {
                latestTarget = target;
                latestSequence = tagged.getValue().applicationSequence();
            }
        }
        return latestTarget;
    }

    /// 主动移除该玩家对目标施加的运行时标记。
    public static void remove(Player owner, LivingEntity target) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }
        Map<Key, Entry> levelTags = TAGS.get(level);
        if (levelTags != null) {
            removeEntry(levelTags, new Key(owner.getUUID(), target.getUUID()), target);
        }
    }

    private static @Nullable Entry find(Player owner, LivingEntity target) {
        if (!(target.level() instanceof ServerLevel level)) {
            return null;
        }
        Map<Key, Entry> levelTags = TAGS.get(level);
        if (levelTags == null) {
            return null;
        }
        long gameTime = level.getGameTime();
        purgeExpired(levelTags, gameTime);
        Key key = new Key(owner.getUUID(), target.getUUID());
        Entry entry = levelTags.get(key);
        if (entry != null && !target.hasEffect(entry.effect())) {
            levelTags.remove(key);
            return null;
        }
        return entry;
    }

    private static Map<Key, Entry> levelTags(ServerLevel level) {
        return TAGS.computeIfAbsent(level, ignored -> new HashMap<>());
    }

    private static void purgeExpired(Map<Key, Entry> tags, long gameTime) {
        tags.entrySet().removeIf(entry -> entry.getValue().expiresAt() <= gameTime);
    }

    private static boolean isEffectUsedByAnotherTag(Map<Key, Entry> tags, UUID targetId, WhipTagEffect effect) {
        return tags.entrySet().stream().anyMatch(entry -> entry.getKey().targetId().equals(targetId) && entry.getValue().effect() == effect);
    }

    private static void removeEntry(Map<Key, Entry> tags, Key key, LivingEntity target) {
        Entry removed = tags.remove(key);
        if (removed != null && !isEffectUsedByAnotherTag(tags, key.targetId(), removed.effect())) {
            target.removeEffect(removed.effect());
        }
    }

    private record Key(UUID ownerId, UUID targetId) {}

    private record Entry(ItemStack whipStack, WhipTagEffect effect, long expiresAt,
                         long applicationSequence) {
        private Entry {
            whipStack = whipStack.copyWithCount(1);
            Objects.requireNonNull(effect, "Whip tag effect must not be null");
        }

        @Override
        public ItemStack whipStack() {
            return whipStack.copy();
        }
    }
}
