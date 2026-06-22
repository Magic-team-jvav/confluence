package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.color.FloatRGB;
import org.confluence.lib.common.LibAttributes;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.confluence.terraentity.mixin.accessor.SlimeAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneySlime extends Slime implements DeathAnimOptions {
    public static final int GROW_TIME = 20000;
    private final FloatRGB color;
    private int growTime = GROW_TIME;

    public HoneySlime(EntityType<? extends Slime> slime, Level level, int color) {
        super(slime, level);
        // setSize在constructor中调用时size还没更新，再变一遍
        setSize(2, true);
//        ItemStack itemStack = new ItemStack(Materials.GEL.getPrefab());
//        ColoredItem.setColor(itemStack, color);
        this.color = FloatRGB.fromInteger(color);
    }

    public static AttributeSupplier.Builder createSlimeAttributes(float attackDamage, int armor, float maxHealth) {
        return BaseSlime.createMobAttributes()
                .add(LibAttributes.getAttackDamage(), attackDamage)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.MAX_HEALTH, maxHealth);
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {}

    @Override
    public void tick() {
        resetFallDistance();
        if (onGround() && !((SlimeAccessor) this).isWasOnGround()) {
            int i = getSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = random.nextFloat() * Mth.TWO_PI;
                float f1 = random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
                float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HONEY_BLOCK.defaultBlockState()), getX() + (double) f2, getY(), getZ() + (double) f3, color.red(), color.green(), color.blue());
            }
        }
        if (!level().isClientSide && getSize() < 3 && growTime-- <= 0) {
            setSize(getSize() + 1, false);
            growTime = GROW_TIME;
        }
        super.tick();
    }

    @Override
    protected boolean spawnCustomParticles() {
        return true;
    }

    @Override
    public void setSize(int pSize, boolean init) {
        int i = init ? 2 : Mth.clamp(pSize, 1, 127);
        entityData.set(ID_SIZE, i);
        reapplyPosition();
        refreshDimensions();
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2F + 0.1F * i);

        this.xpReward = i;
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        brain.clearMemories();
        setRemoved(removalReason);
//        invalidateCaps();
    }

    @Override
    public float[] getBloodColor() {
        return color.mixture(FloatRGB.ZERO, 0.5F).toArray();
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (level().isClientSide) return super.mobInteract(pPlayer, pHand);
        ItemStack item = pPlayer.getItemInHand(pHand);
        if (getSize() == 3 && item.is(Items.GLASS_BOTTLE)) {
            setSize(level().random.nextInt(1, 3), false);
            pPlayer.addItem(new ItemStack(Items.HONEY_BOTTLE));
            if (!pPlayer.getAbilities().instabuild) item.shrink(1);
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.HONEY_DRINK, SoundSource.AMBIENT, 3.0F, 1.5F);

            dropFromLootTable(damageSources().playerAttack(pPlayer), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
