package com.offcn.service.impl;

import com.offcn.po.User;
import com.offcn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    RestTemplate restTemplate;
    //Eureka客户端
    @Autowired
    DiscoveryClient discoveryClient;

    /***
     * 通过客户端负载均衡器获取生产者服务器基础地址
     * @return
     */
    public String getServerUrl() {
        //通过客户端调用器查找指定服务
        List<ServiceInstance> instList = discoveryClient.getInstances("USERPROVIDER");
        //获取第一个服务器
        ServiceInstance inst = instList.get(0);
        //获取服务提供者服务器ip、端口号
        String ip = inst.getHost();
        int port = inst.getPort();
        //拼接调用地址
        String url="http://"+ip+":"+port+ "/user";
        return url;
    }



    @Override
    public Map getUserMap() {
        Map map = restTemplate.getForObject(getServerUrl()+"/getall", Map.class);
        return map;
    }


    @Override
    public void createUser(User user) {
        restTemplate.postForObject(getServerUrl()+"/save", user,String.class);
    }

    @Override
    public User getUser(Long id) {
        return restTemplate.getForObject(getServerUrl()+"/get/"+id, User.class);
    }

    @Override
    public void updateUser(Long id, User user) {
        restTemplate.put(getServerUrl()+"/update/"+id, user);
    }

    @Override
    public void deleteUser(Long id) {
        restTemplate.delete(getServerUrl()+"/delete/"+id);
    }
}
