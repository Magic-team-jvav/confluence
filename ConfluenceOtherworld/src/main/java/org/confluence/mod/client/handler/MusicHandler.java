package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.client.ModMusics;
import org.confluence.mod.common.init.ModTags;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MusicHandler {
    private static Music nextSong;
    private static int nextSongDelay = 1200;
    private static Holder<Biome> lastBiome;
    private static int nextBiomeCheck = 100;
    private static float volume = 1.0F;

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
        if (nextSong == null) {
            BlockPos pos = player.blockPosition();
            Holder<Biome> biome = lastBiome == null ? player.level().getBiome(pos) : lastBiome;

            if (biome.is(Tags.Biomes.IS_DESERT)) {
                nextSong = ModMusics.DESERT;
            } else if (biome.is(Tags.Biomes.IS_COLD_OVERWORLD)) {
                nextSong = ModMusics.ICE;
            } else if (biome.is(ModTags.Biomes.THE_CORRUPTION)) {
                if (pos.getY() < 40) {
                    nextSong = ModMusics.UNDERGROUND_CORRUPTION;
                } else {
                    nextSong = ModMusics.CORRUPTION;
                }
            } else if (biome.is(ModTags.Biomes.TR_CRIMSON)) {
                if (pos.getY() < 40) {
                    nextSong = ModMusics.UNDERGROUND_CRIMSON;
                } else {
                    nextSong = ModMusics.CRIMSON;
                }
            } else {
                nextSong = ModMusics.OVERWORLD_DAY;
            }
        }
        if (nextSongDelay-- <= 0) {
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
            }
        }
    }
}
