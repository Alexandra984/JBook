import controller.AuthenticationController;
import gui.LoginFrame;
import gui.RegisterFrame;

public class Main {
    public static void main(String[] args) {
        AuthenticationController authenticationController = new AuthenticationController();
        LoginFrame loginFrame = new LoginFrame(authenticationController);
    }
}
