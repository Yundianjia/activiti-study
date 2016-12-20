package org.activiti.designer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

/**
 * 测试具有调用活动的子流程:  调用 bpmn 以及 网关 bpmn
 *
 */
public class ProcessTestCallActivityByExpression {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = { "diagrams/CallActivityByExpression.bpmn", "diagrams/Gateway.bpmn" })
	public void startProcess() throws Exception {
		RuntimeService runtimeService = activitiRule.getRuntimeService();

		// 设置初始的变量值, 变量 activityName - BPMN 中设置的变量替换值, processID 是 gateway
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("activityName", "gateway");
		
		IdentityService identityService = activitiRule.getIdentityService();
		identityService.setAuthenticatedUserId("henryyan");

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("CallActivityByExpression", variableMap);
		assertNotNull(processInstance.getId());

		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());
		
		TaskService taskService = activitiRule.getTaskService();
		Task singleResult = taskService.createTaskQuery().singleResult();
		identityService.setAuthenticatedUserId("henryyan");  // 设置运行时, 任务的权限

        System.out.println("当前任务的名称: " + singleResult.getName() + "; 执行的用户: " + singleResult.getAssignee());
        taskService.complete(singleResult.getId());

        // 进入子流程的调用环节
        // 1. 查看当前流程实例数量
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().list();
        System.out.println("当前的流程实例的数量: " + processInstanceList.size());
        assertEquals(2, processInstanceList.size());

        // 2. 查看当前全部的执行流数量
        List<Execution> executions = runtimeService.createExecutionQuery().list();
        System.out.println("当前的执行流的数目: " + executions.size());
        assertEquals(3, executions.size());

        // 3. 查看当前的任务, 根据当前实例,就是应该查不到
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertEquals(null, task);
        if ( task != null ) { System.out.println("当前任务的名称: " + task.getName() + "; 执行的用户: " + task.getAssignee()); }

        // 4. 不限定流程实例的查询
        task = taskService.createTaskQuery().singleResult();
        assertNotNull(task);

        long count = activitiRule.getHistoryService().createHistoricProcessInstanceQuery().count();
		assertEquals(2, count);

        // 打印历史数据
		List<HistoricProcessInstance> list = activitiRule.getHistoryService().createHistoricProcessInstanceQuery().list();
		for (HistoricProcessInstance historicProcessInstance : list) {
			assertEquals("henryyan", historicProcessInstance.getStartUserId());
		}
	}
}