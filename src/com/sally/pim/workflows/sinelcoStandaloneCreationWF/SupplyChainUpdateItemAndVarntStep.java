package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.SupplyChainUpdateItemAndVarntStep.class"

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class SupplyChainUpdateItemAndVarntStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {

		Logger logger = Logger.getLogger(SupplyChainUpdateItemAndVarntStep.class);
		logger.info("Entered out function of SupplyChainUpdateItemAndVarntStep");
		
		PIMCollection<CollaborationItem> objPIMCollection = inArgs.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = inArgs.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("TranslationRework")) {
				Object isSCRework = item.getAttributeValue("Sinelco_Product_c/is_SC_Rework");
				if (isSCRework == null || !isSCRework.equals(Boolean.TRUE)) {

					item.setAttributeValue("Sinelco_Product_c/is_SC_Rework", Boolean.TRUE);
					item.save();
					logger.info("Set SC attribute to True");

				}
			}
		}
		
		logger.info("Exit out function of SupplyChainUpdateItemAndVarntStep");

	

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
