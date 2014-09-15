package me.chanjar;

import static org.junit.Assert.assertEquals;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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
 * 多个outgoing sequence flow的测试
 * @author qianjia
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springTypicalUsageTest-context.xml")
public class MultiFlowTest {
    
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
    @Deployment(resources="me/chanjar/multi-flow-simple.bpmn")
    public void simple() {
      String processDefinitionKey = "multi-flow-simple";
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
}
