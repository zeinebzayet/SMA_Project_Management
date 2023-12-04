import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.proto.AchieveREInitiator;

import java.util.*;

public class AgentChefDeProjet extends Agent {

    private Map<String, Map<String, Integer>> tachesAgents = new HashMap<>();
    private boolean occupe = false;
    private String tache = "";

    private int duree=0;
    protected void setup() {
        System.out.println("Agent Chef de Projet ");

        Map<String, Integer> developpeurTasks = new HashMap<>();
        developpeurTasks.put("TâcheD1", 10);  // Duration in minutes
        developpeurTasks.put("TâcheD2", 15);
        developpeurTasks.put("TâcheD3", 20);

        Map<String, Integer> testeurTasks = new HashMap<>();
        testeurTasks.put("TâcheT1", 12);
        testeurTasks.put("TâcheT2", 18);
        testeurTasks.put("TâcheT3", 25);

        tachesAgents.put("AgentDeveloppeur", developpeurTasks);
        tachesAgents.put("AgentTesteur", testeurTasks);

        // Schedule the cyclic behavior
        addBehaviour(new CyclicTaskBehavior());
    }

    private class CyclicTaskBehavior extends CyclicBehaviour {

        public void action() {
            // Perform your cyclic task here
            System.out.println("Agent Chef de Projet - Performing cyclic task");

            // Call the method to distribute tasks to agents
            rechercherAgents("AgentDeveloppeur");
            rechercherAgents("AgentTesteur");

            // You may add a delay or sleep if needed
            try {
                Thread.sleep(5000);  // Sleep for 5 seconds as an example
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void rechercherAgents(String typeAgent) {
        // Rechercher les agents actifs du type spécifié
        DFAgentDescription template = new DFAgentDescription();

        ServiceDescription sd = new ServiceDescription();
        sd.setType(typeAgent);
        template.addServices(sd);
        try {
            System.out.print("rechercher Et Distribuer Taches");

            DFAgentDescription[] result = DFService.search(this, template);
            for (int i = 0; i < result.length; i++) {
                DFAgentDescription dfd = result[i];
                AID agentAID = dfd.getName();
                System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnn" + isOccupe());

                if (isOccupe()) {
                    if (i + 1 < result.length) {
                        // Si l'agent actuel est occupé, passe à l'agent suivant s'il existe
                        DFAgentDescription dfdSuivant = result[i + 1];
                        AID agentAIDSuivant = dfdSuivant.getName();
                        testerEtaffecterTache(agentAIDSuivant, this.getTache(),this.getDuree());
                        i++; // Avance d'un agent dans la liste
                    }
                }
                extraireTache(agentAID, typeAgent);

                System.out.println(agentAID);
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void extraireTache(AID agent, String typeAgent) {
        // Récupérer la prochaine tâche et sa durée pour l'agent
        System.out.print("Extraire Tache");

        Map<String, Integer> tasksWithDuration = tachesAgents.get(typeAgent);
        System.out.println(tasksWithDuration);
        if (tasksWithDuration != null && !tasksWithDuration.isEmpty()) {
            System.out.println("okkkkkkkkkkkkk");
            // Find the first task and its duration
            Map.Entry<String, Integer> taskEntry = tasksWithDuration.entrySet().iterator().next();
            String tache = taskEntry.getKey();
            int duree = taskEntry.getValue();
            System.out.println(tache);
            System.out.println(duree);


            // Remove the task from the map
            tasksWithDuration.remove(tache);
            testerEtaffecterTache(agent, tache,duree);


        }
    }


    public void setDuree(int duree) {
        this.duree = duree;
    }

    public int getDuree() {
        return duree;
    }

    private void testerEtaffecterTache(AID agent, String tache, int duree) {
        System.out.println(tache+duree);
        // Logique de planification
        ACLMessage occupe = new ACLMessage(ACLMessage.REQUEST);
        occupe.setContent("Es_tu_occupe? pour une tache de  "+tache +"de durée  " +duree+ "s");
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
                demande.setContent(tache+"  "+duree);

                // Ajouter le destinataire (agent spécifique)
                demande.addReceiver(agent);

                // Envoyer la demande à l'agent spécifique
                send(demande);

                // Attendre la réponse de l'agent
                addBehaviour(new AttendreReponsesPlanification(this, demande));
                addBehaviour(new AttendreReponsesPlanification(this, demande));
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
