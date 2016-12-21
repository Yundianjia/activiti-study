package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.io.FileInputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestErrorBoundaryEventAttachToTask {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"diagrams/ErrorBoundaryEventAttachToTask.bpmn"})
    public void startProcess() throws Exception {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("name", "Activiti");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ErrorBoundaryEventAttachToTask", variableMap);
        assertNotNull(processInstance.getId());
        System.out.println("id " + processInstance.getId() + " "
                + processInstance.getProcessDefinitionId());
        Object variable = runtimeService.getVariable(processInstance.getId(), "setOnError");
        assertEquals("1", variable.toString());
    }
}