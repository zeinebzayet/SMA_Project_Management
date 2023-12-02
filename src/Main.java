import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Main {

    public static void main(String[] args) {
        startJADEPlatform();
        // Ajoutez le code pour créer et lancer vos agents ici
    }

    private static void startJADEPlatform() {
        try {
            // Initialisation de la plateforme JADE
            jade.core.Runtime rt = jade.core.Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.GUI, "true"); // Activation de l'interface graphique
            AgentContainer mainContainer = rt.createMainContainer(p);

            // Vous pouvez également configurer d'autres paramètres de profil au besoin
            // p.setParameter(...);

            // Création des agents cognitifs
            AgentController agentControllerDeveloppeur1 = mainContainer.createNewAgent("Developpeur 1", AgentDeveloppeur.class.getName(), null);
            AgentController agentControllerDeveloppeur2 = mainContainer.createNewAgent("Developpeur 2", AgentDeveloppeur.class.getName(), null);
            AgentController agentControllerDeveloppeur3 = mainContainer.createNewAgent("Developpeur 3", AgentDeveloppeur.class.getName(), null);


            AgentController agentControllerTesteur1 = mainContainer.createNewAgent("Testeur 1", AgentTesteur.class.getName(), null);
            AgentController agentControllerTesteur2 = mainContainer.createNewAgent("Testeur 2", AgentTesteur.class.getName(), null);

            AgentController agentControllerChef = mainContainer.createNewAgent("AgentChefDeProjet", AgentChefDeProjet.class.getName(), null);


            // Démarrage des agents

            agentControllerDeveloppeur1.start();
            agentControllerDeveloppeur2.start();
            agentControllerDeveloppeur3.start();
            agentControllerTesteur1.start();
            agentControllerTesteur2.start();

            agentControllerChef.start();




            System.out.println("Plateforme JADE lancée avec l'interface graphique.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}