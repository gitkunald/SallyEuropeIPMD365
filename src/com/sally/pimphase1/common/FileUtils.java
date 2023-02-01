package com.sally.pimphase1.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Table.Cell;
import com.ibm.icu.impl.Row;
import com.ibm.pim.docstore.Document;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;

import java.io.*;
import au.com.bytecode.opencsv.CSVWriter;

public class FileUtils {
	private static Logger logger = LogManager.getLogger(FileUtils.class);

	public static Map<String, List<Entry>> parseCSVFile(File file) throws Exception {
		Map<String, List<Entry>> mapOfPimIDs = new HashMap<>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = " ";
			String[] tempArr;
			int rowCnt = 0;
			ArrayList<String> headerList = new ArrayList<>();
			// Row level iteration

			while ((line = br.readLine()) != null) {

				tempArr = line.split("\\,");

				// column level iteration
				String pimId = "";
				List<Entry> list = new ArrayList<>();
				int i = 1;
				int columnCnt = 0;
				for (String tempStr : tempArr) {
					Entry<String, String> attribute = null;
					if (rowCnt == 0) {
						headerList.add(tempStr);
					}
					if (columnCnt == 0 && rowCnt != 0) {
						pimId = tempStr;
					}

					if (columnCnt != 0 && rowCnt != 0) {
						attribute = new AbstractMap.SimpleEntry<String, String>(headerList.get(i), tempStr);
						i++;
					}

					if (attribute != null)
						list.add(attribute);
					columnCnt++;

				}

				if (rowCnt != 0)
					mapOfPimIDs.put(pimId, list);

				rowCnt++;

			}
			br.close();

			logger.info("Number of lines parsed : " + rowCnt + "for File : " + file);
			logger.info("Returning Result : " + mapOfPimIDs);
			System.out.println("Returning Result : " + mapOfPimIDs);

		}

		catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error while parsing the file ", ex);
			throw new Exception("Error while parsing the file - " + file + " with an error " + ex);
		}
		return mapOfPimIDs;
	}

	public static Map<String, List<Entry>> parsePSVFile(File file) throws Exception {
		Map<String, List<Entry>> mapOfPimIDs = new HashMap<>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = " ";
			String[] tempArr;
			int rowCnt = 0;
			ArrayList<String> headerList = new ArrayList<>();
			// Row level iteration

			while ((line = br.readLine()) != null) {

				tempArr = line.split("\\|");

				// column level iteration
				String pimId = "";
				List<Entry> list = new ArrayList<>();
				int i = 1;
				int columnCnt = 0;
				for (String tempStr : tempArr) {
					Entry<String, String> attribute = null;
					if (rowCnt == 0) {
						headerList.add(tempStr);
					}
					if (columnCnt == 0 && rowCnt != 0) {
						pimId = tempStr;
					}

					if (columnCnt != 0 && rowCnt != 0) {
						attribute = new AbstractMap.SimpleEntry<String, String>(headerList.get(i), tempStr);
						i++;
					}

					if (attribute != null)
						list.add(attribute);
					columnCnt++;

				}

				if (rowCnt != 0)
					mapOfPimIDs.put(pimId, list);

				rowCnt++;

			}
			br.close();

			logger.info("Number of lines parsed : " + rowCnt + "for File : " + file);
			logger.info("Returning Result : " + mapOfPimIDs);
			System.out.println("Returning Result : " + mapOfPimIDs);

		}

		catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error while parsing the file ", ex);
			throw new Exception("Error while parsing the file - " + file + " with an error " + ex);
		}
		return mapOfPimIDs;
	}

	public static Map<String, String> parseCSVFileforMap(File file) throws Exception {
		Map<String, String> mapOfVendorIds = new HashMap<>();
		try {

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = " ";
			String[] tempArr;
			int rowCnt = 0;

			// Row level iteration

			while ((line = br.readLine()) != null) {

				tempArr = line.split("\\,");

				String key = "";
				String val = "";

				int i = 1;
				int columnCnt = 0;
				// column level iteration
				for (String tempStr : tempArr) {

					if (columnCnt == 0 && rowCnt != 0) {
						key = tempStr;

					} else if (columnCnt == 1 && rowCnt != 0) {
						val = tempStr;
					}

					columnCnt++;

				}

				if (rowCnt != 0)
					mapOfVendorIds.put(key, val);

				rowCnt++;

			}
			br.close();

			logger.info("Number of lines parsed : " + rowCnt + "for File : " + file);
			logger.info("Returning Result : " + mapOfVendorIds);
			System.out.println("Returning Result : " + mapOfVendorIds);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error while parsing the file ", ex);
			throw new Exception("Error while parsing the file - " + file + " with an error " + ex);
		}
		return mapOfVendorIds;

	}

	public static Map<String, String> parsePSVFileforMap(File file) throws Exception {
		Map<String, String> mapOfVendorIds = new HashMap<>();
		try {

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = " ";
			String[] tempArr;
			int rowCnt = 0;

			// Row level iteration

			while ((line = br.readLine()) != null) {

				tempArr = line.split("\\|");

				String key = "";
				String val = "";

				int i = 1;
				int columnCnt = 0;
				// column level iteration
				for (String tempStr : tempArr) {

					if (columnCnt == 0 && rowCnt != 0) {
						key = tempStr;

					} else if (columnCnt == 1 && rowCnt != 0) {
						val = tempStr;
					}

					columnCnt++;

				}

				if (rowCnt != 0)
					mapOfVendorIds.put(key, val);

				rowCnt++;

			}
			br.close();

			logger.info("Number of lines parsed : " + rowCnt + "for File : " + file);
			logger.info("Returning Result : " + mapOfVendorIds);
			System.out.println("Returning Result : " + mapOfVendorIds);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error while parsing the file ", ex);
			throw new Exception("Error while parsing the file - " + file + " with an error " + ex);
		}
		return mapOfVendorIds;

	}

	public static ArrayList<String> parsetxtFile(String file) throws Exception {

		logger.error("File received for parse : " + file);

		ArrayList<String> listOfLines = new ArrayList<>();
		BufferedReader bufReader;
		try {
			bufReader = new BufferedReader(new FileReader(file));

			String line = bufReader.readLine();

			while (line != null) {
				listOfLines.add(line);

				line = bufReader.readLine();

			}
			bufReader.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error while parsing the file ", e);
			throw new Exception("Error while parsing the txt file - " + file + " with an error " + e);
		}

		return listOfLines;
	}

	public static ArrayList<String> readXSLXfile(String file) throws Exception {
		ArrayList<String> listOfLines = new ArrayList<>();

		FileInputStream stream = null;
		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		try {

			File xlFile = new File(file);
			stream = new FileInputStream(xlFile);
			workbook = new XSSFWorkbook(stream);
			sheet = workbook.getSheetAt(0);

			for (int row = 1; row < sheet.getPhysicalNumberOfRows(); row++) {
				for (int column = 0; column < sheet.getRow(row).getPhysicalNumberOfCells(); column++) {

					String cellValue = sheet.getRow(row).getCell(column).getStringCellValue();
					listOfLines.add(cellValue);

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error while parsing the file ", e);
			throw new Exception("Error while parsing the XLSX file - " + file + " with an error " + e);
		}
		logger.error("listOfAttributes : " + listOfLines);
		return listOfLines;
	}

	public static void writeCSV(String filePath, Set<String> List) {
		try {
			logger.error("filePath : " + filePath);
			logger.error("List : " + List);
			StringBuilder sb = new StringBuilder();

			for (String s : List) {
				sb.append(s).append(',');
			}

			sb.deleteCharAt(sb.length() - 1); // delete last comma

			String finalContentTobeSaved = sb.toString();

			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(finalContentTobeSaved);
			myWriter.close();
		} catch (IOException e) {
			logger.error("error while writing the file : " + e);
			e.printStackTrace();
		}
	}

	public static String convertDocumentToString(Document doc) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			// below code to remove XML declaration
			// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			return output;
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static File copyInputStreamToFile(InputStream inputStream, String fileName) throws IOException {

		File file = new File(fileName);
		// append = false
		try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
			int read;
			byte[] bytes = new byte[8192];
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
		return file;
	}

	public static void main(String[] args) throws Exception {

		// csv file to read
		// File csvFile =
		String vCSV = "C:\\Users\\mitadmin1\\Desktop\\Sally\\Vendors_2022-09-14T14_08_47Z.csv";
		// FileUtils.parseCSVFileforMap(vCSV);

		String xlsxFile = "C:\\Users\\mitadmin1\\Desktop\\Sally\\Attributes.xlsx";
		// FileUtils.readXSLXfile(xlsxFile);

		String filePath = "C:\\Users\\mitadmin1\\Desktop\\Sally\\Successtest.csv";
		Set<String> pimId = new LinkedHashSet<String>();
		pimId.add("1");
		pimId.add("2");
		pimId.add("3");
		pimId.add("2");

		// FileUtils.writeCSV(filePath, pimId);

	}

}
