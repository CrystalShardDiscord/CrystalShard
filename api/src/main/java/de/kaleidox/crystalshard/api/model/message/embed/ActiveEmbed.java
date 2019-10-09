package de.kaleidox.crystalshard.api.model.message.embed;

import java.awt.Color;
import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import de.kaleidox.crystalshard.util.Util;
import de.kaleidox.crystalshard.util.model.serialization.JsonDeserializable;
import de.kaleidox.crystalshard.util.model.serialization.JsonTrait;
import de.kaleidox.crystalshard.util.model.serialization.JsonTraits;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.identity;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.simple;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.underlying;
import static de.kaleidox.crystalshard.util.model.serialization.JsonTrait.underlyingCollective;

@JsonTraits(ActiveEmbed.Trait.class)
public interface ActiveEmbed extends Embed, JsonDeserializable {
    @Override
    default Optional<String> getTitle() {
        return wrapTraitValue(Trait.TITLE);
    }

    @Override
    default Optional<String> getDescription() {
        return wrapTraitValue(Trait.DESCRIPTION);
    }

    @Override
    default Optional<URL> getURL() {
        return wrapTraitValue(Trait.URL);
    }

    @Override
    default Optional<Instant> getTimestamp() {
        return wrapTraitValue(Trait.TIMESTAMP);
    }

    @Override
    default Optional<Color> getColor() {
        return wrapTraitValue(Trait.COLOR);
    }

    @Override
    default Optional<Footer> getFooter() {
        return wrapTraitValue(Trait.FOOTER);
    }

    @Override
    default Optional<Image> getImage() {
        return wrapTraitValue(Trait.IMAGE);
    }

    @Override
    default Optional<Thumbnail> getThumbnail() {
        return wrapTraitValue(Trait.THUMBNAIL);
    }

    @Override
    default Optional<Video> getVideo() {
        return wrapTraitValue(Trait.VIDEO);
    }

    @Override
    default Optional<Provider> getProvider() {
        return wrapTraitValue(Trait.PROVIDER);
    }

    @Override
    default Optional<Author> getAuthor() {
        return wrapTraitValue(Trait.AUTHOR);
    }

    @Override
    default Collection<Field> getFields() {
        return getTraitValue(Trait.FIELDS);
    }

    interface Trait {
        JsonTrait<String, String> TITLE = identity(JsonNode::asText, "title");
        JsonTrait<String, String> DESCRIPTION = identity(JsonNode::asText, "description");
        JsonTrait<String, URL> URL = simple(JsonNode::asText, "url", Util::url_rethrow);
        JsonTrait<String, Instant> TIMESTAMP = simple(JsonNode::asText, "timestamp", Instant::parse);
        JsonTrait<Integer, Color> COLOR = simple(JsonNode::asInt, "color", Color::new);
        JsonTrait<JsonNode, Footer> FOOTER = underlying("footer", Footer.class);
        JsonTrait<JsonNode, Image> IMAGE = underlying("image", Image.class);
        JsonTrait<JsonNode, Thumbnail> THUMBNAIL = underlying("thumbnail", Thumbnail.class);
        JsonTrait<JsonNode, Video> VIDEO = underlying("video", Video.class);
        JsonTrait<JsonNode, Provider> PROVIDER = underlying("provider", Provider.class);
        JsonTrait<JsonNode, Author> AUTHOR = underlying("author", Author.class);
        JsonTrait<ArrayNode, Collection<Field>> FIELDS = underlyingCollective("fields", Field.class);
    }

    @JsonTraits(Footer.Trait.class)
    interface Footer extends Embed.Footer, JsonDeserializable {
        @Override
        default String getText() {
            return getTraitValue(Trait.TEXT);
        }

        @Override
        default Optional<URL> getIconURL() {
            return wrapTraitValue(Trait.ICON_URL);
        }

        @Override
        default Optional<URL> getProxyIconURL() {
            return wrapTraitValue(Trait.PROXY_ICON_URL);
        }

        interface Trait {
            JsonTrait<String, String> TEXT = identity(JsonNode::asText, "text");
            JsonTrait<String, URL> ICON_URL = simple(JsonNode::asText, "icon_url", Util::url_rethrow);
            JsonTrait<String, URL> PROXY_ICON_URL = simple(JsonNode::asText, "proxy_icon_url", Util::url_rethrow);
        }
    }

    @JsonTraits(Footer.Trait.class)
    interface Image extends Embed.Image, JsonDeserializable {
        @Override
        default Optional<URL> getURL() {
            return wrapTraitValue(Trait.URL);
        }

        @Override
        default Optional<URL> getProxyURL() {
            return wrapTraitValue(Trait.PROXY_URL);
        }

        @Override
        default Optional<Integer> getHeight() {
            return wrapTraitValue(Trait.HEIGHT);
        }

        @Override
        default Optional<Integer> getWidth() {
            return wrapTraitValue(Trait.WIDTH);
        }

        interface Trait {
            JsonTrait<String, URL> URL = simple(JsonNode::asText, "url", Util::url_rethrow);
            JsonTrait<String, URL> PROXY_URL = simple(JsonNode::asText, "proxy_url", Util::url_rethrow);
            JsonTrait<Integer, Integer> HEIGHT = identity(JsonNode::asInt, "height");
            JsonTrait<Integer, Integer> WIDTH = identity(JsonNode::asInt, "width");
        }
    }

    @JsonTraits(Footer.Trait.class)
    interface Thumbnail extends Embed.Thumbnail, JsonDeserializable {
        @Override
        default Optional<URL> getURL() {
            return wrapTraitValue(Trait.URL);
        }

        @Override
        default Optional<URL> getProxyURL() {
            return wrapTraitValue(Trait.PROXY_URL);
        }

        @Override
        default Optional<Integer> getHeight() {
            return wrapTraitValue(Trait.HEIGHT);
        }

        @Override
        default Optional<Integer> getWidth() {
            return wrapTraitValue(Trait.WIDTH);
        }

        interface Trait {
            JsonTrait<String, URL> URL = simple(JsonNode::asText, "url", Util::url_rethrow);
            JsonTrait<String, URL> PROXY_URL = simple(JsonNode::asText, "proxy_url", Util::url_rethrow);
            JsonTrait<Integer, Integer> HEIGHT = identity(JsonNode::asInt, "height");
            JsonTrait<Integer, Integer> WIDTH = identity(JsonNode::asInt, "width");
        }
    }

    @JsonTraits(Footer.Trait.class)
    interface Video extends Embed.Video, JsonDeserializable {
        @Override
        default Optional<URL> getURL() {
            return wrapTraitValue(Trait.URL);
        }

        @Override
        default Optional<Integer> getHeight() {
            return wrapTraitValue(Trait.HEIGHT);
        }

        @Override
        default Optional<Integer> getWidth() {
            return wrapTraitValue(Trait.WIDTH);
        }

        interface Trait {
            JsonTrait<String, URL> URL = simple(JsonNode::asText, "url", Util::url_rethrow);
            JsonTrait<Integer, Integer> HEIGHT = identity(JsonNode::asInt, "height");
            JsonTrait<Integer, Integer> WIDTH = identity(JsonNode::asInt, "width");
        }
    }

    @JsonTraits(Footer.Trait.class)
    interface Provider extends Embed.Provider, JsonDeserializable {
        @Override
        default Optional<String> getName() {
            return wrapTraitValue(Trait.NAME);
        }

        @Override
        default Optional<URL> getURL() {
            return wrapTraitValue(Trait.URL);
        }

        interface Trait {
            JsonTrait<String, String> NAME = identity(JsonNode::asText, "name");
            JsonTrait<String, URL> URL = simple(JsonNode::asText, "url", Util::url_rethrow);
        }
    }

    @JsonTraits(Footer.Trait.class)
    interface Author extends Embed.Author, JsonDeserializable {
        @Override
        default Optional<String> getName() {
            return wrapTraitValue(Trait.NAME);
        }

        @Override
        default Optional<URL> getURL() {
            return wrapTraitValue(Trait.URL);
        }

        @Override
        default Optional<URL> getIconURL() {
            return wrapTraitValue(Trait.ICON_URL);
        }

        @Override
        default Optional<URL> getProxyIconURL() {
            return wrapTraitValue(Trait.PROXY_ICON_URL);
        }

        interface Trait {
            JsonTrait<String, String> NAME = identity(JsonNode::asText, "name");
            JsonTrait<String,URL> URL = simple(JsonNode::asText, "url", Util::url_rethrow);
            JsonTrait<String, URL> ICON_URL = simple(JsonNode::asText, "icon_url", Util::url_rethrow);
            JsonTrait<String,URL> PROXY_ICON_URL = simple(JsonNode::asText, "proxy_icon_url", Util::url_rethrow);
        }
    }

    @JsonTraits(Footer.Trait.class)
    interface Field extends Embed.Field, JsonDeserializable {
        @Override 
        default String getName() {
            return getTraitValue(Trait.NAME);
        }

        @Override
        default String getValue() {
            return getTraitValue(Trait.VALUE);
        }

        @Override
        default boolean isInline() {
            return getTraitValue(Trait.INLINE);
        }

        interface Trait {
            JsonTrait<String, String> NAME = identity(JsonNode::asText, "name");
            JsonTrait<String, String> VALUE = identity(JsonNode::asText, "value");
            JsonTrait<Boolean, Boolean> INLINE = identity(JsonNode::asBoolean, "inline");
        }
    }
}
