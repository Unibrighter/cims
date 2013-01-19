package start;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import sql.ConnectionPool;

public class StartContext extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String dbURL = "jdbc:mysql://localhost/cimsdb";
	String username = "root";
	String password = "12345";

	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		ConnectionPool connectionPool = new ConnectionPool(dbURL, username,
				password);
		try {
			connectionPool.createPool();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getServletContext().setAttribute("ConnectionPool", connectionPool);
		System.out.println("�Զ���������.");
//		System.out.println("�Զ���������.");
//		for (int i = 0; i < 10; i++) {
//
//			System.out.println(i + "�� �Զ���������.");
//		}
	}
}
