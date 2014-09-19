package me.chanjar;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
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
public class TimerEventLazyTest {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    @Autowired
    public RepositoryService repositoryService;
    
    @Test(expected=org.activiti.engine.ActivitiException.class)
    public void timerStartEvent() throws InterruptedException {
      repositoryService.createDeployment().addClasspathResource("me/chanjar/timer/timer-start-event-lazy.bpmn").deploy();
    }
    
    @Test
    @Deployment(resources="me/chanjar/timer/timer-intermediate-catch-event-lazy.bpmn")
    public void timerIntermediateCatchEvent() throws InterruptedException {
      String processDefinitionKey = "timer-intermediate-catch-event-lazy";
      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("duration", "P0Y0M0DT0H0M5S");
      runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
      
      Thread.sleep(10 * 1000);
      // 完成一个任务
      Task task = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task.getId());
    
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources="me/chanjar/timer/timer-intermediate-catch-event-lazy-2.bpmn")
    public void timerIntermediateCatchEvent2() throws InterruptedException {
      String processDefinitionKey = "timer-intermediate-catch-event-lazy-2";
      runtimeService.startProcessInstanceByKey(processDefinitionKey);
      
      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("duration", "P0Y0M0DT0H0M5S");
      
      Task task1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask1").singleResult();
      taskService.complete(task1.getId(), variables);
      
      Thread.sleep(10 * 1000);
      // 完成一个任务
      Task task2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskDefinitionKey("usertask2").singleResult();
      taskService.complete(task2.getId());
    
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).count());
    }
    
}
