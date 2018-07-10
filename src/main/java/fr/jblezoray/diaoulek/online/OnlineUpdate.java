package fr.jblezoray.diaoulek.online;

import java.io.IOException;

import org.apache.http.client.fluent.Request;

import fr.jblezoray.diaoulek.Config;

public class OnlineUpdate {

  public void go() throws IOException {
    
    String uri = Config.URL_UPDATE + "/tot-file-tank.txt";
    String totFileTank = Request.Get(uri).execute().returnContent().asString();
    
    System.out.println(totFileTank);
    
  }
  
}
