package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.model.config;

public class ConnectionEntity {
    private String name;
    private String url;
    private String user;
    private String pass;
    private String driverName;
    private String connectId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getConnectId() {
        return connectId;
    }

    public void setConnectId(String connectId) {
        this.connectId = connectId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public String toString() {
        return "ConnectionEntity{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", driverName='" + driverName + '\'' +
                ", connectId='" + connectId + '\'' +
                '}';
    }
}
