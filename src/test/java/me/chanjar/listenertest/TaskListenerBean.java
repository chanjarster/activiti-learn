package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * Created by qianjia on 15/10/15.
 */
public class TaskListenerBean implements TaskListener {

  @Override
  public void notify(DelegateTask delegateTask) {
    System.out.println("========================================");
    System.out.println("JavaDelegateBean: used in delegate expression");
    System.out.println("parameter delegateTask: " + delegateTask);
    System.out.println();
  }

}
