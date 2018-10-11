package com.cnstroke.utils;

import java.io.File;
import java.io.IOException;

public class ParseWEIpuPdfToTxtJob {
	
	public void excuteParsing(){
		String dirPath = "E:\\out";
		//搜寻目录下的pdf文件
		File dirFile = new File(dirPath);
		File[] files = null;
		if(dirFile.isDirectory()){
			files = dirFile.listFiles();
		}
		String outputPath = "E:\\outs";
		File outFile = new File(outputPath);
		if(!outFile.exists()){
			outFile.mkdir();
		}
		if(files!=null){
		for(File file:files){
			try {
				PDFUtil.parsePDFToTXT(file.getAbsolutePath(), "UTF-8", outputPath);
				file.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	}

	public static void main(String[] args) {
		ParseWEIpuPdfToTxtJob test = new ParseWEIpuPdfToTxtJob();
		test.excuteParsing();
	}
	
}
