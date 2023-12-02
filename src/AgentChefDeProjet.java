import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.proto.AchieveREResponder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class AgentChefDeProjet extends Agent {

    // Liste de tâches associées à chaque agent
    private Map<String, ArrayList<String>> tachesAgents = new HashMap<>();

    protected void setup() {
        System.out.println("Agent Chef de Projet - Prêt.");

        // Associer des tâches aux agents
        tachesAgents.put("AgentDeveloppeur", new ArrayList<>(List.of("TâcheD1", "TâcheD2", "TâcheD3")));
        tachesAgents.put("AgentTesteur", new ArrayList<>(List.of("TâcheT1", "TâcheT2", "TâcheT3")));



        // Planifier et distribuer les tâches aux développeurs
        rechercherEtDistribuerTaches("AgentDeveloppeur");
        rechercherEtDistribuerTaches("AgentTesteur");

            }



    private void rechercherEtDistribuerTaches(String typeAgent) {
        // Rechercher les agents actifs du type spécifié
        DFAgentDescription template = new DFAgentDescription();

        ServiceDescription sd = new ServiceDescription();
        sd.setType(typeAgent);
        template.addServices(sd);

        try {
            System.out.print("rechercherEtDistribuerTaches");

            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfd : result) {
                AID agentAID = dfd.getName();
                // Distribuer une tâche à chaque agent du type spécifié
                distribuerTache(agentAID, typeAgent);
                System.out.println(agentAID);
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void distribuerTache(AID agent, String typeAgent) {
        // Récupérer la prochaine tâche pour l'agent
        System.out.print("distribuerTache");

        ArrayList<String> taches = tachesAgents.get(typeAgent);
        if (taches != null && !taches.isEmpty()) {
            String tache = taches.remove(0);
            planifierEtDistribuerTache(agent, tache);
        }
    }

    private void planifierEtDistribuerTache(AID agent, String tache) {
        // Logique de planification
        System.out.print("hhhhh");

        System.out.println("Agent Chef de Projet - Planifier tâche : " + tache);

        // Créer un message pour demander à l'agent de prendre la tâche
        ACLMessage demande = new ACLMessage(ACLMessage.REQUEST);
        demande.setContent(tache);

        // Ajouter le destinataire (agent spécifique)
        demande.addReceiver(agent);

        // Envoyer la demande à l'agent spécifique
        send(demande);

        // Attendre la réponse de l'agent
        addBehaviour(new AttendreReponsesPlanification(this, demande));
    }

    private void apprendre() {
        // Logique d'apprentissage
        System.out.println("Agent Chef de Projet - Apprentissage en cours...");
        // Mettez en œuvre ici la logique d'apprentissage basée sur l'expérience
    }

    private class AttendreReponsesPlanification extends AchieveREInitiator {
        public AttendreReponsesPlanification(Agent a, ACLMessage demande) {
            super(a, demande);
        }

        protected void handleInform(ACLMessage inform) {
            // Gérer la réponse de l'agent
            System.out.println("Agent Chef de Projet - Réponse reçue : " + inform.getContent());
        }
    }
}