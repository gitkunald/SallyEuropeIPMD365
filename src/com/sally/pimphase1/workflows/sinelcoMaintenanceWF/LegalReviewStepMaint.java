package com.sally.pimphase1.workflows.sinelcoMaintenanceWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoMaintenanceWF.LegalReviewStepMaint.class"

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

public class LegalReviewStepMaint implements WorkflowStepFunction {
	
	Logger logger = LogManager.getLogger(LegalReviewStepMaint.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
//		logger.info("Entered In function of Maintenance LegalReviewStepMaint");
//
//		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
//
//		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
//				.getCollaborationArea();
//
//		CollaborationStep legalReviewStep = currentCollaborationArea.getStep("08 Legal Review");
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
//			Object legalAppRequired = item
//					.getAttributeValue("Product_c/Functional/Func_maint_legal_approval");
//
//			logger.info("legalAppRequired : "+legalAppRequired);
//			
//			
//			if (legalAppRequired != null && legalAppRequired.toString().equalsIgnoreCase("N")) {
//				
//				currentCollaborationArea.moveToNextStep(item, legalReviewStep, "Approve");
//				logger.info("Item moved out of Legal Approval step Maint WF in In method");
//
//			}
//			
//		}

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("*** Start of function of SinelcoMaintLegalReviewStep OUT ***");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Approve")) {
				Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
				if (isLegalApproved == null || (isLegalApproved != null && isLegalApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Product_c/is_Legal_Approved", Boolean.TRUE);
					ProcessingOptions processingOptions = item.getCollaborationArea().getProcessingOptions();
					processingOptions.setAllProcessingOptions(false);
					logger.info("Set legal Approve flag attribute to true");
					item.save();
					

				}
				
			}
			
			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Reject")) {
				Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
				if (isLegalApproved == null || (isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE))) {

					item.setAttributeValue("Product_c/is_Legal_Approved", Boolean.FALSE);
					ProcessingOptions processingOptions = item.getCollaborationArea().getProcessingOptions();
					processingOptions.setAllProcessingOptions(false);
					item.save();
					
				}
			}	
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
