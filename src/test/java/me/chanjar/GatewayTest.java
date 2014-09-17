package me.chanjar;

import static org.junit.Assert.*;

import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springTypicalUsageTest-context.xml")
public class GatewayTest {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    @Test
    @Deployment(resources="me/chanjar/gateway-none.bpmn")
    public void gatewayNone() {
      String processDefinitionKey = "gateway-none";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task usertask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(usertask1.getId());
      
      Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(usertask2.getId());
      
      Task usertask3 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask3").singleResult();
      taskService.complete(usertask3.getId());
    
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    
    @Test
    @Deployment(resources="me/chanjar/gateway-exclusive.bpmn")
    public void exclusiveGateway() {
      String processDefinitionKey = "gateway-exclusive";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task usertask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(usertask1.getId());
      
      Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      if (usertask2 != null) {
        taskService.complete(usertask2.getId());
      }
      
      Task usertask3 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask3").singleResult();
      if (usertask3 != null) {
        taskService.complete(usertask3.getId());
      }
    
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    @Test
    @Deployment(resources="me/chanjar/gateway-parallel.bpmn")
    public void parallelGateway() {
      String processDefinitionKey = "gateway-parallel";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task usertask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(usertask1.getId());
      
      Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(usertask2.getId());
      
      Task usertask3 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask3").singleResult();
      taskService.complete(usertask3.getId());
    
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    @Test
    @Deployment(resources="me/chanjar/gateway-inclusive.bpmn")
    public void inclusivelGateway() {
      String processDefinitionKey = "gateway-inclusive";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task usertask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(usertask1.getId());
      
      Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(usertask2.getId());
      
      Task usertask3 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask3").singleResult();
      taskService.complete(usertask3.getId());
    
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    @Test
    @Deployment(resources="me/chanjar/gateway-event.bpmn")
    public void eventGateway() throws InterruptedException {
      String processDefinitionKey = "gateway-event";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task usertask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(usertask1.getId());
      
      // 你可以在这里触发名为abc的signal event，这样的话就不会有usertask2了
      // 也可以选择不触发signal event，等待10秒钟后，usertask2会出现
      boolean signalIt = true;
      if (signalIt) {
        List<Execution> executions = runtimeService
            .createExecutionQuery()
            .processDefinitionKey(processDefinitionKey)
            .signalEventSubscriptionName("abc")  // 监听abc signal的东西，在本例里是一个intermediate signal catch event
            .list();
        for(Execution execution : executions) {
          runtimeService.signalEventReceived("abc", execution.getId());
        }
        Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
        assertNull(usertask2);
      } else {
        Thread.sleep(10 * 1000);
        Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
        taskService.complete(usertask2.getId());
      }
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
}
