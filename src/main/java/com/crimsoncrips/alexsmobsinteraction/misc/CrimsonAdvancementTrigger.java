package com.crimsoncrips.alexsmobsinteraction.misc;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CrimsonAdvancementTrigger extends SimpleCriterionTrigger<CrimsonAdvancementTrigger.Instance> {
    public final ResourceLocation resourceLocation;

    public CrimsonAdvancementTrigger(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public Instance createInstance(JsonObject p_230241_1_, ContextAwarePredicate p_230241_2_, DeserializationContext p_230241_3_) {
        return new Instance(p_230241_2_, resourceLocation);
    }

    public void trigger(ServerPlayer p_192180_1_) {
        this.trigger(p_192180_1_, (p_226308_1_) -> {
            return true;
        });
    }

    @Override
    public ResourceLocation getId() {
        return resourceLocation;
    }


    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(ContextAwarePredicate p_i231507_1_, ResourceLocation res) {
            super(res, p_i231507_1_);
        }

        public JsonObject serializeToJson(SerializationContext p_230240_1_) {
            JsonObject lvt_2_1_ = super.serializeToJson(p_230240_1_);
            return lvt_2_1_;
        }
    }
}