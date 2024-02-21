package pl.coderslab;

import org.mindrot.jbcrypt.BCrypt;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Arrays;

public class UserDao {
    private static final String INSERT_USER = "INSERT INTO users(userName, email, password) VALUES (?,?,?);";

    private static final String READ_USER = "SELECT * FROM users WHERE id = (?)";
    private static final String READ_ALL_USERS = "SELECT * FROM users";
    private static final String UPDATE_USER = "UPDATE users SET userName = (?), email = (?), password = (?) where id = (?);";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = (?);";


    private String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.hashpw(password, BCrypt.gensalt()));
    }
    public User create(User user){
        try(Connection connection = DbUtil.connection()){
            PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                System.out.println("User added. ID = " + id);
                user.setId(id);
            }
            return user;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public User read(int userId){
        User user = new User();
        try(Connection connection = DbUtil.connection()){
            PreparedStatement statement = connection.prepareStatement(READ_USER);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("userName"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
            }
            return user;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void update(User user){
        try(Connection connection = DbUtil.connection()){
            PreparedStatement statement = connection.prepareStatement(UPDATE_USER);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public User[] findAll() {
        User[] users = new User[0];
        try(Connection connection = DbUtil.connection()){
            PreparedStatement statement = connection.prepareStatement(READ_ALL_USERS);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("userName"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                users = addToArray(user, users);
            }
            return users;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private User[] addToArray(User user, User[] users){
        User[] tmpArray = Arrays.copyOf(users, users.length + 1);
        tmpArray[users.length] = user;
        return tmpArray;
    }

    public void delete(int userId){
        try(Connection connection = DbUtil.connection()){
            PreparedStatement statement = connection.prepareStatement(DELETE_USER);
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
