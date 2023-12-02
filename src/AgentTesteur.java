import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class AgentTesteur extends Agent {

    protected void setup() {
        System.out.println("Agent testeur - Prêt.");
        registerService();

        ACLMessage message = blockingReceive();
        if (message != null) {
            // Si un message est reçu, traiter la tâche
            String tache = message.getContent();
            System.out.println("Agent testeur - Tâche reçue : " + tache);

        } else {
            // Si aucun message n'est reçu, attendre
            System.out.println("block");
        }



    }

    private void traiterTache(String tache) {
        // Logique de traitement de la tâche reçue
        System.out.println("Agent testeur - Traitement de la tâche : " + tache);
        // Mettez en œuvre ici la logique de traitement de la tâche
    }

    private void apprendre() {
        // Logique d'apprentissage
        System.out.println("Agent testeur - Apprentissage en cours...");
        // Mettez en œuvre ici la logique d'apprentissage basée sur l'expérience
    }
    private void registerService() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("AgentTesteur");
        sd.setName("AgentTesteur");

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