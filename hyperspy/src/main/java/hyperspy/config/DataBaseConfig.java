package hyperspy.config;

import hyperspy.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "hyperspy.repository")
public class DataBaseConfig {

    @Bean
    @Primary
    DataSource dataSource(@Value("${datasource.url}") String dbUrl,
                          @Value("${datasource.driver-class-name}") String dbDriverClass,
                          @Value("${datasource.username}") String dbUsername,
                          @Value("${datasource.password}") String dbPassword){
        return DataSourceBuilder.create()
                .url(dbUrl)
                .driverClassName(dbDriverClass)
                .username(dbUsername)
                .password(dbPassword)
                .build();
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                @Value("${hibernate.default_schema}") String hbnDefaultSchema,
                                                                @Value("${hibernate.dialect}") String hbnDialect,
                                                                @Value("${hibernate.format_sql}") Boolean hbnFormalSql,
                                                                @Value("${hibernate.show_sql}") Boolean hbnShowSql,
                                                                @Value("${hibernate.hbm2ddl-auto}") String hbnDdlAuto,
                                                                @Value("${hibernate.connection.encoding}") String hbnConnectionEncoding,
                                                                @Value("${hibernate.connection.useUnicode}") Boolean hbnConnectionUseUnicode) {
        final Properties hibernateProps = new Properties();
        hibernateProps.put("hibernate.default_schema", hbnDefaultSchema);
        hibernateProps.put("hibernate.dialect", hbnDialect);
        hibernateProps.put("hibernate.format_sql", hbnFormalSql);
        hibernateProps.put("hibernate.show_sql", hbnShowSql);
        hibernateProps.put("hibernate.hbm2ddl.auto", hbnDdlAuto);
        hibernateProps.put("hibernate.connection.CharSet", hbnConnectionEncoding);
        hibernateProps.put("hibernate.connection.characterEncoding", hbnConnectionEncoding);
        hibernateProps.put("hibernate.connection.useUnicode", hbnConnectionUseUnicode);

        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(Application.class.getPackage().getName().toString());
        entityManagerFactoryBean.setJpaProperties(hibernateProps);
        return entityManagerFactoryBean;
    }

    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
