package com.example.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraCounterCreateTableTest extends CassandraCounterBaseTest {

    private static Logger LOGGER = LoggerFactory.getLogger(CassandraCounterCreateTableTest.class);

    @Test
    @DisplayName("Test to create a table in ASTRA")
    public void should_create_expected_table() {
        try (CqlSession cqlSession = buildCqlSession()) {
            LOGGER.info("Connection Established to Astra with Keyspace '{}'", cqlSession.getKeyspace().get());
            SimpleStatement stmtCreateTable = SchemaBuilder
                    .createTable(TABLE_AVG_TABLE_NAME)
                    .ifNotExists()
                    .withPartitionKey(TABLE_AVG_FIELD_PKEY, DataTypes.UUID)
                    .withColumn(TABLE_AVG_FIELD_COUNTER, DataTypes.COUNTER)
                    .withColumn(TABLE_AVG_FIELD_TOTAL, DataTypes.COUNTER)
                    .build();

            cqlSession.execute(stmtCreateTable);
            LOGGER.info("Table '{}' has been created (if needed).", TABLE_AVG_TABLE_NAME);
        }
    }

}


