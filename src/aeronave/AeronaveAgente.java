package aeronave;


import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CyclicBarrier;

import ambiente.Aerodromo;
import ambiente.SistemaAeroportuario;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Service;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.SubscriptionInitiator;
import jade.tools.gui.ACLAIDList.AIDListCellRenderer;
import ontologia.Emprego;

public class AeronaveAgente extends Agent {
	 public static class AeronaveAgenteData {
		private String controlador;

	
	public String getControlador() {
		// TODO Auto-generated method stub
		return controlador;
	}

		public void setControlador(String controlador) {
			this.controlador = controlador;
		}
	}



	AeronaveAgenteData data = new AeronaveAgenteData();


	public AeronaveAgente(){
			MessageTemplate.MatchOntology(Emprego.AERODROMO);
}
	
	  protected void setup() {
		 
				 addBehaviour(new AeronaveComportamento(this, 3000));
		  this.data.setControlador(buscaServico("Pouso_Comum"));
		  System.out.println("Aeronave: "+getLocalName()+" Inicializado");
		  System.out.println("Aeronave: "+getLocalName()+" Ol� Controlador  "+data.getControlador() +" Solicito as regras de navega��o "
		  		+ " para controle de aproxima��o e pouso");
		  SolicitarRegras(data.getControlador());
		  
	  }
	  	  
	  		
protected String buscaServico(String tipoServico) {
	try {
  		DFAgentDescription template = new DFAgentDescription();
  		ServiceDescription templateSd = new ServiceDescription();
  		templateSd.setType("Pouso_Comum");
  		template.addServices(templateSd);
  		
  		SearchConstraints sc = new SearchConstraints();
  		sc.setMaxResults(new Long(10));
  		
  		DFAgentDescription[] results = DFService.search(this, template, sc);
  		if (results.length > 0) {
  			System.out.println("Aeronave: "+getLocalName()+" Encontrou os seguintes tipos de servi�os:");
  			for (int i = 0; i < results.length; ++i) {
  				DFAgentDescription dfd = results[i];
  				
  				AID provider = dfd.getName();
  				
  				Iterator it = dfd.getAllServices();
  				while (it.hasNext()) {
  					ServiceDescription sd = (ServiceDescription) it.next();
  					if (sd.getType().equals("Pouso_Comum")) {
  				//		System.out.println("--Servi�o \""+sd.getType());
  				//		System.out.println("--Controlador:"+ provider.getLocalName());
  						return  provider.getLocalName();
  				}
  			}
  		}}
  		else {
  			System.out.println("Aeronave "+getLocalName()+"n�o tem agentes disponiveis"
  					+ " para esse servico, MENDEI! NENDEI!!");
  		}
  	}
  	catch (FIPAException fe) {
  		fe.printStackTrace();
  	}

	return " ";
	}
	


public void SolicitarRegras(String nomeAgente){
	addBehaviour(new OneShotBehaviour(this) {			
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		DFAgentDescription sfd = new DFAgentDescription();
		ServiceDescription svd = new ServiceDescription();
		
		@Override
		public void action() {
			msg.addReceiver(new AID(nomeAgente, AID.ISLOCALNAME));
			msg.setContent(getLocalName());
			myAgent.send(msg);
		}});
	
	addBehaviour(new CyclicBehaviour(this){
		@Override
		public void action() {
		ACLMessage msg = myAgent.receive();	
			if(msg != null) {
			Aerodromo ardm = new Aerodromo();			
		String content = "-->"+msg.getContent()+" aqui � a aeronave solcitando recomenda��es para pouso";
		System.out.println("-->"+msg.getSender().getLocalName()+" : "+content);
			
		}else
			block();}
	});	
}


}

			
