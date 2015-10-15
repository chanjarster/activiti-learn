package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateExecution;

/**
 * Created by qianjia on 15/10/14.
 */
public class SimpleBean {

  public void inExpression(DelegateExecution delegateExecution, String a, String b) {
    System.out.println("========================================");
    System.out.println("SimpleBean: used in expression");
    System.out.println("parameter delegateExecution: " + delegateExecution);
    System.out.println("parameter a: " + a);
    System.out.println("parameter b: " + b);
    System.out.println();
  }

}
