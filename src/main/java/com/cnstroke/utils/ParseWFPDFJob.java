package com.cnstroke.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseWFPDFJob {

	private static Logger logger = LoggerFactory.getLogger(ParseWFPDFJob.class);

	public static boolean parsePDFToTXT(String inputPDFPath, String encoding, String outPutTXTPath) throws IOException {
		// �Ƿ�����
		boolean sort = false;
		// PDF�ļ���
		String pdfFile = inputPDFPath;
		File file = new File(pdfFile);
		// ��ʼ��ȡҳ��
		int startPage = 1;
		// ������ȡҳ��
		int endPage = Integer.MAX_VALUE;
		// �ļ�������������ı��ļ�
		Writer output = null;
		// �ڴ��д洢��PDF Document
		PDDocument document = null;
		try {
			File outFile = new File(outPutTXTPath);
			if (!outFile.getParentFile().exists()) {
				logger.info("----------"+outFile.getParentFile().getAbsolutePath());
				outFile.getParentFile().mkdirs();
			}
			document = PDDocument.load(file);
			// �ļ���������д���ļ���textFile
			output = new OutputStreamWriter(new FileOutputStream(outFile), encoding);
			// ����PDFTextStripper��ȡ�ı�
			PDFTextStripper stripper = new PDFTextStripper();
			// �����Ƿ�����
			stripper.setSortByPosition(sort);
			// ������ʼҳ
			stripper.setStartPage(startPage);
			// ���ý���ҳ
			stripper.setEndPage(endPage);
			// stripper.writeText(document, output);
			String txt = stripper.getText(document);
			if (StringUtils.isEmpty(txt)) {
				logger.warn("解析文件为空" + inputPDFPath);
				return false;
			}
			txt = txt.replace(" ", "");
			txt = txt.replace("\t", "");
			txt = txt.replace("&nbsp;", "");
			txt = txt.replace("\r", "");
			txt = txt.replace("\n", "");
			txt = txt.replace("\r\n", "");
			output.write(txt);
			return true;
		} catch (Exception e) {
			logger.error("解析文件失败" + inputPDFPath, e);
			return false;
		} finally {
			if (output != null) {
				output.close();
			}
			if (document != null) {
				document.close();
			}
		}
	}

	public static Map<String, String> getWFPdfInfo(String fileName) {
		Map<String, String> map = queryInfo(fileName);
		Map<String, String> fileInfo = null;
		if (map != null) {
			fileInfo = new HashMap<String, String>();
			fileInfo.put("author", map.get("author"));
			fileInfo.put("title", map.get("title"));
			String url = map.get("url");
			String year = getTime(url);
			fileInfo.put("year", year);
		}
		return fileInfo;
	}

	public static String getTime(String url) {
		String regex = "[~\\d]*(\\d+.aspx$)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(url);
		String time = null;
		while (m.find()) {
			time = m.group(0);
			time = time.substring(0, 4);
			// System.out.println(time);
		}
		return time;
	}

	public static Map<String, String> queryInfo(String fileName) {
		Map<String, String> map = null;
		// Connection con = null;
		// ResultSet rs = null;
		// String sql = "select url,title,author from wanfaspider where title =
		// ?";
		// try {
		// con = MySql.getConn();
		// //Statement st = con.createStatement();
		// PreparedStatement pt = con.prepareStatement(sql);
		// pt.setString(1, fileName);
		// rs = pt.executeQuery();
		//// rs = MySql.query(sql);
		// if(rs!=null){
		// if(rs.next()){
		// map = new HashMap<String,String>();
		// map.put("url", rs.getString(1));
		// map.put("title", rs.getString(2));
		// map.put("author", rs.getString(3));
		// }
		// }
		//
		// } catch (Exception e) {
		// System.err.println("Exception: " + e.getMessage());
		// System.out.println("sql="+sql);
		// } finally{
		// if(con!=null){
		// try {
		// con.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		return map;
	}

}
