package cloud.simple.conf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZooKeeperConfig {

	@Value("${service.name}")
	String serviceName;
	
	@Value("${zookeeper.server.list}")
	String serverList;
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	
	@PostConstruct
	public void init(){
		executor.execute(new Runnable() {			
			@Override
			public void run() {				
				registService();				
				try {
					Thread.sleep(1000*60*60*24*360*10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// 注册服务
	public ZkClient registService() {
        //这一步是将服务的实例注册进zookeeper，这样才能实现负载均衡。
        // 让客户端可以根据服务实例列表选择服务来执行。当然这里只需要注册服务所在服务器的IP即可，
        // 因为客户端只要知道IP，也就知道访问那个IP下的该服务。
		String servicePath = "/"+serviceName ;// 根节点路径
		ZkClient zkClient = new ZkClient(serverList);
		boolean rootExists = zkClient.exists(servicePath);
		if (!rootExists) {
			zkClient.createPersistent(servicePath);
		}
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String ip = addr.getHostAddress().toString();
		String serviceInstance = System.nanoTime() +"-"+ ip;
		// 注册当前服务
        //要注意这里使用zkClient.createEphemeral建立临时节点，
        // 如果这台服务器宕机，这个临时节点是会被清除的，
        // 这样客户端在访问时就不会再选择该服务器上的服务。
		zkClient.createEphemeral(servicePath + "/" + serviceInstance);
		System.out.println("提供的服务为：" + servicePath + "/" + serviceInstance);
		return zkClient;
	}
	
	
	
}
