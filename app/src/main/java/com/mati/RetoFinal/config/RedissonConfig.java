package com.mati.RetoFinal.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class RedissonConfig {

    @Value("${redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${redis.cluster.password:}")
    private String password;

    @Value("${redis.cluster.ssl-enabled:false}")
    private boolean sslEnabled;

    @Value("${redis.cluster.connect-timeout}")
    private int connectTimeout;

    @Value("${redis.cluster.timeout}")
    private int timeout;

    @Value("${redis.cluster.retry-attempts}")
    private int retryAttempts;

    @Value("${redis.cluster.retry-interval}")
    private int retryInterval;

    @Value("${redis.cluster.master-pool-size}")
    private int masterPoolSize;

    @Value("${redis.cluster.slave-pool-size}")
    private int slavePoolSize;

    @Value("${redis.cluster.master-min-idle}")
    private int masterMinIdle;

    @Value("${redis.cluster.slave-min-idle}")
    private int slaveMinIdle;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();

        String[] nodes = clusterNodes.split(",");
        String[] nodeAddresses = new String[nodes.length];

        for (int i = 0; i < nodes.length; i++) {
            String protocol = sslEnabled ? "rediss://" : "redis://";
            nodeAddresses[i] = protocol + nodes[i].trim();
        }

        config.useClusterServers()
                .addNodeAddress(nodeAddresses)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout)
                .setRetryAttempts(retryAttempts)
                .setRetryInterval(retryInterval)
                .setMasterConnectionPoolSize(masterPoolSize)
                .setSlaveConnectionPoolSize(slavePoolSize)
                .setMasterConnectionMinimumIdleSize(masterMinIdle)
                .setSlaveConnectionMinimumIdleSize(slaveMinIdle)
                .setScanInterval(5000)
                .setCheckSlotsCoverage(false);

        if (StringUtils.hasText(password)) {
            config.useClusterServers().setPassword(password);
        }

        return Redisson.create(config);
    }
}
