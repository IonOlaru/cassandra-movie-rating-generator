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
import java.util.concurrent.CountDownLatch;

public class CassandraCounterInsertDataUsingDriverAsyncTest extends CassandraCounterBaseTest {

    private static Logger LOGGER = LoggerFactory.getLogger(CassandraCounterInsertDataUsingDriverAsyncTest.class);
    private static int N_UPDATES = 1000;

    @Test
    @DisplayName("Test to insert data into ASTRA counter table")
    public void should_insert_rows() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(N_UPDATES);

        try (CqlSession cqlSession = buildCqlSession()) {
            Update update = QueryBuilder
                    .update(TABLE_AVG_TABLE_NAME)
                    .increment(TABLE_AVG_FIELD_COUNTER, QueryBuilder.bindMarker())
                    .increment(TABLE_AVG_FIELD_TOTAL, QueryBuilder.bindMarker())
                    .whereColumn(TABLE_AVG_FIELD_PKEY).isEqualTo(QueryBuilder.bindMarker());
            PreparedStatement ps = cqlSession.prepare(update.build());

            LOGGER.info("Connection Established to Astra with Keyspace '{}'", cqlSession.getKeyspace().get());

            for (int i=0; i<N_UPDATES; i++) {
                submitCassandraUpdate(cqlSession, ps, latch);
            }
        }

        latch.await();
        LOGGER.info("Table '{}' has been updated.", TABLE_AVG_TABLE_NAME);
    }

    private void submitCassandraUpdate(CqlSession cqlSession, PreparedStatement ps, CountDownLatch latch) {
        cqlSession
                .executeAsync(ps.bind(1L, 10L, UUID.fromString("404318db-d442-44dc-b1d0-c15c2f96f3b7")))
                .whenComplete((row, error) -> {
                    if (error == null) {
                        latch.countDown();
                    } else {
                        error.printStackTrace();
                    }
                });
    }

}


