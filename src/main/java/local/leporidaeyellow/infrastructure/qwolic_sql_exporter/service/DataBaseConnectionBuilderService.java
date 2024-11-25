package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config.ConnectionEntity;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DataBaseConnectionBuilderService {
    static private final String ORACLE_DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static private final String POSTGRES_DB_DRIVER = "org.postgresql.ds.PGSimpleDataSource";

    public Connection createOracleConnection(ConnectionEntity connectionEntity) throws ClassNotFoundException, SQLException {
        //register driver class
        Class.forName(getDbDriver(connectionEntity.getDriverName()));
        //establish connection
        Connection connection = DriverManager.getConnection(connectionEntity.getUrl(), connectionEntity.getUser(), connectionEntity.getPass());
        connection.setReadOnly(true);
        return connection;
    }

    private String getDbDriver(String nameDb) {
        switch (nameDb) {
            case "oracle": return ORACLE_DB_DRIVER;
            case "postgresql": return POSTGRES_DB_DRIVER;
            default: return "ERROR name of driver";
        }
    }
}
