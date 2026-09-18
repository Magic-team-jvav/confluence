package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttachmentEntityDamageSource extends DamageSource {

    private final AttachmentEntity attachmentEntity;

    public AttachmentEntityDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition, @NotNull AttachmentEntity attachmentEntity) {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.attachmentEntity = attachmentEntity;
    }

    public AttachmentEntity getAttachmentEntity() {
        return attachmentEntity;
    }
}
