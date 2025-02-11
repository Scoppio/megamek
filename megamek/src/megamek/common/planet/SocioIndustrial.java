package megamek.common.planet;

import java.util.stream.Stream;

public class SocioIndustrial extends SourcedProperty<SocioIndustrialMarker[]> {

    public SocioIndustrial(SourceType sourceType, SocioIndustrialMarker[] value) {
        super(sourceType, value);
    }

    public String getSocioIndustrialString() {
        return String.join("-", Stream.of(getValue()).map(Enum::name).toList());
    }
}
