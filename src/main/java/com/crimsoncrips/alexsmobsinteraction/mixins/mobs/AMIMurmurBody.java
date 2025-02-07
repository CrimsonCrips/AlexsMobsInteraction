package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;
import java.util.UUID;


@Mixin(EntityMurmur.class)
public abstract class AMIMurmurBody extends Mob {

    private static final EntityDataAccessor<Integer> HEAD_ID;

    private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID;


    protected AMIMurmurBody(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    static {
        HEAD_UUID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.OPTIONAL_UUID);
        HEAD_ID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.INT);
    }

    private boolean renderFakeHead = true;

    public void tick() {
        EntityMurmur murmur = (EntityMurmur)(Object)(this);
        super.tick();
        if (this.renderFakeHead) {
            this.renderFakeHead = false;
        }

        this.yBodyRot = this.getYRot();
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70.0F, this.yBodyRot + 70.0F);
        if (!this.level().isClientSide) {
            Entity head = murmur.getHead();
            if (head == null && !(AlexsMobsInteraction.COMMON_CONFIG.GOOFY_MURMUR_DECAPITATED_ENABLED.get())) {
                LivingEntity created;
                AMIReflectionUtil.callMethod(created = this, "createHead", new Class[0], new Object[0]);
                murmur.setHeadUUID(created.getUUID());
                murmur.getEntityData().set(HEAD_ID, created.getId());
            }
        }

    }





}
