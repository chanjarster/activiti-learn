package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by qianjia on 15/10/15.
 */
public class ListenerDelegateBeanWithFieldInjection implements JavaDelegate{

  private Expression text1;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    System.out.println("========================================");
    System.out.println("ListenerDelegateBeanWithFieldInjection: used in delegate expression");
    System.out.println("parameter delegateExecution: " + execution);
    System.out.println("field text1: " + text1);
    System.out.println();
  }

  public void setText1(Expression text1) {
    this.text1 = text1;
  }

}
