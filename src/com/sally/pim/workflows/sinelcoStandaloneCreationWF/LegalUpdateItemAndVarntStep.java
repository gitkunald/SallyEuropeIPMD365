package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.LegalUpdateItemAndVarntStep.class"

import org.apache.logging.log4j.*;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class LegalUpdateItemAndVarntStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {


		Logger logger = LogManager.getLogger(LegalUpdateItemAndVarntStep.class);
		logger.info("Entered out function of LegalUpdateItemAndVarntStep");
		
		PIMCollection<CollaborationItem> objPIMCollection = inArgs.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = inArgs.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("TranslationRework")) {
				Object isLegalRework = item.getAttributeValue("Sinelco_Product_c/is_Legal_Rework");
				if (isLegalRework == null || !isLegalRework.equals(Boolean.TRUE)) {

					item.setAttributeValue("Sinelco_Product_c/is_Legal_Rework", Boolean.TRUE);
					item.save();
					logger.info("Set legal attribute to True");

				}
			}
		}
		
		logger.info("Exit out function of LegalUpdateItemAndVarntStep");

	

	

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
