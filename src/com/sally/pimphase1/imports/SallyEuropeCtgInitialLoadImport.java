package com.sally.pimphase1.imports;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.imports.SallyEuropeCtgInitialLoadImport.class"

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.ibm.ccd.common.context.AustinContext;
import com.ibm.ccd.docstore.interfaces.IDoc;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ExtendedValidationErrors;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.common.exceptions.PIMInvalidOperationException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.extensionpoints.ImportFunction;
import com.ibm.pim.extensionpoints.ImportFunctionArguments;
import com.ibm.pim.hierarchy.Hierarchy;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;
import com.ibm.pim.spec.Spec;
import com.ibm.pim.system.PIMProgress;
import com.ibm.pim.system.ScriptStatistics;

public class SallyEuropeCtgInitialLoadImport implements ImportFunction {

	private static Logger logger = LogManager.getLogger(SallyEuropeCtgInitialLoadImport.class);
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
	public void doImport(ImportFunctionArguments arg0) {
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
			Document doc = arg0.getDocstoreDataDoc();
			// Document doc =
			// ctx.getDocstoreManager().getDocument("temp_reports/Initial_Load_Template.xlsx");

			sbResults = new StringBuilder();
			sbDebug = new StringBuilder();
			sbStack = new StringBuilder();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String todaysDate = sdf.format(calendar.getTime());
			PIMProgress progress = arg0.getProgress();

			String timeStamp = new SimpleDateFormat("HH.mm.ss").format(calendar.getTime());

			resultsFile = "temp_reports/SallyEuropeImport/Success/" + todaysDate + "_" + timeStamp + "_success.csv";
			debugFile = "temp_reports/SallyEuropeImport/Failure/" + todaysDate + "_" + timeStamp + "_failure" + ".csv";
			stackFile = "temp_reports/SallyEuropeImport/Debug/" + todaysDate + "_" + timeStamp + "_debug" + ".csv";

			scriptStats = arg0.getScriptStats();
			writer = arg0.getErrors().getWriter();
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

			LookupTable vendorLkpTable = ctx.getLookupTableManager().getLookupTable("Vendor Lookup Table");
			PIMCollection<LookupTableEntry> vendorLkpEntries = vendorLkpTable.getLookupTableEntries();
			Map<String, String> vendorLkpKeyValues = new HashMap<String, String>();

			for (Iterator<LookupTableEntry> iterator = vendorLkpEntries.iterator(); iterator.hasNext();) {
				LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
				String vendorName = lookupTableEntry.getKey();
				Object vendorID = lookupTableEntry.getAttributeValue("Vendor Lookup Spec/primaryvendor_id");

				if (vendorName != null) {
					vendorLkpKeyValues.put(vendorID.toString(), vendorName);
				}
			}

			if (doc != null) {

				localAustinContext = AustinContext.getCurrentContext();
				document = localAustinContext.getDocStoreMgr().get(doc.getPath(), false,
						AustinContext.getCurrentContext().getCompanyId(), true);
				file = document.getTmpFile();
				stream = new FileInputStream(file);

				XSSFWorkbook workbook = new XSSFWorkbook(stream);

				XSSFSheet sheet = workbook.getSheetAt(1);

				logger.info("Number of Rows : " + sheet.getPhysicalNumberOfRows());
				logger.info("Number of Columns : " + sheet.getRow(0).getPhysicalNumberOfCells());

				for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
					Boolean itemValuePresent = false;
					Boolean errorFlag = false;
					Item item = ctx.getCatalogManager().getCatalog("Sally Europe").createItem();
					for (int z = 0; z < sheet.getRow(0).getPhysicalNumberOfCells(); z++) {

						logger.info("Attribute: " + sheet.getRow(0).getCell(z).getStringCellValue());

						String stringFirstCellValue = sheet.getRow(i).getCell(0).getStringCellValue();

						if (stringFirstCellValue != null && stringFirstCellValue != "") {
							itemValuePresent = true;

							// Banner Hierarchy needs to be mapped
							Hierarchy itemtype = ctx.getHierarchyManager().getHierarchy("Banner Hierarchy");
							Category itemcategory = itemtype.getCategoryByPrimaryKey("SPC");

							if (Objects.nonNull(itemcategory)) {
								try {
									item.mapToCategory(itemcategory);
								} catch (PIMInvalidOperationException e) {
									e.printStackTrace();
								}
							}

							if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Category_code")) {
								Category category = null;
								Hierarchy masterHierarchy = null;

								masterHierarchy = ctx.getHierarchyManager().getHierarchy("Product Hierarchy");
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

							else if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Brand_code")) {
								Category brand = null;
								Hierarchy masterHierarchy = null;

								masterHierarchy = ctx.getHierarchyManager().getHierarchy("Brand Hierarchy");
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
									sbDebug.append(i + "," + "" + "," + "FAILED" + "," + "Missing Brand Code" + "/n");

								}

							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Search_name")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Date_added")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("UN_name")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Primary_vendor_name")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Sys_created_date")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Sys_created_by")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Sys_updated_date")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Sys_updated_by")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Artwork_and_packaging_images")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Vendors/Supplier_lead_time")) {
								continue;
								// Skipping auto populated attributes
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Electrical_ss")) {
								Collection<Spec> specs = item.getSpecs();
								for (Spec spec : specs) {

									if (spec.getName().equalsIgnoreCase("Electrical_ss")) {
										if (sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Specification/Spec_airflow")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_max_temp")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_min_temp")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_contents")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_watt")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_outer_diameter")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_inner_diameter")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Sepc_battery_OEM_qty")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_battery_weight")) {
											Double cellValue = sheet.getRow(i).getCell(z).getNumericCellValue();

											if (cellValue != null) {
												item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
														sheet.getRow(i).getCell(z).getNumericCellValue());
											}
										}

										if (sheet.getRow(0).getCell(z).getStringCellValue()
												.contains("Specification/Model_number")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_battery_chemical_family")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_battery_format")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_battery_removable")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_battery_OEM")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Specification/Spec_battery_rechargeable")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Type/Type_plug")
												|| sheet.getRow(0).getCell(z).getStringCellValue()
														.contains("Regulatory and Legal/WEEE")) {
											XSSFCell cell = sheet.getRow(i).getCell(z);

											if (cell != null) {
												logger.info("stringCellValue : "
														+ sheet.getRow(i).getCell(z).getStringCellValue());
												String stringCellValue = sheet.getRow(i).getCell(z)
														.getStringCellValue();
												if (!stringCellValue.isEmpty()) {
													item.setAttributeValue(
															sheet.getRow(0).getCell(z).getStringCellValue(),
															sheet.getRow(i).getCell(z).getStringCellValue());

												}
											}
										}

									}

								}
							}

							// Number attributes
							else if (sheet.getRow(0).getCell(z).getStringCellValue().contains("Dim_net_weight/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Dim_gross_height/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Dim_gross_width/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Dim_gross_depth/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue().contains("Base_cost/Price")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Packaging/Pack_inner_pack_quantity")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_inner_pack_height/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_inner_pack_width/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_inner_pack_depth/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_inner_pack_weight/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Packaging/Pack_outer_pack_quantity")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_outer_pack_height/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_outer_pack_width/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_outer_pack_depth/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Pack_outer_pack_weight/Value")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Warehouse Attributes/Outers_per_layer")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Warehouse Attributes/Layers_per_pallet")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Warehouse Attributes/Pallet_weight")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Vendors/Legacy_vendor_ID")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Vendors/Minimum_order_quantity")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Vendors/Production_lead_time")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Vendors/Transit_lead_time")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Usage/Use_period_after_opening")) {
								Double cellValue = sheet.getRow(i).getCell(z).getNumericCellValue();

								if (cellValue != null) {
									item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
											sheet.getRow(i).getCell(z).getNumericCellValue());
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/ERP Operational/ERP_legal_entities")) {

								String ERP_legal_entities = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!ERP_legal_entities.isEmpty()) {
									item.setAttributeValue("Product_c/ERP Operational/ERP_legal_entities#0",
											ERP_legal_entities);

								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Warehouse Attributes/Value_added_service_ID")) {
								Double cellValue = sheet.getRow(i).getCell(z).getNumericCellValue();

								if (cellValue != null) {
									item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
											cellValue.intValue());
								}

							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Regulatory and Legal/Commodity_code")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Vendors/Vendor_product_ID")) {

								Double cellValue = sheet.getRow(i).getCell(z).getNumericCellValue();
								logger.info("Raw Value : " + sheet.getRow(i).getCell(z).getRawValue());

								if (cellValue != null) {
									item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
											sheet.getRow(i).getCell(z).getRawValue());
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Vendors/Primary_vendor_ID")) {
								logger.info("Primary Vendor ID Attribute");
								String vendorID = sheet.getRow(i).getCell(z).getStringCellValue();
								logger.info("vendorID : " + vendorID);
								if (vendorID != null && !vendorID.isEmpty()) {

									String vendorNamePK = vendorLkpKeyValues.get(vendorID);
									logger.info("vendorNamePK : " + vendorNamePK);
									item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
											vendorNamePK);
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Packaging/Pack_inner_packaging_material/Material_type")) {

								String innerm = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!innerm.isEmpty()) {

									if (innerm.contains("|")) {
										String[] innerms = innerm.split("\\|");

										for (int jj = 0; jj < innerms.length; jj++) {

											item.setAttributeValue("Product_c/Packaging/Pack_inner_packaging_material#"
													+ jj + "/Material_type", innerms[jj]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/Packaging/Pack_inner_packaging_material#0/Material_type",
												innerm);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Packaging/Pack_inner_packaging_material/Value")) {

								String innermValues = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!innermValues.isEmpty()) {

									if (innermValues.contains("|")) {
										String[] innermsValue = innermValues.split("\\|");

										for (int jj = 0; jj < innermsValue.length; jj++) {

											item.setAttributeValue("Product_c/Packaging/Pack_inner_packaging_material#"
													+ jj + "/Value", innermsValue[jj]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/Packaging/Pack_inner_packaging_material#0/Value",
												innermValues);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Packaging/Pack_outer_packaging_material/Material_type")) {

								String outerm = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!outerm.isEmpty()) {

									if (outerm.contains("|")) {
										String[] outerms = outerm.split("\\|");

										for (int jj = 0; jj < outerms.length; jj++) {

											item.setAttributeValue("Product_c/Packaging/Pack_outer_packaging_material#"
													+ jj + "/Material_type", outerms[jj]);
										}
									} else {
										item.setAttributeValue(
												"Product_c/Packaging/Pack_outer_packaging_material#0/Material_type",
												outerm);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Packaging/Pack_outer_packaging_material/Value")) {

								String outermValues = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!outermValues.isEmpty()) {

									if (outermValues.contains("|")) {
										String[] outermsValue = outermValues.split("\\|");

										for (int jj = 0; jj < outermsValue.length; jj++) {

											item.setAttributeValue("Product_c/Packaging/Pack_outer_packaging_material#"
													+ jj + "/Value", outermsValue[jj]);
										}
									} else {
										item.setAttributeValue(
												"Product_c/Packaging/Pack_outer_packaging_material#0/Value",outermValues);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Barcodes/Pack_barcode_type")) {

								String barcode_type_each_level = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!barcode_type_each_level.isEmpty()) {

									if (barcode_type_each_level.contains("|")) {
										String[] barcode_type = barcode_type_each_level.split("\\|");

										for (int b = 0; b < barcode_type.length; b++) {
											item.setAttributeValue("Product_c/Barcodes#" + b + "/Pack_barcode_type",
													barcode_type[b]);
										}
									} else {
										item.setAttributeValue("Product_c/Barcodes#0/Pack_barcode_type",
												barcode_type_each_level);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Barcodes/Pack_barcode_number")) {

								String bb = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!bb.isEmpty()) {

									if (bb.contains("|")) {
										String[] bbb = bb.split("\\|");

										for (int jj = 0; jj < bbb.length; jj++) {

											item.setAttributeValue("Product_c/Barcodes#" + jj + "/Pack_barcode_number",
													bbb[jj]);
											errorFlag = validateUniquenessOfBarcodes(ctx, arg0, item, i, sbDebug, jj);

											if (!errorFlag) {

												barcodeList.add(bbb[jj]);
											}

										}
									} else {
										item.setAttributeValue("Product_c/Barcodes#0/Pack_barcode_number", bb);
										errorFlag = validateUniquenessOfBarcodes(ctx, arg0, item, i, sbDebug, 0);

										if (!errorFlag) {

											barcodeList.add(bb);
										}
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Barcodes/Pack_barcode_created_date")) {

								String barcode_date_created = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!barcode_date_created.isEmpty()) {

									if (barcode_date_created.contains("|")) {
										String[] barcode_date = barcode_date_created.split("\\|");

										for (int c = 0; c < barcode_date.length; c++) {
											item.setAttributeValue(
													"Product_c/Barcodes#" + c + "/Pack_barcode_created_date",
													barcode_date[c]);
										}
									}

									else {
										item.setAttributeValue("Product_c/Barcodes#0/Pack_barcode_created_date",
												barcode_date_created);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Barcodes/Pack_barcode_unit")) {

								String barcode_unit_value = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!barcode_unit_value.isEmpty()) {

									if (barcode_unit_value.contains("|")) {
										String[] barcode_unit = barcode_unit_value.split("\\|");

										for (int c = 0; c < barcode_unit.length; c++) {
											item.setAttributeValue("Product_c/Barcodes#" + c + "/Pack_barcode_unit",
													barcode_unit[c]);
										}
									}

									else {
										item.setAttributeValue("Product_c/Barcodes#0/Pack_barcode_unit",
												barcode_unit_value);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Regulatory and Legal/Ingredients/Ingredient")) {

								String ingredient_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!ingredient_values.isEmpty()) {

									if (ingredient_values.contains("|")) {
										String[] ingredient_value = ingredient_values.split("\\|");

										for (int c = 0; c < ingredient_value.length; c++) {
											item.setAttributeValue(
													"Product_c/Regulatory and Legal/Ingredients#" + c + "/Ingredient",
													ingredient_value[c]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/Regulatory and Legal/Ingredients#0/Ingredient",
												ingredient_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Regulatory and Legal/UN_number")) {

								String UN_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!UN_values.isEmpty()) {

									if (UN_values.contains("|")) {
										String[] UN_value = UN_values.split("\\|");

										for (int c = 0; c < UN_value.length; c++) {
											item.setAttributeValue("Product_c/Regulatory and Legal/UN_number#" + c,
													UN_value[c]);
										}
									}

									else {
										item.setAttributeValue("Product_c/Regulatory and Legal/UN_number#0", UN_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue().contains(
									"Product_c/Vendors/Country_specific_minimum_order_quantity/Minimum_order_quantity")) {

								String min_order_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!min_order_values.isEmpty()) {

									if (min_order_values.contains("|"))

									{
										String[] min_order_value = min_order_values.split("\\|");

										for (int c = 0; c < min_order_value.length; c++) {
											item.setAttributeValue(
													"Product_c/Vendors/Country_specific_minimum_order_quantity#" + c
															+ "/Minimum_order_quantity",
													min_order_value[c]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/Vendors/Country_specific_minimum_order_quantity#0/Minimum_order_quantity",
												min_order_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Vendors/Country_specific_minimum_order_quantity/Country")) {

								String country_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!country_values.isEmpty()) {

									if (country_values.contains("|")) {
										String[] country_value = country_values.split("\\|");

										for (int c = 0; c < country_value.length; c++) {
											item.setAttributeValue(
													"Product_c/Vendors/Country_specific_minimum_order_quantity#" + c
															+ "/Country",
													country_value[c]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/Vendors/Country_specific_minimum_order_quantity#0/Country",
												country_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/Regulatory and Legal/Instructions_languages")) {

								String inst_lang_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!inst_lang_values.isEmpty()) {

									if (inst_lang_values.contains("|")) {
										String[] inst_lang_value = inst_lang_values.split("\\|");

										for (int c = 0; c < inst_lang_value.length; c++) {
											item.setAttributeValue(
													"Product_c/Regulatory and Legal/Instructions_languages#" + c,
													inst_lang_value[c]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/Regulatory and Legal/Instructions_languages#0",
												inst_lang_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/ERP Operational/Legacy_item_ID/Legacy_item_ID")) {

								String legacyItemIds = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!legacyItemIds.isEmpty()) {

									if (legacyItemIds.contains("|")) {
										String[] legacyItemId = legacyItemIds.split("\\|");

										for (int jj = 0; jj < legacyItemId.length; jj++) {

											item.setAttributeValue("Product_c/ERP Operational/Legacy_item_ID#"
													+ jj + "/Legacy_item_ID", legacyItemId[jj]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/ERP Operational/Legacy_item_ID#0/Legacy_item_ID",legacyItemIds);
									}
								}
							}
							
							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Product_c/ERP Operational/Legacy_item_ID/Legacy_ERP")) {

								String legacyERPs = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!legacyERPs.isEmpty()) {

									if (legacyERPs.contains("|")) {
										String[] legacyERP = legacyERPs.split("\\|");

										for (int jj = 0; jj < legacyERP.length; jj++) {

											item.setAttributeValue("Product_c/ERP Operational/Legacy_item_ID#"
													+ jj + "/Legacy_ERP", legacyERP[jj]);
										}
									}

									else {
										item.setAttributeValue(
												"Product_c/ERP Operational/Legacy_item_ID#0/Legacy_ERP",legacyERPs);
									}
								}
							}
							
							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/en_GB")) {

								String USP_enGB_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_enGB_values.isEmpty()) {

									if (USP_enGB_values.contains("|")) {

										String[] USP_enGB_value = USP_enGB_values.split("\\|");
										for (int c = 0; c < USP_enGB_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/en_GB#" + c,
													USP_enGB_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/en_GB#0",
												USP_enGB_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/es_ES")) {

								String USP_esES_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_esES_values.isEmpty()) {

									if (USP_esES_values.contains("|")) {

										String[] USP_esES_value = USP_esES_values.split("\\|");
										for (int c = 0; c < USP_esES_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/es_ES#" + c,
													USP_esES_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/es_ES#0",
												USP_esES_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/da_DK")) {

								String USP_daDK_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_daDK_values.isEmpty()) {

									if (USP_daDK_values.contains("|")) {

										String[] USP_daDK_value = USP_daDK_values.split("\\|");
										for (int c = 0; c < USP_daDK_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/da_DK#" + c,
													USP_daDK_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/da_DK#0",
												USP_daDK_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/nl_NL")) {

								String USP_nlNL_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_nlNL_values.isEmpty()) {

									if (USP_nlNL_values.contains("|")) {

										String[] USP_nlNL_value = USP_nlNL_values.split("\\|");
										for (int c = 0; c < USP_nlNL_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/nl_NL#" + c,
													USP_nlNL_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/nl_NL#0",
												USP_nlNL_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/pl_PL")) {

								String USP_plPL_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_plPL_values.isEmpty()) {
									if (USP_plPL_values.contains("|")) {

										String[] USP_plPL_value = USP_plPL_values.split("\\|");
										for (int c = 0; c < USP_plPL_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/pl_PL#" + c,
													USP_plPL_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/pl_PL#0",
												USP_plPL_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/it_IT")) {

								String USP_itIT_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_itIT_values.isEmpty()) {
									if (USP_itIT_values.contains("|")) {

										String[] USP_itIT_value = USP_itIT_values.split("\\|");
										for (int c = 0; c < USP_itIT_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/it_IT#" + c,
													USP_itIT_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/it_IT#0",
												USP_itIT_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/pt_PT")) {

								String USP_ptPT_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_ptPT_values.isEmpty()) {
									if (USP_ptPT_values.contains("|")) {

										String[] USP_ptPT_value = USP_ptPT_values.split("\\|");
										for (int c = 0; c < USP_ptPT_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/pt_PT#" + c,
													USP_ptPT_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/pt_PT#0",
												USP_ptPT_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/fr_FR")) {

								String USP_frFR_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_frFR_values.isEmpty()) {
									if (USP_frFR_values.contains("|")) {

										String[] USP_frFR_value = USP_frFR_values.split("\\|");
										for (int c = 0; c < USP_frFR_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/fr_FR#" + c,
													USP_frFR_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/fr_FR#0",
												USP_frFR_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/USPs/USP_bullet_points/de_DE")) {

								String USP_deDE_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!USP_deDE_values.isEmpty()) {
									if (USP_deDE_values.contains("|")) {

										String[] USP_deDE_value = USP_deDE_values.split("\\|");
										for (int c = 0; c < USP_deDE_value.length; c++) {
											item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/de_DE#" + c,
													USP_deDE_value[c]);
										}
									}

									else {
										item.setAttributeValue("Sinelco_ss/USPs/USP_bullet_points/de_DE#0",
												USP_deDE_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue().contains(
									"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition/Catalogue_edition")) {

								String catEdition_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!catEdition_values.isEmpty()) {

									if (catEdition_values.contains("|")) {
										String[] catEdition_value = catEdition_values.split("\\|");

										for (int c = 0; c < catEdition_value.length; c++) {
											item.setAttributeValue(
													"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#" + c
															+ "/Catalogue_edition",
													catEdition_value[c]);
										}
									}

									else {
										item.setAttributeValue(
												"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#0/Catalogue_edition",
												catEdition_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition/Page_number")) {

								String pageNumber_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!pageNumber_values.isEmpty()) {

									if (pageNumber_values.contains("|")) {
										String[] pageNumber_value = pageNumber_values.split("\\|");

										for (int c = 0; c < pageNumber_value.length; c++) {
											item.setAttributeValue(
													"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#" + c
															+ "/Page_number",
													pageNumber_value[c]);
										}
									} else {
										item.setAttributeValue(
												"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#0/Page_number",
												pageNumber_values);

									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition/New")) {

								String new_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!new_values.isEmpty()) {

									if (new_values.contains("|")) {
										String[] new_value = new_values.split("\\|");

										for (int c = 0; c < new_value.length; c++) {
											item.setAttributeValue(
													"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#" + c
															+ "/New",
													new_value[c]);
										}
									} else {
										item.setAttributeValue(
												"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#0/New",
												new_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
									.contains("Sinelco_ss/Sinelco Collections/Cat_collection_reference")) {

								String collec_ref_values = sheet.getRow(i).getCell(z).getStringCellValue();
								if (!collec_ref_values.isEmpty()) {

									if (collec_ref_values.contains("|")) {
										String[] collec_ref_value = collec_ref_values.split("\\|");

										for (int c = 0; c < collec_ref_value.length; c++) {
											item.setAttributeValue(
													"Sinelco_ss/Sinelco Collections/Cat_collection_reference#" + c,
													collec_ref_value[c]);
										}
									} else {
										item.setAttributeValue(
												"Sinelco_ss/Sinelco Collections/Cat_collection_reference#0",
												collec_ref_values);
									}
								}
							}

							else if (sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Product_c/Status Attributes/Approval_date_supply_chain")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Product_c/Status Attributes/Approval_date_legal")
									|| sheet.getRow(0).getCell(z).getStringCellValue()
											.contains("Product_c/Status Attributes/Approval_date_ECOM")) {
								item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(), new Date());
							}

							else {

								logger.info("String attributes");

								XSSFCell cell = sheet.getRow(i).getCell(z);

								if (cell != null) {
									logger.info("stringCellValue : " + sheet.getRow(i).getCell(z).getStringCellValue());
									String stringCellValue = sheet.getRow(i).getCell(z).getStringCellValue();
									if (!stringCellValue.isEmpty()) {
										item.setAttributeValue(sheet.getRow(0).getCell(z).getStringCellValue(),
												sheet.getRow(i).getCell(z).getStringCellValue());

									}
								}

							}

						}

					}

					if (itemValuePresent == true && errorFlag == false) {
						ExtendedValidationErrors extendedValidationErrors = item.save();

						if (extendedValidationErrors == null) {

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

		if ((totalRecordsProcessed - successRecords) > 0) {
			sbDebug.append("\n");
			sbDebug.append("Total records processed: " + totalRecordsProcessed + "\n");
			sbDebug.append("Success records: " + successRecords + "\n");
			sbDebug.append("Failed records: " + (totalRecordsProcessed - successRecords) + "\n");
			sbDebug.append("Job ended at: " + jobEndTime + "\n");
			writeToDebugFile();
		}

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

	public static boolean validateUniquenessOfBarcodes(Context ctx, ImportFunctionArguments arg0, Item item, int row,
			StringBuilder sbDebug, int barcodeSize) {
		logger.info("*** Start of function of validateUniquenessOfBarcodesUpdated ***");
		logger.info("barcodeSize : " + barcodeSize);
		boolean errorflag = false;
		String itemPath = "Product_c/Barcodes/Pack_barcode_number";
		String barcodeTypeEachLevel = (String) item
				.getAttributeValue("Product_c/Barcodes#" + (barcodeSize) + "/Pack_barcode_type");

		String barcodeEachLevel = (String) item
				.getAttributeValue("Product_c/Barcodes#" + (barcodeSize) + "/Pack_barcode_number");
		logger.info("****Barcode Each level****  " + barcodeEachLevel);
		logger.info("****Barcode Type Each level****  " + barcodeTypeEachLevel);
		if (barcodeTypeEachLevel != null || barcodeEachLevel != null) {
			errorflag = validateBarcodes(ctx, arg0, item, barcodeEachLevel, barcodeTypeEachLevel, itemPath, row,
					sbDebug);
			logger.info("Error Flag Value***  " + errorflag);

		}

		logger.info("*** End of function of validateUniquenessOfBarcodesUpdated ***");
		return errorflag;
	}

	public static boolean validateBarcodes(Context ctx, ImportFunctionArguments arg0, Item item, String barcode,
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
			errorFlag = validateCheckDigitOfBarcodes(ctx, arg0, item, barcode, barcodeType, itemPath, rows, sbDebug);
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

		if (barcodeType != null)
		{
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
		}
		}

		logger.info("*** End of function of validateCheckDigitOfBarcodes ***");
		return false;
	}
}
