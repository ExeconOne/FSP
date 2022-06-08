package pl.execon.fsp.mongo;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigurationPackage
@AutoConfigureBefore({JpaRepositoriesAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
class MongoFspConfiguration {
}
