package com.xiaohunao.enemybanner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.enemybanner.items.ItemRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

public class BannerParameters {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, EnemyBanner.MODID);

    public static final Codec<BannerParameters> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("monsterId").forGetter(BannerParameters::getMonsterId),
                    Codec.intRange(0, 15).fieldOf("dyeColorId").forGetter(BannerParameters::getDyeColorId),
                    Codec.STRING.fieldOf("silksId").forGetter(BannerParameters::getSilksId)
            ).apply(instance, BannerParameters::new)
    );
    public static final StreamCodec<ByteBuf, BannerParameters> STREAM_CODEC;

    static {
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, BannerParameters::getMonsterId,
                ByteBufCodecs.INT, BannerParameters::getDyeColorId,
                ByteBufCodecs.STRING_UTF8, BannerParameters::getSilksId,
                BannerParameters::new
        );
    }

    public static final String KEY = "banner_parameters";

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BannerParameters>> BANNER_DATA_COMPONENT = DATA_COMPONENTS.registerComponentType(KEY,
            builder -> builder.persistent(CODEC).networkSynchronized(STREAM_CODEC));

    public static void register(IEventBus bus){
        DATA_COMPONENTS.register(bus);
    }

    private String monsterId;
    private int dyeColorId;
    private String silksId;

    public BannerParameters(){
        this("minecraft:zombie", DyeColor.WHITE.getId(), ItemRegister.BASIC_SILKS.getId().getPath());
    }

    public BannerParameters(String monsterId){
        this(monsterId, DyeColor.WHITE.getId(), ItemRegister.BASIC_SILKS.getId().getPath());
    }

    public BannerParameters(String monsterId, int dyeColorId, String silksId){
        this.monsterId = monsterId;
        this.silksId = silksId;
    }

    public String getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(String monsterId) {
        this.monsterId = monsterId;
    }

    public String getSilksId() {
        return silksId;
    }

    public void setSilksId(String silksId) {
        this.silksId = silksId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BannerParameters that = (BannerParameters) o;
        return dyeColorId == that.dyeColorId && Objects.equals(monsterId, that.monsterId) && Objects.equals(silksId, that.silksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monsterId, dyeColorId, silksId);
    }

    public int getDyeColorId() {
        return dyeColorId;
    }

    public void setDyeColorId(int dyeColorId) {
        this.dyeColorId = dyeColorId;
    }
}
