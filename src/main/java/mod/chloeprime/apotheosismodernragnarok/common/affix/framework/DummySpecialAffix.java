package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixDefinition;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import mod.chloeprime.apotheosismodernragnarok.common.util.ExtraCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Set;

public class DummySpecialAffix extends AbstractAffix {
    public static final Codec<DummySpecialAffix> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    AffixDefinition.CODEC.fieldOf("definition").forGetter(Affix::definition),
                    LootCategory.SET_CODEC.fieldOf("types").forGetter(AbstractAffix::getApplicableCategories),
                    LootRarity.CODEC.fieldOf("min_rarity").forGetter(a -> a.minRarity))
            .apply(inst, DummySpecialAffix::new));

    protected LootRarity minRarity;

    public DummySpecialAffix(AffixDefinition definition, Set<LootCategory> categories, LootRarity minRarity) {
        super(definition, categories);
        this.minRarity = minRarity;
    }

    @Override
    public MutableComponent getDescription(AffixInstance inst, AttributeTooltipContext ctx) {
        return Component.translatable(desc()).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory category, LootRarity rarity) {
        return super.canApplyTo(stack, category, rarity) && (rarity.sortIndex() >= this.minRarity.sortIndex()); // TODO use sortIndex instead of rarity level? use RarityRegistry.getSortedRarities() to check.
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
}
