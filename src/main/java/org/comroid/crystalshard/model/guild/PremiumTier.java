package org.comroid.crystalshard.model.guild;

import org.comroid.api.IntegerAttribute;
import org.comroid.api.Named;
import org.comroid.api.Rewrapper;
import org.jetbrains.annotations.NotNull;

public enum PremiumTier implements IntegerAttribute, Named {
    NONE(0),
    TIER_1(1),
    TIER_2(2),
    TIER_3(3);

    private final int value;

    @Override
    public @NotNull Integer getValue() {
        return value;
    }

    PremiumTier(int value) {
        this.value = value;
    }

    public static Rewrapper<PremiumTier> valueOf(int value) {
        return IntegerAttribute.valueOf(value, PremiumTier.class);
    }
}
