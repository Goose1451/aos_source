package advantage.online.store.config;

/**
 * Created by kubany on 10/11/2015.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.advantage.online.store"})
public class DataJpaConfiguration {
}
