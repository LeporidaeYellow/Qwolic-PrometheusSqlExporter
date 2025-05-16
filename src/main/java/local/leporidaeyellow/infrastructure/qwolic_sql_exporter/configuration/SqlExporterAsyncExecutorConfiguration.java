package local.leporidaeyellow.infrastructure.qwolic_sql_exporter.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class SqlExporterAsyncExecutorConfiguration {

    @Value(Constants.APP_PROPERTIES_EXECUTOR_CORE_POOL_SIZE)
    int corePoolSize;

    @Value(Constants.APP_PROPERTIES_EXECUTOR_MAX_POOL_SIZE)
    int maxPoolSize;

    @Value(Constants.APP_PROPERTIES_EXECUTOR_QUEUE_CAPACITY)
    int queueCapacity;

    @Value(Constants.APP_PROPERTIES_EXECUTOR_KEEP_ALIVE_SECONDS)
    int keepAliveSeconds;

    @Value(Constants.APP_PROPERTIES_EXECUTOR_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN)
    boolean waitForTasksToCompleteOnShutdown;

    @Value(Constants.APP_PROPERTIES_EXECUTOR_AWAIT_TERMINATION_SECONDS)
    int awaitTerminationSeconds;

    @Bean(name = "sqlExporterAsyncExecutor")
    public ThreadPoolTaskExecutor sqlExporterAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.initialize();
        return executor;
    }
}