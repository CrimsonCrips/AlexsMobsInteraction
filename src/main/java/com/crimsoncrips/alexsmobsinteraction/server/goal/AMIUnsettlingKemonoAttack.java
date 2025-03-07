package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.Item;

public class AMIUnsettlingKemonoAttack<T extends LivingEntity> extends NearestAttackableTargetGoal {


    public AMIUnsettlingKemonoAttack(Mob pMob, Class pTargetType, boolean pMustSee) {
        super(pMob, pTargetType, pMustSee);
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && !mob.isAlliedTo(target) && !mob.isBaby();
    }

    protected void findTarget() {
        this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (p_148152_) -> {
            return p_148152_ instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO.get()) && !mob.isAlliedTo(living);
        }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        AMIUtils.awardAdvancement(target, "enemy_of_enemy", "enemies");
    }
}