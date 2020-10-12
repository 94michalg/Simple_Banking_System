package banking;

import javax.sql.DataSource;
import java.sql.*;

public class Database implements AccountDAO {

    private final DataSource dataSource;

    public Database (DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init() {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            statement.executeUpdate(DROP_TABLE);
            statement.executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            System.out.println("Error while creating table " + e.getMessage());
        }
    }

    public void addNewAccount(Account newAccount) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, newAccount.getCardNumber());
            statement.setString(2, String.valueOf(newAccount.getPin()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account findByNumber(String cardNumber) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(FIND_BY_NUMBER)) {
            statement.setString(1, cardNumber);

            try (final ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    final String pin = result.getString("pin");
                    return new Account(cardNumber, Integer.parseInt(pin));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addIncomeToAccount(int amount, String cardNumber) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(ADD_INCOME)) {
            statement.setInt(1, amount);
            statement.setString(2, cardNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBalance(String cardNumber) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(GET_BALANCE)) {
            statement.setString(1, cardNumber);
            try (final ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("balance");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void closeAccount(String cardNumber) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT)) {
            statement.setString(1, cardNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void withdrawMoney(int amount, String cardNumber) {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(WITHDRAW_MONEY)) {
            statement.setInt(1, amount);
            statement.setString(2, cardNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}