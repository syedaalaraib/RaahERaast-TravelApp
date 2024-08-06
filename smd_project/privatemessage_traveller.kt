package com.laraib.smd_project

import android.annotation.SuppressLint
import android.app.Activity.ScreenCaptureCallback
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

data class User_(
    val name: String = "", // Default values for properties
    var photoLink: String = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "")
}


class privatemessage_traveller : AppCompatActivity() {

    private var outputFile: File? = null
    private lateinit var recorder: MediaRecorder
    private var isRecording = false
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var chooseimg: ImageView
    private lateinit var bitmap: Bitmap
    private var encodedImage: String = ""
    var fileuri: Uri? = null


    private var mediaRecorder: MediaRecorder? = null
    private var outputFileName: String = ""

    private val screenCaptureCallback = ScreenCaptureCallback {
        Toast.makeText(this, "screenshot detected", Toast.LENGTH_SHORT).show()
    }


    private lateinit var btnsend: ImageView

    private lateinit var imageView: CircleImageView
    private lateinit var username: TextView
    private lateinit var btn_send: ImageButton
    private lateinit var text_send: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var fuser: FirebaseUser
    private lateinit var database: DatabaseReference

    private lateinit var userIntent: Intent
    private lateinit var madapter: messageadapter
    private var mchat = mutableListOf<ChatRV>()
    private var imageurl: String = ""
    private var audiourl: String = ""
    private val GALLERY_REQUEST_CODE=1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privatemessage_traveller)




        val chatButton = findViewById<ImageView>(R.id.chatbutton)
        chatButton.setOnClickListener {
            val intent = Intent(this, chat_traveller::class.java)
            startActivity(intent)
        }


        val homeButton = findViewById<ImageView>(R.id.homebutton)
        homeButton.setOnClickListener {
            val intent = Intent(this, home::class.java)
            startActivity(intent)
        }

        chooseimg = findViewById(R.id.uploadpic)
        chooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
        }

        val screenCaptureCallback = ScreenCaptureCallback {
            Log.d("ScreenCaptureCallback", "Screenshot detected")
            runOnUiThread {
                Toast.makeText(this, "Screenshot detected", Toast.LENGTH_LONG).show()
            }
        }


        mediaPlayer = MediaPlayer()
        recorder = MediaRecorder()


        // Initialize media recorder and output file name
        outputFileName = "${externalCacheDir?.absolutePath}/recording.3gp"
        mediaRecorder = MediaRecorder()


        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference
        fuser = FirebaseAuth.getInstance().currentUser!!


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        username = findViewById(R.id.name)
        imageView = findViewById(R.id.image)
        btn_send = findViewById(R.id.btn_send)
        text_send = findViewById(R.id.text_send)

        userIntent = intent
        val receiverId = userIntent.getStringExtra("currentuserid")
        val userId = userIntent.getStringExtra("userid")
//        val userName = userIntent.getStringExtra("username")


        // Retrieve receiver's information from Firebase
        if (receiverId != null) {
            val refProfile = FirebaseDatabase.getInstance().getReference("guides").child(receiverId)
            refProfile.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User_::class.java)
                    if (user != null) {
                        // Display receiver's name
                        username.text = user.name
                        // Load receiver's image using Glide
                        imageurl = user.photoLink
                        if (!imageurl.isNullOrEmpty()) {
                            Glide.with(this@privatemessage_traveller)
                                .load(user.photoLink)
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .into(imageView)
                        }
                    } else {
                        Toast.makeText(this@privatemessage_traveller, "Receiver data is null", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@privatemessage_traveller, "Failed to retrieve receiver data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@privatemessage_traveller, "Receiver ID is null", Toast.LENGTH_SHORT).show()
        }

        btn_send.setOnClickListener {
            val msg = text_send.text.toString()
            if (msg.isNotBlank()) {
                if (userId != null) {
                    if (receiverId != null) {
                        sendmessage(fuser.uid, receiverId, msg)

                        NotificationHelper(this, msg).Notification()
                        val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        kh.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    }
                    else
                    {
                        Toast.makeText(this, "receiver id is null", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // Receiver ID is null
                    Log.e("SendMessage", "Receiver ID is null")
                    Toast.makeText(this@privatemessage_traveller, "Receiver ID is null", Toast.LENGTH_SHORT).show()
                }
            } else if (fileuri != null) {
                // Sending message with image
                if (userId != null) {
                    if (receiverId != null) {
//                        sendMessageWithImage(fuser.uid, receiverId, encodedImage)

                        // Clear the selected image after sending
                        chooseimg.setImageResource(android.R.drawable.ic_menu_gallery)
                    }
                    else{
                        Toast.makeText(this, "receiver id is null", Toast.LENGTH_SHORT).show()

                    }
                }
                else{
                    Toast.makeText(this, "user id is null", Toast.LENGTH_SHORT).show()

                }

//                NotificationHelper(this,"Image sent successfully!").Notification()
                val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
            } else {
                // Empty message
                Log.d("SendMessage", "Empty message")
                Toast.makeText(
                    this@privatemessage_traveller,
                    "You can't send an empty message",
                    Toast.LENGTH_SHORT
                ).show()
            }
            text_send.text = ""
        }

//        readmessage(fuser.uid, receiverId ?: "",imageurl)
        readmessage(fuser.uid, receiverId ?: "")

    }
    private fun sendmessage(sender: String, receiver: String, message: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        reference.push().setValue(hashMap)
        reference.keepSynced(true)
        val kh =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)

    }

    private fun readmessage(myid: String, userid: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mchat.clear()
                for (snapshot in snapshot.children) {
                    val chat = snapshot.getValue(ChatRV::class.java)
                    if (chat != null) {
                        if (chat.receiver == myid && chat.sender == userid || chat.receiver == userid && chat.sender == myid) {
                            mchat.add(chat)
                        }
                    }
                }
                // Pass mchat to the adapter
                madapter = messageadapter(this@privatemessage_traveller, mchat)
                recyclerView.adapter = madapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@privatemessage_traveller, "Failed to retrieve chat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        recorder.release()
    }


    private fun uploadImage(imageUri: Uri) {
        val userId = intent.getStringExtra("currentuserid")!!
        val storageReference = FirebaseStorage.getInstance().reference.child("chats")
        val imageFileName = "${System.currentTimeMillis()}.${getFileExtension(imageUri)}"
        val imageRef = storageReference.child(imageFileName)

        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Once image is uploaded, send message with image URL
                sendMessageWithImage(fuser.uid, userId, downloadUri.toString())
            } else {
                // Handle failures
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun sendMessageWithImage(sender: String, receiver: String, image: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["image"] = image

        reference.child("Chats").push().setValue(hashMap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileuri = data.data // Get the URI of the selected image
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(fileuri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                chooseimg.setImageBitmap(bitmap)
                encodeBitmapImage(bitmap)

                // Send the image to be stored on the web
                val receiverId = userIntent.getStringExtra("currentuserid")
                if (receiverId != null) {
                    uploadImage(fileuri!!)
                } else {
                    Toast.makeText(this, "receiver id is null", Toast.LENGTH_SHORT).show()
                }


            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        else if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            // Get the captured image
            val imageBitmap = data.extras?.get("data") as Bitmap

            // Convert the captured image to Base64
            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Send the image to be stored on the web
            val receiverId = userIntent.getStringExtra("currentuserid")
            val userId = userIntent.getStringExtra("userid")
            if (receiverId != null) {
                if (userId != null) {
                    sendMessageWithImage(receiverId, userId, encodedImage)

                }
                else
                {
                    Toast.makeText(this, "user id is null", Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(this, "receiver id is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encodeBitmapImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
        encodedImage = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT)
    }
}























//package com.laraib.smd_project
//
//import android.annotation.SuppressLint
//import android.app.Activity.ScreenCaptureCallback
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.media.MediaPlayer
//import android.media.MediaRecorder
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.database.*
//import de.hdodenhof.circleimageview.CircleImageView
//import android.net.Uri
//import android.provider.MediaStore
//import android.util.Log
//import android.view.inputmethod.InputMethodManager
//import android.webkit.MimeTypeMap
//import com.google.firebase.storage.FirebaseStorage
//import java.io.File
//import java.io.IOException
//
//data class User_(
//    val name: String = "", // Default values for properties
//    var photoLink: String = ""
//) {
//    // No-argument constructor required by Firebase
//    constructor() : this("", "")
//}
//
//
//class privatemessage_traveller : AppCompatActivity() {
//
//    private var outputFile: File? = null
//    private lateinit var recorder: MediaRecorder
//    private var isRecording = false
//    private lateinit var mediaPlayer: MediaPlayer
//
//
//    private var mediaRecorder: MediaRecorder? = null
//    private var outputFileName: String = ""
//
//    private val screenCaptureCallback = ScreenCaptureCallback {
//        Toast.makeText(this, "screenshot detected", Toast.LENGTH_SHORT).show()
//    }
//
//    private lateinit var btnsend: ImageView
//
//    private lateinit var imageView: CircleImageView
//    private lateinit var username: TextView
//    private lateinit var btn_send: ImageButton
//    private lateinit var text_send: TextView
//    private lateinit var recyclerView: RecyclerView
//
//    private lateinit var fuser: FirebaseUser
//    private lateinit var database: DatabaseReference
//
//    private lateinit var userIntent: Intent
//    private lateinit var madapter: messageadapter
//    private var mchat = mutableListOf<ChatRV>()
//    private var imageurl: String = ""
//    private var audiourl: String = ""
//    private val GALLERY_REQUEST_CODE=1001
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_privatemessage_traveller)
//
//        val screenCaptureCallback = ScreenCaptureCallback {
//            Log.d("ScreenCaptureCallback", "Screenshot detected")
//            runOnUiThread {
//                Toast.makeText(this, "Screenshot detected", Toast.LENGTH_LONG).show()
//            }
//        }
//
////        val attachFileImageView = findViewById<ImageView>(com.google.firebase.database.R.id.imageView25)
////        attachFileImageView.setOnClickListener {
////            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
////
////        }
//
//
//        mediaPlayer = MediaPlayer()
//        recorder = MediaRecorder()
//
////        btnsend     = findViewById(com.google.firebase.database.R.id.btnsend)
////
////        btnsend.setOnClickListener {
////            if (isRecording) {
////                stopRecording()
////            } else {
////                startRecording()
////            }
////        }
//
//
//        // Register the screen capture callback
//       // registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
//
//        // Initialize media recorder and output file name
//        outputFileName = "${externalCacheDir?.absolutePath}/recording.3gp"
//        mediaRecorder = MediaRecorder()
//
//
//
////        val Button11 = findViewById<ImageView>(com.google.firebase.database.R.id.video)
////        Button11.setOnClickListener {
////            val intent = Intent(this, videocall::class.java)
////            startActivity(intent) }
////
////        val Button112 = findViewById<ImageView>(com.google.firebase.database.R.id.call)
////        Button112.setOnClickListener {
////            val intent = Intent(this, voicecall::class.java)
////            startActivity(intent) }
//
//        FirebaseApp.initializeApp(this)
//        database = FirebaseDatabase.getInstance().reference
//        fuser = FirebaseAuth.getInstance().currentUser!!
//
////        val toolbar: Toolbar = findViewById(com.google.firebase.database.R.id.toolbar)
////        setSupportActionBar(toolbar)
////        supportActionBar?.title = ""
////        supportActionBar?.setDisplayHomeAsUpEnabled(true)
////        toolbar.setNavigationOnClickListener {
////            finish()
////        }
//
//        recyclerView = findViewById(R.id.recyclerView)
//        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        username = findViewById(R.id.name)
//        imageView = findViewById(R.id.image)
//        btn_send = findViewById(R.id.btn_send)
//        text_send = findViewById(R.id.text_send)
//
//
//
//
//        userIntent = intent
//        val receiverId = userIntent.getStringExtra("currentuserid")
//        val userId = userIntent.getStringExtra("userid")
////        val userName = userIntent.getStringExtra("username")
//
//
//        //camera
////        val camera = findViewById<ImageView>(com.google.firebase.database.R.id.camera)
////        camera.setOnClickListener {
////            val intent = Intent(this, photo::class.java).apply {
////                putExtra("currentuserid", receiverId)
////            }
////            startActivity(intent)
////        }
//
//
//        // Retrieve receiver's information from Firebase
//        if (receiverId != null) {
//            val refProfile = FirebaseDatabase.getInstance().getReference("guides").child(receiverId)
//            refProfile.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val user = snapshot.getValue(User_::class.java)
//                    if (user != null) {
//                        // Display receiver's name
//                        username.text = user.name
//                        // Load receiver's image using Glide
//                        imageurl = user.photoLink
//                        if (!imageurl.isNullOrEmpty()) {
//                            Glide.with(this@privatemessage_traveller)
//                                .load(user.photoLink)
//                                .placeholder(R.drawable.ic_launcher_background)
//                                .error(R.drawable.ic_launcher_background)
//                                .into(imageView)
//                        }
//                    } else {
//                        Toast.makeText(this@privatemessage_traveller, "Receiver data is null", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@privatemessage_traveller, "Failed to retrieve receiver data", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            Toast.makeText(this@privatemessage_traveller, "Receiver ID is null", Toast.LENGTH_SHORT).show()
//        }
//
//        btn_send.setOnClickListener {
//            // Send message
//            val msg = text_send.text.toString()
//            if (msg.isNotBlank()) {
//                if (receiverId != null) {
//                    sendmessage(fuser.uid, receiverId, msg)
//                } else {
//                    Toast.makeText(this@privatemessage_traveller, "Receiver ID is null", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this@privatemessage_traveller, "You can't send an empty message", Toast.LENGTH_SHORT).show()
//            }
//            text_send.text = ""
//        }
//
//        readmessage(fuser.uid, receiverId ?: "",imageurl,)
//    }
//
////    override fun onStart() {
////        super.onStart()
////        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
////    }
//
////    override fun onStop() {
////        super.onStop()
////        unregisterScreenCaptureCallback(screenCaptureCallback)
//////        screenshotDetectionDelegate.stopScreenshotDetection()
////    }
//
//    private fun sendmessage(sender: String, receiver: String, message: String) {
//        val reference = FirebaseDatabase.getInstance().getReference("Chats")
//        val hashMap: HashMap<String, String> = HashMap()
//        hashMap["sender"] = sender
//        hashMap["receiver"] = receiver
//        hashMap["message"] = message
//        reference.push().setValue(hashMap)
//        reference.keepSynced(true)
//        val kh =
//            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
//
//    }
//
//    private fun readmessage(myid: String, userid: String, imageurl: String) {
//        val reference = FirebaseDatabase.getInstance().getReference("Chats")
//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                mchat.clear()
//                for (snapshot in snapshot.children) {
//                    val chat = snapshot.getValue(ChatRV::class.java)
//                    if (chat != null) {
//                        if (chat.receiver == myid && chat.sender == userid || chat.receiver == userid && chat.sender == myid) {
//                            mchat.add(chat)
//                        }
//                    }
//                }
//                // Fetch voice note URLs and set them in ChatRV objects
////                for (chat in mchat) {
////                    val voiceNoteUrl = chat.voiceNoteUrl // Fetch voice note URL from Firebase based on your data model
////                    // Set voice note URL in the ChatRV object
////                    chat.voiceNoteUrl = voiceNoteUrl
////                }
//                // Pass mchat to the adapter after setting voiceNoteUrl for each ChatRV object
//                madapter = messageadapter(this@privatemessage_traveller, mchat, imageurl)
//                recyclerView.adapter = madapter
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@privatemessage_traveller, "Failed to retrieve chat data", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//        reference.keepSynced(true)
//    }
//
//    private fun startRecording() {
//        try {
//            // Create a temporary file to store the recorded voice note
//            outputFile = File.createTempFile("voice_note", ".3gp", cacheDir)
//            // Set up the MediaRecorder
//            recorder.apply {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                outputFile?.let { setOutputFile(it.absolutePath) }
//                prepare()
//                start()
//            }
//            isRecording = true
//            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
//        } catch (e: IOException) {
//            Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
//            // Handle IOException
//        } catch (e: IllegalStateException) {
//            Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
//            // Handle IllegalStateException
//        } catch (e: Exception) {
//            Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
//            // Handle other exceptions
//        }
//    }
//
//
//    private fun stopRecording() {
//        try {
//            recorder.stop()
//            recorder.release()
//            isRecording = false
//            outputFile?.let { outputFile ->
//                // Upload the recorded audio file to Firebase Storage
//                uploadAudio(outputFile)
//            }
//        } catch (e: IllegalStateException) {
//            Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
//            // Handle IllegalStateException properly
//        } catch (e: Exception) {
//            Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
//            // Handle other exceptions
//        }
//    }
//
//    private fun uploadAudio(audioFile: File) {
//        val userId = intent.getStringExtra("currentuserid")!!
//        val storageReference = FirebaseStorage.getInstance().reference.child("audio")
//        val audioFileName = "${System.currentTimeMillis()}.3gp"
//        val audioRef = storageReference.child(audioFileName)
//
//        val uploadTask = audioRef.putFile(Uri.fromFile(audioFile))
//        uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            audioRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val downloadUri = task.result
//                // Once audio is uploaded, send message with audio URL
//                sendMessageWithAudio(fuser.uid, userId, downloadUri.toString())
//            } else {
//                // Handle failures
//            }
//        }
//    }
//
//
//    private fun sendMessageWithAudio(sender: String, receiver: String, audioUrl: String) {
//        val reference = FirebaseDatabase.getInstance().reference
//        val hashMap = HashMap<String, Any>()
//        hashMap["sender"] = sender
//        hashMap["receiver"] = receiver
//        hashMap["audioUrl"] = audioUrl
//        reference.child("Chats").push().setValue(hashMap)
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.release()
//        recorder.release()
//    }
//
//
//    private fun uploadImage(imageUri: Uri) {
//        val userId = intent.getStringExtra("currentuserid")!!
//        val storageReference = FirebaseStorage.getInstance().reference.child("images")
//        val imageFileName = "${System.currentTimeMillis()}.${getFileExtension(imageUri)}"
//        val imageRef = storageReference.child(imageFileName)
//
//        val uploadTask = imageRef.putFile(imageUri)
//        uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            imageRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val downloadUri = task.result
//                // Once image is uploaded, send message with image URL
//                sendMessageWithImage(fuser.uid, userId, downloadUri.toString())
//            } else {
//                // Handle failures
//            }
//        }
//    }
//
//    private fun getFileExtension(uri: Uri): String? {
//        val contentResolver = contentResolver
//        val mimeTypeMap = MimeTypeMap.getSingleton()
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
//    }
//
//    private fun sendMessageWithImage(sender: String, receiver: String, image: String) {
//        val reference = FirebaseDatabase.getInstance().reference
//
//        val hashMap = HashMap<String, Any>()
//        hashMap["sender"] = sender
//        hashMap["receiver"] = receiver
//        hashMap["image"] = image
//
//        reference.child("Chats").push().setValue(hashMap)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
//            val selectedImageUri = data.data
//            if (selectedImageUri != null) {
//                // Upload the selected image to Firebase Storage
//                uploadImage(selectedImageUri)
//            }
//        }
//    }
//}
