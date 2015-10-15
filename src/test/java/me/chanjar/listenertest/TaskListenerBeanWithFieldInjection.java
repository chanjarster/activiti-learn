package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

/**
 * Created by qianjia on 15/10/15.
 */
public class TaskListenerBeanWithFieldInjection implements TaskListener {

  private Expression text1;

  @Override
  public void notify(DelegateTask delegateTask) {
    System.out.println("========================================");
    System.out.println("TaskListenerBeanWithFieldInjection: used in delegate expression");
    // 注意, 如果你在多个task之间共用了同一个taskListenerBean, 且都使用了field injection,
    // 那么需要将TaskListener变成prototype, 否则会有并发问题
    System.out.println("this: " + this);
    System.out.println("parameter delegateTask: " + delegateTask);
    System.out.println("field text1: " + text1);
    System.out.println("eval field text1: " + text1.getValue(delegateTask));
    System.out.println();
  }

  public void setText1(Expression text1) {
    this.text1 = text1;
  }

}
