package com.sally.pim.workflows.sinelcoProductInitiationWorkflow;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoProductInitiationWorkflow.SinelcoCreateStep.class"

import java.util.Iterator;

import org.apache.log4j.Logger;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ExtendedValidationErrors;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;

public class SinelcoCreateStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {

		Logger logger = Logger.getLogger(SinelcoCreateStep.class);
		logger.info("*** Start of OUT function of Create Step ***");
		Context ctx = PIMContextFactory.getCurrentContext();
		
		PIMCollection<CollaborationItem> items = inArgs.getItems();
		logger.info("Number of items : " + items.size());
		ItemCollaborationArea sourceColArea = (ItemCollaborationArea) inArgs.getCollaborationStep().getCollaborationArea();
		
		for (Iterator<CollaborationItem> iterator = items.iterator(); iterator.hasNext();) {
			CollaborationItem collaborationItem = (CollaborationItem) iterator.next();
			logger.info("Collab Item Name : " + collaborationItem.getDisplayName());
			logger.info("Categories mapped : " + collaborationItem.getCategories().toString());
			CollaborationArea destinationColArea = ctx.getCollaborationAreaManager().getCollaborationArea("Sinelco Shared Products Approval Collaboration Area");
			logger.info("Destination Collab Area : " + destinationColArea.getName());
			sourceColArea.moveItemToOtherCollaborationArea(collaborationItem, destinationColArea);
			
		}
		logger.info("*** End of OUT function of Create Step ***");
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
