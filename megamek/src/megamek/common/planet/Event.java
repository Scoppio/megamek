package megamek.common.planet;

import java.time.LocalDate;

public class Event implements Comparable<Event> {

    private final SourcedLocalDate date;
    private SourcedString faction;
    private SocioIndustrial socioIndustrial;
    private long population;

    public Event(SourcedLocalDate date) {
        this.date = date;
    }

    public SourcedLocalDate getSourcedLocalDate() {
        return date;
    }

    public LocalDate getDate() {
        return date.getValue();
    }

    public long getPopulation() {
        return population;
    }

    public SourcedString getFaction() {
        return faction;
    }

    public SocioIndustrial getSocioIndustrial() {
        return socioIndustrial;
    }

    @Override
    public int compareTo(Event o) {
        return date.getValue().compareTo(o.date.getValue());
    }
}
