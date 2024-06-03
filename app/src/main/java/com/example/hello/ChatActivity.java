package com.example.hello;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hello.adapter.ChatRecyclerAdapter;
import com.example.hello.model.ChatMessageModel;
import com.example.hello.model.ChatRoomModel;
import com.example.hello.model.UserModel;
import com.example.hello.utils.AndroidUtil;
import com.example.hello.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatRecyclerAdapter adapter;
    ChatRoomModel chatRoomModel;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    public String userId, targetUserId, username,  targetUsername;
    ZegoSendCallInvitationButton audioCall, videoCall;
    UserModel currentUserModel;
    ImageButton emergency;
    String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get User model
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        //assign id's for variables
        messageInput = findViewById(R.id.chatMessageInput);
        sendMessageBtn = findViewById(R.id.messageSendBtn);
        backBtn = findViewById(R.id.backBtn);
        otherUsername = findViewById(R.id.otherUserName);
        recyclerView = findViewById(R.id.chatRecyclerView);
        imageView = findViewById(R.id.profilePicImageView);
        audioCall = findViewById(R.id.audioCall);
        videoCall = findViewById(R.id.videoCall);
        emergency = findViewById(R.id.emergency);

        //set profile picture of other user on chat
        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this,uri,imageView);
                    }
                });

        //back button
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        //set other users name and mobile number on chat
        otherUsername.setText(otherUser.getUsername());
        mobileNumber = otherUser.getPhone();

        //message sent button
        sendMessageBtn.setOnClickListener(v ->{
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, Emergency.class);
                //send data to the chat activity to emergency activity
                intent.putExtra("phone",mobileNumber);
                startActivity(intent);
            }
        });

        //function call
        getOrCreateChatRoomModel();
        setupChatRecyclerView();
        onlineCall();

    }

    //chat recycler view
    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatRoomMessageReference(chatroomId)
                .orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    //message send function
    void sendMessageToUser(String message){
        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);

        FirebaseUtil.getChatRoomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());

        FirebaseUtil.getChatRoomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });
    }

    //get or create chatroom model
    void getOrCreateChatRoomModel(){
        FirebaseUtil.getChatRoomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel==null){
                    chatRoomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()), Timestamp.now(),""
                    );
                    FirebaseUtil.getChatRoomReference(chatroomId).set(chatRoomModel);
                }
            }
        });
    }

    protected void onDestroy(){
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

    void onlineCall(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            username = currentUserModel.getUsername();
            targetUsername = otherUser.getUsername();
            userId = currentUserModel.getUserId();
            targetUserId = otherUser.getUserId();

            //zego call permissions
            PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .onExplainRequestReason(new ExplainReasonCallback() {
                        @Override
                        public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                            String message = "We need your consent for the following permissions in order to use the offline call function properly";
                            scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                        }
                    }).request(new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                             @NonNull List<String> deniedList) {
                        }
                    });

            //assign caller details
            Application application = getApplication();
            long appID = 584971146;
            String appSign = "4a1154d443f5c24e51fd6e03e1356bd30c673599f1c87128efd10bb97af67c99";
            String userID = userId;
            String userName = username;

            ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
            ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);

            //assign receiver details
            String targetUserID = targetUserId;
            String targetUserName = targetUsername;

            //make voice call
            audioCall.setIsVideoCall(false);
            audioCall.setResourceID("zego_uikit_call");
            audioCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));

            //make video call
            videoCall.setIsVideoCall(true);
            videoCall.setResourceID("zego_uikit_call");
            videoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));

        });
    }

}

