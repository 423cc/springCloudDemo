package cloud.simple.service.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
//mybaits dao 搜索路径
//@MapperScan("cloud.simple.service.dao")
public class MybatisDataSource {
	@Autowired
	private DataSourceProperties dataSourceProperties;
	//mybaits mapper xml搜索路径
	private final static String mapperLocations="classpath:cloud/simple/service/dao/*.xml";

	private org.apache.tomcat.jdbc.pool.DataSource dataSource;

	@RefreshScope
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		DataSourceProperties config = dataSourceProperties;
		this.dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

		this.dataSource.setDriverClassName(config.getDriverClassName());
		this.dataSource.setUrl(config.getUrl());
		if (config.getUsername() != null) {
			this.dataSource.setUsername(config.getUsername());
		}
		if (config.getPassword() != null) {
			this.dataSource.setPassword(config.getPassword());
		}

        dataSource.setInitialSize(5); // 连接池启动时创建的初始化连接数量（默认值为0）
        dataSource.setMaxActive(20); // 连接池中可同时连接的最大的连接数
        dataSource.setMaxIdle(12); // 连接池中最大的空闲的连接数，超过的空闲连接将被释放，如果设置为负数表示不限
        dataSource.setMinIdle(0); // 连接池中最小的空闲的连接数，低于这个数量会被创建新的连接
        dataSource.setMaxWait(60000); // 最大等待时间，当没有可用连接时，连接池等待连接释放的最大时间，超过该时间限制会抛出异常，如果设置-1表示无限等待
        dataSource.setRemoveAbandonedTimeout(180); // 超过时间限制，回收没有用(废弃)的连接
        dataSource.setRemoveAbandoned(true); // 超过removeAbandonedTimeout时间后，是否进 行没用连接（废弃）的回收
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30); // 检查无效连接的时间间隔 设为30分钟
        return dataSource;
	}

	@PreDestroy
	public void close() {
		if (this.dataSource != null) {
			this.dataSource.close();
		}
	}
//
//	@Bean
//	@RefreshScope
//	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
//		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//		sqlSessionFactoryBean.setDataSource(dataSource());
//		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
//		 return sqlSessionFactoryBean.getObject();
//	}

//	@Bean
//	@RefreshScope
//	public PlatformTransactionManager transactionManager() {
//		return new DataSourceTransactionManager(dataSource());
//	}
}
