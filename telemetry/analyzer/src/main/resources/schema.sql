-- Таблица для хранения информации о датчиках умного дома
CREATE TABLE IF NOT EXISTS sensors (
                                       id VARCHAR PRIMARY KEY,
                                       hub_id VARCHAR
);

-- Таблица для параметров условий активации
CREATE TABLE IF NOT EXISTS conditions (
                                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                          type VARCHAR,
                                          operation VARCHAR,
                                          value INTEGER
);

-- Таблица для определения доступных действий
CREATE TABLE IF NOT EXISTS actions (
                                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       type VARCHAR,
                                       value INTEGER
);

-- Основная таблица для хранения автоматизаций
CREATE TABLE IF NOT EXISTS scenarios (
                                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                         hub_id VARCHAR,
                                         name VARCHAR,
                                         UNIQUE(hub_id, name)
    );

-- Связующая таблица для сопоставления сценариев с условиями и датчиками
CREATE TABLE IF NOT EXISTS scenario_conditions (
                                                   scenario_id BIGINT REFERENCES scenarios(id),
    sensor_id VARCHAR REFERENCES sensors(id),
    condition_id BIGINT REFERENCES conditions(id),
    PRIMARY KEY (scenario_id, sensor_id, condition_id)
    );

-- Связующая таблица для определения действий сценария
CREATE TABLE IF NOT EXISTS scenario_actions (
                                                scenario_id BIGINT REFERENCES scenarios(id),
    sensor_id VARCHAR REFERENCES sensors(id),
    action_id BIGINT REFERENCES actions(id),
    PRIMARY KEY (scenario_id, sensor_id, action_id)
    );

-- Вспомогательная функция для проверки соответствия хабов
CREATE OR REPLACE FUNCTION check_hub_id()
RETURNS TRIGGER AS
'
BEGIN
    IF (SELECT hub_id FROM scenarios WHERE id = NEW.scenario_id) != (SELECT hub_id FROM sensors WHERE id = NEW.sensor_id) THEN
        RAISE EXCEPTION ''Hub IDs do not match for scenario_id % and sensor_id %'', NEW.scenario_id, NEW.sensor_id;
END IF;
RETURN NEW;
END;
'
LANGUAGE plpgsql;

-- Триггер для обеспечения целостности данных при добавлении условий
CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION check_hub_id();

-- Триггер для обеспечения целостности данных при добавлении действий
CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON scenario_actions
FOR EACH ROW
EXECUTE FUNCTION check_hub_id();