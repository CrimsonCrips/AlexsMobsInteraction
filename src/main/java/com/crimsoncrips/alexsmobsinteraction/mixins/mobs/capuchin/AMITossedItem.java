package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(EntityTossedItem.class)
public abstract class AMITossedItem extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Boolean> DART = SynchedEntityData.defineId(EntityTossedItem.class, EntityDataSerializers.BOOLEAN);;

    public AMITossedItem(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    Item items;


    protected Item getDefaultItem() {
        if (AMInteractionConfig.GOOFY_CAPUCHIN_BOMB_ENABLED && AMInteractionConfig.GOOFY_MODE_ENABLED) items = Items.TNT;
        else items = Items.COBBLESTONE;
        return this.isDart() ? (Item)AMItemRegistry.ANCIENT_DART.get() : items;
    }

    public boolean isDart() {
        return (Boolean)this.entityData.get(DART);
    }


    protected void onHit(HitResult result) {
        super.onHit(result);
        int x = this.getBlockX();
        int y = this.getBlockY();
        int z = this.getBlockZ();
        if (!this.level().isClientSide && (!this.isDart() || result.getType() == HitResult.Type.BLOCK)) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }

        if (!isDart() && AMInteractionConfig.GOOFY_CAPUCHIN_BOMB_ENABLED && AMInteractionConfig.GOOFY_MODE_ENABLED) {
            this.level().explode(this, x + 1,y + 2,z + 1,2, Level.ExplosionInteraction.MOB);
        }

    }
}
