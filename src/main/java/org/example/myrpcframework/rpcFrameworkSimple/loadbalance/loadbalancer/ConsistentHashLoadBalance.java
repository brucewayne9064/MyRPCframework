package org.example.myrpcframework.rpcFrameworkSimple.loadbalance.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import org.example.myrpcframework.rpcFrameworkSimple.loadbalance.AbstractLoadBalance;
import org.example.myrpcframework.rpcFrameworkSimple.remoting.dto.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();


    //负载均衡器的作用是在服务提供者列表（serviceUrlList）中选择一个合适的服务提供者来处理传入的 RpcRequest 请求。
    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest rpcRequest) {
        int identifyHashCode = System.identityHashCode(serviceUrlList);
        String rpcServiceName = rpcRequest.getRpcServiceName();  //获得服务器的名称
        ConsistentHashSelector selector = selectors.get(rpcServiceName);  //根据名称在map里面获得selector
        if(selector == null || selector.identityHashCode != identifyHashCode){ //如果list发生变化，就需要重新进行构造哈希环上的服务结点
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceUrlList, 160, identifyHashCode));
            selector = selectors.get(rpcServiceName);
        }

        return selector.select(rpcServiceName + Arrays.stream(rpcRequest.getParameters()));  //在selector里面选择一个服务返回。
    }


    //ConsistentHashSelector构造哈希环上的服务节点，select构造哈希环上的请求结点，然后selectForKey查找请求结点对应的服务节点
    static class ConsistentHashSelector{
        private final TreeMap<Long, String> virtualInvokers;

        private final int identityHashCode;

        //一致性哈希选择器
        //https://www.xiaolincoding.com/os/8_network_system/hash.html
        //一致性哈希是一种分布式哈希算法，用于在多个服务器之间分配数据，以实现负载均衡和避免数据重新分布
        //List<String> invokers：一个包含所有可用的调用者（服务器或服务提供者）的列表 ---》哈希环上的真实节点数
        //int replicaNumber： 每个真实结点对应的虚拟节点的数量
        ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode){
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for(String invoker : invokers){
                for(int i = 0; i < replicaNumber / 4; i++){
                    byte[] digest = md5(invoker + i); //每一个invoker replicaNumber / 4 * 4个虚拟副本
                    for(int h = 0; h < 4; h++){
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }

        }

        //生成一个字符串的MD5哈希值,产生一个128位（16字节）的哈希值
        static byte[] md5(String key){
            MessageDigest md;
            try{
                md = MessageDigest.getInstance("MD5");  //获取MD5摘要算法的实例，摘要就是生成的hash值的意思
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);  //将输入的字符串key转换为字节数组，使用UTF-8编码
                md.update(bytes);  //传入字节数组bytes，以更新摘要状态
            }catch(NoSuchAlgorithmException e){
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return md.digest(); //完成哈希计算过程
        }

        //从一个MD5哈希值（通常是16字节长）中提取两个相邻的4字节（32位）部分，并将它们合并成一个8字节（64位）的 long 值。
        //byte[] digest是md5哈希的字节数组结果 128位，16字节
        //idx表示要提取的两个四字节部分的起始索引，索引 0 将提取 digest 的第一个和第二个四个字节，索引 1 将提取第三个和第四个四个字节，依此类推。
        //这种技术有时用于需要将MD5哈希用作64位键（例如在某些数据库或哈希表中）的场景。
        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public String select(String rpcServicekey){
            byte[] digest = md5(rpcServicekey);
            return selectForKey(hash(digest, 0));//构造request结点，传入环中
        }


        //用于根据给定的哈希码（hashCode）在一致性哈希环中选择一个调用者（服务器或服务提供者）
        public String selectForKey(long hashCode){
            //调用 TreeMap 的 tailMap 方法，传入 hashCode 和一个布尔值 true
            //tailMap 方法返回一个 TreeMap 的视图，包含 hashCode 及之后的所有键值对,true 参数表示 hashCode 也被包含在返回的视图中
            //调用返回的 TreeMap 的 firstEntry 方法，获取视图中的第一个键值对，即 hashCode 之后的第一个虚拟节点，如果hashcode本身是一个节点那么返回该虚拟节点
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            //如果entry是null，说明已经在环的末端了，后面没有结点了，那么返回第一个节点
            if(entry == null){
                entry =  virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }
}
