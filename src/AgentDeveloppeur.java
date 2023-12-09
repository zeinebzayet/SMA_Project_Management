import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.awt.*;

public class AgentDeveloppeur extends Agent {
    private MessageDisplay messageDisplay;

    private boolean occupe;

    public void setOccupe(boolean occupe) {
        this.occupe = occupe;
    }
    ImageIcon icon = new ImageIcon(new ImageIcon("./images/devp.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));


    protected void setup() {
        SwingUtilities.invokeLater(() -> {
            messageDisplay = new MessageDisplay();
            messageDisplay.setTitle(getLocalName());
            messageDisplay.setVisible(true);

            messageDisplay.appendMessage("Je suis le "+ getLocalName()+" - Prêt.", icon);
            registerService();
            this.occupe=false;

            addBehaviour(new ReceiveTaskBehaviour());
        });

        //System.out.println("Je suis le "+ getLocalName()+" - Prêt.");

    }

    private class ReceiveTaskBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                String request = message.getContent();
                if (request.contains("Est_tu_occupes")) {
                    if(message.getSender().getLocalName().equals("AgentChefDeProjet"))
                    {
                        icon = new ImageIcon(new ImageIcon("./images/chef.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

                    }
                    messageDisplay.appendMessage(message.getSender().getLocalName()+ ": "+request, icon);

                    //System.out.println(message.getSender().getLocalName()+ ": "+request);
                    if (occupe==false) {
                        ACLMessage response = new ACLMessage(ACLMessage.REQUEST);
                        response.setContent("non");
                        response.addReceiver(message.getSender());
                        send(response);

                    } else if(occupe==true) {
                        ACLMessage response1 = new ACLMessage(ACLMessage.REQUEST);
                        response1.setContent("oui");
                        response1.addReceiver(message.getSender());
                        send(response1);
                    }
                }
                else {
                    if (message != null) {
                        String tache = message.getContent();
                        icon = new ImageIcon(new ImageIcon("./images/devp.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

                        messageDisplay.appendMessage(getLocalName()+" tache et durée reçues: "+tache, icon);

                        //System.out.println(getLocalName()+" tache et durée reçues: "+tache);
                        // Si un message est reçu, traiter la tâche
                        String[] parts = tache.split(" ");
                        if (parts.length == 2) {
                            String receivedTache = parts[0];
                            try {
                                int receivedDuree = Integer.parseInt(parts[1]); // Assuming duration is an integer
                                // Now you have extracted values: receivedTache and receivedDuree
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

    private void traiterTache(String receivedTache, int receivedDuree) {
        setOccupe(true);
        int tempsTraitement = receivedDuree;

        try {
            messageDisplay.appendMessage(this.getLocalName() + " - Apprentissage en cours...", icon);

            //System.out.println(this.getLocalName() + " - Apprentissage en cours...");
            // Loop to periodically check for messages during task execution
            for (int i = 0; i < tempsTraitement; i++) {
                ACLMessage message = receive();
                if (message != null && message.getContent().contains("Est_tu_occupes")) {
                    if(message.getSender().getLocalName().equals("AgentChefDeProjet"))
                    {
                        icon = new ImageIcon(new ImageIcon("./images/chef.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

                    }
                    messageDisplay.appendMessage(message.getSender().getLocalName() + ": " + message.getContent(), icon);

                    //System.out.println(message.getSender().getLocalName() + ": " + message.getContent());
                    // Respond with "oui" if a relevant message is received
                    ACLMessage response = new ACLMessage(ACLMessage.REQUEST);
                    response.setContent("oui");
                    response.addReceiver(message.getSender());
                    send(response);
                }

                // Sleep for 1 second before checking for messages again
                Thread.sleep(1000);
            }
            icon = new ImageIcon(new ImageIcon("./images/devp.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

            // Simulate the completion of the task
            messageDisplay.appendMessage(this.getLocalName() + " - Tâche terminée.", icon);

            //System.out.println(this.getLocalName() + " - Tâche terminée.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setOccupe(false);

        // Notify the ChefDeProjet agent about the completion of the task
        ACLMessage response = new ACLMessage(ACLMessage.REQUEST);
        response.setContent(getLocalName()+ ": J'ai terminé la tâche planifiée");
        response.addReceiver(new AID("AgentChefDeProjet", AID.ISLOCALNAME));
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