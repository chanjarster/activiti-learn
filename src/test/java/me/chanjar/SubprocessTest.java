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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springTypicalUsageTest-context.xml")
public class SubprocessTest {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    @Test
    @Deployment(resources="me/chanjar/subprocess-without-end-event.bpmn")
    public void withoutEndEvent() {
      String processDefinitionKey = "subprocess-without-end-event";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("task1").singleResult();
      taskService.complete(task.getId());
      
      // 判断已经启动了一个sub process
      // 有两个Execution，一个是subtask，另一个是sub process
      assertEquals(2, runtimeService.createExecutionQuery().processDefinitionKey(processDefinitionKey).count());
      
      // 完成一个subprocess里的任务
      Task subTask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("subtask1").singleResult();
      taskService.complete(subTask1.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    @Test
    @Deployment(resources="me/chanjar/subprocess-with-end-event.bpmn")
    public void withEndEvent() {
      String processDefinitionKey = "subprocess-with-end-event";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("task1").singleResult();
      taskService.complete(task.getId());
      
      // 判断已经启动了一个sub process
      // 有两个Execution，一个是subtask，另一个是sub process
      assertEquals(2, runtimeService.createExecutionQuery().processDefinitionKey(processDefinitionKey).count());
      
      // 完成一个subprocess里的任务
      Task subTask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("subtask1").singleResult();
      taskService.complete(subTask1.getId());
      
      // 完成后面的一个任务
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("task2").singleResult();
      taskService.complete(task2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
     
    }
    
    @Test
    @Deployment(resources="me/chanjar/subprocess-nested.bpmn")
    public void nested() {
    
      String processDefinitionKey = "subprocess-nested";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      // 完成一个任务
      Task task = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("task1").singleResult();
      taskService.complete(task.getId());
      
      // 判断已经启动了一个sub process
      // 有两个Execution，一个是subtask，另一个是sub process
      assertEquals(2, runtimeService.createExecutionQuery().processDefinitionKey(processDefinitionKey).count());
      
      // 完成一个subprocess里的任务
      Task subTask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("subtask1").singleResult();
      taskService.complete(subTask1.getId());
      
      // 完成嵌套的subprocess里的任务
      Task subTask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("subtask2").singleResult();
      taskService.complete(subTask2.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
}
