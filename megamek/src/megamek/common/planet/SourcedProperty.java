package megamek.common.planet;

public abstract class SourcedProperty<T> {

    private final SourceType sourceType;
    private final T value;

    public SourcedProperty(SourceType sourceType, T value) {
        this.sourceType = sourceType;
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public SourceType getSource() {
        return this.sourceType;
    }

    @Override
    public String toString() {
        return "SourcedProperty{" +
            "sourceType=" + sourceType +
            ", value=" + value +
            '}';
    }
}
