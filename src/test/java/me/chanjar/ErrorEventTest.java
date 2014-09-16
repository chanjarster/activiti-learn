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
 * error event的测试
 * 
 * error event只应该被用作business fault，也就是说应该被用来处理业务流程上的错误，而不是java上的错误
 * @author qianjia
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springTypicalUsageTest-context.xml")
public class ErrorEventTest {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    /**
     * error start event测试
     * 
     * <pre>
     * 完成usertask1后，进入error end event(error:abc)，随即这个error被event subprocess里的
     * error start event(catch error:abc)捕获到，然后进入usertask2，当usertask2完成的时候，整个流程结束
     * 
     * 1. 只能在event subprocess里使用
     * 2. error event 不能以数字开头
     * 3. error event 只局限于当前process instance
     * 4. error event 总是会打断整个流程
     * 5. 如果在usertask2后面加上一个error end event，那么就会又进入到event subprocess里
     * 6. error end event才能够抛出error，可以被event subprocess里的error start event捕获，也可以被Error Boundary Event捕获到
     * 
     * 捕获error的规则：
     * 1. 如果error start event没有指定error，那么就捕获所有error
     * 2. 如果在整个process中没有定义过任何一个可能抛出的error（比如没有error end event，或者error end event没有设定error），那么也是捕获所有error
     * 3. 如果以上两种情况都不是，那么就只捕获匹配的error
     * </pre>
     * @throws InterruptedException 
     */
    @Test
    @Deployment(resources="me/chanjar/error-start-event.bpmn")
    public void errorStartEvent() throws InterruptedException {
      String processDefinitionKey = "error-start-event";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());

      // 完成一个任务
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    
    /**
     * error boundary event测试
     * 
     * <pre>
     * 1. error boundary event只能用在subprocess或者call activity上
     * 捕获error的规则和 {@link #errorStartEvent()} 一样
     * </pre>
     * 
     * @throws InterruptedException
     */
    @Test
    @Deployment(resources="me/chanjar/error-boundary-event.bpmn")
    public void errorBoundaryEvent() throws InterruptedException {
      String processDefinitionKey = "error-boundary-event";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 完成一个任务
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId());

      // 完成一个任务
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    /**
     * 在java代码里抛出bpmn error，让error boundary event捕获
     * <pre>
     * 在这里我们使用了service task，对应的Java代码是ThrowBPMNErrorTask
     * 
     * 捕获规则和前两个有些区别：
     * 1. 如果error boundary event没有指定error，那么就捕获所有java代码里抛出的任何error
     * 2. 如果不是，那么指捕获匹配的error
     * 
     * </pre>
     */
    @Test
    @Deployment(resources="me/chanjar/error-boundary-event-throw-bpmn-error.bpmn")
    public void throwBPMNErrors() {
      String processDefinitionKey = "error-boundary-event-throw-bpmn-error";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      // 完成一个任务
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
}
