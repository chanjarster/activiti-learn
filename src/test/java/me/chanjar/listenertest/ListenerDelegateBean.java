package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Created by qianjia on 15/10/15.
 */
public class ListenerDelegateBean implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    System.out.println("========================================");
    System.out.println("ListenerDelegateBean: used in delegate expression");
    System.out.println("parameter delegateExecution: " + execution);
    System.out.println();
  }

}
