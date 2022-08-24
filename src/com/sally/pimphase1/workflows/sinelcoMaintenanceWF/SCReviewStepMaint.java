package com.sally.pimphase1.workflows.sinelcoMaintenanceWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoMaintenanceWF.SCReviewStepMaint.class"

import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ProcessingOptions;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class SCReviewStepMaint implements WorkflowStepFunction {
	
	Logger logger = LogManager.getLogger(SCReviewStepMaint.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
//		logger.info("Entered In function of Maintenance SCReviewStepMaint");
//
//		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
//
//		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
//				.getCollaborationArea();
//
//		CollaborationStep sCReviewStep = currentCollaborationArea.getStep("06 Supply Chain Review");
//		
//
//		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
//		HashMap<String, ExitValue> objHashMap = new HashMap<>();
//
//		for (ExitValue exitValue : objCollExitValue) {
//			objHashMap.put(exitValue.toString(), exitValue);
//		}
//
//		for (CollaborationItem item : objPIMCollection) {
//
//			Object sCAppRequired = item
//					.getAttributeValue("Product_c/Functional/Func_maint_SC_approval");
//
//			logger.info("sCAppRequired : "+sCAppRequired);
//			
//			
//			if (sCAppRequired != null && sCAppRequired.toString().equalsIgnoreCase("N")) {
//				
//				currentCollaborationArea.moveToNextStep(item, sCReviewStep, "Approve");
//				logger.info("Item moved out of SC Approval step Maint WF in In method");
//
//			}
//			
//		}

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("*** Start of function of SinelcoMaintSCReviewStep OUT ***");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Approve")) {
				
				Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
				if (isSCApproved == null || (isSCApproved != null && isSCApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Product_c/is_SC_Approved", Boolean.TRUE);
					logger.info("Set SC Approve flag attribute to true");
					ProcessingOptions processingOptions = item.getCollaborationArea().getProcessingOptions();
					processingOptions.setAllProcessingOptions(false);
					item.save();
					logger.info("SC_Approved flag : " +item.getAttributeValue("Product_c/is_SC_Approved"));
					
				}
				
			}
			
			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Reject")) {
				Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
				if (isSCApproved == null || (isSCApproved != null && isSCApproved.equals(Boolean.TRUE))) {

					item.setAttributeValue("Product_c/is_SC_Approved", Boolean.FALSE);
					ProcessingOptions processingOptions = item.getCollaborationArea().getProcessingOptions();
					processingOptions.setAllProcessingOptions(false);
					item.save();
					logger.info("Set SC Approve flag attribute to false");
				}
			}	
		}


	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
