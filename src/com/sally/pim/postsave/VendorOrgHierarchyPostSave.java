package com.sally.pim.postsave;


import com.ibm.ccd.common.util.Configuration;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.PostCategorySaveFunctionArguments;
import com.ibm.pim.extensionpoints.PostCollaborationCategorySaveFunctionArguments;
import com.ibm.pim.extensionpoints.PostCollaborationItemSaveFunctionArguments;
import com.ibm.pim.extensionpoints.PostItemSaveFunctionArguments;
import com.ibm.pim.extensionpoints.PostSaveFunction;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.organization.OrganizationHierarchy;
import com.ibm.pim.organization.OrganizationManager;
import com.ibm.pim.organization.Performer;
import com.ibm.pim.organization.User;
import com.ibm.pim.workflow.Workflow;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class VendorOrgHierarchyPostSave implements PostSaveFunction {
	  
	  private static Logger logger = Logger.getLogger(VendorOrgHierarchyPostSave.class);
	  
	  @Override
	  public void postsave(PostCategorySaveFunctionArguments inArgs) {
		    logger.info("*** Start of function of VendorOrgHierarchyPostSave Post Save ***");
		    Category vendorCategory = inArgs.getCategory();
		    String vendorName = vendorCategory.getPrimaryKey();
		    Context ctx = PIMContextFactory.getCurrentContext();
		    OrganizationManager orgManager = ctx.getOrganizationManager();
		    OrganizationHierarchy vendorHierarchy = orgManager.getOrganizationHierarchy(vendorCategory.getHierarchy().getName());
		    Collection<Catalog> catalogs = ctx.getCatalogManager().getCatalogs();
		    Workflow workflow = ctx.getWorkflowManager().getWorkflow(Configuration.getValue("vendor_product_workflow"));
		    Iterator<Catalog> itr = catalogs.iterator();
		    StringBuffer collabList = new StringBuffer();
		    LookupTable lookupTable = ctx.getLookupTableManager().getLookupTable(Configuration.getValue("vendor_collab_table"));
		    LookupTable vendorLookupTable = ctx.getLookupTableManager().getLookupTable("Vendor Lookup Table");
//		    vendorLookupTable.getLookupTableEntries().iterator();
//		    while ()
//		    if (vendorLookupTable.getLookupTableEntry("").getValues().contains("")) {
//		    	vendorLookupTable.getLookupTableEntry("").getKey();
//		    	set
//		    }		    
//		    else {
//		    	
//		    }
		    String lookupSpecName = lookupTable.getSpec().getName();
		    String vendorLookupSpecName = vendorLookupTable.getSpec().getName();
		    LookupTableEntry lookupTableEntry = lookupTable.getLookupTableEntry(vendorName);
		    LookupTableEntry vendorLookupTableEntry = vendorLookupTable.getLookupTableEntry(vendorName);
		    if (lookupTableEntry != null) {
		      String collabAreas = (String)lookupTableEntry.getAttributeValue(lookupSpecName + "/Collaboration Areas");
		      collabList.append(collabAreas);
		    } 
		    while (itr.hasNext()) {
		      Catalog ctg = itr.next();
		      String collabAreaName = vendorName + " Product Edit Collaboration Area For " + ctg.getName();
		      if (ctx.getCollaborationAreaManager().getCollaborationArea(collabAreaName) == null) {
		        Collection<OrganizationHierarchy> secondaryOrganizationHierarchies = ctg.getSecondaryOrganizationHierarchies();
		        if (secondaryOrganizationHierarchies.contains(vendorHierarchy)) {
		          ItemCollaborationArea itemCollaborationArea = ctx.getCollaborationAreaManager().createItemCollaborationArea(collabAreaName, workflow, ctg);
		          Collection<Performer> admins = itemCollaborationArea.getAdministrators();
		          User user = ctx.getOrganizationManager().getUser("Dummy Vendor");
		          admins.add(user);
		          itemCollaborationArea.setAdministrators(admins);
		          itemCollaborationArea.save();
		          if (!collabList.toString().equalsIgnoreCase(""))
		            collabList.append("|"); 
		          collabList.append(collabAreaName);
		        } 
		      } 
		    } 
		    if (lookupTableEntry != null) {
		      lookupTableEntry.setAttributeValue(lookupSpecName + "/Collaboration Areas", collabList.toString());
		      lookupTableEntry.save();
		    } else {
		      LookupTableEntry newVendorEntry = lookupTable.createEntry();
		      newVendorEntry.setAttributeValue(lookupSpecName + "/Vendor", vendorName);
		      newVendorEntry.setAttributeValue(lookupSpecName + "/Collaboration Areas", collabList.toString());
		      newVendorEntry.save();
		    } 
		    
		    if (vendorLookupTableEntry == null) {
		    	LookupTableEntry newVendorEntry = vendorLookupTable.createEntry();
			    newVendorEntry.setAttributeValue(vendorLookupSpecName + "/name", vendorName);
			    newVendorEntry.save();
		    }
		    logger.info("*** End of function of VendorOrgHierarchyPostSave Post Save ***");
		  }
		  
	    @Override  
	  	public void postsave(PostCollaborationItemSaveFunctionArguments inArgs) {}
		  
	  	@Override
		public void postsave(PostCollaborationCategorySaveFunctionArguments inArgs) {}

		@Override
		public void postsave(PostItemSaveFunctionArguments arg0) {
			// TODO Auto-generated method stub
			
		}

}