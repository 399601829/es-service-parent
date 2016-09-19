package com.es.service.common.client;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.service.common.conf.Conf;

/**
 * 
 * es客户端
 * 
 * @author hailin0@yeah.net
 * @createDate 2016年8月16日
 * 
 */
public class ESClient {

    /**
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ESClient.class);

    private static volatile ESClient instance;

    private Client client;

    private ESClient() {
        client = createESClient();
    }

    /**
     * 构建单例
     * 
     * @return
     */
    public static ESClient getInstance() {
        if (instance == null) {
            synchronized (ESClient.class) {
                if (instance == null) {
                    instance = new ESClient();
                }
            }
        }
        return instance;
    }

    /**
     * 返回es客户端
     * 
     * @return
     */
    public static Client getClient() {
        return getInstance().client;
    }

    /**
     * close客户端
     * 
     * @return
     */
    public static void closeClient() {
        if (instance != null && instance.client != null) {
            try {
                instance.client.close();
            } catch (Exception e) {
                instance.client = null;
            }
        }
    }

    /**
     * 初始化es客户端
     * 
     * @return
     */
    private Client createESClient() {
        Client client = null;
        String esname = Conf.getString("es.cluster.name");
        String esip = Conf.getString("es.cluster.ip");
        try {
            if (StringUtils.isBlank(esip) && StringUtils.isNotBlank(esname)) {
                ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
                settings.put("cluster.name", esname);
                settings.put("node.name", InetAddress.getLocalHost().getHostName() + "-eslocalnode");
                settings.put("node.master", false);
                settings.put("node.data", false);
                settings.put("node.max_local_storage_nodes", 1);
                settings.put("http.enabled", false);
                settings.put("discovery.zen.ping.multicast.enabled", false);
                Node node = nodeBuilder().clusterName(esname).settings(settings).client(true)
                        .data(false).node();
                client = node.client();
            } else {
                // 基本配置
                Settings settings = ImmutableSettings
                        .settingsBuilder()
                        // 节点自动发现
                        .put("client.transport.sniff", Conf.getBoolean("client.transport.sniff"))
                        // 集群名
                        .put("cluster.name", esname)
                        // 事务刷新间隔
                        .put("index.translog.flush_threshold_ops",
                                Conf.getString("index.translog.flush_threshold_ops"))
                        // 索引刷新间隔
                        .put("index.refresh_interval", Conf.getString("index.refresh_interval"))
                        .build();
                TransportClient tc = new TransportClient(settings);
                String[] ipAndPorts = esip.split(",");
                String[] ipPort;
                // ipAndPort-192.0.0.1:9300
                for (String ipAndPort : ipAndPorts) {
                    ipPort = ipAndPort.split(":");
                    tc.addTransportAddress(new InetSocketTransportAddress(ipPort[0], Integer
                            .parseInt(ipPort[1])));
                }
                client = tc;
            }
        } catch (Exception e) {
            LOGGER.error("创建ES集群失败,esip:{},esname:{}", esip, esname, e);
        }

        LOGGER.info("创建ES集群成功 ! esip:{},esname:{}", esip, esname);
        return client;
    }
}
