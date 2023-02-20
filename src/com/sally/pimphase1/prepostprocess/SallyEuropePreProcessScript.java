package com.sally.pimphase1.prepostprocess;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.prepostprocess.SallyEuropePreProcessScript.class"

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.*;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.item.BaseItem;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.common.exceptions.PIMInvalidOperationException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.CategoryPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.ItemPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.PrePostProcessingFunction;
import com.ibm.pim.hierarchy.Hierarchy;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.organization.Role;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;
import com.ibm.pim.spec.Spec;
import com.sally.pimphase1.common.Constants;
import com.vivisimo.gelato.stubs.ForEach;

public class SallyEuropePreProcessScript implements PrePostProcessingFunction {

	private static Logger logger = LogManager.getLogger(SallyEuropePreProcessScript.class);
	Context ctx = PIMContextFactory.getCurrentContext();

	String user = null;

	@Override
	public void prePostProcessing(ItemPrePostProcessingFunctionArguments arg0) {
		logger.info("Inside item preprocess***");
		Item item = arg0.getItem();

		LookupTable fileReaderLkpTable = ctx.getLookupTableManager().getLookupTable(Constants.PIM_CONFIGURATION);
		PIMCollection<LookupTableEntry> fileLkpEntries = fileReaderLkpTable.getLookupTableEntries();
		for (Iterator<LookupTableEntry> fileItr = fileLkpEntries.iterator(); fileItr.hasNext();) {
			LookupTableEntry fileLookupTableEntry = (LookupTableEntry) fileItr.next();
			if (fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_KEY).toString()
					.equalsIgnoreCase(Constants.USER)) {
				user = fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_VALUE).toString();
			}
		}

		if (item != null) {

			// AttributeInstance attributeInstance =
			// item.getAttributeInstance("Product_c/Regulatory and Legal/UN_number");
			AttributeInstance attributeInstance = item.getAttributeInstance(Constants.UN_NUMBER);
			// AttributeInstance unNameInstance =
			// item.getAttributeInstance("Product_c/Regulatory and Legal/UN_name");
			AttributeInstance unNameInstance = item.getAttributeInstance(Constants.UN_NAME);
			int size = attributeInstance.getChildren().size() - unNameInstance.getChildren().size();
			if (size > 0) {
				for (int z = 0; z < size; z++) {
					unNameInstance.addOccurrence();
				}
			}
			if (size < 0) {
				for (int z = 0; z < Math.abs(size); z++) {
					logger.info(unNameInstance.isMultiOccurrence());
					unNameInstance.getChildren().get(z).removeOccurrence();
				}
			}
			PIMCollection<LookupTableEntry> hazardousLkpEntries = ctx.getLookupTableManager()
					.getLookupTable("Hazardous Lookup Table").getLookupTableEntries();
			if (attributeInstance != null && attributeInstance.getChildren().size() > 0) {
				for (int x = 0; x < attributeInstance.getChildren().size(); x++) {
					for (Iterator<LookupTableEntry> iterator = hazardousLkpEntries.iterator(); iterator.hasNext();) {
						LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
						if (attributeInstance.getChildren().get(x).getValue() != null && attributeInstance.getChildren()
								.get(x).getValue().toString().equalsIgnoreCase(lookupTableEntry
										.getAttributeValue("Hazardous_Lookup_Spec/un_number").toString())) {
							// item.setAttributeValue("Product_c/Regulatory and Legal/UN_name#" + x,
							item.setAttributeValue(Constants.UN_NAME + "#" + x,
									lookupTableEntry.getAttributeValue("Hazardous_Lookup_Spec/un_name"));
							break;
						}
					}
				}
			}

			Collection<Category> itemCategories = item.getCategories();

			if (!itemCategories.isEmpty()) {
				for (Category category : itemCategories) {

					String hierName = category.getHierarchy().getName();
					logger.info("hierName >> " + hierName);

					// if (hierName.equals("Product Hierarchy")) {
					if (hierName.equals(Constants.PRODUCT_HIERARCHY)) {
						// Object catCode = category.getAttributeValue("Product_h/category_code");
						// Object catName = category.getAttributeValue("Product_h/category_name");
						Object catCode = category.getAttributeValue(Constants.CATEGORY_CODE);
						Object catName = category.getAttributeValue(Constants.CATEGORY_NAME);

						logger.info("catCode >> " + catCode);
						logger.info("catName >> " + catName);

						if (catCode != null) {

							// AttributeInstance ERPOperationalInst = item
							// .getAttributeInstance("Product_c/ERP Operational");
							AttributeInstance ERPOperationalInst = item.getAttributeInstance(Constants.ERP_OPERATIONAL);

							if (ERPOperationalInst != null) {

								// item.setAttributeValue("Product_c/ERP Operational/Category_code",
								// catCode.toString());
								// item.setAttributeValue("Product_c/ERP Operational/Category_name",
								// catName.toString());
								item.setAttributeValue(Constants.ERP_CATEGORY_CODE, catCode.toString());
								item.setAttributeValue(Constants.ERP_CATEGORY_NAME, catName.toString());

								logger.info("Product Category values set");

							}

						}
					}

					// if (hierName.equals("Brand Hierarchy")) {
					if (hierName.equals(Constants.BRAND_HIERARCHY)) {
						// Object catCode = category.getAttributeValue("Product_h/category_code");
						// Object catName = category.getAttributeValue("Product_h/category_name");
						Object catCode = category.getAttributeValue(Constants.CATEGORY_CODE);
						Object catName = category.getAttributeValue(Constants.CATEGORY_NAME);

						logger.info("catCode >> " + catCode);
						logger.info("catName >> " + catName);

						if (catCode != null) {

							// AttributeInstance ERPOperationalInst = item
							// .getAttributeInstance("Product_c/ERP Operational");
							AttributeInstance ERPOperationalInst = item.getAttributeInstance(Constants.ERP_OPERATIONAL);

							if (ERPOperationalInst != null) {

								// item.setAttributeValue("Product_c/ERP Operational/Brand_code",
								// catCode.toString());
								// item.setAttributeValue("Product_c/ERP Operational/Brand_name",
								// catName.toString());
								item.setAttributeValue(Constants.BRAND_CODE, catCode.toString());
								item.setAttributeValue(Constants.BRAND_NAME, catName.toString());

								logger.info("Brand Category values set");

							}

						}
					}

					// if (!(hierName.equals("Banner Hierarchy"))) {
					if (!(hierName.equals(Constants.BANNER_HIERARCHY))) {

						logger.info("Hierarchy name is not banner hier so map it catalog");

						Hierarchy bannerHierarchy = ctx.getHierarchyManager().getHierarchy(Constants.BANNER_HIERARCHY);
						// Hierarchy bannerHierarchy = ctx.getHierarchyManager().getHierarchy("Banner
						// Hierarchy");
						// String currentUserName = ctx.getCurrentUser().getName();
						// String currentUserRoles = ctx.getCurrentUser().getName();

						PIMCollection<Role> currentUserRoles = ctx.getCurrentUser().getRoles();
						String currentUserName = ctx.getCurrentUser().getName();

						for (Role role : currentUserRoles) {
							// if((role.getName().equals("Category Manager")) ||
							// (currentUserName.equals("Admin"))) {
							if ((role.getName().equals(Constants.CATEGORY_MANAGER)) || (currentUserName.equals(user))) {

								if (bannerHierarchy != null) {
									// Category sinelcoCategory = bannerHierarchy.getCategoryByPrimaryKey("SPC");
									Category sinelcoCategory = bannerHierarchy.getCategoryByPrimaryKey(Constants.SPC);
									try {
										item.mapToCategory(sinelcoCategory);
									} catch (PIMInvalidOperationException e) {
										// TODO Auto-generated catch block
										logger.info("Exception : " + e.getMessage());
										e.printStackTrace();
									}

								}

							}
						}
					}

				}
			}

			// Categories empty
			else {

				// Map to Banner Hierarchy Sinelco when User is brandBuilder
				// Hierarchy bannerHierarchy = ctx.getHierarchyManager().getHierarchy("Banner
				// Hierarchy");
				Hierarchy bannerHierarchy = ctx.getHierarchyManager().getHierarchy(Constants.BANNER_HIERARCHY);
				String currentUserName = ctx.getCurrentUser().getName();

				logger.info("currentUserName >> " + currentUserName);

				// if (currentUserName.equals("BrandBuilder")) {
				if (currentUserName.equals(Constants.BRANDBUILDER)) {

					if (bannerHierarchy != null) {

						Category sinelcoCategory = bannerHierarchy.getCategoryByPrimaryKey("SPC");

						logger.info("Map to Banner Hierarchy");
						try {
							item.mapToCategory(sinelcoCategory);
						} catch (PIMInvalidOperationException e) {
							// TODO Auto-generated catch block
							logger.info("Exception : " + e.getMessage());
							e.printStackTrace();
						}

					}

				}

				// Map to Product Hierarchy based on Category code Attribute Value
				// Object catCode = item.getAttributeValue("Product_c/ERP
				// Operational/Category_code");
				Object catCode = item.getAttributeValue(Constants.ERP_CATEGORY_CODE);

				if (catCode != null) {
					// Hierarchy productHierarchy = ctx.getHierarchyManager().getHierarchy("Product
					// Hierarchy");
					Hierarchy productHierarchy = ctx.getHierarchyManager().getHierarchy(Constants.PRODUCT_HIERARCHY);
					Category productCategoryObj = productHierarchy.getCategoryByPrimaryKey(catCode.toString());

					if (productCategoryObj != null) {
						// Object catName =
						// productCategoryObj.getAttributeValue("Product_h/category_name");
						Object catName = productCategoryObj.getAttributeValue(Constants.CATEGORY_NAME);

						try {
							item.mapToCategory(productCategoryObj);
						} catch (PIMInvalidOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// Object itemCategoryName = item.getAttributeValue("Product_c/ERP
						// Operational/Category_name");
						Object itemCategoryName = item.getAttributeValue(Constants.ERP_CATEGORY_NAME);
						if (itemCategoryName == null) {
							// item.setAttributeValue("Product_c/ERP Operational/Category_name", catName);
							item.setAttributeValue(Constants.ERP_CATEGORY_NAME, catName);
						}
					}
				}

				// Map to Brand Hierarchy based on brand code Attribute Value
				// Object brandCode = item.getAttributeValue("Product_c/ERP
				// Operational/Brand_code");
				Object brandCode = item.getAttributeValue(Constants.BRAND_CODE);

				if (brandCode != null) {
					// Hierarchy brandHierarchy = ctx.getHierarchyManager().getHierarchy("Brand
					// Hierarchy");
					Hierarchy brandHierarchy = ctx.getHierarchyManager().getHierarchy(Constants.BRAND_HIERARCHY);
					Category brandCategoryObj = brandHierarchy.getCategoryByPrimaryKey(brandCode.toString());

					if (brandCategoryObj != null) {

						try {
							item.mapToCategory(brandCategoryObj);
						} catch (PIMInvalidOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Object brandCatName = brandCategoryObj.getAttributeValue(Constants.CATEGORY_NAME);
						Object itemBrandName = item.getAttributeValue(Constants.BRAND_NAME);

						if (itemBrandName == null) {
							item.setAttributeValue(Constants.BRAND_NAME, brandCatName);
						}
					}
				}

			}

		}

		validationsCtgItem(arg0, item);

	}

	@Override
	public void prePostProcessing(CategoryPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prePostProcessing(CollaborationItemPrePostProcessingFunctionArguments arg0) {
		CollaborationItem item = arg0.getCollaborationItem();

		LookupTable fileReaderLkpTable = ctx.getLookupTableManager().getLookupTable(Constants.PIM_CONFIGURATION);
		PIMCollection<LookupTableEntry> fileLkpEntries = fileReaderLkpTable.getLookupTableEntries();
		for (Iterator<LookupTableEntry> fileItr = fileLkpEntries.iterator(); fileItr.hasNext();) {
			LookupTableEntry fileLookupTableEntry = (LookupTableEntry) fileItr.next();
			if (fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_KEY).toString()
					.equalsIgnoreCase(Constants.USER)) {
				user = fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_VALUE).toString();
			}
		}

		if (item != null) {

			AttributeInstance attributeInstance = item.getAttributeInstance(Constants.UN_NUMBER);
			AttributeInstance unNameInstance = item.getAttributeInstance(Constants.UN_NAME);

			int size = attributeInstance.getChildren().size() - unNameInstance.getChildren().size();
			if (size > 0) {
				for (int z = 0; z < size; z++) {
					unNameInstance.addOccurrence();
				}
			}
			if (size < 0) {
				logger.info("Negative Size is : " + Math.abs(size));
				for (int z = 0; z < Math.abs(size); z++) {
					logger.info(unNameInstance.isMultiOccurrence());
					unNameInstance.getChildren().get(z).removeOccurrence();
				}
			}
			PIMCollection<LookupTableEntry> hazardousLkpEntries = ctx.getLookupTableManager()
					.getLookupTable("Hazardous Lookup Table").getLookupTableEntries();
			if (attributeInstance != null && attributeInstance.getChildren().size() > 0) {
				for (int x = 0; x < attributeInstance.getChildren().size(); x++) {
					for (Iterator<LookupTableEntry> iterator = hazardousLkpEntries.iterator(); iterator.hasNext();) {
						LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
						if (attributeInstance.getChildren().get(x).getValue() != null && attributeInstance.getChildren()
								.get(x).getValue().toString().equalsIgnoreCase(lookupTableEntry
										.getAttributeValue("Hazardous_Lookup_Spec/un_number").toString())) {
							item.setAttributeValue(Constants.UN_NAME + "#" + x,
									lookupTableEntry.getAttributeValue("Hazardous_Lookup_Spec/un_name"));
							break;
						}
					}
				}
			}

			if (arg0.getCollaborationStep() != null) {

				if (arg0.getCollaborationStep().getName().equalsIgnoreCase("01 Create Items")
						|| arg0.getCollaborationStep().getName().equalsIgnoreCase("01 Enrich Item Data")) {

					Collection<Category> itemCategories = item.getCategories();

					if (!itemCategories.isEmpty()) {
						for (Category category : itemCategories) {

							String hierName = category.getHierarchy().getName();
							if (hierName.equals(Constants.PRODUCT_HIERARCHY)) {

								Object catCode = category.getAttributeValue(Constants.CATEGORY_CODE);
								Object catName = category.getAttributeValue(Constants.CATEGORY_NAME);

								logger.info("catCode >> " + catCode);
								logger.info("catName >> " + catName);

								if (catCode != null) {

									AttributeInstance ERPOperationalInst = item
											.getAttributeInstance(Constants.ERP_OPERATIONAL);

									if (ERPOperationalInst != null) {

										item.setAttributeValue(Constants.ERP_CATEGORY_CODE, catCode.toString());
										item.setAttributeValue(Constants.ERP_CATEGORY_NAME, catName.toString());

										logger.info("Product Category values set");

									}

								}
							}

							if (hierName.equals(Constants.BRAND_HIERARCHY)) {

								Object catCode = category.getAttributeValue(Constants.CATEGORY_CODE);
								Object catName = category.getAttributeValue(Constants.CATEGORY_NAME);

								logger.info("catCode >> " + catCode);
								logger.info("catName >> " + catName);

								if (catCode != null) {

									AttributeInstance ERPOperationalInst = item
											.getAttributeInstance(Constants.ERP_OPERATIONAL);

									if (ERPOperationalInst != null) {

										item.setAttributeValue(Constants.BRAND_CODE, catCode.toString());
										item.setAttributeValue(Constants.BRAND_NAME, catName.toString());

										logger.info("Brand Category values set");

									}

								}
							}

							// if (!(hierName.equals("Banner Hierarchy"))) {
							if (!(hierName.equals(Constants.BANNER_HIERARCHY))) {

								logger.info("Hierarchy name is not banner hier so map it");

								Hierarchy bannerHierarchy = ctx.getHierarchyManager()
										.getHierarchy(Constants.BANNER_HIERARCHY);

								PIMCollection<Role> currentUserRoles = ctx.getCurrentUser().getRoles();
								String currentUserName = ctx.getCurrentUser().getName();

								for (Role role : currentUserRoles) {

									if ((role.getName().equals(Constants.CATEGORY_MANAGER))
											|| (currentUserName.equals(user))) {

										if (bannerHierarchy != null) {

											Category sinelcoCategory = bannerHierarchy
													.getCategoryByPrimaryKey(Constants.SPC);
											try {
												item.mapToCategory(sinelcoCategory);
											} catch (PIMInvalidOperationException e) {
												// TODO Auto-generated catch block
												logger.info("Exception : " + e.getMessage());
												e.printStackTrace();
											}

										}

									}
								}
							}

						}
					}

					// Categories empty
					else {

						// Map to Banner Hierarchy Sinelco when User is brandBuilder

						Hierarchy bannerHierarchy = ctx.getHierarchyManager().getHierarchy(Constants.BANNER_HIERARCHY);
						String currentUserName = ctx.getCurrentUser().getName();

						logger.info("currentUserName >> " + currentUserName);

						if (currentUserName.equals(Constants.BRANDBUILDER)) {

							if (bannerHierarchy != null) {

								Category sinelcoCategory = bannerHierarchy.getCategoryByPrimaryKey(Constants.SPC);

								try {
									item.mapToCategory(sinelcoCategory);
								} catch (PIMInvalidOperationException e) {
									// TODO Auto-generated catch block
									logger.info("Exception : " + e.getMessage());
									e.printStackTrace();
								}

							}

						}

					}

					// Map to Product Hierarchy based on Category code Attribute Value

					Object catCode = item.getAttributeValue(Constants.ERP_CATEGORY_CODE);
					logger.info("catCOd11e >> " + catCode);
					if (catCode != null) {

						Hierarchy productHierarchy = ctx.getHierarchyManager()
								.getHierarchy(Constants.PRODUCT_HIERARCHY);
						Category productCategoryObj = productHierarchy.getCategoryByPrimaryKey(catCode.toString());

						if (productCategoryObj != null) {
							// Object catName =
							// productCategoryObj.getAttributeValue("Product_h/category_name");
							Object catName = productCategoryObj.getAttributeValue(Constants.CATEGORY_NAME);

							try {
								item.mapToCategory(productCategoryObj);
							} catch (PIMInvalidOperationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// Object itemCategoryName = item.getAttributeValue("Product_c/ERP
							// Operational/Category_name");
							Object itemCategoryName = item.getAttributeValue(Constants.ERP_CATEGORY_NAME);
							if (itemCategoryName == null) {
								// item.setAttributeValue("Product_c/ERP Operational/Category_name", catName);
								item.setAttributeValue(Constants.ERP_CATEGORY_NAME, catName);
							}
						}
					}

					// Map to Brand Hierarchy based on brand code Attribute Value
					// Object brandCode = item.getAttributeValue("Product_c/ERP
					// Operational/Brand_code");
					Object brandCode = item.getAttributeValue(Constants.BRAND_CODE);

					if (brandCode != null) {
						// Hierarchy brandHierarchy = ctx.getHierarchyManager().getHierarchy("Brand
						// Hierarchy");
						Hierarchy brandHierarchy = ctx.getHierarchyManager().getHierarchy(Constants.BRAND_HIERARCHY);
						Category brandCategoryObj = brandHierarchy.getCategoryByPrimaryKey(brandCode.toString());

						if (brandCategoryObj != null) {

							try {
								item.mapToCategory(brandCategoryObj);
							} catch (PIMInvalidOperationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// Object brandCatName =
							// brandCategoryObj.getAttributeValue("Product_h/category_name");
							// Object itemBrandName = item.getAttributeValue("Product_c/ERP
							// Operational/Brand_name");

							Object brandCatName = brandCategoryObj.getAttributeValue(Constants.CATEGORY_NAME);
							Object itemBrandName = item.getAttributeValue(Constants.BRAND_NAME);

							if (itemBrandName == null) {
								// item.setAttributeValue("Product_c/ERP Operational/Brand_name", brandCatName);
								item.setAttributeValue(Constants.BRAND_NAME, brandCatName);
							}
						}
					}

					// AttributeInstance barcodeInst =
					// item.getAttributeInstance("Product_c/Barcodes");
					AttributeInstance barcodeInst = item.getAttributeInstance(Constants.BARCODES);

					if (barcodeInst != null) {
						// int barcodeSize =
						// item.getAttributeInstance("Product_c/Barcodes").getChildren().size();
						int barcodeSize = item.getAttributeInstance(Constants.BARCODES).getChildren().size();

						logger.info("barcodeSize : " + barcodeSize);

						if (barcodeSize > 0) {

							for (int i = 0; i < barcodeSize; i++) {

								// String attrPath = "Product_c/Barcodes#" + i + "/Pack_barcode_number";
								String attrPath = Constants.BARCODES + "#" + i + "/Pack_barcode_number";
								// String barcodeType = (String) item.getAttributeValue("Product_c/Barcodes#" +
								// i + "/Pack_barcode_type");
								String barcodeType = (String) item
										.getAttributeValue(Constants.BARCODES + "#" + i + "/Pack_barcode_type");

								logger.info("barcodeType : " + barcodeType);

								// Object barcodeNumObj = item.getAttributeValue("Product_c/Barcodes#" + i +
								// "/Pack_barcode_number");
								Object barcodeNumObj = item
										.getAttributeValue(Constants.BARCODES + "#" + i + "/Pack_barcode_number");

								logger.info("barcodeNumObj : " + barcodeNumObj);
								String barcodeNumber = "";

								if (barcodeNumObj != null) {
									// barcodeNumber = (String) item.getAttributeValue("Product_c/Barcodes#" + i +
									// "/Pack_barcode_number");
									barcodeNumber = (String) item
											.getAttributeValue(Constants.BARCODES + "#" + i + "/Pack_barcode_number");

									logger.info("barcodeNumber>> : " + barcodeNumber);
									if (barcodeNumber.contains("E") || barcodeNumber.contains(".")) {
										String substring = barcodeNumber.substring(0, barcodeNumber.lastIndexOf("E"));
										String updatedBarcode = substring.replace(".", "");

										if (!barcodeNumber.equals(updatedBarcode)) {
											barcodeNumber = updatedBarcode;

											// item.setAttributeValue("Product_c/Barcodes#" + i +
											// "/Pack_barcode_number",updatedBarcode);
											item.setAttributeValue(
													Constants.BARCODES + "#" + i + "/Pack_barcode_number",
													updatedBarcode);

										}
									}

								}

								logger.info("barcodeNumAfterUpdate : " + barcodeNumber);
								if (barcodeType != null || barcodeNumObj != null) {
									validateCheckDigitOfBarcodes(ctx, arg0, item, barcodeNumber, barcodeType, attrPath);
									validateBarcodes(ctx, arg0, item, barcodeNumber, attrPath);
								}
							}
						}
					}

					Collection<Category> categories = item.getCategories();
					ArrayList<String> hierNames = new ArrayList<>();
					for (Category category : categories) {

						String hierName = category.getHierarchy().getName();
						hierNames.add(hierName);

					}

					// if (!hierNames.contains("Brand Hierarchy")) {
					if (!hierNames.contains(Constants.BRAND_HIERARCHY)) {
						// arg0.addValidationError(item.getAttributeInstance("Product_c/Sys_PIM_item_ID"),
						arg0.addValidationError(item.getAttributeInstance(Constants.SYSTEM_PIM_ID),
								ValidationError.Type.VALIDATION_RULE, "Brand Hierarchy is not mapped to the Item");
					}
				}
			}
		}
		validationsCollabItem(arg0, item);
	}

	private void validationsCollabItem(CollaborationItemPrePostProcessingFunctionArguments arg0,
			CollaborationItem item) {

		if (item != null) {

			if (arg0.getCollaborationStep() != null) {

				if (arg0.getCollaborationStep().getName().equalsIgnoreCase("01 Enrich Item Data")) {
					// Item Type mandatory validations
					// Object itemTypeValue =
					// item.getAttributeValue("Product_c/Type/Type_item_type");
					Object itemTypeValue = item.getAttributeValue(Constants.ITEM_TYPE);
					logger.info("itemTypeValue >> " + itemTypeValue);

					ArrayList<String> attrPaths = new ArrayList<String>();

					/*
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_quantity");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/UOM");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/UOM");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/UOM");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/UOM");
					 * 
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_quantity");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/UOM");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/UOM");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/UOM");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/Value");
					 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/UOM");
					 * 
					 * attrPaths.add("Product_c/Regulatory and Legal/Country_of_origin");
					 * attrPaths.add("Product_c/Regulatory and Legal/Country_of_manufacture");
					 */

					attrPaths.add(Constants.PACKAGING_INNER_PACK_QTY);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_HEIGHT_VALUE);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_HEIGHT_UOM);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_WIDTH_VALUE);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_WIDTH_UOM);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_DEPTH_VALUE);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_DEPTH_UOM);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_WEIGHT_VALUE);
					attrPaths.add(Constants.PACKAGING_INNER_PACK_WEIGHT_UOM);

					attrPaths.add(Constants.PACKAGING_OUTER_PACK_QTY);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_HEIGHT_VALUE);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_HEIGHT_UOM);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_WIDTH_VALUE);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_WIDTH_UOM);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_DEPTH_VALUE);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_DEPTH_UOM);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_WEIGHT_VALUE);
					attrPaths.add(Constants.PACKAGING_OUTER_PACK_WEIGHT_UOM);

					attrPaths.add(Constants.COUNTRY_OF_ORIGIN);
					attrPaths.add(Constants.COUNTRY_OF_MANUFACTURE);

					if (itemTypeValue != null) {
						if (itemTypeValue.toString().equals("Item")) {

							for (String attrPath : attrPaths) {

								AttributeInstance attrInst = item.getAttributeInstance(attrPath);

								if (attrInst != null) {
									if (item.getAttributeValue(attrPath) == null) {
										logger.info("Throw error");

										arg0.addValidationError(item.getAttributeInstance(attrPath),
												ValidationError.Type.VALIDATION_RULE,
												"This field is mandatory when item type is Item");

									}
								}

							}

						}
					}

					// Legal Regulatory Attribute Validations
					// AttributeInstance legalAttributeInstance =
					// item.getAttributeInstance("Product_c/Regulatory and
					// Legal/Legal_classification");
					AttributeInstance legalAttributeInstance = item
							.getAttributeInstance(Constants.LEGAL_CLASSIFICATION);

					if (legalAttributeInstance != null) {
						// Object legalClassificationValue =
						// item.getAttributeValue("Product_c/Regulatory and
						// Legal/Legal_classification");
						Object legalClassificationValue = item.getAttributeValue(Constants.LEGAL_CLASSIFICATION);

						logger.info("legalClassificationValue >> " + legalClassificationValue);
						if (legalClassificationValue != null && (legalClassificationValue.equals("Cosmetics leave on")
								|| legalClassificationValue.equals("Cosmetics wash off")
								|| legalClassificationValue.equals("Aerosols")
								|| legalClassificationValue.equals("Biocide")
								|| legalClassificationValue.equals("Electrical"))) {

							logger.info("legal parent path >> " + legalAttributeInstance.getParent().getPath());

							String safetyDateSheetAttrPath = legalAttributeInstance.getParent().getPath()
									+ "/Safety_data_sheet";

							if (item.getAttributeValue(safetyDateSheetAttrPath) == null) {

								arg0.addValidationError(item.getAttributeInstance(safetyDateSheetAttrPath),
										ValidationError.Type.VALIDATION_RULE,
										"Safety Data Sheet is mandatory for the legal classification value selected");
								logger.info("Safety Data sheet error");

							}
						}

						if (legalClassificationValue != null && (legalClassificationValue.equals("Cosmetics leave on")
								|| legalClassificationValue.equals("Cosmetics wash off")
								|| legalClassificationValue.equals("Aerosols")
								|| legalClassificationValue.equals("Biocide")
								|| legalClassificationValue.equals("Food supplements")
								|| legalClassificationValue.equals("Detergents"))) {

							String ingredientPath = legalAttributeInstance.getParent().getPath()
									+ "/Ingredients/Ingredient";
							if (item.getAttributeInstance(ingredientPath) != null
									&& item.getAttributeValue(ingredientPath) == null) {

								arg0.addValidationError(item.getAttributeInstance(ingredientPath),
										ValidationError.Type.VALIDATION_RULE,
										"Expiry Type is mandatory for the legal classification value selected");
								logger.info("Expiry Date PAO error");
							}
						}

						if (legalClassificationValue != null && (legalClassificationValue.equals("Cosmetics leave on")
								|| legalClassificationValue.equals("Cosmetics wash off")
								|| legalClassificationValue.equals("Aerosols")
								|| legalClassificationValue.equals("Biocide")
								|| legalClassificationValue.equals("Food supplements")
								|| legalClassificationValue.equals("Medical device")
								|| legalClassificationValue.equals("PPE"))) {

							String expiryDatePAOAttrPath = legalAttributeInstance.getParent().getPath()
									+ "/Expiry_type";
							if (item.getAttributeInstance(expiryDatePAOAttrPath) != null
									&& item.getAttributeValue(expiryDatePAOAttrPath) == null) {

								arg0.addValidationError(item.getAttributeInstance(expiryDatePAOAttrPath),
										ValidationError.Type.VALIDATION_RULE,
										"Expiry Type is mandatory for the legal classification value selected");
								logger.info("Expiry Date PAO error");
							}
						}
					}

					// AttributeInstance legalQAttrInstance =
					// item.getAttributeInstance("Product_c/Regulatory and Legal/QPBUNMLGR");
					AttributeInstance legalQAttrInstance = item.getAttributeInstance(Constants.QPBUNMLGR);

					if (legalQAttrInstance != null) {
						// Object legalQAttrValue = item.getAttributeValue("Product_c/Regulatory and
						// Legal/QPBUNMLGR");
						Object legalQAttrValue = item.getAttributeValue(Constants.QPBUNMLGR);

						if (legalQAttrValue != null && (legalQAttrValue.equals("g") || legalQAttrValue.equals("ml"))) {

							String legalQAttrPath = legalQAttrInstance.getParent().getPath() + "/QPBQTYMLGR";
							if (item.getAttributeInstance(legalQAttrPath) != null
									&& item.getAttributeValue(legalQAttrPath) == null) {

								arg0.addValidationError(item.getAttributeInstance(legalQAttrPath),
										ValidationError.Type.VALIDATION_RULE,
										"This field is mandatory for the QPBUNMLGR value selected");
								logger.info("Expiry Date PAO error");
							}
						}

					}

					// Packaging material validation

					// Inner Packaging
					// AttributeInstance innerPackInstance =
					// item.getAttributeInstance("Product_c/Packaging/Pack_inner_packaging_material");
					AttributeInstance innerPackInstance = item
							.getAttributeInstance(Constants.PACKAGING_INNER_PACKAGING_MATERIAL);

					if (innerPackInstance != null) {
						logger.info("Inner Pack Instance not null");
						List<? extends AttributeInstance> innerPackChildren = innerPackInstance.getChildren();

						HashSet<String> hs = new HashSet<String>();

						for (AttributeInstance innerPackChildInst : innerPackChildren) {

							Object materialTypeValue = item
									.getAttributeValue(innerPackChildInst.getPath() + "/Material_type");

							logger.info("materialTypeValue >> " + materialTypeValue);

							if (materialTypeValue != null) {
								if (!hs.contains(materialTypeValue.toString())) {
									hs.add(materialTypeValue.toString());
								}

								else {
									arg0.addValidationError(
											item.getAttributeInstance(innerPackChildInst.getPath() + "/Material_type"),
											ValidationError.Type.VALIDATION_RULE,
											"Inner Packaging Material Type Cannot be duplicated");
									logger.info("Inner Packaging Material Type Cannot be duplicated");
								}
							}

						}
					}

					// Outer Packaging
					AttributeInstance outerPackInstance = item
							.getAttributeInstance(Constants.PACKAGING_OUTER_PACKAGING_MATERIAL);

					if (outerPackInstance != null) {
						logger.info("Inner Pack Instance not null");
						List<? extends AttributeInstance> outerPackChildren = outerPackInstance.getChildren();

						HashSet<String> hs = new HashSet<String>();

						for (AttributeInstance outerPackChildInst : outerPackChildren) {

							Object materialTypeValue = item
									.getAttributeValue(outerPackChildInst.getPath() + "/Material_type");

							logger.info("materialTypeValue >> " + materialTypeValue);

							if (materialTypeValue != null) {
								if (!hs.contains(materialTypeValue.toString())) {
									hs.add(materialTypeValue.toString());
								}

								else {
									arg0.addValidationError(
											item.getAttributeInstance(outerPackChildInst.getPath() + "/Material_type"),
											ValidationError.Type.VALIDATION_RULE,
											"Outer Packaging Material Type Cannot be duplicated");
									logger.info("Outer Packaging Material Type Cannot be duplicated");
								}
							}

						}
					}

				}

				if (arg0.getCollaborationStep().getName().equalsIgnoreCase("10 Validate and Review")) {

					Collection<Spec> specs = item.getSpecs();

					for (Spec seconSpec : specs) {

						if (seconSpec.getName().contains("Sinelco_ss")) {

							AttributeInstance funcAttrInst = item.getAttributeInstance(Constants.FUNCTIONAL);

							if (funcAttrInst != null) {
								Object translationRequired = item
										.getAttributeValue(Constants.FUNC_MODIFY_TRANSLATION_REQUIRED);

								Object packagingRequired = item
										.getAttributeValue(Constants.FUNC_MODIFY_PACKAGING_REQUIRED);

								if (translationRequired == null && packagingRequired == null) {
									arg0.addValidationError(
											item.getAttributeInstance(Constants.FUNC_MODIFY_TRANSLATION_REQUIRED),
											ValidationError.Type.VALIDATION_RULE,
											"Translation and Packaging cannot be blank");

									logger.info("Either Translation or Packaging should be selected");
								}

							}
						}
					}

				}

				if (arg0.getCollaborationStep().getName().equalsIgnoreCase("04 Supply Chain review")) {

					String exitValue = arg0.getExitValueForItem();
					logger.info("exitValue " + exitValue);

					if (exitValue != null && exitValue.equalsIgnoreCase("Reject")) {

						// Object scRejectComments = item.getAttributeValue("Product_c/Status
						// Attributes/SC_Rejection_Comments");
						Object scRejectComments = item.getAttributeValue(Constants.IS_SC_REJECTION);

						if (scRejectComments == null) {
							// arg0.addValidationError(item.getAttributeInstance("Product_c/Status
							// Attributes/SC_Rejection_Comments"),
							arg0.addValidationError(item.getAttributeInstance(Constants.IS_SC_REJECTION),
									ValidationError.Type.VALIDATION_RULE,
									"Rejection comment is mandatory when exit value is Reject");
							logger.info("Either Translation or Packaging should be selected");
						}
					}

				}

				if (arg0.getCollaborationStep().getName().equalsIgnoreCase("05 Legal Review")) {

					String exitValue = arg0.getExitValueForItem();
					logger.info("exitValue " + exitValue);

					if (exitValue != null && exitValue.equalsIgnoreCase("Reject")) {

						// Object legalRejectComments = item.getAttributeValue("Product_c/Status
						// Attributes/Legal_Rejection_Comments");
						Object legalRejectComments = item.getAttributeValue(Constants.IS_LEGAL_REJECTED);

						if (legalRejectComments == null) {
							// arg0.addValidationError(item.getAttributeInstance("Product_c/Status
							// Attributes/Legal_Rejection_Comments"),
							arg0.addValidationError(item.getAttributeInstance(Constants.IS_LEGAL_REJECTED),
									ValidationError.Type.VALIDATION_RULE,
									"Rejection comment is mandatory when exit value is Reject");
							logger.info("Either Translation or Packaging should be selected");
						}
					}

				}

				if (arg0.getCollaborationStep().getName().equalsIgnoreCase("06 Marketing Review")) {

					String exitValue = arg0.getExitValueForItem();
					logger.info("exitValue " + exitValue);

					if (exitValue != null && exitValue.equalsIgnoreCase("Reject")) {

						// Object eCOMRejectComments = item.getAttributeValue("Product_c/Status
						// Attributes/ECOM_Rejection_Comments");
						Object eCOMRejectComments = item.getAttributeValue(Constants.ECOM_REJECT);

						if (eCOMRejectComments == null) {
							// arg0.addValidationError(item.getAttributeInstance("Product_c/Status
							// Attributes/ECOM_Rejection_Comments"),
							arg0.addValidationError(item.getAttributeInstance(Constants.ECOM_REJECT),
									ValidationError.Type.VALIDATION_RULE,
									"Rejection comment is mandatory when exit value is Reject");
							logger.info("Either Translation or Packaging should be selected");
						}
					}

				}

			}
		}
	}

	public static void validateCheckDigitOfBarcodes(Context ctx,
			CollaborationItemPrePostProcessingFunctionArguments arg0, CollaborationItem collabItem, String barcode,
			String barcodeType, String collabItemPath) {
		logger.info("*** Start of function of validateCheckDigitOfBarcodes ***");
		logger.info("Barcode : " + barcode);
		logger.info("barcodeType : " + barcodeType);

		if (barcode != null && !barcode.isEmpty()) {
			if (barcodeType.equalsIgnoreCase("EAN8")) {
				if (barcode.length() != 8) { // check to see if the input is 13 digits
					arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
							ValidationError.Type.VALIDATION_RULE, "Barcode length should be 8 digits as per EAN8");
              	}
				else {
				int checkDigit = checkSumEAN8(barcode); // pass that input to the checkSum function
				Integer checkDigitInteger = new Integer(checkDigit);
				String result = checkDigitInteger.toString();
				if (result.charAt(0) != barcode.charAt(7)) {
					arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
							ValidationError.Type.VALIDATION_RULE,
							"Check digit is not correct as per EAN8 protocls. The check digit should be " + result);
				    }
				}
			}

			else if (barcodeType.equalsIgnoreCase("EAN13")) {
				if (barcode.length() != 13) { // check to see if the input is 13 digits
					arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
							ValidationError.Type.VALIDATION_RULE, "Barcode length should be 13 digits as per EAN13");
		        }
				else { 
				int checkDigit = checkSumEAN13(barcode); // pass that input to the checkSum function
				Integer checkDigitInteger = new Integer(checkDigit);
				String result = checkDigitInteger.toString();
				if (result.charAt(0) != barcode.charAt(12)) {
					arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
							ValidationError.Type.VALIDATION_RULE,
							"Check digit is not correct as per EAN13 protocls. The check digit should be " + result);
				    }
				}
		    }
		}

		logger.info("*** End of function of validateCheckDigitOfBarcodes ***");

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

	
	
	
	
	

	private void validationsCtgItem(ItemPrePostProcessingFunctionArguments arg0, BaseItem item) {

		if (item != null) {

			// Item Type mandatory validations
			// Object itemTypeValue =
			// item.getAttributeValue("Product_c/Type/Type_item_type");
			Object itemTypeValue = item.getAttributeValue(Constants.ITEM_TYPE);
			logger.info("itemTypeValue >> " + itemTypeValue);

			ArrayList<String> attrPaths = new ArrayList<String>();

			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_quantity");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/Value");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/UOM");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/Value");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/UOM");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/Value");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/UOM");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/Value");
			// attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/UOM");

			attrPaths.add(Constants.PACKAGING_INNER_PACK_QTY);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_HEIGHT_VALUE);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_HEIGHT_UOM);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_WIDTH_VALUE);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_WIDTH_UOM);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_DEPTH_VALUE);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_DEPTH_UOM);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_WEIGHT_VALUE);
			attrPaths.add(Constants.PACKAGING_INNER_PACK_WEIGHT_UOM);

			/*
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_quantity");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/Value");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/UOM");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/Value");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/UOM");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/Value");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/UOM");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/Value");
			 * attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/UOM");
			 * 
			 * attrPaths.add("Product_c/Regulatory and Legal/Country_of_origin");
			 * attrPaths.add("Product_c/Regulatory and Legal/Country_of_manufacture");
			 */

			attrPaths.add(Constants.PACKAGING_OUTER_PACK_QTY);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_HEIGHT_VALUE);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_HEIGHT_UOM);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_WIDTH_VALUE);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_WIDTH_UOM);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_DEPTH_VALUE);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_DEPTH_UOM);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_WEIGHT_VALUE);
			attrPaths.add(Constants.PACKAGING_OUTER_PACK_WEIGHT_UOM);

			attrPaths.add(Constants.COUNTRY_OF_ORIGIN);
			attrPaths.add(Constants.COUNTRY_OF_MANUFACTURE);

			if (itemTypeValue != null) {
				if (itemTypeValue.toString().equals("Item")) {

					for (String attrPath : attrPaths) {

						AttributeInstance attrInst = item.getAttributeInstance(attrPath);

						if (attrInst != null) {
							if (item.getAttributeValue(attrPath) == null) {
								logger.info("Throw error");

								arg0.addValidationError(item.getAttributeInstance(attrPath),
										ValidationError.Type.VALIDATION_RULE,
										"This field is mandatory when item type is Item");

							}
						}

					}
				}
			}

			// Legal Regulatory Attribute Validations
			// AttributeInstance legalAttributeInstance =
			// item.getAttributeInstance("Product_c/Regulatory and
			// Legal/Legal_classification");
			AttributeInstance legalAttributeInstance = item.getAttributeInstance(Constants.LEGAL_CLASSIFICATION);

			if (legalAttributeInstance != null) {
				// Object legalClassificationValue =
				// item.getAttributeValue("Product_c/Regulatory and
				// Legal/Legal_classification");
				Object legalClassificationValue = item.getAttributeValue(Constants.LEGAL_CLASSIFICATION);

				logger.info("legalClassificationValue >> " + legalClassificationValue);
				if (legalClassificationValue != null && (legalClassificationValue.equals("Cosmetics leave on")
						|| legalClassificationValue.equals("Cosmetics wash off")
						|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
						|| legalClassificationValue.equals("Electrical"))) {

					logger.info("legal parent path >> " + legalAttributeInstance.getParent().getPath());

					String safetyDateSheetAttrPath = legalAttributeInstance.getParent().getPath()
							+ "/Safety_data_sheet";

					if (item.getAttributeValue(safetyDateSheetAttrPath) == null) {

						arg0.addValidationError(item.getAttributeInstance(safetyDateSheetAttrPath),
								ValidationError.Type.VALIDATION_RULE,
								"Safety Data Sheet is mandatory for the legal classification value selected");
						logger.info("Safety Data sheet error");

					}
				}

//				if (legalClassificationValue != null && legalClassificationValue.equals("Electrical")) {
//
//					AttributeInstance attributeInstance = item.getAttributeInstance("Electrical_ss/Type/Type_battery");
//
//					if (attributeInstance != null) {
//						Object typeBatteryValue = item.getAttributeValue("Electrical_ss/Type_battery");
//
//						if (typeBatteryValue != null && typeBatteryValue != "") {
//							String safetyDateSheetAttrPath = legalAttributeInstance.getParent().getPath()
//									+ "/Safety_data_sheet";
//
//							if (item.getAttributeValue(safetyDateSheetAttrPath) == null) {
//
//								arg0.addValidationError(item.getAttributeInstance(safetyDateSheetAttrPath),
//										ValidationError.Type.VALIDATION_RULE,
//										"Safety Data Sheet is mandatory for the legal classification value and Type Battery selected");
//								logger.info("Safety Data sheet error");
//
//							}
//						}
//					}
//				}

				if (legalClassificationValue != null && (legalClassificationValue.equals("Cosmetics leave on")
						|| legalClassificationValue.equals("Cosmetics wash off")
						|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
						|| legalClassificationValue.equals("Food supplements")
						|| legalClassificationValue.equals("Detergents"))) {

					String ingredientPath = legalAttributeInstance.getParent().getPath() + "/Ingredients/Ingredient";
					if (item.getAttributeInstance(ingredientPath) != null
							&& item.getAttributeValue(ingredientPath) == null) {

						arg0.addValidationError(item.getAttributeInstance(ingredientPath),
								ValidationError.Type.VALIDATION_RULE,
								"Expiry Type is mandatory for the legal classification value selected");
						logger.info("Expiry Date PAO error");
					}
				}

				if (legalClassificationValue != null && (legalClassificationValue.equals("Cosmetics leave on")
						|| legalClassificationValue.equals("Cosmetics wash off")
						|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
						|| legalClassificationValue.equals("Food supplements")
						|| legalClassificationValue.equals("Medical device")
						|| legalClassificationValue.equals("PPE"))) {

					String expiryDatePAOAttrPath = legalAttributeInstance.getParent().getPath() + "/Expiry_type";
					if (item.getAttributeInstance(expiryDatePAOAttrPath) != null
							&& item.getAttributeValue(expiryDatePAOAttrPath) == null) {

						arg0.addValidationError(item.getAttributeInstance(expiryDatePAOAttrPath),
								ValidationError.Type.VALIDATION_RULE,
								"Expiry Type is mandatory for the legal classification value selected");
						logger.info("Expiry Date PAO error");
					}
				}
			}

			// AttributeInstance legalQAttrInstance =
			// item.getAttributeInstance("Product_c/Regulatory and Legal/QPBUNMLGR");
			AttributeInstance legalQAttrInstance = item.getAttributeInstance(Constants.QPBUNMLGR);

			if (legalQAttrInstance != null) {
				// Object legalQAttrValue = item.getAttributeValue("Product_c/Regulatory and
				// Legal/QPBUNMLGR");
				Object legalQAttrValue = item.getAttributeValue(Constants.QPBUNMLGR);

				if (legalQAttrValue != null && (legalQAttrValue.equals("g") || legalQAttrValue.equals("ml"))) {

					String legalQAttrPath = legalQAttrInstance.getParent().getPath() + "/QPBQTYMLGR";
					if (item.getAttributeInstance(legalQAttrPath) != null
							&& item.getAttributeValue(legalQAttrPath) == null) {

						arg0.addValidationError(item.getAttributeInstance(legalQAttrPath),
								ValidationError.Type.VALIDATION_RULE,
								"This field is mandatory for the QPBUNMLGR value selected");
						logger.info("Expiry Date PAO error");
					}
				}

			}

			// Packaging material validation

			// Inner Packaging
			// AttributeInstance innerPackInstance =
			// item.getAttributeInstance("Product_c/Packaging/Pack_inner_packaging_material");
			AttributeInstance innerPackInstance = item
					.getAttributeInstance(Constants.PACKAGING_INNER_PACKAGING_MATERIAL);

			if (innerPackInstance != null) {
				logger.info("Inner Pack Instance not null");
				List<? extends AttributeInstance> innerPackChildren = innerPackInstance.getChildren();

				HashSet<String> hs = new HashSet<String>();

				for (AttributeInstance innerPackChildInst : innerPackChildren) {

					Object materialTypeValue = item.getAttributeValue(innerPackChildInst.getPath() + "/Material_type");

					logger.info("materialTypeValue >> " + materialTypeValue);

					if (materialTypeValue != null) {
						if (!hs.contains(materialTypeValue.toString())) {
							hs.add(materialTypeValue.toString());
						}

						else {
							arg0.addValidationError(
									item.getAttributeInstance(innerPackChildInst.getPath() + "/Material_type"),
									ValidationError.Type.VALIDATION_RULE,
									"Inner Packaging Material Type Cannot be duplicated");
							logger.info("Inner Packaging Material Type Cannot be duplicated");
						}
					}

				}
			}

			// Outer Packaging
			// AttributeInstance outerPackInstance =
			// item.getAttributeInstance("Product_c/Packaging/Pack_outer_packaging_material");
			AttributeInstance outerPackInstance = item
					.getAttributeInstance(Constants.PACKAGING_OUTER_PACKAGING_MATERIAL);

			if (outerPackInstance != null) {
				logger.info("Inner Pack Instance not null");
				List<? extends AttributeInstance> outerPackChildren = outerPackInstance.getChildren();

				HashSet<String> hs = new HashSet<String>();

				for (AttributeInstance outerPackChildInst : outerPackChildren) {

					Object materialTypeValue = item.getAttributeValue(outerPackChildInst.getPath() + "/Material_type");

					logger.info("materialTypeValue >> " + materialTypeValue);

					if (materialTypeValue != null) {
						if (!hs.contains(materialTypeValue.toString())) {
							hs.add(materialTypeValue.toString());
						}

						else {
							arg0.addValidationError(
									item.getAttributeInstance(outerPackChildInst.getPath() + "/Material_type"),
									ValidationError.Type.VALIDATION_RULE,
									"Outer Packaging Material Type Cannot be duplicated");
							logger.info("Outer Packaging Material Type Cannot be duplicated");
						}
					}

				}
			}

		}
	}

	public static void validateBarcodes(Context ctx, CollaborationItemPrePostProcessingFunctionArguments inArgs,
			CollaborationItem collabItem, String barcode, String collabItemPath) {
		logger.info("*** Start of function of validateBarcodes ***");
		String enterpriseId = collabItem.getPrimaryKey();
		logger.info("barcode : " + barcode + "   enterpriseId : " + enterpriseId);
		LookupTable barcodeLkp = ctx.getLookupTableManager().getLookupTable("Barcode_Lookup_Table");
		String lkpName = "Barcode_Lookup_Table";
		String path = "Barcode_Lookup_Spec/barcode";

		String sBarcodeQuery = "select item from catalog('" + lkpName + "') where item['" + path + "']  like '"
				+ barcode + "'";
		logger.info("sBarcodeQuery : " + sBarcodeQuery);
		SearchQuery searchBarcodeQuery = ctx.createSearchQuery(sBarcodeQuery);
		SearchResultSet searchBarcodeResult = searchBarcodeQuery.execute();
		Boolean flag = false;
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
					break;
				}
			}
			logger.info("flag : " + flag);
			if (flag == false) {
				inArgs.addValidationError(collabItem.getAttributeInstance(collabItemPath),
						ValidationError.Type.VALIDATION_RULE,
						"Duplicate Barcode error : Barcode is already associated to another Item in Catalog");
			}
		} else {
			logger.info("** Creates new Entry in lookup **");
			LookupTableEntry newEntry = barcodeLkp.createEntry();
			newEntry.setAttributeValue(path, barcode);
			newEntry.setAttributeValue("Barcode_Lookup_Spec/enterprise_item_id", enterpriseId);
			logger.info(newEntry.save());
		}
		logger.info("*** End of function of validateBarcodes ***");
	}

	@Override
	public void prePostProcessing(CollaborationCategoryPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}