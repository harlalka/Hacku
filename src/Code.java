/*import java.io.*;
import java.sql.SQLException;*/
import java.util.*;
/*import java.net.*;
import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;*/
import java.sql.ResultSet;
import java.sql.Timestamp;


class Code {
        private int num_days=29;
        private int num_ticks = 1440;
        private int[][] login= new int[this.num_days][this.num_ticks]; //to be initialised
        private int[][] logout= new int[this.num_days][this.num_ticks]; //to be initialised
        private int[][] input= new int[this.num_days][this.num_ticks];
        
        public double[] prob =  new double[this.num_ticks];
        private double w_factor = 1.06; 
        
        private ResultSet rs;
        private int current;
        String id;
        String name;
        
        Code(String tempname){
                name = tempname;
                //initialise 2 2-d arrays
                    Database db = new Database();
                    id=db.getId(tempname);
        		java.util.Calendar cal = Calendar.getInstance();
        		cal.setTime(new Date());
        		current = cal.get(java.util.Calendar.DAY_OF_YEAR);
        		//System.out.println(current);
        		
        		rs=db.readDataBase(id);
        		//System.out.println("aashay");
        		init_arrays();
                init();
                init_prob();
                //db.close();
                print_input();
        }
        public void init_arrays(){
        	String[] inString = new String[num_days];
        	String[] outString = new String[num_days];
        	for(int j=0;j<num_days;j++){
        		inString[j] = "";
        		outString[j] = "";
        	}
        	//int i=0;
        	try{
        	while(rs.next()){
        		Timestamp in = rs.getTimestamp("login_time");
        		Timestamp out = rs.getTimestamp("logout_time");
        		java.util.Calendar calin = Calendar.getInstance();
        		calin.setTime(in);
        		java.util.Calendar calout = Calendar.getInstance();
        		calout.setTime(out);
        		int dayin = current - calin.get(java.util.Calendar.DAY_OF_YEAR);
        		int dayout =  current - calout.get(java.util.Calendar.DAY_OF_YEAR);
        		//System.out.println("dayin"+dayin+"dayout"+dayout);
        		if(dayin >=num_days || dayout>=num_days)break;
        		inString[dayin] += Integer.toString(calin.get(java.util.Calendar.HOUR_OF_DAY)*60+calin.get(java.util.Calendar.MINUTE)) + "~";
        		outString[dayout] += Integer.toString(calout.get(java.util.Calendar.HOUR_OF_DAY)*60+calout.get(java.util.Calendar.MINUTE)) + "~";
        		if(dayin!=dayout){
        			outString[dayin] += "1439";
        			inString[dayout] += "0~";
        		}
        	}
        	for(int j=0;j<num_days;j++){
        		String[] tempin = inString[j].split("~");
        		String[] tempout = outString[j].split("~");
        		for(int k=0;k<tempin.length;k++){
        			if(tempin[k]!="")login[j][k]=Integer.parseInt(tempin[k]);
        		}
        		for(int k=0;k<tempout.length;k++){
        			if(tempout[k]!="")logout[j][k]=Integer.parseInt(tempout[k]);
        		}
        	}
        	for(int j=0;j<num_days;j++){
        		//System.out.println("day:"+j+" :-");
        		for(int k=0;k<login.length;k++){
        		//	System.out.print(login[j][k]) ;System.out.print(" ");
        		}//System.out.println();
        		for(int k=0;k<logout.length;k++){
        		//	System.out.print(logout[j][k]) ;System.out.print(" ");
        		}//System.out.println();
        	}
        	}catch(Exception e){
        		System.out.println("error in init_arrays");
        		e.printStackTrace();
        	}
        }
        
        private double power(double a, int b){
                if (b==0) return 1;
                else if (b==1) return a;
                else return (a*power(a,b-1));
        }
        
        private void init(){
                
                for(int m=0;m<num_days;m++){
                        int i=0;
                        //int j=0;
                       //int k=0;
                        for(int k=0;k<num_ticks;k++){
                                while(k<=login[m][i]){
                                        input[m][k]=0;
                                        k++;
                                       // System.out.println(k);
                                        //System.out.println(i);
                                }
                                while(k<=logout[m][i]){
                                        input[m][k]=1;
                                        k++;
                                       // System.out.println(k);
                                        //System.out.println(i);
                                }
                                i++;   
                        }
                }
        }
        
        public void init_prob(){
                for (int j=0;j<num_ticks;j++){
                        prob[j]=0;
                        for(int i=0;i<num_days;i++){
                                prob[j]=prob[j]+((double)input[i][j]*power(w_factor,i));
                        }
                }
        }
        
        private void print_input(){
        	for(int i=0;i<num_days;i++){
        		for (int j=0;j<num_ticks;j++){
        			System.out.print(input[i][j]);
        		}
        		System.out.println();
        	}
        }
        
        
        public String[] get_data()
    	{
        	String name = this.name;
        	ResultSet rs=this.rs;
        	double[] float_arr = this.prob;
    		String[] data = new String[3];
    		try
    		{
    		java.sql.Timestamp temp,temp1,temp2;
    		java.sql.Timestamp current = new Timestamp(new java.util.Date().getTime());
    		rs.previous();
    		temp = rs.getTimestamp("login_time");
                //System.out.println(temp.toString());
                //System.out.println(current.toString());
    		long current_time = current.getTime();
    		long temp_time = temp.getTime();
    		long last = (current_time - temp_time)/(1000*60*60*24);
                //last--;
    		if (last == 0) data[0] = name + " came online yesterday";
    		else data[0] = name + " has not come online for the last " + last + " days";
                //System.out.println(data[0]);
    		double max = float_arr[0];
    		for (int j=0;j<1440;j++)
    		{
    			if (float_arr[j] > max) max = float_arr[j];
    		}
                //System.out.println("float_arr completed");
    		int[] times;
    		times = new int[1440];
    		double threshold = 0.7*max;
    		int k=0;
                int flag = 0;
    		for (int j=0;j<1440;j++)
    		{
                    if (flag == 0)
                    {
    			if (float_arr[j] >= threshold)
                        {
                            times[k] = j;
                            k++;
                            flag = 1;
                        }
                    }
                    else if (flag == 1)
                    {
                        if (float_arr[j] < threshold)
                        {
                            times[k] = j;
                            k++;
                            flag = 0;
                        }
                    }
                }
                //System.out.println("times array completed");
    		data[1] = name + " generally comes online on these durations - ";
                //System.out.println(data[1]);
    		for (int j=0;j<k-1;j+=2)
    		{
                    if((times[j]%60)<10){
                        if((times[j+1]%60)<10){
                            data[1] += (int)(times[j]/60) +":0"+ times[j]%60 +" to " + (int)(times[j+1]/60) +":0"+ times[j+1]%60  + " , ";
                        }
                        else
                            data[1] += (int)(times[j]/60) +":0"+ times[j]%60 +" to " + (int)(times[j+1]/60) +":"+ times[j+1]%60  + " , ";
                    }
                    else{
                        if((times[j+1]%60)<10){
                            data[1] += (int)(times[j]/60) +":"+ times[j]%60 +" to " + (int)(times[j+1]/60) +":0"+ times[j+1]%60  + " , ";
                        }
                        else
                            data[1] += (int)(times[j]/60) +":"+ times[j]%60 +" to " + (int)(times[j+1]/60) +":"+ times[j+1]%60  + " , ";
                    }
    		}
                //System.out.println(data[1]);
    		rs.previous();
    		java.util.Calendar cal = Calendar.getInstance();
    		long[] duration = new long[8];
    		for (int i=0;i<7;i++) duration[i] = 0;
    		long dur;
                rs.next();
                rs.next();
    		while (rs.previous())
    		{
    			temp1 = rs.getTimestamp("login_time");
    			temp2 = rs.getTimestamp("logout_time");
    			cal.setTime(temp1);
                        //System.out.println(cal.get(java.util.Calendar.DAY_OF_WEEK));
    			dur = (temp2.getTime() - temp1.getTime())/(1000*60);
                        //System.out.println(dur);
    			duration[cal.get(java.util.Calendar.DAY_OF_WEEK)] += dur;
    		}
                //System.out.println(duration[4]);
    		long max1 = duration[0], max2 = duration[0];
    		int day1 = 0, day2 = 0;
    		String[] day = {"","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    		for (int i=0;i<=7;i++)
    		{   //System.out.println(duration[i]);
    			if (duration[i] >= max1)
    			{
    				max2 = max1;
    				max1 = duration[i];
    				day2 = day1;
    				day1 = i;
    			}
                        else if (duration[i] > max2)
                        {
                            max2 = duration[i];
                            day2 = i;
                        }
    		}
                long min=duration[0];
                for(int i=1;i<=7;i++){
                    if(duration[i]<min) min=duration[i];
                }
               // System.out.println(min);
                //System.out.println(max1);
                //System.out.println(max2);
                if(max1 <= min*1.7)
                    data[2]= name + "generally comes online everyday ";
                else if(day1 != day2)
                {
                if(duration[day1]>=2*duration[day2])
                    data[2] = name + " generally comes online on " + day[day1];
                else
                    data[2] = name + " generally comes online on " + day[day1] + " and " + day[day2];
                }
                else
                    data[2] = name + " generally comes online on " + day[day1];
    		//System.out.println(data[2]);
                }
    		catch (Exception e)
    		{
    			e.getMessage();
    		}
    		return data;
    	}

}