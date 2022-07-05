package com.sally.pim.workflows.sallyMaintenanceWorkflow;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.*;

import com.ibm.pim.attribute.AttributeChanges;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.attribute.AttributeOwner;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;

public class SallyMaintenanceSuccessStep implements WorkflowStepFunction {

	private static Logger logger = LogManager.getLogger(SallyMaintenanceSuccessStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("SallyMaintenanceSuccessSteps - Initiated In1111");
		PIMCollection<CollaborationItem> items = arg0.getItems();
		Context ctx = PIMContextFactory.getCurrentContext();
		Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally_Products_Catalog");
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		for (CollaborationItem item : items) {

			try {
				AttributeOwner attOwner = item.getSourceItem().cloneAttributeOwner(true);
				AttributeChanges attributeChanges = item.getAttributeChangesComparedTo(attOwner);
				logger.info("Updated instances : " + attributeChanges.getModifiedAttributesWithNewData().size());
				logger.info("Deleted instances : " + attributeChanges.getDeletedAttributes().size());
				logger.info("Newly instances : " + attributeChanges.getNewlyAddedAttributes().size());

				XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
				xmlStreamWriter.writeStartDocument();
				Object entityType = item.getAttributeValue("Product_c/entity_type");
				Map<String, String> modifiedInstanceMap = new HashMap<String, String>();
				Map<String, String> deletedInstanceMap = new HashMap<String, String>();
				Map<String, String> newlyAddedInstanceMap = new HashMap<String, String>();
				String entityString = "";
				if (entityType != null) {
					entityString = entityType.toString();
				}
				logger.info("Entity String : " + entityString);
				xmlStreamWriter.writeStartElement("Modified_Product_Attributes");

				for (int x = 0; x < attributeChanges.getModifiedAttributesWithNewData().size(); x++) {
					logger.info("Modified attributes path for test are : "
							+ attributeChanges.getModifiedAttributesWithNewData().get(x).getPath());
					logger.info("Modified attributes value for test are : "
							+ attributeChanges.getModifiedAttributesWithNewData().get(x).getDisplayValue());
					modifiedInstanceMap.put(attributeChanges.getModifiedAttributesWithNewData().get(x).getPath(),
							attributeChanges.getModifiedAttributesWithNewData().get(x).getDisplayValue());
				}

				for (int x = 0; x < attributeChanges.getDeletedAttributes().size(); x++) {
					logger.info("Deleted attributes path for test are : "
							+ attributeChanges.getDeletedAttributes().get(x).getPath());
					logger.info("Deleted attributes value for test are : "
							+ attributeChanges.getDeletedAttributes().get(x).getDisplayValue());
					deletedInstanceMap.put(attributeChanges.getDeletedAttributes().get(x).getPath(),
							attributeChanges.getDeletedAttributes().get(x).getDisplayValue());
				}

				for (int x = 0; x < attributeChanges.getNewlyAddedAttributes().size(); x++) {
					logger.info("Newly Added attributes path for test are : "
							+ attributeChanges.getNewlyAddedAttributes().get(x).getPath());
					logger.info("Newly Added attributes value for test are : "
							+ attributeChanges.getNewlyAddedAttributes().get(x).getDisplayValue());
					newlyAddedInstanceMap.put(attributeChanges.getNewlyAddedAttributes().get(x).getPath(),
							attributeChanges.getNewlyAddedAttributes().get(x).getDisplayValue());
				}

				int coreCount = 0;

				if (entityString.equalsIgnoreCase("Item")) {
					if (coreCount == 0) {
						xmlStreamWriter.writeStartElement("Product_c");
						xmlStreamWriter = coreAttributesXmlFunction(xmlStreamWriter, item);
						xmlStreamWriter.writeEndElement();
						coreCount++;
					}
					
					for (Map.Entry<String, String> entry : modifiedInstanceMap.entrySet()) {
						logger.info(entry.getKey() + " Key:Value " + entry.getValue().toString());
					}
					xmlStreamWriter.writeStartElement("Item_ss");
					xmlStreamWriter.writeStartElement("Erp_Item_Id");
					xmlStreamWriter.writeStartElement("Item_Id");

					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/erp_item_id/item_id") != null)
							? modifiedInstanceMap.get("/Item_ss/erp_item_id/item_id")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Source");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/erp_item_id/source") != null)
							? modifiedInstanceMap.get("/Item_ss/erp_item_id/source")
							: ""));

					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Replacement_Item_Id");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/replacement_item_id") != null)
							? modifiedInstanceMap.get("/Item_ss/replacement_item_id")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Kit_Listing");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/kit_listing") != null)
							? modifiedInstanceMap.get("/Item_ss/kit_listing")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Item_Type");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/item_type") != null)
							? modifiedInstanceMap.get("/Item_ss/item_type")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Features_and_Benefits");
					xmlStreamWriter.writeStartElement("es_ES");
					xmlStreamWriter
							.writeCharacters(((modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/es_ES") != null)
									? modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/es_ES")
									: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("nl_NL");
					xmlStreamWriter
							.writeCharacters(((modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/nl_NL")
									: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("fr_FR");
					xmlStreamWriter
							.writeCharacters(((modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/fr_FR")
									: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("en_GB");
					xmlStreamWriter
							.writeCharacters(((modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/en_GB") != null)
									? modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/en_GB")
									: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("nl_BE");
					xmlStreamWriter
							.writeCharacters(((modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/nl_BE")
									: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("de_DE");
					xmlStreamWriter
							.writeCharacters(((modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/de_DE") != null)
									? modifiedInstanceMap.get("/Item_ss/features_and_benefits#0/de_DE")
									: ""));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Minimum_order_quantity");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/minimum_order_quantity") != null)
							? modifiedInstanceMap.get("/Item_ss/minimum_order_quantity")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("No_Discount_allowed");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/no_discount_allowed") != null)
							? modifiedInstanceMap.get("/Item_ss/no_discount_allowed")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Serial_tracked_item");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/serial_tracked_item") != null)
							? modifiedInstanceMap.get("/Item_ss/serial_tracked_item")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Commodity_Code");
					xmlStreamWriter.writeStartElement("nl_NL");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/commodity_code#0/nl_NL") != null)
							? modifiedInstanceMap.get("/Item_ss/commodity_code#0/nl_NL")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("fr_FR");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/commodity_code#0/fr_FR") != null)
							? modifiedInstanceMap.get("/Item_ss/commodity_code#0/fr_FR")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("de_DE");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/commodity_code#0/de_DE") != null)
							? modifiedInstanceMap.get("/Item_ss/commodity_code#0/de_DE")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("en_GB");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/commodity_code#0/en_GB") != null)
							? modifiedInstanceMap.get("/Item_ss/commodity_code#0/en_GB")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("nl_BE");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/commodity_code#0/nl_BE") != null)
							? modifiedInstanceMap.get("/Item_ss/commodity_code#0/nl_BE")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("es_ES");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/commodity_code#0/es_ES") != null)
							? modifiedInstanceMap.get("/Item_ss/commodity_code#0/es_ES")
							: ""));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Country_of_origin");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/country_of_origin") != null)
							? modifiedInstanceMap.get("/Item_ss/country_of_origin")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Keywords");
					xmlStreamWriter.writeStartElement("nl_NL");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/keywords#0/nl_NL") != null)
							? modifiedInstanceMap.get("/Item_ss/keywords#0/nl_NL")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("fr_FR");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/keywords#0/fr_FR") != null)
							? modifiedInstanceMap.get("/Item_ss/keywords#0/fr_FR")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("de_DE");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/keywords#0/de_DE") != null)
							? modifiedInstanceMap.get("/Item_ss/keywords#0/de_DE")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("en_GB");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/keywords#0/en_GB") != null)
							? modifiedInstanceMap.get("/Item_ss/keywords#0/en_GB")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("nl_BE");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/keywords#0/nl_BE") != null)
							? modifiedInstanceMap.get("/Item_ss/keywords#0/nl_BE")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("es_ES");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/keywords#0/es_ES") != null)
							? modifiedInstanceMap.get("/Item_ss/keywords#0/es_ES")
							: ""));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Dim_group_id");
					xmlStreamWriter.writeStartElement("nl_NL");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/dim_group_id#0/nl_NL") != null)
							? modifiedInstanceMap.get("/Item_ss/dim_group_id#0/nl_NL")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("fr_FR");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/dim_group_id#0/fr_FR") != null)
							? modifiedInstanceMap.get("/Item_ss/dim_group_id#0/fr_FR")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("de_DE");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/dim_group_id#0/de_DE") != null)
							? modifiedInstanceMap.get("/Item_ss/dim_group_id#0/de_DE")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("en_GB");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/dim_group_id#0/en_GB") != null)
							? modifiedInstanceMap.get("/Item_ss/dim_group_id#0/en_GB")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("nl_BE");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/dim_group_id#0/nl_BE") != null)
							? modifiedInstanceMap.get("/Item_ss/dim_group_id#0/nl_BE")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("es_ES");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/dim_group_id#0/es_ES") != null)
							? modifiedInstanceMap.get("/Item_ss/dim_group_id#0/es_ES")
							: ""));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Item_group_type");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/item_group_type") != null)
							? modifiedInstanceMap.get("/Item_ss/item_group_type")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Retail_group_id");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/retail_group_id") != null)
							? modifiedInstanceMap.get("/Item_ss/retail_group_id")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Retail_department");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/retail_department") != null)
							? modifiedInstanceMap.get("/Item_ss/retail_department")
							: ""));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Retail_group");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/retail_group") != null)
							? modifiedInstanceMap.get("/Item_ss/retail_group")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Commission_group_id");
					xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Item_ss/commission_group_id") != null)
							? modifiedInstanceMap.get("/Item_ss/commission_group_id")
							: ""));

					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Variants");
					AttributeInstance attrInstance = item.getAttributeInstance("Item_ss/variants");

					if (attrInstance != null) {
						for (int x = 0; x < attrInstance.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("Variant_ID");
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Item_ss/variants#" + x + "/variant_id") == null) ? ""
											: item.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString()));
							xmlStreamWriter.writeEndElement();
						}
					}
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					logger.info("Writing the variant data in XML ....");

					xmlStreamWriter.writeStartElement("Variant_Info");
					AttributeInstance variantInstance = item.getAttributeInstance("Item_ss/variants");

					if (variantInstance != null) {
						for (int x = 0; x < variantInstance.getChildren().size(); x++) {
							Item varItem = null;
							xmlStreamWriter.writeStartElement("Variant");
							xmlStreamWriter.writeStartElement("Variant_ID");
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Item_ss/variants#" + x + "/variant_id") == null) ? ""
											: item.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString()));

							String varId = item.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString();
							logger.info("varId .... " + varId);
							varItem = sallyCatalog.getItemByPrimaryKey(varId);

							if (varItem == null) {
								logger.info("Var item not found in catalog");
								String colQuery = "select item from collaboration_area ('Sally_New_Product_Publish_ColArea') where item['Product_c/enterprise_item_id'] ='"
										+ varId + "'";
								logger.info("colQuery: {}" + colQuery);
								SearchQuery selectSearchQuery = ctx.createSearchQuery(colQuery);
								SearchResultSet selectResultSet = selectSearchQuery.execute();
								logger.info("Result Set Size: {}" + selectResultSet.size());

								while (selectResultSet.next()) {
									varItem = selectResultSet.getItem(1);
								}
							}
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeStartElement("Product_c");
							if (varItem != null) {
								logger.info("getting core attribute details of variant item");
								xmlStreamWriter = coreAttributesXmlFunctionForVariant(xmlStreamWriter, varItem);
							}
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeStartElement("Variant_ss");
							if (varItem != null) {
								logger.info("getting variant attribute details of variant item");
								xmlStreamWriter = variantAttributesXmlFunction(xmlStreamWriter, varItem);
							}
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
						}
					}
					
				}

				else if (entityString.equalsIgnoreCase("Variant")) {					
				  if (coreCount == 0) { 
					  xmlStreamWriter.writeStartElement("Product_c");
					  xmlStreamWriter = coreAttributesXmlFunction(xmlStreamWriter, item);
					  xmlStreamWriter.writeEndElement(); 
					  coreCount++;

					  	xmlStreamWriter.writeStartElement("Variant_ss");
						logger.info("Entered variantAttributes func catalog Item");
						xmlStreamWriter.writeStartElement("ERP_Variant_Id");
						xmlStreamWriter.writeStartElement("RBO_Variant_Id");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/erp_variant_id/rbovariantid") != null)
								? modifiedInstanceMap.get("/Variant_ss/erp_variant_id/rbovariantid")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Source");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/erp_variant_id/source") != null)
								? modifiedInstanceMap.get("/Variant_ss/erp_variant_id/source")
								: ""));
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Manufacturer_Id");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/manufacturer_id") != null)
								? modifiedInstanceMap.get("/Variant_ss/manufacturer_id")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Oe_Item_Code");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/oe_item_code") != null)
								? modifiedInstanceMap.get("/Variant_ss/oe_item_code")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Buyer");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/buyer") != null)
								? modifiedInstanceMap.get("/Variant_ss/buyer")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Kvi");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/kvi") != null)
								? modifiedInstanceMap.get("/Variant_ss/kvi")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Route_to_customer");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/route_to_customer") != null)
								? modifiedInstanceMap.get("/Variant_ss/route_to_customer")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Trade_card_restricted");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/trade_card_restricted") != null)
								? modifiedInstanceMap.get("/Variant_ss/trade_card_restricted")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("qpbunmlgr");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/qpbunmlgr") != null)
								? modifiedInstanceMap.get("/Variant_ss/qpbunmlgr")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("qpbqtymlgr");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/qpbqtymlgr") != null)
								? modifiedInstanceMap.get("/Variant_ss/qpbqtymlgr")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Colour_family");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/colour_family") != null)
								? modifiedInstanceMap.get("/Variant_ss/colour_family")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Colour_shade");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/colour_shade") != null)
								? modifiedInstanceMap.get("/Variant_ss/colour_shade")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Colour_class");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/colour_class") != null)
								? modifiedInstanceMap.get("/Variant_ss/colour_class")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Colour_id");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/colour_id") != null)
								? modifiedInstanceMap.get("/Variant_ss/colour_id")
								: ""));
						xmlStreamWriter.writeEndElement();
						logger.info("1111111111111111111");
						xmlStreamWriter.writeStartElement("Airflow");
						xmlStreamWriter.writeStartElement("Value");
						if(modifiedInstanceMap.get("/Variant_ss/airflow/value") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/airflow/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/airflow/value")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/airflow/value") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/airflow/value") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/airflow/value")
									: ""));
						}
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Uom");
						if(modifiedInstanceMap.get("/Variant_ss/airflow/uom") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/airflow/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/airflow/uom")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/airflow/uom") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/airflow/uom") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/airflow/uom")
									: ""));
						}
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Max_temp");
						xmlStreamWriter.writeStartElement("Value");
						if(modifiedInstanceMap.get("/Variant_ss/max_temp/value") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/max_temp/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/max_temp/value")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/max_temp/value") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/max_temp/value") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/max_temp/value")
									: ""));
						}
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Uom");
						if(modifiedInstanceMap.get("/Variant_ss/max_temp/uom") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/max_temp/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/max_temp/uom")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/max_temp/uom") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/max_temp/uom") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/max_temp/uom")
									: ""));
						}
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Min_temp");
						xmlStreamWriter.writeStartElement("Value");
						if(modifiedInstanceMap.get("/Variant_ss/min_temp/value") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/min_temp/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/min_temp/value")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/min_temp/value") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/min_temp/value") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/min_temp/value")
									: ""));
						}
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Uom");
						if(modifiedInstanceMap.get("/Variant_ss/min_temp/uom") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/min_temp/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/min_temp/uom")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/min_temp/uom") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/min_temp/uom") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/min_temp/uom")
									: ""));
						}
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Contents");
						xmlStreamWriter.writeStartElement("Value");
						if(modifiedInstanceMap.get("/Variant_ss/contents/value") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/contents/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/contents/value")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/contents/value") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/contents/value") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/contents/value")
									: ""));
						}
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Uom");
						if(modifiedInstanceMap.get("/Variant_ss/contents/uom") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/contents/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/contents/uom")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/contents/uom") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/contents/uom") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/contents/uom")
									: ""));
						}
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Watt");
						xmlStreamWriter.writeStartElement("Value");
						if(modifiedInstanceMap.get("/Variant_ss/watt/value") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/watt/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/watt/value")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/watt/value") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/watt/value") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/watt/value")
									: ""));
						}
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Uom");
						if(modifiedInstanceMap.get("/Variant_ss/watt/uom") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/watt/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/watt/uom")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/watt/uom") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/watt/uom") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/watt/uom")
									: ""));
						}
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Outer_diameter");
						xmlStreamWriter.writeStartElement("Value");
						if(modifiedInstanceMap.get("/Variant_ss/outer_diameter/value") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/outer_diameter/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/outer_diameter/value")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/outer_diameter/value") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/outer_diameter/value") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/outer_diameter/value")
									: ""));
						}
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Uom");
						if(modifiedInstanceMap.get("/Variant_ss/outer_diameter/uom") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/outer_diameter/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/outer_diameter/uom")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/outer_diameter/uom") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/outer_diameter/uom") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/outer_diameter/uom")
									: ""));
						}
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Inner_diameter");
						xmlStreamWriter.writeStartElement("Value");
						if(modifiedInstanceMap.get("/Variant_ss/inner_diameter/value") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/inner_diameter/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/inner_diameter/value")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/inner_diameter/value") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/inner_diameter/value") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/inner_diameter/value")
									: ""));
						}
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Uom");
						if(modifiedInstanceMap.get("/Variant_ss/inner_diameter/uom") != null) {
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/inner_diameter/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/inner_diameter/uom")
									: ""));
						}
						if(newlyAddedInstanceMap.get("/Variant_ss/inner_diameter/uom") != null) {
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/inner_diameter/uom") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/inner_diameter/uom")
									: ""));
						}
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						logger.info("22222222222222222222");
						xmlStreamWriter.writeStartElement("Web_assortment");
						AttributeInstance assortmentWebInstance = item.getAttributeInstance("Variant_ss/web_assortment");

						if (assortmentWebInstance != null) {
							for (int x = 0; x < assortmentWebInstance.getChildren().size(); x++) {

								xmlStreamWriter.writeStartElement("Trade");
								xmlStreamWriter.writeStartElement("es_ES");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/es_ES") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/es_ES") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/es_ES")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/es_ES") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/es_ES") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/es_ES")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_NL");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_NL") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_NL") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_NL")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_NL") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_NL") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_NL")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("fr_FR");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/fr_FR") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/fr_FR") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/fr_FR")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/fr_FR") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/fr_FR") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/fr_FR")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("en_GB");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/en_GB") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/en_GB") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/en_GB")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/en_GB") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/en_GB") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/en_GB")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("de_DE");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/de_DE") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/de_DE") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/de_DE")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/de_DE") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/de_DE") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/de_DE")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_BE");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_BE") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_BE") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_BE")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_BE") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_BE") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/trade#0/nl_BE")
											: ""));
								}
								xmlStreamWriter.writeEndElement();
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("Retail");
								xmlStreamWriter.writeStartElement("es_ES");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/es_ES") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/es_ES") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/es_ES")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/es_ES") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/es_ES") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/es_ES")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_NL");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_NL") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_NL") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_NL")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_NL") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_NL") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_NL")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("fr_FR");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/fr_FR") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/fr_FR") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/fr_FR")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/fr_FR") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/fr_FR") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/fr_FR")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("en_GB");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/en_GB") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/en_GB") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/en_GB")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/en_GB") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/en_GB") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/en_GB")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("de_DE");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/de_DE") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/de_DE") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/de_DE")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/de_DE") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/de_DE") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/de_DE")
											: ""));
								}
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_BE");
								if(modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_BE") != null) {
									xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_BE") != null)
											? modifiedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_BE")
											: ""));
								}
								if(newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_BE") != null) {
									xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_BE") != null)
											? newlyAddedInstanceMap.get("/Variant_ss/web_assortment/retail#0/nl_BE")
											: ""));
								}
								xmlStreamWriter.writeEndElement();
								xmlStreamWriter.writeEndElement();

							}
						}
						xmlStreamWriter.writeEndElement();
						logger.info("333333333333333333333");

						xmlStreamWriter.writeStartElement("Web_Customer_facing_lead_time");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_customer_facing_lead_time") != null)
								? modifiedInstanceMap.get("/Variant_ss/web_customer_facing_lead_time")
								: ""));
						xmlStreamWriter.writeEndElement();

						logger.info("33333331111111111");

						xmlStreamWriter.writeStartElement("Hair_solution");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/hair_solution") != null)
								? modifiedInstanceMap.get("/Variant_ss/hair_solution")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Web_limited_edition");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_limited_edition") != null)
								? modifiedInstanceMap.get("/Variant_ss/web_limited_edition")
								: ""));
						xmlStreamWriter.writeEndElement();

						logger.info("333333322222222222222");
						xmlStreamWriter.writeStartElement("Web_online_date_trade");
						AttributeInstance webOnlineTradeInstance = item.getAttributeInstance("Variant_ss/web_online_date_trade");

						if (webOnlineTradeInstance != null) {
							for (int x = 0; x < webOnlineTradeInstance.getChildren().size(); x++) {

								xmlStreamWriter.writeStartElement("es_ES");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/es_ES") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/es_ES")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_NL");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/nl_NL") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/nl_NL")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("fr_FR");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/fr_FR") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/fr_FR")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("en_GB");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/en_GB") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/en_GB")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("de_DE");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/de_DE") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/de_DE")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_BE");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/nl_BE") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_trade#0/nl_BE")
										: ""));
								xmlStreamWriter.writeEndElement();

							}
						}

						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeStartElement("Web_online_date_retail");
						AttributeInstance webOnlineRetailInstance = item.getAttributeInstance("Variant_ss/web_online_date_retail");

						if (webOnlineRetailInstance != null) {
							for (int x = 0; x < webOnlineRetailInstance.getChildren().size(); x++) {

								xmlStreamWriter.writeStartElement("es_ES");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/es_ES") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/es_ES")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_NL");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/nl_NL") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/nl_NL")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("fr_FR");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/fr_FR") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/fr_FR")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("en_GB");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/en_GB") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/en_GB")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("de_DE");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/de_DE") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/de_DE")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("nl_BE");
								xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/nl_BE") != null)
										? modifiedInstanceMap.get("/Variant_ss/web_online_date_retail#0/nl_BE")
										: ""));
								xmlStreamWriter.writeEndElement();

							}
						}

						xmlStreamWriter.writeEndElement();
						logger.info("555555555555555555555555");
						xmlStreamWriter.writeStartElement("Web_quantity_restriction");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_quantity_restriction") != null)
								? modifiedInstanceMap.get("/Variant_ss/web_quantity_restriction")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Web_searchable");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_searchable") != null)
								? modifiedInstanceMap.get("/Variant_ss/web_searchable")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Web_trade_restrict");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/web_trade_restrict") != null)
								? modifiedInstanceMap.get("/Variant_ss/web_trade_restrict")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Variant_differentiators");
						xmlStreamWriter.writeStartElement("Colour");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/variant_differentiators/colour") != null)
								? modifiedInstanceMap.get("/Variant_ss/variant_differentiators/colour")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Size");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/variant_differentiators/size") != null)
								? modifiedInstanceMap.get("/Variant_ss/variant_differentiators/size")
								: ""));
						xmlStreamWriter.writeEndElement();
						logger.info("VVVVVVVVVVVVVVVVV");
						xmlStreamWriter.writeStartElement("Style");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/variant_differentiators/style") != null)
								? modifiedInstanceMap.get("/Variant_ss/variant_differentiators/style")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Fragrance");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/variant_differentiators/fragrance") != null)
								? modifiedInstanceMap.get("/Variant_ss/variant_differentiators/fragrance")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Type");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/variant_differentiators/type") != null)
								? modifiedInstanceMap.get("/Variant_ss/variant_differentiators/type")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Configuration");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/variant_differentiators/configuration") != null)
								? modifiedInstanceMap.get("/Variant_ss/variant_differentiators/configuration")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Strength");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/variant_differentiators/strength") != null)
								? modifiedInstanceMap.get("/Variant_ss/variant_differentiators/strength")
								: ""));
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Page_number");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/page_number") != null)
								? modifiedInstanceMap.get("/Variant_ss/page_number")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("New_icon");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/new_icon") != null)
								? modifiedInstanceMap.get("/Variant_ss/new_icon")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Info_block");
						xmlStreamWriter.writeStartElement("Text");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/info_block/text") != null)
								? modifiedInstanceMap.get("/Variant_ss/info_block/text")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Image");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/info_block/image") != null)
								? modifiedInstanceMap.get("/Variant_ss/info_block/image")
								: ""));
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();
						logger.info("66666666666666666666");
						AttributeInstance warehouseInstance = item.getAttributeInstance("Variant_ss/Warehouse");

						for (int x = 0; x < warehouseInstance.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("Warehouse");
							xmlStreamWriter.writeStartElement("Ship_in_pallets");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/ship_in_pallets") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/ship_in_pallets")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Pick_instructions");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/pick_instructions") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/pick_instructions")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Packable");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/packable") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/packable")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Conveyable");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/conveyable") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/conveyable")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Stackable");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/stackable") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/stackable")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Pallet_type");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/pallet_type") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/pallet_type")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Value_added_service_id");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/value_added_service_id") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/value_added_service_id")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Language_Dependent");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warehouse/languagedependent") != null)
									? modifiedInstanceMap.get("/Variant_ss/warehouse/languagedependent")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
							
						}

						logger.info("777777777777777777777777");
						AttributeInstance packagingInstance = item.getAttributeInstance("Variant_ss/Packaging Attributes");

						for (int x = 0; x < packagingInstance.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("Packaging_attribute");
							xmlStreamWriter.writeStartElement("Inner_pack_qty");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_qty") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_qty")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Inner_pack_height");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_height/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_height/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_height/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_height/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Inner_pack_width");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_width/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_width/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_width/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_width/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Inner_pack_depth");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_depth/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_depth/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_depth/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_depth/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Inner_pack_weight");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_weight/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_weight/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_weight/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_weight/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Inner_package_material");
							xmlStreamWriter.writeStartElement("Material_type");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_package_material/material_type") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_package_material/material_type")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Weight");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_package_material/weight/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_package_material/weight/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_package_material/weight/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_package_material/weight/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
							
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Inner_pack_barcode_type");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_barcode_type") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_barcode_type")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Inner_pack_barcode");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_barcode") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/inner_pack_barcode")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_pack_qty");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_qty") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_qty")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_pack_height");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_height/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_height/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_height/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_height/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_pack_width");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_width/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_width/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_width/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_width/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_pack_depth");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_depth/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_depth/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_depth/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_depth/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_pack_weight");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_weight/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_weight/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_weight/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_weight/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_package_material");
							xmlStreamWriter.writeStartElement("Material_type");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_package_material/material_type") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_package_material/material_type")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Weight");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_package_material/weight/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_package_material/weight/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_package_material/weight/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_package_material/weight/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_pack_barcode_type");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_barcode_type") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_barcode_type")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Outer_pack_barcode");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_barcode") != null)
									? modifiedInstanceMap.get("/Variant_ss/Packaging Attributes/outer_pack_barcode")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

						}

						logger.info("8888888888888888888");
						xmlStreamWriter.writeStartElement("Product_Dimensions");
						AttributeInstance prodDimenInst = item.getAttributeInstance("Variant_ss/Product Dimensions");

						for (int x = 0; x < prodDimenInst.getChildren().size(); x++) {

							logger.info("Inside Product DImensions for loop");
							xmlStreamWriter.writeStartElement("Outers_per_layer");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/outers_per_layer") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/outers_per_layer")
									: ""));
							xmlStreamWriter.writeEndElement();

							logger.info("Product DImensions outers per layer"
									+ item.getAttributeInstance("Variant_ss/Product Dimensions#" + x + "/outers_per_layer").getValue()
											.toString());
							xmlStreamWriter.writeStartElement("Layers_per_pallete");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/layers_per_pallete") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/layers_per_pallete")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Net_weight");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/net_weight/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/net_weight/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/net_weight/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/net_weight/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Gross_height");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_height/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_height/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_height/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_height/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Gross_width");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_width/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_width/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_width/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_width/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Gross_depth");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_depth/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_depth/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_depth/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/gross_depth/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Pallet_weight");
							xmlStreamWriter.writeStartElement("Value");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/pallet_weight/value") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/pallet_weight/value")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Uom");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Product Dimensions/pallet_weight/uom") != null)
									? modifiedInstanceMap.get("/Variant_ss/Product Dimensions/pallet_weight/uom")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

						}
						logger.info("999999999999999999");
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Barcodes");
						AttributeInstance barcodeInstance = item.getAttributeInstance("Variant_ss/Barcode");

						for (int x = 0; x < barcodeInstance.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("Barcode");
							
							xmlStreamWriter.writeStartElement("OperationType");
							xmlStreamWriter.writeCharacters("Add");
							xmlStreamWriter.writeEndElement();
							
							xmlStreamWriter.writeStartElement("Barcode_type_each_level");
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/Barcode#"+x+"/barcode_type_each_level") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/Barcode#"+x+"/barcode_type_each_level")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Barcode_each_level");
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/Barcode#"+x+"/barcode_each_level") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/Barcode#"+x+"/barcode_each_level")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Barcode_date_created");
							xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/Barcode#"+x+"/barcode_date_created") != null)
									? newlyAddedInstanceMap.get("/Variant_ss/Barcode#"+x+"/barcode_date_created")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
						}
						
						Set<String> set = deletedInstanceMap.keySet()
			                     .stream()
			                     .filter(s -> s.contains("Variant_ss/Barcode"))
			                     .collect(Collectors.toSet());
						
						if(set.size() > 0) {
							xmlStreamWriter.writeStartElement("Barcode");
							
							xmlStreamWriter.writeStartElement("OperationType");
							xmlStreamWriter.writeCharacters("Del");
							xmlStreamWriter.writeEndElement();
							
							xmlStreamWriter.writeStartElement("Barcode_type_each_level");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Barcode#0/barcode_type_each_level") != null)
									? deletedInstanceMap.get("/Variant_ss/Barcode#0/barcode_type_each_level")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Barcode_each_level");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Barcode#0/barcode_each_level") != null)
									? deletedInstanceMap.get("/Variant_ss/Barcode#0/barcode_each_level")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Barcode_date_created");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Barcode#0/barcode_date_created") != null)
									? deletedInstanceMap.get("/Variant_ss/Barcode#0/barcode_date_created")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
						}
						
						logger.info("10101010101010");
						xmlStreamWriter.writeEndElement();

						AttributeInstance pricingInstance = item.getAttributeInstance("Variant_ss/Pricing");

						for (int x = 0; x < pricingInstance.getChildren().size(); x++) {
							logger.info("Inside Pricing for loop");
							xmlStreamWriter.writeStartElement("Pricing");
							xmlStreamWriter.writeStartElement("Base_cost");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/base_cost") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/base_cost")
									: ""));
							xmlStreamWriter.writeEndElement();

							logger.info("Inside Pricing for loop11111");
							xmlStreamWriter.writeStartElement("Vendor_recommended_retail_price");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();
							logger.info("Inside Pricing for loop22222");
							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_retail_price#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
							logger.info("Inside Pricing for loop333333");

							xmlStreamWriter.writeStartElement("Vendor_recommended_trade");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/vendor_recommended_trade#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							logger.info("Inside Pricing for loop444444");
							xmlStreamWriter.writeStartElement("Professional_price_excluding_vat");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/nl_NL")
									: ""));							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_excluding_vat#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
							logger.info("Inside Pricing for loo555555555");
							xmlStreamWriter.writeStartElement("Professional_price_including_vat");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							logger.info("Inside Pricing for looooooo6666666");
							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();
							logger.info("Inside Pricing for looooooo666667777777");
							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();
							logger.info("Inside Pricing for looooooo7777777");
							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/professional_price_including_vat#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							logger.info("Inside Pricing for loop5555555");
							xmlStreamWriter.writeStartElement("Retail_price_excluding_vat");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters("");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/nl_NL")
									: ""));
							logger.info("Inside Pricing for loop66666666");
							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_excluding_vat#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
							logger.info("Inside Pricing for loop777777");
							xmlStreamWriter.writeStartElement("Retail_price_including_vat");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/retail_price_including_vat#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();
							logger.info("Inside Pricing for 8888888888");
							xmlStreamWriter.writeStartElement("Salon_success_price_excluding_vat");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_excluding_vat#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Salon_success_price_including_vat");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Pricing/salon_success_price_including_vat#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeEndElement();

						}
						logger.info("11_11_101010101010");

						xmlStreamWriter.writeStartElement("Base_item");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/base_item") != null)
								? modifiedInstanceMap.get("/Variant_ss/base_item")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Replacement_variant_id");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/replacement_variant_id") != null)
								? modifiedInstanceMap.get("/Variant_ss/replacement_variant_id")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Warnings");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/warnings") != null)
								? modifiedInstanceMap.get("/Variant_ss/warnings")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Is_vegan");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/is_vegan") != null)
								? modifiedInstanceMap.get("/Variant_ss/is_vegan")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Assortment");
						AttributeInstance assortmentInstance = item.getAttributeInstance("Variant_ss/assortment");

						for (int x = 0; x < assortmentInstance.getChildren().size(); x++) {

							xmlStreamWriter.writeStartElement("Trade");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/trade#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Retail");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/assortment/retail#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

						}
						logger.info("12_12_101010101010");
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeStartElement("Status");
						
						xmlStreamWriter.writeStartElement("StatusFR");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/status/statusFR") != null)
								? modifiedInstanceMap.get("/Variant_ss/status/statusFR")
								: ""));
						xmlStreamWriter.writeEndElement();
						
						xmlStreamWriter.writeStartElement("StatusDE");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/status/statusDE") != null)
								? modifiedInstanceMap.get("/Variant_ss/status/statusDE")
								: ""));
						xmlStreamWriter.writeEndElement();
						
						xmlStreamWriter.writeStartElement("StatusBE");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/status/statusBE") != null)
								? modifiedInstanceMap.get("/Variant_ss/status/statusBE")
								: ""));
						xmlStreamWriter.writeEndElement();
						
						xmlStreamWriter.writeStartElement("StatusNL");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/status/statusNL") != null)
								? modifiedInstanceMap.get("/Variant_ss/status/statusNL")
								: ""));
						xmlStreamWriter.writeEndElement();
						
						xmlStreamWriter.writeStartElement("StatusES");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/status/statusES") != null)
								? modifiedInstanceMap.get("/Variant_ss/status/statusES")
								: ""));
						xmlStreamWriter.writeEndElement();
						
						xmlStreamWriter.writeStartElement("StatusGBAX2009");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/status/statusGBAX2009") != null)
								? modifiedInstanceMap.get("/Variant_ss/status/statusGBAX2009")
								: ""));
						xmlStreamWriter.writeEndElement();
						
						xmlStreamWriter.writeStartElement("StatusGBAX2012");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/status/statusGBAX2012") != null)
								? modifiedInstanceMap.get("/Variant_ss/status/statusGBAX2012")
								: ""));
						xmlStreamWriter.writeEndElement();
						
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Image_reference");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/image_reference") != null)
								? modifiedInstanceMap.get("/Variant_ss/image_reference")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Supplier_lead_time");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/supplier_lead_time") != null)
								? modifiedInstanceMap.get("/Variant_ss/supplier_lead_time")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Click_and_collect");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/click_and_collect") != null)
								? modifiedInstanceMap.get("/Variant_ss/click_and_collect")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Deliver_to_store");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/deliver_to_store") != null)
								? modifiedInstanceMap.get("/Variant_ss/deliver_to_store")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Directions_assembly_instructions");
						xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/directions_assembly_instructions") != null)
								? modifiedInstanceMap.get("/Variant_ss/directions_assembly_instructions")
								: ""));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Legal");
						AttributeInstance legalInstance = item.getAttributeInstance("Variant_ss/Legal");

						for (int x = 0; x < legalInstance.getChildren().size(); x++) {

							xmlStreamWriter.writeStartElement("Legal_classification");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/legal_classification") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/legal_classification")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Safe_supplier");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/safe_supplier#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Safety_data_sheet");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/safety_data_sheet") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/safety_data_sheet")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("EC_declaration_of_conformity");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/ec_declaration_of_conformity") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/ec_declaration_of_conformity")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("UK_declaration_of_conformity");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/uk_declaration_of_conformity") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/uk_declaration_of_conformity")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Product_compliance");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/es_ES") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/nl_NL") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/fr_FR") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/en_GB") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/de_DE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/nl_BE") != null)
									? modifiedInstanceMap.get("/Variant_ss/Legal/product_compliance#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							AttributeInstance ingredientsInstance = item.getAttributeInstance("Variant_ss/Legal/ingredients");
							xmlStreamWriter.writeStartElement("Ingredients");
							for (int z = 0; z < ingredientsInstance.getChildren().size(); z++) {								
								xmlStreamWriter.writeStartElement("Ingredient");
								xmlStreamWriter.writeStartElement("Value");
								xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/Legal/ingredients#"+z+"/value") != null)
										? newlyAddedInstanceMap.get("/Variant_ss/Legal/ingredients#"+z+"/value")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("Date");
								xmlStreamWriter.writeCharacters(((newlyAddedInstanceMap.get("/Variant_ss/Legal/ingredients#"+z+"/date") != null)
										? newlyAddedInstanceMap.get("/Variant_ss/Legal/ingredients#"+z+"/date")
										: ""));
								xmlStreamWriter.writeEndElement();
								xmlStreamWriter.writeEndElement();
							}
							
							Set<String> legalSet = deletedInstanceMap.keySet()
				                     .stream()
				                     .filter(s -> s.contains("Variant_ss/Legal/ingredients"))
				                     .collect(Collectors.toSet());
							
							if(legalSet.size() > 0) {
								xmlStreamWriter.writeStartElement("Ingredient");
								xmlStreamWriter.writeStartElement("Value");
								xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/ingredients#0/value") != null)
										? deletedInstanceMap.get("/Variant_ss/Legal/ingredients#0/value")
										: ""));
								xmlStreamWriter.writeEndElement();

								xmlStreamWriter.writeStartElement("Date");
								xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/ingredients#0/date") != null)
										? deletedInstanceMap.get("/Variant_ss/Legal/ingredients#0/date")
										: ""));
								xmlStreamWriter.writeEndElement();
								xmlStreamWriter.writeEndElement();
							}
							
							xmlStreamWriter.writeEndElement();
							
							xmlStreamWriter.writeStartElement("Expiry_date_pao");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/expiry_date_pao") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/expiry_date_pao")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Restricted_to_professional_use_uk");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/restricted_to_professional_use_uk") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/restricted_to_professional_use_uk")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Restricted_to_professional_use_eu");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/restricted_to_professional_use_eu") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/restricted_to_professional_use_eu")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Instructions_languages");
							xmlStreamWriter.writeStartElement("es_ES");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/es_ES") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/es_ES")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_NL");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/nl_NL") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/nl_NL")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("fr_FR");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/fr_FR") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/fr_FR")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("en_GB");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/en_GB") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/en_GB")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("de_DE");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/de_DE") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/de_DE")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("nl_BE");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/nl_BE") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/instructions_languages#0/nl_BE")
									: ""));
							xmlStreamWriter.writeEndElement();
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Type_of_plug");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/type_of_plug") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/type_of_plug")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Type_of_battery");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/type_of_battery") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/type_of_battery")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Warnings");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/warnings") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/warnings")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Hazardous_hierarchy_name");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/hazardous_hierarchy_name") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/hazardous_hierarchy_name")
									: ""));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Hazardous_hierarchy_code");
							xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/Legal/hazardous_hierarchy_code") != null)
									? deletedInstanceMap.get("/Variant_ss/Legal/hazardous_hierarchy_code")
									: ""));
							xmlStreamWriter.writeEndElement();

							

						}
						
						logger.info("13_12_101010101010");
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeStartElement("Case_size");
						xmlStreamWriter.writeCharacters(((deletedInstanceMap.get("/Variant_ss/case_size") != null)
								? deletedInstanceMap.get("/Variant_ss/case_size")
								: ""));
						xmlStreamWriter.writeEndElement();
						xmlStreamWriter.writeEndElement();
				  }
				}
				logger.info("Splitted Size : " + modifiedInstanceMap.size());

				

				logger.info("9999999999990777777");
				xmlStreamWriter.writeEndElement();
				logger.info("999999999999077777788");
				xmlStreamWriter.writeEndDocument();
				logger.info("999999999999077777744");
				// coreXmlAttr.close();

				// itemssAttr.close();

				// variantssAttr.close();
				// logger.info("999999999999");
				xmlStreamWriter.flush();
				logger.info("999999999999");
				xmlStreamWriter.close();
				logger.info("99999999999900000000");
				String xmlString = stringWriter.getBuffer().toString();
				xmlString = xmlString.replace(">>", ">");
				logger.info("99999999999988888");
				xmlString = xmlString.replace("<Modified_Product_Attributes", "<Modified_Product_Attributes>");
				logger.info("XML is : " + xmlString);
				logger.info("Executed .........");
				Document doc = ctx.getDocstoreManager().createAndPersistDocument(
						"/OutputXml/SallyMaintenance/Modified_Output_Xml_" + item.getPrimaryKey() + ".xml");
				doc.setContent(xmlString);
				stringWriter.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			} catch (Exception e) {
				logger.info(
						"Error while executing the Modification XML in Sally Maintenance : " + e.getLocalizedMessage());
				logger.info("Error while executing the Modification XML in Sally Maintenance : " + e.getMessage());
				e.printStackTrace();

			}

		}
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	private XMLStreamWriter coreAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, CollaborationItem item)
			throws XMLStreamException {

		logger.info("Inside coreAttributesXmlFunction .....");

		xmlStreamWriter.writeStartElement("Enterprise_item_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/enterprise_item_id") == null) ? ""
				: item.getAttributeValue("Product_c/enterprise_item_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_name");
		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Search_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/search_name") == null) ? ""
				: item.getAttributeValue("Product_c/search_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Entity_Type");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/entity_type") == null) ? ""
				: item.getAttributeValue("Product_c/entity_type").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_Title_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Long_Description_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_name") == null) ? ""
				: item.getAttributeValue("Product_c/primaryvendor_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id") == null) ? ""
				: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Vendorproduct_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/vendor_product_name") == null) ? ""
				: item.getAttributeValue("Product_c/vendor_product_name").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		Collection<Category> categories = item.getCategories();
		xmlStreamWriter.writeStartElement("Category_Info");
		for (Category category : categories) {
			String hierName = category.getHierarchy().getName().replaceAll(" ", "_");
			if (!hierName.equalsIgnoreCase("Sally_Item_Type_Hierarchy")) {
				xmlStreamWriter.writeStartElement(hierName);
				xmlStreamWriter.writeCharacters(category.getName());
				xmlStreamWriter.writeEndElement();
			}

		}

		return xmlStreamWriter;
	}

	private XMLStreamWriter coreAttributesXmlFunctionForVariant(XMLStreamWriter xmlStreamWriter, Item item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("Enterprise_item_id");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_name");
		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Search_Name");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Entity_Type");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_Title_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Long_Description_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Name");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		int size = item.getAttributeInstance("Product_c/primaryvendor_id").getChildren().size();

		if (size == 0) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeEndElement();
		}

		if (size == 1) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeEndElement();
		}

		if (size == 2) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeStartElement("Vendorproduct_Name");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		Collection<Category> categories = item.getCategories();
		xmlStreamWriter.writeStartElement("Category_Info");
		for (Category category : categories) {
			String hierName = category.getHierarchy().getName().replaceAll(" ", "_");
			if (!hierName.equalsIgnoreCase("Sally_Item_Type_Hierarchy")) {
				xmlStreamWriter.writeStartElement(hierName);
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();
			}
		}

		return xmlStreamWriter;
	}

	private XMLStreamWriter variantAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, Item item)
			throws XMLStreamException {

		logger.info("Entered variantAttributes func catalog Item");
		xmlStreamWriter.writeStartElement("ERP_Variant_Id");
		xmlStreamWriter.writeStartElement("RBO_Variant_Id");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Source");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Manufacturer_Id");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Oe_Item_Code");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Buyer");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Kvi");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Route_to_customer");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Trade_card_restricted");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbunmlgr");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbqtymlgr");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_family");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_shade");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_class");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_id");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		logger.info("1111111111111111111");
		xmlStreamWriter.writeStartElement("Airflow");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Max_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Min_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Contents");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Watt");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Outer_diameter");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Inner_diameter");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		logger.info("22222222222222222222");
		xmlStreamWriter.writeStartElement("Web_assortment");
		AttributeInstance assortmentWebInstance = item.getAttributeInstance("Variant_ss/web_assortment");

		if (assortmentWebInstance != null) {
			for (int x = 0; x < assortmentWebInstance.getChildren().size(); x++) {

				xmlStreamWriter.writeStartElement("Trade");
				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters((""));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters((""));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters((""));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("Retail");
				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();
				xmlStreamWriter.writeEndElement();

			}
		}
		xmlStreamWriter.writeEndElement();
		logger.info("333333333333333333333");

		xmlStreamWriter.writeStartElement("Customer_facing_lead_time");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		logger.info("33333331111111111");

		xmlStreamWriter.writeStartElement("Hair_solution");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_limited_edition");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		logger.info("333333322222222222222");
		xmlStreamWriter.writeStartElement("Web_online_date_trade");
		AttributeInstance webOnlineTradeInstance = item.getAttributeInstance("Variant_ss/web_online_date_trade");

		if (webOnlineTradeInstance != null) {
			for (int x = 0; x < webOnlineTradeInstance.getChildren().size(); x++) {

				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters((""));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

			}
		}

		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Web_online_date_retail");
		AttributeInstance webOnlineRetailInstance = item.getAttributeInstance("Variant_ss/web_online_date_retail");

		if (webOnlineRetailInstance != null) {
			for (int x = 0; x < webOnlineRetailInstance.getChildren().size(); x++) {

				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters("");
				xmlStreamWriter.writeEndElement();

			}
		}

		xmlStreamWriter.writeEndElement();
		logger.info("555555555555555555555555");
		xmlStreamWriter.writeStartElement("Web_quantity_restriction");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_searchable");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_trade_restrict");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Variant_differentiators");
		xmlStreamWriter.writeStartElement("Colour");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Size");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		logger.info("VVVVVVVVVVVVVVVVV");
		xmlStreamWriter.writeStartElement("Style");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Fragrance");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Type");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Configuration");
		xmlStreamWriter.writeCharacters((""));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Strength");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Page_number");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("New_icon");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Info_block");
		xmlStreamWriter.writeStartElement("Text");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();
		logger.info("66666666666666666666");
		AttributeInstance warehouseInstance = item.getAttributeInstance("Variant_ss/Warehouse");

		for (int x = 0; x < warehouseInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Warehouse");
			xmlStreamWriter.writeStartElement("Ship_in_pallets");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pick_instructions");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Packable");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Conveyable");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Stackable");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_type");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Value_added_service_id");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Language_independent");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
		}

		logger.info("777777777777777777777777");
		AttributeInstance packagingInstance = item.getAttributeInstance("Variant_ss/Packaging Attributes");

		for (int x = 0; x < packagingInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Packaging_attribute");
			xmlStreamWriter.writeStartElement("Inner_pack_qty");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_barcode_type");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_barcode");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_qty");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_barcode_type");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_barcode");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}

		logger.info("8888888888888888888");
		xmlStreamWriter.writeStartElement("Product_Dimensions");
		AttributeInstance prodDimenInst = item.getAttributeInstance("Variant_ss/Product Dimensions");

		for (int x = 0; x < prodDimenInst.getChildren().size(); x++) {

			logger.info("Inside Product DImensions for loop");
			xmlStreamWriter.writeStartElement("Outers_per_layer");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			logger.info("Product DImensions outers per layer"
					+ item.getAttributeInstance("Variant_ss/Product Dimensions#" + x + "/outers_per_layer").getValue()
							.toString());
			xmlStreamWriter.writeStartElement("Layers_per_pallete");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Net_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Gross_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Gross_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Gross_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}
		logger.info("999999999999999999");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Barcodes");
		AttributeInstance barcodeInstance = item.getAttributeInstance("Variant_ss/Barcode");

		for (int x = 0; x < barcodeInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Barcode");
			xmlStreamWriter.writeStartElement("Barcode_type_each_level");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_each_level");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_date_created");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
		}
		logger.info("10101010101010");
		xmlStreamWriter.writeEndElement();

		AttributeInstance pricingInstance = item.getAttributeInstance("Variant_ss/Pricing");

		for (int x = 0; x < pricingInstance.getChildren().size(); x++) {
			logger.info("Inside Pricing for loop");
			xmlStreamWriter.writeStartElement("Pricing");
			xmlStreamWriter.writeStartElement("Base_cost");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop11111");
			xmlStreamWriter.writeStartElement("Vendor_recommended_retail_price");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop22222");
			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop333333");

			xmlStreamWriter.writeStartElement("Vendor_recommended_trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop444444");
			xmlStreamWriter.writeStartElement("Professional_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loo555555555");
			xmlStreamWriter.writeStartElement("Professional_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for looooooo6666666");
			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for looooooo666667777777");
			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for looooooo7777777");
			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop5555555");
			xmlStreamWriter.writeStartElement("Retail_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop66666666");
			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop777777");
			xmlStreamWriter.writeStartElement("Retail_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for 8888888888");
			xmlStreamWriter.writeStartElement("Salon_success_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();

		}
		logger.info("11_11_101010101010");

		xmlStreamWriter.writeStartElement("Base_item");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Replacement_variant_id");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warnings");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Is_vegan");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Assortment");
		AttributeInstance assortmentInstance = item.getAttributeInstance("Variant_ss/assortment");

		for (int x = 0; x < assortmentInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}
		logger.info("12_12_101010101010");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Status");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image_reference");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Supplier_lead_time");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Click_and_collect");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Deliver_to_store");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Directions_assembly_instructions");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Legal");
		AttributeInstance legalInstance = item.getAttributeInstance("Variant_ss/Legal");

		for (int x = 0; x < legalInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Legal_classification");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Safe_supplier");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Safety_data_sheet");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("EC_declaration_of_conformity");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_declaration_of_conformity");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Product_compliance");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Ingredeints");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Date");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Expiry_date_pao");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_uk");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_eu");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Instructions_languages");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Type_of_plug");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Type_of_battery");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Warnings");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Hazardous_hierarchy_name");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Hazardous_hierarchy_code");
			xmlStreamWriter.writeCharacters("");
			xmlStreamWriter.writeEndElement();

			

		}
		
		logger.info("13_12_101010101010");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Case_size");
		xmlStreamWriter.writeCharacters("");
		xmlStreamWriter.writeEndElement();
		return xmlStreamWriter;
	}

}
