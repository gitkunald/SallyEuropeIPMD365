package com.sally.pim.workflows.sinelcoProductCreationAndApprovalWorkflow;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoProductCreationAndApprovalWorkflow.SallyProductCreation.class"

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.*;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ExtendedValidationErrors;
import com.ibm.pim.common.exceptions.PIMInvalidOperationException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.hierarchy.Hierarchy;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.organization.Organization;
import com.ibm.pim.organization.OrganizationHierarchy;

public class SallyProductCreation implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {

		Logger logger = LogManager.getLogger(SallyProductCreation.class);
		logger.info("*** Start of OUT function of Create Step ***");
		Context ctx = PIMContextFactory.getCurrentContext();
		logger.info("Item Come in");
		PIMCollection<CollaborationItem> items = inArgs.getItems();
		logger.info("No of Items : " + items.size());
		ItemCollaborationArea sourceColArea = (ItemCollaborationArea) inArgs.getCollaborationStep().getCollaborationArea();
		
		for (Iterator<CollaborationItem> iterator = items.iterator(); iterator.hasNext();) {
			CollaborationItem collaborationItem = (CollaborationItem) iterator.next();
			logger.info("Item Name : " + collaborationItem.getDisplayName());
			ItemCollaborationArea sally_collab = (ItemCollaborationArea) PIMContextFactory.getCurrentContext().getCollaborationAreaManager().getCollaborationArea("Sally_New_Product_Publish_ColArea");
					CollaborationItem sallyItem = sally_collab.createCollaborationItem();
					logger.info(sallyItem.setAttributeValue("Product_c/MDM_item_id", collaborationItem.getAttributeValue("Sinelco_Product_c/MDM_item_id")));
					sallyItem.setAttributeValue("Product_c/enterprise_item_id", collaborationItem.getAttributeValue("Sinelco_Product_c/enterprise_item_id"));
					logger.info(sallyItem.setAttributeValue("Product_c/display_id", collaborationItem.getAttributeValue("Sinelco_Product_c/display_id")));
					sallyItem.setAttributeValue("Product_c/product_name/nl_NL", collaborationItem.getAttributeValue("Sinelco_Product_c/product_name/nl_NL"));
					sallyItem.setAttributeValue("Product_c/product_name/fr_FR", collaborationItem.getAttributeValue("Sinelco_Product_c/product_name/fr_FR"));
					sallyItem.setAttributeValue("Product_c/product_name/de_DE", collaborationItem.getAttributeValue("Sinelco_Product_c/product_name/de_DE"));
					sallyItem.setAttributeValue("Product_c/product_name/es_ES", collaborationItem.getAttributeValue("Sinelco_Product_c/product_name/es_ES"));
					sallyItem.setAttributeValue("Product_c/product_name/en_GB", collaborationItem.getAttributeValue("Sinelco_Product_c/product_name/en_GB"));
					sallyItem.setAttributeValue("Product_c/search_name", collaborationItem.getAttributeValue("Sinelco_Product_c/search_name"));
					sallyItem.setAttributeValue("Product_c/entity_type", collaborationItem.getAttributeValue("Sinelco_Product_c/entity_type"));
					//sallyItem.setAttributeValue("Product_c/long_name", collaborationItem.getAttributeValue("Sinelco_Product_c/long_name"));
					sallyItem.setAttributeValue("Product_c/product_title_web/nl_NL", collaborationItem.getAttributeValue("Sinelco_Product_c/product_title_web/nl_NL"));
					sallyItem.setAttributeValue("Product_c/product_title_web/fr_FR", collaborationItem.getAttributeValue("Sinelco_Product_c/product_title_web/fr_FR"));
					sallyItem.setAttributeValue("Product_c/product_title_web/de_DE", collaborationItem.getAttributeValue("Sinelco_Product_c/product_title_web/de_DE"));
					sallyItem.setAttributeValue("Product_c/product_title_web/es_ES", collaborationItem.getAttributeValue("Sinelco_Product_c/product_title_web/es_ES"));
					sallyItem.setAttributeValue("Product_c/product_title_web/en_GB", collaborationItem.getAttributeValue("Sinelco_Product_c/product_title_web/en_GB"));
					sallyItem.setAttributeValue("Product_c/long_description_web/nl_NL", collaborationItem.getAttributeValue("Sinelco_Product_c/long_description_web/nl_NL"));
					sallyItem.setAttributeValue("Product_c/long_description_web/fr_FR", collaborationItem.getAttributeValue("Sinelco_Product_c/long_description_web/fr_FR"));
					sallyItem.setAttributeValue("Product_c/long_description_web/de_DE", collaborationItem.getAttributeValue("Sinelco_Product_c/long_description_web/de_DE"));
					sallyItem.setAttributeValue("Product_c/long_description_web/es_ES", collaborationItem.getAttributeValue("Sinelco_Product_c/long_description_web/es_ES"));
					sallyItem.setAttributeValue("Product_c/long_description_web/en_GB", collaborationItem.getAttributeValue("Sinelco_Product_c/long_description_web/en_GB"));
					
					AttributeInstance sharedAtrInst = sallyItem.getAttributeInstance("Product_c/is_shared_sinelco");
					
					if (sharedAtrInst != null)
					{
					
						sharedAtrInst.setValue(Boolean.TRUE);
					}
					
					
					List<? extends AttributeInstance> primaryvendor = collaborationItem.getAttributeInstance("Sinelco_Product_c/primaryvendor_id").getChildren();
					Integer primaryvendorSize = primaryvendor.size();
					logger.info("variantSize : " +primaryvendorSize);
						for (int i=0 ; i < primaryvendorSize ; i++) {
							sallyItem.setAttributeValue("Product_c/primaryvendor_id#"+i, collaborationItem.getAttributeValue("Sinelco_Product_c/primaryvendor_id#"+i));
							}
					
					sallyItem.setAttributeValue("Product_c/primaryvendor_name", collaborationItem.getAttributeValue("Sinelco_Product_c/primaryvendor_name"));
					sallyItem.setAttributeValue("Product_c/vendor_product_name", collaborationItem.getAttributeValue("Sinelco_Product_c/vendor_product_name"));
			
					  List<? extends AttributeInstance> audit_data = collaborationItem.getAttributeInstance("Sinelco_Product_c/audit_data").getChildren();
					  Integer audit_dataSize = audit_data.size();
					  logger.info("audit_dataSize : " + audit_dataSize);
					  for (int i=0 ; i < audit_dataSize ; i++) {
					  sallyItem.setAttributeValue("Product_c/audit_data#"+i+"/comments", collaborationItem.getAttributeValue("Sinelco_Product_c/audit_data#"+i+"/comments"));                   
					  									  }	
					
				
							logger.info("Inside Step condition");
							List<Category> cat = new ArrayList<Category>();
							List<String> hierarchyList = new ArrayList<String>();
							hierarchyList.add("Sally_Item_Type_Hierarchy");
							
							Object entityTypeObj = sallyItem.getAttributeValue("Product_c/entity_type");
							logger.info("Entity Type : "+entityTypeObj);
							String entityObj = "";
							if(entityTypeObj != null) {
								entityObj = entityTypeObj.toString();
							}
							
							
							
							Category category = null;
							Hierarchy masterHierarchy = null;

							for(int x=0; x<hierarchyList.size(); x++) {
								masterHierarchy = ctx.getHierarchyManager().getHierarchy(hierarchyList.get(x));
								
								logger.info("hierarchyList.get(x) : "+hierarchyList.get(x));
								logger.info("masterHierarchy : "+masterHierarchy);
								logger.info("Master Hierarchy : "+masterHierarchy.getName());
								if(entityObj.equalsIgnoreCase("Item")) {
								
									if(masterHierarchy.getName().equalsIgnoreCase("Sally_Item_Type_Hierarchy")) 
										category = masterHierarchy.getCategoryByPrimaryKey("CIH1");
								}
								else if(entityObj.equalsIgnoreCase("Variant")) {
									
									if(masterHierarchy.getName().equalsIgnoreCase("Sally_Item_Type_Hierarchy")) 
										category = masterHierarchy.getCategoryByPrimaryKey("CIH2");
								}
				
								cat.add(category);	
								logger.info("Category is "+category);
							}	
							
							
							if(category != null) {
								sallyItem.moveToCategories(cat);
							}
							
							
							
							OrganizationHierarchy vendorHierarchy = ctx.getOrganizationManager().getOrganizationHierarchy("Vendor Organization Hierarchy");
							Object vendorName = sallyItem.getAttributeValue("Product_c/primaryvendor_name");
							logger.info("Vendor Name Obj : "+vendorName);
							logger.info("New code");
							String sVendor = "";
							if(vendorName.equals("Sinelco")) {
								sVendor = vendorName.toString();
								logger.info("Vendor Name Str : "+sVendor);
								Organization vendorCategory = vendorHierarchy.getOrganizationByPrimaryKey(sVendor);
								sallyItem.mapToOrganization(vendorCategory);
							}	
							
							Object categoryCodeObj = collaborationItem.getAttributeValue("Sinelco_Product_c/Import Attributes/category_code");
							logger.info("Category Path Object : " +categoryCodeObj);
							String categoryCode = "";
							if(categoryCodeObj != null) {
								categoryCode = categoryCodeObj.toString();
								
								logger.info("Debug :: Category Path String changed code : " +categoryCode);
								Hierarchy productsHierarchy = ctx.getHierarchyManager().getHierarchy("Sally_Products_Hierarchy");
								Category leafCategory = productsHierarchy.getCategoryByPrimaryKey(categoryCode);
										
								logger.info("leafCategory : " +leafCategory);
								
								if(Objects.nonNull(leafCategory)) {
									try {
										sallyItem.mapToCategory(leafCategory);
									} catch (PIMInvalidOperationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									// Create Category
								}
							}
							
							// Set Products Hierarchy Category based on Input Category Code
							Object brandCodeObj = collaborationItem.getAttributeValue("Sinelco_Product_c/Import Attributes/brand_code");
							logger.info("Brand Category Path Object : " +brandCodeObj);
							String brandCode = "";
							if(brandCodeObj != null) {
								brandCode = brandCodeObj.toString();
								logger.info("Debug :: Brand Category Path String changed code : " +brandCode);
								Hierarchy brandHierarchy = ctx.getHierarchyManager().getHierarchy("Brand_Hierarchy");
								Category leafBrandCategory = brandHierarchy.getCategoryByPrimaryKey(brandCode);
								logger.info("leafCategory : " +leafBrandCategory);
								
								if(Objects.nonNull(leafBrandCategory)) {
									try {
										sallyItem.mapToCategory(leafBrandCategory);
									} catch (PIMInvalidOperationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									// Create Category
								}
							}
							
							if(entityObj.equalsIgnoreCase("Item"))
							{
							sallyItem.setAttributeValue("Item_ss/erp_item_id/item_id", collaborationItem.getAttributeValue("Sinelco_Item_ss/erp_item_id/item_id"));
							sallyItem.setAttributeValue("Item_ss/erp_item_id/source", collaborationItem.getAttributeValue("Sinelco_Item_ss/erp_item_id/source"));
							sallyItem.setAttributeValue("Item_ss/replacement_item_id", collaborationItem.getAttributeValue("Sinelco_Item_ss/replacement_item_id"));
							sallyItem.setAttributeValue("Item_ss/kit_listing", collaborationItem.getAttributeValue("Sinelco_Item_ss/kit_listing"));
							sallyItem.setAttributeValue("Item_ss/item_type", collaborationItem.getAttributeValue("Sinelco_Item_ss/item_type"));
							//sallyItem.setAttributeValue("Item_ss/product_range_of_colours", collaborationItem.getAttributeValue("Sinelco_Item_ss/product_range_of_colours"));
							//sallyItem.setAttributeValue("Item_ss/product_range_of_sizes", collaborationItem.getAttributeValue("Sinelco_Item_ss/product_range_of_sizes"));
							//sallyItem.setAttributeValue("Item_ss/legal_documentation", collaborationItem.getAttributeValue("Sinelco_Item_ss/legal_documentation"));
							//sallyItem.setAttributeValue("Item_ss/document_reference", collaborationItem.getAttributeValue("Sinelco_Item_ss/document_reference"));
							sallyItem.setAttributeValue("Item_ss/features_and_benefits/nl_NL", collaborationItem.getAttributeValue("Sinelco_Item_ss/features_and_benefits/nl_NL"));
							sallyItem.setAttributeValue("Item_ss/features_and_benefits/fr_FR", collaborationItem.getAttributeValue("Sinelco_Item_ss/features_and_benefits/fr_FR"));
							sallyItem.setAttributeValue("Item_ss/features_and_benefits/de_DE", collaborationItem.getAttributeValue("Sinelco_Item_ss/features_and_benefits/de_DE"));
							sallyItem.setAttributeValue("Item_ss/features_and_benefits/es_ES", collaborationItem.getAttributeValue("Sinelco_Item_ss/features_and_benefits/es_ES"));
							sallyItem.setAttributeValue("Item_ss/features_and_benefits/en_GB", collaborationItem.getAttributeValue("Sinelco_Item_ss/features_and_benefits/en_GB"));
							sallyItem.setAttributeValue("Item_ss/minimum_order_quantity", collaborationItem.getAttributeValue("Sinelco_Item_ss/minimum_order_quantity"));
							sallyItem.setAttributeValue("Item_ss/no_discount_allowed", collaborationItem.getAttributeValue("Sinelco_Item_ss/no_discount_allowed"));
							sallyItem.setAttributeValue("Item_ss/serial_tracked_item", collaborationItem.getAttributeValue("Sinelco_Item_ss/serial_tracked_item"));
							sallyItem.setAttributeValue("Item_ss/commodity_code/nl_NL", collaborationItem.getAttributeValue("Sinelco_Item_ss/commodity_code/nl_NL"));
							sallyItem.setAttributeValue("Item_ss/commodity_code/fr_FR", collaborationItem.getAttributeValue("Sinelco_Item_ss/commodity_code/fr_FR"));
							sallyItem.setAttributeValue("Item_ss/commodity_code/de_DE", collaborationItem.getAttributeValue("Sinelco_Item_ss/commodity_code/de_DE"));
							sallyItem.setAttributeValue("Item_ss/commodity_code/es_ES", collaborationItem.getAttributeValue("Sinelco_Item_ss/commodity_code/es_ES"));
							sallyItem.setAttributeValue("Item_ss/commodity_code/en_GB", collaborationItem.getAttributeValue("Sinelco_Item_ss/commodity_code/en_GB"));
							sallyItem.setAttributeValue("Item_ss/country_of_origin", collaborationItem.getAttributeValue("Sinelco_Item_ss/country_of_origin"));
							//sallyItem.setAttributeValue("Item_ss/slim_flag", collaborationItem.getAttributeValue("Sinelco_Item_ss/slim_flag"));
							sallyItem.setAttributeValue("Item_ss/keywords/nl_NL", collaborationItem.getAttributeValue("Sinelco_Item_ss/keywords/nl_NL"));
							sallyItem.setAttributeValue("Item_ss/keywords/fr_FR", collaborationItem.getAttributeValue("Sinelco_Item_ss/keywords/fr_FR"));
							sallyItem.setAttributeValue("Item_ss/keywords/de_DE", collaborationItem.getAttributeValue("Sinelco_Item_ss/keywords/de_DE"));
							sallyItem.setAttributeValue("Item_ss/keywords/es_ES", collaborationItem.getAttributeValue("Sinelco_Item_ss/keywords/es_ES"));
							sallyItem.setAttributeValue("Item_ss/keywords/en_GB", collaborationItem.getAttributeValue("Sinelco_Item_ss/keywords/en_GB"));
							sallyItem.setAttributeValue("Item_ss/dim_group_id/nl_NL", collaborationItem.getAttributeValue("Sinelco_Item_ss/dim_group_id/nl_NL"));
							sallyItem.setAttributeValue("Item_ss/dim_group_id/fr_FR", collaborationItem.getAttributeValue("Sinelco_Item_ss/dim_group_id/fr_FR"));
							sallyItem.setAttributeValue("Item_ss/dim_group_id/de_DE", collaborationItem.getAttributeValue("Sinelco_Item_ss/dim_group_id/de_DE"));
							sallyItem.setAttributeValue("Item_ss/dim_group_id/es_ES", collaborationItem.getAttributeValue("Sinelco_Item_ss/dim_group_id/es_ES"));
							sallyItem.setAttributeValue("Item_ss/dim_group_id/en_GB", collaborationItem.getAttributeValue("Sinelco_Item_ss/dim_group_id/en_GB"));
							sallyItem.setAttributeValue("Item_ss/item_group_type", collaborationItem.getAttributeValue("Sinelco_Item_ss/item_group_type"));
							sallyItem.setAttributeValue("Item_ss/retail_group_id", collaborationItem.getAttributeValue("Sinelco_Item_ss/retail_group_id"));
							sallyItem.setAttributeValue("Item_ss/retail_department", collaborationItem.getAttributeValue("Sinelco_Item_ss/retail_department"));
							sallyItem.setAttributeValue("Item_ss/retail_group", collaborationItem.getAttributeValue("Sinelco_Item_ss/retail_group"));
							sallyItem.setAttributeValue("Item_ss/commission_group_id", collaborationItem.getAttributeValue("Sinelco_Item_ss/commission_group_id"));
							//sallyItem.setAttributeValue("Item_ss/division_group", collaborationItem.getAttributeValue("Sinelco_Item_ss/division_group"));
							List<? extends AttributeInstance> variants = collaborationItem.getAttributeInstance("Sinelco_Item_ss/variants").getChildren();
							Integer variantsSize = variants.size();
							logger.info("variantSize : " +variantsSize);
								for (int i=0 ; i < variantsSize ; i++) {
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_id", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_id"));
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_colour", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_colour"));
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_size", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_size"));
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_style", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_style"));
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_strength", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_strength"));
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_fragrance", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_fragrance"));
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_type", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_type"));
									sallyItem.setAttributeValue("Item_ss/variants#"+i+"/variant_configuration", collaborationItem.getAttributeValue("Sinelco_Item_ss/variants#"+i+"/variant_configuration"));
									}
							//sallyItem.setAttributeValue("Item_ss/directions_assembly_instructions", collaborationItem.getAttributeValue("Sinelco_Item_ss/directions_assembly_instructions"));		
							}
							else if(entityObj.equalsIgnoreCase("Variant"))
							{
							sallyItem.setAttributeValue("Variant_ss/erp_variant_id/rbovariantid", collaborationItem.getAttributeValue("Sinelco_Variant_ss/erp_variant_id/rbovariantid"));
							sallyItem.setAttributeValue("Variant_ss/erp_variant_id/source", collaborationItem.getAttributeValue("Sinelco_Variant_ss/erp_variant_id/source"));
							sallyItem.setAttributeValue("Variant_ss/manufacturer_id", collaborationItem.getAttributeValue("Sinelco_Variant_ss/manufacturer_id"));
							sallyItem.setAttributeValue("Variant_ss/oe_item_code", collaborationItem.getAttributeValue("Sinelco_Variant_ss/oe_item_code"));
							sallyItem.setAttributeValue("Variant_ss/buyer", collaborationItem.getAttributeValue("Sinelco_Variant_ss/buyer"));
							sallyItem.setAttributeValue("Variant_ss/kvi", collaborationItem.getAttributeValue("Sinelco_Variant_ss/kvi"));
							sallyItem.setAttributeValue("Variant_ss/route_to_customer", collaborationItem.getAttributeValue("Sinelco_Variant_ss/route_to_customer"));
							sallyItem.setAttributeValue("Variant_ss/trade_card_restricted", collaborationItem.getAttributeValue("Sinelco_Variant_ss/trade_card_restricted"));
							sallyItem.setAttributeValue("Variant_ss/qpbunmlgr", collaborationItem.getAttributeValue("Sinelco_Variant_ss/qpbunmlgr"));
							sallyItem.setAttributeValue("Variant_ss/qpbqtymlgr", collaborationItem.getAttributeValue("Sinelco_Variant_ss/qpbqtymlgr"));
							sallyItem.setAttributeValue("Variant_ss/colour_family", collaborationItem.getAttributeValue("Sinelco_Variant_ss/colour_family"));
							sallyItem.setAttributeValue("Variant_ss/colour_shade", collaborationItem.getAttributeValue("Sinelco_Variant_ss/colour_shade"));
							sallyItem.setAttributeValue("Variant_ss/colour_class", collaborationItem.getAttributeValue("Sinelco_Variant_ss/colour_class"));
							sallyItem.setAttributeValue("Variant_ss/colour_id", collaborationItem.getAttributeValue("Sinelco_Variant_ss/colour_id"));
							
							AttributeInstance airflow = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/airflow/value");
							if (airflow != null) {
								logger.info("airflow: " + airflow.getValue());
								if (airflow.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/airflow/value",
											airflow.getValue());
								}
							}
							
							AttributeInstance airflow1 = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/airflow/uom");
							if (airflow1 != null) {
								logger.info("airflow: " + airflow1.getValue());
								if (airflow1.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/airflow/uom",
											airflow1.getValue());
								}
							}
							
							AttributeInstance max_temp = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/max_temp/value");
							if (max_temp != null) {
								logger.info("max_temp: " + max_temp.getValue());
								if (max_temp.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/max_temp/value",
											max_temp.getValue());
								}
							}
							
							AttributeInstance max_temp1 = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/max_temp/uom");
							if (max_temp1 != null) {
								logger.info("max_temp: " + max_temp1.getValue());
								if (max_temp1.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/max_temp/uom",
											max_temp1.getValue());
								}
							}
							AttributeInstance min_temp = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/min_temp/value");
							if (min_temp != null) {
								logger.info("min_temp: " + min_temp.getValue());
								if (min_temp.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/min_temp/value",
											min_temp.getValue());
								}
							}
							
							AttributeInstance min_temp1 = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/min_temp/uom");
							if (min_temp1 != null) {
								logger.info("min_temp: " + min_temp1.getValue());
								if (min_temp1.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/min_temp/uom",
											min_temp1.getValue());
								}
							}
							
							AttributeInstance contents = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/contents/value");
							if (contents != null) {
								logger.info("contents: " + contents.getValue());
								if (contents.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/contents/value",
											contents.getValue());
								}
							}
							
							AttributeInstance contents1 = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/contents/uom");
							if (contents1 != null) {
								logger.info("contents: " + contents1.getValue());
								if (contents1.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/contents/uom",
											contents1.getValue());
								}
							}
							
							AttributeInstance watt = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/watt/value");
							if (watt != null) {
								logger.info("watt: " + watt.getValue());
								if (watt.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/watt/value",
											watt.getValue());
								}
							}
							
							AttributeInstance watt1 = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/watt/uom");
							if (watt1 != null) {
								logger.info("watt: " + watt1.getValue());
								if (watt1.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/watt/uom",
											watt1.getValue());
								}
							}
							//sallyItem.setAttributeValue("Variant_ss/airflow/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/airflow/value"));
							//sallyItem.setAttributeValue("Variant_ss/airflow/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/airflow/uom"));
							//sallyItem.setAttributeValue("Variant_ss/max_temp/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/max_temp/value"));
							//sallyItem.setAttributeValue("Variant_ss/max_temp/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/max_temp/uom"));
							//sallyItem.setAttributeValue("Variant_ss/min_temp/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/min_temp/value"));
							//sallyItem.setAttributeValue("Variant_ss/min_temp/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/min_temp/uom"));
							//sallyItem.setAttributeValue("Variant_ss/contents/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/contents/value"));
							//sallyItem.setAttributeValue("Variant_ss/contents/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/contents/uom"));
							//sallyItem.setAttributeValue("Variant_ss/watt/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/watt/value"));
							//sallyItem.setAttributeValue("Variant_ss/watt/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/watt/uom"));
							//sallyItem.setAttributeValue("Variant_ss/outer_diameter", collaborationItem.getAttributeValue("Sinelco_Variant_ss/outer_diameter"));
							//sallyItem.setAttributeValue("Variant_ss/inner_diameter", collaborationItem.getAttributeValue("Sinelco_Variant_ss/inner_diameter"));
							//sallyItem.setAttributeValue("Variant_ss/web_assortment", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_assortment"));
							sallyItem.setAttributeValue("Variant_ss/web_customer_facing_lead_time", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_customer_facing_lead_time"));
							sallyItem.setAttributeValue("Variant_ss/hair_solution", collaborationItem.getAttributeValue("Sinelco_Variant_ss/hair_solution"));
							sallyItem.setAttributeValue("Variant_ss/web_limited_edition", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_limited_edition"));
							//sallyItem.setAttributeValue("Variant_ss/web_online_date_trade", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_online_date_trade"));
							//sallyItem.setAttributeValue("Variant_ss/web_online_date_retail", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_online_date_retail"));
							sallyItem.setAttributeValue("Variant_ss/web_quantity_restriction", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_quantity_restriction"));
							sallyItem.setAttributeValue("Variant_ss/web_searchable", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_searchable"));
							sallyItem.setAttributeValue("Variant_ss/web_trade_restrict", collaborationItem.getAttributeValue("Sinelco_Variant_ss/web_trade_restrict"));
							sallyItem.setAttributeValue("Variant_ss/variant_differentiators/colour", collaborationItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/colour"));
							sallyItem.setAttributeValue("Variant_ss/variant_differentiators/size", collaborationItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/size"));
							sallyItem.setAttributeValue("Variant_ss/variant_differentiators/style", collaborationItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/style"));
							sallyItem.setAttributeValue("Variant_ss/variant_differentiators/fragrance", collaborationItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/fragrance"));
							sallyItem.setAttributeValue("Variant_ss/variant_differentiators/type", collaborationItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/type"));
							sallyItem.setAttributeValue("Variant_ss/variant_differentiators/configuration", collaborationItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/configuration"));
							sallyItem.setAttributeValue("Variant_ss/variant_differentiators/strength", collaborationItem.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/strength"));
							sallyItem.setAttributeValue("Variant_ss/page_number", collaborationItem.getAttributeValue("Sinelco_Variant_ss/page_number"));
							sallyItem.setAttributeValue("Variant_ss/new_icon", collaborationItem.getAttributeValue("Sinelco_Variant_ss/new_icon"));
							
							AttributeInstance info_block = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/info_block/text");
							if (info_block != null) {
								logger.info("watt: " + info_block.getValue());
								if (info_block.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/info_block/text",
											info_block.getValue());
								}
							}
							AttributeInstance info_block1 = collaborationItem
									.getAttributeInstance("Sinelco_Variant_ss/info_block/image");
							if (info_block1 != null) {
								logger.info("watt: " + info_block1.getValue());
								if (info_block1.getValue() != null) {
									sallyItem.setAttributeValue("Variant_ss/info_block/image",
											info_block1.getValue());
								}
							}
						
							//sallyItem.setAttributeValue("Variant_ss/info_block/text", collaborationItem.getAttributeValue("Sinelco_Variant_ss/info_block/text"));
							//sallyItem.setAttributeValue("Variant_ss/info_block/image", collaborationItem.getAttributeValue("Sinelco_Variant_ss/info_block/image"));
							List<? extends AttributeInstance> Warehouse = collaborationItem.getAttributeInstance("Sinelco_Variant_ss/Warehouse").getChildren();
							Integer variantSize = Warehouse.size();
							logger.info("Warehouse : " + variantSize);
								for (int i=0 ; i < variantSize ; i++) {
									logger.info(collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/ship_in_pallets"));
									logger.info(sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/ship_in_pallets", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/ship_in_pallets")));				  
									sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/pick_instructions", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/pick_instructions"));
									sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/packable", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/packable"));
									sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/conveyable", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/conveyable"));                   
									sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/stackable", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/stackable"));
									sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/pallet_type", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/pallet_type"));
									sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/value_added_service_id", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/value_added_service_id"));                   
									sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/languagedependent", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/languagedependent"));
									//sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/outers_per_layer", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/outers_per_layer"));
									//sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/layers_per_pallete", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/layers_per_pallete"));                   
									
//									AttributeInstance pallet_weight = collaborationItem
//											.getAttributeInstance("Sinelco_Variant_ss/Warehouse#"+i+"/pallet_weight/value");
//									if (pallet_weight != null) {
//										logger.info("pallet_weight: " + pallet_weight.getValue());
//										if (pallet_weight.getValue() != null) {
//											sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/pallet_weight/value",
//													pallet_weight.getValue());
//										}
//									}
//									
//									AttributeInstance pallet_weight1 = collaborationItem
//											.getAttributeInstance("Sinelco_Variant_ss/Warehouse#"+i+"/pallet_weight/uom");
//									if (pallet_weight1 != null) {
//										logger.info("pallet_weight: " + pallet_weight1.getValue());
//										if (pallet_weight1.getValue() != null) {
//											sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/pallet_weight/uom",
//													pallet_weight1.getValue());
//										}
//									}
									//sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/pallet_weight/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/pallet_weight/value"));
									//sallyItem.setAttributeValue("Variant_ss/Warehouse#"+i+"/pallet_weight/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Warehouse#"+i+"/pallet_weight/uom"));                   
									}
								List<? extends AttributeInstance> PackagingAttributes = collaborationItem.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes").getChildren();
								Integer PackagingAttributesSize = PackagingAttributes.size();
								logger.info("PackagingAttributes : " + PackagingAttributesSize);
								for (int i=0 ; i < PackagingAttributesSize ; i++) {
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_qty", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_qty"));
								
								AttributeInstance inner_pack_height = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/value");
								if (inner_pack_height != null) {
									logger.info("inner_pack_height: " + inner_pack_height.getValue());
									if (inner_pack_height.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/value",
												inner_pack_height.getValue());
									}
								}
								
								AttributeInstance inner_pack_height1 = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/uom");
								if (inner_pack_height1 != null) {
									logger.info("inner_pack_height: " + inner_pack_height1.getValue());
									if (inner_pack_height1.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/uom",
												inner_pack_height1.getValue());
									}
								}
								
								AttributeInstance inner_pack_width = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/value");
								if (inner_pack_width != null) {
									logger.info("inner_pack_width: " + inner_pack_width.getValue());
									if (inner_pack_width.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/value",
												inner_pack_width.getValue());
									}
								}
								
								AttributeInstance inner_pack_width1 = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/uom");
								if (inner_pack_width1 != null) {
									logger.info("inner_pack_width: " + inner_pack_width1.getValue());
									if (inner_pack_width1.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/uom",
												inner_pack_width1.getValue());
									}
								}



								AttributeInstance inner_pack_depth = collaborationItem
								.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/value");
						if (inner_pack_depth != null) {
							logger.info("inner_pack_depth: " + inner_pack_depth.getValue());
							if (inner_pack_depth.getValue() != null) {
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/value",
										inner_pack_depth.getValue());
							}
						}

						AttributeInstance inner_pack_depth1 = collaborationItem
								.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/uom");
						if (inner_pack_depth1 != null) {
							logger.info("inner_pack_depth: " + inner_pack_depth1.getValue());
							if (inner_pack_depth1.getValue() != null) {
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/uom",
										inner_pack_depth1.getValue());
							}
						}
						
						AttributeInstance inner_pack_weight = collaborationItem
								.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/value");
						if (inner_pack_weight != null) {
							logger.info("inner_pack_weight: " + inner_pack_weight.getValue());
							if (inner_pack_weight.getValue() != null) {
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/value",
										inner_pack_weight.getValue());
							}
						}
						
						AttributeInstance inner_pack_weight1 = collaborationItem
								.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/uom");
						if (inner_pack_weight1 != null) {
							logger.info("inner_pack_weight: " + inner_pack_weight1.getValue());
							if (inner_pack_weight1.getValue() != null) {
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/uom",
										inner_pack_weight1.getValue());
							}
						}
						
								
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_height/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_width/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_depth/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_weight/uom"));
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_barcode", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_barcode"));
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_pack_barcode_type", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_pack_barcode_type"));
								
								AttributeInstance inner_package_material = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_package_material/material_type");
								if (inner_package_material != null) {
									logger.info("inner_package_material: " + inner_package_material.getValue());
									if (inner_package_material.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_package_material/material_type",
												inner_package_material.getValue());
									}
								}
								
//								AttributeInstance inner_package_material1 = collaborationItem
//										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_package_material/weight");
//								if (inner_package_material1 != null) {
//									logger.info("inner_package_material: " + inner_package_material1.getValue());
//									if (inner_package_material1.getValue() != null) {
//										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_package_material/weight",
//												inner_package_material1.getValue());
//									}
//								}
								
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_package_material/material_type", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_package_material/material_type"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/inner_package_material/weight", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/inner_package_material/weight"));
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_qty", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_qty"));
								
								AttributeInstance outer_pack_height = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/value");
								if (outer_pack_height != null) {
									logger.info("outer_pack_height: " + outer_pack_height.getValue());
									if (outer_pack_height.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/value",
												outer_pack_height.getValue());
									}
								}
								
								AttributeInstance outer_pack_height1 = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/uom");
								if (outer_pack_height1 != null) {
									logger.info("outer_pack_height: " + outer_pack_height1.getValue());
									if (outer_pack_height1.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/uom",
												outer_pack_height1.getValue());
									}
								}
								
								AttributeInstance outer_pack_width = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/value");
								if (outer_pack_width != null) {
									logger.info("outer_pack_width: " + outer_pack_width.getValue());
									if (outer_pack_width.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/value",
												outer_pack_width.getValue());
									}
								}
								
								AttributeInstance outer_pack_width1 = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/uom");
								if (outer_pack_width1 != null) {
									logger.info("outer_pack_width: " + outer_pack_width1.getValue());
									if (outer_pack_width1.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/uom",
												outer_pack_width1.getValue());
									}
								}
								
								AttributeInstance outer_pack_depth = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/value");
								if (outer_pack_depth != null) {
									logger.info("outer_pack_depth: " + outer_pack_depth.getValue());
									if (outer_pack_depth.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/value",
												outer_pack_depth.getValue());
									}
								}
								
								AttributeInstance outer_pack_depth1 = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/uom");
								if (outer_pack_depth1 != null) {
									logger.info("outer_pack_depth: " + outer_pack_depth1.getValue());
									if (outer_pack_depth1.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/uom",
												outer_pack_depth1.getValue());
									}
								}
								
								AttributeInstance outer_pack_weight = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/value");
								if (outer_pack_weight != null) {
									logger.info("outer_pack_weight: " + outer_pack_weight.getValue());
									if (outer_pack_weight.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/value",
												outer_pack_weight.getValue());
									}
								}
								
								AttributeInstance outer_pack_weight1 = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/uom");
								if (outer_pack_weight1 != null) {
									logger.info("outer_pack_weight: " + outer_pack_weight1.getValue());
									if (outer_pack_weight1.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/uom",
												outer_pack_weight1.getValue());
									}
								}
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_height/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_width/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_depth/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/value"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_weight/uom"));
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_barcode", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_barcode"));
								sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_pack_barcode_type", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_pack_barcode_type"));
								
								AttributeInstance outer_package_material = collaborationItem
										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_package_material/material_type");
								if (outer_package_material != null) {
									logger.info("outer_package_material: " + outer_package_material.getValue());
									if (outer_package_material.getValue() != null) {
										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_package_material/material_type",
												outer_package_material.getValue());
									}
								}
								
//								AttributeInstance outer_package_material1 = collaborationItem
//										.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_package_material/weight");
//								if (outer_package_material1 != null) {
//									logger.info("outer_package_material: " + outer_package_material1.getValue());
//									if (outer_package_material1.getValue() != null) {
//										sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_package_material/weight",
//												outer_package_material1.getValue());
//									}
//								}
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_package_material/material_type", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_package_material/material_type"));
								//sallyItem.setAttributeValue("Variant_ss/Packaging Attributes#"+i+"/outer_package_material/weight", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#"+i+"/outer_package_material/weight"));
								
								}
								List<? extends AttributeInstance> ProductDimensions = collaborationItem.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions").getChildren();
								Integer ProductDimensionsSize = ProductDimensions.size();
								logger.info("ProductDimensions : " + ProductDimensionsSize);
								for (int i=0 ; i < ProductDimensionsSize ; i++) {
									AttributeInstance net_weight = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/net_weight/value");
									if (net_weight != null) {
										logger.info("net_weight: " + net_weight.getValue());
										if (net_weight.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/net_weight/value",
													net_weight.getValue());
										}
									}
									AttributeInstance net_weight1 = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/net_weight/uom");
									if (net_weight1 != null) {
										logger.info("net_weight: " + net_weight1.getValue());
										if (net_weight1.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/net_weight/uom",
													net_weight1.getValue());
										}
									}
									AttributeInstance gross_height = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_height/value");
									if (gross_height != null) {
										logger.info("gross_height: " + gross_height.getValue());
										if (gross_height.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_height/value",
													gross_height.getValue());
										}
									}
									AttributeInstance gross_height1 = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_height/uom");
									if (gross_height1 != null) {
										logger.info("gross_height: " + gross_height1.getValue());
										if (gross_height1.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_height/uom",
													gross_height1.getValue());
										}
									}
									AttributeInstance gross_width = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_width/value");
									if (gross_width != null) {
										logger.info("gross_width: " + gross_width.getValue());
										if (gross_width.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_width/value",
													gross_width.getValue());
										}
									}
									AttributeInstance gross_width1 = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_width/uom");
									if (gross_width1 != null) {
										logger.info("gross_width: " + gross_width1.getValue());
										if (gross_width1.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_width/uom",
													gross_width1.getValue());
										}
									}
									AttributeInstance gross_depth = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_depth/value");
									if (gross_depth != null) {
										logger.info("gross_depth: " + gross_depth.getValue());
										if (gross_depth.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_depth/value",
													gross_depth.getValue());
										}
									}
									AttributeInstance gross_depth1 = collaborationItem
											.getAttributeInstance("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_depth/uom");
									if (gross_depth1 != null) {
										logger.info("gross_depth: " + gross_depth1.getValue());
										if (gross_depth1.getValue() != null) {
											sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_depth/uom",
													gross_depth1.getValue());
										}
									}
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/net_weight/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/net_weight/value"));
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/net_weight/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/net_weight/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_height/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_height/value"));
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_height/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_height/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_width/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_width/value"));
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_width/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_width/uom"));
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_depth/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_depth/value"));
								//sallyItem.setAttributeValue("Variant_ss/Product Dimensions#"+i+"/gross_depth/uom", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Product Dimensions#"+i+"/gross_depth/uom"));
								}
								
								List<? extends AttributeInstance> Barcode = collaborationItem.getAttributeInstance("Sinelco_Variant_ss/Barcode").getChildren();
								Integer BarcodeSize = Barcode.size();
								logger.info(" Barcode: " + BarcodeSize);
								for (int i=0 ; i < BarcodeSize ; i++) {
								logger.info(collaborationItem.getAttributeValue("Sinelco_Variant_ss/Barcode#"+i+"/barcode_each_level"));
								logger.info(sallyItem.setAttributeValue("Variant_ss/Barcode#"+i+"/barcode_each_level", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Barcode#"+i+"/barcode_each_level")));				  
								sallyItem.setAttributeValue("Variant_ss/Barcode#"+i+"/barcode_type_each_level", collaborationItem.getAttributeValue("Sinelco_Variant_ss/Barcode#"+i+"/barcode_type_each_level"));                   
																	  }	
								
								List<? extends AttributeInstance>Pricing = collaborationItem.getAttributeInstance("Sinelco_Variant_ss/Pricing").getChildren();
								Integer PricingSize = Pricing.size();
								logger.info("Pricing : " + PricingSize);
								for (int i=0 ; i < PricingSize ; i++){	
								AttributeInstance PriceA = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/base_cost");
																
																if (PriceA != null) {
															logger.info("Local Value : " + PriceA.getValue());
															if (PriceA.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/base_cost",
																		PriceA.getValue());
																		}
																}
							
								AttributeInstance PriceC = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/nl_NL");
																
																if (PriceC != null) {
															logger.info("Local Value : " + PriceC.getValue());
															if (PriceC.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/nl_NL",
																		PriceC.getValue());
																		}}
																		AttributeInstance Priced = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/fr_FR");
																
																if (Priced != null) {
															logger.info("Local Value : " + Priced.getValue());
															if (Priced.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/fr_FR",
																		Priced.getValue());
																		}}
																		AttributeInstance Pricee = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/de_DE");
																
																if (Pricee != null) {
															logger.info("Local Value : " + Pricee.getValue());
															if (Pricee.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/de_DE",
																		Pricee.getValue());
																		}}
																		AttributeInstance Pricef = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/es_ES");
																
																if (Pricef != null) {
															logger.info("Local Value : " + Pricef.getValue());
															if (Pricef.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/es_ES",
																		Pricef.getValue());
																		}}
																		AttributeInstance Priceg = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/en_GB");
																
																if (Priceg != null) {
															logger.info("Local Value : " + Priceg.getValue());
															if (Priceg.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_retail_price/en_GB",
																		Priceg.getValue());
																		}}
																		
															AttributeInstance Priceh1 = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_trade/nl_NL");
																
																if (Priceh1 != null) {
															logger.info("Local Value : " + Priceh1.getValue());
															if (Priceh1.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_trade/nl_NL",
																		Priceh1.getValue());
																		}}
																		AttributeInstance Pricei = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_trade/fr_FR");
																
																if (Pricei != null) {
															logger.info("Local Value : " + Pricei.getValue());
															if (Pricei.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_trade/fr_FR",
																		Pricei.getValue());
																		}}
																		AttributeInstance Pricej = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_trade/de_DE");
																
																if (Pricej != null) {
															logger.info("Local Value : " + Pricej.getValue());
															if (Pricej.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_trade/de_DE",
																		Pricej.getValue());
																		}}
																		AttributeInstance Pricek = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_trade/es_ES");
																
																if (Pricek != null) {
															logger.info("Local Value : " + Pricek.getValue());
															if (Pricek.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_trade/es_ES",
																		Pricek.getValue());
																		}}
																		AttributeInstance Pricel = collaborationItem
																.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/vendor_recommended_trade/en_GB");
																
																if (Pricel != null) {
															logger.info("Local Value : " + Pricel.getValue());
															if (Pricel.getValue() != null) {
																sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/vendor_recommended_trade/en_GB",
																		Pricel.getValue());
																		}}
																		
																
																AttributeInstance PriceC1 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/nl_NL");
																		
																		if (PriceC1 != null) {
																	logger.info("Local Value : " + PriceC1.getValue());
																	if (PriceC1.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/nl_NL",
																				PriceC1.getValue());
																				}}
																				AttributeInstance Priced11 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/fr_FR");
																		
																		if (Priced11 != null) {
																	logger.info("Local Value : " + Priced11.getValue());
																	if (Priced11.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/fr_FR",
																				Priced11.getValue());
																				}}
																				AttributeInstance Pricee11 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/de_DE");
																		
																		if (Pricee11 != null) {
																	logger.info("Local Value : " + Pricee11.getValue());
																	if (Pricee11.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/de_DE",
																				Pricee11.getValue());
																				}}
																				AttributeInstance Pricef11 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/es_ES");
																		
																		if (Pricef11 != null) {
																	logger.info("Local Value : " + Pricef11.getValue());
																	if (Pricef11.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/es_ES",
																				Pricef11.getValue());
																				}}
																				AttributeInstance Priceg11 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/en_GB");
																		
																		if (Priceg11 != null) {
																	logger.info("Local Value : " + Priceg11.getValue());
																	if (Priceg11.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_excluding_vat/en_GB",
																				Priceg11.getValue());
																				}}
																				
																	AttributeInstance Priceh111 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_including_vat/nl_NL");
																		
																		if (Priceh111 != null) {
																	logger.info("Local Value : " + Priceh111.getValue());
																	if (Priceh111.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_including_vat/nl_NL",
																				Priceh111.getValue());
																				}}
																				AttributeInstance Pricei1 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_including_vat/fr_FR");
																		
																		if (Pricei1 != null) {
																	logger.info("Local Value : " + Pricei1.getValue());
																	if (Pricei1.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_including_vat/fr_FR",
																				Pricei1.getValue());
																				}}
																				AttributeInstance Pricej1 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_including_vat/de_DE");
																		
																		if (Pricej1 != null) {
																	logger.info("Local Value : " + Pricej1.getValue());
																	if (Pricej1.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_including_vat/de_DE",
																				Pricej1.getValue());
																				}}
																				AttributeInstance Pricek1 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_including_vat/es_ES");
																		
																		if (Pricek1 != null) {
																	logger.info("Local Value : " + Pricek1.getValue());
																	if (Pricek1.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_including_vat/es_ES",
																				Pricek1.getValue());
																				}}
																				AttributeInstance Pricel1 = collaborationItem
																		.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/professional_price_including_vat/en_GB");
																		
																		if (Pricel1 != null) {
																	logger.info("Local Value : " + Pricel1.getValue());
																	if (Pricel1.getValue() != null) {
																		sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/professional_price_including_vat/en_GB",
																				Pricel1.getValue());
																				}}
																				
																		
																		AttributeInstance PriceC11 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/nl_NL");
																				
																				if (PriceC11 != null) {
																			logger.info("Local Value : " + PriceC11.getValue());
																			if (PriceC11.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/nl_NL",
																						PriceC11.getValue());
																						}}
																						AttributeInstance Priced1111 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/fr_FR");
																				
																				if (Priced1111 != null) {
																			logger.info("Local Value : " + Priced1111.getValue());
																			if (Priced1111.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/fr_FR",
																						Priced1111.getValue());
																						}}
																						AttributeInstance Pricee1111 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/de_DE");
																				
																				if (Pricee1111 != null) {
																			logger.info("Local Value : " + Pricee1111.getValue());
																			if (Pricee1111.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/de_DE",
																						Pricee1111.getValue());
																						}}
																						AttributeInstance Pricef1111 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/es_ES");
																				
																				if (Pricef1111 != null) {
																			logger.info("Local Value : " + Pricef1111.getValue());
																			if (Pricef1111.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/es_ES",
																						Pricef1111.getValue());
																						}}
																						AttributeInstance Priceg1111 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/en_GB");
																				
																				if (Priceg1111 != null) {
																			logger.info("Local Value : " + Priceg1111.getValue());
																			if (Priceg1111.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_excluding_vat/en_GB",
																						Priceg1111.getValue());
																						}}
																						
																			AttributeInstance Priceh11111 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_including_vat/nl_NL");
																				
																				if (Priceh11111 != null) {
																			logger.info("Local Value : " + Priceh11111.getValue());
																			if (Priceh11111.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_including_vat/nl_NL",
																						Priceh11111.getValue());
																						}}
																						AttributeInstance Pricei11 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_including_vat/fr_FR");
																				
																				if (Pricei11 != null) {
																			logger.info("Local Value : " + Pricei11.getValue());
																			if (Pricei11.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_including_vat/fr_FR",
																						Pricei11.getValue());
																						}}
																						AttributeInstance Pricej11 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_including_vat/de_DE");
																				
																				if (Pricej11 != null) {
																			logger.info("Local Value : " + Pricej11.getValue());
																			if (Pricej11.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_including_vat/de_DE",
																						Pricej11.getValue());
																						}}
																						AttributeInstance Pricek11 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_including_vat/es_ES");
																				
																				if (Pricek11 != null) {
																			logger.info("Local Value : " + Pricek11.getValue());
																			if (Pricek11.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_including_vat/es_ES",
																						Pricek11.getValue());
																						}}
																						AttributeInstance Pricel11 = collaborationItem
																				.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/retail_price_including_vat/en_GB");
																				
																				if (Pricel11 != null) {
																			logger.info("Local Value : " + Pricel11.getValue());
																			if (Pricel11.getValue() != null) {
																				sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/retail_price_including_vat/en_GB",
																						Pricel11.getValue());
																						}}
																			
																				
																				AttributeInstance PriceC111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/nl_NL");
																						
																						if (PriceC111 != null) {
																					logger.info("Local Value : " + PriceC111.getValue());
																					if (PriceC111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/nl_NL",
																								PriceC111.getValue());
																								}}
																								AttributeInstance Priced111111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/fr_FR");
																						
																						if (Priced111111 != null) {
																					logger.info("Local Value : " + Priced111111.getValue());
																					if (Priced111111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/fr_FR",
																								Priced111111.getValue());
																								}}
																								AttributeInstance Pricee111111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/de_DE");
																						
																						if (Pricee111111 != null) {
																					logger.info("Local Value : " + Pricee111111.getValue());
																					if (Pricee111111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/de_DE",
																								Pricee111111.getValue());
																								}}
																								AttributeInstance Pricef111111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/es_ES");
																						
																						if (Pricef111111 != null) {
																					logger.info("Local Value : " + Pricef111111.getValue());
																					if (Pricef111111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/es_ES",
																								Pricef111111.getValue());
																								}}
																								AttributeInstance Priceg111111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/en_GB");
																						
																						if (Priceg111111 != null) {
																					logger.info("Local Value : " + Priceg111111.getValue());
																					if (Priceg111111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_excluding_vat/en_GB",
																								Priceg111111.getValue());
																								}}
																						
																					AttributeInstance Priceh1111111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/nl_NL");
																						
																						if (Priceh1111111 != null) {
																					logger.info("Local Value : " + Priceh1111111.getValue());
																					if (Priceh1111111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/nl_NL",
																								Priceh1111111.getValue());
																								}}
																								AttributeInstance Pricei111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/fr_FR");
																						
																						if (Pricei111 != null) {
																					logger.info("Local Value : " + Pricei111.getValue());
																					if (Pricei111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/fr_FR",
																								Pricei111.getValue());
																								}}
																								AttributeInstance Pricej111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/de_DE");
																						
																						if (Pricej111 != null) {
																					logger.info("Local Value : " + Pricej111.getValue());
																					if (Pricej111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/de_DE",
																								Pricej111.getValue());
																								}}
																								AttributeInstance Pricek111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/es_ES");
																						
																						if (Pricek111 != null) {
																					logger.info("Local Value : " + Pricek111.getValue());
																					if (Pricek111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/es_ES",
																								Pricek111.getValue());
																								}}
																								AttributeInstance Pricel111 = collaborationItem
																						.getAttributeInstance("Sinelco_Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/en_GB");
																						
																						if (Pricel111 != null) {
																					logger.info("Local Value : " + Pricel111.getValue());
																					if (Pricel111.getValue() != null) {
																						sallyItem.setAttributeValue("Variant_ss/Pricing#"+i+"/salon_success_price_including_vat/en_GB",
																								Pricel111.getValue());
																								}}
																				
																				
																		
								}

								sallyItem.setAttributeValue("Variant_ss/base_item", collaborationItem.getAttributeValue("Sinelco_Variant_ss/base_item"));
								sallyItem.setAttributeValue("Variant_ss/replacement_variant_id", collaborationItem.getAttributeValue("Sinelco_Variant_ss/replacement_variant_id"));
//								List<? extends AttributeInstance> ingredients = collaborationItem.getAttributeInstance("Sinelco_Variant_ss/ingredients").getChildren();
//								Integer ingredientsSize = ingredients.size();
//								logger.info("ingredients : " + ingredientsSize);
//								for (int i=0 ; i < ingredientsSize ; i++) {
//								sallyItem.setAttributeValue("Variant_ss/ingredients#"+i+"/value", collaborationItem.getAttributeValue("Sinelco_Variant_ss/ingredients#"+i+"/value"));
//								sallyItem.setAttributeValue("Variant_ss/ingredients#"+i+"/date", collaborationItem.getAttributeValue("Sinelco_Variant_ss/ingredients#"+i+"/date"));
//								}
								sallyItem.setAttributeValue("Variant_ss/warnings", collaborationItem.getAttributeValue("Sinelco_Variant_ss/warnings"));
								//sallyItem.setAttributeValue("Variant_ss/hazardous_hierarchy_name", collaborationItem.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_name"));
								//sallyItem.setAttributeValue("Variant_ss/hazardous_hierarchy_code", collaborationItem.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_code"));
								sallyItem.setAttributeValue("Variant_ss/is_vegan", collaborationItem.getAttributeValue("Sinelco_Variant_ss/is_vegan"));
								sallyItem.setAttributeValue("Variant_ss/assortment/trade/nl_NL", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/trade/nl_NL"));
								sallyItem.setAttributeValue("Variant_ss/assortment/trade/fr_FR", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/trade/fr_FR"));
								sallyItem.setAttributeValue("Variant_ss/assortment/trade/de_DE", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/trade/de_DE"));
								sallyItem.setAttributeValue("Variant_ss/assortment/trade/es_ES", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/trade/es_ES"));
								sallyItem.setAttributeValue("Variant_ss/assortment/trade/en_GB", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/trade/en_GB"));
								sallyItem.setAttributeValue("Variant_ss/assortment/retail/nl_NL", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/retail/nl_NL"));
								sallyItem.setAttributeValue("Variant_ss/assortment/retail/fr_FR", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/retail/fr_FR"));
								sallyItem.setAttributeValue("Variant_ss/assortment/retail/de_DE", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/retail/de_DE"));
								sallyItem.setAttributeValue("Variant_ss/assortment/retail/es_ES", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/retail/es_ES"));
								sallyItem.setAttributeValue("Variant_ss/assortment/retail/en_GB", collaborationItem.getAttributeValue("Sinelco_Variant_ss/assortment/retail/en_GB"));
								sallyItem.setAttributeValue("Variant_ss/status", collaborationItem.getAttributeValue("Sinelco_Variant_ss/status"));
								sallyItem.setAttributeValue("Variant_ss/image_reference", collaborationItem.getAttributeValue("Sinelco_Variant_ss/image_reference"));
								sallyItem.setAttributeValue("Variant_ss/supplier_lead_time", collaborationItem.getAttributeValue("Sinelco_Variant_ss/supplier_lead_time"));
								sallyItem.setAttributeValue("Variant_ss/click_and_collect", collaborationItem.getAttributeValue("Sinelco_Variant_ss/click_and_collect"));
								sallyItem.setAttributeValue("Variant_ss/deliver_to_store", collaborationItem.getAttributeValue("Sinelco_Variant_ss/deliver_to_store"));
		
							}
							sallyItem.save();
							logger.info("created sally item"+sallyItem.getPrimaryKey());
							sally_collab.moveToNextStep(sallyItem,sally_collab.getStep("INITIAL"), "SUCCESS");
		logger.info("*** End of OUT function of Create Step ***");
	}
	}
	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}

