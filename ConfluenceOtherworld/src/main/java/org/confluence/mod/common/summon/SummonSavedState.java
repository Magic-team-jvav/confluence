package org.confluence.mod.common.summon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.dragon.StardustDragonSummon;
import org.confluence.mod.common.summon.flying.FinchSummon;
import org.confluence.mod.common.summon.flying.HornetSummon;
import org.confluence.mod.common.summon.flying.ImpSummon;
import org.confluence.mod.common.summon.flying.SculkWispSummon;
import org.confluence.mod.common.summon.ground.IronGolemSummon;
import org.confluence.mod.common.summon.ground.SnowFlinxSummon;
import org.confluence.mod.common.summon.slime.SlimeSummon;
import org.confluence.mod.common.summon.sword.SummonSword;
import org.confluence.mod.common.summon.terraprisma.TerraprismaSummon;

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
        double x = tag.getDouble("X");
        double y = tag.getDouble("Y");
        double z = tag.getDouble("Z");
        float yaw = tag.getFloat("Yaw");
        float pitch = tag.getFloat("Pitch");
        float roll = tag.getFloat("Roll");
        if (type == null || slotCost <= 0 || !Float.isFinite(baseDamage) || baseDamage < 0.0F
                || !Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                || !Float.isFinite(yaw) || !Float.isFinite(pitch) || !Float.isFinite(roll)) {
            return null;
        }
        return new SummonSavedState(type, tag.getUUID("UUID"), slotCost, new SummonStats(baseDamage),
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
        if (type.equals(Confluence.asResource("finch_baby"))) {
            return new FinchSummon(owner, slotCost, stats, pose);
        }
        if (type.equals(Confluence.asResource("i_32_iron_golem"))) {
            return new IronGolemSummon(owner, slotCost, stats, pose);
        }
        if (type.equals(Confluence.asResource("slime_baby"))) {
            return new SlimeSummon(owner, slotCost, stats, pose);
        }
        if (type.equals(Confluence.asResource("hornet_baby"))) {
            return new HornetSummon(owner, slotCost, stats, pose);
        }
        if (type.equals(Confluence.asResource("sculk_wisp"))) {
            return new SculkWispSummon(owner, slotCost, stats, pose);
        }
        if (type.equals(Confluence.asResource("summon_imp"))) {
            return new ImpSummon(owner, slotCost, stats, pose);
        }
        if (type.equals(Confluence.asResource("summon_snow_flinx"))) {
            return new SnowFlinxSummon(owner, slotCost, stats, pose);
        }
        for (SummonSword.Kind kind : SummonSword.Kind.values()) {
            if (type.equals(kind.type())) {
                return new SummonSword(owner, slotCost, stats, pose, kind);
            }
        }
        if (type.equals(Confluence.asResource("terraprisma"))) {
            return new TerraprismaSummon(owner, slotCost, stats, pose);
        }
        if (type.equals(Confluence.asResource("stardust_dragon"))) {
            return new StardustDragonSummon(owner, slotCost, stats, pose);
        }
        return null;
    }
}
