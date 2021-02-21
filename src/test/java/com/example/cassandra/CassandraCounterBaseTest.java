package com.example.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Paths;

@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/application.properties")
public abstract class CassandraCounterBaseTest {

    @Value("${spring.data.cassandra.username}")
    protected String username;

    @Value("${spring.data.cassandra.password}")
    protected String password;

    @Value("${spring.data.cassandra.keyspace-name}")
    protected String keyspace;

    @Value("${datastax.astra.secure-connect-bundle}")
    protected String cloudSecureBundle;

    public static String TABLE_AVG_TABLE_NAME = "average_rating_by_movie";
    public static String TABLE_AVG_FIELD_PKEY = "movie_id";
    public static String TABLE_AVG_FIELD_COUNTER = "rating_counter";
    public static String TABLE_AVG_FIELD_TOTAL = "rating_total";

    protected CqlSession buildCqlSession() {
        return CqlSession.builder()
                .withCloudSecureConnectBundle(Paths.get(cloudSecureBundle))
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace)
                .build();
    }
}


