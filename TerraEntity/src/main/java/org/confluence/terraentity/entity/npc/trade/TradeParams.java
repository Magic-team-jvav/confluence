package org.confluence.terraentity.entity.npc.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeTask;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>npc任务表参数，用于更新交易任务
 * <p>而不需要使用局部更新任务列表实例
 * @param params 交易表序列号对应的参数表
 * @param bitMask 交易表的位掩码,为0表示启用，1表示禁用
 */
public record TradeParams(Map<Integer, Param> params, BitMask bitMask) {

    public static final String KEY = "npc_trade_params";

    public static TradeParams create(){
        return new TradeParams(new HashMap<>(), BitMask.create());
    }

    // 可以扩展参数数量

    /**
     * 交易表参数
     */
    public static class Param {
        private int level;
        private Optional<Boolean> isReady;

        public int level() {
            return level;
        }
        public Optional<Boolean> isReady() {
            return isReady;
        }
        /**
         * @param level 等级
         * @param isReady 是否准备好，当某些任务使用{@link ITradeTask#canTrade(ITradeHolder, int) 额外判断}条件时使用
         */
        public Param(int level, Optional<Boolean> isReady) {
            this.level = level;
            this.isReady = isReady;
        }

        public static Codec<Param> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("i").forGetter(Param::level),
                Codec.BOOL.optionalFieldOf("b").forGetter(Param::isReady)
        ).apply(instance, Param::new));

        public static Builder builder(){
            return new Builder();
        }

        public static class Builder{
            private int current;
            private Boolean isReady;

            public Builder current(int current){
                this.current = current;
                return this;
            }

            public Builder isReady(boolean isReady){
                this.isReady = isReady;
                return this;
            }

            public Param build(){
                return new Param(current, Optional.ofNullable(isReady));
            }
        }

    }

    public static Codec<TradeParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Param.CODEC).fieldOf("params").forGetter(param->param.params.entrySet().stream()
                    .map(entry->new AbstractMap.SimpleEntry<>(entry.getKey().toString(), entry.getValue()))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))),
            BitMask.CODEC.fieldOf("bit_mask").forGetter(TradeParams::bitMask)
    ).apply(instance, (params, bitMask)->{
                return new TradeParams(params.entrySet()
                        .stream()
                        .map(entry->new AbstractMap.SimpleEntry<>(Integer.parseInt(entry.getKey()), entry.getValue()))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)),
                        bitMask
                );
            }));

    public static StreamCodec<ByteBuf, TradeParams> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);


    public boolean isEmpty(){
        return params.isEmpty();
    }

    public void setLevel(int index, int value){
        if(!params.containsKey(index)){
            params.put(index, Param.builder().current(value).build());
        }
        params.get(index).level = value;
    }

    public void increaseLevel(int index){
        setLevel(index, getLevel(index)+1);
    }

    public int getLevel(int index){
        if(!params.containsKey(index)){
            return 0;
        }
        return params.get(index).level;
    }


    public void setIsReady(int index, boolean value){
        if(!params.containsKey(index)){
            params.put(index, Param.builder().current(0).build());
        }
        params.get(index).isReady = Optional.of(value);
    }

    public boolean isReady(int index){
        if(!params.containsKey(index)){
            return true;
        }
        return params.get(index).isReady.orElse(true);
    }




}
