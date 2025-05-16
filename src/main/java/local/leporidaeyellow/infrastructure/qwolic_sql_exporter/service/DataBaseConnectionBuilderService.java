package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.data.ConnectionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Slf4j
@Service
public class DataBaseConnectionBuilderService {
    public Connection createDataBaseConnection(ConnectionEntity connectionEntity) {
        try {
            Properties props = new Properties();
            props.put("user", connectionEntity.getUser());
            props.put("password", connectionEntity.getPass());

            //register driver class
            Class.forName(getDbDriver(connectionEntity.getDriverName()));
            //establish connection
            Connection connection = DriverManager.getConnection(connectionEntity.getUrl(), props);
            connection.setReadOnly(true);
            return connection;
        } catch (Throwable ex) {
            String errorFormattedString = String.format(Constants.ERROR_LOG_DB_CONNECTION_WITH_FORMATTED_STRING, connectionEntity.getDriverName(), connectionEntity.getConnectId());
            log.error(errorFormattedString);
        }
        return null;
    }

    private String getDbDriver(String nameDb) {
        switch (nameDb) {
            case Constants.ORACLE_DB_DRIVER_NAME: return Constants.ORACLE_DB_DRIVER;
            case Constants.POSTGRES_DB_DRIVER_NAME: return Constants.POSTGRES_DB_DRIVER;
            case Constants.CLICKHOUSE_DB_DRIVER_NAME: return Constants.CLICKHOUSE_DB_DRIVER;
            default: return "ERROR name of driver";
        }
    }
}
