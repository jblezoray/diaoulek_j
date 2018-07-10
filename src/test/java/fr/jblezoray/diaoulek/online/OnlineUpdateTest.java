package fr.jblezoray.diaoulek.online;

import java.io.IOException;

import junit.framework.TestCase;


public class OnlineUpdateTest extends TestCase {
  
  
  public void test() throws IOException {
    new OnlineUpdate().go();
  }
  
}
