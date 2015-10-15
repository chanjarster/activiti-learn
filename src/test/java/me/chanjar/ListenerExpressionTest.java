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

import static org.junit.Assert.assertNotNull;

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

  private String processDefinitionKey = "listener-expression";

  @Test
  @Deployment(resources = "me/chanjar/listenertest/listener-expression.bpmn")
  public void test() {
    runtimeService.startProcessInstanceByKey(processDefinitionKey);
    Task usertask1 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey)
        .taskDefinitionKey("usertask1").singleResult();
    taskService.complete(usertask1.getId());

    Task usertask2 = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey)
        .taskDefinitionKey("usertask2").singleResult();
    taskService.complete(usertask2.getId());

  }

  @Test
  @Deployment(resources = "me/chanjar/listenertest/listener-expression.bpmn")
  public void testDynamicAssignment() {
    // 测试动态设置assignee, candidateUsers, candidateGroups

    // http://www.activiti.org/userguide/index.html#bpmnUserTaskUserCustomAssignmentTaskListeners
    // http://www.activiti.org/userguide/index.html#apiExpressions
    // http://www.activiti.org/userguide/index.html#springExpressions

    // 下面的逻辑看这几个类
    // AssigneeGetter
    // CandidateGroupGetter
    // CandidateUserGetter

    runtimeService.startProcessInstanceByKey(processDefinitionKey);
    Task usertask1 = taskService
        .createTaskQuery()
        .processDefinitionKey(processDefinitionKey)
        .taskDefinitionKey("usertask1")
        .taskCandidateUser("userId1")
        .singleResult();
    assertNotNull(usertask1);

    usertask1 = taskService
        .createTaskQuery()
        .processDefinitionKey(processDefinitionKey)
        .taskDefinitionKey("usertask1")
        .taskCandidateGroup("groupId1")
        .singleResult();
    assertNotNull(usertask1);

    taskService.complete(usertask1.getId());

    Task usertask2 = taskService
        .createTaskQuery()
        .processDefinitionKey(processDefinitionKey)
        .taskDefinitionKey("usertask2")
        .taskAssignee("userId2")
        .singleResult();

    assertNotNull(usertask2);

    taskService.complete(usertask2.getId());

  }

}
