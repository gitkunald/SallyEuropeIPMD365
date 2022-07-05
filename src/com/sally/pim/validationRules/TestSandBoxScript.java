package com.sally.pim.validationRules;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.validationRules.TestSandBoxScript.class"

import org.apache.logging.log4j.*;

import com.ibm.pim.extensionpoints.ScriptingSandboxFunction;
import com.ibm.pim.extensionpoints.ScriptingSandboxFunctionArguments;

public class TestSandBoxScript implements ScriptingSandboxFunction {
	
	private static Logger logger = LogManager.getLogger(TestSandBoxScript.class);

	@Override
	public void scriptingSandbox(ScriptingSandboxFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
		logger.info("Testttttt");

	}

}
