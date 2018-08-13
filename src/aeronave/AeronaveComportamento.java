package aeronave;


import ambiente.Aerodromo;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.introspection.AddedBehaviour;
import jade.lang.acl.ACLMessage;

public class AeronaveComportamento extends CyclicBehaviour{
	long dalay;
	long tempoInical = System.currentTimeMillis();
	long posicaoInicial = 1000;
	long coeficienteReducao = 3;
	long potencia = 150 ;
	long VELOCIDADE_MEDIA = 150;
	long AUTONOMIA = 100000;
	Agent agt;

	/**public static long tempInicial;
	public static long tempFinal;
	public static void comecar() {
		tempInicial = System.currentTimeMillis();
		
	}**/
	
	public AeronaveComportamento(Agent a, long dalay){
	super(a); 
	this.agt = a;
	this.dalay = dalay;
	}
	//realiza o calculo da velocidade
	public long velocidade(long potencia){
		long tempofinal = System.currentTimeMillis();
		if((((tempofinal - tempoInical)/1000)*potencia) >VELOCIDADE_MEDIA ){
			return VELOCIDADE_MEDIA;
		}else {
		return ((tempofinal - tempoInical)/1000)*potencia;
		}
	}
	

	//Deslocamento em km por hora
	public long deslocamento(long velocidade) {
		long tempofinal = System.currentTimeMillis();
		return (long) (((((tempofinal - tempoInical)/1000)*velocidade)- posicaoInicial)/3.6);
	}
	
	// calculo da autonomia
	public long autonomia(long autonomia) {
		return	autonomia = autonomia - velocidade(potencia);
	}	
	
	//regula a velocidade para pouso
	public void regularVelocidade(){
		long aux = 1;
		String estado = "null";
		//aeronave em voo
		if(velocidade(potencia) >= VELOCIDADE_MEDIA) {
			potencia = 100;
			estado = "Aeronave: "+myAgent.getLocalName()+ " [ Velocidade "+velocidade(potencia)+" ] " + " [ Em voo ] Posi��o:"
					+ " [ "+deslocamento(velocidade(potencia))+"] Autonomia: "+ autonomia(AUTONOMIA);
			confirmarAproximacao(estado);
			} 
		//aeronave reduzindo e se aproximando da cabeceira da pista		
		if(velocidade(potencia) < VELOCIDADE_MEDIA ){
		long tempofinal = System.currentTimeMillis() - velocidade(potencia);
		aux = (long)(this.coeficienteReducao/10 *(tempofinal - tempoInical)/1000) - velocidade(potencia);
		velocidade(aux);
			System.out.println("Aeronave: "+myAgent.getLocalName()+ " [ Velocidade "+aux+" ] " + " [ Aproximando da Cabeceira ] "
					+ "Posi��o: [ "+deslocamento(velocidade(potencia))+"] Autonomia: ["+ autonomia(AUTONOMIA)+"]");
		} 
			
		//aeronave em pouso e reduzindo a velocidade
		if(velocidade(potencia) < 100 ){
		long tempofinal = System.currentTimeMillis() - velocidade(potencia);
		aux = (long)(this.coeficienteReducao/10 *(tempofinal - tempoInical)/1000) - velocidade(potencia);
		velocidade(aux);
			System.out.println("Aeronave: "+myAgent.getLocalName()+ " [ Velocidade "+aux+" ] " + " [ Reduzindo a velocidade pos pouso ]"
					+ " Posi��o [ " +deslocamento(velocidade(potencia))+"] Autonomia: ["+autonomia(AUTONOMIA)+"]");
		}
		//aeronave iniciando o procedimento de taxiamento
		if(velocidade(potencia) < 10 ){
		long tempofinal = System.currentTimeMillis() - velocidade(potencia);
		aux = (long)(this.coeficienteReducao/10 *(tempofinal - tempoInical)/1000) - velocidade(potencia);
		velocidade(aux);
			System.out.println("Aeronave: "+myAgent.getLocalName()+ " [ Velocidade "+aux+" ] " + " "
					+ "[ Reduzindo a velocidade no taxiamento ] Posi��o [ "+deslocamento(velocidade(potencia))+"] Autonomia ["+ autonomia(AUTONOMIA)+"]");
		}
		//aeronave iniciando o procedimento de taxiamento
		if(velocidade(potencia) < 1 ){
		long tempofinal = System.currentTimeMillis() - velocidade(potencia);
		aux = (long)(this.coeficienteReducao/10 *(tempofinal - tempoInical)/1000) - velocidade(potencia);
		velocidade(aux);
		System.out.println("Aeronave: "+myAgent.getLocalName()+ " [ Velocidade "+aux+" ] " + " [ Estacionada] Posi��o: [ "+deslocamento(velocidade(potencia))+"]");
		}		
	}

	//solicita ao controlador pouso da aeronave
	public String SolicitarPouso(String nomeAgente, String resp){
		SequentialBehaviour sqb = new SequentialBehaviour();
		
		System.out.println("enviar"+resp+"cotr"+nomeAgente);
		sqb.addSubBehaviour(new OneShotBehaviour(agt) {			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			DFAgentDescription sfd = new DFAgentDescription();
			ServiceDescription svd = new ServiceDescription();
			
			
			@Override
			public void action() {
				System.out.println("Aeronave: "+myAgent.getLocalName()+ "envidando situa��o de voo ao "+ nomeAgente);
				msg.addReceiver(new AID(nomeAgente, AID.ISLOCALNAME));
			//	msg.setContent(resp);
				myAgent.send(msg);
				
				}});
		sqb.addSubBehaviour(new CyclicBehaviour(agt){
			@Override
			public void action() {
			ACLMessage msg = myAgent.receive();	
				if(msg != null) {	
			//	resp =  msg.getContent();
				}}});
			return resp;
			}

	//realica a confirmacao da posicao de aproximacao
	public void confirmarAproximacao(String str){
		Aerodromo ardm = new Aerodromo();
		AeronaveAgente agtAeronave = new AeronaveAgente();
		SequentialBehaviour sqb = new SequentialBehaviour();
		
					if((deslocamento(velocidade(potencia))) >= ardm.get_diametro_aerovia() && (deslocamento(velocidade(potencia))) < ardm.get_diametro_aerovia()+100 ) {
						System.out.println("Aeronave : "+ myAgent.getLocalName()+" se aproximando do aerodromo");
						System.out.println(str);
					
						SolicitarPouso("Controlador", "Status"+ str);
												
						
						if( str.equalsIgnoreCase("Realizar_Pouso") == true){
							System.out.println("Aeronave : "+ myAgent.getLocalName()+" iniciando processo de pouso");
							velocidade(-99);
						}	
						if( str.equalsIgnoreCase("Entre_na_fila") == true){
							System.out.println("Aeronave : "+ myAgent.getLocalName()+" entrando na fila");
						}
						if( str.equalsIgnoreCase("Reduza_a_velocidade") == true){
							System.out.println("Aeronave : "+ myAgent.getLocalName()+" entrando na fila");
							velocidade(VELOCIDADE_MEDIA-20);
						}
					}}
	

	public void action() {
			block(dalay);
			regularVelocidade();
			
		}
		

	
	
}




	/**			}
			if (velocidade == 0)
			done();
			if(nivelVoo == 0)
			System.out.println("Aeronave"+myAgent.getName()+"no solo");
			if(autonomia == 0)
			System.out.println("Aeronave "+myAgent.getName()+"com autonomia exgotada");
		}
		
		long verificarPosicaoNoAmbiente(long deslocamento, long nivelVoo, long dimRegras){
		long posicao = 33;
		return posicao;
		}


	**/
		
	
	

