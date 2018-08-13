package controlador;


import java.beans.SimpleBeanInfo;
import java.io.IOException;

import ambiente.Aerodromo;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import ontologia.Emprego;


public class ControladorAgente extends Agent {
	
	
	protected void setup() {
//	addBehaviour(new AerodromoEmpregoComportamento());
		//registrarServico("Apoio_Pouso");
		String serviceName = "Apoio_de_Pouso-";
	  	
	  	// Read the name of the service to register as an argument
	  	Object[] args = getArguments();
	  	if (args != null && args.length > 0) {
	  		serviceName = (String) args[0];
	  	}
		DFAgentDescription dfa = new DFAgentDescription();
		ServiceDescription svd = new ServiceDescription();
		try {
		dfa.setName(getAID());
		svd.setName(serviceName);
		svd.setType("Pouso_Comum");
		svd.addOntologies("InstrucoesVoo");
		dfa.addServices(svd);
		dfa.addOntologies(Emprego.REGRAS);
	
		DFService.register(this, dfa);

		}catch (FIPAException e) {
			e.printStackTrace();
		}
		
		RecebeMensagens();
		gerirVoo();
		
	}
	
	protected void RecebeMensagens(){
			
		addBehaviour(new OneShotBehaviour() {	
		@Override
		public void action() {
		  	try {
				Ontology o = myAgent.getContentManager().lookupOntology(Emprego.REGRAS);	  
			} catch (Exception e) {
		     	e.printStackTrace();
		    }}  } );	
		
		addBehaviour(new CyclicBehaviour(this){
			@Override
			public void action() {
				// TODO Auto-generated method stub
				ACLMessage msg = myAgent.receive();
				if(msg != null) {
					String content = msg.getContent();
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent("Aqui � o controlador: " +getLocalName()+ " Recomenda��es em ontologia!!"
							+ " Obrigado por auxiliar no meu servico");
					System.out.println("Comtrolador " +getLocalName()+" vejo que o"
							+ " "+  msg.getSender().getLocalName()+" solicitou recomenda��es para navega��o! "  );
					System.out.println("Comtrolador " +getLocalName()+
							"tudo bem? " +msg.getSender().getLocalName()+ " estou passando as regras para ele..");
					myAgent.send(reply);
				}else 	
					block();
			}//fim do action
			}); //fim do addBehaviours
		
		

	}
	
	


	protected void gerirVoo(){
		addBehaviour(new CyclicBehaviour(this){
			@Override
			public void action() {
				// TODO Auto-generated method stub
				ACLMessage msg = myAgent.receive();
				if(msg != null) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent("Realizar_Pouso");
					myAgent.send(reply);
				}else 	
					block();
			}
			});
	}}



	


/**
class AerodromoEmpregoComportamento extends SequentialBehaviour {		
		
	public void onStart(){

				//Obtem informacoes do ambiente
				try {
					Aerodromo ardm = new Aerodromo();
					ardm.set_dim_aerodromo(3000l);
					ardm.set_diametro_aerovia(1200l);
					ardm.set_lim_max_pista(1200l); // 
					ardm.set_lim_area_escape(600l); // 
					ardm.set_lim_max_fl(60l); //representando subleitos com K entre 60 MN/m3 (ANAC)
				
					Ontology o = myAgent.getContentManager().lookupOntology(Emprego.REGRAS);	
			
					ACLMessage queryMsg = new ACLMessage(ACLMessage.REQUEST);
					//receptor
					queryMsg.setOntology(Emprego.REGRAS);		
					System.out.println("ENVIANDO:"+ ardm.get_diametro_aerovia());
				}catch (Exception e) {
    		e.printStackTrace();
    	}
	}
}

**/