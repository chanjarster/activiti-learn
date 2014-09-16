package me.chanjar.executionlistener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventExecutionListener implements ExecutionListener {

  protected Logger logger = LoggerFactory.getLogger(this.getClass());
      
  @Override
  public void notify(DelegateExecution execution) throws Exception {
    logger.info("Entering EventExecutionListener");
  }

}
