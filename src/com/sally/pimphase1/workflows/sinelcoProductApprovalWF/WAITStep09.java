package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.WAITStep09.class"

import java.util.Collection;
import java.util.HashMap;
import org.apache.logging.log4j.*;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class WAITStep09 implements WorkflowStepFunction {
	
	Logger logger = LogManager.getLogger(WAITStep09.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		
		logger.info("Entered In function of 08WaitStep");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		
		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
				.getCollaborationArea();
		
		CollaborationStep waitStep = currentCollaborationArea.getStep("08 WAIT");

		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}

		for (CollaborationItem item : objPIMCollection) {

			Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
			Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
			Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
			
			logger.info("isSCApproved >>>> "+isSCApproved);
			logger.info("isLegalApproved >> "+isLegalApproved);
			logger.info("isECOMApproved >> "+isECOMApproved);

			if (isLegalApproved != null && isSCApproved != null && isECOMApproved != null) {
				
				currentCollaborationArea.moveToNextStep(item, waitStep, "DONE");
				
				logger.info("set exitValue to Done...");
			}
			
//			else if ((isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE))
//					&& (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE)))
//			{
//				currentCollaborationArea.moveToNextStep(item, waitStep, "DONE");
//				logger.info("set exitValue to Done");
//				
//			}

		}
	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
