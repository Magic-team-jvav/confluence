package org.confluence.terraentity.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.terraentity.entity.util.trail.player.ColorfulItemInHandTrail;
import org.confluence.terraentity.entity.util.trail.player.ItemInHandTail;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.item.TESummonItems;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 手持物品拖尾注册器
 */
public class ItemInHandTrailAttachment implements INBTSerializable<Tag> {

    public ItemInHandTail trail;

    public int trailTicks = 0;
    public float sliderProgress = 0.003f;

    float colorProgress = 0;
    int colorFrom = 0x1FE6C0;
    int colorTo = 0xC67C28;


    static Map<Item, Operator> registry = new HashMap<>();

    public int tickColor(Player player){
        float d = (player.getRandom().nextFloat() - 0.5f) * 0.05f;

        colorProgress = Mth.clamp(colorProgress + d + this.sliderProgress, 0, 1);
        if(colorProgress >= 1){
            this.sliderProgress = -0.003f;
        }else if(colorProgress <= 0){
            this.sliderProgress = 0.003f;
        }
        return lerpColor(colorFrom, colorTo, colorProgress);
    }

    private int lerpColor(int from, int to, float t) {
        int r = (int) ((from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * t);
        int g = (int) ((from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * t);
        int b = (int) ((from & 255) + ((to & 255) - (from & 255)) * t);
        return (r << 16) + (g << 8) + b;
    }


    static class Operator {
        Function<Player, Boolean> checkAdditionCondition;
        ItemInHandTail trail;
        public Operator(Function<Player, Boolean> checkAdditionCondition, ItemInHandTail trail) {
            this.checkAdditionCondition = checkAdditionCondition;
            this.trail = trail;
        }
    }

    public static void register(Item item, Function<Player, Boolean> checkAdditionCondition, ItemInHandTail trail){
        registry.put(item, new Operator(checkAdditionCondition, trail));
    }

    public static void register(Item item, ItemInHandTail trail){
        registry.put(item, new Operator((player) -> true, trail));
    }

    public static void registerDefault(){
        ItemInHandTrailAttachment.register(TESummonItems.TERRAPRISMA.asItem(), new ColorfulItemInHandTrail(1, 0.15f, 8));
        ItemInHandTrailAttachment.register(TESummonItems.SUMMON_DIAMOND_SWORD_STAFF.asItem(), new ItemInHandTail(1, 0.15f, 0x0000FF, 8));
    }


    @Nullable
    public static ItemInHandTail updateTrails(Player player){
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Item item = stack.getItem();
        ItemInHandTrailAttachment data = player.getData(TEAttachments.TRAIL_STORAGE);
        if(registry.containsKey(item)){
            // 如果已注册物品拖尾
            Operator operator = registry.get(item);
            if(data.trail == operator.trail){
                // 没有变化
                return data.trail;
            }
            if(operator.checkAdditionCondition.apply(player)){
                // 条件满足，添加拖尾
                if(data.trail != null) {
                    // 之前有拖尾，清空
                    data.trail.trailsQueue.clear();
                }
                data.trail = operator.trail;
                return data.trail;
            }
        }
        return null;
    }



    @Override
    public @UnknownNullability Tag serializeNBT(HolderLookup.Provider provider) {return new CompoundTag();}

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {}
}
