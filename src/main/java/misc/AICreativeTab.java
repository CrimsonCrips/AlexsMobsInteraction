package misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.item.AIItemRegistry;
import com.github.alexthe666.alexsmobs.item.CustomTabBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AICreativeTab {


    public static final DeferredRegister<CreativeModeTab> DEF_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AlexsMobsInteraction.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = DEF_REG.register(AlexsMobsInteraction.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("alexsmobsinteraction.itemTab" + AlexsMobsInteraction.MODID))
            .displayItems((enabledFeatures, output) -> {
                for(RegistryObject<Item> item : AIItemRegistry.DEF_REG.getEntries()){
                    if(item.get() instanceof CustomTabBehavior customTabBehavior){
                        customTabBehavior.fillItemCategory(output);
                    }else{
                        output.accept(item.get());
                    }
                }
            })
            .build());
}
