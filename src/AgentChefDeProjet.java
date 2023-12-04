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

import java.util.*;


public class AgentChefDeProjet extends Agent {
    private Map<String, ArrayList<String>> tachesAgents = new HashMap<>();

    protected void setup() {
        System.out.println("Agent Chef de Projet - Prêt.");
        // Associer des tâches aux agents
        tachesAgents.put("AgentDeveloppeur", new ArrayList<>(List.of("TâcheD1", "TâcheD2", "TâcheD3")));
        tachesAgents.put("AgentTesteur", new ArrayList<>(List.of("TâcheT1", "TâcheT2", "TâcheT3")));

        addBehaviour(new taskBehaviour());
    }

    private class taskBehaviour extends CyclicBehaviour {
        // Liste de tâches associées à chaque agent

        private boolean occupe=false;
        private String tache="";


        @Override
        public void action() {
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
                System.out.print("rechercher Et Distribuer Taches");

                DFAgentDescription[] result = DFService.search(this.getAgent(), template);
                for (int i = 0; i < result.length; i++) {
                    DFAgentDescription dfd = result[i];
                    AID agentAID = dfd.getName();

                    if (isOccupe()) {
                        if (i + 1 < result.length) {
                            // Si l'agent actuel est occupé, passe à l'agent suivant s'il existe
                            DFAgentDescription dfdSuivant = result[i + 1];
                            AID agentAIDSuivant = dfdSuivant.getName();
                            planifierEtDistribuerTache(agentAIDSuivant, this.getTache());
                            i++; // Avance d'un agent dans la liste
                        }
                    }
                    distribuerTache(agentAID, typeAgent);


                    System.out.println(agentAID);
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

        private void distribuerTache(AID agent, String typeAgent) {
            // Récupérer la prochaine tâche pour l'agent
            System.out.print("distribuer Tache");

            ArrayList<String> taches = tachesAgents.get(typeAgent);
            if (taches != null && !taches.isEmpty()) {
                String tache = taches.remove(0);
                planifierEtDistribuerTache(agent, tache);
            }
        }

        private void planifierEtDistribuerTache(AID agent, String tache) {
            // Logique de planification
            ACLMessage occupe = new ACLMessage(ACLMessage.REQUEST);
            occupe.setContent("Es_tu_occupe?");
            occupe.addReceiver(agent);
            send(occupe);
            ACLMessage response = blockingReceive();
            System.out.println("Agent chef :" + response.getContent());

            if (response != null) {
                String request = response.getContent();
                if (request.equals("non")) {
                    this.setOccupe(false);
                    System.out.println("Agent Chef de Projet - Planifier tâche : " + tache);

                    // Créer un message pour demander à l'agent de prendre la tâche
                    ACLMessage demande = new ACLMessage(ACLMessage.REQUEST);
                    demande.setContent(tache);

                    // Ajouter le destinataire (agent spécifique)
                    demande.addReceiver(agent);

                    // Envoyer la demande à l'agent spécifique
                    send(demande);

                    // Attendre la réponse de l'agent
                    addBehaviour(new AttendreReponsesPlanification(this.getAgent(), demande));
                } else {
                    this.setOccupe(true);
                    this.setTache(tache);
                }
            }
        }

        public boolean isOccupe() {
            return occupe;
        }

        public void setOccupe(boolean occupe) {
            this.occupe = occupe;
        }

        public void setTache(String tache) {
            this.tache = tache;
        }

        public String getTache() {
            return tache;
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
}