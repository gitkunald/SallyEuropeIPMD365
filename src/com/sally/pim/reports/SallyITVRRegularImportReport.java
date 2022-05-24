package com.sally.pim.reports;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ExtendedValidationErrors;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.common.exceptions.PIMSearchException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Directory;
import com.ibm.pim.docstore.DocstoreManager;
import com.ibm.pim.extensionpoints.ReportGenerateFunction;
import com.ibm.pim.extensionpoints.ReportGenerateFunctionArguments;
import com.ibm.pim.organization.Company;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;

public class SallyITVRRegularImportReport implements ReportGenerateFunction {

	private static Logger logger = Logger.getLogger(SallyPublishAndITAndVRReport.class);
	Context ctx = PIMContextFactory.getCurrentContext();
	Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally_Products_Catalog");
	HashMap<String, HashMap<String, String>> xmlValuesHashMap = new HashMap<>();
	DocstoreManager docstoreManager = ctx.getDocstoreManager();
	Directory directory = docstoreManager.getDirectory("ITVRRegularImport/");
	PIMCollection<com.ibm.pim.docstore.Document>  documents = directory.getDocuments();
	
	@Override
	public void reportGenerate(ReportGenerateFunctionArguments arg0) {
		
		try { 
			System.out.println("1Calling before fetchITVRRegularDataFromDocstoreXML"+xmlValuesHashMap);
			xmlValuesHashMap = fetchITVRRegularDataFromDocstoreXML(documents);
			System.out.println("Calling after fetchITVRRegularDataFromDocstoreXML"+xmlValuesHashMap);
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HashMap<String,String> isUpdatedAllItems= new HashMap<>();
		
		//Reading data from the xmlValuesHashMap
		for(String mdmKey:xmlValuesHashMap.keySet())
		{
			Boolean isUpdatedItems=false;
			try {
				isUpdatedItems=	iTVRUpdation(mdmKey);
				System.out.println("Is saved successfully : " + isUpdatedItems);
			} catch (PIMSearchException | ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!isUpdatedItems)	
			{
				System.out.println("Failed to Update an Item: " + mdmKey);
				isUpdatedAllItems.put(mdmKey, "Failed");
				
			}
		}
		System.out.println("isUpdatedAllItems.... isUpdatedAllItems: " + isUpdatedAllItems);
		//If nothing is failed then documents to archive folder
		if(!isUpdatedAllItems.containsValue("Failed"))
		{
			if(!documents.isEmpty() && documents.size()>0)
			{
			for (com.ibm.pim.docstore.Document document : documents) {
				System.out.println("Moving to Archive folder: " );
			    document.copyTo("ITVRRegularImport/archive/");
			}
			}
		}
		
   }


		

	
	private HashMap<String, HashMap<String, String>> fetchITVRRegularDataFromDocstoreXML(PIMCollection<com.ibm.pim.docstore.Document>  documents)
			throws ParserConfigurationException, SAXException, IOException {
	
	//	HashMap<String, HashMap<String,String>> xmlValuesHashMap = new HashMap<>();
		if(!documents.isEmpty() && documents.size()>0)
		{
		for (com.ibm.pim.docstore.Document document : documents) {

			
			String filePath = uploadFileInRealPath(document);
			
			File fXmlFile = new File(filePath);
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("Product");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				HashMap<String,String> hmValues = new HashMap<>();
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String enterpriseId = eElement.getElementsByTagName("Enterprise_ID").item(0).getTextContent();
				    String entityType = eElement.getElementsByTagName("Entity_Type").item(0).getTextContent();
					hmValues.put("EntityType", entityType);
					String erpID = eElement.getElementsByTagName("ERP_ID").item(0).getTextContent();
					hmValues.put("ERPID", erpID);
					String source = eElement.getElementsByTagName("Source").item(0).getTextContent();
					hmValues.put("Source", source);
							
					if(entityType.equalsIgnoreCase("Variant"))
					{
						NodeList ERPListNames = eElement.getElementsByTagName("ERP_ID");
						for (int i = 0; i < ERPListNames.getLength(); i++) {
                          if(i>0)
                          hmValues.put("ERPID_"+i, ERPListNames.item(i).getTextContent());
                          else
                            hmValues.put("ERPID", ERPListNames.item(i).getTextContent());  
						  }
						NodeList SourceListNames = eElement.getElementsByTagName("Source");
						for (int i = 0; i < SourceListNames.getLength(); i++) {
                         if(i>0)
                          hmValues.put("Source_"+i, SourceListNames.item(i).getTextContent());
                         else
                         hmValues.put("Source", SourceListNames.item(i).getTextContent());
                     	}
						String statusNL = eElement.getElementsByTagName("statusNL").item(0).getTextContent();
						String statusFR = eElement.getElementsByTagName("statusFR").item(0).getTextContent();
						String statusDE = eElement.getElementsByTagName("statusDE").item(0).getTextContent();
						String statusES = eElement.getElementsByTagName("statusES").item(0).getTextContent();
						String statusBE = eElement.getElementsByTagName("statusBE").item(0).getTextContent();
						String statusGBAX2012 = eElement.getElementsByTagName("statusGBAX2012").item(0).getTextContent();
						String statusGBAX2009 = eElement.getElementsByTagName("statusGBAX2009").item(0).getTextContent();
						hmValues.put("statusNL", statusNL);
						hmValues.put("statusFR", statusFR);
						hmValues.put("statusDE", statusDE);
						hmValues.put("statusES", statusES);
						hmValues.put("statusBE", statusBE);
						hmValues.put("statusGBAX2012", statusGBAX2012);
						hmValues.put("statusGBAX2009", statusGBAX2009);
					}	
					xmlValuesHashMap.put(enterpriseId, hmValues);
					
				}
			}
	   	 }
       }
		else
		{
		System.out.println("No files are existed to process....");
		logger.info("No files are existed to process....");
        }
		
		return xmlValuesHashMap;
	}
	private String uploadFileInRealPath(com.ibm.pim.docstore.Document document) {
		System.out.println("Entered uploadFileInRealPath method111");

		String sSrcFileCopyLocation = "/public_html/tmp_files/";
		com.ibm.pim.docstore.Document tmpDoc = document.copyTo(sSrcFileCopyLocation);
		String tmpDocPath = tmpDoc.getPath();
		//Dev env path
		String sFileSystemRootPath = "/opt/IBM/MDM";
		//SIT path
		//String sFileSystemRootPath = "/data/opt/IBM/MDM";
		int index = tmpDocPath.lastIndexOf("/");
		String sSourceFileName = tmpDocPath.substring(index + 1);
		Company company = PIMContextFactory.getCurrentContext().getCurrentUser().getCompany();
		String compName = company.getName();
		String sSystemFilePath = sFileSystemRootPath + "/public_html/suppliers/" + compName + "/tmp_files/"
				+ sSourceFileName;

		System.out.println("Exit uploadFileInRealPath method with fileSystemPath : " + sSystemFilePath);
		return sSystemFilePath;
	}
private boolean iTVRUpdation(String key) throws ParserConfigurationException, SAXException, IOException, PIMSearchException {

		System.out.println("Entered iTVRUpdation() method for the item : "+key);
		boolean iTVRUpdated = false;
		boolean baseItemUpdated = false;
		//boolean variantsUpdated = false;
					
	HashMap<String,String>	getHMvalues=xmlValuesHashMap.get(key);
	String varItemEntityType=getHMvalues.get("EntityType");
	String varItemSource=getHMvalues.get("Source");
	String varItemErpId=getHMvalues.get("ERPID");
	if(key!= null && varItemEntityType!=null )
	{
		String varItemSearchQuery2 = "select item from Catalog('Sally_Products_Catalog') where item['Product_c/MDM_item_id'] = "+key;
		SearchQuery selectSearchQueryVarnt2 = ctx
				.createSearchQuery(varItemSearchQuery2);
		SearchResultSet selectResultSetVarnt2 = selectSearchQueryVarnt2
				.execute();
     //System.out.println("Found Item.."+selectResultSetVarnt2.size());
		while (selectResultSetVarnt2.next()) {

			Item varItem=null;
			try {
				varItem = selectResultSetVarnt2.getItem(1);
				//System.out.println("Found Item in catalog.."+varItem.getPrimaryKey());
			} catch (PIMSearchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if Item checked out then get from collaboration Area
			//System.out.println("Found Item is checkedout.."+varItem.isCheckedOut());
			if(varItem.isCheckedOut())
			{
			String baseItemSearchQuery = "select item from collaboration_area('Sally Maintenance') where item['Product_c/MDM_item_id'] = "+key;
			SearchQuery selectSearchQuery = ctx.createSearchQuery(baseItemSearchQuery);
			SearchResultSet selectResultSet = selectSearchQuery.execute();
			
		  while (selectResultSet.next()) {
				try {
					
				 varItem = selectResultSet.getItem(1);
				// System.out.println("get Item from collabarea.."+varItem.getPrimaryKey());
				}
				catch (PIMSearchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
			//if its Item then set the ERP,Source attribute values after checking the variants is matching
			if(varItem != null && varItemEntityType.equalsIgnoreCase("Item"))
                {
                	Boolean allVariantsInfoPresentInXML = true;
					AttributeInstance variantInstance = varItem
							.getAttributeInstance("Item_ss/variants");

					if (variantInstance != null) {
						for (int x = 0; x < variantInstance.getChildren().size(); x++) {

							String varId = varItem
									.getAttributeValue("Item_ss/variants#" + x + "/variant_id")
									.toString();
							
							if (!xmlValuesHashMap.containsKey(varId))
							{
								allVariantsInfoPresentInXML = false;
							}
						}
					}
					
					if (allVariantsInfoPresentInXML)
					{
														
						varItem.setAttributeValue("Item_ss/erp_item_id/item_id", varItemErpId);
						varItem.setAttributeValue("Item_ss/erp_item_id/source", varItemSource);
					    varItem.getCatalog().getProcessingOptions().setAllProcessingOptions(false);
		                System.out.println("Item ERP ID is set and value " +varItem.getAttributeValue("Item_ss/erp_item_id/item_id"));
						ExtendedValidationErrors extendedValErrors = varItem.save();
		                varItem.getCatalog().getProcessingOptions().resetProcessingOptions();
						if (extendedValErrors != null) {
							System.out.println("Error saving Base Item : " + varItem.getPrimaryKey());
							List<ValidationError> errors = extendedValErrors.getErrors();
	
							for (ValidationError error : errors) {
	
								System.out.println(error.toString());
							}
							baseItemUpdated=false;
						 }
						else 
						{
								baseItemUpdated = true;
						 }
					}
              }
				//if its variant then set the status attribute values
                if(varItem != null && varItemEntityType.equalsIgnoreCase("Variant"))
				{
			
				   //1 ERPID update
				   if (!varItemErpId.isEmpty()) {
					varItem.setAttributeValue(
							"Variant_ss/erp_variant_id#0/rbovariantid",
							varItemErpId);
				     }
				   //2 ERPID update
				   if(getHMvalues.containsKey("ERPID_1"))
				   {
				   String varItemErpId2=getHMvalues.get("ERPID_1");
				   if (!varItemErpId2.isEmpty()) {
						varItem.setAttributeValue(
								"Variant_ss/erp_variant_id#1/rbovariantid",
								varItemErpId2);
					     }
				   }
				 //3 ERPID update
				   if(getHMvalues.containsKey("ERPID_2"))
				   {
				   String varItemErpId3=getHMvalues.get("ERPID_2");
				   if (!varItemErpId3.isEmpty()) {
						varItem.setAttributeValue(
								"Variant_ss/erp_variant_id#2/rbovariantid",
								varItemErpId3);
					     } 
				   }
				   
				   
					//#1 Source update
					if (!varItemSource.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/erp_variant_id#0/source",
								varItemSource);
					}
					//#2 Source update
					if(getHMvalues.containsKey("Source_1"))
					   {

						String varItemsrcId2=getHMvalues.get("Source_1");
						if (!varItemsrcId2.isEmpty()) {
							varItem.setAttributeValue("Variant_ss/erp_variant_id#1/source",
									varItemsrcId2);
						}
					   }
					//#3 Source update
					if(getHMvalues.containsKey("Source_2"))
					   {

						String varItemsrcId3=getHMvalues.get("Source_2");
						if (!varItemsrcId3.isEmpty()) {
							varItem.setAttributeValue("Variant_ss/erp_variant_id#2/source",
									varItemsrcId3);
						}
					   }		
					//statusNL update
					String varItemstatusNL=getHMvalues.get("statusNL");
					if (!varItemstatusNL.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/status/statusNL",
								varItemstatusNL);
					}
					//statusFR update
					String varItemstatusFR=getHMvalues.get("statusFR");
					if (!varItemstatusFR.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/status/statusFR",
								varItemstatusFR);
					}
					//statusDE update
					String varItemstatusDE=getHMvalues.get("statusDE");
					if (!varItemstatusDE.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/status/statusDE",
								varItemstatusDE);
					}
					//statusBE update
					String varItemstatusBE=getHMvalues.get("statusBE");
					if (!varItemstatusBE.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/status/statusBE",
								varItemstatusBE);
					}
					
					//statusES update
					String varItemstatusES=getHMvalues.get("statusES");
					if (!varItemstatusES.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/status/statusES",
								varItemstatusES);
					}
					
					//varItemstatusAX2009 update
					String varItemstatusAX2009=getHMvalues.get("statusGBAX2009");
					if (!varItemstatusAX2009.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/status/statusGBAX2009",
								varItemstatusAX2009);
					}
					//varItemstatusAX2012 update
					String varItemstatusAX2012=getHMvalues.get("statusGBAX2012");
					if (!varItemstatusAX2012.isEmpty()) {
						varItem.setAttributeValue("Variant_ss/status/statusGBAX2012",
								varItemstatusAX2012);
					}
						
				varItem.getCatalog().getProcessingOptions().setAllProcessingOptions(false);
				ExtendedValidationErrors extendedValidationErrors = varItem.save();
				varItem.getCatalog().getProcessingOptions().resetProcessingOptions();

				if (extendedValidationErrors != null) {
					System.out.println("Error saving Variant Item : "
							+ varItem.getPrimaryKey());
					List<ValidationError> errors = extendedValidationErrors
							.getErrors();

					for (ValidationError error : errors) {

						System.out.println(error.toString());
					}
					baseItemUpdated = false;
				}
				else
				{
					baseItemUpdated = true;
					System.out.println("Variant Item ERP ID is set and value " +varItem.getAttributeValue("Variant_ss/erp_variant_id/rbovariantid"));
					
				}
			  }
			}
	    }
    
			if (baseItemUpdated )
			{
				iTVRUpdated = true;
			}
			
		return iTVRUpdated;
	}
	
	
	
}