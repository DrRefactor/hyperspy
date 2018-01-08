DROP EVENT IF EXISTS delete_unused_states;
CREATE EVENT delete_unused_states
  ON SCHEDULE
    EVERY 1 DAY
    STARTS timestamp(current_date) + INTERVAL 5 MINUTE
DO
  DELETE FROM STATE
  WHERE id NOT IN (SELECT STATE_ID
                   FROM INFRASTRUCTURE_ELEMENT
                   UNION
                   SELECT STATE_ID
                   FROM CAPSULE
  );


DROP EVENT IF EXISTS stop_capsule_on_station;
CREATE EVENT stop_capsule_on_station
  ON SCHEDULE
    EVERY 1 SECOND
DO CALL stop_capsules_on_station();

DROP EVENT IF EXISTS start_capsule_from_station_continue;
CREATE EVENT start_capsule_from_station_continue
  ON SCHEDULE
    EVERY 30 SECOND
DO CALL start_capsules_from_station_continue();

DROP EVENT IF EXISTS start_capsule_from_origin_station;
create event start_capsule_from_origin_station
  ON SCHEDULE
    EVERY 1 MINUTE
DO CALL start_capsules_from_station_continue();


set GLOBAL EVENT_SCHEDULER = on;