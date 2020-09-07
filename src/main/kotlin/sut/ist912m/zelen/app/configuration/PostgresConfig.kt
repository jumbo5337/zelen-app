package sut.ist912m.zelen.app.configuration

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@EnableTransactionManagement
@Configuration
class PostgresConfig(
        @Value("\${db.host}")
        private val host: String,
        @Value("\${db.name}")
        private val dbName: String,
        @Value("\${db.port}")
        private val port: String,
        @Value("\${db.username}")
        private val username: String,
        @Value("\${db.password}")
        private val password: String,
        @Value("\${db.version}")
        private val version: String
) {
        @Bean
        fun dataSource(): DataSource? {
                val dataSource = HikariDataSource()
                dataSource.username = username
                dataSource.password = password
                dataSource.jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName)
                return dataSource
        }

        @Bean(initMethod = "migrate")
        fun flyway(dataSource: DataSource?): Flyway? {
                val flyway = Flyway()
                flyway.dataSource = dataSource
                flyway.target = MigrationVersion.fromVersion(version)
                return flyway
        }

}