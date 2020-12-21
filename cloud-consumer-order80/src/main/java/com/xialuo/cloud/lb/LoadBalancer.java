package com.xialuo.cloud.lb;

import java.util.List;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @auther zzyy
 * @create 2020-02-19 20:31
 */
public interface LoadBalancer
{
    ServiceInstance instances(List<ServiceInstance> serviceInstances);
}
