package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.TranslationtoSinelcoLangStep.class"

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

public class TranslationtoSinelcoLangStep implements WorkflowStepFunction {

	Logger logger = LogManager.getLogger(TranslationtoSinelcoLangStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered In function of TranslationStep");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();

		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
				.getCollaborationArea();

		CollaborationStep translationStep = currentCollaborationArea.getStep("02 Translation to Sinelco Languages");

		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}

		for (CollaborationItem item : objPIMCollection) {

			Object translationRequired = item
					.getAttributeValue("Sinelco_ss/Functional/Func_modify_translation_required");
			Object packagingRequired = item.getAttributeValue("Sinelco_ss/Functional/Func_modify_packaging_required");

			logger.info("translationRequired : "+translationRequired);
			logger.info("packagingRequired : "+packagingRequired);
			if (translationRequired != null && packagingRequired != null
					&& translationRequired.toString().equalsIgnoreCase("N")
					&& packagingRequired.toString().equalsIgnoreCase("Y")) {
				
				currentCollaborationArea.moveToNextStep(item, translationStep, "DONE");
				logger.info("Item moved out of translation step when only packaging is required");

			}

		}

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered OUT function of TranslationStep");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();

		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
				.getCollaborationArea();

		// 03 Translation to Sinelco Languages
		// 04 Packaging And Art Work
		CollaborationStep packagingStep = currentCollaborationArea.getStep("03 Packaging And Art Work");

		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}

		for (CollaborationItem item : objPIMCollection) {

			Object translationRequired = item
					.getAttributeValue("Sinelco_ss/Functional/Func_modify_translation_required");
			Object packagingRequired = item.getAttributeValue("Sinelco_ss/Functional/Func_modify_packaging_required");

			logger.info("translationRequired : "+translationRequired);
			logger.info("packagingRequired : "+packagingRequired);
			if (translationRequired != null && packagingRequired != null
					&& translationRequired.toString().equalsIgnoreCase("Y")
					&& packagingRequired.toString().equalsIgnoreCase("N")) {
				
				currentCollaborationArea.moveToNextStep(item, packagingStep, "DONE");
				logger.info("Item moved out of Packaging step when only translation is required");

			}

		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
