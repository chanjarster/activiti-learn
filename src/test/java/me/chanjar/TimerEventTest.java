package me.chanjar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springTypicalUsageTest-context.xml")
public class TimerEventTest {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    @Test
    @Deployment(resources="me/chanjar/timer-start-event.bpmn")
    public void timerStartEvent() throws InterruptedException {
      String processDefinitionKey = "timer-start-event";
      
      Thread.sleep(10 * 1000);
      // 完成一个任务
      Task task = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task.getId());
    
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-cancel.bpmn")
    public void timerBoundaryEventTimeoutAndCancelActivity() throws InterruptedException {
      String processDefinitionKey = "timer-boundary-event-cancel";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      Thread.sleep(10 * 1000);
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      assertNull(task1);
      
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-cancel.bpmn")
    public void timerBoundaryEventFinishOnTimeAndCancelActivity() throws InterruptedException {
      String processDefinitionKey = "timer-boundary-event-cancel";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());
    
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      assertNull(task2);
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-not-cancel.bpmn")
    public void timerBoundaryEventTimeoutAndNotCancelActivity() throws InterruptedException {
      String processDefinitionKey = "timer-boundary-event-not-cancel";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      Thread.sleep(10 * 1000);
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      assertNotNull(task1);
      taskService.complete(task1.getId());
    
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-not-cancel.bpmn")
    public void timerBoundaryEventFinishOnTimeAndNotCancel() throws InterruptedException {
      String processDefinitionKey = "timer-boundary-event-not-cancel";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());
    
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      assertNull(task2);
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    @Test
    @Deployment(resources="me/chanjar/timer-intermediate-catch-event.bpmn")
    public void timerIntermediateCatchEvent() throws InterruptedException {
      String processDefinitionKey = "timer-intermediate-catch-event";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      assertNull(task1);
      
      Thread.sleep(10 * 1000);
      task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      assertNotNull(task1);
      taskService.complete(task1.getId());
    
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
}
