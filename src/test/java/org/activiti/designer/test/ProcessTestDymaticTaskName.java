package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.io.FileInputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestDymaticTaskName {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = { "diagrams/DymaticTaskName.bpmn" })
	public void startProcess() throws Exception {
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		// 设置 启动任务的变量值, 在解析时, 传入可动态替换的值
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "Activiti");

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("DymaticTaskName", variableMap);
		assertNotNull(processInstance.getId());  // 判断为空性的验证

		System.out.println("id " + processInstance.getId() + " "
				+ processInstance.getProcessDefinitionId());
		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("User TaskActiviti", task.getName());
	}
}