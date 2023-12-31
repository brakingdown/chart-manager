package develop;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class UserController {
    DatabaseManager dbManager = new DatabaseManager();
    String getAll = "SELECT * FROM User";
    String table = "User";
    Scanner scanner = new Scanner(System.in);
    public User setUser(Integer Id, String username, Integer vipLevel, String signDate, Float money, String phoneNum, String email, String password){
        User user = new User();
        user.setId(Id);
        user.setUsername(username);
        user.setVipLevel(vipLevel);
        user.setSignDate(signDate);
        user.setMoney(money);
        user.setPhoneNum(phoneNum);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    public String[] getUserString(User user){
        String[] userString = new String[8];
        userString[0] = String.valueOf(user.getId());
        userString[1] = user.getUsername();
        userString[2] = user.getVipLevelDetail();
        userString[3] = user.getSignDate();
        userString[4] = String.valueOf(user.getMoney());
        userString[5] = user.getPhoneNum();
        userString[6] = user.getEmail();
        userString[7] = user.getPassword();
        return userString;
    }

    public Integer getVipLevel(String vipLevelDetail){
        return switch (vipLevelDetail) {
            case "铜牌客户" -> 0;
            case "银牌客户" -> 1;
            case "金牌客户" -> 2;
            default -> -1;
        };
    }

    public User userLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM User WHERE 用户名 = ? AND 用户密码 = ?";
        ArrayList<String[]> userList = dbManager.read(sql, username, password);
        User user = null;
        if (!userList.isEmpty()) {
            String[] row = userList.get(0);
            user = setUser(Integer.valueOf(row[0]), row[1], getVipLevel(row[2]), row[3], Float.valueOf(row[4]), row[5], row[6], row[7]);
        }
        return user;
    }

    public User getUserById(String id) throws SQLException{
        String sql = "SELECT * FROM User WHERE 客户ID = ?";
        ArrayList<String[]> userList = dbManager.read(sql, id);
        User user = null;
        if (!userList.isEmpty()) {
            String[] row = userList.get(0);
            user = setUser(Integer.valueOf(row[0]), row[1], getVipLevel(row[2]), row[3], Float.valueOf(row[4]), row[5], row[6], row[7]);
        }
        return user;
    }

    public User getUserByUsername(String username) throws SQLException{
        String sql = "SELECT * FROM User WHERE 用户名 = ?";
        ArrayList<String[]> userList = dbManager.read(sql, username);
        User user = null;
        for (String[] strings : userList) {
            if (username.equals(strings[1])) {

                user = setUser(Integer.valueOf(strings[0]), strings[1], getVipLevel(strings[2]), strings[3], Float.valueOf(strings[4]), strings[5], strings[6], strings[7]);

            }

        }
        return user;
    }

    public User getUserByUsernameAndEmail(String username, String email) throws SQLException{
        String sql = "SELECT * FROM User WHERE 用户名 = ? AND 用户邮箱 = ?";
        ArrayList<String[]> userList = dbManager.read(sql, username, email);
        User user = null;
        for (String[] strings : userList) {
            if (username.equals(strings[1]) && email.equals(strings[6])) {

                user = setUser(Integer.valueOf(strings[0]), strings[1], getVipLevel(strings[2]), strings[3], Float.valueOf(strings[4]), strings[5], strings[6], strings[7]);

            }

        }
        return user;
    }

    public ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<String[]> userList = dbManager.read(getAll);
        ArrayList<User> users = new ArrayList<>();
        for (String[] strings : userList) {

            User user = setUser(Integer.valueOf(strings[0]), strings[1], getVipLevel(strings[2]), strings[3], Float.parseFloat(strings[4]), strings[5], strings[6], strings[7]);

            users.add(user);

        }
        return users;
    }

    public void showUser(User user) throws SQLException {
        System.out.println();
        if (user != null) {
            dbManager.showHeader(table);
            System.out.println(user.getId()+" "+user.getUsername()+" "+user.getVipLevelDetail()+" "+user.getSignDate()+" "+user.getMoney()+" "+user.getPhoneNum()+ " "+user.getEmail());
        }
        else System.out.println("找不到用户");
    }

    public void showAllUsers() throws SQLException {
        ArrayList<User> users = getAllUsers();
        dbManager.showHeader(table);
        for(User user:users){
            System.out.println(user.getId()+" "+user.getUsername()+" "+user.getVipLevelDetail()+" "+user.getSignDate()+" "+user.getMoney()+" "+user.getPhoneNum()+ " "+user.getEmail());
        }
    }

    public void deleteUser(String phoneNum) throws SQLException {
        String sql = "SELECT * FROM User WHERE 用户手机号 = ?";
        ArrayList<String[]> userList = dbManager.read(sql, phoneNum);

        if (!userList.isEmpty()) {
            System.out.println("请确认是否删除以下客户(y/n)");
            String[] userRow = userList.get(0);
            User user = setUser(Integer.valueOf(userRow[0]), userRow[1], getVipLevel(userRow[2]), userRow[3], Float.valueOf(userRow[4]), userRow[5], userRow[6], userRow[7]);
            showUser(user);
            String judge = scanner.next();
            if (judge.equalsIgnoreCase("y")) {
                dbManager.deleteUserByPhoneNum(phoneNum);
                System.out.println("删除成功");
            } else if (judge.equalsIgnoreCase("n")) {
                System.out.println("取消删除");
            } else {
                System.out.println("输入错误");
            }
        } else {
            System.out.println("用户手机号不存在");
        }
    }

    public void userRegister() throws SQLException {
        ArrayList<String[]> userList = dbManager.read(getAll);

        System.out.println("请输入用户名:");
        String username = scanner.next();

        int id = 0;
        for (String[] strings : userList) {
            if (username.equals(strings[1])) {
                System.out.println("用户名已存在");
                return;
            }
            id = Integer.parseInt(strings[0]);
        }
        id = id + 1;

        System.out.println();
        System.out.println("请输入密码（大于8位且包含大小写字母与特殊符号）:");
        String password;
        while(true){
            password = scanner.next();
            if (!new Password().checkPassword(password)){
                System.out.println("密码要求大于8位且包含大小写字母与特殊符号,请重新输入:");
            }
            else break;
        }
        System.out.println();
        System.out.println("请输入手机号:");
        String phoneNum = scanner.next();
        System.out.println();
        System.out.println("请输入邮箱:");
        String email = scanner.next();
        System.out.println();

        //获取当前时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String signDate = df.format(new Date());

        String[] userString = getUserString(setUser(id, username,0, signDate, 0f, phoneNum, email, password));
        long uid = Long.parseLong(userString[0]);
        String uname = userString[1];
        String uLevel = userString[2];  //这里可能需要转换为与数据库中相匹配的数据格式
        String uDate = userString[3];
        BigDecimal uMoney = new BigDecimal(userString[4]);
        String uPhone = userString[5];
        String uEmail = userString[6];
        String uPwd = userString[7];

        try {
            dbManager.insertUser(uid, uname, uLevel, uDate, uMoney, uPhone, uEmail, uPwd);
            System.out.println("注册成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifyUserPwdByID(String id, String pwd) throws SQLException {
        Long userId = Long.parseLong(id);
        boolean isUpdated = dbManager.updateUserPasswordByID(userId, pwd);

        if (isUpdated) {
            System.out.println("用户密码已更新");
        } else {
            System.out.println("用户ID不存在，密码更新失败");
        }
    }

}

