/**
 * MainActivity is the entry point of the application. It displays a list of items
 * retrieved from a Firebase Realtime Database using a RecyclerView.
 */
package com.example.voxmind

import android.view.View
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voxmind.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

/**
 * MainActivity is responsible for:
 * - Initializing the layout using View Binding.
 * - Fetching data from Firebase Realtime Database.
 * - Displaying the data in a RecyclerView.
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding for accessing views in the layout
    private lateinit var binding: ActivityMainBinding

    // List to hold VoxModel objects retrieved from the Firebase database
    private lateinit var voxModelList: MutableList<VoxModel>

    // Adapter for populating the RecyclerView
    private lateinit var adapter: VoxListAdapter

    /**
     * Called when the activity is created. Initializes view binding,
     * data structures, and starts the process of fetching data from Firebase.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if available.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the list to hold quiz models
        voxModelList = mutableListOf()

        // Start fetching data from Firebase
        getDataFromFirebase()
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager and an adapter.
     * Hides the progress bar once data loading is complete.
     */
    private fun setupRecyclerview() {
        // Hide the progress bar
        binding.progressBar.visibility = View.GONE

        // Initialize and set the adapter for the RecyclerView
        adapter = VoxListAdapter(voxModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    /**
     * Fetches data from Firebase Realtime Database. On successful retrieval,
     * populates the `voxModelList` with data and initializes the RecyclerView.
     * Displays a progress bar while data is being fetched.
     */
    private fun getDataFromFirebase() {
        // Show the progress bar while data is being loaded
        binding.progressBar.visibility = View.VISIBLE

        // Reference to the Firebase Realtime Database
        FirebaseDatabase.getInstance().reference
            .get()
            .addOnSuccessListener { dataSnapshot ->
                // Check if the data snapshot contains any data
                if (dataSnapshot.exists()) {
                    // Iterate through each child in the snapshot
                    for (snapshot in dataSnapshot.children) {
                        // Convert each child to a VoxModel object
                        val quizModel = snapshot.getValue(VoxModel::class.java)
                        if (quizModel != null) {
                            // Add the model to the list
                            voxModelList.add(quizModel)
                        }
                    }
                }
                // Set up the RecyclerView with the loaded data
                setupRecyclerview()
            }
    }
}
