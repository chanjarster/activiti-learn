package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;

/**
 * Created by qianjia on 15/10/14.
 */
public class SimpleBeanWithFieldInjection {

  private Expression text1;

  public void inExpression(DelegateExecution execution, String a, String b) {
    System.out.println("========================================");
    System.out.println("SimpleBeanWithFieldInjection: used in expression");
    System.out.println("parameter execution: " + execution);
    System.out.println("parameter execution.getCurrentActivityId: " + execution.getCurrentActivityId());
    System.out.println("parameter execution.getCurrentActivityName: " + execution.getCurrentActivityName());
    System.out.println("parameter a: " + a);
    System.out.println("parameter b: " + b);
    // 当在expression中使用的时候, 无法注入field
    System.out.println("field text1: " + text1);
    System.out.println("当在expression中使用的时候, 无法注入field");
    System.out.println();

  }

  public void setText1(Expression text1) {
    this.text1 = text1;
  }

}
