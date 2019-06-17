package de.kaleidox.crystalshard.api.entity.message.embed;

import de.kaleidox.crystalshard.api.entity.Nameable;
import de.kaleidox.crystalshard.api.util.FileContainer;
import de.kaleidox.crystalshard.internal.InternalInjector;

import java.awt.Color;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public interface EmbedDraft extends Embed {
    Optional<String> getTitle();

    Optional<String> getDescription();

    Optional<URL> getUrl();

    Optional<Instant> getTimestamp();

    Optional<Color> getColor();

    Optional<Footer> getFooter();

    Optional<Image> getImage();

    Optional<Thumbnail> getThumbnail();

    Optional<Author> getAuthor();

    List<Field> getFields();

    interface Footer extends FileContainer.Containable {
        String getText();

        Optional<URL> getIconUrl();

        static Footer BUILD(String text, String iconUrl) {
            return InternalInjector.newInstance(Footer.class, text, iconUrl);
        }
    }

    interface Image extends FileContainer.Containable {
        Optional<URL> getUrl();

        static Image BUILD(String url) {
            return InternalInjector.newInstance(Image.class, url);
        }
    }

    interface Author extends Nameable, FileContainer.Containable {
        Optional<URL> getUrl();

        Optional<URL> getIconUrl();

        static Author BUILD(String name, String url, String iconUrl) {
            return InternalInjector.newInstance(Author.class, name, url, iconUrl);
        }
    }

    interface Thumbnail extends FileContainer.Containable {
        Optional<URL> getUrl();

        static Thumbnail BUILD(String url) {
            return InternalInjector.newInstance(Thumbnail.class, url);
        }
    }

    interface Field {
        String getTitle();

        String getText();

        boolean isInline();

        int getTotalCharCount();

        default Optional<EditableField> toEditableField() {
            return Optional.of(InternalInjector.newInstance(EditableField.class, this));
        }

        static Field BUILD(String title, String text, boolean inline) {
            return InternalInjector.newInstance(Field.class, title, text, (Objects.nonNull(inline) && inline));
        }
    }

    interface EditableField extends Field {
        static EditableField BUILD(Field fromField) {
            return InternalInjector.newInstance(EditableField.class, fromField);
        }
    }
}