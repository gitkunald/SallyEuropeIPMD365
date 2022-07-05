package com.sally.pim.workflows.ownerApprovalWorkflow;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.ownerApprovalWorkflow.ERPStep.class"


import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;
import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.*;

public class ERPStep implements WorkflowStepFunction {
	Logger logger = LogManager.getLogger(ERPStep.class);
	Context ctx = PIMContextFactory.getCurrentContext();

	public void in(WorkflowStepFunctionArguments inArgs) {
	}

	public void out(WorkflowStepFunctionArguments inArgs) {
		Logger logger = LogManager.getLogger(ERPStep.class);
		logger.info("*** Start of OUT function of Create Step ***");
		Context ctx = PIMContextFactory.getCurrentContext();
		PIMCollection<CollaborationItem> items = inArgs.getItems();
		logger.info("Number of items : " + items.size());

		CollaborationStepTransitionConfiguration transConfig = inArgs.getTransitionConfiguration();

		ItemCollaborationArea sourceCollaborationArea = (ItemCollaborationArea) inArgs.getCollaborationStep()
				.getCollaborationArea();
		logger.info("Source Collaboration Area : " + sourceCollaborationArea);

		CollaborationArea destinationCollaborationArea = ctx.getCollaborationAreaManager()
				.getCollaborationArea("Sally_New_Product_Publish_ColArea");
		logger.info("Destination Collaboration Area.. : " + destinationCollaborationArea);
		Iterator<CollaborationItem> itr = items.iterator();
		while (itr.hasNext()) {
			CollaborationItem itm = itr.next();
			ExitValue exitValue = transConfig.getExitValue((CollaborationObject) itm);
			logger.info("Exit Value : " + exitValue);
			if (!exitValue.toString().equals("DONE") && !exitValue.toString().equals("CHECKIN"))
				itr.remove();
		}
		if (!items.isEmpty())
			sourceCollaborationArea.moveItemsToOtherCollaborationArea((Collection) items, destinationCollaborationArea);
		logger.info("Item(s) Successfully moved");
		logger.info("*** End of OUT function of Create Step ***");
	}

	

	public void timeout(WorkflowStepFunctionArguments inArgs) {
	}

}
