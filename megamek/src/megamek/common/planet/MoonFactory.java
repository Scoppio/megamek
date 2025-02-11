package megamek.common.planet;

import java.util.Random;

public class MoonFactory {

    private final Random random;

    public MoonFactory(Random random) {
        this.random = random;
    }

    public Moon createMoon(MoonData data) {
        Moon moon = new Moon();
        moon.setName(data.getName());
        moon.setIcon(data.getIcon());

        // Size: convert "large", "medium", "small" to the enum
        moon.setSize(determineMoonSize(data.getSize()));

        return moon;
    }

    private MoonSize determineMoonSize(String sizeString) {
        if (sizeString == null) return MoonSize.SMALL;
        return switch (sizeString.toLowerCase()) {
            case "large" -> MoonSize.LARGE;
            case "medium" -> MoonSize.MEDIUM;
            default -> MoonSize.SMALL;
        };
    }

}
