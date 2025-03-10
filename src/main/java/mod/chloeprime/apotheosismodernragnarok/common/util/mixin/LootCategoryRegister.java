package mod.chloeprime.apotheosismodernragnarok.common.util.mixin;

import dev.shadowsoffire.apotheosis.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.mixin.apoth.LootCategoriesRegisterAccessor;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Predicate;

public class LootCategoryRegister {
    public static final Logger LOGGER = LogManager.getLogger();
    public static LootCategory reg(String path, Predicate<ItemStack> filter, EquipmentSlotGroup slots) {
        LOGGER.debug("Registering loot category to apotheosis: " + path);
        path = path.replace(":", "_");
        return LootCategoriesRegisterAccessor.LootCategoryAccessor.register(path, filter, slots);
    }
}
