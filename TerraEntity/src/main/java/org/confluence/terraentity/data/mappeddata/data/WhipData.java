package org.confluence.terraentity.data.mappeddata.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record WhipData(Optional<Float> damage,
                       Optional<Float> markDamage,
                       Optional<Float> range,
                       Optional<Float> speed,
                       Optional<Integer> cd,
                       Optional<Boolean> canPenetrate) {
    public static Codec<WhipData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("damage").forGetter(WhipData::damage),
            Codec.FLOAT.optionalFieldOf("markDamage").forGetter(WhipData::markDamage),
            Codec.FLOAT.optionalFieldOf("range").forGetter(WhipData::range),
            Codec.FLOAT.optionalFieldOf("speed").forGetter(WhipData::speed),
            Codec.INT.optionalFieldOf("cd").forGetter(WhipData::cd),
            Codec.BOOL.optionalFieldOf("canPenetrate").forGetter(WhipData::canPenetrate)
    ).apply(instance, WhipData::new));

    public static WhipData of(float damage, float markDamage, float range, float speed, int cd, boolean canPenetrate){
        return new WhipData(Optional.of(damage), Optional.of(markDamage), Optional.of(range), Optional.of(speed), Optional.of(cd), Optional.of(canPenetrate));
    }

    public static WhipData empty(){
        return new WhipData(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }
    public static Builder builder(){
        return new Builder();
    }
    public static class Builder{
        private Optional<Float> damage = Optional.empty();
        private Optional<Float> markDamage = Optional.empty();
        private Optional<Float> range = Optional.empty();
        private Optional<Float> speed = Optional.empty();
        private Optional<Integer> cd = Optional.empty();
        private Optional<Boolean> canPenetrate = Optional.empty();

        public Builder setDamage(float damage){
            this.damage = Optional.of(damage);
            return this;
        }

        public Builder setMarkDamage(float markDamage){
            this.markDamage = Optional.of(markDamage);
            return this;
        }

        public Builder setRange(float range){
            this.range = Optional.of(range);
            return this;
        }

        public Builder setSpeed(float speed){
            this.speed = Optional.of(speed);
            return this;
        }

        public Builder setCd(int cd){
            this.cd = Optional.of(cd);
            return this;
        }

        public Builder setCanPenetrate(boolean canPenetrate){
            this.canPenetrate = Optional.of(canPenetrate);
            return this;
        }

        public WhipData build(){
            return new WhipData(damage, markDamage, range, speed, cd, canPenetrate);
        }

    }
}
