package me.chanjar.listenertest;

import org.activiti.engine.delegate.DelegateExecution;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by qianjia on 15/10/15.
 */
public class CandidateUserGetter {

  /**
   * 动态设置 candidateUsers 只能返回 Collection<String>
   *
   * @param execution
   * @return
   */
  public Collection<String> get(DelegateExecution execution) {
    System.out.println("========================================");
    System.out.println("CandidateUserGetter: used in candidateUsers expression");
    System.out.println("parameter execution: " + execution);
    System.out.println("parameter execution.getCurrentActivityId: " + execution.getCurrentActivityId());
    System.out.println("parameter execution.getCurrentActivityName: " + execution.getCurrentActivityName());
    System.out.println();

    return Arrays.asList("userId1", "userId2");

  }

}
