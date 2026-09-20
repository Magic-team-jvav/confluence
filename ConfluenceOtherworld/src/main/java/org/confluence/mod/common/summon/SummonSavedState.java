package org.confluence.mod.common.summon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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
        ResourceLocation type = ResourceLocation.parse(tag.getString("Type"));
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
        return new SummonSavedState(type, tag.getUUID("UUID"), slotCost,
                new SummonStats(baseDamage, weaponDamageMultiplier, penetration, tagDamage, knockback),
                new SummonPose(new Vec3(x, y, z), yaw, pitch, roll));
    }

    @Nullable SummonInstance restore(ServerPlayer owner) {
        SummonInstance summon = create(owner);
        if (summon != null) {
            summon.restoreUuid(uuid);
        }
        return summon;
    }

    private @Nullable SummonInstance create(ServerPlayer owner) {
        SummonType summonType = SummonTypes.byId(type);
        return summonType == null ? null : summonType.create(owner, slotCost, stats, pose);
    }
}
