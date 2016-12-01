package cloud.simple.provider;

import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import cloud.simple.conf.ZooKeeperConfig;
import cloud.simple.interfaces.UserService;

@Component
public class UserServiceProvider {


    //有了服务列表，客户端在调用服务的时候就可以采用负载均衡的方式了，在这里使用最简单的随机方式：
    public UserService.Client getBalanceUserService() {
        Map<String, UserService.Client> serviceMap = ZooKeeperConfig.serviceMap;
        //以负载均衡的方式获取服务实例
        for (Map.Entry<String, UserService.Client> entry : serviceMap.entrySet()) {
            System.out.println("可供选择服务:" + entry.getKey());
        }
        int rand = new Random().nextInt(serviceMap.size());
        String[] mkeys = serviceMap.keySet().toArray(new String[serviceMap.size()]);
        return serviceMap.get(mkeys[rand]);
    }


}
