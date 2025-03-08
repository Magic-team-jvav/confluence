package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.init.TEEntities;

import java.util.Map;

import static org.confluence.mod.client.ModMusics.*;

@OnlyIn(Dist.CLIENT)
public final class MusicHandler {
    private static CachedLocationMusic nextSong;
    private static int nextSongDelay = 1200;
    private static Holder<Biome> lastBiome;
    private static int nextBiomeCheck = 100;
    private static float volume = 1.0F;
    private static boolean hasBossMusic = false;

    public static void handle(SelectMusicEvent event, LocalPlayer player) {
        if (nextBiomeCheck-- <= 0) {
            Holder<Biome> biome = player.level().getBiome(player.blockPosition());
            if (biome != lastBiome) {
                lastBiome = biome;
                nextSongDelay = 0;
                nextSong = null;
            }
            nextBiomeCheck = 100;
        }
        selectBossMusic(player);
        if (nextSong == null) {
            selectMusic(player);
        }
        SoundInstance playingMusic = event.getPlayingMusic();
        if ((playingMusic == null || (nextSong != null && !nextSong.getLocation().equals(playingMusic.getLocation())) && nextSongDelay-- <= 0)) {
            if (volume > 0.0F) {
                volume -= 0.01F;
                Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel = Minecraft.getInstance().getSoundManager().soundEngine.instanceToChannel;
                for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry : instanceToChannel.entrySet()) {
                    if (entry.getKey().getSource() != SoundSource.MUSIC) continue;
                    entry.getValue().execute(channel -> {
                        if (volume <= 0.0F) {
                            channel.stop();
                        } else {
                            channel.setVolume(volume);
                        }
                    });
                }
            } else {
                Minecraft.getInstance().getMusicManager().stopPlaying();
                event.setMusic(nextSong);
                nextSongDelay = 1200;
                volume = 1.0F;
                nextSong = null;
            }
        }
    }

    public static void clear() {
        nextSong = null;
        nextSongDelay = 1200;
        lastBiome = null;
        nextBiomeCheck = 100;
        volume = 1.0F;
        hasBossMusic = false;
    }

    private static void selectBossMusic(Player player) {
        if (Minecraft.getInstance().gui.getBossOverlay().shouldPlayMusic()) {
            AABB area = new AABB(player.blockPosition()).inflate(Minecraft.getInstance().levelRenderer.getLastViewDistance());
            for (AbstractTerraBossBase<?> boss : player.level().getEntitiesOfClass(AbstractTerraBossBase.class, area)) {
                if (boss.getType() == TEEntities.KING_SLIME.get()) {
                    nextSong = KING_SLIME;
                } else if (boss.getType() == TEEntities.EYE_OF_CTHULHU.get()) {
                    nextSong = EYE_OF_CTHULHU;
                } else if (boss.getType() == TEEntities.EATER_OF_WORLDS.get()) {
                    nextSong = EATER_OF_WORLDS;
                } else if (boss.getType() == TEEntities.BRAIN_OF_CTHULHU.get()) {
                    nextSong = BRAIN_OF_CTHULHU;
                } else if (boss.getType() == TEEntities.QUEEN_BEE.get()) {
                    nextSong = QUEEN_BEE;
                }
                if (nextSong != null) {
                    hasBossMusic = true;
                    nextSongDelay = 0;
                    break;
                }
            }
        } else if (hasBossMusic && nextSong.getLocation().getPath().endsWith("_combat")) {
            hasBossMusic = false;
            nextSong = null;
            nextSongDelay = 0;
        }
    }

    // todo eerie, high_wind, slime_rain, town_day, town_night, aether
    private static void selectMusic(LocalPlayer player) {
        BlockPos pos = player.blockPosition();
        int y = pos.getY();
        Level level = player.level();
        Holder<Biome> biome = lastBiome == null ? level.getBiome(pos) : lastBiome;

        if (y > 260) {
            nextSong = SPACE;
        } else if (level.isRaining()) {
            long dayTime = level.getDayTime();
            if (dayTime >= 22500 || dayTime <= 1500) {
                nextSong = MORNING_RAIN;
            } else {
                nextSong = RAIN;
            }
        } else if (level.isThundering()) {
            nextSong = STORM;
        } else if (biome.is(ModBiomes.GLOWING_MUSHROOM)) {
            nextSong = MUSHROOMS;
        } else if (biome.is(Tags.Biomes.IS_ICY) || biome.is(Tags.Biomes.IS_SNOWY)) {
            switchMusic(y, ICE, UNDERGROUND_ICE);
        } else if (biome.is(ModTags.Biomes.THE_CORRUPTION)) {
            switchMusic(y, CORRUPTION, UNDERGROUND_CORRUPTION);
        } else if (biome.is(ModTags.Biomes.TR_CRIMSON)) {
            switchMusic(y, CRIMSON, UNDERGROUND_CRIMSON);
        } else if (biome.is(ModTags.Biomes.THE_HALLOW)) {
            switchMusic(y, HALLOW, UNDERGROUND_HALLOW);
        } else if (biome.is(Tags.Biomes.IS_DESERT)) {
            nextSong = DESERT;
        } else if (biome.is(Tags.Biomes.IS_OCEAN)) {
            if (level.getDayTime() % 24000 < 12000) {
                nextSong = OCEAN;
            } else {
                nextSong = OCEAN_NIGHT;
            }
        } else if (biome.is(Tags.Biomes.IS_JUNGLE)) {
            if (y < 40) {
                nextSong = UNDERGROUND_JUNGLE;
            } else {
                if (level.getDayTime() % 24000 < 12000) {
                    nextSong = JUNGLE;
                } else {
                    nextSong = JUNGLE_NIGHT;
                }
            }
        } else {
            if (y < 40) {
                nextSong = player.getRandom().nextBoolean() ? UNDERGROUND : ALTERNATE_UNDERGROUND;
            } else {
                if (level.getDayTime() % 24000 < 12000) {
                    nextSong = player.getRandom().nextBoolean() ? OVERWORLD_DAY : ALTERNATE_DAY;
                } else {
                    nextSong = OVERWORLD_NIGHT;
                }
            }
        }
    }

    private static void switchMusic(int y, CachedLocationMusic surface, CachedLocationMusic underground) {
        if (y < 40) {
            nextSong = underground;
        } else {
            nextSong = surface;
        }
    }
}
