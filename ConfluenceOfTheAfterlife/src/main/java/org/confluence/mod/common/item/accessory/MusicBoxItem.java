package org.confluence.mod.common.item.accessory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.block.functional.MusicBoxBlock;
import org.confluence.mod.mixed.IMusicManager;
import org.confluence.mod.network.c2s.ReplaceMusicBoxItemPacketC2S;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.confluence.terra_curio.util.CuriosUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Hashtable;
import java.util.Map;

public class MusicBoxItem extends BlockItem implements ICurioItem, IFunctionCouldEnable {
    private static final Map<ResourceLocation, MusicBoxItem> SOUND_ID_2_ITEM = new Hashtable<>();
    public final @Nullable Music music;

    public MusicBoxItem(@Nullable Music music, MusicBoxBlock block) {
        super(block, new Properties().stacksTo(1).component(TCDataComponentTypes.MOD_RARITY, ModRarity.ORANGE));
        this.music = music;
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
            if (music == null) {
                SoundInstance currentMusic = ((IMusicManager) musicManager).confluence$getCurrentMusic();
                if (currentMusic != null && level.random.nextInt(540) == 0) {
                    MusicBoxItem item = SOUND_ID_2_ITEM.get(currentMusic.getLocation());
                    if (item == null) return;
                    // todo 成功音效
                    ReplaceMusicBoxItemPacketC2S.sendToServer(slotContext.index(), item);
                }
            } else if (!musicManager.isPlayingMusic(music)) {
                musicManager.startPlaying(music);
            }
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(getDescriptionId()).withStyle(style -> style.withColor(stack.get(TCDataComponentTypes.MOD_RARITY).getColor()));
    }

    public static void initialize() {
        // todo 注册音乐->物品
    }

    public static void register(Holder<SoundEvent> holder, MusicBoxItem musicBoxItem) {
        SOUND_ID_2_ITEM.put(holder.value().getLocation(), musicBoxItem);
    }

    public static void register(ResourceLocation id, MusicBoxItem musicBoxItem) {
        SOUND_ID_2_ITEM.put(id, musicBoxItem);
    }
}
