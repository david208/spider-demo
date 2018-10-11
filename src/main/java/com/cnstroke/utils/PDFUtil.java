package com.cnstroke.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class PDFUtil {
	
	/**
	 * pdf转txt文件  不支持图片pdf
	 * @param inputPDFPath 
	 * @param encoding      
	 * @param outPutTXTPath
	 * @throws IOException
	 */
	 public static void parsePDFToTXT(String inputPDFPath,String encoding,String outPutTXTPath) throws IOException {  
		 // 是否排序  
	        boolean sort = false;  
	        // PDF文件名  
	        String pdfFile = inputPDFPath;  
	        File file = new File(pdfFile);
	        // 输入文本文件名称  
	        String textFile = null;  
	        // 开始提取页数  
	        int startPage = 1;  
	        // 结束提取页数  
	        int endPage = Integer.MAX_VALUE;  
	        // 文件输入流，生成文本文件  
	        Writer output = null;  
	        // 内存中存储的PDF Document  
	        PDDocument document = null;  
	        try{
	            document = PDDocument.load(file);  
	            if(pdfFile.length()>4){  
	                textFile = pdfFile.substring(0, pdfFile.length()-4) + ".txt";
	                String[] temp = textFile.split("\\\\");
	                textFile = temp[temp.length-1];
	                textFile = outPutTXTPath+File.separator+textFile;
	            }  
	            // 文件输入流，写入文件到textFile  
	            output = new OutputStreamWriter(new FileOutputStream(textFile), encoding);  
	            // 采用PDFTextStripper提取文本  
	           
	            PDFTextStripper stripper = new PDFTextStripper();  
	            // 设置是否排序  
	            stripper.setSortByPosition(sort);  
	            // 设置起始页  
	            stripper.setStartPage(startPage);  
	            // 设置结束页  
	            stripper.setEndPage(endPage);  
//	            stripper.writeText(document, output);  
	           

	           String txt = stripper.getText(document);
	        
//	           txt = txt.replace(" ", "");
//	           txt = txt.replace("\t", "");
//	           txt = txt.replace("&nbsp;", "");
//	           txt = txt.replace("\r", "");
	           txt = txt.replace("\n", "");
	           txt = txt.replace("\r\n", "");
	           stripper.writeText(document, output);
//	           output.write(txt);

	        }catch(Exception e){  
	            e.printStackTrace();  
	        }finally{  
	            if(output != null){  
	                output.close();  
	            }  
	            if(document != null){  
	                document.close();  
	            }  
	        }  
	    }
	 
	
	 
	 public static String fileToTxt(String path) {
		 	File f = new File(path);
	        //1、创建一个parser  
		 	Tika tika = new Tika();
		 	String str = "";
		 	try {
				str = tika.parseToString(f);
				str = str.replace("\n", "");
				FileWriter fileWriter=new FileWriter("c:\\Result.txt");
				   int [] a=new int[]{11112,222,333,444,555,666};
				   for (int i = 0; i < a.length; i++) {
				    fileWriter.write(String.valueOf(a[i])+" ");
				     }
				   fileWriter.flush();
				   fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 	return str;
	    }
	 
	 
	 
	
	public static void main(String[] args) {
		String filePath = "D:\\1101班丁纪京都（省优）.pdf";
//		PDFUtil.parsingWFPDF(filePath, "UTF-8", "E:\\wangf");
		//PDFUtil.getTime("");
//		try {
//			String str = PDFUtil.fileToTxt(filePath);
//			str = str.replace("\n", "");
//			System.out.println(str);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		 try {
			PDFUtil.parsePDFToTXT(filePath, "UTF-8", "E:\\");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
