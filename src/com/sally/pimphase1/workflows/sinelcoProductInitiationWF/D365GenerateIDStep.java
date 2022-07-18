package com.sally.pimphase1.workflows.sinelcoProductInitiationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductInitiationWF.D365GenerateIDStep.class"

import java.util.Iterator;
import org.apache.logging.log4j.*;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;

public class D365GenerateIDStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		Logger logger = LogManager.getLogger(D365GenerateIDStep.class);
		logger.info("*** Start of OUT function of D365 Generate ID Step ***");
		Context ctx = PIMContextFactory.getCurrentContext();
		
		PIMCollection<CollaborationItem> items = arg0.getItems();
		logger.info("Number of items : " + items.size());
		ItemCollaborationArea sourceColArea = (ItemCollaborationArea) arg0.getCollaborationStep().getCollaborationArea();
		
		for (Iterator<CollaborationItem> iterator = items.iterator(); iterator.hasNext();) {
			CollaborationItem collaborationItem = (CollaborationItem) iterator.next();
			logger.info("Collab Item Name : " + collaborationItem.getDisplayName());
			CollaborationArea destinationColArea = ctx.getCollaborationAreaManager().getCollaborationArea("Product Approval Collaboration Area");
			logger.info("Destination Collab Area : " + destinationColArea.getName());
			sourceColArea.moveItemToOtherCollaborationArea(collaborationItem, destinationColArea);
			
		}
		logger.info("*** End of OUT function of D365 Generate ID Step ***");

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
