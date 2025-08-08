package org.example.pdnight.global.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Properties;

// 카프카 AdminClient 설정 및 생성해 주는 클래스
public class KafkaAdminClientFactory {

    // AdminClient 설정에 따라 생성 해 주는 전역 팩토리 메서드
    public static AdminClient create() {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:10000"); // EXTERNAL 포트
        return AdminClient.create(config);
    }

}
