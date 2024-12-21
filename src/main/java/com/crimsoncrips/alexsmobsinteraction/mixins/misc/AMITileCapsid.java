package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(TileEntityCapsid.class)
public abstract class AMITileCapsid extends BaseContainerBlockEntity {


    protected AMITileCapsid(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityEnderiophage;setVariant(I)V"), index = 0,remap = false)
    private int adjustY(int variant) {
        if (AMInteractionConfig.ENDERIOPHAGE_ADAPTION_ENABLED){
            ResourceKey<Level> dimension = this.level.dimension();

            if(dimension == Level.NETHER){
                return 2;
            }else if(dimension == Level.OVERWORLD){
                return 1;
            }else{
                return 0;
            }
        } else return variant;
    }




}
