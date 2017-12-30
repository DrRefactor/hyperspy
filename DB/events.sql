
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


DROP EVENT stop_capsule_on_station;
CREATE EVENT stop_capsule_on_station
  ON SCHEDULE
    EVERY 1 SECOND
DO CALL stop_capsules_on_station();

drop event IF EXISTS start_capsule_from_station_continue;
create EVENT start_capsule_from_station_continue
  on SCHEDULE
  every 30 SECOND
  DO call start_capsules_from_station_continue();



