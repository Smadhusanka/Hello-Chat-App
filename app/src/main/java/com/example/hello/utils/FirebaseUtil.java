package com.example.hello.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    //current user ID
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    //current user details
    public static DocumentReference currentUserDetails(){

        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    //check user already logged
    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    //get user collection reference
   public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    //get chat room reference
    public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    //get chat room ID
    public static String getChatRoomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }
        else {
            return userId2+"_"+userId1;
        }
    }

    //get chat room message reference
    public static CollectionReference getChatRoomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }

    //all chat room collection reference
    public static CollectionReference allChatroomsCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    //get other users from chat room
    public static DocumentReference getOtherUsersFromChatroom(List<String> userId){
        if(userId.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userId.get(1));
        }
        else {
            return allUserCollectionReference().document(userId.get(0));
        }
    }

    //set time stamp
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    //logout
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    //get current user profile picture details
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    //get other user profile picture details
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
}
