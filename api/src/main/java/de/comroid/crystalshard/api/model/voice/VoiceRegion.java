package de.comroid.crystalshard.api.model.voice;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import de.comroid.crystalshard.adapter.Adapter;
import de.comroid.crystalshard.util.annotation.IntroducedBy;
import de.comroid.crystalshard.util.model.serialization.JsonDeserializable;
import de.comroid.crystalshard.util.model.serialization.JsonBinding;

import com.fasterxml.jackson.databind.JsonNode;

import static de.comroid.crystalshard.util.annotation.IntroducedBy.ImplementationSource.API;
import static de.comroid.crystalshard.util.model.serialization.JsonBinding.identity;

public interface VoiceRegion extends JsonDeserializable {
    default String getID() {
        return getTraitValue(Trait.ID);
    }

    default String getName() {
        return getTraitValue(Trait.NAME);
    }

    default boolean isVIPonly() {
        return getTraitValue(Trait.VIP_ONLY);
    }

    default boolean isOptimal() {
        return getTraitValue(Trait.OPTIMAL);
    }

    default boolean isDeprecated() {
        return getTraitValue(Trait.DEPRECATED);
    }

    default boolean isCustom() {
        return getTraitValue(Trait.CUSTOM);
    }

    interface Trait {
        JsonBinding<String, String> ID = identity(JsonNode::asText, "id");
        JsonBinding<String, String> NAME = identity(JsonNode::asText, "name");
        JsonBinding<Boolean, Boolean> VIP_ONLY = identity(JsonNode::asBoolean, "vip");
        JsonBinding<Boolean, Boolean> OPTIMAL = identity(JsonNode::asBoolean, "optimal");
        JsonBinding<Boolean, Boolean> DEPRECATED = identity(JsonNode::asBoolean, "deprecated");
        JsonBinding<Boolean, Boolean> CUSTOM = identity(JsonNode::asBoolean, "custom");
    }

    @IntroducedBy(value = API, docs = "https://discordapp.com/developers/docs/resources/voice#list-voice-regions")
    static CompletableFuture<Collection<VoiceRegion>> requestVoiceRegions() {
        return Adapter.staticOverride(VoiceRegion.class, "requestVoiceRegions");
    }
}
