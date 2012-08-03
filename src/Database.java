

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;


public class Database {
       
        Connection connect = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        String query;
        
        /*Database(){
                query="SELECT login_time,logout_time FROM database_hacku.login_info WHERE login_info.id="+id+"ORDER BY ASC;";
        }*/
        
        Database(){
             try{Class.forName("com.mysql.jdbc.Driver");
 		connect = DriverManager.getConnection("jdbc:mysql://localhost/database_hacku?"+ "user=roruser&password=rorpw");
            }catch(Exception e){
                System.out.println("cannot connect");
            }
        }
        
        public String getId(String tempname){
            try{query = "Select id from user where name=?";
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1,tempname);
            ResultSet re = preparedStatement.executeQuery();
            re.next();
            //System.out.println(re.getString("id"));
            return re.getString("id");
            }catch(Exception e){
                System.out.println("oops!");
            }
            return null;
        }
        public ResultSet readDataBase(String id){
        	try{
        		
        		query="SELECT login_time,logout_time FROM login_info WHERE login_info.id=? AND logout_time>=? ORDER BY logout_time ASC;";
                        preparedStatement = connect.prepareStatement(query);

        		preparedStatement.setString(1, id);
        		java.util.Calendar calin = Calendar.getInstance();
        		calin.setTime(new Date());
        		calin.add(Calendar.DAY_OF_YEAR, -29);
        		//System.out.println(new java.sql.Timestamp(calin.getTime().getTime()));
        		java.sql.Timestamp var = new java.sql.Timestamp(calin.getTime().getTime());
        		String str= var.toString();
        		String s= str.substring(0,11)+"23:59:59.999";
        		preparedStatement.setString(2,s);
        		//System.out.println(s);
        		resultSet = preparedStatement.executeQuery();
                        return resultSet;
        	}catch (Exception e) {
        		System.out.println("error occured");
        		e.printStackTrace();
        	}
                return null;
        }
        
        
   
        public void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}
}