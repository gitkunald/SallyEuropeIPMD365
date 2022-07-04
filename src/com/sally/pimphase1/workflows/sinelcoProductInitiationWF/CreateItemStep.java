package com.sally.pimphase1.workflows.sinelcoProductInitiationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductInitiationWF.CreateItemStep.class"

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.*;
import com.sally.pimphase1.common.*;

import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.exceptions.PIMSearchException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;

public class CreateItemStep implements WorkflowStepFunction {
	
	private static Logger logger = LogManager.getLogger(CreateItemStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Inside Out Func CreateItem PublishXML ....");
		Context ctx = PIMContextFactory.getCurrentContext();
		Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally Europe");
		PIMCollection<CollaborationItem> items = arg0.getItems();
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		
		for (CollaborationItem item : items) {
			try {
				publishXML(ctx, sallyCatalog, stringWriter, xmlOutputFactory, item);
			} catch (Exception e) {
				logger.info("Error in XML : " + e);
			}
		}

	}
	
	private void publishXML(Context ctx, Catalog sallyCatalog, StringWriter stringWriter,
			XMLOutputFactory xmlOutputFactory, CollaborationItem item)
			throws XMLStreamException, PIMSearchException, IOException {
		
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.writeStartDocument();
		xmlStreamWriter.writeStartElement("Product_Attributes_XML");

		xmlStreamWriter.writeStartElement("Product_c");
		
		xmlStreamWriter.writeStartElement("PIM_item_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PIM_MDM_ID) == null) ? ""
				: item.getAttributeValue(Constants.PIM_MDM_ID).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Product_name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCT_NAME) == null) ? ""
				: item.getAttributeValue(Constants.PRODUCT_NAME).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Item_type");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ITEM_TYPE) == null) ? ""
				: item.getAttributeValue(Constants.ITEM_TYPE).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Serial_tracked_item");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SERIAL_TRACKED_ITEM) == null) ? ""
				: item.getAttributeValue(Constants.SERIAL_TRACKED_ITEM).toString()));
		xmlStreamWriter.writeEndElement();

		//End tag of Product_c
		xmlStreamWriter.writeEndElement();
		
		// Ending the Product Attributes XML Tag
		xmlStreamWriter.writeEndElement();
				
				xmlStreamWriter.writeEndDocument();
				xmlStreamWriter.flush();
				xmlStreamWriter.close();

				String xmlString = stringWriter.getBuffer().toString();
				logger.info("XML is : " + xmlString);
				logger.info("Executed .........");
				logger.info("To Xml : " + item.toXml().toString());
				Document doc = null;
				
					logger.info("Save the XML for Items");
				doc = ctx.getDocstoreManager().createAndPersistDocument("/PIM_D365_Integration/OutBound/" + item.getPrimaryKey() + ".xml");
				
				if (doc != null) {
					doc.setContent(xmlString);
				}
				stringWriter.close();
		
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
