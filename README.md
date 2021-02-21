### Update C* counters

#### Update `application properties` file

- `datastax.astra.secure-connect-bundle` - location of Astra Secure Connection Bundle file 
- `spring.data.cassandra.keyspace-name` - keyspace name (using `ks1` in the example below)
- `spring.data.cassandra.username` - C* username
- `spring.data.cassandra.password` - C* password


#### build project
It's important to keep `-x test` to avoid running the tests during the build. Tests will run in a separate command.  
```
./gradlew clean build -x test -i
```

#### run tests
a) Run a "test" that will create the table with 2 counters
```
./gradlew test -i --tests com.example.cassandra.CassandraCounterCreateTableTest
```

b) Using Astra CQL Console connect to your database and check the new table
```
token@cqlsh> use ks1;

username@cqlsh:ks1> DESC TABLE average_rating_by_movie;

CREATE TABLE ks1.average_rating_by_movie (
    movie_id uuid PRIMARY KEY,
    rating_counter counter,
    rating_total counter
) WITH additional_write_policy = 'NONE'
    AND bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.UnifiedCompactionStrategy'}
    AND compression = {'enabled': 'false'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND nodesync = {'enabled': 'true', 'incremental': 'true'}
    AND read_repair = 'BLOCKING'
    AND speculative_retry = 'NONE';
```

c) Check that both counters are `0`
```

username@cqlsh:ks1> SELECT * FROM average_rating_by_movie ;

 movie_id                             | rating_counter | rating_total
--------------------------------------+----------------+--------------
 404318db-d442-44dc-b1d0-c15c2f96f3b7 |              0 |            0
```

d) Run a "test" that will use 3 threads each doing 100 updates to both counters
```
./gradlew test -i --tests com.example.cassandra.CassandraCounterInsertDataTest
```

e) Check the counters values again (expected values are `300` counts and a total value of `3000`)
```

username@cqlsh:ks1> SELECT * FROM average_rating_by_movie ;

 movie_id                             | rating_counter | rating_total
--------------------------------------+----------------+--------------
 404318db-d442-44dc-b1d0-c15c2f96f3b7 |            300 |         3000

(1 rows)
```