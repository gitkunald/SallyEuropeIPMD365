package com.sally.pimphase1.workflows.sinelcoProductInitiationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductInitiationWF.CreateItemStep.class"

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

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
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;

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
		
		
		LookupTable itmTypeLkpTable = ctx.getLookupTableManager().getLookupTable("AzureConstantsLookup");
		PIMCollection<LookupTableEntry> lkpEntries = itmTypeLkpTable.getLookupTableEntries();
		String storageConnectionString = "";
		String localFilePath = "";
		String fileShare = "";
		String outboundWorkingDirectory = "";
		
		for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("storageConnectionString")) {
				storageConnectionString = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("OutboundLocalFilePath")) {
				localFilePath = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("fileShare")) {
				fileShare = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("OutboundWorkingDirectory")) {
				outboundWorkingDirectory = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
		}
		logger.info("Connection Str : "+storageConnectionString+" localfilepath : "+localFilePath);
		
		
		for (CollaborationItem item : items) {
			StringWriter stringWriter = new StringWriter();
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			try {
				Document doc = null;
				logger.info("WF Item .... "+item.getPrimaryKey());
				publishXML(ctx, sallyCatalog, stringWriter, xmlOutputFactory, item,doc,storageConnectionString,localFilePath,fileShare,outboundWorkingDirectory);
				stringWriter.flush();
				stringWriter.close();
				
			} catch (Exception e) {
				logger.info("Error in XML : " + e);
			}
		}

	}
	
	private void publishXML(Context ctx, Catalog sallyCatalog, StringWriter stringWriter,
			XMLOutputFactory xmlOutputFactory, CollaborationItem item, Document docstoreDoc, String storageConnectionString, String localFilePath, String fileShare, String outboundWorkingDirectory)
			throws XMLStreamException, PIMSearchException, IOException {
		
		
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.writeStartDocument();
		xmlStreamWriter.writeStartElement("Product_Attributes_XML");

		xmlStreamWriter.writeStartElement("Product");
		
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
		
		xmlStreamWriter.writeStartElement("ERP_legal_entities");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_LEGAL_ENTITIES) == null) ? ""
				: item.getAttributeValue(Constants.ERP_LEGAL_ENTITIES).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Pack_barcode_number");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACK_BARCODE_NUMBER) == null) ? ""
				: item.getAttributeValue(Constants.PACK_BARCODE_NUMBER).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Pack_barcode_type");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACK_BARCODE_TYPE) == null) ? ""
				: item.getAttributeValue(Constants.PACK_BARCODE_TYPE).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Pack_barcode_unit");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACK_BARCODE_UNIT) == null) ? ""
				: item.getAttributeValue(Constants.PACK_BARCODE_UNIT).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("ERP_batch_tracked_item");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_BATCH_TRACKED_ITEM) == null) ? ""
				: item.getAttributeValue(Constants.ERP_BATCH_TRACKED_ITEM).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Category_code");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_CATEGORY_CODE) == null) ? ""
				: item.getAttributeValue(Constants.ERP_CATEGORY_CODE).toString()));
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Category_name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_CATEGORY_NAME) == null) ? ""
				: item.getAttributeValue(Constants.ERP_CATEGORY_NAME).toString()));
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
		
		
		logger.info("Save the XML for Items");
		if(docstoreDoc == null)
		{
			docstoreDoc = ctx.getDocstoreManager().createAndPersistDocument("/outbound/ItemCreation/Working/" + item.getPrimaryKey() + ".xml");	
			docstoreDoc.setContent(xmlString);
		logger.info("XML Saved");
		}
		
		logger.info("Flush Stringwriter");
		stringWriter.flush();
		stringWriter.close();
	
		try {
			logger.info("Executing the cloud code ..");
            // Use the CloudStorageAccount object to connect to your storage account
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the Azure Files client.
            CloudFileClient fileClient = storageAccount.createCloudFileClient();

            logger.info("End Point URI "+fileClient.getEndpoint());
            logger.info("Path : "+(fileClient.getStorageUri().getPrimaryUri().toString()));

            // Get a reference to the file share
            CloudFileShare share = fileClient.getShareReference(fileShare);

            logger.info("Share Name : "+share.getName());

            // Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();

            // Get a reference to the working directory from root directory
            CloudFileDirectory workingDir = rootDir.getDirectoryReference(outboundWorkingDirectory);

            CloudFile cloudFile = workingDir.getFileReference(item.getPrimaryKey()+".xml");
            logger.info("File : "+cloudFile);
            //logger.info("Cloud File Text : "+cloudFile.downloadText());
            cloudFile.uploadFromFile(localFilePath+item.getPrimaryKey()+".xml");
            logger.info("File uploaded successfully");  
            docstoreDoc.moveTo("/outbound/ItemCreation/Archive/" + item.getPrimaryKey() + ".xml");
            logger.info("File archived successfully"); 
           
        }
        catch(Exception e) {
        	logger.info("Exception : "+e.getMessage());
            e.printStackTrace();
        }
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
