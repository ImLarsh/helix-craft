
package dev.lovable.spiral;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SpiralType {
    HELIX("Classic spiral helix", 1),
    DOUBLE_HELIX("Intertwined double spiral", 2),
    WAVE("Undulating wave pattern", 1),
    TORNADO("Swirling tornado effect", 1),
    GALAXY("Galactic spiral arms", 3),
    DNA("DNA double helix structure", 2);

    private final String description;
    private final int streamCount;

    public static SpiralType fromString(final String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (final IllegalArgumentException ex) {
            return HELIX; // default fallback
        }
    }
}
