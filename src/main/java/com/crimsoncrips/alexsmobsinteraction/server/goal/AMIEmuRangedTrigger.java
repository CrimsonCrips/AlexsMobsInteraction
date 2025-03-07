package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class AMIEmuRangedTrigger<T extends EntityEmu> extends NearestAttackableTargetGoal {


    public AMIEmuRangedTrigger(Mob pMob, Class pTargetType, boolean pMustSee) {
        super(pMob, pTargetType, pMustSee);
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && !mob.isAlliedTo(target) && !mob.isBaby();
    }

    protected void findTarget() {
        this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (p_148152_) -> {
            return p_148152_ instanceof LivingEntity living && living.isHolding(Ingredient.of(AMIItemTagGenerator.EMU_TRIGGER)) && !mob.isAlliedTo(living);
        }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

    }

    @Override
    public void tick() {
        super.tick();
        AMIUtils.awardAdvancement(target,"ranged_aggro","aggro");
    }

}