package org.confluence.mod.common.attachment;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.mixed.ILibDamageSource;
import org.confluence.mod.client.ModClientConfigs;
import org.confluence.mod.client.info.NumberInfo;
import org.confluence.mod.client.info.TextInfo;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityDamageSource;
import org.confluence.mod.common.summoner.summonMark.SummonMarkInstance;
import org.confluence.mod.network.s2c.SummonerBatchedInfoS2C;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public final class InfoData {

    public static void record(Level level, float amount, Vec3 pos, Vec3 velocity, Type type) {
        if (!level.isClientSide()) {
            InfoData data = level.getData(ModAttachmentTypes.SUMMONER_INFO);
            data.pendingNumbers.add(new SummonerBatchedInfoS2C.Number(type, amount, pos, velocity));
        }
    }

    public static void record(Level level, Component text, Vec3 pos, Vec3 velocity) {
        if (!level.isClientSide()) {
            InfoData data = level.getData(ModAttachmentTypes.SUMMONER_INFO);
            data.pendingTexts.add(new SummonerBatchedInfoS2C.Text(text, pos, velocity));
        }
    }

    public static void sync(Player player, List<SummonerBatchedInfoS2C.Number> numbers, List<SummonerBatchedInfoS2C.Text> texts) {
        InfoData infoData = player.level().getData(ModAttachmentTypes.SUMMONER_INFO);
        Vec3 eye = player.getEyePosition();
        for (SummonerBatchedInfoS2C.Number entry : numbers) {
            /// 只有虚拟模式才接收数值：粒子模式的数字由原版指示粒子显示，关闭模式两者都不显示。
            if (!(entry.type() == Type.HEAL ? ModClientConfigs.healIndicator : ModClientConfigs.damageIndicator).isVirtual())
                continue;
            NumberInfo info = new NumberInfo(entry.type(), entry.amount(), entry.pos(), entry.velocity());
            if (info.getRenderPos(0.0F).distanceToSqr(eye) <= 64.0D * 64.0D) {
                infoData.numbers.add(info);
            }
        }
        for (SummonerBatchedInfoS2C.Text entry : texts) {
            TextInfo info = new TextInfo(entry.text(), entry.pos(), entry.velocity());
            if (info.getRenderPos(0.0F).distanceToSqr(eye) <= 64.0D * 64.0D) {
                infoData.texts.add(info);
            }
        }
    }

    /// 治疗数字只显示实际可恢复的血量，满血、死亡、无效和非正治疗均不生成数值。
    public static float effectiveHealAmount(float amount, LivingEntity living) {
        if (!living.isAlive() || !Float.isFinite(amount) || amount <= 0) return 0;
        return Math.max(0, Math.min(amount, living.getMaxHealth() - living.getHealth()));
    }

    public static void post(ServerLevel level, float amount, @Nullable Entity attacker, LivingEntity victim, DamageSource damageSource) {
        if (amount >= 0.01F && attacker instanceof LivingEntity living && victim != living) {
            if (victim instanceof Player player) {
                player.getData(ModAttachmentTypes.SUMMONER_TARGET_CACHE).record(living, 200);
            }
            if (living instanceof Player player) {
                player.getData(ModAttachmentTypes.SUMMONER_TARGET_CACHE).record(victim, 200);
                if (damageSource instanceof AttachmentEntityDamageSource a) {
                    WhipMarkTracker tracker = living.getData(ModAttachmentTypes.SUMMON_MARK_DATA);
                    if (tracker.isSummonMarkTarget(victim)) {
                        List<SummonMarkInstance> summonMarkInstances = tracker.getSummonMarkInstances();
                        for (SummonMarkInstance instance : summonMarkInstances) {
                            instance.getType().damagePost(tracker, instance, victim, a, amount);
                        }
                    }
                }
            }
        }
        Vec3 pos = victim instanceof BaseBoss boss ? boss.getDamageFeedbackTarget().getBoundingBox().getCenter() : victim.getBoundingBox().getCenter();
        Vec3 velocity = new Vec3(0, 2, 0).offsetRandom(level.getRandom(), 0.5F).subtract(Vec3.ZERO).scale(0.4);
        ILibDamageSource lds = ILibDamageSource.of(damageSource);
        record(level, amount, pos, velocity, lds != null && lds.confluence$isCritical() ? Type.CRITICAL : Type.DAMAGE);
    }

    public static void heal(float amount, ServerLevel level, LivingEntity living) {
        amount = effectiveHealAmount(amount, living);
        if (amount >= 0.01F) {
            Vec3 pos = living.getBoundingBox().getCenter();
            Vec3 velocity = new Vec3(0, 2, 0).offsetRandom(level.getRandom(), 0.5F).subtract(Vec3.ZERO).scale(0.4);
            record(level, amount, pos, velocity, Type.HEAL);
        }
    }

    public enum Type {
        DAMAGE(0xff9400),
        CRITICAL(0xFF0421),
        HEAL(0x55FF55);

        private final int color;

        Type(int color) {
            this.color = color;
        }

        public int color() {
            return color;
        }
    }

    /// 服务端：本 tick 待发包的两类条目
    public final List<SummonerBatchedInfoS2C.Number> pendingNumbers = new ArrayList<>();
    public final List<SummonerBatchedInfoS2C.Text> pendingTexts = new ArrayList<>();
    /// 客户端：两类活跃渲染数据
    public final List<NumberInfo> numbers = new ArrayList<>();
    public final List<TextInfo> texts = new ArrayList<>();

    public static void tick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        InfoData data = level.getData(ModAttachmentTypes.SUMMONER_INFO);
        if (level.isClientSide()) {
            data.numbers.removeIf(NumberInfo::tick);
            data.texts.removeIf(TextInfo::tick);
        } else if (!data.pendingNumbers.isEmpty() || !data.pendingTexts.isEmpty()) {
            SummonerBatchedInfoS2C payload = new SummonerBatchedInfoS2C(List.copyOf(data.pendingNumbers), List.copyOf(data.pendingTexts));
            data.pendingNumbers.clear();
            data.pendingTexts.clear();
            if (level instanceof ServerLevel serverLevel) {
                PacketDistributor.sendToPlayersInDimension(serverLevel, payload);
            }
        }
    }

    public boolean isEmpty() {
        return numbers.isEmpty() && texts.isEmpty();
    }

    public List<NumberInfo> getNumbers() {
        return numbers;
    }

    public List<TextInfo> getTexts() {
        return texts;
    }
}
