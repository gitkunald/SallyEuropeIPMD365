package com.sally.pim.prepostprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;
import com.vivisimo.gelato.stubs.ForEach;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.attribute.ExtendedAttributeChanges;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.BaseItem;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.Entry;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.common.exceptions.PIMInvalidOperationException;
import com.ibm.pim.common.exceptions.PIMSearchException;
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
import com.ibm.pim.organization.Organization;
import com.ibm.pim.organization.OrganizationHierarchy;

public class SallyPreProcessing implements PrePostProcessingFunction {

	private static Logger logger = Logger.getLogger(SallyPreProcessing.class);
	public static HashMap<String, CollaborationItem> hmBaseItemDetails = new HashMap<String, CollaborationItem>();
	Context ctx = PIMContextFactory.getCurrentContext();
	Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally_Products_Catalog");

	@Override
	public void prePostProcessing(ItemPrePostProcessingFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		logger.info("Inside item");
	}

	@Override
	public void prePostProcessing(CategoryPrePostProcessingFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		logger.info("Inside Category");
	}

	@Override
	public void prePostProcessing(CollaborationItemPrePostProcessingFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		logger.info("*** Start of function of Sally Catalog Post Process ***");

		CollaborationItem collabItem = inArgs.getCollaborationItem();

		if (inArgs.getCollaborationStep() != null) {

			logger.info("Step Name : " + inArgs.getCollaborationStep().getName());
			logger.info("Primary Key is : " + collabItem.getPrimaryKey());
			logger.info("** Get EntityType **");
			Object entityTypeObj = collabItem.getAttributeValue("Product_c/entity_type");
			String sEntityType = "";
			if (entityTypeObj != null) {
				sEntityType = entityTypeObj.toString();
			}

			logger.info("Entity Type : " + sEntityType);

			if (inArgs.getCollaborationStep().getName().equalsIgnoreCase("Upload Spreadsheet")) {

				List<Category> cat = new ArrayList<Category>();
				List<String> hierarchyList = new ArrayList<String>();
				hierarchyList.add("Sally_Item_Type_Hierarchy");

				Category category = null;
				Hierarchy masterHierarchy = null;

				for (int x = 0; x < hierarchyList.size(); x++) {
					masterHierarchy = ctx.getHierarchyManager().getHierarchy(hierarchyList.get(x));
					logger.info("Master Hierarchy : " + masterHierarchy.getName());
					if (sEntityType.equalsIgnoreCase("Item")) {
						if (masterHierarchy.getName().equalsIgnoreCase("Sally_Item_Type_Hierarchy"))
							category = masterHierarchy.getCategoryByPrimaryKey("CIH1");
					} else if (sEntityType.equalsIgnoreCase("Variant")) {
						if (masterHierarchy.getName().equalsIgnoreCase("Sally_Item_Type_Hierarchy"))
							category = masterHierarchy.getCategoryByPrimaryKey("CIH2");
					}

					cat.add(category);
					logger.info("Category is " + category);
				}

				if (category != null) {
					collabItem.moveToCategories(cat);
				}

				// Auto populate Vendor Name based on Vendor ID
				Object primaryVendorIdObj = collabItem.getAttributeValue("Product_c/primaryvendor_id#0");
				logger.info("primaryVendorIdObj : " + primaryVendorIdObj);
				String primaryVendorId = "";
				if (primaryVendorIdObj != null) {
					primaryVendorId = primaryVendorIdObj.toString();
				}

				logger.info("primaryVendorId : " + primaryVendorId);
				if (primaryVendorId != null) {
					PIMCollection<LookupTableEntry> vendorLkpEntries = ctx.getLookupTableManager()
							.getLookupTable("Vendor Lookup Table").getLookupTableEntries();
					logger.info("vendorLkpEntries size : " + vendorLkpEntries.size());
					for (Iterator<LookupTableEntry> iterator = vendorLkpEntries.iterator(); iterator.hasNext();) {
						LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
						logger.info("lookupTableEntry.getValues() : " + lookupTableEntry.getValues());
						if (lookupTableEntry.getValues().contains(primaryVendorId)) {
							// Get the vendor name
							logger.info("lookupTableEntry.getKey() : " + lookupTableEntry.getKey());
							OrganizationHierarchy vendorHierarchy = ctx.getOrganizationManager()
									.getOrganizationHierarchy("Vendor Organization Hierarchy");
							Organization vendorCategory = vendorHierarchy
									.getOrganizationByPrimaryKey(lookupTableEntry.getKey());
							collabItem.mapToOrganization(vendorCategory);
							collabItem.setAttributeValue("Product_c/primaryvendor_name", lookupTableEntry.getKey());
							break;
						}
					}
				}

				// Set Products Hierarchy Category based on Input Category Code
				Object categoryCodeObj = collabItem.getAttributeValue("Product_c/Import Attributes/category_code");
				logger.info("Category Path Object : " + categoryCodeObj);
				String categoryCode = "";
				if (categoryCodeObj != null) {
					categoryCode = categoryCodeObj.toString();

					logger.info("Debug :: Category Path String changed code : " + categoryCode);
					Hierarchy productsHierarchy = ctx.getHierarchyManager().getHierarchy("Sally_Products_Hierarchy");
					Category leafCategory = productsHierarchy.getCategoryByPrimaryKey(categoryCode);

					logger.info("leafCategory : " + leafCategory);

					if (Objects.nonNull(leafCategory)) {
						try {
							collabItem.mapToCategory(leafCategory);
						} catch (PIMInvalidOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						// Create Category
					}
				}
				// End

				// Set Products Hierarchy Category based on Input Category Code
				Object brandCodeObj = collabItem.getAttributeValue("Product_c/Import Attributes/brand_code");
				logger.info("Brand Category Path Object : " + brandCodeObj);
				String brandCode = "";
				if (brandCodeObj != null) {
					brandCode = brandCodeObj.toString();
					logger.info("Debug :: Brand Category Path String changed code : " + brandCode);
					Hierarchy brandHierarchy = ctx.getHierarchyManager().getHierarchy("Brand_Hierarchy");
					Category leafBrandCategory = brandHierarchy.getCategoryByPrimaryKey(brandCode);
					logger.info("leafCategory : " + leafBrandCategory);

					if (Objects.nonNull(leafBrandCategory)) {
						try {
							collabItem.mapToCategory(leafBrandCategory);
						} catch (PIMInvalidOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						// Create Category
					}
				}
				// End

				logger.info("Test debug");
				collabItem.setAttributeValue("Product_c/product_name/en_GB",
						collabItem.getAttributeValue("Product_c/Import Attributes/Name"));
				collabItem.setAttributeValue("Product_c/product_name/nl_NL",
						collabItem.getAttributeValue("Product_c/Import Attributes/Name"));
				collabItem.setAttributeValue("Product_c/product_name/fr_FR",
						collabItem.getAttributeValue("Product_c/Import Attributes/Name"));
				collabItem.setAttributeValue("Product_c/product_name/de_DE",
						collabItem.getAttributeValue("Product_c/Import Attributes/Name"));
				collabItem.setAttributeValue("Product_c/product_name/es_ES",
						collabItem.getAttributeValue("Product_c/Import Attributes/Name"));
				collabItem.setAttributeValue("Product_c/product_name/nl_BE",
						collabItem.getAttributeValue("Product_c/Import Attributes/Name"));

				if (sEntityType.equalsIgnoreCase("Item")) {
					logger.info(
							"Item Type is : " + collabItem.getAttributeValue("Product_c/Import Attributes/Item_Type"));
					collabItem.setAttributeValue("Item_ss/item_type",
							collabItem.getAttributeValue("Product_c/Import Attributes/Item_Type"));
				}

				if (sEntityType.equalsIgnoreCase("Variant")) {
					logger.info(
							"Item Type is : " + collabItem.getAttributeValue("Product_c/Import Attributes/Item_Type"));
					collabItem.setAttributeValue("Variant_ss/assortment/trade/es_ES",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/trade/es_ES"));
					collabItem.setAttributeValue("Variant_ss/assortment/trade/nl_NL",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/trade/nl_NL"));
					collabItem.setAttributeValue("Variant_ss/assortment/trade/fr_FR",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/trade/fr_FR"));
					collabItem.setAttributeValue("Variant_ss/assortment/trade/en_GB",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/trade/en_GB"));
					collabItem.setAttributeValue("Variant_ss/assortment/trade/de_DE",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/trade/de_DE"));
					collabItem.setAttributeValue("Variant_ss/assortment/trade/nl_BE",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/trade/nl_BE"));
					collabItem.setAttributeValue("Variant_ss/assortment/retail/es_ES",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/retail/es_ES"));
					collabItem.setAttributeValue("Variant_ss/assortment/retail/nl_NL",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/retail/nl_NL"));
					collabItem.setAttributeValue("Variant_ss/assortment/retail/fr_FR",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/retail/fr_FR"));
					collabItem.setAttributeValue("Variant_ss/assortment/retail/en_GB",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/retail/en_GB"));
					collabItem.setAttributeValue("Variant_ss/assortment/retail/de_DE",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/retail/de_DE"));
					collabItem.setAttributeValue("Variant_ss/assortment/retail/nl_BE",
							collabItem.getAttributeValue("Product_c/Import Attributes/assortment/retail/nl_BE"));
					collabItem.setAttributeValue("Variant_ss/variant_differentiators/configuration",
							collabItem.getAttributeValue("Product_c/Import Attributes/configuration"));
					collabItem.setAttributeValue("Variant_ss/variant_differentiators/fragrance",
							collabItem.getAttributeValue("Product_c/Import Attributes/fragrance"));
					collabItem.setAttributeValue("Variant_ss/variant_differentiators/colour",
							collabItem.getAttributeValue("Product_c/Import Attributes/colour"));
					collabItem.setAttributeValue("Variant_ss/variant_differentiators/size",
							collabItem.getAttributeValue("Product_c/Import Attributes/size"));
					collabItem.setAttributeValue("Variant_ss/variant_differentiators/style",
							collabItem.getAttributeValue("Product_c/Import Attributes/style"));
					collabItem.setAttributeValue("Variant_ss/variant_differentiators/strength",
							collabItem.getAttributeValue("Product_c/Import Attributes/strength"));

				}

				setBaseAndVariant(collabItem);
			}

			logger.info("entityObj end : " + sEntityType);
			if (sEntityType.equalsIgnoreCase("Variant")) {
				logger.info("It is Variant");

				if (inArgs.getCollaborationStep().getName().equalsIgnoreCase("Create_Items")) {
					logger.info("Hazardous Hier attribute set for variants");
					Collection<Category> collabCategories = collabItem.getCategories();
					for (Category category : collabCategories) {

						String hierName = category.getHierarchy().getName();
						logger.info("hierName >> " + hierName);

						if (hierName.equals("Hazardous_Hierarchy")) {
							Object catCode = category.getAttributeValue("Product_h/category_code");
							Object catName = category.getAttributeValue("Product_h/category_name");

							logger.info("catCode >> " + catCode);
							logger.info("catName >> " + catName);

							if (catCode != null) {

								AttributeInstance legalAttributeInstance = collabItem
										.getAttributeInstance("Variant_ss/Legal");
								
								logger.info("legalAttributeInstance >> " + legalAttributeInstance);

								if (legalAttributeInstance != null){
									
									collabItem.setAttributeValue("Variant_ss/Legal/hazardous_hierarchy_code",catCode.toString());
									collabItem.setAttributeValue("Variant_ss/Legal/hazardous_hierarchy_name",catName.toString());
									
									logger.info("Hazardous values set");

								}

							}
						}

					}

				}

				// Validations
				validateUniquenessOfBarcodes(ctx, inArgs, collabItem);
				logger.info("Workflow Name new : " + collabItem.getCollaborationArea().getWorkflow().getName());

				// Legal Attribute Mandatory validations
				validateLegalAttributes(inArgs, collabItem);

				if (collabItem.getCollaborationArea().getWorkflow().getName()
						.equalsIgnoreCase("Sally_Product_Maintenance_Workflow")) {
					validateBarcodeModifications(collabItem, inArgs, "Variant_ss/Barcode", "barcode_type_each_level",
							"barcode_each_level");
					validateBarcodeModifications(collabItem, inArgs, "Variant_ss/Packaging Attributes",
							"inner_pack_barcode_type", "inner_pack_barcode");
					validateBarcodeModifications(collabItem, inArgs, "Variant_ss/Packaging Attributes",
							"outer_pack_barcode_type", "outer_pack_barcode");
				}
			}
			
			if (sEntityType.equalsIgnoreCase("Item")) {
				// Adding entry to the lkp table to store ItemType
				
				Collection<Category> categories = collabItem.getCategories();
				
				if (!categories.isEmpty())
				{
					for (Category category : categories) {
						
						if (category.getDisplayName().contains("Items"))
						{
							addItemTypeLkpEntry(collabItem);
						}
					}
					
				
				}
			}

			if (inArgs.getCollaborationStep().getName().equalsIgnoreCase("Add_Information")
					|| inArgs.getCollaborationStep().getName().equalsIgnoreCase("Data_Team_Review_and_Pricing")
					|| inArgs.getCollaborationStep().getName().equalsIgnoreCase("Supply_Chain_Data_Review")
					|| inArgs.getCollaborationStep().getName().equalsIgnoreCase("Create_Items")) {

				try {
					validateItemTypeMandatoryAttributes(collabItem, inArgs);
				} catch (PIMSearchException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

		logger.info("*** End of function of Sally Catalog Post Process ***");
	}

	private void addItemTypeLkpEntry(CollaborationItem collabItem) {

		logger.info("Entered addItemTypeLkpEntry method");
		ExtendedAttributeChanges attributeChangesSinceLastSave = collabItem.getAttributeChangesSinceLastSave();
		LookupTable itemTypeLkpTable = ctx.getLookupTableManager().getLookupTable("Item_Type_Lkp_Table");

		Object entityTypeObj = collabItem.getAttributeValue("Product_c/entity_type");
		String entityObj = "";
		if (entityTypeObj != null) {
			entityObj = entityTypeObj.toString();
		}
		if (entityObj.equalsIgnoreCase("Item")) {
			addUpdateItmTypeLkpEntry(collabItem, itemTypeLkpTable);
		}

		for (int x = 0; x < attributeChangesSinceLastSave.getModifiedAttributesWithNewData().size(); x++) {
			if (attributeChangesSinceLastSave.getModifiedAttributesWithNewData().get(x).getPath()
					.contains("item_type")) {
				addUpdateItmTypeLkpEntry(collabItem, itemTypeLkpTable);
			}
		}

		for (int y = 0; y < attributeChangesSinceLastSave.getNewlyAddedAttributes().size(); y++) {
			if (attributeChangesSinceLastSave.getNewlyAddedAttributes().get(y).getPath().contains("item_type")) {
				addUpdateItmTypeLkpEntry(collabItem, itemTypeLkpTable);
			}
		}

		for (int z = 0; z < attributeChangesSinceLastSave.getDeletedAttributes().size(); z++) {
			if (attributeChangesSinceLastSave.getDeletedAttributes().get(z).getPath().contains("item_type")) {
				addUpdateItmTypeLkpEntry(collabItem, itemTypeLkpTable);
			}
		}
	}

	private void addUpdateItmTypeLkpEntry(CollaborationItem collabItem, LookupTable itemTypeLkpTable) {
		logger.info("Entered addUpdateItmTypeLkpEntry");
		Object itmTypeAttrValue = collabItem.getAttributeValue("Item_ss/item_type");

		if (itmTypeAttrValue != null) {
			String[] lookupEntryKeys = itemTypeLkpTable.getLookupEntryKeys();

			if (lookupEntryKeys.length > 0) {
				for (String lkpKey : lookupEntryKeys) {

					if (lkpKey.equals(collabItem.getPrimaryKey())) {
						LookupTableEntry lookupTableEntry = itemTypeLkpTable.getLookupTableEntry(lkpKey);
						lookupTableEntry.setAttributeValue("Item_Type_Lkp_Spec/ItemType", itmTypeAttrValue);
						lookupTableEntry.save();
						
					}

					else {
						LookupTableEntry createEntry = itemTypeLkpTable.createEntry();
						createEntry.setAttributeValue("Item_Type_Lkp_Spec/Enterprise_ID", collabItem.getPrimaryKey());
						createEntry.setAttributeValue("Item_Type_Lkp_Spec/ItemType", itmTypeAttrValue);
						createEntry.save();
					}
				}
			}

			else {
				LookupTableEntry createEntry = itemTypeLkpTable.createEntry();
				createEntry.setAttributeValue("Item_Type_Lkp_Spec/Enterprise_ID", collabItem.getPrimaryKey());
				createEntry.setAttributeValue("Item_Type_Lkp_Spec/ItemType", itmTypeAttrValue);
				createEntry.save();
			}

		}
	}

	public static String getValidAttributePath(BaseItem itemObj, String attrPaths) {

		String validAttrPath = StringUtils.EMPTY;

		if (attrPaths != null && !attrPaths.isEmpty()) {
			String[] allAttrPaths = attrPaths.split("\\|");

			for (int i = 0; i < allAttrPaths.length; i++) {
				String attrPath = allAttrPaths[i].trim();

				try {
					itemObj.getAttributeInstance(attrPath);
					validAttrPath = attrPath;
					break;
				}

				catch (Exception e) {
					logger.info("Invalid attribute path: {}");
				}
			}
		}

		logger.info("Exiting getValidAttributePath() for itemObj where attribute path is {} " + validAttrPath);

		return validAttrPath;
	}

	private void validateItemTypeMandatoryAttributes(CollaborationItem collabItem,
			CollaborationItemPrePostProcessingFunctionArguments inArgs) throws PIMSearchException {

		logger.info("Entered validateItemTypeMandatoryAttributes method");
		ArrayList<String> attributePaths = new ArrayList<>();

		attributePaths.add("Item_ss/country_of_origin");
		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_qty");
		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_height/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_height/uom");

		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_width/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_width/uom");

		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_depth/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_depth/uom");

		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_weight/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/inner_pack_weight/uom");

		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_qty");
		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_height/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_height/uom");

		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_width/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_width/uom");

		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_depth/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_depth/uom");

		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_weight/value");
		attributePaths.add("Variant_ss/Packaging Attributes#0/outer_pack_weight/uom");

		String primaryKey = collabItem.getPrimaryKey();
		for (String attrPath : attributePaths) {

			if (attrPath.contains("Item_ss"))

			{
				String validAttributePath = getValidAttributePath(collabItem, attrPath);

				if (!validAttributePath.isEmpty()) {

					throwValidationForItemType(collabItem, primaryKey, inArgs, attrPath);

				}
			}

			else if (attrPath.contains("Variant_ss")) {

				String validAttributePath = getValidAttributePath(collabItem, attrPath);

				if (!validAttributePath.isEmpty()) {

					Object baseItemObjId = collabItem.getAttributeValue("Variant_ss/base_item");

					if (baseItemObjId != null) {
						String baseItemId = baseItemObjId.toString();
						throwValidationForItemType(collabItem, baseItemId, inArgs, attrPath);
					}
				}
			}

		}

	}

	private void throwValidationForItemType(CollaborationItem collabItem, String primaryKey,
			CollaborationItemPrePostProcessingFunctionArguments inArgs, String attrPath) {
		LookupTable itmTypeLkpTable = ctx.getLookupTableManager().getLookupTable("Item_Type_Lkp_Table");

		String[] lookupEntryKeys = itmTypeLkpTable.getLookupEntryKeys();

		if (lookupEntryKeys.length > 0) {
			for (String lkpKey : lookupEntryKeys) {

				if (lkpKey.equals(primaryKey)) {
					
					LookupTableEntry lookupTableEntry = itmTypeLkpTable.getLookupTableEntry(lkpKey);
					Object itmType = lookupTableEntry.getAttributeValue("Item_Type_Lkp_Spec/ItemType");
					if (itmType != null)

					{
						if (itmType.toString().equalsIgnoreCase("Item")) {
							if (collabItem.getAttributeValue(attrPath) == null) {
								inArgs.addValidationError(collabItem.getAttributeInstance(attrPath),
										ValidationError.Type.VALIDATION_RULE, "This field is mandatory for Item type");
							}
						}
					}
				}

			}

		}
	}

	public static void validateBarcodeModifications(CollaborationItem collabItem,
			CollaborationItemPrePostProcessingFunctionArguments inArgs, String barcodePath, String barcodeType,
			String barcode) {
		logger.info("*** Start of function of validateBarcodeModifications **");

		BaseItem ctgItem = collabItem.getSourceItem();
		int ctgItemSize = ctgItem.getAttributeInstance(barcodePath).getChildren().size();
		int collabItemSize = collabItem.getAttributeInstance(barcodePath).getChildren().size();
		logger.info("ctgItemSize : " + ctgItemSize);
		logger.info("collabItemSize : " + collabItemSize);
		int size = ctgItemSize;
		if (collabItemSize < ctgItemSize) {
			size = collabItemSize;
		}

		for (int i = 0; i < size; i++) {
			String collabTypeValue = (String) collabItem.getAttributeValue(barcodePath + "#" + i + "/" + barcodeType);
			String ctgTypeValue = (String) ctgItem.getAttributeValue(barcodePath + "#" + i + "/" + barcodeType);
			String collabValue = (String) collabItem.getAttributeValue(barcodePath + "#" + i + "/" + barcode);
			String ctgValue = (String) ctgItem.getAttributeValue(barcodePath + "#" + i + "/" + barcode);
			logger.info("collabValue : " + collabValue + "    ctgValue : " + ctgValue);
			logger.info("ctgTypeValue : " + ctgTypeValue + "    collabTypeValue : " + ctgValue);
			if (collabTypeValue != null && ctgTypeValue != null && collabValue != null && ctgValue != null) {

				logger.info("collabValue.equalsIgnoreCase(ctgValue) : " + collabValue.equalsIgnoreCase(ctgValue));
				if (!collabValue.equalsIgnoreCase(ctgValue) || !collabTypeValue.equalsIgnoreCase(ctgTypeValue)) {
					inArgs.addValidationError(collabItem.getAttributeInstance(barcodePath + "#" + i + "/" + barcode),
							ValidationError.Type.VALIDATION_RULE, "Existing Barcode occurrence can not be modified");
				}
			}
		}

		logger.info("*** End of function of validateBarcodeModifications ***");
	}

	public static void validateUniquenessOfBarcodes(Context ctx,
			CollaborationItemPrePostProcessingFunctionArguments inArgs, CollaborationItem collabItem) {
		logger.info("*** Start of function of validateUniquenessOfBarcodesUpdated ***");
		int barcodeSize = collabItem.getAttributeInstance("Variant_ss/Barcode").getChildren().size();
		int packagingSize = collabItem.getAttributeInstance("Variant_ss/Packaging Attributes").getChildren().size();
		logger.info("barcodeSize : " + barcodeSize);

		if (barcodeSize > 0) {
			String CollabItemPath = "Variant_ss/Barcode/barcode_each_level";
			String barcodeTypeEachLevel = (String) collabItem
					.getAttributeValue("Variant_ss/Barcode#" + (barcodeSize - 1) + "/barcode_type_each_level");
			String barcodeEachLevel = (String) collabItem
					.getAttributeValue("Variant_ss/Barcode#" + (barcodeSize - 1) + "/barcode_each_level");
			if (barcodeTypeEachLevel != null || barcodeEachLevel != null) {
				validateBarcodes(ctx, inArgs, collabItem, barcodeEachLevel, barcodeTypeEachLevel, CollabItemPath);
			}
		}

		logger.info("packagingSize : " + packagingSize);
		if (packagingSize > 0) {
			String innerPackBarcode = (String) collabItem.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + (packagingSize - 1) + "/inner_pack_barcode");
			String outerPackBarcode = (String) collabItem.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + (packagingSize - 1) + "/outer_pack_barcode");
			logger.info("innerPackBarcode : " + innerPackBarcode);
			logger.info("outerPackBarcode : " + outerPackBarcode);
			if (innerPackBarcode != "" || innerPackBarcode != null) {
				String CollabItemPath = "Variant_ss/Packaging Attributes/inner_pack_barcode";
				String barcodeTypeInnerPack = (String) collabItem.getAttributeValue(
						"Variant_ss/Packaging Attributes#" + (packagingSize - 1) + "/inner_pack_barcode_type");
				logger.info("barcodeTypeInnerPack : " + barcodeTypeInnerPack);
				if (barcodeTypeInnerPack != null || innerPackBarcode != null) {
					validateBarcodes(ctx, inArgs, collabItem, innerPackBarcode, barcodeTypeInnerPack, CollabItemPath);
				}
			}

			if (outerPackBarcode != "" || outerPackBarcode != null) {
				String CollabItemPath = "Variant_ss/Packaging Attributes/outer_pack_barcode";
				String barcodeTypeOuterPack = (String) collabItem.getAttributeValue(
						"Variant_ss/Packaging Attributes#" + (packagingSize - 1) + "/outer_pack_barcode_type");
				logger.info("barcodeTypeOuterPack : " + barcodeTypeOuterPack);
				if (barcodeTypeOuterPack != null || outerPackBarcode != null) {
					validateBarcodes(ctx, inArgs, collabItem, outerPackBarcode, barcodeTypeOuterPack, CollabItemPath);
				}
			}
		}
		logger.info("*** End of function of validateUniquenessOfBarcodesUpdated ***");
	}

	public static void validateLegalAttributes(CollaborationItemPrePostProcessingFunctionArguments inArgs,
			CollaborationItem collabItem) {
		logger.info("*** Start of function of validateLegalAttributes ***");

		AttributeInstance legalAttributeInstance = collabItem.getAttributeInstance("Variant_ss/Legal");
		if (legalAttributeInstance != null) {
			List<? extends AttributeInstance> legalChildren = legalAttributeInstance.getChildren();

			for (AttributeInstance legalAttributeInstanceChild : legalChildren) {

				String legalClasifPath = legalAttributeInstanceChild.getPath() + "/legal_classification";

				Object legalClassificationValue = collabItem.getAttributeValue(legalClasifPath);
				logger.info("legalClassificationValue >> " + legalClassificationValue);

				if (legalClassificationValue != null) {

					if (legalClassificationValue.equals("Cosmetics leave on")
							|| legalClassificationValue.equals("Cosmetics wash off")
							|| legalClassificationValue.equals("Aerosols")
							|| legalClassificationValue.equals("Biocide")) {

						String safetyDateSheetAttrPath = legalAttributeInstanceChild.getPath() + "/safety_data_sheet";

						if (collabItem.getAttributeValue(safetyDateSheetAttrPath) == null) {

							inArgs.addValidationError(collabItem.getAttributeInstance(safetyDateSheetAttrPath),
									ValidationError.Type.VALIDATION_RULE,
									"Safety Data Sheet is mandatory for the legal classification value selected");
							logger.info("Safety Data sheet error");

						}
					}

					if (legalClassificationValue.equals("Electrical")) {
						Object typeBatteryValue = collabItem
								.getAttributeValue(legalAttributeInstanceChild.getPath() + "/type_of_battery");
						String safetyDateSheetAttrPath = legalAttributeInstanceChild.getPath() + "/safety_data_sheet";
						if (typeBatteryValue != null) {
							if (collabItem.getAttributeValue(safetyDateSheetAttrPath) == null) {

								inArgs.addValidationError(collabItem.getAttributeInstance(safetyDateSheetAttrPath),
										ValidationError.Type.VALIDATION_RULE,
										"Safety Data Sheet is mandatory if legal classification is Electrical and Type of Battery selected");
								logger.info("Safety Data sheet error");
							}
						}
					}

					if (legalClassificationValue.equals("Cosmetics leave on")
							|| legalClassificationValue.equals("Cosmetics wash off")
							|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
							|| legalClassificationValue.equals("Food supplements")
							|| legalClassificationValue.equals("Medical device")
							|| legalClassificationValue.equals("PPE")) {

						String expiryDatePAOAttrPath = legalAttributeInstanceChild.getPath() + "/expiry_date_pao";
						if (collabItem.getAttributeValue(expiryDatePAOAttrPath) == null) {

							inArgs.addValidationError(collabItem.getAttributeInstance(expiryDatePAOAttrPath),
									ValidationError.Type.VALIDATION_RULE,
									"Expiry Date PAO is mandatory for the legal classification value selected");
							logger.info("Expiry Date PAO error");
						}
					}

					if (legalClassificationValue.equals("Cosmetics leave on")
							|| legalClassificationValue.equals("Cosmetics wash off")
							|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
							|| legalClassificationValue.equals("Food supplements")
							|| legalClassificationValue.equals("Detergents")) {

						if (collabItem.getAttributeInstance("Variant_ss/Legal/ingredients").isGrouping() && collabItem
								.getAttributeInstance("Variant_ss/Legal/ingredients").isMultiOccurrence()) {

							AttributeInstance ingredAttrInst = collabItem
									.getAttributeInstance("Variant_ss/Legal/ingredients");
							if (ingredAttrInst != null) {
								int size = collabItem.getAttributeInstance("Variant_ss/Legal/ingredients").getChildren()
										.size();

								if (size == 0) {

									inArgs.addValidationError(
											collabItem.getAttributeInstance("Variant_ss/Legal/ingredients"),
											ValidationError.Type.VALIDATION_RULE,
											"Ingredients is mandatory for the legal classification value selected");
									logger.info("Ingredients grouping mandatory");
								}

								else if (size == 1) {
									if ((collabItem
											.getAttributeValue("Variant_ss/Legal/ingredients#0" + "/value") == null)) {
										inArgs.addValidationError(
												collabItem.getAttributeInstance("Variant_ss/Legal/ingredients#0/value"),
												ValidationError.Type.VALIDATION_RULE,
												"Ingredients Value is mandatory for the legal classification value selected");
										logger.info("Ingredients Value is mandatory");
									}

									if ((collabItem
											.getAttributeValue("Variant_ss/Legal/ingredients#0" + "/date") == null)) {
										inArgs.addValidationError(
												collabItem.getAttributeInstance("Variant_ss/Legal/ingredients#0/date"),
												ValidationError.Type.VALIDATION_RULE,
												"Ingredients Date is mandatory for the legal classification value selected");
										logger.info("Ingredients Date is mandatory");
									}
								}
							}
						}
					}
				}

			}

		}
	}

	public static void validateBarcodes(Context ctx, CollaborationItemPrePostProcessingFunctionArguments inArgs,
			CollaborationItem collabItem, String barcode, String barcodeType, String collabItemPath) {
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
				}
			}
			logger.info("flag : " + flag);
			if (flag == false) {
				inArgs.addValidationError(collabItem.getAttributeInstance(collabItemPath),
						ValidationError.Type.VALIDATION_RULE,
						"Duplicate Barcode error : Barcode is already associated to another Enterprise ID in Catalog");
			}
		} else {
			Boolean errorFlag = validateCheckDigitOfBarcodes(ctx, inArgs, collabItem, barcode, barcodeType,
					collabItemPath);
			logger.info("errorFlag : " + errorFlag);
			if (!errorFlag) {
				logger.info("** Creates new Entry **");
				LookupTableEntry newEntry = barcodeLkp.createEntry();
				newEntry.setAttributeValue(path, barcode);
				newEntry.setAttributeValue("Barcode_Lookup_Spec/enterprise_item_id", enterpriseId);
				logger.info(newEntry.save());
			}
		}
		logger.info("*** End of function of validateBarcodes ***");
	}

	public static void validateUniquenessOfBarcodesOld(Context ctx,
			CollaborationItemPrePostProcessingFunctionArguments arg0, CollaborationItem collabItem) {
		logger.info("*** Start of function of validateUniquenessOfBarcodes updated ***");
		String catalogName = "Sally_Products_Catalog";
		String colAreaName = "Owner Approval Collaboration Area For Sally_Products_Catalog";
		String path = "Variant_ss/Barcode/barcode_each_level";
		String innerPackPath = "Variant_ss/Packaging Attributes/inner_pack_barcode";
		String outerPackPath = "Variant_ss/Packaging Attributes/outer_pack_barcode";
		int barcodeSize = collabItem.getAttributeInstance("Variant_ss/Barcode").getChildren().size();
		int packagingSize = collabItem.getAttributeInstance("Variant_ss/Packaging Attributes").getChildren().size();
		logger.info("barcodeSize : " + barcodeSize);
		logger.info("packagingSize : " + packagingSize);
		String barcodeObj = "Test";
		String innerPackBarcode = "";
		String outerPackBarcode = "";

		if (barcodeSize > 0) {
			barcodeObj = (String) collabItem
					.getAttributeValue("Variant_ss/Barcode#" + (barcodeSize - 1) + "/barcode_each_level");
			barcodeObj = barcodeObj.trim();
			String sBarcodeQuery = "select item from catalog('" + catalogName + "')  where item['" + path + "']  like '"
					+ barcodeObj + "' or item['" + innerPackPath + "'] like '" + barcodeObj + "' or item['"
					+ outerPackPath + "'] like '" + barcodeObj + "'";
			SearchQuery searchBarcodeQuery = ctx.createSearchQuery(sBarcodeQuery);
			SearchResultSet searchBarcodeResult = searchBarcodeQuery.execute();
			if (searchBarcodeResult.size() > 0) {
				logger.info("*** Throwing error Barcode ***");
				arg0.addValidationError(collabItem.getAttributeInstance(path), ValidationError.Type.VALIDATION_RULE,
						"Duplicate Barcode error : Barcode is already associated to another Enterprise ID in Catalog");
			}
		}

		if (packagingSize > 0) {
			if (innerPackBarcode != "") {
				innerPackBarcode = (String) collabItem.getAttributeValue(
						"Variant_ss/Packaging Attributes#" + (packagingSize - 1) + "/inner_pack_barcode");
				if (innerPackBarcode != "" || innerPackBarcode != null) {
					innerPackBarcode = innerPackBarcode.trim();
					String sInnerPackQuery = "select item from catalog('" + catalogName + "')  where item['" + path
							+ "']  like '" + innerPackBarcode + "' or item['" + innerPackPath + "'] like '"
							+ innerPackBarcode + "' or item['" + outerPackPath + "'] like '" + innerPackBarcode + "'";
					SearchQuery searchInnerPackBarcodeQuery = ctx.createSearchQuery(sInnerPackQuery);
					SearchResultSet searchInnerPackBarcodeResult = searchInnerPackBarcodeQuery.execute();
					if (searchInnerPackBarcodeResult.size() > 0) {
						logger.info("*** Throwing error Barcode ***");
						arg0.addValidationError(collabItem.getAttributeInstance(innerPackPath),
								ValidationError.Type.VALIDATION_RULE,
								"Duplicate Inner Pack Barcode error : Inner Pack Barcode is already associated to another Enterprise ID in Catalog");
					}
				}
			}

			if (outerPackBarcode != "") {
				outerPackBarcode = (String) collabItem.getAttributeValue(
						"Variant_ss/Packaging Attributes#" + (packagingSize - 1) + "/outer_pack_barcode");
				if (outerPackBarcode != "" || outerPackBarcode != null) {
					outerPackBarcode = outerPackBarcode.trim();
					String sOuterPackQuery = "select item from catalog('" + catalogName + "')  where item['" + path
							+ "']  like '" + outerPackBarcode + "' or item['" + innerPackPath + "'] like '"
							+ outerPackBarcode + "' or item['" + outerPackPath + "'] like '" + outerPackBarcode + "'";
					SearchQuery searchOuterPackBarcodeQuery = ctx.createSearchQuery(sOuterPackQuery);
					SearchResultSet searchOuterPackBarcodeResult = searchOuterPackBarcodeQuery.execute();
					if (searchOuterPackBarcodeResult.size() > 0) {
						logger.info("*** Throwing error Barcode ***");
						arg0.addValidationError(collabItem.getAttributeInstance(outerPackPath),
								ValidationError.Type.VALIDATION_RULE,
								"Duplicate Outer Pack Barcode error : Outer Pack Barcode is already associated to another Enterprise ID in Catalog");
					}
				}
			}

		}
		logger.info("*** End of function of validateUniquenessOfBarcodes ***");
	}

	public static Boolean validateCheckDigitOfBarcodes(Context ctx,
			CollaborationItemPrePostProcessingFunctionArguments arg0, CollaborationItem collabItem, String barcode,
			String barcodeType, String collabItemPath) {
		logger.info("*** Start of function of validateCheckDigitOfBarcodes ***");
		logger.info("Barcode : " + barcode);
		logger.info("barcodeType : " + barcodeType);

		if (barcodeType.equalsIgnoreCase("EAN8")) {
			if (barcode.length() != 8) { // check to see if the input is 13 digits
				arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
						ValidationError.Type.VALIDATION_RULE, "Barcode length should be 8 digits as per EAN8");
				return true;
			}

			int checkDigit = checkSumEAN8(barcode); // pass that input to the checkSum function
			Integer checkDigitInteger = new Integer(checkDigit);
			String result = checkDigitInteger.toString();
			if (result.charAt(0) != barcode.charAt(7)) {
				arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
						ValidationError.Type.VALIDATION_RULE,
						"Check digit is not correct as per EAN8 protocls. The check digit should be " + result);
				return true;
			}
		}

		else if (barcodeType.equalsIgnoreCase("EAN13")) {
			if (barcode.length() != 13) { // check to see if the input is 13 digits
				arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
						ValidationError.Type.VALIDATION_RULE, "Barcode length should be 13 digits as per EAN13");
				return true;
			}

			int checkDigit = checkSumEAN13(barcode); // pass that input to the checkSum function
			Integer checkDigitInteger = new Integer(checkDigit);
			String result = checkDigitInteger.toString();
			if (result.charAt(0) != barcode.charAt(12)) {
				arg0.addValidationError(collabItem.getAttributeInstance(collabItemPath),
						ValidationError.Type.VALIDATION_RULE,
						"Check digit is not correct as per EAN13 protocls. The check digit should be " + result);
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

	public static void setBaseAndVariant(CollaborationItem collabItem) {
		logger.info("*** Start of function of setBaseAndVariant ***");
		Object entityTypeObj = collabItem.getAttributeValue("Product_c/entity_type");
		logger.info("Entity Type : " + entityTypeObj);
		String entityObj = "";
		if (entityTypeObj != null) {
			entityObj = entityTypeObj.toString();
		}
		String productName = (String) collabItem.getAttributeValue("Product_c/product_name/en_GB");
		logger.info("entityType : " + entityObj);
		logger.info("productName : " + productName);

		if (productName != null) {
			if (entityObj.equalsIgnoreCase("Item")) {
				logger.info("*** Item is BASE ***");
				// hmBaseItemDetails.clear();
				hmBaseItemDetails.put(productName, collabItem);
				logger.info("hmBaseItemDetails" + hmBaseItemDetails);
			} else if (entityObj.equalsIgnoreCase("Variant")) {
				logger.info("*** Item is Variant ***");
				logger.info("hmBaseItemDetails" + hmBaseItemDetails);
				if (!hmBaseItemDetails.isEmpty()) {

					logger.info("Item Item Name : "
							+ hmBaseItemDetails.get(productName).getAttributeValue("Product_c/product_name/en_GB"));
					if (productName.equalsIgnoreCase((String) hmBaseItemDetails.get(productName)
							.getAttributeValue("Product_c/product_name/en_GB"))) {
						List<? extends AttributeInstance> variantChildren = hmBaseItemDetails.get(productName)
								.getAttributeInstance("Item_ss/variants").getChildren();
						Integer variantSize = variantChildren.size();
						logger.info("variantSize : " + variantSize);
						logger.info("collabItem.getPrimaryKey() : " + collabItem.getPrimaryKey());

						List<Object> variantValues = new ArrayList<>();
						for (int i = 0; i < variantSize; i++) {
							variantValues.add(hmBaseItemDetails.get(productName)
									.getAttributeValue("Item_ss/variants#" + i + "/variant_id"));

						}
						if (!variantValues.contains(collabItem.getPrimaryKey())) {
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_id", collabItem.getPrimaryKey());
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_colour",
									collabItem.getAttributeValue("Variant_ss/variant_differentiators/colour"));
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_size",
									collabItem.getAttributeValue("Variant_ss/variant_differentiators/size"));
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_style",
									collabItem.getAttributeValue("Variant_ss/variant_differentiators/style"));
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_strength",
									collabItem.getAttributeValue("Variant_ss/variant_differentiators/strength"));
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_fragrance",
									collabItem.getAttributeValue("Variant_ss/variant_differentiators/fragrance"));
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_type",
									collabItem.getAttributeValue("Variant_ss/variant_differentiators/type"));
							hmBaseItemDetails.get(productName).setAttributeValue(
									"Item_ss/variants#" + variantSize + "/variant_configuration",
									collabItem.getAttributeValue("Variant_ss/variant_differentiators/configuration"));
							logger.info(hmBaseItemDetails.get(productName).save());
							logger.info(hmBaseItemDetails.get(productName)
									.getAttributeValue("Item_ss/variants#" + variantSize + "/variant_id"));
							logger.info("Item Item Saved !");
						}

						collabItem.setAttributeValue("Variant_ss/base_item",
								hmBaseItemDetails.get(productName).getPrimaryKey());
						logger.info("Variant Item Saved !");
					}
				}
			}
		}
		logger.info("*** End of function of setBaseAndVariant ***");
	}

	@Override
	public void prePostProcessing(CollaborationCategoryPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Inside collab category");
	}

}