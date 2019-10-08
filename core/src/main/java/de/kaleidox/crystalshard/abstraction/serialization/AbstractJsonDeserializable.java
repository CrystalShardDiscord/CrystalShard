package de.kaleidox.crystalshard.abstraction.serialization;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import de.kaleidox.crystalshard.CrystalShard;
import de.kaleidox.crystalshard.abstraction.AbstractApiBound;
import de.kaleidox.crystalshard.api.Discord;
import de.kaleidox.crystalshard.api.entity.Snowflake;
import de.kaleidox.crystalshard.util.model.serialization.JsonDeserializable;
import de.kaleidox.crystalshard.util.model.serialization.JsonTrait;
import de.kaleidox.crystalshard.util.model.serialization.JsonTraits;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;

public abstract class AbstractJsonDeserializable extends AbstractApiBound implements JsonDeserializable {
    private static final FluentLogger log = FluentLogger.forEnclosingClass();

    protected final Set<JsonTrait> possibleTraits;
    
    private Map<JsonTrait, Object> values;

    protected AbstractJsonDeserializable(Discord api, JsonNode data) {
        super(api);
        
        JsonTraits traitsClass = getClass().getAnnotation(JsonTraits.class);

        if (traitsClass == null)
            throw new AssertionError("Could not determine @JsonTraits annotation for "
                    + getClass().getSimpleName() + "! Please open an issue at " + CrystalShard.ISSUES_URL);

        possibleTraits = Arrays.stream(traitsClass.value()
                .getFields())
                .filter(field -> Snowflake.Trait.class.isAssignableFrom(field.getType()))
                .filter(field -> Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
                .map(field -> {
                    try {
                        return field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new AssertionError("Could not access Traits", e);
                    }
                })
                .map(JsonTrait.class::cast)
                .map(trait -> trait.withApi(api))
                .collect(Collectors.toSet());
        

        values = new ConcurrentHashMap<>();

        updateFromJson(data);
    }

    @Override
    public Set<JsonTrait> possibleTraits() {
        return possibleTraits;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, T> T getTraitValue(JsonTrait<S, T> trait) {
        S val;

        return (val = (S) values.getOrDefault(trait, null)) == null ? null : trait.apply(val);
    }

    @Override
    public void updateFromJson(final JsonNode data) {
        for (JsonTrait<?, ?> jsonTrait : possibleTraits()) {
            final String fieldName = jsonTrait.fieldName();
            final JsonNode field = data.path(fieldName);

            if (field.isMissingNode()) {
                log.at(Level.FINER).log("[%s] Field %s is missing; skipping!", toString(), fieldName);
                continue;
            }

            values.put(jsonTrait, jsonTrait.extract(field));
        }
    }
}
