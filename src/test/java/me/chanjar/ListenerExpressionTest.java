package me.chanjar;

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
 * Created by qianjia on 15/10/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springTypicalUsageTest-context.xml")
public class ListenerExpressionTest {

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private TaskService taskService;

  @Autowired
  @Rule
  public ActivitiRule activitiSpringRule;

  @Test
  @Deployment(resources = "me/chanjar/listenertest/listener-expression.bpmn")
  public void test() {
    String processDefinitionKey = "listener-expression";
    runtimeService.startProcessInstanceByKey(processDefinitionKey);
    Task usertask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey)
        .taskDefinitionKey("usertask1").singleResult();
    taskService.complete(usertask1.getId());

    Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey)
        .taskDefinitionKey("usertask2").singleResult();
    taskService.complete(usertask2.getId());

  }

}
