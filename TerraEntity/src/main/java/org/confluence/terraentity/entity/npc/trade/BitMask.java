package org.confluence.terraentity.entity.npc.trade;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public class BitMask {

    public List<Long> bitMask;

    public static Codec<BitMask> CODEC = Codec.LONG.listOf().xmap(BitMask::new, BitMask::bitMask);
    public static StreamCodec<ByteBuf, BitMask> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    private List<Long> bitMask() {
        if(bitMask == null){
            bitMask = new ArrayList<>();
        }
        return bitMask;
    }

    public static BitMask create() {
        return new BitMask(new ArrayList<>());
    }

    public BitMask(List<Long> bitMask) {
        this.bitMask = new ArrayList<>(bitMask);
    }

    public boolean add(int index){
        if(index < 0){
            throw new IllegalArgumentException("Index must be between 0 and 63");
        }else{
            boolean added = false;
            int count = bitMask.size();
            int indexToAdd = index % 64;
            int indexToAddInArray = index / 64;
            long mask = 1L << indexToAdd;
            if(indexToAddInArray >= count){
                for(int i = count; i < indexToAddInArray; i++){
                    bitMask.add(0L);
                }
                bitMask.add(mask);
                added = true;
            }
            else{
                long current = bitMask.get(indexToAddInArray);
                if((current & mask) == 0){
                    bitMask.set(indexToAddInArray, current | mask);
                    added = true;
                }
            }

            return added;
        }
    }

    public boolean remove(int index){
        if(index < 0){
            throw new IllegalArgumentException("Index must be between 0 and 63");
        }else{
            boolean removed = false;
            int indexToRemove = index % 64;
            int indexToRemoveInArray = index / 64;
            long mask = 1L << indexToRemove;
            if(indexToRemoveInArray < bitMask.size()){
                long current = bitMask.get(indexToRemoveInArray);
                if((current & mask) != 0){
                    bitMask.set(indexToRemoveInArray, current & ~mask);
                    removed = true;
                }
            }

            return removed;
        }
    }

    public void clear(){
        bitMask.clear();
    }

    public boolean contains(int index){
        if(index < 0){
            throw new IllegalArgumentException("Index must be between 0 and 63");
        }else{
            int indexToCheck = index % 64;
            int indexToCheckInArray = index / 64;
            if(indexToCheckInArray >= bitMask.size()){
                return false;
            }
            long mask = 1L << indexToCheck;
            return (bitMask.get(indexToCheckInArray) & mask) != 0;
        }
    }

    public List<Integer> getIndexes(){
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < bitMask.size(); i++){
            long mask = bitMask.get(i);
            for(int j = 0; j < 64; j++){
                if((mask & (1L << j)) != 0){
                    indexes.add(i * 64 + j);
                }
            }
        }
        return indexes;
    }

    @Override
    public String toString(){
        String s = "";
        for(int i = 0; i < bitMask.size(); i++){
            for(int j = 0; j < 64; j++){
                if((bitMask.get(i) & (1L << j)) != 0){
                    s += i * 64 + j + " ";
                }
            }
        }
        return s;
    }
}
