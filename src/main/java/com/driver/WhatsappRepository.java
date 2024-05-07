
package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;


import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String,User> userMap;
    private HashMap<Integer,Message> messageMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        messageMap=new HashMap<>();
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMap = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if(userMap.containsKey(mobile))
            throw new Exception("User already exist");
        userMap.put(mobile,new User(name,mobile));
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        // The list contains at least 2 users where the first user is the admin.
        // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        // If there are 2+ users, the name of group should be "Group #count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        // Note that a personal chat is not considered a group and the count is not updated for personal chats.
        if(users.size()<2)
            return null;

        for(User u:users){
            try {
                createUser(u.getName(),u.getMobile());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(users.size()>2){
            customGroupCount++;
            Group g=new Group("Group"+customGroupCount,users.size());
            groupUserMap.put(g,users);
            adminMap.put(g,users.get(0));
            return g;
        }
        Group g=new Group(users.get(1).getName(),2);
        groupUserMap.put(g,users);
        return g;
    }

    public int createMessage(String content){
        // The 'i^th' created message has message id 'i'.
        messageId++;
        messageMap.put(messageId,new Message(messageId,content,new Date()));
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        int flag=0;
        for(User user:groupUserMap.get(group)){
            if(user.getMobile().equals(sender.getMobile()))
                flag=1;
        }
        if(flag==0)
            throw new Exception("You are not allowed to send message");
        List<Message> list=groupMessageMap.getOrDefault(group,new ArrayList<>());
        list.add(message);
        groupMessageMap.put(group,list);
        senderMap.put(message,sender);
        return list.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Change the admin of the group to "user".
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        if(!adminMap.containsKey(group))
            throw new Exception("Group does not exist");
        if(!adminMap.get(group).getMobile().equals(approver.getMobile()))
            throw new Exception("Approver does not have rights");
        int flag=0;
        for(User u:groupUserMap.get(group)){
            if(u.getMobile().equals(user.getMobile()))
                flag=1;
        }
        if(flag==0)
            throw new Exception("User is not a participant");

        adminMap.put(group,user);
        return "SUCCESS";

    }

    public int removeUser(User user) throws Exception{
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        if(!userMap.containsKey(user.getMobile()))
            throw new Exception("User not found");
        for(List<User> userList:groupUserMap.values()){
            if(userList.get(0).getMobile().equals(user.getMobile()))
                throw new Exception("Cannot remove admin");
            for(User u:userList){
                if(u.getMobile().equals(user.getMobile())){ // removable user exist
                    userMap.remove(user);
                    userList.remove(user);
                    int ct=userList.size();
                    for(Message m:senderMap.keySet()){
                        if(senderMap.get(m).equals(user))
                            senderMap.remove(m);
                    }
                    ct+=senderMap.size();
                    return ct;
                }
            }
        }
        throw new Exception("User not found");
    }

    public String findMessage(Date start, Date end, int K) throws Exception{
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception

        return "";
    }
}

