package com.sally.pim.workflows.sallyCreateItemsWorkflow;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ExtendedValidationErrors;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;

public class CreateStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {

		Logger logger = Logger.getLogger(CreateStep.class);
		logger.info("*** Start of OUT function of Create Step ***");
		Context ctx = PIMContextFactory.getCurrentContext();
		
		PIMCollection<CollaborationItem> items = inArgs.getItems();
		logger.info("Number of items : " + items.size());
		ItemCollaborationArea sourceColArea = (ItemCollaborationArea) inArgs.getCollaborationStep().getCollaborationArea();
		LookupTable vendorLkpTable = ctx.getLookupTableManager().getLookupTable("Vendor Collab Table");
		String[] vendors = vendorLkpTable.getLookupEntryKeys();
		
		for (Iterator<CollaborationItem> iterator = items.iterator(); iterator.hasNext();) {
			CollaborationItem collaborationItem = (CollaborationItem) iterator.next();
			logger.info("Collab Item Name : " + collaborationItem.getDisplayName());
			logger.info("Categories mapped : " + collaborationItem.getCategories().toString());
			
			for (int i = 0; i < vendors.length; i++) {
				if (collaborationItem.getCategories().toString().contains(vendors[i])) {
					LookupTableEntry vendorLkpEntry = vendorLkpTable.getLookupTableEntry(vendors[i]);
					String sColArea = (String) vendorLkpEntry.getAttributeValue("Vendor Collaboration Spec/Collaboration Areas");
					CollaborationArea destinationColArea = ctx.getCollaborationAreaManager().getCollaborationArea(sColArea);
					logger.info("Destination Collab Area : " + destinationColArea.getName());
					sourceColArea.moveItemToOtherCollaborationArea(collaborationItem, destinationColArea);
					logger.info("Item(s) Successfully moved");
					continue;
				}
			}
		}
		logger.info("*** End of OUT function of Create Step ***");
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
