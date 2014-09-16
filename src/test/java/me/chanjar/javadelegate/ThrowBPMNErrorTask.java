package me.chanjar.javadelegate;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrowBPMNErrorTask implements JavaDelegate {

  protected Logger logger = LoggerFactory.getLogger(this.getClass());
  
  @Override
  public void execute(DelegateExecution execution) throws Exception {
    logger.error("throw BPMN error");
    throw new BpmnError("abc2");
  }

}
