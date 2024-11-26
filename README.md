# Qwolic-PrometheusSqlExporter ver.0.x

## Prometheus metrics Project for asynchronous requests to databases.

Is used simple yaml-config "application.yaml" for implementing database connections and metrics. Also metrics (counter, gauge) mapped to db-connections via tag "connectId", what makes using more one connection to databases (oracle, postgres).


Configuration file description:

```editorconfig

connections:

      #### custom name for connection
    - name:'sql-1'
      #### connection string ####
      url: 'jdbc:oracle:thin:@host:1521/database'
      #### set db user login ####  
      user: 'user'
      #### set db user password ####
      pass: 'password'
      #### map selector - named connection ####
      connectId: 'sql-1'
      #### set database type (oracle or postgres) ####
      driverName: 'oracle'
   
      #### custom name for connection ####
    - name: 'sql-2'
      #### connection string ####
      url: 'jdbc:postgresql://host:5432/database'
      #### set db user password ####
      user: 'user'
      #### set db user password ####
      pass: 'password'
      #### map selector - named connection ####
      connectId: 'sql-2'
      #### set database type (oracle or postgres) ####
      driverName: 'postgresql'

metrics:

      #### custom name for metric ####
    - name: testing_sql_request
      #### set metric description ####
      description: "<Testing gauge>"
      #### map selector - to connection ####
      connectId: 'sql-1'
      #### query string ####
      query:
        SELECT COUNT(*)
        FROM data
        WHERE status=1
      #### choose metric type (counter or gauge) ####
      metricType: gauge
      #### set time limit for execution in seconds ####
      timeout: 300
      #### set labels in format [key, value, ..., key, value] ####
      labels: [
        "env", "dev",
        "service", "database"
        ]

```



Prometheus metrics example:

```commandline

# HELP testing_sql_request <Testing gauge>
# TYPE testing_sql_request gauge
testing_sql_request{env="dev",service="database"} 5437.0

```