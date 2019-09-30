package de.kaleidox.crystalshard.core.api.cache;

import java.util.Optional;
import java.util.OptionalLong;

import de.kaleidox.crystalshard.api.entity.Snowflake;

import com.fasterxml.jackson.databind.JsonNode;

public interface Cacheable {
    void update(JsonNode data);

    default OptionalLong getCacheParentID() {
        return OptionalLong.empty();
    }

    default Optional<Class<? extends Cacheable>> getCacheParentType() {
        return Optional.empty();
    }

    default boolean isSingletonType() {
        return false;
    }

    default boolean isSubcacheMember() {
        return getCacheParentType().isPresent();
    }
}