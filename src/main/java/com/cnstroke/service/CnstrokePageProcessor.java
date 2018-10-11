package com.cnstroke.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.plaf.SliderUI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.cnstroke.utils.HbaseUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

@Service
public class CnstrokePageProcessor implements PageProcessor {

	private static final String BASE_URL = "http://pro.cnstroke.com/home/login";

	private static Logger logger = LoggerFactory.getLogger(CnstrokePageProcessor.class);

	private static List<String> ills = Arrays
			.asList(new String[] { "既往脑卒中", "脑卒中类型", "缺血性脑卒中发作次数", "缺血性脑卒中发作时间", "出血性脑卒中发作次数", "出血性脑卒中发作时间", "短暂性脑卒中发作",
					"高血压", "高血压详情", "血脂异常", "血脂异常详情", "血脂异常类型", "糖尿病", "糖尿病详情", "心脏病", "心脏病类型" });

	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setCycleRetryTimes(10).setRetryTimes(0).setSleepTime(3000).setTimeOut(5000)
			.addCookie("ASP.NET_SessionId", "0i3hln4vq5pzcfzr3vyrxnlr");

	@Override
	public void process(Page page) {
		try {
			int step = (int) (page.getRequest().getExtra("step") == null ? 0 : page.getRequest().getExtra("step"));
			Request request;
			switch (step) {
			case 0:
				request = new Request("http://pro.cnstroke.com/FollowUp/FollowUpInfo/_FollowUpInfoList");
				request.setMethod(HttpConstant.Method.POST);
				request.putExtra("step", 1);
				request.putExtra("page", 1);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("FUVersion_TString", "2013C");
				map.put("listType", 129);
				request.setRequestBody(HttpRequestBody.form(map, "utf-8"));
				page.addTargetRequest(request);
				page.setSkip(true);
				break;
			// 分页
			case 1:
				// File file = new File("a.txt");
				// FileOutputStream fileOutputStream = new
				// FileOutputStream(file);
				// fileOutputStream.write(page.getHtml().get().getBytes());
				String[] urls = StringUtils.substringsBetween(page.getHtml().toString(), "'档案详情','", "'");
				for (String url : urls) {
					Request request2 = new Request("http://pro.cnstroke.com" + url.replace("amp;", ""));
					request2.putExtra("step", 2);
					page.addTargetRequest(request2);
					// break;
				}
				int pageno = ((int) page.getRequest().getExtra("page")) + 1;
				if (pageno > 1) {
					page.setSkip(true);
					break;
				}
				Request request3 = new Request(
						"http://pro.cnstroke.com/FollowUp/FollowUpInfo/_FollowUpInfoList?pageIndex=" + pageno);
				map = new HashMap<String, Object>();
				map.put("FUVersion_TString", "2013C");
				map.put("listType", 129);
				request3.setRequestBody(HttpRequestBody.form(map, "utf-8"));
				request3.setMethod(HttpConstant.Method.POST);
				request3.putExtra("step", 1);
				request3.putExtra("page", pageno);
				page.addTargetRequest(request3);
				page.setSkip(true);
				break;
			/* logger.info(); */
			case 2:

				Selectable div = page.getHtml().$("#content");

				String id = StringUtils.substringBetween(div.get(), "（档案号：", "，");
				List<String> names = div.$(".active", "allText").all();
				List<String> values = div.$(".tdColor", "allText").all();
				for (int i = 0; i < names.size() - 1; i++) {
					logger.info(names.get(i) + ":" + values.get(i));

				}
				boolean finalStrokeInfro = false;
				for (String link : page.getHtml().$("#xxx").links().all()) {
					if (link.indexOf("NeckUltrasound") >= 0)
						continue;
					if (link.indexOf("FinalStrokeInfro") >= 0)
						finalStrokeInfro = true;
					Request request2 = new Request(link);
					request2.putExtra("step", 3);
					request2.putExtra("id", id);
					page.addTargetRequest(request2);
				}
				if (!finalStrokeInfro) {
					String[] temp = new String[22];
					Arrays.fill(temp, "");
					HbaseUtils.addDate(id, "FinalStrokeInfro", Arrays.asList(temp));
				}
				page.putField("info", values);
				page.putField("type", "base");
				page.putField("id", id);
				break;
			case 3:
				String url = page.getUrl().get();
				String type = StringUtils.substringBetween(url, "Stroke2012/", "/");

				Selectable table = page.getHtml().$("table");

				names = table.$(".active", "allText").all();
				if (type.equals("Pharmacy")) {
					values = table.$("td:not(.active)", "allText").all();
				} else
					values = table.$(".tdColor", "allText").all();
				if (!type.equals("StrokeReport")) {
					for (int i = 0; i < names.size(); i++) {
						logger.info(names.get(i) + ":" + values.get(i));

					}
				}

				if (type.equals("StrokeFollowup")) {
					List<String> v = new ArrayList<>();
					for (String ill : ills) {
						boolean marched = false;
						for (int i = 0; i < names.size(); i++) {
							if (ill.equals(names.get(i).trim().replace("：", ""))) {
								v.add(values.get(i));
								marched = true;
								break;
							}
						}
						if (!marched)
							v.add("");
					}
					values = v;
				}
				if (type.equals("PhysicalExam") && values.size() == 10) {
					values.add(4, "");
				}
				if (type.equals("StrokeReport")) {
					values.add(6, table.$(".ACStudy", "allText").all().get(0));
					if (values.size() == 22) {
						values.remove(13);
					}
				}
				if (type.equals("LifeStyle")) {
					if (values.get(0).indexOf("无") >= 0) {
						values.add(1, "");
						values.add(2, "");
					}
					if (values.get(3).indexOf("无") >= 0) {
						values.add(4, "");
						values.add(5, "");
					}
					if (values.get(6).indexOf("无") >= 0) {
						values.add(7, "");
					}
				}

				page.putField("info", values);
				page.putField("type", type);
				page.putField("id", page.getRequest().getExtra("id"));
				break;
			default:
				page.setSkip(true);
				break;
			}
		} catch (Exception e) {
			logger.error("爬虫异常", e);
			page.setSkip(true);
		}

	}

	/*
	 * @Override public Site getSite() { return
	 * site.setUserAgent(DownloadUtil.agentList[random.nextInt(DownloadUtil.
	 * agentList.length)]); }
	 */

	public static void main(String[] args) throws IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-batis.xml");
		CnstrokePageProcessor weipuPageProcessor = (CnstrokePageProcessor) context.getBean("cnstrokePageProcessor");
		HbasePipeline hbasePipeline = (HbasePipeline) context.getBean("hbasePipeline");
		Spider spider = Spider.create(weipuPageProcessor).addUrl(BASE_URL).addPipeline(hbasePipeline)
				// .thread(Runtime.getRuntime().availableProcessors() );
				.thread(Runtime.getRuntime().availableProcessors());
		// 启动爬虫
		// MyHttpClientDownloader httpClientDownloader = new
		// MyHttpClientDownloader();

		// Properties pro = new Properties();
		// InputStream in =
		// WanfPageProcessor.class.getClassLoader().getResourceAsStream("jdbc.properties");
		// pro.load(in);
		// String redis = (String) pro.get("redis.url");
		//
		// WeipuDownload.setBasePath((String) pro.get("baseUrl"));

		// RedisPriorityScheduler priorityScheduler = new
		// RedisPriorityScheduler(redis);
		PriorityScheduler priorityScheduler = new PriorityScheduler();
		// httpClientDownloader.setProxyProvider(new AutoProxyProvider(list,
		// BASE_URL));
		// spider.setDownloader(httpClientDownloader);
		spider.setScheduler(priorityScheduler);
		spider.run();
		System.exit(0);

	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

}
