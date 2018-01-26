package io.gresse.hugo.tp3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * First app open
 * 1. blank SP
 * 2. MainActivty avec SP vide
 *
 *
 *
 * Display a simple chat connected to Firebase
 */
public class MainActivity extends AppCompatActivity implements ValueEventListener, MessageAdapter.Listener {




    public static final String TAG = MainActivity.class.getSimpleName();

    EditText       mInputEditText;
    ImageButton    mSendButton;
    MessageAdapter mMessageAdapter;
    User user;

    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UserStorage.isUserLoggedIn(this)) {
            Intent intent = new Intent(this, NamePickerActivity.class);
            this.startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mInputEditText = findViewById(R.id.inputEditText);
        mSendButton = findViewById(R.id.sendButton);
        user = UserStorage.getUserInfo(this);

        mMessageAdapter = new MessageAdapter(this, new ArrayList<Message>(), user);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mMessageAdapter);

        connectAndListenToFirebase();

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitNewMessage(mInputEditText.getText().toString());
                mInputEditText.setText("");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabaseReference.removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Message> items = new ArrayList<>();
        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
            Message message = messageSnapshot.getValue(Message.class);
            message.key = messageSnapshot.getKey();
            items.add(message);
        }
        mMessageAdapter.setData(items);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.scrollToPosition(mMessageAdapter.getItemCount() - 1);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();

    }

    private Menu m = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_message, menu);
        m = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.logout:
                //Dans le Menu "m", on active tous les items dans le groupe d'identifiant "R.id.group2"
                User user = UserStorage.getUserInfo(this);
                UserStorage.clearUser(this);
                Intent intent = new Intent(this, NamePickerActivity.class);
                this.startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void connectAndListenToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference(Constant.FIREBASE_PATH);

        mDatabaseReference.addValueEventListener(this);
    }

    private void submitNewMessage(String message) {
        User user = UserStorage.getUserInfo(this);
        if (message.isEmpty() || user == null) {
            return;
        }

        DatabaseReference newData  = mDatabaseReference.push();
        newData.setValue(
                new Message( message,
                        user.name,
                        user.email,
                        System.currentTimeMillis() ));
    }

    @Override
    public void onItemClick(int position, Message message) {
        mDatabaseReference.child(message.key).removeValue();
    }

    public void onItemClick2(int position, Message message) {
        mDatabaseReference.child(message.key).setValue(new Message("valou il est pas beau", message.userName, message.userEmail, message.timestamp ));
    }

}
