package org.example.pdnight.global.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Properties;

public class KafkaAdminClientFactory {

    public static AdminClient create() {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:10000"); // EXTERNAL 포트
        return AdminClient.create(config);
    }

}
