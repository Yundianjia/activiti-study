package org.activiti.designer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestDymaticFormForProcessInstanceState {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"diagrams/form/DymaticForm.bpmn"})
    public void startProcess() throws Exception {
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("DymaticForm").latestVersion().singleResult();

        // 带有动态的 form 表单的的 bpmn
        FormService formService = activitiRule.getFormService();
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        assertNull(startFormData.getFormKey());

        Map<String, String> formProperties = new HashMap<String, String>();
        // 设置表单的值
        formProperties.put("name", "HenryYan");

        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), formProperties);
        assertNotNull(processInstance);

        RuntimeService runtimeService = activitiRule.getRuntimeService();
        System.err.println("=============");
//    runtimeService.suspendProcessInstanceById(processInstance.getId());
        activitiRule.getRepositoryService().suspendProcessDefinitionById(processInstance.getProcessDefinitionId(), true, null);
        System.err.println("=============");
        long count = runtimeService.createProcessInstanceQuery().active().count();
        assertEquals(0, count);
        TaskService taskService = activitiRule.getTaskService();
        long count2 = taskService.createTaskQuery().active().count();
        assertEquals(0, count2);

        runtimeService.activateProcessInstanceById(processInstance.getId());
        count = runtimeService.createProcessInstanceQuery().active().count();
        assertEquals(1, count);
        count2 = taskService.createTaskQuery().active().count();
        assertEquals(1, count2);
    }
}