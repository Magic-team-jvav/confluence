package org.confluence.mod.network.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.ClientUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public final class RequestAchievementsPacketS2C implements IPacketS2C {
    public static final RequestAchievementsPacketS2C INSTANCE = new RequestAchievementsPacketS2C();
    public static final Type<RequestAchievementsPacketS2C> TYPE = Confluence.createType("request_achievements");
    public static final StreamCodec<ByteBuf, RequestAchievementsPacketS2C> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RequestAchievementsPacketS2C() {}

    @Override
    public void handle(IPayloadContext context) {
        UUID id = ClientUtils.getGameProfile().getId();
        Path path = AchievementUtils.CONFLUENCE_ACHIEVEMENTS_DIR.resolve(id + ".json");
        PlayerAdvancements.Data data = null;
        if (Files.isRegularFile(path)) {
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                reader.setLenient(false);
                JsonElement element = Streams.parse(reader);
                data = AchievementUtils.getCodecClientOnly().parse(JsonOps.INSTANCE, element).getOrThrow(JsonParseException::new);
            } catch (JsonIOException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't access confluence achievements in {}", path, ioexception);
            } catch (JsonParseException jsonParseException) {
                Confluence.LOGGER.error("Couldn't parse confluence achievements in {}", path, jsonParseException);
            }
        }
        if (data == null) {
            data = new PlayerAdvancements.Data(Map.of());
        }
        context.reply(new ReplyAchievementsPacketC2S(id, data));
    }

    @Override
    public void work(Player player) {}

    @Override
    public Type<RequestAchievementsPacketS2C> type() {
        return TYPE;
    }
}
