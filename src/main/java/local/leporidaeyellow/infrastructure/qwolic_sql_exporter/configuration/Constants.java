package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration;

public class Constants {
    public static final Integer START_VALUE_METRIC_GAUGE = -102030201;
    public static final String METRIC_COUNTER = "counter";
    public static final String METRIC_GAUGE = "gauge";

    public static final String APPLICATION_CONFIGURATION_FILE= "application.yaml";

    public static final String ORACLE_DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static final String POSTGRES_DB_DRIVER = "org.postgresql.ds.PGSimpleDataSource";
    public static final String CLICKHOUSE_DB_DRIVER = "ru.yandex.clickhouse.ClickHouseDriver";

    public static final String ORACLE_DB_DRIVER_NAME = "oracle";
    public static final String POSTGRES_DB_DRIVER_NAME = "postgresql";
    public static final String CLICKHOUSE_DB_DRIVER_NAME = "clickhouse";

    public static final String APP_PROPERTIES_SCHEDULER_PERIOD_OBSERVER = "${scheduler.period.observer}";
    public static final String APP_PROPERTIES_SCHEDULER_PERIOD_CLEANER = "${scheduler.period.cleaner}";

    public static final String APP_PROPERTIES_EXECUTOR_CORE_POOL_SIZE = "${executor.core.pool.size}";
    public static final String APP_PROPERTIES_EXECUTOR_MAX_POOL_SIZE = "${executor.max.pool.size}";
    public static final String APP_PROPERTIES_EXECUTOR_QUEUE_CAPACITY = "${executor.queue.capacity}";
    public static final String APP_PROPERTIES_EXECUTOR_KEEP_ALIVE_SECONDS = "${executor.keep.alive.seconds}";
    public static final String APP_PROPERTIES_EXECUTOR_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = "${executor.wait.for.tasks.to.complete.on.shutdown}";
    public static final String APP_PROPERTIES_EXECUTOR_AWAIT_TERMINATION_SECONDS = "${executor.await.termination.seconds}";

    public static final String INFO_LOG_SET_PREVIOUS_VALUES_METRICS_CAUSE_CONNECTION_WITH_FORMATTED_STRING = "Set previous values, cause connection error: %s";
    public static final String INFO_LOG_SET_PREVIOUS_VALUES_METRICS_CAUSE_EXECUTION_WITH_FORMATTED_STRING = "Set previous values, cause execution error: %s";

    public static final String WARN_LOG_COULD_NOT_CLOSE_CONNECTION = "Could not close connection";

    public static final String ERROR_LOG_DB_CONNECTION_WITH_FORMATTED_STRING = "Can not create DB connection: connection = %s, connectionID = %s";
    public static final String ERROR_LOG_WHILE_PROCEEDING_GETTING_METRICS = "Was error while proceeding getting metrics";
    public static final String ERROR_LOG_WHILE_READING_CONFIG_FILE = "Error reading config file";
    public static final String ERROR_LOG_WHILE_REQUEST_EXECUTION = "Request execution error: ";
    public static final String ERROR_LOG_WHILE_CAN_NOT_CLOSE_STATEMENT = "Can not close Statement";
    public static final String ERROR_LOG_WHILE_CAN_NOT_CLOSE_RESULTSET = "Can not close ResultSet";

    public static final String DEBUG_LOG_SQL_QUERY_EXECUTE_WITH_FORMATTED_STRING = "Operation execute: %s";
    public static final String DEBUG_LOG_SQL_QUERY_DONE_WITH_FORMATTED_STRING = "Operation done: %s";
    public static final String DEBUG_LOG_START_GAUGE_OBSERVER = "Starting GAUGE observer";
    public static final String DEBUG_LOG_STOP_GAUGE_OBSERVER = "Stopping GAUGE observer";
    public static final String DEBUG_LOG_START_COUNTER_OBSERVER = "Starting COUNTER observer";
    public static final String DEBUG_LOG_STOP_COUNTER_OBSERVER = "Stopping COUNTER observer";
    public static final String DEBUG_LOG_START_CLEAN_BLOCKED_QUERIES = "Starting CLEAN blocked queries";
    public static final String DEBUG_LOG_STOP_CLEAN_BLOCKED_QUERIES = "Stopping CLEAN blocked queries";
}
