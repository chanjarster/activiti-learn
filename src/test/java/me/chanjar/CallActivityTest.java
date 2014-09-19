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
public class CallActivityTest {
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;
    
    @Autowired
    public RepositoryService repositoryService;
    
    @Test
    @Deployment(resources={"me/chanjar/call/call-activity-main.bpmn", "me/chanjar/call/call-activity-sub.bpmn"})
    public void test() throws InterruptedException {
      String mainProcessDefinitionKey = "call-activity-main";
      String subProcessDefinitionKey = "call-activity-sub";
      String businessKey = "businessKey";
      Map<String, Object> variables = new HashMap<String, Object>();
      /*
       * 在这里设置了a_in_sub是没用的，因为parent process instance里的变量名
       * 是不共享到sub process instance的
       */
      variables.put("a_in_main", Boolean.TRUE);
      variables.put("businessKey", businessKey);
      // variables.put("a_in_sub", Boolean.TRUE);

      runtimeService.startProcessInstanceByKey(mainProcessDefinitionKey, businessKey, variables);
      
      // 当走到call activity的时候，会产生一条新的process instance，而这个process instance是属于call activity的
      assertEquals(2, runtimeService.createProcessInstanceQuery().count());

      // 完成一个任务
      Task task = taskService
            .createTaskQuery()
            // 必须是根据sub process的definition key来查找task
            .processDefinitionKey(subProcessDefinitionKey)
            // 如果找到和这个process instance关联的sub process instance里的task
            // 就要用到在sub process instance里设置的变量
            .processVariableValueEquals("businessKey", businessKey)
            .taskDefinitionKey("usertask1")
            .singleResult();
      taskService.complete(task.getId());
      
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(mainProcessDefinitionKey).count());
    }
    
    @Test
    @Deployment(resources={"me/chanjar/call/call-activity-main.bpmn", "me/chanjar/call/call-activity-sub.bpmn"})
    public void test2() throws InterruptedException {
      String mainProcessDefinitionKey = "call-activity-main";
      String subProcessDefinitionKey = "call-activity-sub";
      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("a_in_main", Boolean.TRUE);
      // variables.put("a_in_sub", Boolean.TRUE);

      runtimeService.startProcessInstanceByKey(mainProcessDefinitionKey, variables);
      
      // 当走到call activity的时候，会产生一条新的process instance，而这个process instance是属于call activity的
      assertEquals(2, runtimeService.createProcessInstanceQuery().count());
      
      // 完成一个任务
      Task task = taskService
            .createTaskQuery()
            // 必须是根据sub process的definition key来查找task
            .processDefinitionKey(subProcessDefinitionKey)
            .taskDefinitionKey("usertask1")
            .singleResult();
      taskService.complete(task.getId());
    
      // 判断process instance已经结束
      assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(mainProcessDefinitionKey).count());
    }
    
}
