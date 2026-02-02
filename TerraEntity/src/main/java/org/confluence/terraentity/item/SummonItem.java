package org.confluence.terraentity.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.api.event.SummonEvent;
import org.confluence.terraentity.attachment.SummonerAttachment;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.utils.AdapterUtils;
import org.confluence.terraentity.utils.TEUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class SummonItem<T extends Mob & ISummonMob> extends Item {
    public final DeferredHolder<EntityType<?>, EntityType<T>> entityType;
    public final int consume;

    public final float baseAttackDamage;
    Supplier<AttachmentType<SummonerAttachment>> summonType; // 召唤物类型

    List<Component> tooltips;
    Supplier<SoundEvent> sound;

    public SummonItem(Properties properties, DeferredHolder<EntityType<?>, EntityType<T>> entityType, int consume, float baseAttackDamage) {
        this(properties, entityType, consume, baseAttackDamage, TEAttachments.SUMMONER_STORAGE, List.of());
    }

    public SummonItem(Properties properties, DeferredHolder<EntityType<?>, EntityType<T>> entityType, int consume, float baseAttackDamage, Supplier<AttachmentType<SummonerAttachment>> summonType, List<Component> tooltips) {
        super(properties.stacksTo(1));
        this.entityType = entityType;
        this.consume = consume;
        this.baseAttackDamage = baseAttackDamage;
        this.summonType = summonType;
        this.tooltips = tooltips;
        this.sound = TESounds.ROUTINE_SUMMON;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide) {

            var data = player.getData(summonType.get());
            data.refresh((ServerPlayer) player);

            EntityHitResult hit = TEUtils.getEyeTraceHitResult(player, player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE));
            if (hit != null) {
                if (this.canDiscard(hit.getEntity(), player)) {
                    hit.getEntity().discard();
                    return InteractionResultHolder.success(player.getItemInHand(hand));
                }
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            player.startUsingItem(hand);

            return InteractionResultHolder.pass(itemstack);
        }
        return InteractionResultHolder.fail(itemstack);
    }

    protected boolean canDiscard(Entity entity, Player player){
        // 这里设计不合理，不过也没有其他需求
        return entity instanceof ISummonMob summonMob && !summonMob.isPet() &&  summonMob.summon_getOwner() == player;
    }


    public void summon(Player player, ItemStack stack) {
        Level level = player.level();
        if (AdapterUtils.postGameEvent(new SummonEvent.Pre<>(player, stack, entityType.get())).isCanceled()) return;

        T entity = entityType.get().create(level);
        if (entity != null) {
            BlockPos pos = TEUtils.getEyeBlockHitResult(player).above();
            entity.setPos(pos.getX(), pos.getY(), pos.getZ());
            entity.summon(player, stack);
            entity.setCost(consume);
            level.addFreshEntity(entity);
            entity.playSound(this.sound.get(), 1.0F, 1.0F);
            var data = player.getData(summonType.get());
            data.summon(consume, entity.getId());
            if (player instanceof ServerPlayer serverPlayer)
                data.sync(serverPlayer);
        }
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        tooltipComponents.add(Component.translatable("tooltic.terra_entity.summon_item.desc"));

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        float additionAttackDamage = (float) localPlayer.getAttributeValue(TEAttributes.MARK_DAMAGE);

        tooltipComponents.add(Component.translatable("attribute.name.player.summon_damage").append(": " +
                        (baseAttackDamage + (additionAttackDamage > 0 ? "  +%.1f".formatted((additionAttackDamage)) : "")))
                .withColor(0x00AB00));

        tooltipComponents.add(Component.translatable("tooltip.terra_entity.summon_item_cost", consume).withColor(0xABAC00));
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.summon_item_entity", entityType.get().getDescription()).withColor(0x1E90FF));

        var data = localPlayer.getData(summonType.get());
        int a = data.getCurrentCapacity();
        int b = SummonerAttachment.getMaxCapacity(localPlayer);
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.summon_info", b - a, b).withColor(a <= 0 ? 0xAB0000 : 0x00ABAC));

        this.tooltips.forEach(tooltipComponents::add);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 1000;
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity livingEntity, int count) {

        // 召唤
        if (count > getUseDuration(stack, livingEntity) - 20) {
            livingEntity.swing(livingEntity.getUsedItemHand());
            if (livingEntity instanceof ServerPlayer player) {
                var data = player.getData(summonType.get());
                if (!data.canSummon(consume)) {
                    // 如果没有足够的召唤栏位，就移除最后一个仆从，再尝试生成。
                    data.removeLast(player, consume);
                }
                if (data.canSummon(consume)) {
                    summon(player, stack);
                }
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        // 收回所有召唤物
        if (getUseDuration(stack, livingEntity) - remainingUseDuration == 20) {
            this.onRetrieve(livingEntity, stack);
        }
    }

    protected void onRetrieve(LivingEntity livingEntity, ItemStack stack) {
        livingEntity.swing(livingEntity.getUsedItemHand());
        if (livingEntity instanceof ServerPlayer player) {
            var data = player.getData(summonType.get());
            data.clear(player);
            data.sync(player);
        }
    }

    public SummonItem<T> setSound(Supplier<SoundEvent> sound) {
        this.sound = sound;
        return this;
    }
}