package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.EcomUpdateItemAndVarntStep.class"
import org.apache.logging.log4j.*;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class EcomUpdateItemAndVarntStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		Logger logger = LogManager.getLogger(EcomUpdateItemAndVarntStep.class);
		logger.info("Entered out function of EcomUpdateItemAndVarntStep");
		
		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("TranslationRework")) {
				Object isECOMRework = item.getAttributeValue("Sinelco_Product_c/is_ECOM_Rework");
				if (isECOMRework == null || !isECOMRework.equals(Boolean.TRUE)) {

					item.setAttributeValue("Sinelco_Product_c/is_ECOM_Rework", Boolean.TRUE);
					item.save();
					logger.info("Set ECOM attribute to True");

				}
			}
		}
		
		logger.info("Exit out function of EcomUpdateItemAndVarntStep");

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
