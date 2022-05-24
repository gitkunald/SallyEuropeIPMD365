package com.sally.pim.imports;

import com.ibm.pim.extensionpoints.ImportFunction;
import com.ibm.pim.extensionpoints.ImportFunctionArguments;
import com.ibm.pim.hierarchy.Hierarchy;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.organization.Organization;
import com.ibm.pim.organization.OrganizationHierarchy;
import com.ibm.pim.system.PIMProgress;
import com.ibm.pim.system.ScriptStatistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.ccd.common.context.AustinContext;
import com.ibm.ccd.docstore.interfaces.IDoc;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ExtendedValidationErrors;
import com.ibm.pim.common.ProcessingOptions;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.common.exceptions.PIMInvalidOperationException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.imports.SallyCtgInitialImport.class"

public class SallyCtgInitialImport implements ImportFunction {

	private static Logger logger = Logger.getLogger(SallyCtgInitialImport.class);
	public static HashMap<String, Item> hmBaseItemDetails = new HashMap<String, Item>();
	public static List<String> barcodeList = new ArrayList<>();
	static Context ctx = PIMContextFactory.getCurrentContext();
	StringBuilder sbDebug = null;
	StringBuilder sbResults = null;
	StringBuilder sbStack = null;
	String resultsFile = StringUtils.EMPTY;
	String debugFile = StringUtils.EMPTY;
	String stackFile = StringUtils.EMPTY;
	ScriptStatistics scriptStats = null;
	Writer writer = null;

	@Override
	public void doImport(ImportFunctionArguments inArgs) {
		IDoc document = null;
		AustinContext localAustinContext = null;
		File file = null;
		FileInputStream stream = null;
		long startTime = System.currentTimeMillis();
		int totalRecordsProcessed = 0;
		int successRecords = 0;
		int errorsCount = 0;
		
		try {

			ctx.getAdminHelper().flushScriptCache();
			String jobStartTime = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(new Date());

			// 1. Gets uploaded file
			Document doc = inArgs.getDocstoreDataDoc();
			// Document doc =
			// ctx.getDocstoreManager().getDocument("temp_reports/Initial_Load_Template.xlsx");

			sbResults = new StringBuilder();
			sbDebug = new StringBuilder();
			sbStack = new StringBuilder();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String todaysDate = sdf.format(calendar.getTime());
			PIMProgress progress = inArgs.getProgress();

			String timeStamp = new SimpleDateFormat("HH.mm.ss").format(calendar.getTime());

			resultsFile = "temp_reports/SallyImport/" + todaysDate + "_" + timeStamp + "_success.csv";
			debugFile = "temp_reports/SallyImport/" + todaysDate + "_" + timeStamp + "_failure" + ".csv";
			stackFile = "temp_reports/SallyImport/" + todaysDate + "_" + timeStamp + "_debug" + ".csv";

			scriptStats = inArgs.getScriptStats();
			writer = inArgs.getErrors().getWriter();
			initialiseJobStats();

			// Captures item's save status & errors
			sbResults.append("Job started at: " + jobStartTime + "\n");
			sbResults.append("Sally Products Catalog" + "\n");
			sbResults.append("\n");
			sbResults.append("ROW-NUMBER" + "," + "ITEM-ID" + "," + "Result" + "," + "Reason for failure" + "\n");

			// Captures import job results & exceptions
			sbDebug.append("Job started at: " + jobStartTime + "\n");
			sbDebug.append("Sally Products Catalog" + "\n");
			sbDebug.append("\n");
			sbDebug.append("ROW-NUMBER" + "," + "ITEM-ID" + "," + "Result" + "," + "Reason for failure" + "\n");

			// Captures import job exceptions and stackrace
			sbStack.append("Job started at: " + jobStartTime + "\n");
			sbStack.append("Sally Products Catalog" + "\n");
			sbStack.append("\n");
			sbStack.append("ROW-NUMBER" + "," + "ITEM-ID" + "," + "Result" + "," + "Reason for failure" + "\n");

			if (doc != null) {
				
				localAustinContext = AustinContext.getCurrentContext();
				document = localAustinContext.getDocStoreMgr().get(doc.getPath(), false,
						AustinContext.getCurrentContext().getCompanyId(), true);
				file = document.getTmpFile();
				stream = new FileInputStream(file);

				XSSFWorkbook workbook = new XSSFWorkbook(stream);

				XSSFSheet sheet = workbook.getSheetAt(0);

				logger.info("Number of Rows : " + sheet.getPhysicalNumberOfRows());
				logger.info("Number of Columns : " + sheet.getRow(0).getPhysicalNumberOfCells());

				for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
					Boolean itemValuePresent = false;
					Boolean errorFlag = false;
					Item item = ctx.getCatalogManager().getCatalog("Sally_Products_Catalog").createItem();
					for (int z = 0; z < sheet.getRow(0).getPhysicalNumberOfCells(); z++) {

						logger.info("Attribute: " + sheet.getRow(0).getCell(z).getStringCellValue());

						String stringFirstCellValue = sheet.getRow(i).getCell(0).getStringCellValue();

						if (stringFirstCellValue != null && stringFirstCellValue != "") {
							itemValuePresent = true;
							if (sheet.getRow(i).getCell(7).getStringCellValue().equalsIgnoreCase("Item")
									&& !sheet.getRow(0).getCell(z).getStringCellValue().contains("Variant_ss")) {

								// Item_Type Hierarchy needs to be mapped
								Hierarchy itemtype = ctx.getHierarchyManager()
										.getHierarchy("Sally_Item_Type_Hierarchy");
								Category itemcategory = itemtype.getCategoryByPrimaryKey("CIH1");


								if (Objects.nonNull(itemcategory)) {
									try {
										item.mapToCategory(itemcategory);
									} catch (PIMInvalidOperationException e) {
										e.printStackTrace();
									}
								}

								if (sheet.getRow(0).getCell(z).getStringCellValue().contains("retail_group_id")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("minimum_order_quantity")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("commission_group_id")
										|| sheet.getRow(0).getCell(z).getStringCellValue().contains("commodity_code")) {
									item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
											sheet.getRow(i).getCell(z).getNumericCellValue());
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("primaryvendor_id")) {

									item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue() + "#0",
											sheet.getRow(i).getCell(z).getNumericCellValue());
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Product_c/primaryvendor_name")) {
									Organization vendorCategory = null;
									OrganizationHierarchy vendorHierarchy = ctx.getOrganizationManager()
											.getOrganizationHierarchy("Vendor Organization Hierarchy");
									String vendorname = sheet.getRow(i).getCell(z).getStringCellValue();
									if (vendorHierarchy != null) {
										if (vendorname != null && !vendorname.isEmpty()) {
											vendorCategory = vendorHierarchy.getOrganizationByPrimaryKey(vendorname);
										}
									}
									if (vendorCategory != null) {
										item.mapToOrganization(vendorCategory);
									} else {

										errorFlag = true;

										sbDebug.append(
												i + "," + "" + "," + "FAILED" + "," + "Missing vendor ID" + "\n");

									}

								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Product Category Code")) {
									Category category = null;
									Hierarchy masterHierarchy = null;
									
									masterHierarchy = ctx.getHierarchyManager()
											.getHierarchy("Sally_Products_Hierarchy");
									String categoryCode = sheet.getRow(i).getCell(z).getStringCellValue();
									
									if (masterHierarchy != null) {
										if (categoryCode != null && !categoryCode.isEmpty()) {
											category = masterHierarchy.getCategoryByPrimaryKey(categoryCode);
										}
									}
									if (category != null) {
										item.mapToCategory(category);

									} else {
										errorFlag = true;
										sbDebug.append(
												i + "," + "" + "," + "FAILED" + "," + "Missing Category Code" + "\n");

									}

								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Brand Code")) {
									Category brand = null;
									Hierarchy masterHierarchy = null;
									
									masterHierarchy = ctx.getHierarchyManager().getHierarchy("Brand_Hierarchy");
									String brandCode = sheet.getRow(i).getCell(z).getStringCellValue();
									if (masterHierarchy != null) {
										if (brandCode != null && !brandCode.isEmpty()) {
											brand = masterHierarchy.getCategoryByPrimaryKey(brandCode);
										}
									}
									if (brand != null) {
										item.mapToCategory(brand);
									} else {

										errorFlag = true;
										sbDebug.append(
												i + "," + "" + "," + "FAILED" + "," + "Missing Brand Code" + "/n");

									}

								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("replacement_item_id")) {
									continue;
									// As of now skipping to set the relationship attribute and multi occurring
									// attrib types.
								}


								else {

									logger.info("Not a Primary Vendor Id attribute");

									XSSFCell cell = sheet.getRow(i).getCell(z);

									if (cell != null) {
										logger.info(
												"stringCellValue : " + sheet.getRow(i).getCell(z).getStringCellValue());
										String stringCellValue = sheet.getRow(i).getCell(z).getStringCellValue();
										if (!stringCellValue.isEmpty()) {
											item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
													sheet.getRow(i).getCell(z).getStringCellValue());

										}
									}

								}
							}

							else if (sheet.getRow(i).getCell(7).getStringCellValue().equalsIgnoreCase("Variant")
									&& !sheet.getRow(0).getCell(z).getStringCellValue().contains("Item_ss")) {

								Hierarchy itemtype = ctx.getHierarchyManager()
										.getHierarchy("Sally_Item_Type_Hierarchy");
								Category itemcategory = itemtype.getCategoryByPrimaryKey("CIH2");


								if (Objects.nonNull(itemcategory)) {
									try {
										item.mapToCategory(itemcategory);
									} catch (PIMInvalidOperationException e) {

										e.printStackTrace();
									}
								}

								if (sheet.getRow(0).getCell(z).getStringCellValue().contains("qpbqtymlgr")
										|| sheet.getRow(0).getCell(z).getStringCellValue().contains("outer_diameter")
										|| sheet.getRow(0).getCell(z).getStringCellValue().contains("inner_diameter")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("web_quantity_restriction")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Variant_ss/page_number")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("supplier_lead_time")
										|| sheet.getRow(0).getCell(z).getStringCellValue().contains("manufacturer_id")
										|| sheet.getRow(0).getCell(z).getStringCellValue().contains("colour_id")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Variant_ss/airflow/value")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Variant_ss/max_temp/value")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Variant_ss/min_temp/value")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Variant_ss/contents/value")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Variant_ss/watt/value")
										|| sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Variant_ss/variant_differentiators/size")

								) {
									Double cellValue = sheet.getRow(i).getCell(z).getNumericCellValue();

									if (cellValue != null) {
										item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
												sheet.getRow(i).getCell(z).getNumericCellValue());
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("primaryvendor_id")) {

									item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue() + "#0",
											sheet.getRow(i).getCell(z).getNumericCellValue());
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/base_cost")) {
									
									String basecost = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!basecost.isEmpty() && basecost.contains("|")) {

										String[] basecosts = basecost.split("\\|");

										for (int aa = 0; aa < basecosts.length; aa++) {
											logger.info("Pipeline values: " + basecosts[aa]);
											item.setAttributeValue("Variant_ss/Pricing#" + aa + "/base_cost",
													basecosts[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_retail_price/es_ES")) {
									
									String vendorrecommendedretailes = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedretailes.isEmpty()
											&& vendorrecommendedretailes.contains("|")) {

										String[] vendorrecommendedretailses = vendorrecommendedretailes.split("\\|");
										

										for (int aa = 0; aa < vendorrecommendedretailses.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/vendor_recommended_retail_price/es_ES",
													vendorrecommendedretailses[aa]);
										}
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_retail_price/nl_NL")) {
									
									String vendorrecommendedretailnl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedretailnl.isEmpty()
											&& vendorrecommendedretailnl.contains("|")) {

										String[] vendorrecommendedretailsnl = vendorrecommendedretailnl.split("\\|");
										
										for (int aa = 0; aa < vendorrecommendedretailsnl.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/vendor_recommended_retail_price/nl_NL",
													vendorrecommendedretailsnl[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_retail_price/fr_FR")) {
									
									String vendorrecommendedretailfr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedretailfr.isEmpty()
											&& vendorrecommendedretailfr.contains("|")) {

										String[] vendorrecommendedretailsfr = vendorrecommendedretailfr.split("\\|");
										
										for (int aa = 0; aa < vendorrecommendedretailsfr.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/vendor_recommended_retail_price/fr_FR",
													vendorrecommendedretailsfr[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_retail_price/en_GB")) {
									
									String vendorrecommendedretailgb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedretailgb.isEmpty()
											&& vendorrecommendedretailgb.contains("|")) {

										String[] vendorrecommendedretailsgb = vendorrecommendedretailgb.split("\\|");

										for (int aa = 0; aa < vendorrecommendedretailsgb.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/vendor_recommended_retail_price/en_GB",
													vendorrecommendedretailsgb[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_retail_price/de_DE")) {
									
									String vendorrecommendedretailde = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedretailde.isEmpty()
											&& vendorrecommendedretailde.contains("|")) {

										String[] vendorrecommendedretailsde = vendorrecommendedretailde.split("\\|");
										
										for (int aa = 0; aa < vendorrecommendedretailsde.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/vendor_recommended_retail_price/de_DE",
													vendorrecommendedretailsde[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_retail_price/nl_BE")) {
									
									String vendorrecommendedretailbe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedretailbe.isEmpty()
											&& vendorrecommendedretailbe.contains("|")) {

										String[] vendorrecommendedretailsbe = vendorrecommendedretailbe.split("\\|");

										for (int aa = 0; aa < vendorrecommendedretailsbe.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/vendor_recommended_retail_price/nl_BE",
													vendorrecommendedretailsbe[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_trade/es_ES")) {
									
									String vendorrecommendedtradees = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedtradees.isEmpty() && vendorrecommendedtradees.contains("|")) {

										String[] vendorrecommendedtradeses = vendorrecommendedtradees.split("\\|");
										
										for (int aa = 0; aa < vendorrecommendedtradeses.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/vendor_recommended_trade/es_ES",
													vendorrecommendedtradeses[aa]);
										}
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_trade/nl_NL")) {
									
									String vendorrecommendedtradenl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedtradenl.isEmpty() && vendorrecommendedtradenl.contains("|")) {

										String[] vendorrecommendedtradesnl = vendorrecommendedtradenl.split("\\|");
										
										for (int aa = 0; aa < vendorrecommendedtradesnl.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/vendor_recommended_trade/nl_NL",
													vendorrecommendedtradesnl[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_trade/fr_FR")) {
									
									String vendorrecommendedtradefr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedtradefr.isEmpty() && vendorrecommendedtradefr.contains("|")) {

										String[] vendorrecommendedtradesfr = vendorrecommendedtradefr.split("\\|");

										for (int aa = 0; aa < vendorrecommendedtradesfr.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/vendor_recommended_trade/fr_FR",
													vendorrecommendedtradesfr[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_trade/en_GB")) {
									
									String vendorrecommendedtradegb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedtradegb.isEmpty() && vendorrecommendedtradegb.contains("|")) {

										String[] vendorrecommendedtradesgb = vendorrecommendedtradegb.split("\\|");

										for (int aa = 0; aa < vendorrecommendedtradesgb.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/vendor_recommended_trade/en_GB",
													vendorrecommendedtradesgb[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_trade/de_DE")) {
									String vendorrecommendedtradede = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedtradede.isEmpty() && vendorrecommendedtradede.contains("|")) {

										String[] vendorrecommendedtradesde = vendorrecommendedtradede.split("\\|");

										for (int aa = 0; aa < vendorrecommendedtradesde.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/vendor_recommended_trade/de_DE",
													vendorrecommendedtradesde[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/vendor_recommended_trade/nl_BE")) {
									
									String vendorrecommendedtradebe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!vendorrecommendedtradebe.isEmpty() && vendorrecommendedtradebe.contains("|")) {

										String[] vendorrecommendedtradesbe = vendorrecommendedtradebe.split("\\|");

										for (int aa = 0; aa < vendorrecommendedtradesbe.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/vendor_recommended_trade/nl_BE",
													vendorrecommendedtradesbe[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_excluding_vat/es_ES")) {
									
									String professionalexcludinges = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalexcludinges.isEmpty() && professionalexcludinges.contains("|")) {

										String[] professionalsexcludinges = professionalexcludinges.split("\\|");

										for (int aa = 0; aa < professionalsexcludinges.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_excluding_vat/es_ES",
													professionalsexcludinges[aa]);
										}
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_excluding_vat/nl_NL")) {
									
									String professionalexcludingnl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalexcludingnl.isEmpty() && professionalexcludingnl.contains("|")) {

										String[] professionalsexcludingnl = professionalexcludingnl.split("\\|");
										

										for (int aa = 0; aa < professionalsexcludingnl.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_excluding_vat/nl_NL",
													professionalsexcludingnl[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_excluding_vat/fr_FR")) {
									
									String professionalexcludingfr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalexcludingfr.isEmpty() && professionalexcludingfr.contains("|")) {

										String[] professionalsexcludingfr = professionalexcludingfr.split("\\|");

										for (int aa = 0; aa < professionalsexcludingfr.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_excluding_vat/fr_FR",
													professionalsexcludingfr[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_excluding_vat/en_GB")) {
									
									String professionalexcludinggb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalexcludinggb.isEmpty() && professionalexcludinggb.contains("|")) {

										String[] professionalsexcludinggb = professionalexcludinggb.split("\\|");

										for (int aa = 0; aa < professionalsexcludinggb.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_excluding_vat/en_GB",
													professionalsexcludinggb[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_excluding_vat/de_DE")) {
									
									String professionalexcludingde = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalexcludingde.isEmpty() && professionalexcludingde.contains("|")) {

										String[] professionalsexcludingde = professionalexcludingde.split("\\|");
										for (int aa = 0; aa < professionalsexcludingde.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_excluding_vat/de_DE",
													professionalsexcludingde[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_excluding_vat/nl_BE")) {
									
									String professionalexcludingbe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalexcludingbe.isEmpty() && professionalexcludingbe.contains("|")) {

										String[] professionalsexcludingbe = professionalexcludingbe.split("\\|");
										
										for (int aa = 0; aa < professionalsexcludingbe.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_excluding_vat/nl_BE",
													professionalsexcludingbe[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_including_vat/es_ES")) {
									
									String professionalincludinges = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalincludinges.isEmpty() && professionalincludinges.contains("|")) {

										String[] professionalsincludinges = professionalincludinges.split("\\|");

										for (int aa = 0; aa < professionalsincludinges.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_including_vat/es_ES",
													professionalsincludinges[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_including_vat/nl_NL")) {
									
									String professionalincludingnl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalincludingnl.isEmpty() && professionalincludingnl.contains("|")) {

										String[] professionalsincludingnl = professionalincludingnl.split("\\|");

										for (int aa = 0; aa < professionalsincludingnl.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_including_vat/nl_NL",
													professionalsincludingnl[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_including_vat/fr_FR")) {
									
									String professionalincludingfr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalincludingfr.isEmpty() && professionalincludingfr.contains("|")) {

										String[] professionalsincludingfr = professionalincludingfr.split("\\|");

										for (int aa = 0; aa < professionalsincludingfr.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_including_vat/fr_FR",
													professionalsincludingfr[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_including_vat/en_GB")) {
									
									String professionalincludinggb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalincludinggb.isEmpty() && professionalincludinggb.contains("|")) {

										String[] professionalsincludinggb = professionalincludinggb.split("\\|");
										
										for (int aa = 0; aa < professionalsincludinggb.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_including_vat/en_GB",
													professionalsincludinggb[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_including_vat/de_DE")) {
									
									String professionalincludingde = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalincludingde.isEmpty() && professionalincludingde.contains("|")) {

										String[] professionalsincludingde = professionalincludingde.split("\\|");

										for (int aa = 0; aa < professionalsincludingde.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_including_vat/de_DE",
													professionalsincludingde[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/professional_price_including_vat/nl_BE")) {
									
									String professionalincludingbe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!professionalincludingbe.isEmpty() && professionalincludingbe.contains("|")) {

										String[] professionalsincludingbe = professionalincludingbe.split("\\|");

										for (int aa = 0; aa < professionalsincludingbe.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa
															+ "/professional_price_including_vat/nl_BE",
													professionalsincludingbe[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_excluding_vat/es_ES")) {
									
									String rees = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!rees.isEmpty() && rees.contains("|")) {

										String[] reess = rees.split("\\|");

										for (int aa = 0; aa < reess.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_excluding_vat/es_ES",
													reess[aa]);
										}
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_excluding_vat/nl_NL")) {
									
									String renl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!renl.isEmpty() && renl.contains("|")) {

										String[] renls = renl.split("\\|");

										for (int aa = 0; aa < renls.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_excluding_vat/nl_NL",
													renls[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_excluding_vat/fr_FR")) {
									
									String refr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!refr.isEmpty() && refr.contains("|")) {

										String[] refrs = refr.split("\\|");

										for (int aa = 0; aa < refrs.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_excluding_vat/fr_FR",
													refrs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_excluding_vat/en_GB")) {
									
									String regb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!regb.isEmpty() && regb.contains("|")) {

										String[] regbs = regb.split("\\|");

										for (int aa = 0; aa < regbs.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_excluding_vat/en_GB",
													regbs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_excluding_vat/de_DE")) {
									
									String rede = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!rede.isEmpty() && rede.contains("|")) {

										String[] redes = rede.split("\\|");

										for (int aa = 0; aa < redes.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_excluding_vat/de_DE",
													redes[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_excluding_vat/nl_BE")) {
									String rebe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!rebe.isEmpty() && rebe.contains("|")) {

										String[] rebes = rebe.split("\\|");

										for (int aa = 0; aa < rebes.length; aa++) {
											
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_excluding_vat/nl_BE",
													rebes[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_including_vat/es_ES")) {
									
									String ries = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!ries.isEmpty() && ries.contains("|")) {

										String[] riess = ries.split("\\|");

										for (int aa = 0; aa < riess.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_including_vat/es_ES",
													riess[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_including_vat/nl_NL")) {
									
									String rinl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!rinl.isEmpty() && rinl.contains("|")) {

										String[] rinls = rinl.split("\\|");

										for (int aa = 0; aa < rinls.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_including_vat/nl_NL",
													rinls[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_including_vat/fr_FR")) {
									
									String rifr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!rifr.isEmpty() && rifr.contains("|")) {

										String[] rifrs = rifr.split("\\|");

										for (int aa = 0; aa < rifrs.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_including_vat/fr_FR",
													rifrs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_including_vat/en_GB")) {
									
									String rigb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!rigb.isEmpty() && rigb.contains("|")) {

										String[] rigbs = rigb.split("\\|");

										for (int aa = 0; aa < rigbs.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_including_vat/en_GB",
													rigbs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_including_vat/de_DE")) {
									
									String ride = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!ride.isEmpty() && ride.contains("|")) {

										String[] rides = ride.split("\\|");

										for (int aa = 0; aa < rides.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_including_vat/de_DE",
													rides[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/retail_price_including_vat/nl_BE")) {
									String ribe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!ribe.isEmpty() && ribe.contains("|")) {

										String[] ribes = ribe.split("\\|");

										for (int aa = 0; aa < ribes.length; aa++) {
											item.setAttributeValue(
													"Variant_ss/Pricing#" + aa + "/retail_price_including_vat/nl_BE",
													ribes[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_excluding_vat/es_ES")) {
									
									String sees = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sees.isEmpty() && sees.contains("|")) {

										String[] seess = sees.split("\\|");

										for (int aa = 0; aa < seess.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_excluding_vat/es_ES", seess[aa]);
										}
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_excluding_vat/nl_NL")) {
									
									String senl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!senl.isEmpty() && senl.contains("|")) {

										String[] senls = senl.split("\\|");

										for (int aa = 0; aa < senls.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_excluding_vat/nl_NL", senls[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_excluding_vat/fr_FR")) {
									
									String sefr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sefr.isEmpty() && sefr.contains("|")) {

										String[] sefrs = sefr.split("\\|");

										for (int aa = 0; aa < sefrs.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_excluding_vat/fr_FR", sefrs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_excluding_vat/en_GB")) {
									
									String segb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!segb.isEmpty() && segb.contains("|")) {

										String[] segbs = segb.split("\\|");

										for (int aa = 0; aa < segbs.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_excluding_vat/en_GB", segbs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_excluding_vat/de_DE")) {
									
									String sede = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sede.isEmpty() && sede.contains("|")) {

										String[] sedes = sede.split("\\|");

										for (int aa = 0; aa < sedes.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_excluding_vat/de_DE", sedes[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_excluding_vat/nl_BE")) {
									
									String sebe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sebe.isEmpty() && sebe.contains("|")) {

										String[] sebes = sebe.split("\\|");

										for (int aa = 0; aa < sebes.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_excluding_vat/nl_BE", sebes[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_including_vat/es_ES")) {
									String sies = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sies.isEmpty() && sies.contains("|")) {

										String[] siess = sies.split("\\|");

										for (int aa = 0; aa < siess.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_including_vat/es_ES", siess[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_including_vat/nl_NL")) {
									
									String sinl = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sinl.isEmpty() && sinl.contains("|")) {

										String[] sinls = sinl.split("\\|");
										

										for (int aa = 0; aa < sinls.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_including_vat/nl_NL", sinls[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_including_vat/fr_FR")) {
									
									String sifr = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sifr.isEmpty() && sifr.contains("|")) {

										String[] sifrs = sifr.split("\\|");

										for (int aa = 0; aa < sifrs.length; aa++) {
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_including_vat/fr_FR", sifrs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_including_vat/en_GB")) {
									
									String sigb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sigb.isEmpty() && sigb.contains("|")) {

										String[] sigbs = sigb.split("\\|");

										for (int aa = 0; aa < sigbs.length; aa++) {
											
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_including_vat/en_GB", sigbs[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_including_vat/de_DE")) {
									
									String side = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!side.isEmpty() && side.contains("|")) {

										String[] sides = side.split("\\|");

										for (int aa = 0; aa < sides.length; aa++) {
											
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_including_vat/de_DE", sides[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Pricing/salon_success_price_including_vat/nl_BE")) {
									
									String sibe = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!sibe.isEmpty() && sibe.contains("|")) {

										String[] sibes = sibe.split("\\|");

										for (int aa = 0; aa < sibes.length; aa++) {
											
											item.setAttributeValue("Variant_ss/Pricing#" + aa
													+ "/salon_success_price_including_vat/nl_BE", sibes[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_qty")) {
									
									String innerq = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerq.isEmpty() && innerq.contains("|")) {

										String[] innerqa = innerq.split("\\|");

										for (int jj = 0; jj < innerqa.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/inner_pack_qty",
													innerqa[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_height/value")) {
									
									String innerhv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerhv.isEmpty() && innerhv.contains("|")) {

										String[] innerhvs = innerhv.split("\\|");

										for (int jj = 0; jj < innerhvs.length; jj++) {
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/inner_pack_height/value", innerhvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_height/uom")) {
									
									String innerhu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerhu.isEmpty() && innerhu.contains("|")) {

										String[] innerhus = innerhu.split("\\|");

										for (int jj = 0; jj < innerhus.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/inner_pack_height/uom",
													innerhus[jj]);
										}
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_width/value")) {
									
									String innerwv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerwv.isEmpty() && innerwv.contains("|")) {

										String[] innerwvs = innerwv.split("\\|");

										for (int jj = 0; jj < innerwvs.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/inner_pack_width/value",
													innerwvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_width/uom")) {
									
									String innerwu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerwu.isEmpty() && innerwu.contains("|")) {

										String[] innerwus = innerwu.split("\\|");

										for (int jj = 0; jj < innerwus.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/inner_pack_width/uom",
													innerwus[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_depth/value")) {
									
									String innerdv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerdv.isEmpty() && innerdv.contains("|")) {

										String[] innerdvs = innerdv.split("\\|");

										for (int jj = 0; jj < innerdvs.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/inner_pack_depth/value",
													innerdvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_depth/uom")) {
									
									String innerdu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerdu.isEmpty() && innerdu.contains("|")) {

										String[] innerdus = innerdu.split("\\|");

										for (int jj = 0; jj < innerdus.length; jj++) {
											
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/inner_pack_depth/uom",
													innerdus[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_weight/value")) {
									
									String innerpv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerpv.isEmpty() && innerpv.contains("|")) {

										String[] innerpvs = innerpv.split("\\|");

										for (int jj = 0; jj < innerpvs.length; jj++) {
											
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/inner_pack_weight/value", innerpvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_weight/uom")) {
									
									String innerpu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerpu.isEmpty() && innerpu.contains("|")) {

										String[] innerpus = innerpu.split("\\|");

										for (int jj = 0; jj < innerpus.length; jj++) {
											
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/inner_pack_weight/uom",
													innerpus[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_barcode_type")) {
									
									String innerb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerb.isEmpty() && innerb.contains("|")) {

										String[] innerbs = innerb.split("\\|");

										for (int jj = 0; jj < innerbs.length; jj++) {
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/inner_pack_barcode_type", innerbs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue().contains(
										"Variant_ss/Packaging Attributes/inner_package_material/material_type")) {
									
									String innerm = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerm.isEmpty() && innerm.contains("|")) {

										String[] innerms = innerm.split("\\|");

										for (int jj = 0; jj < innerms.length; jj++) {
											
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/inner_package_material/material_type", innerms[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_package_material/weight")) {
									
									String innerma = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!innerma.isEmpty() && innerma.contains("|")) {

										String[] innermas = innerma.split("\\|");

										for (int jj = 0; jj < innermas.length; jj++) {
											
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/inner_package_material/weight", innermas[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_qty")) {
									
									String outerq = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerq.isEmpty() && outerq.contains("|")) {

										String[] outerqa = outerq.split("\\|");

										for (int jj = 0; jj < outerqa.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/outer_pack_qty",
													outerqa[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_height/value")) {
									
									String outerhv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerhv.isEmpty() && outerhv.contains("|")) {

										String[] outerhvs = outerhv.split("\\|");

										for (int jj = 0; jj < outerhvs.length; jj++) {
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/outer_pack_height/value", outerhvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_height/uom")) {
									
									String outerhu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerhu.isEmpty() && outerhu.contains("|")) {

										String[] outerhus = outerhu.split("\\|");

										for (int jj = 0; jj < outerhus.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/outer_pack_height/uom",
													outerhus[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_width/value")) {
									
									String outerwv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerwv.isEmpty() && outerwv.contains("|")) {

										String[] outerwvs = outerwv.split("\\|");

										for (int jj = 0; jj < outerwvs.length; jj++) {
											
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/outer_pack_width/value",
													outerwvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_width/uom")) {
									
									String outerwu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerwu.isEmpty() && outerwu.contains("|")) {

										String[] outerwus = outerwu.split("\\|");

										for (int jj = 0; jj < outerwus.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/outer_pack_width/uom",
													outerwus[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_depth/value")) {
									
									String outerdv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerdv.isEmpty() && outerdv.contains("|")) {

										String[] outerdvs = outerdv.split("\\|");

										for (int jj = 0; jj < outerdvs.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/outer_pack_depth/value",
													outerdvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_depth/uom")) {
									
									String outerdu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerdu.isEmpty() && outerdu.contains("|")) {

										String[] outerdus = outerdu.split("\\|");

										for (int jj = 0; jj < outerdus.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/outer_pack_depth/uom",
													outerdus[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_weight/value")) {
									
									String outerpv = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerpv.isEmpty() && outerpv.contains("|")) {

										String[] outerpvs = outerpv.split("\\|");

										for (int jj = 0; jj < outerpvs.length; jj++) {
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/outer_pack_weight/value", outerpvs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_weight/uom")) {
									
									String outerpu = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerpu.isEmpty() && outerpu.contains("|")) {

										String[] outerpus = outerpu.split("\\|");

										for (int jj = 0; jj < outerpus.length; jj++) {
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + jj + "/outer_pack_weight/uom",
													outerpus[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_barcode_type")) {
									
									String outerb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerb.isEmpty() && outerb.contains("|")) {

										String[] outerbs = outerb.split("\\|");

										for (int jj = 0; jj < outerbs.length; jj++) {
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/outer_pack_barcode_type", outerbs[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue().contains(
										"Variant_ss/Packaging Attributes/outer_package_material/material_type")) {
									
									String outerm = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerm.isEmpty() && outerm.contains("|")) {

										String[] outerms = outerm.split("\\|");

										for (int jj = 0; jj < outerms.length; jj++) {
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/outer_package_material/material_type", outerms[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_package_material/weight")) {
									String outerma = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outerma.isEmpty() && outerma.contains("|")) {

										String[] outermas = outerma.split("\\|");

										for (int jj = 0; jj < outermas.length; jj++) {
											item.setAttributeValue("Variant_ss/Packaging Attributes#" + jj
													+ "/outer_package_material/weight", outermas[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/ship_in_pallets")) {
									String shipinpallets = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!shipinpallets.isEmpty() && shipinpallets.contains("|")) {

										String[] shipinpallet = shipinpallets.split("\\|");

										for (int aa = 0; aa < shipinpallet.length; aa++) {
											
											item.setAttributeValue("Variant_ss/Warehouse#" + aa + "/ship_in_pallets",
													shipinpallet[aa]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/pick_instructions")) {
									
									String pickinstructions = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!pickinstructions.isEmpty() && pickinstructions.contains("|")) {

										String[] pickinstruction = pickinstructions.split("\\|");

										for (int bb = 0; bb < pickinstruction.length; bb++) {
											
											item.setAttributeValue("Variant_ss/Warehouse#" + bb + "/pick_instructions",
													pickinstruction[bb]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/packable")) {
									
									String packable = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!packable.isEmpty() && packable.contains("|")) {

										String[] packables = packable.split("\\|");

										for (int cc = 0; cc < packables.length; cc++) {
											item.setAttributeValue("Variant_ss/Warehouse#" + cc + "/packable",
													packables[cc]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/conveyable")) {
									
									String conveyable = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!conveyable.isEmpty() && conveyable.contains("|")) {

										String[] conveyables = conveyable.split("\\|");

										for (int dd = 0; dd < conveyables.length; dd++) {
											item.setAttributeValue("Variant_ss/Warehouse#" + dd + "/conveyable",
													conveyables[dd]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/stackable")) {
									
									String stackable = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!stackable.isEmpty() && stackable.contains("|")) {

										String[] stackables = stackable.split("\\|");

										for (int ee = 0; ee < stackables.length; ee++) {
											item.setAttributeValue("Variant_ss/Warehouse#" + ee + "/stackable",
													stackables[ee]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/pallet_type")) {
									
									String pallettype = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!pallettype.isEmpty() && pallettype.contains("|")) {

										String[] pallettypes = pallettype.split("\\|");

										for (int ff = 0; ff < pallettypes.length; ff++) {
											
											item.setAttributeValue("Variant_ss/Warehouse#" + ff + "/pallet_type",
													pallettypes[ff]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/value_added_service_id")) {
									
									String valueaddedserviceid = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!valueaddedserviceid.isEmpty() && valueaddedserviceid.contains("|")) {

										String[] valueaddedserviceids = valueaddedserviceid.split("\\|");

										for (int gg = 0; gg < valueaddedserviceids.length; gg++) {
											
											item.setAttributeValue(
													"Variant_ss/Warehouse#" + gg + "/value_added_service_id",
													valueaddedserviceids[gg]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/languagedependent")) {
									
									String languagedependent = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!languagedependent.isEmpty() && languagedependent.contains("|")) {

										String[] languagedependents = languagedependent.split("\\|");

										for (int h = 0; h < languagedependents.length; h++) {
											
											item.setAttributeValue("Variant_ss/Warehouse#" + h + "/languagedependent",
													languagedependents[h]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/outers_per_layer")) {
									String outersperlayer = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!outersperlayer.isEmpty() && outersperlayer.contains("|")) {

										String[] outersperlayers = outersperlayer.split("\\|");

										for (int ii = 0; ii < outersperlayers.length; ii++) {
											item.setAttributeValue("Variant_ss/Warehouse#" + ii + "/outers_per_layer",
													outersperlayers[ii]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/layers_per_pallete")) {
									
									String layersperpallete = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!layersperpallete.isEmpty() && layersperpallete.contains("|")) {

										String[] layersperpalletes = layersperpallete.split("\\|");

										for (int jj = 0; jj < layersperpalletes.length; jj++) {
											
											item.setAttributeValue("Variant_ss/Warehouse#" + jj + "/layers_per_pallete",
													layersperpalletes[jj]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/pallet_weight/value")) {
									
									String palletweightvalue = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!palletweightvalue.isEmpty() && palletweightvalue.contains("|")) {

										String[] palletweightvalues = palletweightvalue.split("\\|");

										for (int kk = 0; kk < palletweightvalues.length; kk++) {
											item.setAttributeValue(
													"Variant_ss/Warehouse#" + kk + "/pallet_weight/value",
													palletweightvalues[kk]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Warehouse/pallet_weight/uom")) {
									
									String palletweightuom = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!palletweightuom.isEmpty() && palletweightuom.contains("|")) {

										String[] palletweightsuom = palletweightuom.split("\\|");

										for (int ll = 0; ll < palletweightsuom.length; ll++) {
											item.setAttributeValue("Variant_ss/Warehouse#" + ll + "/pallet_weight/uom",
													palletweightsuom[ll]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/ingredients/value")) {
									
									String ingredientsvalue = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!ingredientsvalue.isEmpty() && ingredientsvalue.contains("|")) {

										String[] ingredientsvalues = ingredientsvalue.split("\\|");

										for (int n = 0; n < ingredientsvalues.length; n++) {
											item.setAttributeValue("Variant_ss/ingredients#" + n + "/value",
													ingredientsvalues[n]);
										}
									}
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/ingredients/date")) {
									String ingredientsdate = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!ingredientsdate.isEmpty() && ingredientsdate.contains("|")) {

										String[] ingredientsdates = ingredientsdate.split("\\|");

										for (int o = 0; o < ingredientsdates.length; o++) {
											item.setAttributeValue("Variant_ss/ingredients#" + o + "/date",
													ingredientsdates[o]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/net_weight/value")) {
									
									String netweightvalue = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!netweightvalue.isEmpty() && netweightvalue.contains("|")) {

										String[] netweightvalues = netweightvalue.split("\\|");

										for (int d = 0; d < netweightvalues.length; d++) {
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + d + "/net_weight/value",
													netweightvalues[d]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/net_weight/uom")) {
									
									String netweightuom = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!netweightuom.isEmpty() && netweightuom.contains("|")) {

										String[] netweightsuom = netweightuom.split("\\|");

										for (int e = 0; e < netweightsuom.length; e++) {
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + e + "/net_weight/uom",
													netweightsuom[e]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/gross_height/value")) {
									
									String grossheightvalue = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!grossheightvalue.isEmpty() && grossheightvalue.contains("|")) {

										String[] grossheightvalues = grossheightvalue.split("\\|");

										for (int f = 0; f < grossheightvalues.length; f++) {
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + f + "/gross_height/value",
													grossheightvalues[f]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/gross_height/uom")) {
									
									String grossheightuom = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!grossheightuom.isEmpty() && grossheightuom.contains("|")) {

										String[] grossheightsuom = grossheightuom.split("\\|");

										for (int g = 0; g < grossheightsuom.length; g++) {
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + g + "/gross_height/uom",
													grossheightsuom[g]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/gross_width/value")) {
									
									String grosswidthvalue = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!grosswidthvalue.isEmpty() && grosswidthvalue.contains("|")) {

										String[] grosswidthvalues = grosswidthvalue.split("\\|");

										for (int h = 0; h < grosswidthvalues.length; h++) {
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + h + "/gross_width/value",
													grosswidthvalues[h]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/gross_width/uom")) {
									String grosswidthuom = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!grosswidthuom.isEmpty() && grosswidthuom.contains("|")) {

										String[] grosswidthsuom = grosswidthuom.split("\\|");

										for (int k = 0; k < grosswidthsuom.length; k++) {
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + k + "/gross_width/uom",
													grosswidthsuom[k]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/gross_depth/value")) {
									
									String grossdepthvalue = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!grossdepthvalue.isEmpty() && grossdepthvalue.contains("|")) {

										String[] grossdepthvalues = grossdepthvalue.split("\\|");
										
										for (int l = 0; l < grossdepthvalues.length; l++) {
											
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + l + "/gross_depth/value",
													grossdepthvalues[l]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Product Dimensions/gross_depth/uom")) {
									
									String grossdepthuom = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!grossdepthuom.isEmpty() && grossdepthuom.contains("|")) {

										String[] grossdepthsuom = grossdepthuom.split("\\|");

										for (int m = 0; m < grossdepthsuom.length; m++) {
											item.setAttributeValue(
													"Variant_ss/Product Dimensions#" + m + "/gross_depth/uom",
													grossdepthsuom[m]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Barcode/barcode_type_each_level")) {
									
									String barcode_type_each_level = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!barcode_type_each_level.isEmpty() && barcode_type_each_level.contains("|")) {

										String[] barcode_type = barcode_type_each_level.split("\\|");

										for (int b = 0; b < barcode_type.length; b++) {
											item.setAttributeValue(
													"Variant_ss/Barcode#" + b + "/barcode_type_each_level",
													barcode_type[b]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Barcode/barcode_date_created")) {
									
									String barcode_date_created = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!barcode_date_created.isEmpty() && barcode_date_created.contains("|")) {

										String[] barcode_date = barcode_date_created.split("\\|");

										for (int c = 0; c < barcode_date.length; c++) {
											item.setAttributeValue("Variant_ss/Barcode#" + c + "/barcode_date_created",
													barcode_date[c]);
										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Barcode/barcode_each_level")) {
									
									String bb = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!bb.isEmpty() && bb.contains("|")) {

										String[] bbb = bb.split("\\|");

										for (int jj = 0; jj < bbb.length; jj++) {

											String code = "barcode";
											item.setAttributeValue("Variant_ss/Barcode#" + jj + "/barcode_each_level",
													bbb[jj]);
											errorFlag = validateUniquenessOfBarcodes(ctx, inArgs, item, i, sbDebug, jj,
													code);


											if (!errorFlag) {

												barcodeList.add(bbb[jj]);
											}

										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/outer_pack_barcode")) {
									
									String ob = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!ob.isEmpty() && ob.contains("|")) {

										String[] obs = ob.split("\\|");

										for (int out = 0; out < obs.length; out++) {

											String code = "outercode";
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + out + "/outer_pack_barcode",
													obs[out]);
											errorFlag = validateUniquenessOfBarcodes(ctx, inArgs, item, i, sbDebug, out,
													code);

											if (!errorFlag) {

												barcodeList.add(obs[out]);
											}

										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Variant_ss/Packaging Attributes/inner_pack_barcode")) {
									
									String ib = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!ib.isEmpty() && ib.contains("|")) {

										String[] ibs = ib.split("\\|");

										for (int in = 0; in < ibs.length; in++) {

											String code = "innercode";
											item.setAttributeValue(
													"Variant_ss/Packaging Attributes#" + in + "/inner_pack_barcode",
													ibs[in]);
											errorFlag = validateUniquenessOfBarcodes(ctx, inArgs, item, i, sbDebug, in,
													code);


											if (!errorFlag) {

												barcodeList.add(ibs[in]);

											}

										}
									}
								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Item_ss")) {
									continue;

								} else if (sheet.getRow(i).getCell(7).getStringCellValue().equalsIgnoreCase("Item")
										&& sheet.getRow(0).getCell(z).getStringCellValue().contains("Product_c")
										&& sheet.getRow(0).getCell(z).getStringCellValue().contains("Variant_ss")) {
									continue;
								} else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Product_c/primaryvendor_name")) {
									Organization vendorCategory = null;
									
									OrganizationHierarchy vendorHierarchy = ctx.getOrganizationManager()
											.getOrganizationHierarchy("Vendor Organization Hierarchy");
									String vendorname = sheet.getRow(i).getCell(z).getStringCellValue();
									
									if (vendorHierarchy != null) {
										if (vendorname != null && !vendorname.isEmpty()) {
											vendorCategory = vendorHierarchy.getOrganizationByPrimaryKey(vendorname);
										}
									}
									if (vendorCategory != null) {
										item.mapToOrganization(vendorCategory);
									} else {
										errorFlag = true;
										sbDebug.append(
												i + "," + "" + "," + "FAILED" + "," + "Missing vendor ID" + "\n");

									}

								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue()
										.contains("Product Category Code")) {
									Category category = null;
									Hierarchy masterHierarchy = null;
									
									masterHierarchy = ctx.getHierarchyManager()
											.getHierarchy("Sally_Products_Hierarchy");
									String categoryCode = sheet.getRow(i).getCell(z).getStringCellValue();
									if (masterHierarchy != null) {
										if (categoryCode != null && !categoryCode.isEmpty()) {
											category = masterHierarchy.getCategoryByPrimaryKey(categoryCode);
										}
									}
									if (category != null) {
										item.mapToCategory(category);
									} else {
										errorFlag = true;
										sbDebug.append(
												i + "," + "" + "," + "FAILED" + "," + "Missing Category Code" + "\n");

									}

								}

								else if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Brand Code")) {
									Category brand = null;
									Hierarchy masterHierarchy = null;
									masterHierarchy = ctx.getHierarchyManager().getHierarchy("Brand_Hierarchy");
									String brandCode = sheet.getRow(i).getCell(z).getStringCellValue();
									if (masterHierarchy != null) {
										if (brandCode != null && !brandCode.isEmpty()) {
											brand = masterHierarchy.getCategoryByPrimaryKey(brandCode);
										}
									}
									if (brand != null) {
										item.mapToCategory(brand);
									} else {
										errorFlag = true;
										sbDebug.append(
												i + "," + "" + "," + "FAILED" + "," + "Missing Brand Code" + "\n");

									}

								}

								else {


									XSSFCell cell = sheet.getRow(i).getCell(z);

									if (cell != null) {
										
										String stringCellValue = sheet.getRow(i).getCell(z).getStringCellValue();
										if (!stringCellValue.isEmpty()) {
											item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
													sheet.getRow(i).getCell(z).getStringCellValue());

										}
									}

								}

							}

						}

					}

					if (itemValuePresent == true && errorFlag == false) {
						ExtendedValidationErrors extendedValidationErrors = item.save();

						if (extendedValidationErrors == null) {

							setBaseAndVariant(item);
							updateBarcodeLookupEntryWithEnterpriseID(item, barcodeList);
							successRecords++;
							sbResults.append(i + "," + item.getPrimaryKey() + "," + "SUCCESS" + "\n");
							logger.info("Row Number : " + i + " : SUCCESS Item : " + item.getPrimaryKey()
									+ " Saved Successfully");
						} else {
							List<ValidationError> errors = extendedValidationErrors.getErrors();

							for (ValidationError validationError : errors) {
								errorsCount++;

								sbDebug.append(i + "," + "" + "," + "FAILED" + "," + validationError.getMessage());
								writeResultsAndStats(i, "", validationError.getAttributeInstance().getPath() + " >>> "
										+ validationError.getMessage());

								logger.info("Row Number : " + i + " : FAILED Item : "
										+ " Save Failed With Validation error : " + validationError.getMessage());

							}

						}

					}

					// Setting progress bar for the import
					progress.setProgress(i * 100 / sheet.getPhysicalNumberOfRows());

					totalRecordsProcessed++;
				}

				workbook.close();
				writer.close();
			}

			logger.info("Import end ------");
		} catch (Exception e) {

			logger.info("Exception : " + e.getMessage());
			writeStackTraceInDebugFile(e, 0, 0, StringUtils.EMPTY);
			writeToStackFile();
		}

		// Sets statistics in the import job feed
		scriptStats.setLinesProcessedCount(totalRecordsProcessed);
		scriptStats.setItemsAddedCount(successRecords);
		scriptStats.setFailedValidationCount(totalRecordsProcessed - successRecords);
		scriptStats.setErrorCount(errorsCount);
		scriptStats.setErrorLineCount(totalRecordsProcessed - successRecords);

		// Job time stamps
		String jobEndTime = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(new Date());
		String timeTaken = getTimeTaken(startTime);
		sbResults.append("\n");
		sbResults.append("Job ended at: " + jobEndTime + "\n");
		sbResults.append("Time Taken: " + timeTaken);
		writeToResultsFile();

		sbDebug.append("\n");
		sbDebug.append("Total records processed: " + totalRecordsProcessed + "\n");
		sbDebug.append("Success records: " + successRecords + "\n");
		sbDebug.append("Failed records: " + (totalRecordsProcessed - successRecords) + "\n");
		sbDebug.append("Job ended at: " + jobEndTime + "\n");
		writeToDebugFile();

	}

	public static void updateBarcodeLookupEntryWithEnterpriseID(Item item, List<String> barcodeList) {
		logger.info("*** Start of function of updateBarcodeLookupEntryWithEnterpriseID ***");

		LookupTable barcodeLkp = ctx.getLookupTableManager().getLookupTable("Barcode_Lookup_Table");
		PIMCollection<LookupTableEntry> lkpEntries = barcodeLkp.getLookupTableEntries();
		for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			Object entObject = lookupTableEntry.getAttributeValue("Barcode_Lookup_Spec/enterprise_item_id");
			String entId = "";
			if (entObject != null) {
				entId = (String) entObject;
			}
			String lkpBarcode = lookupTableEntry.getKey();

			if (barcodeList.contains(lkpBarcode) && entId.isEmpty()) {
				lookupTableEntry.setAttributeValue("Barcode_Lookup_Spec/enterprise_item_id", item.getPrimaryKey());
				lookupTableEntry.save();
			}
		}

	}

	public static void setBaseAndVariant(Item item) {
		logger.info("*** Start of function of setBaseAndVariant ***");
		Object entityTypeObj = item.getAttributeValue("Product_c/entity_type");
		logger.info("Entity Type : " + entityTypeObj);
		String entityObj = "";
		if (entityTypeObj != null) {
			entityObj = entityTypeObj.toString();
		}
		String productName = (String) item.getAttributeValue("Product_c/product_name/en_GB");

		if (productName != null) {
			if (entityObj.equalsIgnoreCase("Item")) {
				hmBaseItemDetails.put(productName, item);
				
			} else if (entityObj.equalsIgnoreCase("Variant")) {
				
				if (!hmBaseItemDetails.isEmpty()) {

					if (productName.equalsIgnoreCase((String) hmBaseItemDetails.get(productName)
							.getAttributeValue("Product_c/product_name/en_GB"))) {
						List<? extends AttributeInstance> variantChildren = hmBaseItemDetails.get(productName)
								.getAttributeInstance("Item_ss/variants").getChildren();
						Integer variantSize = variantChildren.size();
						Item baseItem = hmBaseItemDetails.get(productName);
						
						List<Object> variantValues = new ArrayList<>();
						
						for (int i = 0; i < variantSize; i++) {

							variantValues.add(hmBaseItemDetails.get(productName).getAttributeValue("Item_ss/variants#" + i + "/variant_id"));
							
						}
							
							if(!variantValues.contains(item.getPrimaryKey())) {

								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_id", item.getPrimaryKey());
								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_colour",
										item.getAttributeValue("Variant_ss/variant_differentiators/colour"));
								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_size",
										item.getAttributeValue("Variant_ss/variant_differentiators/size"));
								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_style",
										item.getAttributeValue("Variant_ss/variant_differentiators/style"));
								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_strength",
										item.getAttributeValue("Variant_ss/variant_differentiators/strength"));
								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_fragrance",
										item.getAttributeValue("Variant_ss/variant_differentiators/fragrance"));
								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_type",
										item.getAttributeValue("Variant_ss/variant_differentiators/type"));
								baseItem.setAttributeValue("Item_ss/variants#" + variantSize + "/variant_configuration",
										item.getAttributeValue("Variant_ss/variant_differentiators/configuration"));

							}

						

						Catalog catalog = baseItem.getCatalog();
						ProcessingOptions processingOptions = catalog.getProcessingOptions();
						processingOptions.setAllProcessingOptions(false);
						baseItem.save();

						item.setAttributeValue("Variant_ss/base_item",
								hmBaseItemDetails.get(productName).getPrimaryKey());
						Catalog variantCatalog = item.getCatalog();
						ProcessingOptions processingOptions2 = variantCatalog.getProcessingOptions();
						processingOptions2.setAllProcessingOptions(false);
						item.save();
					}
				}
			}
		}
		logger.info("*** End of function of setBaseAndVariant ***");
	}

	/**
	 * Writes item's save status & errors into docstore
	 * (/Import/yyyy-MM-dd_HHmmSS.csv)
	 * 
	 * @param sbResults contains item's save status
	 */
	public void writeToResultsFile() {

		Document docResults = ctx.getDocstoreManager().createAndPersistDocument(resultsFile);
		docResults.setContent(sbResults.toString());
	}

	/**
	 * Writes import job results & exceptions (if any) into docstore
	 * (Import/yyyy-MM-dd_HHmmSS_debug.log)
	 * 
	 * @param sbDebug contains exception details if any
	 */
	public void writeToDebugFile() {

		Document docDebug = ctx.getDocstoreManager().createAndPersistDocument(debugFile);
		docDebug.setContent(sbDebug.toString());
	}

	/**
	 * Writes import job results & exceptions (if any) into docstore
	 * (Import/yyyy-MM-dd_HHmmSS_debug.log)
	 * 
	 * @param sbDebug contains exception details if any
	 */
	public void writeToStackFile() {

		Document docDebug = ctx.getDocstoreManager().createAndPersistDocument(stackFile);
		docDebug.setContent(sbStack.toString());
	}

	/**
	 * Calculates the time take to complete the job
	 * 
	 * @param startTime Job start time
	 * @return String Returns the time taken for job's completion
	 */
	public static String getTimeTaken(long startTime) {

		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;

		long days = TimeUnit.MILLISECONDS.toDays(elapsedTime);
		elapsedTime -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
		elapsedTime -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
		elapsedTime -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);

		return days + " Days " + hours + " Hours " + minutes + " Minutes " + seconds + " Seconds ";
	}

	/**
	 * 
	 * Initialises the job stats and writer
	 */
	private void initialiseJobStats() {

		if (null != scriptStats) {
			scriptStats.setItemsProcessedCount(0);
			scriptStats.setLinesProcessedCount(0);
			scriptStats.setErrorCount(0);
			scriptStats.setErrorLineCount(0);
			scriptStats.setFailedValidationCount(0);
			scriptStats.setItemsModifiedCount(0);
			scriptStats.setItemsNotFoundCount(0);
			scriptStats.setWarningCount(0);
		}

		if (null != writer) {
			try {
				writer.flush();
			}

			catch (IOException e) {
				logger.error("Exception While flushing the writer" + e.getMessage());
			}
		}
	}

	/**
	 * Writes exception to debug file in docstore
	 * 
	 * @param e            Exception
	 * @param rowNumber    row number in the import file
	 * @param primaryKey   New item's primary key
	 * @param pathAndValue Path and attribute path in each column of a row
	 * @return
	 */
	public void writeStackTraceInDebugFile(Exception e, int rowNumber, Object primaryKey, String pathAndValue) {

		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();
		logger.error(stackTrace);
		sbStack.append(rowNumber + "," + primaryKey + "," + "FAILED" + "," + pathAndValue + stackTrace + "\n");
	}

	/**
	 * Writes errors in the import results file and import job statistics
	 * 
	 * @param item
	 * @param primaryKey
	 * @param message
	 * @throws IOException
	 */
	public void writeResultsAndStats(int rowNumber, Object primaryKey, String message) throws IOException {

		// Write in the writer
		writer.append(rowNumber + "," + primaryKey + "," + "FAILED" + "," + message + "\n");
	}

	public static boolean validateUniquenessOfBarcodes(Context ctx, ImportFunctionArguments inArgs, Item item, int row,
			StringBuilder sbDebug, int barcodeSize, String code) {
		logger.info("*** Start of function of validateUniquenessOfBarcodesUpdated ***");
		logger.info("barcodeSize : " + barcodeSize);
		boolean errorflag = false;
		if (code == "barcode") {
			String itemPath = "Variant_ss/Barcode/barcode_each_level";
			String barcodeTypeEachLevel = (String) item
					.getAttributeValue("Variant_ss/Barcode#" + (barcodeSize) + "/barcode_type_each_level");

			String barcodeEachLevel = (String) item
					.getAttributeValue("Variant_ss/Barcode#" + (barcodeSize) + "/barcode_each_level");
			logger.info("****Barcode Each level****  " + barcodeEachLevel);
			logger.info("****Barcode Each level****  " + barcodeTypeEachLevel);
			if (barcodeTypeEachLevel != null || barcodeEachLevel != null) {
				errorflag = validateBarcodes(ctx, inArgs, item, barcodeEachLevel, barcodeTypeEachLevel, itemPath, row,
						sbDebug);
				logger.info("Error Flag Value***  " + errorflag);

			}
		}
		logger.info("*** End of function of validateUniquenessOfBarcodesUpdated ***");

		if (code == "innercode") {
			String innerPath = "Variant_ss/Packaging Attributes/inner_pack_barcode_type";
			String barcodeTypeInnerPack = (String) item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + (barcodeSize) + "/inner_pack_barcode_type");
			String innerPackBarcode = (String) item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + (barcodeSize) + "/inner_pack_barcode");

			logger.info("inner_pack_barcode : " + innerPackBarcode);
			logger.info("barcodeTypeInnerPack : " + barcodeTypeInnerPack);

			if (barcodeTypeInnerPack != null || innerPackBarcode != null) {
				errorflag = validateBarcodes(ctx, inArgs, item, innerPackBarcode, barcodeTypeInnerPack, innerPath, row,
						sbDebug);
				logger.info("Error Flag Value***  " + errorflag);
			}
		}

		if (code == "outercode") {
			String outerPath = "Variant_ss/Packaging Attributes/outer_pack_barcode";
			String barcodeTypeOuterPack = (String) item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + (barcodeSize) + "/outer_pack_barcode_type");
			String outerPackBarcode = (String) item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + (barcodeSize) + "/outer_pack_barcode");

			logger.info("outerPackBarcode : " + outerPackBarcode);
			logger.info("outer_pack_barcode_type : " + barcodeTypeOuterPack);

			if (barcodeTypeOuterPack != null || outerPackBarcode != null) {
				errorflag = validateBarcodes(ctx, inArgs, item, outerPackBarcode, barcodeTypeOuterPack, outerPath, row,
						sbDebug);
				logger.info("Error Flag Value***  " + errorflag);
			}
		}

		return errorflag;
	}

	public static boolean validateBarcodes(Context ctx, ImportFunctionArguments inArgs, Item item, String barcode,
			String barcodeType, String itemPath, int rows, StringBuilder sbDebug) {
		logger.info("*** Start of function of validateBarcodes ***");
		String enterpriseId = item.getPrimaryKey();
		logger.info("barcode : " + barcode + "   enterpriseId : " + enterpriseId);
		LookupTable barcodeLkp = ctx.getLookupTableManager().getLookupTable("Barcode_Lookup_Table");
		String lkpName = "Barcode_Lookup_Table";
		String path = "Barcode_Lookup_Spec/barcode";

		String sBarcodeQuery = "select item from catalog('" + lkpName + "') where item['" + path + "']  like '"
				+ barcode + "'";
		logger.info("sBarcodeQuery : " + sBarcodeQuery);
		SearchQuery searchBarcodeQuery = ctx.createSearchQuery(sBarcodeQuery);
		SearchResultSet searchBarcodeResult = searchBarcodeQuery.execute();
		boolean flag = false;
		boolean errorFlag = false;
		logger.info("searchBarcodeResult.size() updated : " + searchBarcodeResult.size());
		if (searchBarcodeResult.size() > 0) {
			PIMCollection<LookupTableEntry> lkpEntries = barcodeLkp.getLookupTableEntries();
			for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
				LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
				String entId = (String) lookupTableEntry.getAttributeValue("Barcode_Lookup_Spec/enterprise_item_id");
				String lkpBarcode = lookupTableEntry.getKey();
				logger.info("entId : " + entId);
				logger.info("enterpriseId.equalsIgnoreCase(entId) : " + enterpriseId.equalsIgnoreCase(entId));
				if (enterpriseId.equalsIgnoreCase(entId) && barcode.equalsIgnoreCase(lkpBarcode)) {
					flag = true;
				}
			}
			logger.info("flag : " + flag);
			if (flag == false) {
				errorFlag = true;
				try {
					sbDebug.append(rows + "," + "" + "," + "FAILED" + ","
							+ "Duplicate Barcode error : Barcode is already associated to another Enterprise ID in Catalog"
							+ "\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			errorFlag = validateCheckDigitOfBarcodes(ctx, inArgs, item, barcode, barcodeType, itemPath, rows, sbDebug);
			logger.info("errorFlag : " + errorFlag);
			if (!errorFlag) {
				logger.info("** Creates new Entry **");
				LookupTableEntry newEntry = barcodeLkp.createEntry();
				newEntry.setAttributeValue(path, barcode);
				newEntry.setAttributeValue("Barcode_Lookup_Spec/enterprise_item_id", enterpriseId);
				logger.info(newEntry.save());

			} else {
				return true;
			}
		}

		logger.info("*** End of function of validateBarcodes ***");
		return errorFlag;
	}

	public static Boolean validateCheckDigitOfBarcodes(Context ctx, ImportFunctionArguments arg0, Item item,
			String barcode, String barcodeType, String itemPath, int r, StringBuilder sbDebug) {
		logger.info("*** Start of function of validateCheckDigitOfBarcodes ***");
		logger.info("Barcode : " + barcode);
		logger.info("barcodeType : " + barcodeType);

		if (barcodeType.equalsIgnoreCase("EAN8")) {
			if (barcode.length() != 8) { // check to see if the input is 13 digits

				try {
					sbDebug.append(r + "," + "" + "," + "FAILED" + "," + "Barcode length should be 8 digits as per EAN8"
							+ "\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}

			int checkDigit = checkSumEAN8(barcode); // pass that input to the checkSum function
			Integer checkDigitInteger = new Integer(checkDigit);
			String result = checkDigitInteger.toString();
			if (result.charAt(0) != barcode.charAt(7)) {

				try {
					sbDebug.append(r + "," + "" + "," + "FAILED" + ","
							+ "Check digit is not correct as per EAN8 protocls. The check digit should be" + result
							+ "\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}
		}

		else if (barcodeType.equalsIgnoreCase("EAN13")) {
			if (barcode.length() != 13) { // check to see if the input is 13 digits

				try {
					sbDebug.append(r + "," + "" + "," + "FAILED" + ","
							+ "Barcode length should be 13 digits as per EAN13" + "\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}

			int checkDigit = checkSumEAN13(barcode); // pass that input to the checkSum function
			Integer checkDigitInteger = new Integer(checkDigit);
			String result = checkDigitInteger.toString();
			if (result.charAt(0) != barcode.charAt(12)) {

				try {
					sbDebug.append(r + "," + "" + "," + "FAILED" + ","
							+ "Check digit is not correct as per EAN13 protocls. The check digit should be" + result
							+ "\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}
		}

		logger.info("*** End of function of validateCheckDigitOfBarcodes ***");
		return false;
	}

	public static int checkSumEAN8(String code) {
		logger.info("*** Start of function of checkSumEAN8 ***");
		int sum1 = (int) code.charAt(1) + (int) code.charAt(3) + (int) code.charAt(5);
		int sum2 = 3 * ((int) code.charAt(0) + (int) code.charAt(2) + (int) code.charAt(4) + (int) code.charAt(6));

		int checksum_value = sum1 + sum2;

		int checksum_digit = 10 - (checksum_value % 10);
		if (checksum_digit == 10)
			checksum_digit = 0;

		logger.info("*** End of function of checkSumEAN8 ***");
		return checksum_digit;
	}

	public static int checkSumEAN13(String Input) {
		int evens = 0; // initialize evens variable
		int odds = 0; // initialize odds variable
		int checkSum = 0; // initialize the checkSum
		for (int i = 0; i < 12; i++) {// fixed because it is fixed in practices but you can use length() insted
			int digit = Integer.parseInt(Input.substring(i, i + 1));
			if (i % 2 == 0) {
				evens += digit;// then add it to the evens
			} else {
				odds += digit; // else add it to the odds
			}
		}
		odds = odds * 3; // multiply odds by three
		int total = odds + evens; // sum odds and evens
		if (total % 10 == 0) { // if total is divisible by ten, special case
			checkSum = 0;// checksum is zero
		} else { // total is not divisible by ten
			checkSum = 10 - (total % 10); // subtract the ones digit from 10 to find the checksum
		}
		return checkSum;
	}

}
