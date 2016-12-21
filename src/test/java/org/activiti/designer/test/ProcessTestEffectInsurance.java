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

/**
 * effectInsurance 第一个真正的复杂的投保流程, 其中的分支节点之多, 业务之复杂, 以及错综复杂的流程节点,非常值得编写一个全量的完整的测试用例
 *
 * @note 编写测试用例, 将流程图中所有的可能的流程全部跑一遍
 *
 */
public class ProcessTestEffectInsurance {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"diagrams/joying/effectInsurance.bpmn"})
    public void startProcess() throws Exception {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("name", "Activiti");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("effectInsurance", variableMap);
        assertNotNull(processInstance.getId());
        System.out.println("id " + processInstance.getId() + " "
                + processInstance.getProcessDefinitionId());
    }
}