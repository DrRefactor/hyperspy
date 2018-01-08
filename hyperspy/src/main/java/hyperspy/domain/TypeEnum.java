package hyperspy.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum TypeEnum {
    CAPSULE("capsule"), CAPSULE_TYPE("capsule-type"), CITY("city"), CONNECTION("connection"),
    INFRASTRUCTURE_ELEMENT("infrastructure-element"), INFRASTRUCTURE_ELEMENT_TYPE("infrastructure-element-type"),
    LINE("line"),
    STATE("state"), STATE_TYPE("state-type"), STATION("station"), STOPS_ON_ROUTE("stops-on-route"),
    TIMETABLE("timetable"), TIMETABLE_AT_TIMETABLE_DAY("timetable-at-timetable-day"), TIMETABLE_DAY("timetable-day"), TIMETABLE_TIME_FREQ("timetable-time-frequency"),
    WHERE_IS_CAPSULE("where-is-capsule");

    private final String name;

    public static Optional<TypeEnum> findByName(final String name){
        for (TypeEnum typeEnum : Arrays.asList(TypeEnum.values())) {
            if(typeEnum.name.equals(name.toLowerCase()))
                return Optional.of(typeEnum);
        }
        return Optional.empty();
    }
}
