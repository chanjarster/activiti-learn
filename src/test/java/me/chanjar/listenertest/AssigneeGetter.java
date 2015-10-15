package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateExecution;

/**
 * Created by qianjia on 15/10/15.
 */
public class AssigneeGetter {

  /**
   * 动态设置assignee只能返回单值
   *
   * @param execution
   * @return
   */
  public String get(DelegateExecution execution) {

    System.out.println("========================================");
    System.out.println("AssigneeGetter: used in assignee expression");
    System.out.println("parameter execution: " + execution);
    System.out.println("parameter execution.getCurrentActivityId: " + execution.getCurrentActivityId());
    System.out.println("parameter execution.getCurrentActivityName: " + execution.getCurrentActivityName());
    System.out.println();
    return "userId2";

  }

}
