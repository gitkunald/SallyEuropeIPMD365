package com.sally.pim.prepostprocessing;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.prepostprocessing.SinelcoPreProcessing.class"

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.*;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.CategoryPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.ItemPrePostProcessingFunctionArguments;
import com.ibm.pim.extensionpoints.PrePostProcessingFunction;
import com.ibm.pim.hierarchy.Hierarchy;
import com.ibm.pim.hierarchy.category.Category;


public class SinelcoPreProcessing implements PrePostProcessingFunction {

	private static Logger logger = LogManager.getLogger(SinelcoPreProcessing.class);
	public static HashMap<String, CollaborationItem> hmBaseItemDetails = new HashMap<String, CollaborationItem>();

	@Override
	public void prePostProcessing(ItemPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Inside item");
	}

	@Override
	public void prePostProcessing(CategoryPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Inside Category");
	}

	@Override
	public void prePostProcessing(CollaborationItemPrePostProcessingFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("*** Start of function of Sinelco Catalog Post Process ***");
		Context ctx = PIMContextFactory.getCurrentContext();
		CollaborationItem collabItem = arg0.getCollaborationItem();
		
		if (arg0.getCollaborationStep() != null) {
			logger.info("Step Name : " + arg0.getCollaborationStep().getName());
			logger.info("Primary Key is : " + collabItem.getAttributeValue("Sinelco_Product_c/MDM_item_id"));
			Object entityTypeObj = collabItem.getAttributeValue("Sinelco_Product_c/entity_type");
			logger.info("Entity Type : " + entityTypeObj);
			String entityObj = "";
			if (entityTypeObj != null) {
				entityObj = entityTypeObj.toString();
			}
			if (arg0.getCollaborationStep().getName().equalsIgnoreCase("Upload Spreadsheet")
					|| arg0.getCollaborationStep().getName().equalsIgnoreCase("01 Create Item and Variants")) {
				logger.info("Inside Step condition1111");
				
				//Setting Name attribute

				AttributeInstance importAttrNameAttrInst = collabItem
						.getAttributeInstance("Sinelco_Product_c/Import Attributes/Name");

				if (importAttrNameAttrInst != null) {
					if (importAttrNameAttrInst.getValue() != null) {
						logger.info("Import Attr Name is not null : "+importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/nl_NL", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/fr_FR", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/de_DE", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/es_ES", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/en_GB", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/it_IT", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/da_DK", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/pl_PL", importAttrNameAttrInst.getValue());
						collabItem.setAttributeValue("Sinelco_Product_c/product_name/pt_PT", importAttrNameAttrInst.getValue());
					}
				}
				
				List<Category> cat = new ArrayList<Category>();
				List<String> hierarchyList = new ArrayList<String>();
				hierarchyList.add("Sinelco_Products_Hierarchy");
				hierarchyList.add("Sinelco_Item_Type_Hierarchy");

				Category category = null;
				Hierarchy masterHierarchy = null;

				for (int x = 0; x < hierarchyList.size(); x++) {
					masterHierarchy = ctx.getHierarchyManager().getHierarchy(hierarchyList.get(x));

					logger.info("hierarchyList.get(x) : " + hierarchyList.get(x));
					logger.info("masterHierarchy : " + masterHierarchy);
					logger.info("Master Hierarchy : " + masterHierarchy.getName());
					if (entityObj.equalsIgnoreCase("Item")) {
						if (masterHierarchy.getName().equalsIgnoreCase("Sinelco_Products_Hierarchy"))
							category = masterHierarchy.getCategoryByPrimaryKey("CAT0001");
						else if (masterHierarchy.getName().equalsIgnoreCase("Sinelco_Item_Type_Hierarchy"))
							category = masterHierarchy.getCategoryByPrimaryKey("H2C1");

					} else if (entityObj.equalsIgnoreCase("Variant")) {
						if (masterHierarchy.getName().equalsIgnoreCase("Sinelco_Products_Hierarchy"))
							category = masterHierarchy.getCategoryByPrimaryKey("CATVR0001");
						else if (masterHierarchy.getName().equalsIgnoreCase("Sinelco_Item_Type_Hierarchy"))
							category = masterHierarchy.getCategoryByPrimaryKey("H2C2");
					}
					cat.add(category);
				}

				if (category != null) {
					collabItem.moveToCategories(cat);
				}

				if (!entityObj.isEmpty()) {

					if (entityObj.equalsIgnoreCase("Item")) {

						logger.info("Entity object is Base111111");
						Object importItemTypeValue = collabItem
								.getAttributeValue("Sinelco_Product_c/Import Attributes/Item_Type");
						logger.info("importItemTypeValue : " + importItemTypeValue);

						AttributeInstance itemTypeAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/Item_Type");

						if (itemTypeAttrInst != null) {
							if (itemTypeAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Item_ss/item_type", itemTypeAttrInst.getValue());
							}
						}	

					}

					else if (entityObj.equalsIgnoreCase("Variant")) {

						logger.info("Entity object is Variant111111");
						
						AttributeInstance nlAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/nl_NL");
						if (nlAttrInst != null) {
							logger.info("Local Value : " + nlAttrInst.getValue());
							if (nlAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/nl_NL",
										nlAttrInst.getValue());
							}
						}

						AttributeInstance frAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/fr_FR");
						if (frAttrInst != null) {
							logger.info("Local Value fr: " + frAttrInst.getValue());
							if (frAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/fr_FR",
										frAttrInst.getValue());
							}
						}

						AttributeInstance enAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/en_GB");
						if (enAttrInst != null) {
							logger.info("Local Value en: " + enAttrInst.getValue());
							if (enAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/en_GB",
										enAttrInst.getValue());
							}
						}

						AttributeInstance deAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/de_DE");
						if (deAttrInst != null) {
							logger.info("Local Value De: " + deAttrInst.getValue());
							if (deAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/de_DE",
										deAttrInst.getValue());
							}
						}

						AttributeInstance iTAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/it_IT");
						if (iTAttrInst != null) {
							logger.info("Local Value IT: " + iTAttrInst.getValue());
							if (iTAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/it_IT",
										iTAttrInst.getValue());
							}
						}

						AttributeInstance eSAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/es_ES");
						if (eSAttrInst != null) {
							logger.info("Local Value ES: " + eSAttrInst.getValue());
							if (eSAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/es_ES",
										eSAttrInst.getValue());
							}
						}
						
						AttributeInstance dAAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/da_DK");
						if (dAAttrInst != null) {
							logger.info("Local Value DA: " + dAAttrInst.getValue());
							if (dAAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/da_DK",
										dAAttrInst.getValue());
							}
						}
						
						AttributeInstance plAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/pl_PL");
						if (plAttrInst != null) {
							logger.info("Local Value PL: " + plAttrInst.getValue());
							if (plAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/pl_PL",
										plAttrInst.getValue());
							}
						}

						AttributeInstance pTAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/trade/pt_PT");
						if (pTAttrInst != null) {
							logger.info("Local Value PT: " + pTAttrInst.getValue());
							if (pTAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/trade/pt_PT",
										pTAttrInst.getValue());
							}
						}


						// Store Values

						AttributeInstance esStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/es_ES");
						if (esStoreAttrInst != null) {
							logger.info("Local Value STore ES: " + esStoreAttrInst.getValue());
							if (esStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/es_ES",
										esStoreAttrInst.getValue());
							}
						}

						AttributeInstance iTStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/it_IT");
						if (iTStoreAttrInst != null) {
							logger.info("Local Value STore IT: " + iTStoreAttrInst.getValue());
							if (iTStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/it_IT",
										iTStoreAttrInst.getValue());
							}
						}

						AttributeInstance nLStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/nl_NL");
						if (nLStoreAttrInst != null) {
							logger.info("Local Value STore NL: " + nLStoreAttrInst.getValue());
							if (nLStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/nl_NL",
										nLStoreAttrInst.getValue());
							}
						}

						AttributeInstance fRStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/fr_FR");
						if (fRStoreAttrInst != null) {
							logger.info("Local Value STore FR: " + fRStoreAttrInst.getValue());
							if (fRStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/fr_FR",
										fRStoreAttrInst.getValue());
							}
						}

						AttributeInstance eNStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/en_GB");
						if (eNStoreAttrInst != null) {
							logger.info("Local Value STore EN: " + eNStoreAttrInst.getValue());
							if (eNStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/en_GB",
										eNStoreAttrInst.getValue());
							}
						}

						AttributeInstance dEStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/de_DE");
						if (dEStoreAttrInst != null) {
							logger.info("Local Value STore DE: " + dEStoreAttrInst.getValue());
							if (dEStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/de_DE",
										dEStoreAttrInst.getValue());
							}
						}
						
						AttributeInstance dAStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/da_DK");
						if (dAStoreAttrInst != null) {
							logger.info("Local Value STore DA: " + dAStoreAttrInst.getValue());
							if (dAStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/da_DK",
										dAStoreAttrInst.getValue());
							}
						}
						
						AttributeInstance plStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/pl_PL");
						if (plStoreAttrInst != null) {
							logger.info("Local Value Store PL: " + plStoreAttrInst.getValue());
							if (plStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/pl_PL",
										plStoreAttrInst.getValue());
							}
						}

						AttributeInstance pTStoreAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/assortment/retail/pt_PT");
						if (pTStoreAttrInst != null) {
							logger.info("Local Value Store PT: " + pTStoreAttrInst.getValue());
							if (pTStoreAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/assortment/retail/pt_PT",
										pTStoreAttrInst.getValue());
							}
						}
						
						
						AttributeInstance colorAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/colour");

						if (colorAttrInst != null) {
							if (colorAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/variant_differentiators/colour",
										colorAttrInst.getValue());
							}
						}
						
						AttributeInstance sizeAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/size");

						if (sizeAttrInst != null) {
							if (sizeAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/variant_differentiators/size",
										sizeAttrInst.getValue());
							}
						}
						
						AttributeInstance styleAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/style");

						if (styleAttrInst != null) {
							if (styleAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/variant_differentiators/style",
										styleAttrInst.getValue());
							}
						}
						
						
						
						AttributeInstance fragAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/fragrance");

						if (fragAttrInst != null) {
							if (fragAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/variant_differentiators/fragrance",
										fragAttrInst.getValue());
							}
						}
						
						
						AttributeInstance configAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/configuration");

						if (configAttrInst != null) {
							if (configAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/variant_differentiators/configuration",
										configAttrInst.getValue());
							}
						}
						
						AttributeInstance typeAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/type");

						if (typeAttrInst != null) {
							if (typeAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/variant_differentiators/type",
										typeAttrInst.getValue());
							}
						}
						
						AttributeInstance strengthAttrInst = collabItem
								.getAttributeInstance("Sinelco_Product_c/Import Attributes/strength");

						if (strengthAttrInst != null) {
							if (strengthAttrInst.getValue() != null) {
								collabItem.setAttributeValue("Sinelco_Variant_ss/variant_differentiators/strength",
										strengthAttrInst.getValue());
							}
						}

					}
				}
				setBaseAndVariant(collabItem);
			}
			logger.info("entityObj end : "+entityObj);
		}

	}

		public static void setBaseAndVariant(CollaborationItem collabItem) {
		logger.info("*** Start of function of setBaseAndVariant ***");
		Object entityTypeObj = collabItem.getAttributeValue("Sinelco_Product_c/entity_type");
		logger.info("Entity Type : "+entityTypeObj);
		String entityObj = "";
		if(entityTypeObj != null) {
			entityObj = entityTypeObj.toString();
		}
		String productName = (String) collabItem.getAttributeValue("Sinelco_Product_c/product_name/en_GB");
		logger.info("entityType : " + entityObj);
		logger.info("productName : " + productName);
		
		if(productName != null) {
			if(entityObj.equalsIgnoreCase("Item")) {
				logger.info("*** Item is BASE ***");
				//hmBaseItemDetails.clear();
				hmBaseItemDetails.put(productName, collabItem);
				logger.info("hmBaseItemDetails" + hmBaseItemDetails);
			} else if (entityObj.equalsIgnoreCase("Variant")) {
				logger.info("*** Item is Variant ***");
				logger.info("hmBaseItemDetails" + hmBaseItemDetails);
				if(!hmBaseItemDetails.isEmpty()) {
					
					logger.info("Base Item Name : " + hmBaseItemDetails.get(productName).getAttributeValue("Sinelco_Product_c/product_name/en_GB"));
					if (productName.equalsIgnoreCase((String) hmBaseItemDetails.get(productName).getAttributeValue("Sinelco_Product_c/product_name/en_GB"))) {
						List<? extends AttributeInstance> variantChildren = hmBaseItemDetails.get(productName).getAttributeInstance("Sinelco_Item_ss/variants").getChildren();
						Integer variantSize = variantChildren.size();
						logger.info("variantSize : " + variantSize);
						logger.info("collabItem.getPrimaryKey() : " + collabItem.getPrimaryKey());
						
						List<Object> variantValues = new ArrayList<>();
						for (int i=0 ; i < variantSize ; i++) {
							variantValues.add(hmBaseItemDetails.get(productName).getAttributeValue("Sinelco_Item_ss/variants#" + i + "/variant_id"));
						}
						if(!variantValues.contains(collabItem.getPrimaryKey())) {
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_id", collabItem.getPrimaryKey());
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_colour", collabItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/colour"));
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_size", collabItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/size"));
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_style", collabItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/style"));
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_strength", collabItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/strength"));
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_fragrance", collabItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/fragrance"));
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_type", collabItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/type"));
							hmBaseItemDetails.get(productName).setAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_configuration", collabItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/configuration"));
							logger.info(hmBaseItemDetails.get(productName).save());
							logger.info(hmBaseItemDetails.get(productName).getAttributeValue("Sinelco_Item_ss/variants#" + variantSize + "/variant_id"));
							logger.info("Base Item Saved !");
						}
	
						collabItem.setAttributeValue("Sinelco_Variant_ss/base_item", hmBaseItemDetails.get(productName).getPrimaryKey());
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