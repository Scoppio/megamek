package megamek.common;

public enum EngineType {
    COMBUSTION_ENGINE(0),
    NORMAL_ENGINE(1),
    XL_ENGINE(2),
    XXL_ENGINE(3),
    FUEL_CELL(4),
    LIGHT_ENGINE(5),
    COMPACT_ENGINE(6),
    FISSION(7),
    NONE(8),
    MAGLEV(9),
    STEAM(10),
    BATTERY(11),
    SOLAR(12),
    EXTERNAL(13);

    final int engineType;

    EngineType(int engineType) {
        this.engineType = engineType;
    }

    public int getEngineType() {
        return engineType;
    }

    static EngineType getEngineType(int type) {
        for (EngineType engineType : EngineType.values()) {
            if (engineType.getEngineType() == type) {
                return engineType;
            }
        }
        return null;
    }
}
