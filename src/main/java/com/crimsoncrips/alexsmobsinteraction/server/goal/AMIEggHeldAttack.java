package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;

public class AMIEggHeldAttack<T extends LivingEntity> extends NearestAttackableTargetGoal {


    public AMIEggHeldAttack(Mob pMob, Class pTargetType, boolean pMustSee) {
        super(pMob, pTargetType, pMustSee);
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && !mob.isAlliedTo(target) && !mob.isBaby();
    }

    protected void findTarget() {
        if (mob instanceof EntityEmu){
            isHoldingEgg(AMItemRegistry.EMU_EGG.get());
        }
        if (mob instanceof EntityCaiman){
            isHoldingEgg(AMBlockRegistry.CAIMAN_EGG.get().asItem());
        }

    }

    @Override
    public void tick() {
        super.tick();
        AMIUtils.awardAdvancement(target,"egg_steal","egg_steal");
    }

    public void isHoldingEgg(Item item){
        this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (p_148152_) -> {
            return p_148152_ instanceof LivingEntity living && living.isHolding(item) && !mob.isAlliedTo(living);
        }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

    }
}