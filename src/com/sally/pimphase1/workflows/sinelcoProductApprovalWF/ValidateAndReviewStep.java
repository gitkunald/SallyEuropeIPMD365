package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.ValidateAndReviewStep.class"

import org.apache.logging.log4j.*;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class ValidateAndReviewStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
		Logger logger = LogManager.getLogger(ValidateAndReviewStep.class);
		logger.info("Entered out function of ValidateAndReviewStep");
		
		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Packaging")) {
				
				ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
						.getCollaborationArea();
				CollaborationStep sCReviewStep = currentCollaborationArea.getStep("06 Supply Chain review");
				currentCollaborationArea.moveToNextStep(item, sCReviewStep, "Approve");
				logger.info("Moved out of SC review step");

			}
		}
		
		logger.info("Exit out function of ValidateAndReviewStep");
		

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
