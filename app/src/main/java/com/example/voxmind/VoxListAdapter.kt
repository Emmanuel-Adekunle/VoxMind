/**
 * VoxListAdapter is a custom RecyclerView adapter for displaying a list of `VoxModel` objects.
 * Each item represents a quiz with its title, subtitle, and duration.
 * On clicking an item, it navigates to the `VoxActivity` and initializes the quiz.
 */
package com.example.voxmind

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voxmind.databinding.VoxItemRecyclerRowBinding

/**
 * VoxListAdapter is responsible for binding `VoxModel` data to the UI and handling item click events.
 *
 * @param voxModelList The list of `VoxModel` objects to be displayed in the RecyclerView.
 */
class VoxListAdapter(private val voxModelList: List<VoxModel>) :
    RecyclerView.Adapter<VoxListAdapter.MyViewHolder>() {

    /**
     * MyViewHolder is responsible for binding data to each row in the RecyclerView.
     *
     * @param binding The ViewBinding object for the row layout.
     */
    class MyViewHolder(private val binding: VoxItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a `VoxModel` object to the row layout.
         *
         * @param model The `VoxModel` object to bind.
         */
        @SuppressLint("SetTextI18n")
        fun bind(model: VoxModel) {
            binding.apply {
                // Set quiz title and subtitle
                quizTitleText.text = model.title
                quizSubtitleText.text = model.subtitle

                // Display quiz time in minutes
                voxTimeText.text = model.time + " min"

                // Set up click listener for navigating to VoxActivity
                root.setOnClickListener {
                    val intent = Intent(root.context, VoxActivity::class.java)

                    // Pass quiz data to the activity
                    VoxActivity.time = model.time
                    VoxActivity.questionModelList = model.questionList

                    // Start the activity
                    root.context.startActivity(intent)
                }
            }
        }
    }

    /**
     * Inflates the layout for each row in the RecyclerView.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The type of the new view.
     * @return A new `MyViewHolder` instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = VoxItemRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The size of the `voxModelList`.
     */
    override fun getItemCount(): Int {
        return voxModelList.size
    }

    /**
     * Binds the data for a specific position in the RecyclerView.
     *
     * @param holder The `MyViewHolder` instance for the current row.
     * @param position The position of the item in the data set.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(voxModelList[position])
    }
}
