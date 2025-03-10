package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractAffix extends AffixBaseUtility {
    protected final Set<LootCategory> categories;

    public AbstractAffix(AffixDefinition definition, Set<LootCategory> categories) {
        super(definition);
        this.categories = categories;
    }

    public Set<LootCategory> getApplicableCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public String desc() {
        return "affix." + id() + ".desc";
    }

    public static boolean isStillHoldingTheSameGun(ItemStack gunStack, @Nonnull ResourceLocation gunId) {
        return Optional.ofNullable(IGun.getIGunOrNull(gunStack))
                .map(g -> g.getGunId(gunStack))
                .filter(gunId::equals)
                .isPresent();
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory category, LootRarity rarity) {
        if (category == Apoth.LootCategories.NONE) {
            return false;
        }
        var validTypes = getApplicableCategories();
        return validTypes.isEmpty() || validTypes.contains(category);
    }
}
