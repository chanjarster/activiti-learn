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

/**
 * event的测试
 * <pre>
 * </pre>
 * @author qianjia
 *
 */
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
    
    /**
     * timer start event，定为5秒钟后进入usertask1
     * 1. subprocess不能有timer start event
     * 2. timer start event在部署的时候就自动启动了，不需要runtimeService.startProcessInstanceByXXX 
     * 3. 如果部署了一个新版本升级了，那么原先的定时器就被删除了 
     * @throws InterruptedException 
     */
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
    
    /**
     * timer boundary event，定义在usertask1上，5秒钟时长是5秒，cancel activity为true
     * 
     * <pre>
     * 当流程走到usertask1上的时候，如果在5秒钟内没有完成usertask1
     * 那么就会取消usertask1，并进入到usertask2上，完成usertask2才能结束流程恒
     * 相反，如果在5秒钟内完成了usertask1，那么流程就直接结束了
     * 
     * 本例人为制造超时
     * </pre>
     * @throws InterruptedException 
     * 
     */
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-cancel.bpmn")
    public void timerBoundaryEventCancelWait() throws InterruptedException {
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
    
    /**
     * timer boundary event，定义在usertask1上，5秒钟时长是5秒，cancel activity为true
     * 
     * 和 {@link #timerBoundaryEventCancelWait()} 一样，只是不人为制造超时
     * @throws InterruptedException
     */
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-cancel.bpmn")
    public void timerBoundaryEventCancelNoWait() throws InterruptedException {
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
    
    /**
     * timer boundary event，定义在usertask1上，5秒钟时长是5秒，cancel activity为false
     * 
     * <pre>
     * 当流程走到usertask1上的时候，如果在5秒钟内没有完成usertask1
     * 那么会产生一条usertask2，完成usertask1,2才能结束流程
     * 相反，如果5秒钟内完成了usertask1，那么流程就直接结束了，
     * 
     * 本例人为制造超时
     * </pre>
     * @throws InterruptedException 
     * 
     */
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-not-cancel.bpmn")
    public void timerBoundaryEventNotCancelWait() throws InterruptedException {
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
    
    /**
     * timer boundary event，定义在usertask1上，5秒钟时长是5秒，cancel activity为false
     * 
     * 和 {@link #timerBoundaryEventNotCancelWait()} 一样，只是不人为制造超时
     * @throws InterruptedException
     */
    @Test
    @Deployment(resources="me/chanjar/timer-boundary-event-not-cancel.bpmn")
    public void timerBoundaryEventNotCancelNoWait() throws InterruptedException {
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
    
    /**
     * timer intermediate catching event，定义时长为5秒钟
     * 
     * 当5秒钟一过，才会进入到usertask1里
     * 
     * 可以结合event gateway使用，参考 {@link GatewayTest#eventGateway()}
     * @throws InterruptedException
     */
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
