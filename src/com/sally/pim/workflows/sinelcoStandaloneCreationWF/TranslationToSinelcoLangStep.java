package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.TranslationToSinelcoLangStep.class"

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class TranslationToSinelcoLangStep implements WorkflowStepFunction {
	
	Logger logger = Logger.getLogger(TranslationToSinelcoLangStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {
		
		logger.info("Entered out function of TranslationToSinelcoLangStep");

		PIMCollection<CollaborationItem> objPIMCollection = inArgs.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = inArgs.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			logger.info("exitValue >> " + exitValue.toString());

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("DONE")) {

				Object isLegalRework = item.getAttributeValue("Sinelco_Product_c/is_Legal_Rework");
				Object isSCRework = item.getAttributeValue("Sinelco_Product_c/is_SC_Rework");
				Object isECOMRework = item.getAttributeValue("Sinelco_Product_c/is_ECOM_Rework");

				logger.info("isLegalRework >> " + isLegalRework);
				logger.info("isSCRework >> " + isSCRework);
				logger.info("isECOMRework >> " + isECOMRework);

				Object isLegalApproved = item.getAttributeValue("Sinelco_Product_c/is_Legal_Approved");
				Object isSCApproved = item.getAttributeValue("Sinelco_Product_c/is_SC_Approved");
				Object isECOMApproved = item.getAttributeValue("Sinelco_Product_c/is_ECOM_Approved");

				logger.info("isLegalApproved >> " + isLegalApproved);
				logger.info("isSCApproved >> " + isSCApproved);
				logger.info("isECOMApproved >> " + isECOMApproved);

				ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) inArgs.getCollaborationStep()
						.getCollaborationArea();
				CollaborationStep eCOMReviewStep = currentCollaborationArea.getStep("Sinelco Ecom Review");
				CollaborationStep legalReviewStep = currentCollaborationArea.getStep("Sinelco Legal Review");
				CollaborationStep sCReviewStep = currentCollaborationArea.getStep("Sinelco Supply Chain Review");

				logger.info("isSCRework check111 >> " + (isSCRework != null && isSCRework.equals(Boolean.TRUE)));
				logger.info("islegalwork check >> " + (isLegalRework == null || (isLegalRework != null && isLegalRework.equals(Boolean.FALSE))));
				logger.info("isECOMRework check >> " + (isECOMRework == null || (isECOMRework != null && isECOMRework.equals(Boolean.FALSE))));
				
				if ((isLegalRework != null && isLegalRework.equals(Boolean.TRUE))
						&& (isSCRework == null || (isSCRework != null && isSCRework.equals(Boolean.FALSE)))
						&& (isECOMRework == null || (isECOMRework != null && isECOMRework.equals(Boolean.FALSE)))) {

					logger.info("Item should stay only in Legal step, so moving it from ECOM and SC");
					// Item should stay only in Legal step, so moving it from ECOM and SC

					if (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE)) 
					{
						currentCollaborationArea.moveToNextStep(item, eCOMReviewStep, "Approve");
					}
					
					if (isSCApproved != null && isSCApproved.equals(Boolean.TRUE))
					{
						currentCollaborationArea.moveToNextStep(item, sCReviewStep, "Approve");
					}

				}
				
				else if ((isSCRework != null && isSCRework.equals(Boolean.TRUE))
						&& (isLegalRework == null || (isLegalRework != null && isLegalRework.equals(Boolean.FALSE)))
						&& (isECOMRework == null || (isECOMRework != null && isECOMRework.equals(Boolean.FALSE)))) {

					// Item should stay only in Supply chain step
					logger.info("Item should stay only in Supply chain step1111");
							if (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE)) {
								
								logger.info("isECOM Approved is true");
								currentCollaborationArea.moveToNextStep(item, eCOMReviewStep, "Approve");
								
							}
							
							if (isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE))
							{
								logger.info("islegal Approved is true");
								currentCollaborationArea.moveToNextStep(item, legalReviewStep, "Approve");
							}

				}

				else if ((isECOMRework != null && isECOMRework.equals(Boolean.TRUE))
						&& (isLegalRework == null || (isLegalRework != null && isLegalRework.equals(Boolean.FALSE)))
						&& (isSCRework == null || (isSCRework != null && isSCRework.equals(Boolean.FALSE)))) {

					// Item should stay only in ECOM step
					logger.info("Item should stay only in ECOM step");

					if (isSCApproved != null && isSCApproved.equals(Boolean.TRUE)){
						currentCollaborationArea.moveToNextStep(item, sCReviewStep, "Approve");
						
					}
					
					if (isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE))
					{
						currentCollaborationArea.moveToNextStep(item, legalReviewStep, "Approve");
					}

				}

				else if ((isECOMRework != null && isECOMRework.equals(Boolean.TRUE))
						&& (isLegalRework != null && isLegalRework.equals(Boolean.TRUE))
						&& (isSCRework == null || (isSCRework != null && isSCRework.equals(Boolean.FALSE)))) {

					// Item should stay In ECOM and Legal and move out of SC
					logger.info("Item should stay In ECOM and Legal and move out of SC");
					if (isSCApproved != null && isSCApproved.equals(Boolean.TRUE)) {
						currentCollaborationArea.moveToNextStep(item, sCReviewStep, "Approve");
					}

				}

				else if ((isECOMRework != null && isECOMRework.equals(Boolean.TRUE))
						&& (isSCRework != null && isSCRework.equals(Boolean.TRUE))
						&& (isLegalRework == null || (isLegalRework != null && isLegalRework.equals(Boolean.FALSE)))) {

					// Item should stay In ECOM and SC and move out of Legal
					logger.info("Item should stay In ECOM and SC and move out of Legal");
					if (isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE)) {
						currentCollaborationArea.moveToNextStep(item, legalReviewStep, "Approve");
					}

				}

				else if ((isLegalRework != null && isLegalRework.equals(Boolean.TRUE))
						&& (isSCRework != null && isSCRework.equals(Boolean.TRUE))
						&& (isECOMRework == null || (isECOMRework != null && isECOMRework.equals(Boolean.FALSE)))) {

					// Item should stay In Legal and SC and move out of ECOM
					logger.info("Item should stay In Legal and SC and move out of ECOM");
					if (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE)) {
						currentCollaborationArea.moveToNextStep(item, eCOMReviewStep, "Approve");
					}

				}

			}
		}
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
