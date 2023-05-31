package com.aleynahasagdas.mychatapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Chatting : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var messageText: EditText

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private val chatMessages = ArrayList<String>()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_items_sign_out -> {
                mAuth.signOut()
                val intent = Intent(applicationContext, SignUp::class.java)
                startActivity(intent)
            }
            R.id.menu_items_profile -> {
                val intent = Intent(applicationContext, Profile::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        messageText = findViewById(R.id.activity_chatting_message_text)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerViewAdapter = RecyclerViewAdapter(chatMessages)

        val recyclerViewManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = recyclerViewManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = recyclerViewAdapter

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference

        getData()


    fun sendMessage(view: View) {
        val messageToSend = messageText.text.toString()

        val uuid = UUID.randomUUID()
        val uuidString = uuid.toString()

        val user = mAuth.currentUser
        val userEmail = user?.email.toString()

        databaseReference.child("Chats").child(uuidString).child("usermessage").setValue(messageToSend)
        databaseReference.child("Chats").child(uuidString).child("useremail").setValue(userEmail)
        databaseReference.child("Chats").child(uuidString).child("usermessagetime").setValue(ServerValue.TIMESTAMP)

        messageText.setText("")

        getData()

        val newReference = database.getReference("PlayerIDs")
        newReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val hashMap = ds.value as HashMap<*, *>
                    val playerID = hashMap["playerID"] as String

                    /*try {
                        OneSignal.postNotification(
                            JSONObject("{'contents': {'en':'$messageToSend'}, 'include_player_ids': ['$playerID']}"),
                            null
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }*/
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getData() {
        val newReference = database.getReference("Chats")
        val query: Query = newReference.orderByChild("usermessagetime")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatMessages.clear()

                for (ds in dataSnapshot.children) {
                    val hashMap = ds.value as HashMap<*, *>
                    val useremail = hashMap["useremail"] as String
                    val usermessage = hashMap["usermessage"] as String

                    chatMessages.add("$useremail: $usermessage")

                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, databaseError.message.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }
}
