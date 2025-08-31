package org.balanceeat.domain.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Bean
    fun dataSource(dataSourceProperties: DataSourceProperties): DataSource {
        val dataSource = dataSourceProperties.initializeDataSourceBuilder().build()
        return LazyConnectionDataSourceProxy(dataSource)
    }
}