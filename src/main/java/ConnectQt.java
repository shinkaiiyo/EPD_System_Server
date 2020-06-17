import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class ConnectQt {
    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String clientId = "server";
        final String mqttBrokerUri = "tcp://127.0.0.1:1883";
        ServerSocket serverSocket = new ServerSocket(8888);
        while (true) {
            Socket socket = serverSocket.accept();
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf8"));
            String type = is.readLine();
            String account = is.readLine();
            String password = is.readLine();
            System.out.println("type: " + type);
            ArrayList<String> changePwdName = null;
            //PahoMqttClient client = PahoMqttClient.getInstance();
            //client.init(clientId, mqttBrokerUri);
            //int qos = 1;
            //String topic = null;

            //MqttCallback callback = new DataProcessCallBack();
           // client.setCallback(callback);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finalcourse?serverTimezone=UTC", "root", "259796")) {
                System.out.println(conn);
                // create new connect
                Statement stmt = conn.createStatement();
                // run
                String sql;
                switch (type)
                {
                    case "1":
                        sql = "select * from adminuser";
                        break;
                    case "2":
                    case "3":
                    case "5":
                    case "7":
                    case "13":
                        sql = "select * from nomaluser";
                        break;
                    case "4":
                    case "11":
                    case "12":
                    case "14":
                        sql = "select * from item";
                        break;
                    case "6":
                    case "10":
                        sql = "select * from adminuser";
                        break;
                    case "8":
                        changePwdName  = new ArrayList<>(Arrays.asList(account.split(",")));
                        if(changePwdName.get(0).equals("admin"))
                        {
                            sql = "select * from adminuser";

                        }
                        else if(changePwdName.get(0).equals("worker"))
                        {
                            sql = "select * from nomaluser";
                        }
                        else{
                            sql = null;
                        }
                        break;
                    case "9":
                        sql = "select * from nomaluser";
                        break;
                    default:
                        sql = null;
                }
                String  result = null;
                Map<String,String> user = new HashMap<>();
                Map<String,ArrayList<?>> users = new HashMap<>();
                ArrayList<String> finalName = new ArrayList<>();
                ArrayList<String> items = null;
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    String pwd = null;
                    String workerName = null;
                    String newMember = null;
                    String newName = null;
                    String newPassword = null;
                    int i,j = 0;
                    int isRight = 0;
                    switch (type) {
                        case "1":
                            while (rs.next()) {
                                String username = rs.getNString(2);
                                String passwd = rs.getNString(3);
                                user.put(username, passwd);
                            }
                            pwd = user.get(account);
                            isRight = password.equals(pwd) ? 1 : 0;
                            result = "" + isRight;
                            break;
                        case "2":
                            while (rs.next()) {
                                String username = rs.getNString(3);
                                String passwd = rs.getNString(4);
                                user.put(username, passwd);
                            }
                            pwd = user.get(account);
                            isRight = password.equals(pwd) ? 1 : 0;
                            result = "" + isRight;
                            break;
                        case "3":
                            while (rs.next()) {
                                String marketName = rs.getNString(2);
                                if(marketName.equals(account))
                                {
                                    String name = rs.getNString(3);
                                    finalName.add(name);
                                    users.put(marketName,finalName);
                                }
                            }
                            newMember = String.join(",",finalName);
                            result = newMember;
                            break;
                        case "4":
                            while (rs.next()) {
                                String marketName = rs.getNString(11);
                                if(marketName.equals(account))
                                {
                                    StringBuffer goods = new StringBuffer();
                                    goods.append(rs.getNString(3));
                                    goods.append("\u0001");
                                    goods.append(rs.getNString(4));
                                    goods.append("\u0001");
                                    goods.append(rs.getNString(5));
                                    goods.append("\u0001");
                                    goods.append(rs.getNString(6));
                                    goods.append("\u0001");
                                    goods.append(rs.getNString(7));
                                    goods.append("\u0001");
                                    goods.append(rs.getNString(8));
                                    goods.append("\u0001");
                                    goods.append(rs.getDouble(9));
                                    goods.append("\u0001");
                                    goods.append(rs.getDouble(10));
                                    String sgoods = goods.toString();
                                    finalName.add(sgoods);
                                    users.put(marketName,finalName);
                                }
                            }
                            newMember= String.join(",",finalName);
                            result = newMember;
                            break;
                        case "5":
                            while (rs.next()) {
                                workerName = rs.getNString(3);
                                String goods = rs.getNString(5);
                                Arrays.stream(goods.split(",")).forEach(System.out::println);
                                items  = new ArrayList<>(Arrays.asList(goods.split(","))) ;
                                users.put(workerName,items);
                                isRight = account.equals(workerName) ? 1 : 0;
                                if(isRight == 1)
                                {
                                    String tempSql = "select * from item";
                                    Statement tempStmt = conn.createStatement();
                                    ResultSet tempRs = tempStmt.executeQuery(tempSql);
                                    while (tempRs.next()) {
                                        int fid = tempRs.getInt(1);
                                        String id = String.valueOf(fid);
                                        for(int a = 0;a<items.size();a++)
                                        {
                                            if(id.equals(items.get(a))) {
                                                StringBuffer goodinfo = new StringBuffer();
                                                goodinfo.append(tempRs.getNString(3));
                                                goodinfo.append("\u0001");
                                                goodinfo.append(tempRs.getNString(4));
                                                goodinfo.append("\u0001");
                                                goodinfo.append(tempRs.getNString(5));
                                                goodinfo.append("\u0001");
                                                goodinfo.append(tempRs.getNString(6));
                                                goodinfo.append("\u0001");
                                                goodinfo.append(tempRs.getNString(7));
                                                goodinfo.append("\u0001");
                                                goodinfo.append(tempRs.getNString(8));
                                                goodinfo.append("\u0001");
                                                goodinfo.append(tempRs.getDouble(9));
                                                goodinfo.append("\u0001");
                                                goodinfo.append(tempRs.getDouble(10));
                                                String sgoods = goodinfo.toString();
                                                finalName.add(sgoods);
                                            }
                                        }
                                    }
                                    newMember= String.join(",",finalName);
                                    result = newMember;
                                }
                            }
                            break;
                        case "6":
                            while (rs.next()) {
                                StringBuffer adminUsers = new StringBuffer();;
                                adminUsers.append(rs.getNString(2));
                                adminUsers.append("\u0001");
                                adminUsers.append(rs.getNString(4));
                                String sadminUsers = adminUsers.toString();
                                finalName.add(sadminUsers);
                                users.put("text",finalName);
                            }
                            newMember= String.join(",",finalName);
                            result = newMember;
                            break;
                        case "7":
                            while (rs.next()) {
                                workerName = rs.getNString(3);
                                String goods = rs.getNString(5);
                                Arrays.stream(goods.split(",")).forEach(System.out::println);
                                items  = new ArrayList<>(Arrays.asList(goods.split(","))) ;
                                users.put(workerName,items);
                                isRight = account.equals(workerName) ? 1 : 0;
                                if(isRight == 1)
                                {
                                    String tempSql = "select * from item";
                                    Statement tempStmt = conn.createStatement();
                                    ResultSet tempRs = tempStmt.executeQuery(tempSql);
                                    while (tempRs.next()) {
                                        int fid = tempRs.getInt(1);
                                        String id = String.valueOf(fid);
                                        for(int a = 0;a<items.size();a++)
                                        {
                                            if(id.equals(items.get(a)))
                                            {
                                                StringBuffer goodinfo = new StringBuffer();
                                                goodinfo.append(tempRs.getNString(3));
                                                String sgoods = goodinfo.toString();
                                                finalName.add(sgoods);
                                            }
                                        }
                                    }
                                    newMember= String.join(",",finalName);
                                    result = newMember;
                                }
                            }
                            break;
                        case "8":
                            String toDo = null;
                            //System.out.println(changePwdName);
                            if(changePwdName.get(0).equals("admin"))
                            {
                                while(rs.next())
                                {
                                    String id = String.valueOf(rs.getInt(1));
                                    String name = rs.getNString(2);
                                    user.put(name,id);
                                    if (name.equals(changePwdName.get(1)))
                                    {
                                        toDo = "UPDATE adminuser SET passwd = " + password + " WHERE id = " + id;
                                        System.out.println(toDo);
                                    }
                                }
                            }
                            else if(changePwdName.get(0).equals("worker"))
                            {
                                //System.out.println(sql);
                                while(rs.next())
                                {
                                    String id = String.valueOf(rs.getInt(1));
                                    String name = rs.getNString(3);
                                    user.put(name,id);
                                    //System.out.println(id);
                                    if (name.equals(changePwdName.get(1)))
                                    {
                                        toDo = "UPDATE nomaluser SET passwd = " + password + " WHERE id = " + id;
                                        //System.out.println(toDo);
                                    }
                                }
                            }
                            stmt.execute(toDo);
                            break;
                        case "9":
                            changePwdName  = new ArrayList<>(Arrays.asList(password.split(",")));
                            newName = changePwdName.get(0);
                            newPassword = changePwdName.get(1);
                            i = 0;
                            while(rs.next())
                            {
                                i++;
                            }
                            j = i+1;
                            toDo = "INSERT INTO nomaluser (id, marketname, name, passwd) VALUES ( %d, \"%s\",  \"%s\", \"%s\")";
                            toDo = String.format(toDo, j, account, newName, newPassword);
                            System.out.println(toDo);
                            stmt.execute(toDo);
                            break;
                        case "10":
                            changePwdName = new ArrayList<>(Arrays.asList(password.split(",")));
                            newName = changePwdName.get(0);
                            newPassword = changePwdName.get(1);
                            i = 0;
                            while(rs.next())
                            {
                                i++;
                            }
                            j = i+1;
                            toDo = "INSERT INTO adminuser (id, name, passwd,marketname) VALUES ( %d, \"%s\",  \"%s\", \"%s\")";
                            toDo = String.format(toDo, j, newName, newPassword, account);
                            System.out.println(toDo);
                            stmt.execute(toDo);
                            Statement tempStmt2 = conn.createStatement();
                            ResultSet tempRs = tempStmt2.executeQuery("select * from market");
                            int x = 0;
                            while(tempRs.next())
                            {
                                x++;
                            }
                            int y = x + 1;
                            String newToDo = "INSERT INTO market (id,marketname) VALUES ( %d,\"%s\")";
                            newToDo = String.format(newToDo,y,account);
                            tempStmt2.execute(newToDo);
                            break;
                        case "11":
                            Statement tempStmt = conn.createStatement();
                            while (rs.next())
                            {
                                String name = rs.getNString(3);
                                int id = rs.getInt(1);
                                if(name.equals(account))
                                {
                                    toDo =  "UPDATE item SET price = " + password + " WHERE id = " + id;
                                    tempStmt.execute(toDo);
                                }
                            }
                           // topic = "updateDev";
                           // client.publishMessage(topic,account+","+password,qos);
                            break;
                        case "12":
                            changePwdName  = new ArrayList<>(Arrays.asList(password.split(",")));
                            int goodstype = Integer.parseInt(changePwdName.get(0));
                            String pname = changePwdName.get(1);
                            String brand = changePwdName.get(2);
                            String origin = changePwdName.get(3);
                            String spec = changePwdName.get(4);
                            String inmu = changePwdName.get(5);
                            String barcode = changePwdName.get(6);
                            String price = changePwdName.get(7);
                            String oriPrice = changePwdName.get(8);
                            String worker = changePwdName.get(9);
                            i = 0;
                            while(rs.next())
                            {
                                i++;
                            }
                            j = i+1;
                            toDo = "INSERT INTO item (id, type, pname, brand, origin, spec, inum, barcode, price, oriPrice, spname, salername) VALUES ( %d, \"%s\",  \"%s\", \"%s\" , \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")";
                            toDo = String.format(toDo, j, goodstype, pname, brand,origin,spec,inmu,barcode,price,oriPrice,account,worker);
                            System.out.println(toDo);
                            stmt.execute(toDo);
                            Statement tempStmt3 = conn.createStatement();
                            ResultSet tempRs2 = tempStmt3.executeQuery("select * from nomaluser");
                            while(tempRs2.next())
                            {
                                if(tempRs2.getNString(2).equals(account) && tempRs2.getNString(3).equals(worker))
                                {
                                    int uid = tempRs2.getInt(1);
                                    String reItemId = tempRs2.getNString(5);
                                    String newItemId = reItemId + "," + j;
                                    toDo =  "UPDATE nomaluser SET itemid = \"%s\" WHERE id = %d";
                                    toDo = String.format(toDo,newItemId,uid);
                                    System.out.println(toDo);
                                    stmt.execute(toDo);
                                }
                            }
                            //topic = "devOnline";
                           // client.publishMessage(topic,password,qos);
                            break;
                        case "13":
                            while (rs.next())
                            {
                                int id = rs.getInt(1);
                                String name = rs.getNString(3);
                                String spname = rs.getNString(2);
                                if(account.equals(name) && password.equals(spname))
                                {
                                    toDo = "DELETE FROM nomaluser WHERE id =" + id;
                                    Statement tempStmt5 = conn.createStatement();
                                    tempStmt5.execute(toDo);
                                }
                            }
                            break;
                        case "14":
                            while (rs.next())
                            {
                                int id = rs.getInt(1);
                                String name = rs.getNString(3);
                                String spname = rs.getNString(11);
                                System.out.println(account);
                                System.out.println(name);
                                System.out.println(password);
                                System.out.println(spname);
                                if(account.equals(name) && password.equals(spname))
                                {
                                    toDo = "DELETE FROM item WHERE id =" + id;
                                    Statement tempStmt4 = conn.createStatement();
                                    tempStmt4.execute(toDo);
                                }
                            }
                          //  topic = "clearDev";
                           // client.publishMessage(topic,account+","+password,qos);
                            break;
                        default:
                            System.out.println(type);
                    }
                    System.out.println(result);
                    PrintWriter os = new PrintWriter(socket.getOutputStream());
                    os.println(result);
                    os.flush();
                }
            }
        }
    }
}
