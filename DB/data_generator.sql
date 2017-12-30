/* GENERATORS */

-- CITY generator
DELIMITER //
DROP PROCEDURE IF EXISTS cities_generator;
CREATE PROCEDURE cities_generator( in nCount INTEGER )
BEGIN
  DECLARE i INT;
  DECLARE city_name VARCHAR( 50 );
  DECLARE country_name VARCHAR( 50 );
  DECLARE post_code VARCHAR( 20 );

  DROP TEMPORARY TABLE IF EXISTS cnames;
  CREATE TEMPORARY TABLE cnames
  (
    `str`   VARCHAR(50)
  );
  INSERT INTO cnames VALUES ( 'Płock' );
  INSERT INTO cnames VALUES ( 'Warszawa' );
  INSERT INTO cnames VALUES ( 'Kraków' );
  INSERT INTO cnames VALUES ( 'Poznań' );
  INSERT INTO cnames VALUES ( 'Berlin' );
  INSERT INTO cnames VALUES ( 'Moskwa' );
  INSERT INTO cnames VALUES ( 'Nowy Jork' );
  INSERT INTO cnames VALUES ( 'Praga' );
  INSERT INTO cnames VALUES ( 'Bratysława' );
  INSERT INTO cnames VALUES ( 'Paryż' );
  INSERT INTO cnames VALUES ( 'Madryt' );
  INSERT INTO cnames VALUES ( 'Barcelona' );
  INSERT INTO cnames VALUES ( 'Manchester' );
  INSERT INTO cnames VALUES ( 'Londyn' );
  INSERT INTO cnames VALUES ( 'Dublin' );
  INSERT INTO cnames VALUES ( 'Tokio' );
  INSERT INTO cnames VALUES ( 'Olsztyn' );
  INSERT INTO cnames VALUES ( 'Mogadiuszu' );
  INSERT INTO cnames VALUES ( 'Wiedeń' );
  INSERT INTO cnames VALUES ( 'Lwów' );
  INSERT INTO cnames VALUES ( 'Wilno' );

  DROP TEMPORARY TABLE IF EXISTS countries;
  CREATE TEMPORARY TABLE countries
  (
    `str`   VARCHAR( 50 )
  );
  INSERT INTO countries VALUES ( 'Sri Lanka' );
  INSERT INTO countries VALUES ( 'Dania' );
  INSERT INTO countries VALUES ( 'Anglia' );
  INSERT INTO countries VALUES ( 'Niemcy' );
  INSERT INTO countries VALUES ( 'Słowacja');
  INSERT INTO countries VALUES ( 'Czechy' );
  INSERT INTO countries VALUES ( 'Hiszpania' );
  INSERT INTO countries VALUES ( 'Francja' );
  INSERT INTO countries VALUES ( 'USA' );
  INSERT INTO countries VALUES ( 'Gruzja' );
  INSERT INTO countries VALUES ( 'Etiopia' );
  INSERT INTO countries VALUES ( 'Kenia' );
  INSERT INTO countries VALUES ( 'Somalia' );
  INSERT INTO countries VALUES ( 'Polska' );
  INSERT INTO countries VALUES ( 'Rosja' );
  INSERT INTO countries VALUES ( 'Ukraina' );
  INSERT INTO countries VALUES ( 'Litwa' );

  DROP TEMPORARY TABLE IF EXISTS postal_codes;
  CREATE TEMPORARY TABLE postal_codes
  (
    `str`   VARCHAR( 20 )
  );
  INSERT INTO postal_codes VALUES ( '513-321' );
  INSERT INTO postal_codes VALUES ( '9888888' );
  INSERT INTO postal_codes VALUES ( '11111111' );
  INSERT INTO postal_codes VALUES ( '01-1121' );
  INSERT INTO postal_codes VALUES ( '55-78889' );
  INSERT INTO postal_codes VALUES ( '136924' );
  INSERT INTO postal_codes VALUES ( '13579' );
  INSERT INTO postal_codes VALUES ( '02468' );
  INSERT INTO postal_codes VALUES ( '1029384' );
  INSERT INTO postal_codes VALUES ( '2244444' );
  INSERT INTO postal_codes VALUES ( '98765' );
  INSERT INTO postal_codes VALUES ( '00731' );
  INSERT INTO postal_codes VALUES ( '09-400' );
  INSERT INTO postal_codes VALUES ( '09-454' );
  INSERT INTO postal_codes VALUES ( '1-23-56' );
  INSERT INTO postal_codes VALUES ( '123456768' );
  INSERT INTO postal_codes VALUES ( '7689-121' );
  INSERT INTO postal_codes VALUES ( '666666' );
  INSERT INTO postal_codes VALUES ( '01-222' );
  INSERT INTO postal_codes VALUES ( '012345' );

  SET i = 0;
  WHILE i < nCount
  DO
    SELECT str INTO city_name FROM cnames ORDER BY RAND() LIMIT 1;
    SELECT str INTO country_name FROM countries ORDER BY RAND() LIMIT 1;
    SELECT str INTO post_code FROM postal_codes ORDER BY RAND() LIMIT 1;
    INSERT INTO city VALUES ( NULL, city_name, post_code, country_name );
    SET i = i + 1;
  END WHILE;

DROP TABLE IF EXISTS cnames;
DROP TABLE IF EXISTS countries;
DROP TABLE IF EXISTS postal_codes;

COMMIT;
END //
DELIMITER ;


-- STATION generator
DELIMITER //
DROP PROCEDURE IF EXISTS stations_generator;
CREATE PROCEDURE stations_generator( in nScale INT )
BEGIN
  DECLARE cities_count, i INT;
  DECLARE x_pos, y_pos DECIMAL( 6, 3 );
  DECLARE platforms_no INT;
  SELECT COUNT( * ) INTO cities_count FROM `city`;
  SET i = 0;
  WHILE i < cities_count
  DO
    SET i = i + 1;
    SET x_pos = rand() * nScale;
    SET y_pos = rand() * nScale;
    SET platforms_no = floor( rand() * 10 ) + 1;
    INSERT INTO station ( `CITY_ID`, `PLATFORMS_NO`, `COOR_X`, `COOR_Y` ) VALUES ( i, platforms_no, x_pos, y_pos );
  END WHILE;

COMMIT;
END //
DELIMITER ;

-- CONNECTION generator
DELIMITER $$
DROP PROCEDURE IF EXISTS connections_generator;
CREATE PROCEDURE connections_generator( IN nCount INT )
BEGIN
  DECLARE i, station_1, station_2, max_speed_1, max_speed_2, distance_1, distance_2, x_1, y_1, x_2, y_2, distance_x2, distance_y2, tmp_count INT;
  DECLARE x_tmp, y_tmp DECIMAL( 6, 3 );
  SET i = 0;
  WHILE i < nCount
  DO
    SET i = i + 1;
    SELECT ID, COOR_X, COOR_Y INTO station_1, x_tmp, y_tmp FROM station ORDER BY RAND() LIMIT 1;
    SET x_1 = floor( x_tmp );
    SET y_1 = floor( y_tmp );
    SELECT ID, COOR_X, COOR_Y INTO station_2, x_tmp, y_tmp FROM station WHERE ID != station_1 ORDER BY RAND() LIMIT 1;
    SET x_2 = floor( x_tmp );
    SET y_2 = floor( y_tmp );
    SELECT COUNT( * ) INTO tmp_count FROM connection WHERE ( START_STATION = station_1 AND END_STATION = station_2 ) OR ( START_STATION = station_2 AND END_STATION = station_1 );
    IF tmp_count = 0 THEN
      SET distance_x2 = ( x_1 - x_2 ) * ( x_1 - x_2 );
      SET distance_y2 = ( y_1 - y_2 ) * ( y_1 - y_2 );
      SET distance_1 = sqrt( distance_x2 + distance_y2 );
      SET max_speed_1 = 100 + floor( rand() * 100 );
      SET max_speed_2 = 100 + floor( rand() * 100 );
      SET distance_2 = distance_1 + floor( rand() * 50 );
      INSERT INTO connection ( `START_STATION`, `END_STATION`, `DISTANCE`, `MAX_SPEED` ) VALUES ( station_1, station_2, distance_1, max_speed_1 );
      INSERT INTO connection ( `START_STATION`, `END_STATION`, `DISTANCE`, `MAX_SPEED` ) VALUES ( station_2, station_1, distance_2, max_speed_2 );
    ELSE
      SET i = i - 1;
    END IF;
  END WHILE;

COMMIT;
END $$
DELIMITER ;

-- STOPS_ON_ROUTE AND LINE generator
DELIMITER $$
DROP PROCEDURE IF EXISTS lines_and_stops_generator;
CREATE PROCEDURE lines_and_stops_generator()
BEGIN
  DECLARE i, connections_count, line_id, stop_1, stop_2, stop_3, stop_4, stop_5, tmp_count, stops_count INT;
  DECLARE line_desc VARCHAR( 45 );
  DECLARE cursor1 CURSOR FOR SELECT START_STATION ,END_STATION FROM CONNECTION WHERE MOD( ID, 2 ) = 1;
  SET line_id = 1;
  SELECT COUNT( * ) INTO connections_count FROM connection;
  SET connections_count = connections_count / 2;
  SET i = 1;
  OPEN cursor1;

  WHILE i < connections_count
  DO
    SET i = i + 1;
    FETCH cursor1 INTO  stop_1, stop_2;
    -- check if exist line with at least 3 stations
    SELECT COUNT( * ), END_STATION INTO tmp_count, stop_3 FROM connection WHERE START_STATION = stop_2 AND END_STATION != stop_1;
    IF tmp_count > 0 THEN
      SELECT CONCAT( line_id, '_A') INTO line_desc;
      INSERT INTO line ( NAME ) VALUES ( line_desc );
      SELECT CONCAT( line_id, '_B') INTO line_desc;
      INSERT INTO line ( NAME ) VALUES ( line_desc );

      SET stops_count = 3;
      -- check if exist 4th stop
      SELECT COUNT( * ), END_STATION INTO tmp_count, stop_4 FROM connection WHERE START_STATION = stop_3 AND END_STATION != stop_1 AND END_STATION != stop_2;
      IF tmp_count > 0 THEN
        SET stops_count = 4;
        -- check if exist 5th stop
        SELECT COUNT( * ), END_STATION INTO tmp_count, stop_5 FROM connection WHERE START_STATION = stop_4 AND END_STATION != stop_1 AND END_STATION != stop_2 AND END_STATION != stop_3;
        IF tmp_count > 0 THEN
          SET stops_count = 5;
        END IF;
      END IF;

      -- insert stops
      IF stops_count = 3 THEN
        INSERT INTO stops_on_route VALUES( line_id, stop_1, 1 );
        INSERT INTO stops_on_route VALUES( line_id, stop_2, 2 );
        INSERT INTO stops_on_route VALUES( line_id, stop_3, 3 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_3, 1 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_2, 2 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_1, 3 );
      ELSEIF stops_count = 4 THEN
        INSERT INTO stops_on_route VALUES( line_id, stop_1, 1 );
        INSERT INTO stops_on_route VALUES( line_id, stop_2, 2 );
        INSERT INTO stops_on_route VALUES( line_id, stop_3, 3 );
        INSERT INTO stops_on_route VALUES( line_id, stop_4, 4 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_4, 1 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_3, 2 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_2, 3 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_1, 4 );
      ELSE
        INSERT INTO stops_on_route VALUES( line_id, stop_1, 1 );
        INSERT INTO stops_on_route VALUES( line_id, stop_2, 2 );
        INSERT INTO stops_on_route VALUES( line_id, stop_3, 3 );
        INSERT INTO stops_on_route VALUES( line_id, stop_4, 4 );
        INSERT INTO stops_on_route VALUES( line_id, stop_5, 5 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_5, 1 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_4, 2 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_3, 3 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_2, 4 );
        INSERT INTO stops_on_route VALUES( line_id + 1, stop_1, 5 );
      END IF;
      SET line_id = line_id + 2;
    END IF;
  END WHILE;
  CLOSE cursor1;

  COMMIT;
END $$
DELIMITER ;

-- TIMETABLE generator
DELIMITER $$
DROP PROCEDURE IF EXISTS timetables_generator;
CREATE PROCEDURE timetables_generator()
BEGIN
  DECLARE line_id, lines_count, timetable_id INT;
  SET line_id = 0;
  SET timetable_id = 0;
  SELECT COUNT( * ) INTO lines_count FROM line;
  WHILE line_id < lines_count
  DO
    -- working days
    SET line_id = line_id + 1;

    SET timetable_id = timetable_id + 1;
    INSERT INTO timetable VALUES( NULL, line_id, STR_TO_DATE( '2017-01-01', '%Y-%m-%d' ), STR_TO_DATE( '2018-01-31', '%Y-%m-%d' ) );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 1 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 2 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 3 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 4 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 5 );
    INSERT INTO timetable_time_freq VALUES ( '07:00:00', timetable_id, 5 );
    INSERT INTO timetable_time_freq VALUES ( '11:00:00', timetable_id, 10 );
    INSERT INTO timetable_time_freq VALUES ( '15:00:00', timetable_id, 5 );
    INSERT INTO timetable_time_freq VALUES ( '20:00:00', timetable_id, 20 );

    -- weekend
    SET timetable_id = timetable_id + 1;
    INSERT INTO timetable VALUES( NULL, line_id, STR_TO_DATE( '2017-01-01', '%Y-%m-%d' ), STR_TO_DATE( '2018-01-31', '%Y-%m-%d' ) ); -- weekend
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 6 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 7 );
    INSERT INTO timetable_time_freq VALUES ( '08:00:00', timetable_id, 10 );
    INSERT INTO timetable_time_freq VALUES ( '20:00:00', timetable_id, 20 );


    -- working days
    SET timetable_id = timetable_id + 1;
    INSERT INTO timetable VALUES( NULL, line_id, STR_TO_DATE( '2018-02-01', '%Y-%m-%d' ), STR_TO_DATE( '2018-12-31', '%Y-%m-%d' ) );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 1 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 2 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 3 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 4 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 5 );
    INSERT INTO timetable_time_freq VALUES ( '07:00:00', timetable_id, 5 );
    INSERT INTO timetable_time_freq VALUES ( '11:00:00', timetable_id, 10 );
    INSERT INTO timetable_time_freq VALUES ( '15:00:00', timetable_id, 5 );
    INSERT INTO timetable_time_freq VALUES ( '20:00:00', timetable_id, 20 );


    -- weekend
    SET timetable_id = timetable_id + 1;
    INSERT INTO timetable VALUES( NULL, line_id, STR_TO_DATE( '2018-02-01', '%Y-%m-%d' ), STR_TO_DATE( '2018-12-31', '%Y-%m-%d' ) ); -- weekend
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 6 );
    INSERT INTO timetable_at_timetable_day VALUES ( timetable_id, 7 );
    INSERT INTO timetable_time_freq VALUES ( '08:00:00', timetable_id, 10 );
    INSERT INTO timetable_time_freq VALUES ( '20:00:00', timetable_id, 20 );
  END WHILE;

COMMIT;
END $$
DELIMITER ;

INSERT INTO capsule_type VALUES ( NULL, 'SMALL', 500 );
INSERT INTO capsule_type VALUES ( NULL, 'MEDIUM', 1000 );
INSERT INTO capsule_type VALUES ( NULL, 'LARGE', 2000 );
INSERT INTO capsule_type VALUES ( NULL, 'XXL', 5000 );

-- CAPSULE generator
DELIMITER //
DROP PROCEDURE IF EXISTS capsules_generator;
CREATE PROCEDURE capsules_generator( IN nCapsulesForLine INT )
BEGIN
  DECLARE lines_count, lineid, j, mod_count, stop_id, serial_nr INT;
  -- DECLARE date_now DATETIME;
  SELECT COUNT( * ) INTO lines_count FROM `line`;
  SELECT COUNT( * ) INTO mod_count FROM capsule_type;
  SET lineid = 0, j = 0, serial_nr = 0;
  WHILE lineid < lines_count
  DO
    SET lineid = lineid + 1;
    SET j = 0;
    SELECT STATION_ID INTO stop_id FROM stops_on_route WHERE LINE_ID = lineid AND SEQUENCE_NO = 1;
    WHILE j < nCapsulesForLine
    DO
      SET j = j + 1;
      SET serial_nr = serial_nr + 1;
      INSERT INTO CAPSULE ( PRODUCTION_DATE, CAPSULE_TYPE_ID, SERIAL_NO ) VALUES ( STR_TO_DATE( '2016-02-01', '%Y-%m-%d' ), ( MOD( j, mod_count ) + 1 ), serial_nr );
      SELECT NOW() INTO @date_now FROM DUAL;
      INSERT INTO where_is_capsule VALUES ( serial_nr, stop_id, @date_now );
    END WHILE;
  END WHILE;

COMMIT;
END //
DELIMITER ;


-- Run generators
CALL cities_generator( 600 );
CALL stations_generator( 300 ); -- argument -> scale on map; stations count = cities count
CALL connections_generator( 400 );
CALL lines_and_stops_generator();
CALL timetables_generator();
CALL capsules_generator( 10 );