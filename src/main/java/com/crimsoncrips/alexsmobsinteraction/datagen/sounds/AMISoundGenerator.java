package com.crimsoncrips.alexsmobsinteraction.datagen.sounds;

import com.crimsoncrips.alexsmobsinteraction.client.AMISoundRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.data.ExistingFileHelper;


public class AMISoundGenerator extends ACESoundProvider {

	public AMISoundGenerator(PackOutput output, ExistingFileHelper helper) {
		super(output, helper);
	}

	@Override
	public void registerSounds() {
		this.generateNewSoundWithSubtitle(AMISoundRegistry.BANANA_SLIP, "mob/banana_slip", 1);
	}
}
