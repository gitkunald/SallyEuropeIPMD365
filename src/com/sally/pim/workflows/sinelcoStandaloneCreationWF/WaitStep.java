package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.WaitStep.class"

import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.*;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class WaitStep implements WorkflowStepFunction {
	Logger logger = LogManager.getLogger(WaitStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		
		logger.info("Entered In function of WaitStep");

		PIMCollection<CollaborationItem> objPIMCollection = inArgs.getItems();
		
		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) inArgs.getCollaborationStep()
				.getCollaborationArea();
		
		CollaborationStep waitStep = currentCollaborationArea.getStep("Wait");

		Collection<ExitValue> objCollExitValue = inArgs.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}

		for (CollaborationItem item : objPIMCollection) {

			Object isLegalApproved = item.getAttributeValue("Sinelco_Product_c/is_Legal_Approved");
			Object isSCApproved = item.getAttributeValue("Sinelco_Product_c/is_SC_Approved");
			Object isECOMApproved = item.getAttributeValue("Sinelco_Product_c/is_ECOM_Approved");

			if ((isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE))
					&& (isSCApproved != null && isSCApproved.equals(Boolean.TRUE))
					&& (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE))) {
				
				currentCollaborationArea.moveToNextStep(item, waitStep, "DONE");
				
				logger.info("set exitValue to Done");
			}

		}
	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {
		
		logger.info("Entered out function of WaitStep");

		PIMCollection<CollaborationItem> objPIMCollection = inArgs.getItems();

		for (CollaborationItem item : objPIMCollection) {

			Object isECOMApproved = item.getAttributeValue("Sinelco_Product_c/is_ECOM_Approved");
			Object isSCApproved = item.getAttributeValue("Sinelco_Product_c/is_SC_Approved");
			Object isLegalApproved = item.getAttributeValue("Sinelco_Product_c/is_Legal_Approved");
			if (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE)) {

				item.setAttributeValue("Sinelco_Product_c/is_ECOM_Approved", "");
				logger.info("clear ECOM flag attribute");

			}
			
			if (isSCApproved != null && isSCApproved.equals(Boolean.TRUE)) {

				item.setAttributeValue("Sinelco_Product_c/is_SC_Approved", "");
				logger.info("clear SC flag attribute");

			}
			
			if (isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE)) {

				item.setAttributeValue("Sinelco_Product_c/is_Legal_Approved", "");
				logger.info("clear Legal flag attribute");

			}

			
			item.save();
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
