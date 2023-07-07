import java.sql.*;
import java.util.Scanner;
//or you can keep the following instead
//import java.sql.DriverManager;
//import java.sql.Connection;
//import java.sql.SQLException;
 
public class OracleJDBC {
 
	public static void main(String[] argv) {
 
		System.out.println("-------- Oracle JDBC Connection Testing ------");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver?");
			e.printStackTrace();
			return;
		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {
			// Replace the JDBC URL, username, and password with your actual database credentials
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sathvikreddyvuyyuru", "postgres", "1234");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}


		if (connection != null) {
			System.out.println("You made it, take control of your database now!\n");
            //System.out.println("Printing employee names from the sharmac.employee table stored on omega");
		} else {
			System.out.println("Failed to make connection!");
		}
		Boolean opt = true;
		while(opt){
		System.out.println("Enter your choice 1 (or) 2 (or) 3");
		System.out.println("1 : To view any table data");
		System.out.println("2 : To make changes to relations");
		System.out.println("3 : To see output of queries");
		
		Scanner sc=new Scanner(System.in);
		int ip=sc.nextInt();
		sc.nextLine();
		switch (ip)
		{
        case 1:
		try {
			System.out.println("Please enter table name");
			Scanner t = new Scanner(System.in);
			String tablename = t.next();
            Statement stmt = connection.createStatement();
	       ResultSet rs = stmt.executeQuery("select * from "+tablename);
		   ResultSetMetaData rm = rs.getMetaData();
		   int n = rm.getColumnCount();
		   for(int i=1;i<=n;i++){
			System.out.printf("%-25s",rm.getColumnName(i));
		   }
		   System.out.println();
		   for(int i=1;i<=n*25;i++){
			System.out.printf("-");
		   }
		   System.out.println();
	       while (rs.next()){
		   for(int i=1;i<=n;i++){
	         System.out.printf("%-25s",rs.getString(i));
		   }System.out.println();
		}
		rs.close();
		stmt.close();

        }
         catch (SQLException e) {
			System.out.println("\nPlease enter correct table name");
			//e.printStackTrace();
			//return;
		}
			System.out.println();
			System.out.println("Do you want to Continue: yes ; Exit: no");
			Scanner a1 = new Scanner(System.in);
			String s1 = a1.next();
				if (s1.equals("yes")){
					opt = true;
		
				}
				else{opt=false;}
		break;

			
		case 2:
		System.out.println("a : To update rating of an product");
		System.out.println("b : To add a new phone number");
		Scanner t = new Scanner(System.in);
		String ip1 = t.next();
		switch (ip1.charAt(0)){
			case 'a':
		try {
			Scanner t1 = new Scanner(System.in);
			System.out.println("Enter your customerID");
			String cid = t1.next();
			System.out.println("Enter ReviewID");
			String rid = t1.next();
			System.out.println("Enter new rating value");
			int rat = t1.nextInt();
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	       ResultSet rs = stmt.executeQuery("select cRating from Fall22_S003_16_C_gives where rgid = '"+rid+"' and "+"cgid ='"+cid+"'");
		   rs.last();
			int n1 = rs.getRow();
			if (n1==0){
				System.out.println("\nYou have not purchased this product");
			}
			else{
		   //rs.beforeFirst();
		   //while (rs.next()){
			//rs.updateInt("cRating", rat);
		   // rs.updateRow();
		  // }
	       //rs.close();
				System.out.println('else')
		   stmt.executeQuery("UPDATE fall22_s003_16_c_givesSET crating =" + Integer.toString(rat) +   "where rgid = '"+rid+"' and "+"cgid ='"+cid+"'");
	       ResultSet rs1 = stmt.executeQuery("select * from Fall22_S003_16_C_gives where rgid = '"+rid+"' and "+"cgid ='"+cid+"'");
		   ResultSetMetaData rm = rs1.getMetaData();
		   int n = rm.getColumnCount();
		   for(int i=1;i<=n;i++){
			System.out.printf("%-25s",rm.getColumnName(i));
		   }
		   System.out.println();
		   for(int i=1;i<=n*25;i++){
			System.out.printf("-");
		   }
		   System.out.println();
	       while (rs1.next()){
		   for(int i=1;i<=n;i++){
	         System.out.printf("%-25s",rs1.getString(i));
		   }System.out.println();
		}
	} 
        }
         catch (SQLException e) {
			System.out.println("error in accessing the relation");
			e.printStackTrace();
			return;}

			break;

			case 'b':
		try {
			Scanner t1 = new Scanner(System.in);
			System.out.println("Enter your customerID");
			String cid = t1.next();
			System.out.println("Enter Phone number");
			Float ph = t1.nextFloat();

            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	       ResultSet rs = stmt.executeQuery("select cid,phno from Fall22_S003_16_Customer_phno");	
		   rs.beforeFirst();
		   rs.moveToInsertRow();
		   rs.updateString("cid", cid);
		   rs.updateFloat("phno", ph);
		   rs.insertRow();
	       rs.close();

	       ResultSet rs1 = stmt.executeQuery("select * from Fall22_S003_16_Customer_phno where cid = '"+cid+"'");
		   ResultSetMetaData rm = rs1.getMetaData();
		   int n = rm.getColumnCount();
		   for(int i=1;i<=n;i++){
			System.out.printf("%-25s",rm.getColumnName(i));
		   }
		   System.out.println();
		   for(int i=1;i<=n*25;i++){
			System.out.printf("-");
		   }
		   System.out.println();
	       while (rs1.next()){
		   for(int i=1;i<=n;i++){
	         System.out.printf("%-25s",rs1.getString(i));
		   }System.out.println();
		}

        }
         catch (SQLException e) {
			System.out.println("error in accessing the relation");
			e.printStackTrace();
			return;}
			
		}
		System.out.println();
			System.out.println("Do you want to Continue: yes ; Exit: no");
			Scanner a2 = new Scanner(System.in);
			String s2 = a2.next();
				if (s2.equals("yes")){
					opt = true;
		
				}
				else{opt=false;}
			break;
		case 3:
		System.out.println("a : To get total quantity ordered of a product");
		System.out.println("b : To get top n transactions with respect to card type");
		System.out.println("c : To get detailed report of orders on the day(s) selected");
		System.out.println("d : To get all Reviews given on a product");

		Scanner t2 = new Scanner(System.in);
		String ip2 = t2.next();

		switch(ip2.charAt(0)){
			case 'a':
		try {
            System.out.println("Enter product Id");
			Scanner st = new Scanner(System.in);
			String pid = st.nextLine();
			String qy="select p.pdname, o.pcid, sum(o.pqty) as TotQty from fall22_s003_16_o_contains o , fall22_s003_16_product p where p.pdid = o.pcid and o.pcid ='" + pid + "' group by p.pdname, o.pcid ";
			Statement stmt = connection.createStatement();
			ResultSet rs1 = stmt.executeQuery(qy);
			ResultSetMetaData rm = rs1.getMetaData();
			int n = rm.getColumnCount();
			for(int i=1;i<=n;i++){
			 System.out.printf("%-25s",rm.getColumnName(i));
			}
			System.out.println();
			for(int i=1;i<=n*25;i++){
			 System.out.printf("-");
			}
			System.out.println();
			while (rs1.next()){
			for(int i=1;i<=n;i++){
			  System.out.printf("%-25s",rs1.getString(i));
			}System.out.println();

        }
	}
         catch (SQLException e) {
 
			System.out.println("error in accessing the relation");
			e.printStackTrace();
			return;}
			break;
		case 'b':
		try{
			System.out.println("Enter Card Type 'Discover';'Visa','Master';'CapitalOne';'Deserve'");
			Scanner st = new Scanner(System.in);
			String ct = st.next();
			System.out.println("Enter number of rows to see");
			String r = st.next();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT distinct t.trct, t.tramount  AS Amt FROM fall22_s003_16_transaction t where t.trct = '"+ct+"' order by Amt desc Fetch first "+ r +" rows only");
			ResultSetMetaData rm = rs.getMetaData();
		   int n = rm.getColumnCount();
		   for(int i=1;i<=n;i++){
			System.out.printf("%-25s",rm.getColumnName(i));
		   }
		   System.out.println();
		   for(int i=1;i<=n*25;i++){
			System.out.printf("-");
		   }
		   System.out.println();
	       while (rs.next()){
		   for(int i=1;i<=n;i++){
	         System.out.printf("%-25s",rs.getString(i));
		   }System.out.println();
		}

		}
		catch (SQLException e) {
 
			System.out.println("error in accessing the relation");
			e.printStackTrace();
			return;}	
			break;
		case 'c':
		try{
			System.out.println("Enter day(s) between '1 to 30'\nFor multiple dates please add coma between them");
			Scanner st = new Scanner(System.in);
			String day = st.next();

			Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery("select o.odid,extract(day from o.oddatetime) as O_Date ,sum(t.tramount)as Total from fall22_s003_16_order o, fall22_s003_16_transaction t where o.otid = t.trid and extract(day from o.oddatetime) in(" + day +") group by cube(o.odid, extract(day from o.oddatetime))");
			rs.last();
			int n1 = rs.getRow();
			if (n1==0){
				System.out.println("No orders on selected day(s)");
			}
			else{
			rs.beforeFirst();
			ResultSetMetaData rm = rs.getMetaData();
			int n = rm.getColumnCount();
			for(int i=1;i<=n;i++){
			System.out.printf("%-25s",rm.getColumnName(i));
			}
			System.out.println();
			for(int i=1;i<=n*25;i++){
			System.out.printf("-");
			}
			System.out.println();
			while (rs.next()){
			for(int i=1;i<=n;i++){
				System.out.printf("%-25s",rs.getString(i));
			}System.out.println();
		}
		}

		}
		catch (SQLException e) {
	
			System.out.println("error in accessing the relation");
			e.printStackTrace();
			return;}
			break;

		case 'd':
		try{
			System.out.println("Enter the review ID");
			Scanner st = new Scanner(System.in);
			String rid = st.next();
			Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery("select rgid, round(avg(crating),2) as TotRating,cgid  from fall22_s003_16_c_gives where rgid = '"+rid+"' group by rollup(rgid,cgid)");
			rs.last();
			int n1 = rs.getRow();
			if (n1==0){
				System.out.println("No Reviews on the selected ID");
			}
			else{
			rs.beforeFirst();
			ResultSetMetaData rm = rs.getMetaData();
			int n = rm.getColumnCount();
			for(int i=1;i<=n;i++){
			System.out.printf("%-25s",rm.getColumnName(i));
			}
			System.out.println();
			for(int i=1;i<=n*25;i++){
			System.out.printf("-");
			}
			System.out.println();
			while (rs.next()){
			for(int i=1;i<=n;i++){
				System.out.printf("%-25s",rs.getString(i));
			}System.out.println();
		}}

		}
		catch (SQLException e) {
	
			System.out.println("error in accessing the relation");
			e.printStackTrace();
			return;}
			break;
		   } 
		   
		   System.out.println();
			System.out.println("Do you want to Continue: yes ; Exit: no");
			Scanner a3 = new Scanner(System.in);
			String s3 = a3.next();
				if (s3.equals("yes")){
					opt = true;
		
				}
				else{opt=false;}

	}
}
//connection.close();
	}	
}
