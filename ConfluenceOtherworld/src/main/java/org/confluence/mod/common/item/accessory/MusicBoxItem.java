package org.confluence.mod.common.item.accessory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.client.event.GameClientEvents;
import org.confluence.mod.common.block.functional.MusicBoxBlock;
import org.confluence.mod.mixed.IMusicManager;
import org.confluence.mod.network.c2s.ReplaceMusicBoxItemPacketC2S;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.confluence.terra_curio.util.CuriosUtils;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MusicBoxItem extends BlockItem implements ICurioItem, IFunctionCouldEnable {
    private static Map<Music, MusicBoxItem> MUSIC_2_ITEM = new HashMap<>();
    private static final Map<ResourceLocation, MusicBoxItem> SOUND_ID_2_ITEM = new Hashtable<>();
    public final @Nullable Music music;

    public MusicBoxItem(MusicBoxBlock block) {
        super(block, new Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE));
        this.music = block.music;
        if (music != null) {
            MUSIC_2_ITEM.put(music, this);
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosUtils.noSameCurio(slotContext.entity(), MusicBoxItem.class);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Level level = slotContext.entity().level();
        if (level.isClientSide && isEnabled(stack, null)) {
            MusicManager musicManager = Minecraft.getInstance().getMusicManager();
            IMusicManager manager = (IMusicManager) musicManager;
            if (music == null) {
                SoundInstance currentMusic = manager.confluence$getCurrentMusic();
                if (currentMusic != null && level.random.nextInt(540) == 0) {
                    MusicBoxItem item = SOUND_ID_2_ITEM.get(currentMusic.getLocation());
                    if (item == null) return;
                    // todo 成功音效
                    ReplaceMusicBoxItemPacketC2S.sendToServer(slotContext.index(), item);
                }
            } else {
                if (!musicManager.isPlayingMusic(music)) {
                    musicManager.stopPlaying();
                    musicManager.startPlaying(music);
                }
                /**
                 * @see GameClientEvents#clientTick$Post(ClientTickEvent.Post) 1st
                 * @see MusicBoxBlock.Entity#clientTick(Level, BlockPos, BlockState, MusicBoxBlock.Entity) 3rd
                 */
                manager.confluence$setMusicBoxOccupied(IMusicManager.State.ACCESSORY); // 2nd
            }
        }
    }

    public static void initialize() {
        for (Map.Entry<Music, MusicBoxItem> entry : MUSIC_2_ITEM.entrySet()) {
            SOUND_ID_2_ITEM.put(entry.getKey().getEvent().value().getLocation(), entry.getValue());
        }
        MUSIC_2_ITEM = null;
    }

    public static void register(Holder<SoundEvent> holder, MusicBoxItem musicBoxItem) {
        SOUND_ID_2_ITEM.put(holder.value().getLocation(), musicBoxItem);
    }

    public static void register(ResourceLocation id, MusicBoxItem musicBoxItem) {
        SOUND_ID_2_ITEM.put(id, musicBoxItem);
    }
}
