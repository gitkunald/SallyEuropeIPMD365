package com.sally.pimphase1.workflows.sinelcoMaintenanceWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoMaintenanceWF.AutomatedApprovalStep05.class"

import java.util.Collection;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class AutomatedApprovalStep05 implements WorkflowStepFunction {
	
	Logger logger = LogManager.getLogger(AutomatedApprovalStep05.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered Out function of AutomatedApprovalStep05");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		
		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
				.getCollaborationArea();

		CollaborationStep sCReviewStep = currentCollaborationArea.getStep("06 Supply Chain Review");
		CollaborationStep eCOMReviewStep = currentCollaborationArea.getStep("07 Marketing Review");
		CollaborationStep legalReviewStep = currentCollaborationArea.getStep("08 Legal Review");
		

		HashMap<String, ExitValue> objHashMap = new HashMap<>();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();
		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}
		
		for (CollaborationItem item : objPIMCollection) {

			Object isLegalRequired = item.getAttributeValue("Product_c/Functional/Func_maint_legal_approval");
			Object isSCRequired= item.getAttributeValue("Product_c/Functional/Func_maint_SC_approval");
			Object isECOMRequired = item.getAttributeValue("Product_c/Functional/Func_maint_ecom_approval");
			
			logger.info("isSCRequired >> "+isSCRequired);
			logger.info("isLegalRequired >> "+isLegalRequired);
			logger.info("isECOMRequired >> "+isECOMRequired);

			
			if ((isLegalRequired != null && isLegalRequired.toString().equalsIgnoreCase("N"))
					&& (isSCRequired != null && isSCRequired.toString().equalsIgnoreCase("N"))
					&& (isECOMRequired != null && isECOMRequired.toString().equalsIgnoreCase("N"))) {
				
				
				objCollabStepTransConfig.setExitValue((CollaborationObject) item, objHashMap.get("No Approval Required"));
				
				logger.info("set exitValue to No Approval Req");
			}
			else
			{
				objCollabStepTransConfig.setExitValue((CollaborationObject) item, objHashMap.get("Approval Required"));
				if (isSCRequired != null && isSCRequired.toString().equalsIgnoreCase("N")) {
					
					currentCollaborationArea.moveToNextStep(item, sCReviewStep, "Approve");
					logger.info("Item moved out of SC Approval step Maint WF in In method");

				}
				
				if (isECOMRequired != null && isECOMRequired.toString().equalsIgnoreCase("N")) {
					
					currentCollaborationArea.moveToNextStep(item, eCOMReviewStep, "Approve");
					logger.info("Item moved out of ECOM Approval step Maint WF in In method");

				}	
				
				if (isLegalRequired != null && isLegalRequired.toString().equalsIgnoreCase("N")) {
					
					currentCollaborationArea.moveToNextStep(item, legalReviewStep, "Approve");
					logger.info("Item moved out of Legal Approval step Maint WF in In method");

				}
			}
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
