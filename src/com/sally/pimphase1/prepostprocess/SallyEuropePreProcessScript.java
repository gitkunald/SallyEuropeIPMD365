package com.sally.pimphase1.prepostprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.prepostprocess.SallyEuropePreProcessScript.class"

import org.apache.log4j.Logger;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.item.BaseItem;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.CategoryPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.ItemPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.PrePostProcessingFunction;
import com.ibm.pim.hierarchy.category.Category;

public class SallyEuropePreProcessScript implements PrePostProcessingFunction {

	private static Logger logger = Logger.getLogger(SallyEuropePreProcessScript.class);
	Context ctx = PIMContextFactory.getCurrentContext();

	@Override
	public void prePostProcessing(ItemPrePostProcessingFunctionArguments arg0) {
		logger.info("Inside item preprocess");

		Item item = arg0.getItem();
		
		Collection<Category> itemCategories = item.getCategories();
		for (Category category : itemCategories) {

			String hierName = category.getHierarchy().getName();
			logger.info("hierName >> " + hierName);

			if (hierName.equals("Product Hierarchy")) {
				Object catCode = category.getAttributeValue("Product_h/category_code");
				Object catName = category.getAttributeValue("Product_h/category_name");

				logger.info("catCode >> " + catCode);
				logger.info("catName >> " + catName);

				if (catCode != null) {

					AttributeInstance ERPOperationalInst = item
							.getAttributeInstance("Product_c/ERP Operational");

					if (ERPOperationalInst != null){
						
						item.setAttributeValue("Product_c/ERP Operational/Category_code",catCode.toString());
						item.setAttributeValue("Product_c/ERP Operational/Category_name",catName.toString());
						
						logger.info("Product Category values set");

					}

				}
			}
			
			if (hierName.equals("Brand Hierarchy")) {
				Object catCode = category.getAttributeValue("Product_h/category_code");
				Object catName = category.getAttributeValue("Product_h/category_name");

				logger.info("catCode >> " + catCode);
				logger.info("catName >> " + catName);

				if (catCode != null) {

					AttributeInstance ERPOperationalInst = item
							.getAttributeInstance("Product_c/ERP Operational");

					if (ERPOperationalInst != null){
						
						item.setAttributeValue("Product_c/ERP Operational/Brand_code",catCode.toString());
						item.setAttributeValue("Product_c/ERP Operational/Brand_name",catName.toString());
						
						logger.info("Brand Category values set");

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

		validationsCollabItem(arg0, item);

	}

	private void validationsCollabItem(CollaborationItemPrePostProcessingFunctionArguments arg0,
			CollaborationItem item) {

		if (item != null) {

			// Item Type mandatory validations
			Object itemTypeValue = item.getAttributeValue("Product_c/Type/Type_item_type");
			logger.info("itemTypeValue >> " + itemTypeValue);

			ArrayList<String> attrPaths = new ArrayList<String>();

			attrPaths.add("Product_c/Packaging/Pack_inner_pack_quantity");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/UOM");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/UOM");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/UOM");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/UOM");

			attrPaths.add("Product_c/Packaging/Pack_outer_pack_quantity");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/UOM");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/UOM");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/UOM");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/UOM");

			attrPaths.add("Product_c/Regulatory and Legal/Country_of_origin");
			attrPaths.add("Product_c/Regulatory and Legal/Country_of_manufacture");

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
			AttributeInstance legalAttributeInstance = item
					.getAttributeInstance("Product_c/Regulatory and Legal/Legal_classification");

			if (legalAttributeInstance != null) {
				Object legalClassificationValue = item
						.getAttributeValue("Product_c/Regulatory and Legal/Legal_classification");
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
//					AttributeInstance attributeInstance = item.getAttributeInstance("Secondary Spec/Type_battery");
//					if (attributeInstance != null)
//					{
//						Object typeBatteryValue = item.getAttributeValue("Secondary Spec/Type_battery");
//						
//						if (typeBatteryValue!= null && typeBatteryValue != "")
//						{
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

				if (legalClassificationValue.equals("Cosmetics leave on")
						|| legalClassificationValue.equals("Cosmetics wash off")
						|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
						|| legalClassificationValue.equals("Food supplements")
						|| legalClassificationValue.equals("Detergents")) {

					String ingredientPath = legalAttributeInstance.getParent().getPath() + "/Ingredients/Ingredient";
					if (item.getAttributeInstance(ingredientPath) != null
							&& item.getAttributeValue(ingredientPath) == null) {

						arg0.addValidationError(item.getAttributeInstance(ingredientPath),
								ValidationError.Type.VALIDATION_RULE,
								"Expiry Type is mandatory for the legal classification value selected");
						logger.info("Expiry Date PAO error");
					}
				}

				if (legalClassificationValue.equals("Cosmetics leave on")
						|| legalClassificationValue.equals("Cosmetics wash off")
						|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
						|| legalClassificationValue.equals("Food supplements")
						|| legalClassificationValue.equals("Medical device")
						|| legalClassificationValue.equals("PPE")) {

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

			AttributeInstance legalQAttrInstance = item
					.getAttributeInstance("Product_c/Regulatory and Legal/QPBUNMLGR");

			if (legalQAttrInstance != null) {
				Object legalQAttrValue = item.getAttributeValue("Product_c/Regulatory and Legal/QPBUNMLGR");

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

			//Inner Packaging
			AttributeInstance innerPackInstance = item
					.getAttributeInstance("Product_c/Packaging/Pack_inner_packaging_material");

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
			
			//Outer Packaging
			AttributeInstance outerPackInstance = item
					.getAttributeInstance("Product_c/Packaging/Pack_outer_packaging_material");

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

	private void validationsCtgItem(ItemPrePostProcessingFunctionArguments arg0, BaseItem item) {

		if (item != null) {

			// Item Type mandatory validations
			Object itemTypeValue = item.getAttributeValue("Product_c/Type/Type_item_type");
			logger.info("itemTypeValue >> " + itemTypeValue);

			ArrayList<String> attrPaths = new ArrayList<String>();

			attrPaths.add("Product_c/Packaging/Pack_inner_pack_quantity");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_height/UOM");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_width/UOM");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_depth/UOM");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/Value");
			attrPaths.add("Product_c/Packaging/Pack_inner_pack_weight/UOM");

			attrPaths.add("Product_c/Packaging/Pack_outer_pack_quantity");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_height/UOM");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_width/UOM");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_depth/UOM");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/Value");
			attrPaths.add("Product_c/Packaging/Pack_outer_pack_weight/UOM");

			attrPaths.add("Product_c/Regulatory and Legal/Country_of_origin");
			attrPaths.add("Product_c/Regulatory and Legal/Country_of_manufacture");

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
			AttributeInstance legalAttributeInstance = item
					.getAttributeInstance("Product_c/Regulatory and Legal/Legal_classification");

			if (legalAttributeInstance != null) {
				Object legalClassificationValue = item
						.getAttributeValue("Product_c/Regulatory and Legal/Legal_classification");
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
//					AttributeInstance attributeInstance = item.getAttributeInstance("Secondary Spec/Type_battery");
//					if (attributeInstance != null)
//					{
//						Object typeBatteryValue = item.getAttributeValue("Secondary Spec/Type_battery");
//						
//						if (typeBatteryValue!= null && typeBatteryValue != "")
//						{
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

				if (legalClassificationValue.equals("Cosmetics leave on")
						|| legalClassificationValue.equals("Cosmetics wash off")
						|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
						|| legalClassificationValue.equals("Food supplements")
						|| legalClassificationValue.equals("Detergents")) {

					String ingredientPath = legalAttributeInstance.getParent().getPath() + "/Ingredients/Ingredient";
					if (item.getAttributeInstance(ingredientPath) != null
							&& item.getAttributeValue(ingredientPath) == null) {

						arg0.addValidationError(item.getAttributeInstance(ingredientPath),
								ValidationError.Type.VALIDATION_RULE,
								"Expiry Type is mandatory for the legal classification value selected");
						logger.info("Expiry Date PAO error");
					}
				}

				if (legalClassificationValue.equals("Cosmetics leave on")
						|| legalClassificationValue.equals("Cosmetics wash off")
						|| legalClassificationValue.equals("Aerosols") || legalClassificationValue.equals("Biocide")
						|| legalClassificationValue.equals("Food supplements")
						|| legalClassificationValue.equals("Medical device")
						|| legalClassificationValue.equals("PPE")) {

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

			AttributeInstance legalQAttrInstance = item
					.getAttributeInstance("Product_c/Regulatory and Legal/QPBUNMLGR");

			if (legalQAttrInstance != null) {
				Object legalQAttrValue = item.getAttributeValue("Product_c/Regulatory and Legal/QPBUNMLGR");

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

			//Inner Packaging
			AttributeInstance innerPackInstance = item
					.getAttributeInstance("Product_c/Packaging/Pack_inner_packaging_material");

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
			
			//Outer Packaging
			AttributeInstance outerPackInstance = item
					.getAttributeInstance("Product_c/Packaging/Pack_outer_packaging_material");

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

	@Override
	public void prePostProcessing(CollaborationCategoryPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
