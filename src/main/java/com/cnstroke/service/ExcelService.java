package com.cnstroke.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.cnstroke.utils.HbaseUtils;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeFunction;

public class ExcelService {

	public static void main(String[] args) throws Exception {
		FileInputStream fileInputStream = new FileInputStream("d://data/病历表.xls"); // 获取d://test.xls,建立数据的输入通道
		//System.out.println(fileInputStream);
		POIFSFileSystem poifsFileSystem = new POIFSFileSystem(fileInputStream); // 使用POI提供的方法得到excel的信息
		//System.out.println("excel-1的信息:" + poifsFileSystem);
		HSSFWorkbook workbook = new HSSFWorkbook(poifsFileSystem);// 得到文档对象
		System.out.println("excel-2的信息:" + workbook);
		HSSFSheet sheet = workbook.getSheet("sheet1"); // 根据name获取sheet表
		System.out.println("excel-3的信息：" + sheet);

		/*HSSFRow row = sheet.getRow(0); // 获取第一行
		System.out.println(sheet.getLastRowNum() + " " + row.getLastCellNum()); // 分别得到最后一行的行号，和一条记录的最后一个单元格
*/
		
		List<List> rows = HbaseUtils.scan();
		int i = 2;
		for(List<String> l:rows){
			HSSFRow row = sheet.createRow(i++);
			int j= 0;
			for(String s : l){
			row.createCell(j++).setCellValue(s);
			}
		}
		
		FileOutputStream out = new FileOutputStream("d://data/病历表1.xls"); // 向d://test.xls中写数据

		out.flush();
		workbook.write(out);
		out.close();
	}
}
