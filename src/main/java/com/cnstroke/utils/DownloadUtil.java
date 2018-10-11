package com.cnstroke.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.proxy.Proxy;

public class DownloadUtil {

	private static Logger logger = LoggerFactory.getLogger(DownloadUtil.class);

	private static Random random = new Random();

	public static String[] agentList = new String[] {
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
			"User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
			"User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11",
			"Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; TencentTraveler 4.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; The World)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Avant Browser)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)" };

	public static void downLoad(String url, String localFileName, HttpHost proxy) throws Exception {
		// DefaultHttpClient httpClient = new DefaultHttpClient();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		OutputStream out = null;
		InputStream in = null;

		try {
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(600000).setConnectTimeout(20000)
					.setProxy(proxy).build();// 设置请求和传输超时

			httpGet.setConfig(requestConfig);
			httpGet.setHeader("User-Agent", agentList[random.nextInt(agentList.length)]);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			in = entity.getContent();

			long length = entity.getContentLength();
			if (length <= 0) {
				logger.warn("下载文件" + url + "不存在！");
				return;
			}

			File file = new File(localFileName);
			if (!file.exists()) {
				createNewFile(localFileName);
			}

			out = new FileOutputStream(file);
			byte[] buffer = new byte[4096];
			int readLength = 0;
			while ((readLength = in.read(buffer)) > 0) {
				byte[] bytes = new byte[readLength];
				System.arraycopy(buffer, 0, bytes, 0, readLength);
				out.write(bytes);
			}

			out.flush();

		} catch (Exception e) {
			logger.error("下载文件失败：" + url, e);
			throw e;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void download(String url, String localFileName) {
		try {
			System.out.println("---------开始下载---------");
			java.net.URI uri = java.net.URI.create(url);
			// 获取当前系统桌面扩展
			java.awt.Desktop dp = java.awt.Desktop.getDesktop();
			// 判断系统桌面是否支持要执行的功能
			if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
				// File file = new File("D:\\aa.txt");
				// dp.edit(file);// 编辑文件
				// dp.browse(uri);// 获取系统默认浏览器打开链接
				// Runtime.getRuntime().exec("rundll32
				// url.dll,FileProtocolHandler " +uri);

				FileOutputStream fos = new FileOutputStream(localFileName);
				Process process = Runtime.getRuntime().exec("explorer " + uri);
				InputStream is = process.getInputStream();
				int ch = 0;
				try {
					while ((ch = is.read()) != -1) {
						fos.write(ch);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					// 关闭输入流等（略）
					fos.close();
					is.close();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean CreateMultilayerFile(String dir) {
		try {
			File dirPath = new File(dir);
			if (!dirPath.exists()) {
				dirPath.mkdirs();
			}
		} catch (Exception e) {
			logger.error("创建多层目录操作出错: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 新建文件
	public static void createNewFile(String path) {
		try {
			logger.info("创建文件全名" + path);
			String[] lists = path.split("\\.");
			int lastLength = lists[0].lastIndexOf(File.separator);
			// 得到文件夹目录
			String dir = lists[0].substring(0, lastLength);
			// 得到文件名称
			String fileName = lists[0].substring(lastLength);
			// 得到路径e:\a\b之后,先创建文件夹
			if (CreateMultilayerFile(dir) == true) {
				File filePath = new File(path);
				if (!filePath.exists()) {
					filePath.createNewFile();
				}
			}
		} catch (Exception e) {
			logger.error("新建文件操作出错: " + path + "|" + e.getMessage());
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		download("https://www.baidu.com/home/news/data/newspage?nid=17087347364451407100&n_type=0&p_from=1&dtype=-1",
				"a.pdf");

	}
}
