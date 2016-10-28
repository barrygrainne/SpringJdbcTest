package ie.revenue.rccourse.SpringJdbcTest;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import ie.rccourse.userdb.SpringUserDB;
import ie.rccourse.userdb.UserDBException;
import ie.rccourse.userdb.User;
import ie.rccourse.userdb.UserTransaction;

public class SpringUserDBTest {

	SpringUserDB userDB;
	
	@Before 
	public void setup() throws UserDBException {
		
    	ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeans.xml"); 
        
    	DriverManagerDataSource dmds = context.getBean(DriverManagerDataSource.class);
 		userDB = new SpringUserDB(dmds);
 		
 		// we must have at least 1 user for these tests to run
 		User user = new User(-1, "TESTER", "MCTESTFACE", true, "2000-01-01");
 		
 		userDB.create(user);
 		
 		// we must have a a few transactions for this user
 		
 		UserTransaction ut = new UserTransaction(-1, user.getId(),
 				"Transaction for Testing", "2016-01-01", 100.0);
 		userDB.createTransaction(ut);
 		
 		ut = new UserTransaction(-1, user.getId(),
 				"Second Transaction for Testing", "2016-01-01", 100.0);
 		userDB.createTransaction(ut);
 		
	}
	
	@After
	public void tearDown(){
		
		// delete the remaining 9 test users
		
		userDB.close();
	}
	@Test
	public void testGetUser() throws UserDBException{
		List<User> users = userDB.getUsers();
		
		User userToFind = users.get(0);
		User user;
		
		user = userDB.getUser(userToFind.getId());
		
		assertTrue(user.equals(userToFind));
	}
	@Test
	public void testUserDB() {
		assertTrue(true);
	}
	@Test
	public void testCreate() throws UserDBException {
		
		User user = new User(-1, "FirstName", "LastName", false, "2000-01-01");
		
		userDB.create(user);
		
		int id = user.getId();
		
		User check = userDB.getUser(id);
		
		assertTrue(user.equals(check));
	
		
	} 
	@Test
	public void testDelete() {

		List<User> users = userDB.getUsers();

		User userToDelete = users.get(0);

		int beforeCount = users.size();

		userDB.delete(userToDelete.getId());
		
		users = userDB.getUsers();
		
		int afterCount = users.size();
		
		assert(afterCount == beforeCount - 1);

	}

	@Test
	public void testDeleteWithTransactions() throws UserDBException{
		
		User user = new User(-1, "TESTER", "MCTESTFACE", false, "2000-01-01");
		
		userDB.create(user);
		
		UserTransaction tx = new UserTransaction(-1, user.getId(),
				"TEST TX", "2000-01-02", 99.0);
		
		userDB.createTransaction(tx);
		userDB.delete(user.getId());
		
		List<UserTransaction>txs = userDB.getTransactionsForUser(user.getId());
		assertTrue(txs.size() == 0);
		
		
	}
	
	@Test
	public void testSearch() throws UserDBException{
		
		User user = new User(-1, "someXXXthing", "someXXXthing", false, "2000-01-01");
		
		userDB.create(user);
		
		List<User> results = userDB.find("XXX");
		
		assertTrue(results.size() > 0);
	}
}
