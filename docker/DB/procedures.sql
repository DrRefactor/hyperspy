DROP PROCEDURE IF EXISTS check_value_below_zero;
CREATE PROCEDURE check_value_below_zero(val INT)
  BEGIN
    IF val < 0
    THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Value cannot be below zero';
    END IF;
  END;


DROP PROCEDURE IF EXISTS stop_capsules_on_station;
CREATE PROCEDURE stop_capsules_on_station()
  BEGIN


    DECLARE capsule, element_id INT;
    DECLARE start_time_ DATETIME;
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE end_station_, distance_, max_speed_ INT;
    DECLARE remaining_time DECIMAL(32, 16);

    DECLARE curs CURSOR FOR SELECT
                              CAPSULE_SIDE_NO,
                              INFRASTRUCTURE_ELEMENT_ID,
                              START_TIME
                            FROM WHERE_IS_CAPSULE
                              JOIN INFRASTRUCTURE_ELEMENT
                                ON WHERE_IS_CAPSULE.INFRASTRUCTURE_ELEMENT_ID = INFRASTRUCTURE_ELEMENT.ID
                              JOIN INFRASTRUCTURE_ELEMENT_TYPE
                                ON INFRASTRUCTURE_ELEMENT.INFRASTRUCTURE_ELEMENT_TYPE_ID =
                                   INFRASTRUCTURE_ELEMENT_TYPE.ID
                            WHERE TYPE = 'CONNECTION';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    OPEN curs;
    capsules_loop : LOOP

      SET done = FALSE;

      FETCH curs
      INTO capsule, element_id, start_time_;
      IF done
      THEN
        LEAVE capsules_loop;
      END IF;
      
      SELECT
        END_STATION,
        DISTANCE,
        MAX_SPEED
      FROM CONNECTION
      WHERE ID = element_id
      INTO end_station_, distance_, max_speed_;

      SET remaining_time = ((distance_ / max_speed_) * 3600) - timestampdiff(SECOND, start_time_, current_timestamp);

      IF remaining_time <= 0
      THEN
        UPDATE WHERE_IS_CAPSULE
        SET INFRASTRUCTURE_ELEMENT_ID = end_station_,
          START_TIME                  = current_timestamp
        WHERE CAPSULE_SIDE_NO = capsule;
      END IF;
    END LOOP;
    CLOSE curs;
  END;

DROP PROCEDURE IF EXISTS start_capsules_from_station_continue;
CREATE PROCEDURE start_capsules_from_station_continue()
  BEGIN
    DECLARE capsule INT;
    DECLARE station INT;
    DECLARE start_time_ DATETIME;
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE new_connection, line_id_ INT;
    DECLARE curs CURSOR FOR SELECT
                              CAPSULE_SIDE_NO,
                              INFRASTRUCTURE_ELEMENT_ID,
                              START_TIME
                            FROM WHERE_IS_CAPSULE
                              JOIN CAPSULE ON WHERE_IS_CAPSULE.CAPSULE_SIDE_NO = CAPSULE.SIDE_NO
                              JOIN INFRASTRUCTURE_ELEMENT
                                ON WHERE_IS_CAPSULE.INFRASTRUCTURE_ELEMENT_ID = INFRASTRUCTURE_ELEMENT.ID
                              JOIN INFRASTRUCTURE_ELEMENT_TYPE
                                ON INFRASTRUCTURE_ELEMENT.INFRASTRUCTURE_ELEMENT_TYPE_ID =
                                   INFRASTRUCTURE_ELEMENT_TYPE.ID
                            WHERE TYPE = 'STATION' AND CAPSULE.CURRENT_LINE IS NOT NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    OPEN curs;
    capsules_loop : LOOP

      SET done = FALSE;

      FETCH curs
      INTO capsule, station, start_time_;

      IF done
      THEN
        LEAVE capsules_loop;
      END IF;

      SELECT ID
      FROM LINE
        JOIN CAPSULE ON LINE.ID = CAPSULE.CURRENT_LINE
      WHERE capsule.SIDE_NO = capsule
      INTO line_id_;


      IF line_id_ IS NOT NULL
         AND current_timestamp >= timestampadd(SECOND, 1, start_time_)
      THEN

        SELECT id
        FROM CONNECTION
        WHERE START_STATION = station AND END_STATION = (SELECT STATION_ID
                                                         FROM STOPS_ON_ROUTE
                                                         WHERE LINE_ID = line_id_ AND
                                                               SEQUENCE_NO = (SELECT SEQUENCE_NO
                                                                              FROM STOPS_ON_ROUTE
                                                                              WHERE LINE_ID = line_id_ AND
                                                                                    STATION_ID = station) + 1)
        INTO new_connection;

        IF new_connection IS NOT NULL
        THEN

          UPDATE WHERE_IS_CAPSULE
          SET INFRASTRUCTURE_ELEMENT_ID = new_connection,
            START_TIME                  = current_timestamp
          WHERE CAPSULE_SIDE_NO = capsule;

        ELSE

          UPDATE CAPSULE
          SET CURRENT_LINE = NULL
          WHERE SIDE_NO = capsule;

        END IF;

      END IF;
    END LOOP;
    CLOSE curs;
  END;

DROP PROCEDURE IF EXISTS start_capsules_from_origin_stations;
CREATE PROCEDURE start_capsules_from_origin_stations()
  BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE line_, timetable_, frequency_, capsule_ INT;
    DECLARE start_hour_ TIME;
    DECLARE curs CURSOR FOR SELECT
                              LINE_ID,
                              TIMETABLE.ID
                            FROM TIMETABLE
                              JOIN TIMETABLE_AT_TIMETABLE_DAY ON TIMETABLE.ID = TIMETABLE_AT_TIMETABLE_DAY.TIMETABLE_ID
                              JOIN TIMETABLE_DAY
                                ON TIMETABLE_AT_TIMETABLE_DAY.TIMETABLE_DAY_DAY_OF_WEEK = TIMETABLE_DAY.DAY_OF_WEEK
                            WHERE current_timestamp BETWEEN TIMETABLE.FROM_DATE AND TIMETABLE.UNTIL AND
                                  TIMETABLE_DAY.DAY_OF_WEEK = dayofweek(current_date);

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    OPEN curs;
    lines_loop : LOOP

      SET done = FALSE;
      FETCH curs
      INTO line_, timetable_;

      IF done
      THEN
        LEAVE lines_loop;
      END IF;

      SELECT
        FREQUENCY,
        START_HOUR
      INTO frequency_, start_hour_
      FROM TIMETABLE_TIME_FREQ
      WHERE
        TIMETABLE_ID = timetable_ AND
        START_HOUR <= current_time
      ORDER BY START_HOUR DESC
      LIMIT 1;


      IF mod(timestampdiff(MINUTE, start_hour_, current_time), frequency_) = 0
      THEN

        SET capsule_ = NULL;

        SELECT CAPSULE_SIDE_NO
        INTO capsule_
        FROM WHERE_IS_CAPSULE
          JOIN CAPSULE ON WHERE_IS_CAPSULE.CAPSULE_SIDE_NO = CAPSULE.SIDE_NO
          JOIN STATE ON CAPSULE.STATE_ID = STATE.ID
          JOIN STATE_TYPE ON STATE.STATE_TYPE_ID = STATE_TYPE.ID
        WHERE
          STATE_TYPE.TYPE = 'OK' AND
          CURRENT_LINE IS NULL AND
          INFRASTRUCTURE_ELEMENT_ID = (SELECT STATION_ID
                                       FROM STOPS_ON_ROUTE
                                       WHERE LINE_ID = line_ AND SEQUENCE_NO = 1)
        LIMIT 1;

        IF capsule_ IS NOT NULL
        THEN

          UPDATE CAPSULE
          SET CURRENT_LINE = line_
          WHERE SIDE_NO = capsule_;

          UPDATE WHERE_IS_CAPSULE
          SET START_TIME = current_timestamp
          WHERE CAPSULE_SIDE_NO = capsule_;

        END IF;

      END IF;

    END LOOP;
    CLOSE curs;
  END;



