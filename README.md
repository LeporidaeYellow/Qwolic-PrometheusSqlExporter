# Qwolic-PrometheusSqlExporter ver.0.x

## Prometheus metrics Project for asynchronous requests to databases.

Is used simple yaml-config "application.yaml" for implementing database connections and metrics. Also metrics (counter, gauge) mapped to db-connections via tag "connectId", what makes using more one connection to databases (oracle, postgres).

```dtd

connections:

  - name: 'sql-1'
    url: 'jdbc:oracle:thin:@host:1521/database'
    user: 'user'
    pass: 'password'
    connectId: 'sql-1'
    driverName: 'oracle'

  - name: 'sql-2'
    url: 'jdbc:postgresql://host:5432/database'
    user: 'user'
    pass: 'password'
    connectId: 'sql-2'
    driverName: 'postgresql'

metrics:

  - name: testing_sql_request
    description: "<Testing gauge>"
    connectId: 'sql-1'
    query:
      SELECT COUNT(*)
      FROM data 
      WHERE status=1
    metricType: gauge
    timeout: 300
    labels: [
      "env", "dev",
      "service", "database"
    ]


```