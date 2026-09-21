package org.confluence.mod.common.summoner.attachment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.summoner.info.NumberInfo;
import org.confluence.mod.client.summoner.info.TextInfo;
import org.confluence.mod.common.summoner.network.SummonerBatchedInfoPayload;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.portlib.event.tick.PortLevelTickEvent;
import org.mesdag.portlib.network.PortPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class InfoData {

    public static void record(Level level, float amount, Vec3 pos, Vec3 velocity, Type type) {
        if (false && !level.isClientSide()) {
            InfoData data = level.getData(SummonerAttachmentTypes.INFO);
            data.pendingNumbers.add(new SummonerBatchedInfoPayload.Number(type, amount, pos, velocity));
        }
    }

    public static void record(Level level, Component text, Vec3 pos, Vec3 velocity) {
        if (false && !level.isClientSide()) {
            InfoData data = level.getData(SummonerAttachmentTypes.INFO);
            data.pendingTexts.add(new SummonerBatchedInfoPayload.Text(text, pos, velocity));
        }
    }

    public enum Type {
        DAMAGE(0xFFCD04),
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
    public final List<SummonerBatchedInfoPayload.Number> pendingNumbers = new ArrayList<>();
    public final List<SummonerBatchedInfoPayload.Text> pendingTexts = new ArrayList<>();
    /// 客户端：两类活跃渲染数据
    public final List<NumberInfo> numbers = new ArrayList<>();
    public final List<TextInfo> texts = new ArrayList<>();

    public static void tick(PortLevelTickEvent.Post event) {
        Level level = event.getLevel();
        InfoData data = level.getData(SummonerAttachmentTypes.INFO);
        if (level.isClientSide()) {
            data.numbers.removeIf(NumberInfo::tick);
            data.texts.removeIf(TextInfo::tick);
        } else if (!data.pendingNumbers.isEmpty() || !data.pendingTexts.isEmpty()) {
            SummonerBatchedInfoPayload payload = new SummonerBatchedInfoPayload(List.copyOf(data.pendingNumbers), List.copyOf(data.pendingTexts));
            data.pendingNumbers.clear();
            data.pendingTexts.clear();
            PortPacketDistributor.sendToPlayersInDimension(level.dimension(), payload);
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
