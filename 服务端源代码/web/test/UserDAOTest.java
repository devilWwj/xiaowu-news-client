

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.szy.web.dao.UserDAO;

/**
 * @author  coolszy
 * @time    Dec 4, 2011 3:09:07 PM
 */
public class UserDAOTest
{
	UserDAO userDAO;
	
	@Before
	public void init() throws IOException, ClassNotFoundException, SQLException
	{
		userDAO = new UserDAO();
	}
	
	@Test
	public void testValidate() throws Exception
	{
		System.out.println("&&"+userDAO.validate("admin", "admin"));
	}

}
