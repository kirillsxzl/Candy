package cf.ga2mer.candy.database;

import java.util.List;

public interface IDatabaseAccountHandler {

    public void addAccount(Account account);
    public Account getAccount(int id);
    public List<Account> getAllAccounts();
    public int getCountAccounts();
    public int updateAccount(Account account);
    public void deleteAccount(Account account);
    public void deleteAll();

}
