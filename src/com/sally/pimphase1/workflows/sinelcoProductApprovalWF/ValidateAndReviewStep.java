package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.ValidateAndReviewStep.class"

import org.apache.logging.log4j.*;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class ValidateAndReviewStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		Logger logger = LogManager.getLogger(ValidateAndReviewStep.class);
		logger.info("Entered IN function of ValidateAndReviewStep");
		
		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();

		for (CollaborationItem item : objPIMCollection) {
			
			Object scRejectionComments = item.getAttributeValue("Product_c/Status Attributes/SC_Rejection_Comments");
			Object legalRejectionComments = item.getAttributeValue("Product_c/Status Attributes/Legal_Rejection_Comments");
			Object eCOMRejectionComments = item.getAttributeValue("Product_c/Status Attributes/ECOM_Rejection_Comments");
			
			Object commentsHistoryObj = item.getAttributeValue("Product_c/Status Attributes/Comments_History");
			String scCommentConcat = "";
			String eCOMCommentConcat = "";
			String legalCommentConcat = "";
			String finalConcatValue = "";
			
			if(scRejectionComments != null)
			{
				if (commentsHistoryObj != null)
				{
					scCommentConcat = commentsHistoryObj.toString() + "|" + scRejectionComments.toString();
				}
				else
				{
					scCommentConcat = scRejectionComments.toString();
				}
			}
			
			logger.info("scCommentConcat >> "+scCommentConcat);
			if(eCOMRejectionComments != null)
			{
				
				if (commentsHistoryObj != null && scCommentConcat.isEmpty())
				{
					eCOMCommentConcat = commentsHistoryObj.toString() + "|" + eCOMRejectionComments.toString();
				}
				else if (!scCommentConcat.isEmpty())
				{
					eCOMCommentConcat = scCommentConcat + "|" + eCOMRejectionComments.toString();
				}
				else
				{
					eCOMCommentConcat = eCOMRejectionComments.toString();
				}
			}
			
			logger.info("eCOMCommentConcat >> "+eCOMCommentConcat);
			
			if(legalRejectionComments != null)
			{
				if (commentsHistoryObj != null && scCommentConcat.isEmpty() && eCOMCommentConcat.isEmpty())
				{
					legalCommentConcat = commentsHistoryObj.toString() + "|" + legalRejectionComments.toString();
				}
				else if((!scCommentConcat.isEmpty() && !eCOMCommentConcat.isEmpty()) || 
						(scCommentConcat.isEmpty() && !eCOMCommentConcat.isEmpty()))
				{
					legalCommentConcat = eCOMCommentConcat + "|" + legalRejectionComments.toString();
				}
				
				else if (!scCommentConcat.isEmpty() && eCOMCommentConcat.isEmpty())
				{
					legalCommentConcat = scCommentConcat + "|" + legalRejectionComments.toString();
				}
				
				else
				{
					legalCommentConcat = legalRejectionComments.toString();
					
				}
			}
			
			logger.info("legalCommentConcat >> "+legalCommentConcat);
			
			if (!legalCommentConcat.isEmpty())
			{
				finalConcatValue = legalCommentConcat;
			}
			else if (legalCommentConcat.isEmpty() && !eCOMCommentConcat.isEmpty())
			{
				finalConcatValue = eCOMCommentConcat;
			}
			
			else if (legalCommentConcat.isEmpty() && eCOMCommentConcat.isEmpty() && !scCommentConcat.isEmpty())
			{
				finalConcatValue = scCommentConcat;
			}
			else
			{
				finalConcatValue = commentsHistoryObj.toString();
			}

			logger.info("finalConcatValue >> "+finalConcatValue);
			if(!finalConcatValue.isEmpty())
			{
				item.setAttributeValue("Product_c/Status Attributes/Comments_History", finalConcatValue);
			}
			
			item.getCollaborationArea().getProcessingOptions().setAllProcessingOptions(false);
			item.save();
			item.getCollaborationArea().getProcessingOptions().resetProcessingOptions();
		}

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
		Logger logger = LogManager.getLogger(ValidateAndReviewStep.class);
		logger.info("Entered out function of ValidateAndReviewStep");
		
		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Packaging")) {
				
				ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
						.getCollaborationArea();
				CollaborationStep sCReviewStep = currentCollaborationArea.getStep("04 Supply Chain review");
				currentCollaborationArea.moveToNextStep(item, sCReviewStep, "Approve");
				logger.info("Moved out of SC review step");

			}
			
			Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
			Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
			Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
			Object funcReject = item.getAttributeValue("Product_c/Functional/Func_reject_on_create");
			
			
			if (isECOMApproved != null) {

				item.setAttributeValue("Product_c/is_ECOM_Approved", "");
				logger.info("clear ECOM flag attribute");

			}
			
			if (isSCApproved != null) {

				item.setAttributeValue("Product_c/is_SC_Approved", "");
				logger.info("clear SC flag attribute");

			}
			
			if (isLegalApproved != null) {

				item.setAttributeValue("Product_c/is_Legal_Approved", "");
				logger.info("clear Legal flag attribute");

			}
			
			if (funcReject != null) {

				item.setAttributeValue("Product_c/Functional/Func_reject_on_create", "");
				logger.info("clear Reject flag attribute");

			}
			
			
			//item.getCollaborationArea().getProcessingOptions().setAllProcessingOptions(false);
			item.save();
		}
		
		logger.info("Exit out function of ValidateAndReviewStep");
		

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
