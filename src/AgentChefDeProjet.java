import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.proto.AchieveREInitiator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
public class AgentChefDeProjet extends Agent {
    private MessageDisplay messageDisplay;
    ImageIcon icon = new ImageIcon(new ImageIcon("./images/chef.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

    public Map<String, Map<String, Integer>> getTachesAgents() {
        return tachesAgents;
    }

    public void setTachesAgents(Map<String, Map<String, Integer>> tachesAgents) {
        this.tachesAgents = tachesAgents;
    }

    private Map<String, Map<String, Integer>> tachesAgents = new HashMap<>();

    private boolean occupe;
    private String tache;

    private String typeAgent;
    private int duree;
    protected void setup() {

        SwingUtilities.invokeLater(() -> {
            messageDisplay = new MessageDisplay();
            messageDisplay.setTitle("Agent chef de projet");
            messageDisplay.setVisible(true);
            messageDisplay.appendMessage("Agent Chef de Projet - Prêt. ", icon);

            Map<String, Integer> developpeurTasks = new HashMap<>();
            developpeurTasks.put("TâcheD1", 120);  // Duration in minutes
            developpeurTasks.put("TâcheD2", 60);
            developpeurTasks.put("TâcheD3", 20);
            developpeurTasks.put("TâcheD4", 30);
            developpeurTasks.put("TâcheD5", 30);
            Map<String, Integer> testeurTasks = new HashMap<>();
            testeurTasks.put("TâcheT1", 60);
            testeurTasks.put("TâcheT2", 30);
            testeurTasks.put("TâcheT3", 40);
            tachesAgents.put("AgentDeveloppeur", developpeurTasks);
            tachesAgents.put("AgentTesteur", testeurTasks);
            this.setOccupe(false);

            // Schedule the cyclic behavior
            addBehaviour(new CyclicTaskBehavior());
        });
        //System.out.println("Agent Chef de Projet - Prêt. ");


    }
    public String getTypeAgent() {
        return typeAgent;
    }

    public void setTypeAgent(String typeAgent) {
        this.typeAgent = typeAgent;
    }

    private class CyclicTaskBehavior extends CyclicBehaviour {

        public void action() {
            rechercherAgents("AgentDeveloppeur");
            rechercherAgents("AgentTesteur");

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
            DFAgentDescription[] result = DFService.search(this, template);
            for (int i = 0; i < result.length; i++) {
                DFAgentDescription dfd = result[i];
                AID agentAID = dfd.getName();
                if (isOccupe()) {
                    if (i + 1 < result.length && this.getTypeAgent().equals(typeAgent) ){
                        // Si l'agent actuel est occupé, passe à l'agent suivant s'il existe
                        DFAgentDescription dfdSuivant = result[i + 1];
                        AID agentAIDSuivant = dfdSuivant.getName();
                        testerEtaffecterTache(agentAIDSuivant, this.getTache(),this.getDuree());
                        i++; // Avance d'un agent dans la liste
                    }
                }
                extraireTache(agentAID, typeAgent);
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void extraireTache(AID agent, String typeAgent) {
        // Récupérer la prochaine tâche et sa durée pour l'agent
        Map<String, Integer> tasksWithDuration = tachesAgents.get(typeAgent);
        if (tasksWithDuration != null && !tasksWithDuration.isEmpty()) {
            // Find the first task and its duration
            Map.Entry<String, Integer> taskEntry = tasksWithDuration.entrySet().iterator().next();
            String tache = taskEntry.getKey();
            int duree = taskEntry.getValue();
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
    private String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    private void testerEtaffecterTache(AID agent, String tache, int duree) {
        // Logique de planification
        System.out.println("**********************************");
        ACLMessage occupe = new ACLMessage(ACLMessage.REQUEST);
        occupe.setContent(agent.getLocalName()+" Est_tu_occupes pour une tache "+tache +" de durée " +duree+ "s");
        occupe.addReceiver(agent);
        send(occupe);
        ACLMessage response = blockingReceive();

        //messageDisplay.appendMessage(response.getSender().getLocalName() +"- " + response.getContent(),true, icon);
        String message=response.getContent();
        if(response.getContent().equals("oui")){
            message = "<html><b><font color='" + getHexColor(Color.BLACK) + "'>" +
                response.getSender().getLocalName()+"</font></b>: <font color='" + getHexColor(Color.red) + "'>" +
                response.getContent() + "</font></html>";}
        else if(response.getContent().equals("non")){
            message = "<html><b><font color='" + getHexColor(Color.BLACK) + "'>" +
                    response.getSender().getLocalName()+"</font></b>: <font color='" + getHexColor(Color.green) + "'>" +
                    response.getContent() + "</font></html>";
            }
        messageDisplay.appendMessage(message,icon);

        //System.out.println(response.getSender().getLocalName() +"- " + response.getContent());
        if (response != null) {
            String request = response.getContent();
            if (request.equals("non")) {
                this.setOccupe(false);
                messageDisplay.appendMessage(getLocalName()+" - Planifier la tâche : " +tache+" de durée "+duree+" s pour l'agent: "+response.getSender().getLocalName(), icon);

                //System.out.println(getLocalName()+" - Planifier la tâche : " +tache+" de durée "+duree+" s pour l'agent: "+response.getSender().getLocalName());
                ACLMessage demande = new ACLMessage(ACLMessage.REQUEST);
                demande.setContent(tache+" "+duree);
                demande.addReceiver(agent);
                // Envoyer la demande à l'agent spécifique
                send(demande);
                // Attendre la réponse de l'agent
                addBehaviour(new AttendreReponsesPlanification(this, demande));
            } else if (request.equals("oui")){
                this.setOccupe(true);
                this.setTache(tache);
                this.setDuree(duree);
                if(tache.contains("D")) {
                    this.setTypeAgent("AgentDeveloppeur");
                }
                else{
                    this.setTypeAgent("AgentTesteur");
                }

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