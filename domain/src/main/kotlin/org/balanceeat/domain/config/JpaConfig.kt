package org.balanceeat.domain.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = ["org.balanceeat.domain"])
@EnableJpaRepositories(basePackages = ["org.balanceeat.domain"])
class JpaConfig
