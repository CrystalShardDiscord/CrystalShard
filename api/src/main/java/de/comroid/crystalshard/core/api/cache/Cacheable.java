package de.comroid.crystalshard.core.api.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

import de.comroid.crystalshard.adapter.Adapter;
import de.comroid.crystalshard.api.entity.Snowflake;
import de.comroid.crystalshard.util.model.serialization.JsonDeserializable;

import com.fasterxml.jackson.databind.JsonNode;

public interface Cacheable extends JsonDeserializable {
    default void update(JsonNode data) {
        updateFromJson(data);
    }

    static <S extends Cacheable, P extends Cacheable & Snowflake> Optional<CacheInformation<P>> getCacheInfo(S of) {
        final Class<? extends Cacheable> klass = of.getClass();

        try {
            for (Field field : klass.getFields()) {
                final CacheInformation.Marker annotation = field.getAnnotation(CacheInformation.Marker.class);

                if (annotation != null)
                    //noinspection unchecked
                    return Optional.ofNullable((CacheInformation<P>) field.get(null));
            }
        } catch (IllegalAccessException ignored) {
        }
        
        return Optional.empty();
    } 

    static <S extends Cacheable, P extends Cacheable & Snowflake> CacheInformation<P> makeSubcacheableInfo(
            Class<P> parentClass,
            Function<S, P> parentExtractor
    ) {
        return Adapter.require(CacheInformation.class, 1, parentClass, parentExtractor);
    }

    static <S extends Cacheable, P extends Cacheable & Snowflake> CacheInformation<P> makeSingletonCacheableInfo(
            Class<P> parentClass,
            Function<S, P> parentExtractor
    ) {
        return Adapter.require(CacheInformation.class, 2, parentClass, parentExtractor);
    }

    interface CacheInformation<P extends Cacheable & Snowflake> {
        Class<P> getParentClass();

        P getParent();

        default long getIDfromParent() {
            return getParent().getID();
        }
        
        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface Marker {
        }
    }
}
