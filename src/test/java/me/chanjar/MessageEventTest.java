package me.chanjar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
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
 * message event的测试
 * message和signal一样是传播的，但是只能有一个接受者。
 * 
 * Activiti并不关心实际接收到的message，message event主要用来结JMS Queue/Topic或者用来处理Webservice or REST request的请求
 * @author qianjia
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springTypicalUsageTest-context.xml")
public class MessageEventTest {
    
    @Autowired
    private RepositoryService repositoryService;
  
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    /**
     * message start event的例子
     * <pre>
     * 在这个例子里，process instance的启动不应该使用
     * runtimeService.startProcessInstanceByKey
     * 
     * 而应该使用
     * runtimeService.startProcessInstanceByMessage
     * </pre>
     */
    @Test
    @Deployment(resources = "me/chanjar/message-start-event.bpmn")
    public void messageStartEvent() {
      String processDefinitionKey = "message-start-event";
      runtimeService.startProcessInstanceByMessage("msg");
      
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    /**
     * 这个例子用来说明
     * 
     * 在跨process definition的情况下，不能有多于一个message start event监听同一个msg
     * </pre>
     */
    @Test(expected=ActivitiException.class)
    public void duplicateMessageStartEventCrossProcessDefinitions() {
      repositoryService
        .createDeployment()
        .addClasspathResource("me/chanjar/message-start-event.bpmn")
        .addClasspathResource("me/chanjar/message-start-event-2.bpmn")
        .deploy();
    }
    
    /**
     * 这个例子用来说明
     * 
     * 在同一个process definition里，不能有多于一个message start event监听同一个msg
     */
    @Test(expected=ActivitiException.class)
    public void duplicateMessageStartEventInSameProcessDefinition() {
      repositoryService
      .createDeployment()
      .addClasspathResource("me/chanjar/message-start-event-3.bpmn")
      .deploy();
    }
    
    /**
     * <pre>
     * 在这个例子里，我们使用intermediate message catch event，
     * 并在java代码里给每一个process instance发送消息。
     * 这是因为 “message只能有一个接受者”
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/message-intermediate-event-catch.bpmn")
    public void messageIntermediateCatch() {
      String processDefinitionKey = "message-intermediate-event-catch";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      runtimeService.startProcessInstanceByKey(processDefinitionKey);

      assertEquals(2, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());

      sendMessage(processDefinitionKey);
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    /**
     * 在某个User Task的时候抛出message
     * 
     * signal必须严格匹配
     */
    @Test
    @Deployment(resources="me/chanjar/message-boundary-catch.bpmn")
    public void messageBoundaryCatch() {
      String processDefinitionKey = "message-boundary-catch";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 只有当当前execution在usertask1的时候才能够发送消息，否则是没用的。
      sendMessage(processDefinitionKey);
      
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      assertNull(task1);
      
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    /**
     *  发送message
     */
    private void sendMessage(String processDefinitionKey) {
      List<Execution> executions = runtimeService
          .createExecutionQuery()
          .processDefinitionKey(processDefinitionKey)
          .messageEventSubscriptionName("msg") // 监听msg message的东西，在本例里是一个intermediate message catch event
          .list();
      for(Execution execution : executions) {
        runtimeService.messageEventReceived("msg", execution.getId());
      }
      
    }

}
