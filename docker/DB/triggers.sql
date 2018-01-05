-- INFRASTRUCTURE ELEMENT

CREATE TRIGGER delete_infrastructure_element
AFTER DELETE ON INFRASTRUCTURE_ELEMENT
FOR EACH ROW
  BEGIN
    DELETE FROM STATE
    WHERE id = OLD.STATE_ID;
  END;

-- STATION

CREATE TRIGGER create_station
BEFORE INSERT ON STATION
FOR EACH ROW
  BEGIN
    DECLARE state_type_id, state_id, element_type_id INT;
    CALL check_value_below_zero(new.PLATFORMS_NO);
    SELECT id
    INTO state_type_id
    FROM STATE_TYPE
    WHERE TYPE = 'OK';
    INSERT INTO STATE (STATE_TYPE_ID, DESCRIPTION) VALUE
      (state_type_id, 'Initial status');
    SELECT last_insert_id()
    INTO state_id;
    SELECT id
    INTO element_type_id
    FROM INFRASTRUCTURE_ELEMENT_TYPE
    WHERE TYPE = 'STATION';
    INSERT INTO INFRASTRUCTURE_ELEMENT (INFRASTRUCTURE_ELEMENT_TYPE_ID, STATE_ID) VALUES
      (element_type_id, state_id);
    SET new.ID = LAST_INSERT_ID();
  END;

CREATE TRIGGER delete_station
AFTER DELETE ON STATION
FOR EACH ROW
  BEGIN
    DELETE FROM INFRASTRUCTURE_ELEMENT
    WHERE INFRASTRUCTURE_ELEMENT.ID = OLD.ID;
  END;

-- CONNECTION

CREATE TRIGGER create_connection
BEFORE INSERT ON CONNECTION
FOR EACH ROW
  BEGIN
    DECLARE state_type_id, state_id, element_type_id INT;

    CALL check_value_below_zero(new.max_speed);
    CALL check_value_below_zero(new.distance);

    SELECT id
    INTO state_type_id
    FROM STATE_TYPE
    WHERE TYPE = 'OK';
    INSERT INTO STATE (STATE_TYPE_ID, DESCRIPTION) VALUE
      (state_type_id, 'Initial status');
    SELECT last_insert_id()
    INTO state_id;
    SELECT id
    INTO element_type_id
    FROM INFRASTRUCTURE_ELEMENT_TYPE
    WHERE TYPE = 'CONNECTION';
    INSERT INTO INFRASTRUCTURE_ELEMENT (INFRASTRUCTURE_ELEMENT_TYPE_ID, STATE_ID) VALUES
      (element_type_id, state_id);
    SET new.ID = LAST_INSERT_ID();

  END;

CREATE TRIGGER update_connection
BEFORE UPDATE ON CONNECTION
FOR EACH ROW
  BEGIN
    CALL check_value_below_zero(new.max_speed);
    CALL check_value_below_zero(new.distance);
  END;

CREATE TRIGGER delete_connection
AFTER DELETE ON CONNECTION
FOR EACH ROW
  BEGIN
    DELETE FROM INFRASTRUCTURE_ELEMENT
    WHERE INFRASTRUCTURE_ELEMENT.ID = OLD.ID;
  END;

-- STOPS_ON_ROUTE

CREATE TRIGGER new_stop_check_sequence
AFTER INSERT ON STOPS_ON_ROUTE
FOR EACH ROW
  BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE n INT DEFAULT 1;
    DECLARE if_exists BOOLEAN;
    SELECT max(SEQUENCE_NO)
    FROM STOPS_ON_ROUTE
    WHERE LINE_ID = new.LINE_ID
    INTO n;
    WHILE i < n DO
      SELECT exists(SELECT *
                    FROM STOPS_ON_ROUTE
                    WHERE LINE_ID = new.LINE_ID AND SEQUENCE_NO = i)
      INTO if_exists;
      IF if_exists = 0
      THEN
        SIGNAL SQLSTATE '45001'
        SET MESSAGE_TEXT = 'Incorrect sequence';
      END IF;
      SET i = i + 1;
    END WHILE;
  END;

CREATE TRIGGER new_stop_check_connection
AFTER INSERT ON STOPS_ON_ROUTE
FOR EACH ROW
FOLLOWS new_stop_check_sequence
  BEGIN
    DECLARE last_station INT;
    DECLARE if_exists_there, if_exists_back BOOLEAN;

    SELECT STATION_ID
    FROM STOPS_ON_ROUTE
    WHERE SEQUENCE_NO = new.SEQUENCE_NO - 1
          AND LINE_ID = new.LINE_ID
    INTO last_station;
    IF last_station IS NOT NULL
    THEN
      SELECT exists(SELECT id
                    FROM CONNECTION
                    WHERE START_STATION = last_station AND END_STATION = new.STATION_ID)
      INTO if_exists_there;
      SELECT exists(SELECT id
                    FROM CONNECTION
                    WHERE START_STATION = new.STATION_ID AND END_STATION = last_station)
      INTO if_exists_back;
      IF if_exists_there = 0 OR if_exists_back = 0
      THEN
        SIGNAL SQLSTATE '54002'
        SET MESSAGE_TEXT = 'Connection does not exist';
      END IF;
    END IF;
  END;

-- TIMETABLE_AT_TIMETABLE_DAY

CREATE TRIGGER new_timetable_on_day
BEFORE INSERT ON TIMETABLE_AT_TIMETABLE_DAY
FOR EACH ROW
  BEGIN
    DECLARE id_ INT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE curr_from, curr_until, from_date_, until DATETIME;
    DECLARE curs CURSOR FOR SELECT DISTINCT TIMETABLE_ID
                            FROM TIMETABLE_AT_TIMETABLE_DAY
                            WHERE TIMETABLE_DAY_DAY_OF_WEEK = new.TIMETABLE_DAY_DAY_OF_WEEK
                                  AND TIMETABLE_ID IN (SELECT ID
                                                       FROM TIMETABLE
                                                       WHERE LINE_ID = (SELECT LINE_ID
                                                                        FROM TIMETABLE
                                                                        WHERE id = new.TIMETABLE_ID))
                                  AND TIMETABLE_ID <> new.TIMETABLE_ID;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    SELECT
      FROM_DATE,
      UNTIL
    FROM TIMETABLE
    WHERE ID = new.TIMETABLE_ID
    INTO curr_from, curr_until;
    OPEN curs;
    timetables_loop : LOOP

      SET done = FALSE;

      FETCH curs
      INTO id_;

      IF done
      THEN
        LEAVE timetables_loop;
      END IF;

      SELECT
        FROM_DATE,
        UNTIL
      FROM TIMETABLE
      WHERE ID = id_
      INTO from_date_, until;

      IF (curr_from BETWEEN from_date_ AND until)
         OR (from_date_ BETWEEN curr_from AND curr_until)
      THEN
        SIGNAL SQLSTATE '54003'
        SET MESSAGE_TEXT = 'Timetables overlap';
      END IF;
    END LOOP;
    CLOSE curs;
  END;

-- CAPSULE

CREATE TRIGGER create_capsule
BEFORE INSERT ON CAPSULE
FOR EACH ROW
  BEGIN
    DECLARE state_type_id INT;

    SELECT ID
    FROM STATE_TYPE
    WHERE TYPE = 'OK'
    INTO state_type_id;
    INSERT INTO STATE (STATE_TYPE_ID, DESCRIPTION) VALUES (state_type_id, 'Initial status');
    SET new.STATE_ID = last_insert_id();
  END;

-- TODO's
# update trigger on stops_on_route
# update trigger on TIMETABLE_AT_TIMETABLE_DAY
# update trigger on timetable (from, until)





