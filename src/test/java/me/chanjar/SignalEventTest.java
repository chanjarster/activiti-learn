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
 * signal event的测试
 * 
 * signal是会广播到所有process instance的
 * signal throw的时候不会中断正在执行的流程
 * @author qianjia
 *
 */
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
    
    /**
     * 使用intermediate signal catch event来捕获signal
     * <pre>
     * 当进入到intermediate signal catch event的时候，Activiti会停在那里，直到它所期望的signal被它捕获。
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/signal-intermediate-catch.bpmn")
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
    
    /**
     * 在这个例子里，我们使用intermediate signal throw event抛出一个signal
     * 
     * <pre>
     * 然后让一个intermediate signal catch event来捕获，需要注意的是，throw前Activiti必须已经在catch了，否则是捕获不到的
     * 所以我们在intermediate signal throw event前添加了一个定时器，保证throw不会在catch前面执行
     * 
     * 值得注意的是，当signal被抛出的时候，我们就立即能够获得usertask3了，这也就说明signal的抛出不会打断流程的进程
     * </pre>
     * @throws InterruptedException 
     * 
     */
    @Test
    @Deployment(resources="me/chanjar/signal-intermediate-catch-2.bpmn")
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
    
    /**
     * 这是一个错误的使用intermediate signal catch event的例子。
     * <pre>
     * 
     * 正如 {@link #signalIntermediateCatch2()} 里所讲，throw不能在catch之前，否则就会catch不到
     * </pre>
     * @throws InterruptedException
     */
    @Test
    @Deployment(resources="me/chanjar/signal-intermediate-catch-bad.bpmn")
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
    
    /**
     * 在一个子流程里抛出signal，然后被signal boundary event捕获
     * 
     * signal必须严格匹配
     */
    @Test
    @Deployment(resources="me/chanjar/signal-boundary-catch.bpmn")
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
    
    /**
     * <pre>
     * 这个例子用来证明signal boundary catch event不仅会捕获来自其所挂载的Activit的signal
     * 也捕获全局的signal
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/signal-boundary-catch-from-outside.bpmn")
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
    
    
    /**
     * <pre>
     * 和 {@link #signalBoundaryCatchFromOutside()} 差不多，但是在捕获到signal的时候，不会取消task1
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/signal-boundary-catch-from-outside-not-cancel.bpmn")
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
    
    /**
     * 本例子用来说明signal是会传播到所有的process instance的
     */
    @Test
    @Deployment(resources="me/chanjar/signal-intermediate-propagate.bpmn")
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
    
    /**
     * 发送signal
     * @param processDefinitionKey
     */
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
