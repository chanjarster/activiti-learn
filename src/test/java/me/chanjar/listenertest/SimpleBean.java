package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateExecution;

/**
 * Created by qianjia on 15/10/14.
 */
public class SimpleBean {

  public void inExpression(DelegateExecution execution, String a, String b) {
    System.out.println("========================================");
    System.out.println("SimpleBean: used in expression");
    System.out.println("parameter execution: " + execution);
    System.out.println("parameter execution.getCurrentActivityId: " + execution.getCurrentActivityId());
    System.out.println("parameter execution.getCurrentActivityName: " + execution.getCurrentActivityName());
    System.out.println("parameter a: " + a);
    System.out.println("parameter b: " + b);
    System.out.println();
  }

}
