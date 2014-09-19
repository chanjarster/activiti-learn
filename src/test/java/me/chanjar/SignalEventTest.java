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
public class SignalEventTest {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    @Test
    @Deployment(resources="me/chanjar/signal/signal-intermediate-catch.bpmn")
    public void signalIntermediateCatch() {
      String processDefinitionKey = "signal-intermediate-catch";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());
      
      sendSignal(processDefinitionKey);
      
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/signal/signal-intermediate-catch-2.bpmn")
    public void signalIntermediateCatch2() throws InterruptedException {
      String processDefinitionKey = "signal-intermediate-catch-2";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());
      
      Thread.sleep(10 * 1000);
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      Task task3 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask3").singleResult();
      taskService.complete(task3.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/signal/signal-intermediate-catch-bad.bpmn")
    public void signalIntermediateCatchBad() throws InterruptedException {
      String processDefinitionKey = "signal-intermediate-catch-bad";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());
      
      // 没有catch到signal，所以不会有task2
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      assertNull(task2);
      
    }
    
    @Test
    @Deployment(resources="me/chanjar/signal/signal-boundary-catch.bpmn")
    public void signalBoundaryCatch() {
      String processDefinitionKey = "signal-boundary-catch";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());
      
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/signal/signal-boundary-catch-from-outside.bpmn")
    public void signalBoundaryCatchFromOutside() {
      String processDefinitionKey = "signal-boundary-catch-from-outside";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);

      // 给所有的process instance发送信号
      // 不能在task1完成后调用，因为这个时候execution并不在捕获信息的activity上
      sendSignalToAll();

      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      assertNull(task1);
      
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    
    @Test
    @Deployment(resources="me/chanjar/signal/signal-boundary-catch-from-outside-not-cancel.bpmn")
    public void signalBoundaryCatchFromOutsideAndNotCancelActivity() {
      String processDefinitionKey = "signal-boundary-catch-from-outside-not-cancel";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);

      // 给所有的process instance发送信号
      // 不能在task1完成后调用，因为这个时候execution并不在捕获信息的activity上
      sendSignalToAll();

      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      assertNotNull(task1);
      taskService.complete(task1.getId());

      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/signal/signal-intermediate-propagate.bpmn")
    public void signalIntermediateCatchPropagate() {
      String processDefinitionKey = "signal-intermediate-propagate";
      // 建立两个process instance
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      runtimeService.startProcessInstanceByKey(processDefinitionKey);

      // 此时两个process instance都还没有结束
      assertEquals(2, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());

      // throw一个signal
      runtimeService.signalEventReceived("def");
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    private void sendSignal(String processDefinitionKey) {
      List<Execution> executions = runtimeService
          .createExecutionQuery()
          .processDefinitionKey(processDefinitionKey)
          .signalEventSubscriptionName("def")   // 监听def signal的东西，在本例里是一个intermediate signal catch event
          .list();
      for(Execution execution : executions) {
        runtimeService.signalEventReceived("def", execution.getId());
      }
    }
    
    private void sendSignalToAll() {
      runtimeService.signalEventReceived("def");
    }
    
}
