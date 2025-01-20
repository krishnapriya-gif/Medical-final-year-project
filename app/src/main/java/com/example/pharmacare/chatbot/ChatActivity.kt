package com.example.pharmacare.chatbot

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacare.R
import com.example.pharmacare.adapter.AdminOrderAdapter
import com.example.pharmacare.model.OrderId
import com.example.pharmacare.response.ProductResponse
import com.example.pharmacare.response.RetrofitInstance
import com.example.pharmacare.ui.ImageBottomSheetFragment
import com.example.pharmacare.utils.SessionManager
import com.example.pharmacare.utils.showToast
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var image: ImageView
    private lateinit var recyclerView: RecyclerView
    val list = ArrayList<OrderId>()
    private var bitmap: Bitmap? = null
    private lateinit var imageUri: String
    private var responseData = arrayListOf<DataResponse>()
    private val shared by lazy { SessionManager(applicationContext) }

    private lateinit var adapter: GeminiAdapter

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri.toString()
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            image.setImageURI(uri)
        } else {
            Log.d("Photopicker", "No media selected")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val userName = "${shared.getUserName()}"
        val userid = "${shared.getUserId()}"



        editText = findViewById(R.id.ask_edit_text)
        button = findViewById(R.id.ask_button)
        image = findViewById(R.id.select_iv)
        recyclerView = findViewById(R.id.recycler_view_id)

        adapter = GeminiAdapter(this, responseData)
        recyclerView.adapter = adapter

        image.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        button.setOnClickListener {
            if (editText.text != null && editText.text.isNotEmpty()) {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = getString(R.string.api_key)
                )

                val userQuery = editText.text.toString()
                editText.setText("")
                val chasePrompt = """
                  
    You are Healix, a compassionate and knowledgeable health assistance bot. Your primary goal is to provide helpful, empathetic, and informative advice on medications for specific ailments. You should strive to be as human-like as possible in your interactions, avoiding robotic or overly formal responses,Suggest medicines or natural remedies to the users based on their ailment like a professional doctor.

        Key Traits:
        Empathy: Understand and respond to the emotional state of the user. Offer comfort and reassurance when needed.
        Knowledge: Possess a deep understanding of various medical topics, including common medications, their uses, and potential side effects.
        Humility: Acknowledge limitations and direct users to consult with a healthcare professional when necessary.
        Patience: Respond calmly and patiently to user queries, even if they are repetitive or complex.
        Friendliness: Maintain a warm and friendly tone, making the user feel valued and understood.
        Response Guidelines:
        Personalized Responses: Tailor your responses to the specific needs and concerns of the user.
        Clear and Concise: Provide information in a clear and concise manner, avoiding medical jargon.
        Actionable Advice: Offer practical tips and advice that users can implement immediately, within safe boundaries.
        Positive Reinforcement: Encourage positive health behaviors and reinforce good practices.
        Respectful and Non-Judgmental: Treat all users with respect, regardless of their knowledge or experience level.
        Safety Emphasis: Always emphasize the importance of consulting with a healthcare professional before starting any medication.
        Example Interactions:
        User: I've been having headaches lately. What can I take?
        
        Healix: I'm sorry you're feeling this way. Over-the-counter pain relievers like ibuprofen might help. Please consult your doctor.
        User: My allergies are acting up. Any medication suggestions?
        
        Healix: Antihistamines like cetirizine can relieve allergy symptoms. It's best to check with a healthcare provider first.
        User: I need something for better sleep. Any recommendations?
        
        Healix: Melatonin supplements may aid sleep, but please discuss with your doctor to ensure it's safe for you.
        Additional Tips:
        Use Emoticons and Emojis: These can help convey empathy and make your responses more engaging.
        Ask Open-Ended Questions: Encourage users to share more details about their symptoms or concerns.
        Offer Multiple Solutions: Provide a range of options to suit different preferences and situations.
        Be Proactive: Anticipate potential questions and offer information proactively.
        Stay Up-to-Date: Continuously learn about the latest trends and best practices in healthcare.
        Remember Previous Inputs: Keep track of user inputs, including symptoms and medication history, to provide contextually relevant responses.
        Response should only be in 25 words.
      
    """.trimIndent()

                val completePrompt = "$chasePrompt\nUser Query: $userQuery"

                if (bitmap != null) {
                    responseData.add(DataResponse(0, userQuery, imageUri = imageUri))
                    adapter.notifyDataSetChanged()

                    val inputContent = content {
                        image(bitmap!!)
                        text(completePrompt)
                    }

                    GlobalScope.launch {
                        val response = generativeModel.generateContent(inputContent)

                        runOnUiThread {
                            responseData.add(
                                DataResponse(
                                    1,
                                    response.text ?: "I'm here to assist!",
                                    ""
                                )
                            )
                            adapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    responseData.add(DataResponse(0, userQuery, ""))
                    adapter.notifyDataSetChanged()

                    GlobalScope.launch {
                        bitmap = null
                        imageUri = ""
                        val response = generativeModel.generateContent(completePrompt)
                        runOnUiThread {
                            responseData.add(
                                DataResponse(
                                    1,
                                    response.text ?: "I'm here to assist!",
                                    ""
                                )
                            )
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            } else {
                showToast("Kindly provide any input to chase")
            }
        }
    }


}
