package com.sally.pimphase1.workflows.sinelcoMaintenanceWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoMaintenanceWF.TranslationToSinelcoLangMaintStep.class"

import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;
import com.sally.pimphase1.workflows.sinelcoProductApprovalWF.TranslationtoSinelcoLangStep;

public class TranslationToSinelcoLangMaintStep implements WorkflowStepFunction {
	
	Logger logger = LogManager.getLogger(TranslationToSinelcoLangMaintStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered In function of Maintenance TranslationStep");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();

		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
				.getCollaborationArea();

		CollaborationStep translationStep = currentCollaborationArea.getStep("03 Translation to Sinelco Languages");
		

		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}

		for (CollaborationItem item : objPIMCollection) {

			Object translationRequired = item
					.getAttributeValue("Product_c/Functional/Func_maint_translation");

			logger.info("translationRequired : "+translationRequired);
			
			
			if (translationRequired != null && translationRequired.toString().equalsIgnoreCase("N")) {
				
				currentCollaborationArea.moveToNextStep(item, translationStep, "DONE");
				logger.info("Item moved out of translation step Maint WF in In method");

			}
			
			

		}

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered OUT function of TranslationStep");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();

		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
				.getCollaborationArea();

		CollaborationStep packagingStep = currentCollaborationArea.getStep("04 Packaging And Art Work");

		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}

		for (CollaborationItem item : objPIMCollection) {

			Object packagingRequired = item.getAttributeValue("Product_c/Functional/Func_maint_packaging");

			logger.info("packagingRequired : "+packagingRequired);
			
			if (packagingRequired != null && packagingRequired.toString().equalsIgnoreCase("N")) {
				
				currentCollaborationArea.moveToNextStep(item, packagingStep, "DONE");
				logger.info("Item moved out of Packaging step Maint WF in Out func");

			}

		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
