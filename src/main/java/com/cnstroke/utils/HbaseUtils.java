package com.cnstroke.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.gson.Gson;

public class HbaseUtils {

	static Configuration conf = null;
	static Connection conn = null;
	private static Gson gson = new Gson();

	static {
		try {
			conf = HBaseConfiguration.create();
			conf.set("hbase.zookeeper.property.clientPort", "2181");
			conf.set("hbase.zookeeper.quorum", "localhost");
			conn = ConnectionFactory.createConnection(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void addDate(String id, String colume, List<String> s) throws IOException {
		Put put = new Put(Bytes.toBytes(id));
		HTable table = (HTable) conn.getTable(TableName.valueOf("patient"));
		put.addColumn(Bytes.toBytes("info"), Bytes.toBytes(colume), Bytes.toBytes(gson.toJson(s)));
		table.put(put);

	}
	
	public static void scan() throws IOException{
		Scan scan=new Scan();
		HTable table = (HTable) conn.getTable(TableName.valueOf("patient"));
		ResultScanner resultScanner=table.getScanner(scan);
	
		for(Result result:resultScanner){
			List<String> all = new ArrayList<String>();
			Map<String,List> map = new TreeMap<>();
			for(Cell cell:result.rawCells()){
				List l = gson.fromJson(Bytes.toString(cell.getValue()),ArrayList.class);
				if(Bytes.toString(cell.getQualifier()).equals("StrokeReport"))
				map.put(Bytes.toString(cell.getQualifier()), l);
				
			}
			for(List list : map.values())
			all.addAll(list);
			System.out.println(all.size()+""+all);
		}
	
		resultScanner.close();

	}
	
	public static void createTable(String tableName, String[] family)
	        throws Exception {
	  

	    HBaseAdmin admin = (HBaseAdmin) conn.getAdmin();

	    // 表 HTableDescriptor 列 HColumnDescriptor
	    HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
	    for (int i = 0; i < family.length; i++) {
	        desc.addFamily(new HColumnDescriptor(family[i]));
	    }
	    if (admin.tableExists(tableName)) {
	        System.out.println("table Exists!");
	    } else {
	        admin.createTable(desc);
	        System.out.println("create table Success!");
	    }
	    admin.close();
	}

	public static void main(String[] args) throws Exception {
	HbaseUtils.scan();
	}

}
