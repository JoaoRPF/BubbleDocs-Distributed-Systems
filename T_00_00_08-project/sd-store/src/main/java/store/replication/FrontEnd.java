package store.replication;

import pt.ulisboa.tecnico.sdis.store.ws.uddi.UDDINaming;

import java.util.ArrayList;
import java.util.List;

import javax.xml.registry.JAXRException;

public class FrontEnd {
	
	private List<String> wsNamesPublished = new ArrayList<String>();
	private UDDINaming uddiNaming = null;
	private List<String> urls = new ArrayList<String>();
		

	public FrontEnd(){}
	
	public FrontEnd(List<String> wsNames, UDDINaming uddiNaming){
		this.wsNamesPublished = wsNames;
		this.uddiNaming = uddiNaming;
		this.connectToReplicas();
	}
	
	private void connectToReplicas(){
			try {
				for(String url : this.uddiNaming.list("SD-STORE")){
					if(!url.contains("8090")){
						urls.add(url);
						System.out.println("CONECTAR A REPLICA SD STORE NO URL "+url);
					}
				}
				this.uddiNaming.lookup("SD-STORE");
			} catch (JAXRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
