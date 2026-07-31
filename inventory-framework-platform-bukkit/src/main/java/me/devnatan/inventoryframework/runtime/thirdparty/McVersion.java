package me.devnatan.inventoryframework.runtime.thirdparty;

import java.util.Objects;
import org.bukkit.Bukkit;

public class McVersion implements Comparable<McVersion> {

    /**
     * Held apart from {@link McVersion} itself so that loading the class — to call
     * {@link #parse(String)} from a test, for one — does not require a running server.
     */
    private static final class Current {
        private static final McVersion VALUE = parse(Bukkit.getBukkitVersion());
    }

    /**
     * Reads the Minecraft version out of a Bukkit version string.
     *
     * <p>The qualifier that follows the numbers is not part of the version and differs per
     * distribution: {@code 1.20.6-R0.1-SNAPSHOT} on Spigot, {@code 26.2.build.84-stable} on the
     * builds Paper publishes under the year-based scheme — where the third dot-separated component
     * is the word {@code build} rather than a patch number.
     *
     * @param bukkitVersion The version as reported by {@link Bukkit#getBukkitVersion()}.
     * @return The Minecraft version, with any component that is not a number left at zero.
     */
    static McVersion parse(final String bukkitVersion) {
        final int qualifierAt = bukkitVersion.indexOf('-');
        final String[] parts =
                (qualifierAt == -1 ? bukkitVersion : bukkitVersion.substring(0, qualifierAt)).split("\\.");

        final int[] numbers = new int[] {0, 0, 0};
        for (int i = 0; i < Math.min(parts.length, numbers.length); i++) {
            final int number = leadingNumber(parts[i]);
            // Nothing numeric left: whatever follows belongs to the build identifier, not to the
            // version, so the remaining components stay at zero.
            if (number == -1) break;
            numbers[i] = number;
        }

        return new McVersion(numbers[0], numbers[1], numbers[2]);
    }

    private static int leadingNumber(final String part) {
        int end = 0;
        while (end < part.length() && Character.isDigit(part.charAt(end))) end++;
        return end == 0 ? -1 : Integer.parseInt(part.substring(0, end));
    }

    private final int major;
    private final int minor;
    private final int patch;

    public McVersion(final int major, final int minor) {
        this(major, minor, 0);
    }

    public McVersion(final int major, final int minor, final int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Gets the currently running McVersion
     */
    public static McVersion current() {
        return Current.VALUE;
    }

    public boolean isAtLeast(final McVersion other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    public int compareTo(final McVersion other) {
        if (this.major > other.major) return 3;
        if (other.major > this.major) return -3;
        if (this.minor > other.minor) return 2;
        if (other.minor > this.minor) return -2;
        return Integer.compare(this.patch, other.patch);
    }

    /**
     * Gets the "major" part of this McVersion. For 1.16.5, this would be 1
     */
    public int getMajor() {
        return major;
    }

    /**
     * Gets the "minor" part of this McVersion. For 1.16.5, this would be 16
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Gets the "patch" part of this McVersion. For 1.16.5, this would be 5.
     */
    public int getPatch() {
        return patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final McVersion mcVersion = (McVersion) o;
        return major == mcVersion.major && minor == mcVersion.minor && patch == mcVersion.patch;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        if (patch == 0) {
            return major + "." + minor;
        } else {
            return major + "." + minor + "." + patch;
        }
    }

    public boolean isAtLeast(final int major, final int minor, final int patch) {
        return this.isAtLeast(new McVersion(major, minor, patch));
    }

    public boolean isAtLeast(final int major, final int minor) {
        return this.isAtLeast(new McVersion(major, minor));
    }

    /**
     * Checks whether the server version is equal or greater than the given version.
     *
     * @param minorNumber the version to compare the server version with.
     * @return true if the version is equal or newer, otherwise false.
     * @see #current()
     * @since 4.0.0
     */
    public static boolean supports(int minorNumber) {
        return current().isAtLeast(1, minorNumber);
    }

    /**
     * Checks whether the server version is equal or greater than the given version.
     *
     * @param minorNumber the version to compare the server version with.
     * @param patchNumber the version to compare the server version with.
     * @return true if the version is equal or newer, otherwise false.
     * @see #current()
     * @since 4.0.0
     */
    public static boolean supports(int minorNumber, int patchNumber) {
        return current().isAtLeast(1, minorNumber, patchNumber);
    }

    /**
     * Whether the server is running a "modern" Mojang-mapped NMS naming scheme (1.17+, including
     * the year-based versioning scheme introduced afterwards, e.g. 26.x), as opposed to the legacy
     * obfuscated/versioned CraftBukkit naming scheme used prior to 1.17.
     */
    public static boolean isModern() {
        return current().getMajor() > 1 || current().getMinor() >= 17;
    }
}
