package org.confluence.mod.common.item.sponsor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terraentity.init.TEAttributes;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;

/*
 * 饰品：「无聊之咒·陨志」
 * 每移动（竖直方向五格，水平移动6格），获得1层「地脉共鸣」（上限8层），每层+2%全属性攻击伤害与暴击率
 * 站立时每秒消耗1层，层数归零后触发巨石坠落（这是原地站立8
 */
public class BoredomsPactFallingResolve extends BaseCurioItem {
    public static final ResourceLocation ID = Confluence.asResource("boredoms_pact_falling_resolve");

    public BoredomsPactFallingResolve() {
        super(builder(ID.getPath()).rarity(ModRarity.MASTER).tooltips(8));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        Level level = living.level();
        if (level.isClientSide) return;
        LibUtils.updateItemStackNbt(stack, tag -> {
            Vec3 currentPos = readPos(tag);
            if (currentPos == null) {
                savePos(tag, living);
            } else {
                byte count = tag.getByte("count");
                Vec3 pos = living.position().subtract(currentPos);
                if (Math.abs(pos.x) < Mth.EPSILON && Math.abs(pos.y) < Mth.EPSILON && Math.abs(pos.z) < Mth.EPSILON) {
                    if (level.getGameTime() % 20 == 0) {
                        if (count > 0) {
                            tag.putByte("count", --count);
                        } else if (!tag.getBoolean("summoned")) {
                            BlockPos.MutableBlockPos mutable = living.blockPosition().mutable();
                            int i = 0;
                            for (; i < 16 && level.getBlockState(mutable).getCollisionShape(level, mutable).isEmpty(); i++) {
                                mutable.move(0, 1, 0);
                            }
                            BlockState blockState = level.getBlockState(living.blockPosition().below());
                            if (blockState.isAir())
                                blockState = FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState();
                            level.addFreshEntity(new BoulderEntity(level, currentPos.add(0, i, 0), blockState));
                            tag.putBoolean("summoned", true);
                        }
                    }
                } else {
                    double h = tag.getDouble("h");
                    double v = tag.getDouble("v");
                    if (v >= 5 || h >= 6) {
                        if (count < 8) {
                            tag.putByte("count", ++count);
                        }
                        h = 0;
                        v = 0;
                    } else {
                        h += pos.horizontalDistance();
                        v += Math.abs(pos.y);
                    }
                    tag.putDouble("h", h);
                    tag.putDouble("v", v);
                    savePos(tag, living);
                    tag.putBoolean("summoned", false);
                }
            }
        });
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        AttributeModifier modifier = new AttributeModifier(ID, 0.2 * LibUtils.getItemStackNbtNoCopy(stack).getByte("count"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        return ImmutableMultimap.of(
                Attributes.ATTACK_DAMAGE, modifier,
                TCAttributes.getMagicDamage(), modifier,
                TCAttributes.getRangedDamage(), modifier,
                TEAttributes.MARK_DAMAGE, modifier,
                TCAttributes.getCriticalChance(), modifier
        );
    }

    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return ModSecretSeeds.BOULDER_WORLD.match() ? ICurio.DropRule.ALWAYS_KEEP : ICurio.DropRule.DEFAULT;
    }

    private static void savePos(CompoundTag tag, LivingEntity living) {
        tag.put("currentPos", Vec3.CODEC.encodeStart(NbtOps.INSTANCE, living.position()).getOrThrow());
    }

    private static @Nullable Vec3 readPos(CompoundTag tag) {
        return Vec3.CODEC.parse(NbtOps.INSTANCE, tag.get("currentPos")).result().orElse(null);
    }
}
