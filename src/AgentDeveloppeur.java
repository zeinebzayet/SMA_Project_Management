import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class AgentDeveloppeur extends Agent {

    protected void setup() {
        System.out.println("Agent Développeur - Prêt.");
        registerService();
        // Comportement cognitif

                // Attendre les messages provenant de l'agent Chef de Projet
                ACLMessage message = blockingReceive();
                if (message != null) {
                    // Si un message est reçu, traiter la tâche
                    String tache = message.getContent();
                    System.out.println("Agent Développeur - Tâche reçue : " + tache);


                } else {
                    // Si aucun message n'est reçu, attendre
                    System.out.println("block");
                }
    }

    private void traiterTache(String tache) {
        // Logique de traitement de la tâche reçue
        System.out.println("Agent Développeur - Traitement de la tâche : " + tache);
        // Mettez en œuvre ici la logique de traitement de la tâche
    }

    private void apprendre() {
        // Logique d'apprentissage
        System.out.println("Agent Développeur - Apprentissage en cours...");
        // Mettez en œuvre ici la logique d'apprentissage basée sur l'expérience
    }
    private void registerService() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("AgentDeveloppeur");
        sd.setName("AgentDeveloppeur");

        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
            doDelete();
        }
    }
}
