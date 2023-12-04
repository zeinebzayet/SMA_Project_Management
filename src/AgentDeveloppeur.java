import jade.core.AID;
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
        System.out.println(getLocalName()+" - Prêt.");
        registerService();

        addBehaviour(new ReceiveTaskBehaviour());
    }

    private class ReceiveTaskBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                String request = message.getContent();
                if (request.contains("Est_tu_occupes")) {
                    System.out.println(message.getSender().getLocalName()+ ": "+request);
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
                    if (message != null) {
                        String tache = message.getContent();
                        System.out.println(getLocalName()+" tache et durée reçues: "+tache);
                        // Si un message est reçu, traiter la tâche
                        String[] parts = tache.split(" ");
                        if (parts.length == 2) {
                            String receivedTache = parts[0];
                            try {
                                int receivedDuree = Integer.parseInt(parts[1]); // Assuming duration is an integer

                                // Now you have extracted values: receivedTache and receivedDuree
                                System.out.println("Received Tache: " + receivedTache);
                                System.out.println("Received Duree: " + receivedDuree);
                                traiterTache(receivedTache, receivedDuree);
                            } catch (NumberFormatException e) {
                                // Handle the case where duration is not a valid integer
                                System.err.println("Invalid duration format: " + parts[1]);
                            }
                        } else {
                            // Handle the case where the content is not in the expected format
                            System.err.println("Invalid content format: " + tache);
                        }
                    }
                }
            } else {
                block();
            }
        }
    }

    private void traiterTache(String receivedTache,int receivedDuree) {
        occupe = true;
        // Simuler un temps de traitement aléatoire entre 1 et 5 secondes
        int tempsTraitement = receivedDuree;
        try {
            Thread.sleep(tempsTraitement);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.getLocalName()+" - Apprentissage en cours...");
        // Mettez en œuvre ici la logique d'apprentissage basée sur l'expérience
        occupe = false;
        ACLMessage response = new ACLMessage(ACLMessage.REQUEST);
        response.setContent("non");
        response.addReceiver(new AID("AgentChefDeProjet",AID.ISLOCALNAME));
        send(response);
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