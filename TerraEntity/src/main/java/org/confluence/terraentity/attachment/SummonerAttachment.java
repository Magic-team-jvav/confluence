package org.confluence.terraentity.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.network.s2c.SyncSummonPacket;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chester.ChesterConditionalType;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 召唤师附件
 */
public class SummonerAttachment implements INBTSerializable<CompoundTag> {
    /**
     * 当前仆从栏容量
     */
    int currentCapacity = 1;
    SummonerType type;
    /**
     *  召唤物实体ID列表
     *  <p>只用于服务端，实体进入Level双向绑定</p>
     */
    List<Integer> ids = new CopyOnWriteArrayList<>();

    public Set<Integer> prismaIDs = new HashSet<>(); // 存储棱镜的序列，以确定棱镜的位置

    /**
     * 记录切斯特打开全局存储的类型的索引
     */
    public int chestType = 0;
    /**
     * 记录切斯特绑定块的位置索引
     */
    public int chestTypeAdditional;
    /**
     * 存储切斯特绑定块
     */
    public Map<Key, ChesterConditionalType> boundBlocks = new HashMap<>();

    public int beeFlyTick; // 蜜蜂坐骑飞行时间

    public boolean canBind(Key pos, Player player){
        return boundBlocks.isEmpty();
    }

    public record Key(BlockPos pos, ResourceKey<Level> levelId){
        public static final Codec<Key> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(Key::pos),
                Level.RESOURCE_KEY_CODEC.fieldOf("levelId").forGetter(Key::levelId)
        ).apply(instance, Key::new));

        @Override
        public boolean equals(Object o) {
            if(this == o){
                return true;
            }else if(o == null || getClass()!= o.getClass()){
                return false;
            }else{
                Key key = (Key) o;
                return pos.equals(key.pos) && levelId.equals(key.levelId);
            }
        }
    }

    record BandedBlockEntry (Key pos, ChesterConditionalType type){
        public static final Codec<BandedBlockEntry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Key.CODEC.fieldOf("pos").forGetter(BandedBlockEntry::pos),
                TERegistries.CHESTER_CONDITIONAL_TYPES.byNameCodec().fieldOf("type").forGetter(BandedBlockEntry::type)
        ).apply(instance, BandedBlockEntry::new));
    }

    public static final Codec<Map<Key, ChesterConditionalType>> bandedBlocksCodec = BandedBlockEntry.CODEC.listOf().xmap(ins->{
        Map<Key, ChesterConditionalType> map = new HashMap<>();
        for(BandedBlockEntry entry : ins){
            map.put(entry.pos(), entry.type());
        }
        return map;
    }, outs->{
        List<BandedBlockEntry> list = new ArrayList<>();
        for(Map.Entry<Key, ChesterConditionalType> entry : outs.entrySet()){
            list.add(new BandedBlockEntry(entry.getKey(), entry.getValue()));
        }
        return list;
    });


    public SummonerAttachment(SummonerType type) {
        this.type = type;
    }

    public enum SummonerType {
        MINION,
        SENTRY
    }

    /**
     * 默认同步仆从栏数据
     * @param player 玩家
     */
    public void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SyncSummonPacket(currentCapacity, (byte) type.ordinal()));
    }

//    /**
//     * 同步哨兵栏数据
//     * @param player 玩家
//     * @param type 哨兵类型 type = SENTRY
//     */
//    public void sync(ServerPlayer player, SummonerType type) {
//        PacketDistributor.sendToPlayer(player, new SyncSummonPacket(currentCapacity, (byte) type.ordinal()));
//    }

    /**
     * 移除死亡的实体，刷新仆从栏错误数据
     */
    public void refresh(ServerPlayer player) {
        int occupied = 0;
        for(int id : this.getIds()){
            Entity entity = player.level().getEntity(id);
            if(entity == null || !entity.isAlive()){
                this.getIds().remove(id);
            }
            if(entity instanceof ISummonMob mob){
                occupied += mob.getCost();
            }
        }
        this.setCurrentCapacity(getMaxCapacity(player) - occupied);
    }

    /**
     * 清除所有仆从
     */
    public void clear(ServerPlayer player){
        // 不知道为什么会导致线程安全问题
        for(int id : ids){
            var entity = player.level().getEntity(id);
            if(entity!= null && entity.isAlive()){
                entity.discard();
            }
        }
        this.ids.clear();
        this.setCurrentCapacity(getMaxCapacity(player));
    }

    /**
     * 召唤仆从
     * @param cost 栏位
     * @param id 实体ID
     */
    public void summon(int cost, int id) {
        currentCapacity -= cost;
        if(!ids.contains(id))
            ids.add(id);

    }

    /**
     * 移除仆从
     * @param cost 栏位
     * @param id 实体ID
     */
    public void remove(Player player, int cost, int id) {
        currentCapacity += cost;
        if(ids.contains(id))
            ids.remove(Integer.valueOf(id));
        if(currentCapacity > getMaxCapacity(player)){
            currentCapacity = getMaxCapacity(player);
        }
    }

    public void removeLast(Player player, int cost){
        List<Entity> summons = getEntities(player.level()).stream().filter(entity -> entity instanceof ISummonMob mob && !mob.isPet()).toList();
        if(!summons.isEmpty()){
            Entity entity = summons.getLast();
            if(entity!= null && entity.isAlive()){
                entity.discard();
            }
        }

    }


    public boolean canSummon(int cost) {
        return getCurrentCapacity() >= cost;
    }
    public boolean canRemove(int amount) {
        return true;
    }


    public static int getMaxCapacity(Player player) {
        return (int) player.getAttributeValue(TEAttributes.MINION_CAPACITY);
    }


    public int getCurrentCapacity() {
        return currentCapacity;
    }
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }



    public List<Integer> getIds() {
        return ids;
    }
    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Entity> getEntities(Level level) {
        return ids.stream().map(level::getEntity).filter(Objects::nonNull).toList();
    }


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("currentCapacity", currentCapacity);
        tag.put("container", JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, bandedBlocksCodec.encodeStart(JsonOps.INSTANCE, boundBlocks).getOrThrow()));
//        tag.putIntArray("ids", ids);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        currentCapacity = tag.getInt("currentCapacity");
        if(tag.contains("container")) {
            this.boundBlocks = bandedBlocksCodec.decode(NbtOps.INSTANCE, tag.get("container")).getOrThrow().getFirst();
        }
//        ids = Arrays.stream(tag.getIntArray("ids")).boxed().toList();
    }
}
