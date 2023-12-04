import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class AgentDeveloppeur extends Agent {
    private boolean occupe = false;

    protected void setup() {
        System.out.println("Agent Développeur - Prêt." + getLocalName());
        registerService();

        // Comportement cognitif
        addBehaviour(new ReceiveTaskBehaviour());
    }

    private class ReceiveTaskBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                // Si un message est reçu, traiter la tâche
                String request = message.getContent();
                if (request.equals("Es_tu_occupe?")) {
                    if (occupe==false) {
                        ACLMessage response = new ACLMessage(ACLMessage.REQUEST);
                        response.setContent("non");
                        response.addReceiver(message.getSender());
                        send(response);

                    } else {
                        ACLMessage response1 = new ACLMessage(ACLMessage.REQUEST);
                        response1.setContent("oui");
                        response1.addReceiver(message.getSender());
                        send(response1);
                    }
                }
                else {
                    ACLMessage messageTache = blockingReceive();
                    if (messageTache != null) {
                        // Si un message est reçu, traiter la tâche
                        String tache = messageTache.getContent();
                        System.out.println("Agent Développeur - Tâche reçue : " + tache);
                        traiterTache(tache);
                    }
                }
            } else {
                block();
            }
        }
    }

    private void traiterTache(String tache) {
        occupe = true;
        // Logique de traitement de la tâche reçue
        System.out.println("Agent Développeur - Traitement de la tâche : " + tache);
        // Mettez en œuvre ici la logique de traitement de la tâche
        occupe = false;
    }

    private void apprendre() {
        // Simuler un temps de traitement aléatoire entre 1 et 5 secondes
        int tempsTraitement = (int) (Math.random() * 4000) + 7000; // entre 1 et 5 secondes
        try {
            Thread.sleep(tempsTraitement);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
            doDelete();
        }
    }
}