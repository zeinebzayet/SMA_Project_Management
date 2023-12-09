import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Main {
    public static void main(String[] args) {
        startJADEPlatform();
    }

    private static void startJADEPlatform() {
        try {
            // Initialisation de la plateforme JADE
            jade.core.Runtime rt = jade.core.Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.GUI, "true"); // Activation de l'interface graphique
            AgentContainer mainContainer = rt.createMainContainer(p);

            // Création des agents cognitifs
            AgentController agentControllerDeveloppeur1 = mainContainer.createNewAgent("Developpeur 1", AgentDeveloppeur.class.getName(), null);
            AgentController agentControllerDeveloppeur2 = mainContainer.createNewAgent("Developpeur 2", AgentDeveloppeur.class.getName(), null);
            AgentController agentControllerDeveloppeur3 = mainContainer.createNewAgent("Developpeur 3", AgentDeveloppeur.class.getName(), null);
            AgentController agentControllerTesteur1 = mainContainer.createNewAgent("Testeur 1", AgentTesteur.class.getName(), null);
            AgentController agentControllerTesteur2 = mainContainer.createNewAgent("Testeur 2", AgentTesteur.class.getName(), null);
            AgentController agentControllerChef = mainContainer.createNewAgent("AgentChefDeProjet", AgentChefDeProjet.class.getName(), null);

            // Démarrage des agents
            agentControllerDeveloppeur1.start();
            Thread.sleep(1000); // Add a delay (1 second) after starting each agent
            agentControllerDeveloppeur2.start();
            Thread.sleep(1000);
            agentControllerDeveloppeur3.start();
            Thread.sleep(1000);
            agentControllerTesteur1.start();
            Thread.sleep(1000);
            agentControllerTesteur2.start();
            Thread.sleep(1000);

            // Start the ChefDeProjet agent
            agentControllerChef.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
