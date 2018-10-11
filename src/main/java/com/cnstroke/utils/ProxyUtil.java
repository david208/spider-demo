package com.cnstroke.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyUtil implements Callable<String> {

	private static Logger logger = LoggerFactory.getLogger(ProxyUtil.class);

	private static int limit = 500;

	private String baseUrl;

	private String proxyUrl;

	public ProxyUtil(String proxyUrl, String baseUrl) {
		this.proxyUrl = proxyUrl;
		this.baseUrl = baseUrl;
	}

	public static String getProxy() {
		String firstUrl = "http://tpv.daxiangdaili.com/ip/?tid=557209132216618&&num=1000&port=&check_country_group%5B0%5D=1&check_http_type%5B0%5D=1&check_anonymous%5B0%5D=2&check_anonymous%5B1%5D=3&check_elapsed=10&check_upcount=500&result_sort_field=1&result_format=txt";
		String ips = getContentByUrl(firstUrl);
		if (ips.contains("ERROR"))
			ips = "";
		String secondUrl = "http://api.goubanjia.com/api/get.shtml?order=aa7c7137e4ce61924fe6ff89ec8f3d7d&num=500&carrier=0&protocol=0&an1=1&an2=2&an3=3&sp1=1&sp2=2&sp3=3&sort=1&system=1&distinct=0&rettype=1&seprator=%0D%0A";
		if (ips == null || ips.equals("")) {
			ips = getContentByUrl(secondUrl);
		} else {
			ips = ips + "\r\n" + getContentByUrl(secondUrl);
		}
		return ips;
	}

	public static String getContentByUrl(String url) {
		try {
			Document doc = Jsoup.connect(url).timeout(30000)
					.header("User-Agent",
							"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
					.get();
			String text = doc.body().text();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		// getValidProxy();
	}

	public static ExecutorService executorService;

	public static List filterValid(String proxys, String baseUrl) {
		String[] proxyArray = proxys.split(" ");
//		proxyArray = ArrayUtils.subarray(proxyArray, 0, limit);
		List<String> list = new ArrayList();
		List<ProxyUtil> task = new ArrayList();
		for (String proxy : proxyArray) {
			ProxyUtil ProxyUtil = new ProxyUtil(proxy, baseUrl);
			task.add(ProxyUtil);

		}
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 5);
		try {
			List<Future<String>> futures = executorService.invokeAll(task);
			for (Future future : futures) {
				Object url = future.get();
				if (null != url)
					list.add((String) url);
			}
			executorService.shutdown();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("获取代理失败",e);
		}
		logger.info("proxys:" + list);
		return list;

	}

	public static List<String> getValidProxy(String baseUrl) {
		return filterValid(getProxy(), baseUrl);

	}

	@Override
	public String call() throws Exception {
		try {
			String[] info = proxyUrl.split(":");
			HttpHost host = new HttpHost(info[0], Integer.valueOf(info[1]), "http");
			CloseableHttpClient httpclient = HttpClients.createDefault();
			RequestConfig config = RequestConfig.custom().setProxy(host).setConnectTimeout(2000).setSocketTimeout(2000)
					.setConnectionRequestTimeout(1000).build();
			// CloseableHttpClient httpclient =
			// HttpClients.custom().setConnectionTimeToLive(2,
			// TimeUnit.SECONDS).setProxy(host).build();
			HttpGet get = new HttpGet(baseUrl);
			get.setConfig(config);
			httpclient.execute(get);
			return proxyUrl;
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

}
