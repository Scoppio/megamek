package megamek.common.planet;

import java.time.LocalDate;

public class SourcedLocalDate extends SourcedProperty<LocalDate> {

    public SourcedLocalDate(SourceType sourceType, LocalDate value) {
        super(sourceType, value);
    }
}
