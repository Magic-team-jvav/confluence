package org.confluence.mod.common.summon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

record SummonSavedState(ResourceLocation type, UUID uuid, int slotCost, SummonStats stats,
                        SummonPose pose) {
    static SummonSavedState capture(SummonInstance summon) {
        return new SummonSavedState(summon.type(), summon.uuid(), summon.slotCost(), summon.stats(), summon.currentPose());
    }

    CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", type.toString());
        tag.putUUID("UUID", uuid);
        tag.putInt("SlotCost", slotCost);
        tag.putFloat("BaseDamage", stats.baseDamage());
        tag.putFloat("WeaponDamageMultiplier", stats.weaponDamageMultiplier());
        tag.putFloat("ArmorPenetration", stats.armorPenetration());
        tag.putFloat("TagDamage", stats.tagDamage());
        tag.putFloat("KnockbackMultiplier", stats.knockbackMultiplier());
        tag.putDouble("X", pose.position().x);
        tag.putDouble("Y", pose.position().y);
        tag.putDouble("Z", pose.position().z);
        tag.putFloat("Yaw", pose.yaw());
        tag.putFloat("Pitch", pose.pitch());
        tag.putFloat("Roll", pose.roll());
        return tag;
    }

    static SummonSavedState fromTag(CompoundTag tag) {
        if (!tag.contains("Type", Tag.TAG_STRING) || !tag.hasUUID("UUID")
                || !tag.contains("SlotCost", Tag.TAG_INT) || !tag.contains("BaseDamage", Tag.TAG_FLOAT)
                || !tag.contains("X", Tag.TAG_DOUBLE) || !tag.contains("Y", Tag.TAG_DOUBLE)
                || !tag.contains("Z", Tag.TAG_DOUBLE) || !tag.contains("Yaw", Tag.TAG_FLOAT)
                || !tag.contains("Pitch", Tag.TAG_FLOAT) || !tag.contains("Roll", Tag.TAG_FLOAT)) {
            return null;
        }
        ResourceLocation type = ResourceLocation.tryParse(tag.getString("Type"));
        int slotCost = tag.getInt("SlotCost");
        float baseDamage = tag.getFloat("BaseDamage");
        float weaponDamageMultiplier = tag.contains("WeaponDamageMultiplier", Tag.TAG_FLOAT) ? tag.getFloat("WeaponDamageMultiplier") : 1.0F;
        float penetration = tag.getFloat("ArmorPenetration");
        float tagDamage = tag.getFloat("TagDamage");
        float knockback = tag.contains("KnockbackMultiplier") ? tag.getFloat("KnockbackMultiplier") : 1;
        double x = tag.getDouble("X");
        double y = tag.getDouble("Y");
        double z = tag.getDouble("Z");
        float yaw = tag.getFloat("Yaw");
        float pitch = tag.getFloat("Pitch");
        float roll = tag.getFloat("Roll");
        if (type == null || slotCost <= 0 || !Float.isFinite(baseDamage) || baseDamage < 0.0F
                || !Float.isFinite(weaponDamageMultiplier) || weaponDamageMultiplier < 0.0F
                || !Float.isFinite(penetration) || penetration < 0 || !Float.isFinite(tagDamage) || tagDamage < 0
                || !Float.isFinite(knockback) || knockback < 0
                || !Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                || !Float.isFinite(yaw) || !Float.isFinite(pitch) || !Float.isFinite(roll)) {
            return null;
        }
        return new SummonSavedState(type, tag.getUUID("UUID"), slotCost,
                new SummonStats(baseDamage, weaponDamageMultiplier, penetration, tagDamage, knockback),
                new SummonPose(new Vec3(x, y, z), yaw, pitch, roll));
    }

    SummonInstance restore(ServerPlayer owner) {
        SummonInstance summon = create(owner);
        if (summon != null) {
            summon.restoreUuid(uuid);
        }
        return summon;
    }

    private SummonInstance create(ServerPlayer owner) {
        SummonType summonType = SummonTypes.byId(type);
        return summonType == null ? null : summonType.create(owner, slotCost, stats, pose);
    }
}
