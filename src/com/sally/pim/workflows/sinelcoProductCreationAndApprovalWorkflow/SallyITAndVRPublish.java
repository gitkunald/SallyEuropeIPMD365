package com.sally.pim.workflows.sinelcoProductCreationAndApprovalWorkflow;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoProductCreationAndApprovalWorkflow.SallyITAndVRPublish.class"
//uploaded_java_classes/com/sally/pim/workflows/sinelcoProductCreationAndApprovalWorkflow
import java.io.StringWriter;

import java.util.Collection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.*;

import com.ibm.pim.attribute.AttributeInstance;
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
import com.sally.pim.workflows.sallyProductPublishWorkflow.SallyPublishWorkflowSuccessStep;

public class SallyITAndVRPublish implements WorkflowStepFunction {

	private static Logger logger = LogManager.getLogger(SallyITAndVRPublish.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

		logger.info("inside Sally IT AND VR Test ....");
		Context ctx = PIMContextFactory.getCurrentContext();
		Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally_Products_Catalog");
		PIMCollection<CollaborationItem> items = arg0.getItems();
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		for (CollaborationItem item : items) {
			try {
				XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
				xmlStreamWriter.writeStartDocument();
				Object entityType = item.getAttributeValue("Sinelco_Product_c/entity_type");
				String entityString = "";
				if (entityType != null) {
					entityString = entityType.toString();
				}
				logger.info("Entity String : " + entityString);
				xmlStreamWriter.writeStartElement("Product_Attributes_XML");

				xmlStreamWriter.writeStartElement("Product_c");

				xmlStreamWriter = coreAttributesXmlFunction(xmlStreamWriter, item);

				xmlStreamWriter.writeEndElement();

				if (entityString.equalsIgnoreCase("Item")) {

					xmlStreamWriter.writeStartElement("Item_ss");
					xmlStreamWriter.writeStartElement("Erp_Item_Id");
					xmlStreamWriter.writeStartElement("Item_Id");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/erp_item_id/item_id") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/erp_item_id/item_id").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Source");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/erp_item_id/source") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/erp_item_id/source").toString()));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Replacement_Item_Id");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/replacement_item_id") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/replacement_item_id").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Kit_Listing");
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/kit_listing") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/kit_listing").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Item_Type");
					xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/item_type") == null) ? ""
							: item.getAttributeValue("Sinelco_Item_ss/item_type").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Product_Range_Of_Colours");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/product_range_of_colours") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/product_range_of_colours").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Product_Range_Of_Sizes");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/product_range_of_sizes") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/product_range_of_sizes").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Legal_Documentation");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/legal_documentation") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/legal_documentation").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Document_Reference");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/document_reference") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/document_reference").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Legal_Documentation");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/legal_documentation") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/legal_documentation").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Features_and_Benefits");
					xmlStreamWriter.writeStartElement("es_ES");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/es_ES") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/es_ES")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("nl_NL");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/nl_NL") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/nl_NL")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("fr_FR");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/fr_FR") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/fr_FR")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("en_GB");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/en_GB") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/en_GB")
											.toString()));
					xmlStreamWriter.writeEndElement();

//                    xmlStreamWriter.writeStartElement("it_IT"); 
//                    xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/it_IT") == null) ? "" : item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/it_IT").toString()));
//                    xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("de_DE");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/de_DE") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/features_and_benefits/de_DE")
											.toString()));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Minimum_order_quantity");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/minimum_order_quantity") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/minimum_order_quantity").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("No_Discount_allowed");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/no_discount_allowed") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/no_discount_allowed").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Serial_tracked_item");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/serial_tracked_item") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/serial_tracked_item").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Commodity_Code");
					xmlStreamWriter.writeStartElement("nl_NL");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/commodity_code/nl_NL") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/commodity_code/nl_NL").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("fr_FR");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/commodity_code/fr_FR") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/commodity_code/fr_FR").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("de_DE");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/commodity_code/de_DE") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/commodity_code/de_DE").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("en_GB");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/commodity_code/en_GB") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/commodity_code/en_GB").toString()));
					xmlStreamWriter.writeEndElement();

//                    xmlStreamWriter.writeStartElement("it_IT");
//                    xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/commodity_code/it_IT") == null) ? "" : item.getAttributeValue("Sinelco_Item_ss/commodity_code/it_IT").toString()));
//                    xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("es_ES");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/commodity_code/es_ES") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/commodity_code/es_ES").toString()));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Country_of_origin");
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/country_of_origin") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/country_of_origin").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Dim_group_id");
					xmlStreamWriter.writeStartElement("nl_NL");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/dim_group_id/nl_NL") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/dim_group_id/nl_NL").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("fr_FR");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/dim_group_id/fr_FR") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/dim_group_id/fr_FR").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("de_DE");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/dim_group_id/de_DE") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/dim_group_id/de_DE").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("en_GB");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/dim_group_id/en_GB") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/dim_group_id/en_GB").toString()));
					xmlStreamWriter.writeEndElement();

//                    xmlStreamWriter.writeStartElement("it_IT");
//                    xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/dim_group_id/it_IT") == null) ? "" : item.getAttributeValue("Sinelco_Item_ss/dim_group_id/it_IT").toString()));
//                    xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("es_ES");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/dim_group_id/es_ES") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/dim_group_id/es_ES").toString()));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Item_group_type");
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/item_group_type") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/item_group_type").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Retail_group_id");
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/retail_group_id") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/retail_group_id").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Retail_department");
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/retail_department") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/retail_department").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Retail_group");
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/retail_group") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/retail_group").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Commission_group_id");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Sinelco_Item_ss/commission_group_id") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/commission_group_id").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Division_group");
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue("Sinelco_Item_ss/division_group") == null) ? ""
									: item.getAttributeValue("Sinelco_Item_ss/division_group").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Variants");
					AttributeInstance attrInstance = item.getAttributeInstance("Sinelco_Item_ss/variants");
					if (attrInstance != null) {
						for (int x = 0; x < attrInstance.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("Variant_ID");
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Sinelco_Item_ss/variants#" + x + "/variant_id") == null)
											? ""
											: item.getAttributeValue("Sinelco_Item_ss/variants#" + x + "/variant_id")
													.toString()));
							xmlStreamWriter.writeEndElement();
						}
					}
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();

					logger.info("Writing the variant data in XML**** ....");

					xmlStreamWriter.writeStartElement("Variant_Info");
					AttributeInstance variantInstance = item.getAttributeInstance("Sinelco_Item_ss/variants");
					if (variantInstance != null) {
						logger.info("variant Childern" + variantInstance.getChildren().size());
						for (int x = 0; x < variantInstance.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("Variant_" + x);
							xmlStreamWriter.writeStartElement("Variant_ID");
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Sinelco_Item_ss/variants#" + x + "/variant_id") == null)
											? ""
											: item.getAttributeValue("Sinelco_Item_ss/variants#" + x + "/variant_id")
													.toString()));
							String variantId = item.getAttributeValue("Sinelco_Item_ss/variants#" + x + "/variant_id")
									.toString();

							String colQuery = "select item from collaboration_area ('Sinelco Products Creation And Approval Collaboration Area') where item['Sinelco_Product_c/enterprise_item_id'] ='"
									+ variantId + "'";
							logger.info("colQuery: {}" + colQuery);
							SearchQuery selectSearchQuery = ctx.createSearchQuery(colQuery);
							SearchResultSet selectResultSet = selectSearchQuery.execute();
							logger.info("Result Set Size: {}" + selectResultSet.size());

							while (selectResultSet.next()) {
								Item varItem = selectResultSet.getItem(1);

								xmlStreamWriter.writeEndElement();
								xmlStreamWriter.writeStartElement("Product_c");
								xmlStreamWriter = coreAttributesXmlFunction(xmlStreamWriter, varItem);
								xmlStreamWriter.writeEndElement();
								xmlStreamWriter.writeStartElement("Variant_ss");
								xmlStreamWriter = variantAttributesXmlFunction(xmlStreamWriter, varItem);
								xmlStreamWriter.writeEndElement();
								xmlStreamWriter.writeEndElement();
							}
						}
					}
				} else {
					xmlStreamWriter.writeStartElement("Variant_ss");
					xmlStreamWriter = variantAttributesXmlFunction(xmlStreamWriter, item);
					xmlStreamWriter.writeEndElement();
				}

				// Ending the start element
				xmlStreamWriter.writeEndElement();
				xmlStreamWriter.writeEndDocument();
				xmlStreamWriter.flush();
				xmlStreamWriter.close();

				String xmlString = stringWriter.getBuffer().toString();
				logger.info("XML is : " + xmlString);
				logger.info("Executed .........");
				logger.info("To Xml : " + item.toXml().toString());

				Document doc = null;
				if (entityString.equalsIgnoreCase("Item")) {
					logger.info("Save the XML for Items");
					doc = ctx.getDocstoreManager().createAndPersistDocument(
							"/BaseOutputXml/SallyITVR/Final_Output_Xml_" + item.getPrimaryKey() + ".xml");
				}

				else {
					logger.info("Save the XML for Variants");
					doc = ctx.getDocstoreManager().createAndPersistDocument(
							"/VariantOutputXml/SallyITVR/Final_Output_Xml_" + item.getPrimaryKey() + ".xml");
				}

				if (doc != null) {
					doc.setContent(xmlString);
				}
				stringWriter.close();
			} catch (Exception e) {
				logger.info("Error in XML : " + e);
				e.printStackTrace();
			}
		}
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {

	}

	private XMLStreamWriter variantAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, CollaborationItem item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("ERP_Variant_Id");
		xmlStreamWriter.writeStartElement("RBO_Variant_Id");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/erp_variant_id/rbovariantid") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/erp_variant_id/rbovariantid").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Source");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/erp_variant_id/source") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/erp_variant_id/source").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Manufacturer_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/manufacturer_id") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/manufacturer_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Oe_Item_Code");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/oe_item_code") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/oe_item_code").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Buyer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/buyer") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/buyer").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Kvi");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/kvi") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/kvi").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Route_to_customer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/route_to_customer") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/route_to_customer").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Trade_card_restricted");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/trade_card_restricted") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/trade_card_restricted").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbunmlgr");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/qpbunmlgr") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/qpbunmlgr").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbqtymlgr");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/qpbqtymlgr") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/qpbqtymlgr").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_family");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_family") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_family").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_shade");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_shade") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_shade").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_class");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_class") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_class").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_id") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Airflow");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/airflow/value") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/airflow/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/airflow/uom") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/airflow/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Max_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/max_temp/value") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/max_temp/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/max_temp/uom") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/max_temp/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Min_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/min_temp/value") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/min_temp/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/min_temp/uom") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/min_temp/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Contents");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/contents/value") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/contents/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/contents/uom") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/contents/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Watt");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/watt/value") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/watt/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/watt/uom") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/watt/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Outer_diameter");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/outer_diameter") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/outer_diameter").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Inner_diameter");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/inner_diameter") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/inner_diameter").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_assortment");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_assortment") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/web_assortment").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Customer_facing_lead_time");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/web_customer_facing_lead_time") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_customer_facing_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Hair_solution");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/hair_solution") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/hair_solution").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_limited_edition");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_limited_edition") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/web_limited_edition").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_online_date_trade");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_online_date_trade") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_online_date_trade").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_online_date_retail");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_online_date_retail") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_online_date_retail").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_quantity_restriction");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_quantity_restriction") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_quantity_restriction").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Variant_differentiators");
		xmlStreamWriter.writeStartElement("Colour");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/colour") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/colour").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Size");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/size") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/size").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Style");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/style") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/style").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Fragrance");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/fragrance") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/fragrance").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Type");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/type") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/type").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Configuration");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/configuration") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/configuration").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Strength");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/strength") == null) ? ""
						: item.getAttributeInstance("Sinelco_Variant_ss/variant_differentiators/strength").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Page_number");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/page_number") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/page_number").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("New_icon");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/new_icon") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/new_icon").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Info_block");
		xmlStreamWriter.writeStartElement("Text");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/info_block/text") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/info_block/text").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Sinelco_Variant_ss/info_block/image") == null) ? ""
				: item.getAttributeInstance("Sinelco_Variant_ss/info_block/image").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warehouse");
		AttributeInstance warehouseInstance = item.getAttributeInstance("Sinelco_Variant_ss/Warehouse");

		for (int x = 0; x < warehouseInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Warehouse_" + x);
			xmlStreamWriter.writeStartElement("Ship_in_pallets");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/ship_in_pallets") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/ship_in_pallets").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pick_instructions");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pick_instructions") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pick_instructions").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Packable");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/packable") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/packable").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Conveyable");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/conveyable") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/conveyable").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Stackable");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/stackable") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/stackable").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_type");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_type") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_type").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Value_added_service_id");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/value_added_service_id") == null)
							? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/value_added_service_id").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Language_independent");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/languagedependent") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/languagedependent").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outers_per_layer");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/outers_per_layer") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/outers_per_layer").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Layers_per_pallete");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/layers_per_pallete") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/layers_per_pallete").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/value") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/value").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/uom") == null) ? ""
							: item.getAttributeInstance("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/uom").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();
		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Packaging_attributes");
		AttributeInstance packagingInstance = item.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes");

		for (int x = 0; x < packagingInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Packaging_attributes_" + x);
			xmlStreamWriter.writeStartElement("Inner_pack_qty");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_barcode");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_barcode_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type") == null)
							? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x
									+ "/inner_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_qty");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_barcode");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_barcode_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type") == null)
							? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x
									+ "/outer_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();
		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Barcode");
		AttributeInstance barcodeInstance = item.getAttributeInstance("Sinelco_Variant_ss/Barcode");

		for (int x = 0; x < barcodeInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Barcode_" + x);
			xmlStreamWriter.writeStartElement("Barcode_type_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_type_each_level") == null)
							? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_type_each_level")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_each_level") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_each_level")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_date_created");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_date_created") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_date_created")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Pricing");
		AttributeInstance pricingInstance = item.getAttributeInstance("Sinelco_Variant_ss/Pricing");

		for (int x = 0; x < pricingInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Pricing_" + x);
			xmlStreamWriter.writeStartElement("Base_cost");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/base_cost") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/base_cost").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Vendor_recommended_retail_price");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_retail_price/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_retail_price/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
//        	xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Vendor_recommended_trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE")
											.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_trade/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_trade/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
//        	xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Professional_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_excluding_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_excluding_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
//        	xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Professional_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_including_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_including_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
//        	xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_excluding_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_excluding_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
//        	xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_including_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_including_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
//        	xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_excluding_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_excluding_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
//        	xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_including_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_including_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();

		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Base_item");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/base_item") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/base_item").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Replacement_variant_id");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/replacement_variant_id") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/replacement_variant_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Ingredients");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/ingredients/value") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/ingredients/value").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Date");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/ingredients/date") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/ingredients/date").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warnings");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/warnings") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/warnings").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Hazardous_hierarchy_name");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_name") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Hazardous_hierarchy_code");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_code") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_code").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Is_vegan");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/is_vegan") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/is_vegan").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Assortment");
		AttributeInstance assortmentInstance = item.getAttributeInstance("Sinelco_Variant_ss/assortment");

		for (int x = 0; x < assortmentInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/es_ES") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/nl_NL") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/fr_FR") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/en_GB") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/de_DE") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/trade/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/trade/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/es_ES") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/nl_NL") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/fr_FR") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/en_GB") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/de_DE") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/retail/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/retail/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}

		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Status");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/status") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/status").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image_reference");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/image_reference") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/image_reference").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Supplier_lead_time");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/supplier_lead_time") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/supplier_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Click_and_collect");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/click_and_collect") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/click_and_collect").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Deliver_to_store");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/deliver_to_store") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/deliver_to_store").toString()));
		xmlStreamWriter.writeEndElement();

		return xmlStreamWriter;
	}

	private XMLStreamWriter coreAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, Item item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("Product_name");
		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/nl_NL") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/fr_FR") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/de_DE") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/es_ES") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/en_GB") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

//            xmlStreamWriter.writeStartElement("nl_BE");
//            xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Product_c/product_name/nl_BE").toString()));
//            xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Search_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/search_name") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/search_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Entity_Type");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/entity_type") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/entity_type").toString()));
		xmlStreamWriter.writeEndElement();

//		xmlStreamWriter.writeStartElement("Long_Name");
//		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_name") == null) ? ""
//				: item.getAttributeValue("Sinelco_Product_c/long_name").toString()));
//		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_Title_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/es_ES") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_NL") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/fr_FR") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/en_GB") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/de_DE") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

//            xmlStreamWriter.writeStartElement("nl_BE");
//            xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_BE").toString()));
//            xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Long_Description_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/es_ES") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_NL") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/fr_FR") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/en_GB") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/de_DE") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

//            xmlStreamWriter.writeStartElement("nl_BE");
//            xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_BE").toString()));
//            xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_name") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/primaryvendor_name").toString()));
		xmlStreamWriter.writeEndElement();

		int size = item.getAttributeInstance("Sinelco_Product_c/primaryvendor_id").getChildren().size();

		if (size == 0) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_id#0") == null) ? ""
							: item.getAttributeValue("Sinelco_Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeEndElement();
		}

		if (size == 1) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_id#0") == null) ? ""
							: item.getAttributeValue("Sinelco_Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_id#1") == null) ? ""
							: item.getAttributeValue("Sinelco_Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeEndElement();
		}

		if (size == 2) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_id#0") == null) ? ""
							: item.getAttributeValue("Sinelco_Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_id#1") == null) ? ""
							: item.getAttributeValue("Sinelco_Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_id#2") == null) ? ""
							: item.getAttributeValue("Sinelco_Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeStartElement("Vendorproduct_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/vendor_product_name") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/vendor_product_name").toString()));
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

	private XMLStreamWriter variantAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, Item item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("ERP_Variant_Id");
		xmlStreamWriter.writeStartElement("RBO_Variant_Id");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/erp_variant_id/rbovariantid") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/erp_variant_id/rbovariantid").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Source");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/erp_variant_id/source") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/erp_variant_id/source").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Manufacturer_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/manufacturer_id") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/manufacturer_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Oe_Item_Code");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/oe_item_code") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/oe_item_code").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Buyer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/buyer") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/buyer").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Kvi");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/kvi") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/kvi").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Route_to_customer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/route_to_customer") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/route_to_customer").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Trade_card_restricted");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/trade_card_restricted") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/trade_card_restricted").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbunmlgr");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/qpbunmlgr") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/qpbunmlgr").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbqtymlgr");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/qpbqtymlgr") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/qpbqtymlgr").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_family");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_family") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_family").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_shade");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_shade") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_shade").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_class");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_class") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_class").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/colour_id") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/colour_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Airflow");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/airflow/value") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/airflow/value").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/airflow/uom") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/airflow/uom").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Max_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/max_temp/value") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/max_temp/value").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/max_temp/uom") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/max_temp/uom").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Min_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/min_temp/value") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/min_temp/value").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/min_temp/uom") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/min_temp/uom").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Contents");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/contents/value") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/contents/value").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/contents/uom") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/contents/uom").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Watt");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/watt/value") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/watt/value").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/watt/uom") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/watt/uom").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Outer_diameter");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/outer_diameter") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/outer_diameter").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Inner_diameter");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/inner_diameter") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/inner_diameter").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_assortment");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_assortment") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/web_assortment").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Customer_facing_lead_time");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/web_customer_facing_lead_time") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_customer_facing_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Hair_solution");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/hair_solution") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/hair_solution").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_limited_edition");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_limited_edition") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/web_limited_edition").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_online_date_trade");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_online_date_trade") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_online_date_trade").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_online_date_retail");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_online_date_retail") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_online_date_retail").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_quantity_restriction");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/web_quantity_restriction") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/web_quantity_restriction").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Variant_differentiators");
		xmlStreamWriter.writeStartElement("Colour");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/colour") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/colour").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Size");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/size") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/size").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Style");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/style") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/style").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Fragrance");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/fragrance") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/fragrance").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Type");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/type") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/type").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Configuration");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/configuration") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/configuration")
								.toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Strength");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/strength") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/variant_differentiators/strength").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Page_number");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/page_number") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/page_number").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("New_icon");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/new_icon") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/new_icon").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Info_block");
		xmlStreamWriter.writeStartElement("Text");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/info_block/text") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/info_block/text").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/info_block/image") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/info_block/image").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warehouse");
		AttributeInstance warehouseInstance = item.getAttributeInstance("Sinelco_Variant_ss/Warehouse");

		for (int x = 0; x < warehouseInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Warehouse_" + x);
			xmlStreamWriter.writeStartElement("Ship_in_pallets");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/ship_in_pallets") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/ship_in_pallets")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pick_instructions");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pick_instructions") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pick_instructions")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Packable");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/packable") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/packable").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Conveyable");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/conveyable") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/conveyable").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Stackable");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/stackable") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/stackable").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_type");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_type") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Value_added_service_id");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/value_added_service_id") == null)
							? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/value_added_service_id")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Language_independent");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/languagedependent") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/languagedependent")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outers_per_layer");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/outers_per_layer") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/outers_per_layer")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Layers_per_pallete");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/layers_per_pallete") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/layers_per_pallete")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/value") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/uom") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Warehouse#" + x + "/pallet_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();
		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Packaging_attributes");
		AttributeInstance packagingInstance = item.getAttributeInstance("Sinelco_Variant_ss/Packaging Attributes");

		for (int x = 0; x < packagingInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Packaging_attributes_" + x);
			xmlStreamWriter.writeStartElement("Inner_pack_qty");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_barcode");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_barcode_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type") == null)
							? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x
									+ "/inner_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_qty");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_barcode");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_barcode_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type") == null)
							? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Packaging Attributes#" + x
									+ "/outer_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();
		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Barcode");
		AttributeInstance barcodeInstance = item.getAttributeInstance("Sinelco_Variant_ss/Barcode");

		for (int x = 0; x < barcodeInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Barcode_" + x);
			xmlStreamWriter.writeStartElement("Barcode_type_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_type_each_level") == null)
							? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_type_each_level")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_each_level") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_each_level")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_date_created");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_date_created") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Barcode#" + x + "/barcode_date_created")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Pricing");
		AttributeInstance pricingInstance = item.getAttributeInstance("Sinelco_Variant_ss/Pricing");

		for (int x = 0; x < pricingInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Pricing_" + x);
			xmlStreamWriter.writeStartElement("Base_cost");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/base_cost") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/base_cost").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Vendor_recommended_retail_price");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_retail_price/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_retail_price/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Vendor_recommended_trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Sinelco_Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE")
											.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_trade/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/vendor_recommended_trade/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Professional_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_excluding_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_excluding_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Professional_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_including_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/professional_price_including_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_excluding_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_excluding_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_including_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/retail_price_including_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_excluding_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_excluding_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Sinelco_Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_including_vat/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/Pricing#"+x+"/salon_success_price_including_vat/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();

		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Base_item");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/base_item") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/base_item").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Replacement_variant_id");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/replacement_variant_id") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/replacement_variant_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Ingredeints");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/ingredients/value") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/ingredients/value").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Date");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/ingredients/date") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/ingredients/date").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warnings");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/warnings") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/warnings").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Hazardous_hierarchy_name");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_name") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Hazardous_hierarchy_code");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_code") == null) ? ""
						: item.getAttributeValue("Sinelco_Variant_ss/hazardous_hierarchy_code").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Is_vegan");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/is_vegan") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/is_vegan").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Assortment");
		AttributeInstance assortmentInstance = item.getAttributeInstance("Sinelco_Variant_ss/assortment");

		for (int x = 0; x < assortmentInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/es_ES") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/nl_NL") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/fr_FR") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/en_GB") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/de_DE") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/trade/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/trade/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/trade/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/es_ES") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/nl_NL") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/fr_FR") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/en_GB") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/de_DE") == null) ? ""
							: item.getAttributeValue("Sinelco_Variant_ss/assortment#" + x + "/retail/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

//        	xmlStreamWriter.writeStartElement("nl_BE");
//        	xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/retail/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Variant_ss/assortment#"+x+"/retail/nl_BE").toString()));
//        	xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}

		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Status");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/status") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/status").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image_reference");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/image_reference") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/image_reference").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Supplier_lead_time");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/supplier_lead_time") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/supplier_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Click_and_collect");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/click_and_collect") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/click_and_collect").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Deliver_to_store");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Variant_ss/deliver_to_store") == null) ? ""
				: item.getAttributeValue("Sinelco_Variant_ss/deliver_to_store").toString()));
		xmlStreamWriter.writeEndElement();

		return xmlStreamWriter;
	}

	private XMLStreamWriter coreAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, CollaborationItem item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("Product_name");
		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/nl_NL") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/fr_FR") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/de_DE") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/es_ES") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/en_GB") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/product_name/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

//            xmlStreamWriter.writeStartElement("nl_BE");
//            xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_name/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Product_c/product_name/nl_BE").toString()));
//            xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Search_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/search_name") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/search_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Entity_Type");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/entity_type") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/entity_type").toString()));
		xmlStreamWriter.writeEndElement();

//		xmlStreamWriter.writeStartElement("Long_Name");
//		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_name") == null) ? ""
//				: item.getAttributeValue("Sinelco_Product_c/long_name").toString()));
//		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_Title_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/es_ES") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_NL") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/fr_FR") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/en_GB") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/de_DE") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/product_title_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

//            xmlStreamWriter.writeStartElement("nl_BE");
//            xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Product_c/product_title_web/nl_BE").toString()));
//            xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Long_Description_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/es_ES") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_NL") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/fr_FR") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/en_GB") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/de_DE") == null) ? ""
						: item.getAttributeValue("Sinelco_Product_c/long_description_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

//            xmlStreamWriter.writeStartElement("nl_BE");
//            xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_BE") == null) ? "" : item.getAttributeValue("Sinelco_Product_c/long_description_web/nl_BE").toString()));
//            xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_name") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/primaryvendor_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/primaryvendor_id") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/primaryvendor_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Vendorproduct_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Sinelco_Product_c/vendor_product_name") == null) ? ""
				: item.getAttributeValue("Sinelco_Product_c/vendor_product_name").toString()));
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

}
