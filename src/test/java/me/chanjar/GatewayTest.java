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

/**
 * gateway测试
 * @author qianjia
 *
 */
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
    
    /**
     * <pre>
     * 一个user task + 2个outgoing sequence flow
     * 只有当usertask3和usertask2都完成的时候，才算完成
     * usertask2和usertask3的完成顺序无所谓
     * http://forums.activiti.org/comment/3900#comment-3900
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/multi-flow-nogateway.bpmn")
    public void nogateway() {
      String processDefinitionKey = "multi-flow-nogateway";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task.getId());
      
      Task usertask2 = taskService.createTaskQuery().taskDefinitionKey("usertask2").singleResult();
      taskService.complete(usertask2.getId());
      
      Task usertask3 = taskService.createTaskQuery().taskDefinitionKey("usertask3").singleResult();
      taskService.complete(usertask3.getId());

      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    
    /**
     * <pre>
     * 一个user task + exclusive gateway + 2个outgoing sequence flow
     * exclusiave gateway只会选择一个sequence flow
     * 如果有多个outgoing sequence flow的condition解析为true，则选择xml里定义的第一个flow
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/multi-flow-exclusive-gateway.bpmn")
    public void exclusiveGateway() {
      String processDefinitionKey = "multi-flow-exclusive-gateway";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task.getId());
      
      Task usertask2 = taskService.createTaskQuery().taskDefinitionKey("usertask2").singleResult();
      if (usertask2 != null) {
        taskService.complete(usertask2.getId());
      }
      
      Task usertask3 = taskService.createTaskQuery().taskDefinitionKey("usertask3").singleResult();
      if (usertask3 != null) {
        taskService.complete(usertask3.getId());
      }

      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    /**
     * <pre>
     * 一个user task + parallel gateway + 2个outgoing sequence flow
     * 从效果上看，和 simple 的例子一样
     * parallel gateway的outgoing sequence flow上的condition是没用的
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/multi-flow-parallel-gateway.bpmn")
    public void parallelGateway() {
      String processDefinitionKey = "multi-flow-parallel-gateway";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task.getId());
      
      Task usertask2 = taskService.createTaskQuery().taskDefinitionKey("usertask2").singleResult();
      taskService.complete(usertask2.getId());
      
      Task usertask3 = taskService.createTaskQuery().taskDefinitionKey("usertask3").singleResult();
      taskService.complete(usertask3.getId());

      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    /**
     * <pre>
     * 一个user task + parallel gateway + 2个outgoing sequence flow
     * 从效果上看，和 simple 的例子一样
     * inclusive gateway和parallel gateway差不多，但是可以给outgoing sequence flow加condition
     * 只有condition结果为true或者压根没有condition的outgoing sequence flow才会被选择
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/multi-flow-inclusive-gateway.bpmn")
    public void inclusivelGateway() {
      String processDefinitionKey = "multi-flow-inclusive-gateway";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task.getId());
      
      Task usertask2 = taskService.createTaskQuery().taskDefinitionKey("usertask2").singleResult();
      taskService.complete(usertask2.getId());
      
      Task usertask3 = taskService.createTaskQuery().taskDefinitionKey("usertask3").singleResult();
      taskService.complete(usertask3.getId());

      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    /**
     * <pre>
     * 一个user task + event gateway + 2个outgoing sequence flow
     * event gateway和上面三个都不同
     * 1. event gateway的outgoing sequence flow上加condition是没用的
     * 2. 只有当某个outgoing sequence flow所指向的intermediate catching event抓到了event时，
     * event gateway才会决定执行哪条sequence flow。在本例中，一个是timer intermediate 
     * catching event,一个是signal intermediate catching event。
     * 在等待5秒过后，timer所对应的那条sequence flow执行了，此时才会出现usertask2。
     * </pre>
     * @throws InterruptedException 
     */
    @Test
    @Deployment(resources="me/chanjar/multi-flow-event-gateway.bpmn")
    public void eventGateway() throws InterruptedException {
      String processDefinitionKey = "multi-flow-event-gateway";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task.getId());
      
      // 你可以在这里触发名为abc的signal event，这样的话就不会有usertask2了
      // 也可以选择不触发signal event，等待10秒钟后，usertask2会出现
      boolean signalIt = false;
      if (signalIt) {
        List<Execution> executions = runtimeService
            .createExecutionQuery()
            .processDefinitionKey(processDefinitionKey)
            .activityId("eventgateway1")  // process definition里event gateway定义的id
            .list();
        for(Execution execution : executions) {
          runtimeService.signalEventReceived("abc", execution.getId());
        }
        Task usertask2 = taskService.createTaskQuery().taskDefinitionKey("usertask2").singleResult();
        assertNull(usertask2);
      } else {
        Thread.sleep(10 * 1000);
        Task usertask2 = taskService.createTaskQuery().taskDefinitionKey("usertask2").singleResult();
        taskService.complete(usertask2.getId());
      }
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
}
