package com.cnstroke.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cnstroke.utils.HbaseUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/*
 * @author sm
 */
@Service
public class HbasePipeline implements Pipeline {

	private Logger logger = LoggerFactory.getLogger(getClass());


	@Override
	public void process(ResultItems resultItems, Task task) {
		try {
			List<String> s = resultItems.get("info");
			String colume = resultItems.get("type");
			String id = resultItems.get("id");
			HbaseUtils.addDate(id, colume, s);
		} catch (Exception e) {
			logger.error("保存hbase失败",e);
		}
	}

}
