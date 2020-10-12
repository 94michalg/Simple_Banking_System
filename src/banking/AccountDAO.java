package banking;

public interface AccountDAO {
    String TABLE_NAME = "card";

    String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    String  CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "number TEXT," +
            "pin TEXT," +
            "balance INTEGER DEFAULT 0" +
            ")";

    String INSERT = "INSERT INTO " + TABLE_NAME + " (NUMBER, PIN)" +
            "VALUES(?, ?)";

    String FIND_BY_NUMBER = "SELECT * FROM " + TABLE_NAME + " WHERE number=?";

    String ADD_INCOME = "UPDATE " + TABLE_NAME + " SET balance = balance + ? WHERE number = ?";

    String WITHDRAW_MONEY = "UPDATE " + TABLE_NAME + " SET balance = balance - ? WHERE number = ?";

    String GET_BALANCE = "SELECT balance FROM " + TABLE_NAME + " WHERE number = ?";

    String DELETE_ACCOUNT = "DELETE FROM " + TABLE_NAME + " WHERE number = ?";

}