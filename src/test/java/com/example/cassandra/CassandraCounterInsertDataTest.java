package com.example.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CassandraCounterInsertDataTest extends CassandraCounterBaseTest {

    private static Logger LOGGER = LoggerFactory.getLogger(CassandraCounterInsertDataTest.class);

    @Test
    @DisplayName("Test to insert data into ASTRA counter table")
    public void should_insert_rows() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i=0; i<3; i++) {
            executorService.submit(() -> submitCassandraJob());
        }

        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        LOGGER.info("Executor service shutdown gracefully.");
    }

/*
    private void submitCassandraUpdate(CqlSession cqlSession, PreparedStatement ps) {
        cqlSession.execute(ps.bind(1L, 10L, UUID.fromString("404318db-d442-44dc-b1d0-c15c2f96f3b7")));
        cqlSession.execute(ps.bind(1L, 10L, UUID.fromString("404318db-d442-44dc-b1d0-c15c2f96f3b7")));
        cqlSession.execute(ps.bind(1L, 10L, UUID.fromString("404318db-d442-44dc-b1d0-c15c2f96f3b7")));
        cqlSession.execute(ps.bind(1L, 10L, UUID.fromString("404318db-d442-44dc-b1d0-c15c2f96f3b7")));
        cqlSession.execute(ps.bind(1L, 10L, UUID.fromString("404318db-d442-44dc-b1d0-c15c2f96f3b7")));
    }
*/

    private void submitCassandraJob() {
        try (CqlSession cqlSession = buildCqlSession()) {
            LOGGER.info("Connection Established to Astra with Keyspace '{}'", cqlSession.getKeyspace().get());

            Update update = QueryBuilder
                    .update(TABLE_AVG_TABLE_NAME)
                    .increment(TABLE_AVG_FIELD_COUNTER, QueryBuilder.bindMarker())
                    .increment(TABLE_AVG_FIELD_TOTAL, QueryBuilder.bindMarker())
                    .whereColumn(TABLE_AVG_FIELD_PKEY).isEqualTo(QueryBuilder.bindMarker());
            PreparedStatement ps = cqlSession.prepare(update.build());

            for (int i=1; i<=100; i++) {
                LOGGER.info(Thread.currentThread().getName() + " insert " + i);
                cqlSession.execute(ps.bind(1L, 10L, UUID.fromString("404318db-d442-44dc-b1d0-c15c2f96f3b7")));
            }

            LOGGER.info("Table '{}' has been updated.", TABLE_AVG_TABLE_NAME);
        }
    }
}


