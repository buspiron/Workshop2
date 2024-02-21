package pl.coderslab;

public class TaskManager {
    public static void main(String[] args) {
        UserDao userDao2 = new UserDao();
        User[] users = userDao2.findAll();
        for(User user : users){
            System.out.println(user);
        }
    }
}