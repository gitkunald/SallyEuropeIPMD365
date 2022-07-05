package com.sally.pim.workflows.sallyMaintenanceWorkflow;

import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import org.apache.logging.log4j.*;

import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.workflow.ExitValue;
public class SallyOrVendorChange implements WorkflowStepFunction{

	private static Logger logger = LogManager.getLogger(SallyOrVendorChange.class);

	Context objContext = null;
	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("SallyOrVendorChange - Initiated In");
		PIMCollection<CollaborationItem> items = arg0.getItems();
		for (CollaborationItem item : items) {
			objContext = PIMContextFactory.getCurrentContext();
			ExitValue exitValue = arg0.getTransitionConfiguration().getExitValue(item);
			logger.debug("Exit Value Is : "+exitValue.toString());
			ItemCollaborationArea sourceColArea = (ItemCollaborationArea) item.getCollaborationArea();
			logger.debug("Source Collab Area is : "+sourceColArea.getName());
			if ((exitValue != null) && (exitValue.toString().equalsIgnoreCase("Vendor"))) {
				logger.debug("Vendor Exit Value Clicked");
				LookupTable vendorLkpTable = objContext.getLookupTableManager().getLookupTable("Vendor Collab Table");
				String[] vendors = vendorLkpTable.getLookupEntryKeys();
				for (int i = 0; i < vendors.length; i++) {
					if (item.getCategories().toString().contains(vendors[i]) && 
							item.getAttributeValue("Product_c/primaryvendor_name").toString().equalsIgnoreCase(vendors[i])) {
						LookupTableEntry vendorLkpEntry = vendorLkpTable.getLookupTableEntry(vendors[i]);
						String sColArea = (String) vendorLkpEntry.getAttributeValue("Vendor Collaboration Spec/Collaboration Areas");
						CollaborationArea destinationColArea = objContext.getCollaborationAreaManager().getCollaborationArea(sColArea);
						logger.debug("Destination Collab Area : " + destinationColArea.getName());
						item.setAttributeValue("VendorInfo/Rework", "True");
						item.setAttributeValue("Product_c/item_previous_workflow_info",sourceColArea.getName());
						item.save();
						sourceColArea.moveItemToOtherCollaborationArea(item, destinationColArea);
						logger.debug("Item(s) Successfully moved : "+item.getCollaborationArea().getName());
						continue;
					}
				}
			}
		}
		logger.info("SallyOrVendorChange - Ending In");
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}
}
