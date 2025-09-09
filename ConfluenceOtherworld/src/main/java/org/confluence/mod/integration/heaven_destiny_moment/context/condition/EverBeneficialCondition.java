package org.confluence.mod.integration.heaven_destiny_moment.context.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.heaven_destiny_moment.common.automation.AutomationContext;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.ICondition;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.attachment.EverBeneficial;

import java.util.Optional;

public record EverBeneficialCondition(Optional<Integer> lifeCrystals,
                                      Optional<Integer> lifeFruits,
                                      Optional<Boolean> vitalCrystal,
                                      Optional<Boolean> aegisApple,
                                      Optional<Boolean> ambrosia,
                                      Optional<Boolean> gummyWorm,
                                      Optional<Boolean> galaxyPearl,
                                      Optional<Boolean> minecartUpgradeKit,
                                      Optional<Boolean> artisanLoafUsed
                                      ) implements ICondition {

    public static final MapCodec<EverBeneficialCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("lifeCrystals").forGetter(EverBeneficialCondition::lifeCrystals),
            Codec.INT.optionalFieldOf("lifeFruits").forGetter(EverBeneficialCondition::lifeFruits),
            Codec.BOOL.optionalFieldOf("vitalCrystal").forGetter(EverBeneficialCondition::vitalCrystal),
            Codec.BOOL.optionalFieldOf("aegisApple").forGetter(EverBeneficialCondition::aegisApple),
            Codec.BOOL.optionalFieldOf("ambrosia").forGetter(EverBeneficialCondition::ambrosia),
            Codec.BOOL.optionalFieldOf("gummyWorm").forGetter(EverBeneficialCondition::gummyWorm),
            Codec.BOOL.optionalFieldOf("galaxyPearl").forGetter(EverBeneficialCondition::galaxyPearl),
            Codec.BOOL.optionalFieldOf("minecartUpgradeKit").forGetter(EverBeneficialCondition::minecartUpgradeKit),
            Codec.BOOL.optionalFieldOf("artisanLoafUsed").forGetter(EverBeneficialCondition::artisanLoafUsed)
    ).apply(instance, EverBeneficialCondition::new));

    public boolean matches(AutomationContext context) {
        if (context.player().isEmpty()) {
            return false;
        }

        Player player = context.player().get();
        EverBeneficial everBeneficial = EverBeneficial.of(player);

        if (lifeCrystals.isPresent() && everBeneficial.getUsedLifeCrystals() < lifeCrystals.get()) {
            return false;
        }
        if (lifeFruits.isPresent() && everBeneficial.getUsedLifeFruits() < lifeFruits.get()) {
            return false;
        }
        if (vitalCrystal.isPresent() && everBeneficial.isVitalCrystalUsed() != vitalCrystal.get()) {
            return false;
        }
        if (aegisApple.isPresent() && everBeneficial.isAegisAppleUsed() != aegisApple.get()) {
            return false;
        }
        if (ambrosia.isPresent() && everBeneficial.isAmbrosiaUsed() != ambrosia.get()) {
            return false;
        }
        if (gummyWorm.isPresent() && everBeneficial.isGummyWormUsed() != gummyWorm.get()) {
            return false;
        }
        if (galaxyPearl.isPresent() && everBeneficial.isGalaxyPearlUsed() != galaxyPearl.get()) {
            return false;
        }
        if (minecartUpgradeKit.isPresent() && everBeneficial.isMinecartUpgradeKitUsed() != minecartUpgradeKit.get()) {
            return false;
        }
        if (artisanLoafUsed.isPresent() && everBeneficial.isArtisanLoafUsed() != artisanLoafUsed.get()) {
            return false;
        }

        return true;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private Integer lifeCrystals;
        private Integer lifeFruits;
        private Boolean vitalCrystal;
        private Boolean aegisApple;
        private Boolean ambrosia;
        private Boolean gummyWorm;
        private Boolean galaxyPearl;
        private Boolean minecartUpgradeKit;
        private Boolean artisanLoafUsed;

        public Builder lifeCrystals(Integer value) {
            this.lifeCrystals = value;
            return this;
        }

        public Builder lifeFruits(Integer value) {
            this.lifeFruits = value;
            return this;
        }

        public Builder vitalCrystal(Boolean value) {
            this.vitalCrystal = value;
            return this;
        }

        public Builder aegisApple(Boolean value) {
            this.aegisApple = value;
            return this;
        }

        public Builder ambrosia(Boolean value) {
            this.ambrosia = value;
            return this;
        }

        public Builder gummyWorm(Boolean value) {
            this.gummyWorm = value;
            return this;
        }

        public Builder galaxyPearl(Boolean value) {
            this.galaxyPearl = value;
            return this;
        }

        public Builder minecartUpgradeKit(Boolean value) {
            this.minecartUpgradeKit = value;
            return this;
        }

        public Builder artisanLoafUsed(Boolean value) {
            this.artisanLoafUsed = value;
            return this;
        }

        public EverBeneficialCondition build() {
            return new EverBeneficialCondition(
                    Optional.ofNullable(lifeCrystals),
                    Optional.ofNullable(lifeFruits),
                    Optional.ofNullable(vitalCrystal),
                    Optional.ofNullable(aegisApple),
                    Optional.ofNullable(ambrosia),
                    Optional.ofNullable(gummyWorm),
                    Optional.ofNullable(galaxyPearl),
                    Optional.ofNullable(minecartUpgradeKit),
                    Optional.ofNullable(artisanLoafUsed));
        }
    }
}
