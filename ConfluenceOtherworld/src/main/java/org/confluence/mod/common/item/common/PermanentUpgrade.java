package org.confluence.mod.common.item.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.item.MinecartItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.network.s2c.RightClickSubtractorPacketS2C;
import org.confluence.terra_curio.util.TCUtils;


public enum PermanentUpgrade {
    LIFE_CRYSTAL("life_crystal") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
            if (health == null || data.isLifeCrystalsMaximum()) return false;
            int count = data.getUsedLifeCrystals() + 1;
            data.setUsedLifeCrystals(count);
            updateAttribute(health, count * 4.0);
            heal(player, 4.0F);
            return true;
        }

        @Override
        protected void restoreAttribute(ServerPlayer player, EverBeneficial data) {
            updateAttribute(player.getAttribute(Attributes.MAX_HEALTH), data.getUsedLifeCrystals() * 4.0);
        }
    },
    LIFE_FRUITS("life_fruit") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
            if (health == null || !data.isLifeCrystalsMaximum() || data.isLifeFruitsMaximum())
                return false;
            int count = data.getUsedLifeFruits() + 1;
            data.setUsedLifeFruits(count);
            updateAttribute(health, count);
            heal(player, 1.0F);
            return true;
        }

        @Override
        protected void restoreAttribute(ServerPlayer player, EverBeneficial data) {
            updateAttribute(player.getAttribute(Attributes.MAX_HEALTH), data.getUsedLifeFruits());
        }
    },
    RECALL_LIFE_CRYSTAL("life_crystal") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
            if (health == null || data.getUsedLifeCrystals() <= -4) return false;
            int count = data.getUsedLifeCrystals() - 1;
            if (health.getBaseValue() + count * 4.0 <= 0) return false;
            data.setUsedLifeCrystals(count);
            updateAttribute(health, count * 4.0);
            return true;
        }
    },
    VITAL_CRYSTAL("vital_crystal") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            if (data.isVitalCrystalUsed()) return false;
            data.setVitalCrystalUsed(true);
            return true;
        }
    },
    AEGIS_APPLE("aegis_apple") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
            if (armor == null || data.isAegisAppleUsed()) return false;
            data.setAegisAppleUsed(true);
            updateAttribute(armor, 4.0);
            return true;
        }

        @Override
        protected void restoreAttribute(ServerPlayer player, EverBeneficial data) {
            updateAttribute(player.getAttribute(Attributes.ARMOR), data.isAegisAppleUsed() ? 4.0 : 0.0);
        }
    },
    AMBROSIA("ambrosia") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            AttributeInstance speed = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
            if (speed == null || data.isAmbrosiaUsed()) return false;
            data.setAmbrosiaUsed(true);
            updateAttribute(speed, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            syncAmbrosia(player);
            return true;
        }

        @Override
        protected void restoreAttribute(ServerPlayer player, EverBeneficial data) {
            updateAttribute(player.getAttribute(Attributes.BLOCK_BREAK_SPEED), data.isAmbrosiaUsed() ? 0.05 : 0.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    },
    GUMMY_WORM("gummy_worm") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            if (data.isGummyWormUsed()) return false;
            data.setGummyWormUsed(true);
            return true;
        }
    },
    GALAXY_PEARL("galaxy_pearl") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            AttributeInstance luck = player.getAttribute(Attributes.LUCK);
            if (luck == null || data.isGalaxyPearlUsed()) return false;
            data.setGalaxyPearlUsed(true);
            updateAttribute(luck, 0.03);
            return true;
        }

        @Override
        protected void restoreAttribute(ServerPlayer player, EverBeneficial data) {
            updateAttribute(player.getAttribute(Attributes.LUCK), data.isGalaxyPearlUsed() ? 0.03 : 0.0);
        }
    },
    MINECART_UPGRADE_KIT("minecart_upgrade_kit") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            data.setMinecartUpgradeKitUsed(true);
            player.drop(MinecartItems.MECHANICAL_CART.get().getDefaultInstance(), true);
            return true;
        }
    },
    ARTISAN_LOAF("artisan_loaf") {
        @Override
        protected boolean use(ServerPlayer player, EverBeneficial data) {
            AttributeInstance range = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
            if (range == null || data.isArtisanLoafUsed()) return false;
            data.setArtisanLoafUsed(true);
            updateAttribute(range, 4.0);
            return true;
        }

        @Override
        protected void restoreAttribute(ServerPlayer player, EverBeneficial data) {
            updateAttribute(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE), data.isArtisanLoafUsed() ? 4.0 : 0.0);
        }
    };

    private final ResourceLocation location;
    private final ResourceLocation id;

    PermanentUpgrade(String path) {
        this.location = Confluence.asResource(path);
        this.id = location;
    }

    public ResourceLocation id() {
        return id;
    }

    public final boolean tryUse(ServerPlayer player) {
        if (!use(player, EverBeneficial.of(player))) return false;
        clampHealth(player);
        return true;
    }

    protected abstract boolean use(ServerPlayer player, EverBeneficial data);

    public static void restore(ServerPlayer player) {
        EverBeneficial data = EverBeneficial.of(player);
        for (PermanentUpgrade upgrade : values()) upgrade.restoreAttribute(player, data);
        syncAmbrosia(player);
        clampHealth(player);
    }

    protected void restoreAttribute(ServerPlayer player, EverBeneficial data) {}

    protected final void updateAttribute(AttributeInstance instance, double value) {
        updateAttribute(instance, value, AttributeModifier.Operation.ADD_VALUE);
    }

    protected final void updateAttribute(AttributeInstance instance, double value, AttributeModifier.Operation operation) {
        if (instance == null) return;
        AttributeModifier previous = instance.getModifier(id);
        if (value == 0.0) {
            if (previous != null) instance.removeModifier(id);
        } else if (previous == null || previous.amount() != value || previous.operation() != operation) {
            instance.addOrReplacePermanentModifier(new AttributeModifier(id, value, operation));
        }
    }

    private static void clampHealth(ServerPlayer player) {
        player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
    }

    private static void heal(ServerPlayer player, float amount) {
        player.heal(amount);
        EverBeneficial data = EverBeneficial.of(player);
        if (data.isLifeCrystalsMaximum() && data.isLifeFruitsMaximum() && ManaStorage.of(player).isStarMaximum()) {
            AchievementUtils.awardAchievement(player, "topped_off");
        }
    }

    private static void syncAmbrosia(ServerPlayer player) {
        int value = TCUtils.getValue(player, TCItems.RIGHT$CLICK$DELAY$SUBSTRACTOR);
        if (EverBeneficial.of(player).isAmbrosiaUsed()) value++;
        PacketDistributor.sendToPlayer(player, new RightClickSubtractorPacketS2C((byte) Math.min(value, 4)));
    }
}
