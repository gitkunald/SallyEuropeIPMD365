package com.sally.pimphase1.prepostprocess;

import java.util.ArrayList;

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

public class SallyEuropePreProcessScript implements PrePostProcessingFunction {

	private static Logger logger = Logger.getLogger(SallyEuropePreProcessScript.class);
	Context ctx = PIMContextFactory.getCurrentContext();

	@Override
	public void prePostProcessing(ItemPrePostProcessingFunctionArguments arg0) {
		logger.info("Inside item preprocess");

		Item item = arg0.getItem();

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

		}
	}

	@Override
	public void prePostProcessing(CollaborationCategoryPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
