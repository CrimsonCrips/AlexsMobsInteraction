package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bone_serpent;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.BonePartInterface;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.UUID;


@Mixin(EntityBoneSerpentPart.class)
public abstract class AMIBoneSerpentPart extends LivingEntity implements BonePartInterface {

    @Shadow
    public abstract Entity getParent();

    @Shadow
    public abstract void setTail(boolean tail);

    protected AMIBoneSerpentPart(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Override
    public void detectChildLoop(){
        if(getChild() instanceof EntityBoneSerpentPart entityBoneSerpentPart1){
            if (entityBoneSerpentPart1.isTail()){
                if (this.getParent() instanceof EntityBoneSerpent)
                    return;
                entityBoneSerpentPart1.discard();
                this.setTail(true);
            } else {
                ((BonePartInterface)entityBoneSerpentPart1).detectChildLoop();
            }
        }
    }

    @Override
    public Entity getChild() {
        UUID id = AMIAttachments.getUUID(this, AMIAttachments.CHILD_UUID);
        if (id != null && !this.level().isClientSide) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

}
