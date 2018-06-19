package com.rocky.real.estate.monitor.crawler;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by rocky on 18/6/18.
 */
@Component
public class ProxyManager implements ProxyProvider {

    private Map<Proxy, Long> proxyMap = Maps.newConcurrentMap();
    private Map<Proxy, Integer> candidateProxyMap = Maps.newConcurrentMap();
    private ProxyFetcher proxyFetcher = new ProxyFetcher();

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {

    }

    @Override
    public Proxy getProxy(Task task) {
        Proxy proxy = optProxy();
        if (optProxy() == null) {
            if (candidateProxyMap.size() == 0) {
                fetchCandidate();
            }
            candidateSelect(1);
            proxy = optProxy();
        }
        return proxy;
    }

    private void candidateSelect(int places) {
        int current = 0;
        for (Proxy proxy : candidateProxyMap.keySet()) {
            if (current == places) {
                //名额已足够
                return;
            }
            Long mills = testProxy(proxy);
            candidateProxyMap.remove(proxy);
            if (mills != null) {
                //有效的proxy
                proxyMap.put(proxy, mills);
                current++;

            }
        }
    }

    private Proxy optProxy(){
        if (proxyMap.size() != 0) {
            Long minMills = Long.MAX_VALUE;
            Proxy optProxy = null;
            for (Proxy proxy : proxyMap.keySet()) {
                Long thisMills = proxyMap.get(proxy);
                if (thisMills < minMills) {
                    optProxy = proxy;
                    minMills = thisMills;
                }
            }
            return optProxy;
        }else {
            return null;
        }

    }

    private Long testProxy(Proxy proxy) {
        HttpHost httpHost = new HttpHost(proxy.getHost(), proxy.getPort(), "http");
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setProxy(httpHost)
                .build();
        HttpGet httpGet = new HttpGet("https://cd.lianjia.com/ershoufang/");
        try {
            long totalMills = 0;
            Stopwatch stopwatch = Stopwatch.createUnstarted();
            for (int i = 0; i < 5; i++) {
                CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
                stopwatch.reset().start();
                CloseableHttpResponse httpResp = httpclient.execute(httpGet);
                if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    totalMills += stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
                }else {
                    return null;
                }
            }
            return totalMills / 5;

        }catch (IOException e) {
            return null;
        }
    }

    private void fetchCandidate(){
        List<Proxy> proxies = proxyFetcher.fetch();
        for (Proxy p : proxies) {
            candidateProxyMap.put(p, 1);
        }
    }

    public static void main(String[] args) {
        Long mills = new ProxyManager().testProxy(new Proxy("218.39.192.206",3128));
        Proxy proxy = new ProxyManager().getProxy(null);
        System.out.println();
    }
}
