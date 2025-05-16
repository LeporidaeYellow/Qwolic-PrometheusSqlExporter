package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.service;

import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration.Constants;
import local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.data.ConnectionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class ConnectionService {
    @Autowired
    ConfigService configService;

    @Autowired
    DataBaseConnectionBuilderService dataBaseConnectionBuilderService;

    private final Map<String, Object> map = new ConcurrentHashMap<>();

    public Connection popConnection(String connectId) throws SQLException, ClassNotFoundException {
        ConnectionEntity connection = getConnectionEntityByConnectIdFromConfig(connectId);
        if (!keyExist(connectId) || getValue(connectId) == null) {
            setConnection(connectId);
            return dataBaseConnectionBuilderService.createDataBaseConnection(connection);
        } else {
            Connection existConnection = getConnection(connectId);
            setConnection(connectId);
            return existConnection;
        }
    }

    public Connection getConnection(String connectId) {
        Connection connection = (Connection) map.get(connectId);
        map.remove(connectId);
        return connection;
    }

    public Boolean setConnection(String connectId) {
        ConnectionEntity connection = getConnectionEntityByConnectIdFromConfig(connectId);
        Connection conn = dataBaseConnectionBuilderService.createDataBaseConnection(connection);
        map.put(connection.getConnectId(), conn);
        return map.equals(conn);
    }

    public boolean keyExist(String connectId) {
        return map.containsKey(connectId);
    }

    public Object getValue(String connectId) {
        return map.get(connectId);
    }

    public ConnectionEntity getConnectionEntityByConnectIdFromConfig(String connectId) {
        AtomicReference<ConnectionEntity> connection = new AtomicReference<>();
        configService.getConnectionList()
                .forEach(entity -> {
                    if (entity.getConnectId().equals(connectId)) connection.set((ConnectionEntity) entity);
                });
        return connection.get();
    }

    public void releaseConnection(String connectId, Connection connection) {
        if (map.containsKey(connectId) && map.get(connectId) == connection) {
            map.replace(connectId, connection);
        } else {
            try {
                connection.close();
            } catch (SQLException ex) {
                log.warn(Constants.WARN_LOG_COULD_NOT_CLOSE_CONNECTION, ex);
            }
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setDataBaseConnectionBuilderService(DataBaseConnectionBuilderService dataBaseConnectionBuilderService) {
        this.dataBaseConnectionBuilderService = dataBaseConnectionBuilderService;
    }
}
